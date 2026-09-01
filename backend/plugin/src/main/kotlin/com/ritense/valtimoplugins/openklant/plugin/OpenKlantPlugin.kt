package com.ritense.valtimoplugins.openklant.plugin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.ritense.plugin.annotation.Plugin
import com.ritense.plugin.annotation.PluginAction
import com.ritense.plugin.annotation.PluginActionProperty
import com.ritense.plugin.annotation.PluginProperty
import com.ritense.processlink.domain.ActivityTypeWithEventName
import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.dto.Actor
import com.ritense.valtimoplugins.openklant.dto.Adres
import com.ritense.valtimoplugins.openklant.dto.Betrokkene
import com.ritense.valtimoplugins.openklant.dto.BijlageIdentificator
import com.ritense.valtimoplugins.openklant.dto.Contactnaam
import com.ritense.valtimoplugins.openklant.dto.CreateActorKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.CreateActorRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBetrokkeneRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBijlageRequest
import com.ritense.valtimoplugins.openklant.dto.CreateInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.CreateOnderwerpobjectRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijIdentificatorRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.CreateRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.dto.CreateVertegenwoordigingRequest
import com.ritense.valtimoplugins.openklant.dto.Identificator
import com.ritense.valtimoplugins.openklant.dto.InterneTaakStatus
import com.ritense.valtimoplugins.openklant.dto.KlantcontactCreationRequest
import com.ritense.valtimoplugins.openklant.dto.Onderwerpobjectidentificator
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.dto.PatchActorKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.PatchActorRequest
import com.ritense.valtimoplugins.openklant.dto.PatchBetrokkeneRequest
import com.ritense.valtimoplugins.openklant.dto.PatchBijlageRequest
import com.ritense.valtimoplugins.openklant.dto.PatchInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.PatchKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.PatchOnderwerpobjectRequest
import com.ritense.valtimoplugins.openklant.dto.PatchPartijIdentificatorRequest
import com.ritense.valtimoplugins.openklant.dto.PatchPartijRequest
import com.ritense.valtimoplugins.openklant.dto.PatchRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.dto.PatchVertegenwoordigingRequest
import com.ritense.valtimoplugins.openklant.dto.Referable
import com.ritense.valtimoplugins.openklant.model.AdresInformation
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresPatch
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.KeyValueQueryParam
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.OpenKlantQuery
import com.ritense.valtimoplugins.openklant.model.PartijInformationImpl
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import com.ritense.valtimoplugins.openklant.service.OpenKlantService
import com.ritense.valtimoplugins.openklant.util.ReflectionUtil
import com.ritense.valtimoplugins.openklant.util.StringToBooleanDeserializer
import com.ritense.valtimoplugins.openklant.util.toNestedUuidIfPresent
import com.ritense.valtimoplugins.openklant.util.toNestedUuidList
import com.ritense.valtimoplugins.openklant.util.toRequiredNestedUuid
import com.ritense.valtimoplugins.openklant.util.toRequiredString
import com.ritense.valtimoplugins.openklant.util.toRequiredUuid
import com.ritense.valtimoplugins.openklant.util.toUuidIfPresent
import com.ritense.valtimoplugins.openklant.util.trimToNull
import io.github.oshai.kotlinlogging.KotlinLogging
import org.operaton.bpm.engine.delegate.DelegateExecution
import java.net.URI
import java.util.UUID

@Plugin(
    key = "openklant",
    title = "Open Klant 2 Plugin",
    description = "Open Klant 2 plugin",
)
@Suppress("UNUSED", "TooManyFunctions", "LongParameterList")
class OpenKlantPlugin(
    private val openKlantPluginService: OpenKlantService,
    private val openKlantClient: OpenKlantClient,
    private val reflectionUtil: ReflectionUtil,
    private val objectMapper: ObjectMapper,
) {
    @PluginProperty(key = "klantinteractiesUrl", secret = false, required = true)
    lateinit var klantinteractiesUrl: URI

    @PluginProperty(key = "token", secret = true, required = true)
    lateinit var token: String

    @PluginAction(
        key = "store-contact-info",
        title = "Store Contactinfo",
        description = "Store contact info in Open Klant",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun storeContactInformation(
        execution: DelegateExecution,
        @PluginActionProperty bsn: String,
        @PluginActionProperty firstName: String,
        @PluginActionProperty inFix: String,
        @PluginActionProperty lastName: String,
        @PluginActionProperty emailAddress: String,
        @PluginActionProperty caseUuid: String,
        @PluginActionProperty resultPvName: String? = null,
    ) {
        logger.debug { "Storing contact information in Open Klant..." }

        val contactInformation =
            ContactInformation.fromActionProperties(
                bsn = bsn,
                voornaam = firstName,
                voorvoegselAchternaam = inFix,
                achternaam = lastName,
                emailadres = emailAddress,
                zaaknummer = caseUuid,
            )
        val partijUuid =
            openKlantPluginService.storeContactInformation(
                contactInformation = contactInformation,
                properties = openKlantProperties,
            )

        execution.setVariable(resultPvName.trimToNull() ?: OUTPUT_PARTIJ_UUID, partijUuid)

        logger.info {
            "Successfully stored contact information in Open Klant " +
                "(partij uuid: '$partijUuid', business key: '${execution.processBusinessKey}')"
        }
    }

    @PluginAction(
        key = "get-or-create-partij",
        title = "Get or create Partij",
        description = "Create partij in Open Klant or gets the partij if already exists",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getOrCreatePartij(
        execution: DelegateExecution,
        @PluginActionProperty bsn: String,
        @PluginActionProperty voorletters: String,
        @PluginActionProperty voornaam: String,
        @PluginActionProperty voorvoegselAchternaam: String,
        @PluginActionProperty achternaam: String,
        @PluginActionProperty resultPvName: String? = null,
    ) {
        logger.debug { "Getting or otherwise creating partij in Open Klant..." }

        val partijInformation =
            PartijInformationImpl.fromActionProperties(
                bsn = bsn,
                voorletters = voorletters,
                voornaam = voornaam,
                voorvoegselAchternaam = voorvoegselAchternaam,
                achternaam = achternaam,
            )
        val partij =
            openKlantPluginService.getOrCreatePartij(
                partijInformation = partijInformation,
                properties = openKlantProperties,
            )

        execution.setVariable(resultPvName.trimToNull() ?: OUTPUT_PARTIJ_UUID, partij.uuid.toString())

        logger.info {
            "Successfully got or created partij in Open Klant (uuid: '${partij.uuid}', url: '${partij.url}')"
        }
    }

    fun getDigitaleAdressen(query: DigitaalAdresQuery): List<DigitaalAdres> =
        openKlantPluginService.getAllDigitaleAdressen(query = query, properties = openKlantProperties)

    @PluginAction(
        key = "get-digitale-adressen",
        title = "Get digitale adressen",
        description = "Fetches digitale adressen from Open Klant based on provided filters",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getDigitaleAdressen(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty
        queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("digitale adressen", resultPvName) {
        getDigitaleAdressen(query = DigitaalAdresQuery.fromKeyValueQueryParamList(queryParams))
    }

    fun createDigitaalAdres(request: DigitaalAdres): DigitaalAdres =
        openKlantPluginService.createDigitaalAdres(request = request, properties = openKlantProperties)

    @PluginAction(
        key = "create-digitaal-adres",
        title = "Create Digitaal Adres",
        description = "Create a digitaal adres in Open Klant",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createDigitaalAdres(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty verstrektDoorBetrokkene: String? = null,
        @PluginActionProperty verstrektDoorPartij: String,
        @PluginActionProperty adres: String,
        @PluginActionProperty soortDigitaalAdres: String,
        @PluginActionProperty isStandaardAdres: Boolean? = null,
        @PluginActionProperty omschrijving: String = "",
        @PluginActionProperty referentie: String? = null,
        @PluginActionProperty verificatieDatum: String? = null,
    ) {
        val request =
            DigitaalAdres(
                verstrektDoorBetrokkeneUuid = verstrektDoorBetrokkene.toUuidIfPresent(),
                verstrektDoorPartijUuid = verstrektDoorPartij.toUuidIfPresent(),
                adres = adres.trim(),
                soortDigitaalAdres = SoortDigitaalAdres.valueOf(soortDigitaalAdres.trim().uppercase()),
                isStandaardAdres = isStandaardAdres,
                omschrijving = omschrijving.trim(),
                referentie = referentie.trimToNull(),
                verificatieDatum = verificatieDatum.trimToNull(),
            )

        logger.debug { "Creating digitaal adres in Open Klant..." }

        val digitaalAdres = createDigitaalAdres(request)
        execution.storeDigitaalAdresJson(resultPvName, digitaalAdres)

        logger.info {
            "Successfully created digitaal adres in Open Klant " +
                "(uuid: '${digitaalAdres.uuid}', url: '${digitaalAdres.url}')"
        }
    }

    fun setDefaultDigitaalAdres(digitaalAdres: DigitaalAdres): DigitaalAdres =
        openKlantPluginService.setDefaultDigitaalAdres(request = digitaalAdres, properties = openKlantProperties)

    @PluginAction(
        key = "set-default-digitaal-adres",
        title = "Set default Digitaal Adres",
        description = "Sets a default digitaal adres in Open Klant",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun setDefaultDigitaalAdres(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty partijUuid: String,
        @PluginActionProperty adres: String,
        @PluginActionProperty soortDigitaalAdres: String,
        @PluginActionProperty verificatieDatum: String,
    ) {
        val adresInformation =
            AdresInformation.fromActionProperties(
                partijUuid = partijUuid,
                adres = adres,
                soortDigitaalAdres = soortDigitaalAdres,
                referentie = DEFAULT_DIGITALE_ADRES_REFERENCE,
                verificatieDatum = verificatieDatum,
            )
        val request = adresInformation.toDigitaalAdresCreationRequest(isStandaardAdres = true)

        logger.debug { "Setting default digitaal adres in Open Klant..." }

        val digitaalAdres = setDefaultDigitaalAdres(request)
        execution.storeDigitaalAdresUuid(resultPvName, digitaalAdres)

        logger.info {
            "Successfully set the default digitaal adres in Open Klant " +
                "(uuid: '${digitaalAdres.uuid}', url: '${digitaalAdres.url}')"
        }
    }

    fun updateDigitaalAdres(
        digitaalAdresUuid: NestedUuid,
        digitaalAdresPatchRequest: DigitaalAdresPatch,
    ): DigitaalAdres =
        openKlantPluginService.updateDigitaalAdres(
            digitaalAdresUuid = digitaalAdresUuid,
            request = digitaalAdresPatchRequest,
            properties = openKlantProperties,
        )

    @PluginAction(
        key = "update-digitaal-adres",
        title = "Update Digitaal Adres",
        description = "Update any value of the digitaal adres in Open Klant",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateDigitaalAdres(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty digitaalAdresUuid: String,
        @PluginActionProperty verstrektDoorBetrokkene: String? = null,
        @PluginActionProperty verstrektDoorPartij: String? = null,
        @PluginActionProperty adres: String? = null,
        @PluginActionProperty soortDigitaalAdres: String? = null,
        // When null is passed to a Formio form, Formio stubbornly
        // returns an empty string, thus we use this deserializer
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty isStandaardAdres: Boolean? = null,
        @PluginActionProperty omschrijving: String? = null,
        @PluginActionProperty referentie: String? = null,
        @PluginActionProperty verificatieDatum: String? = null,
    ) {
        val patchRequest =
            DigitaalAdresPatch(
                verstrektDoorBetrokkeneUuid = verstrektDoorBetrokkene.toUuidIfPresent(),
                verstrektDoorPartijUuid = verstrektDoorPartij.toUuidIfPresent(),
                adres = adres.trimToNull(),
                soortDigitaalAdres =
                    soortDigitaalAdres
                        .trimToNull()
                        ?.let { SoortDigitaalAdres.valueOf(it.uppercase()) },
                isStandaardAdres = isStandaardAdres,
                omschrijving = omschrijving.trimToNull(),
                referentie = referentie.trimToNull(),
                verificatieDatum = verificatieDatum.trimToNull(),
            )

        logger.debug { "Updating digitaal adres in Open Klant..." }

        val digitaalAdres =
            updateDigitaalAdres(
                digitaalAdresUuid = NestedUuid.fromString(digitaalAdresUuid),
                digitaalAdresPatchRequest = patchRequest,
            )
        execution.storeDigitaalAdresJson(resultPvName, digitaalAdres)

        logger.info {
            "Successfully updated digitaal adres in Open Klant " +
                "(uuid: '${digitaalAdres.uuid}', url: '${digitaalAdres.url}')"
        }
    }

    @PluginAction(
        key = "get-contact-moments-by-case-uuid",
        title = "Get contact history by case UUID",
        description = "Get contact history by case UUID from Open Klant",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getContactMoments(
        @PluginActionProperty caseUuid: String,
        @PluginActionProperty resultPvName: String,
        execution: DelegateExecution,
    ) = execution.storeReflectedProjection("contact history by case uuid '$caseUuid'", resultPvName) {
        openKlantPluginService.getAllKlantcontacten(
            query = KlantcontactQuery(objectUuid = caseUuid),
            properties = openKlantProperties,
        )
    }

    @PluginAction(
        key = "get-contact-moments-by-bsn",
        title = "Get contact history by BSN",
        description =
            "Get contact history by BSN from Open Klant. " +
                "Queries the API using the 'partij-identificator object-ID' parameter.",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getContactMomentsByBsn(
        @PluginActionProperty bsn: String,
        @PluginActionProperty resultPvName: String,
        execution: DelegateExecution,
    ) = execution.storeReflectedProjection("contact history by bsn", resultPvName) {
        openKlantPluginService.getAllKlantcontacten(
            query = KlantcontactQuery(bsn = bsn),
            properties = openKlantProperties,
        )
    }

    @PluginAction(
        key = "get-contact-moments-by-partij-uuid",
        title = "Get contact history by Partij UUID",
        description = "Get contact history by Partij UUID from Open Klant.",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getContactMomentsByPartijUuid(
        @PluginActionProperty partijUuid: String,
        @PluginActionProperty resultPvName: String,
        execution: DelegateExecution,
    ) = execution.storeReflectedProjection("contact history by partij uuid '$partijUuid'", resultPvName) {
        openKlantPluginService.getAllKlantcontacten(
            query = KlantcontactQuery(partijUuid = partijUuid),
            properties = openKlantProperties,
        )
    }

    @PluginAction(
        key = "register-klantcontact",
        title = "Register klantcontact",
        description = "Registers a new klantcontact to OpenKlant",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun postKlantContact(
        @PluginActionProperty referentienummer: String?,
        @PluginActionProperty kanaal: String,
        @PluginActionProperty onderwerp: String,
        @PluginActionProperty inhoud: String?,
        @PluginActionProperty reactie: String?,
        @PluginActionProperty indicatieContactGelukt: String?,
        @PluginActionProperty vertrouwelijk: String,
        @PluginActionProperty taal: String,
        @PluginActionProperty plaatsgevondenOp: String,
        @PluginActionProperty hasBetrokkene: Boolean?,
        @PluginActionProperty partijUuid: String?,
        @PluginActionProperty voorletters: String?,
        @PluginActionProperty voornaam: String?,
        @PluginActionProperty voorvoegselAchternaam: String?,
        @PluginActionProperty achternaam: String?,
        @PluginActionProperty metadata: Map<String, String>?,
        execution: DelegateExecution,
    ) {
        logger.debug { "Registering klantcontact in Open Klant..." }

        val klantcontactCreationInformation =
            KlantcontactCreationInformation.fromActionProperties(
                referentienummer = referentienummer,
                kanaal = kanaal,
                onderwerp = onderwerp,
                inhoud = inhoud,
                reactie = reactie,
                indicatieContactGelukt = indicatieContactGelukt,
                vertrouwelijk = vertrouwelijk,
                taal = taal,
                plaatsgevondenOp = plaatsgevondenOp,
                hasBetrokkene = hasBetrokkene,
                partijUuid = partijUuid,
                voorletters = voorletters,
                voornaam = voornaam,
                voorvoegselAchternaam = voorvoegselAchternaam,
                achternaam = achternaam,
                metadata = metadata,
            )
        openKlantPluginService.postKlantcontact(
            klantcontactCreationInformation = klantcontactCreationInformation,
            properties = openKlantProperties,
        )

        logger.info {
            "Successfully registered klantcontact in Open Klant (business key: '${execution.processBusinessKey}')"
        }
    }

    // Actoren

    @PluginAction(
        key = "get-actoren",
        title = "Get actoren (beta)",
        description = "Retrieves actoren from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getActoren(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("actoren", resultPvName) {
        openKlantClient.getActoren(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-actor",
        title = "Get actor (beta)",
        description = "Retrieves a single actor from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getActor(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("actor", resultPvName) {
        openKlantClient.getActor(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-actor",
        title = "Create actor (beta)",
        description = "Creates a new actor in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createActor(
        execution: DelegateExecution,
        @PluginActionProperty naam: String? = null,
        @PluginActionProperty soortActor: String? = null,
        @PluginActionProperty resultPvName: String,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieActief: Boolean? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
        @PluginActionProperty functie: String? = null,
        @PluginActionProperty emailadres: String? = null,
        @PluginActionProperty telefoonnummer: String? = null,
        @PluginActionProperty omschrijving: String? = null,
        @PluginActionProperty faxnummer: String? = null,
    ) = execution.storeReference(Operation.CREATE, "actor", resultPvName) {
        openKlantClient.createActor(
            CreateActorRequest(
                naam = naam.toRequiredString("naam"),
                soortActor = Actor.SoortActor.fromValue(soortActor.toRequiredString("soortActor")),
                indicatieActief = indicatieActief,
                actoridentificator = identificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
                actorIdentificatie =
                    actorIdentificatieOrNull(
                        functie,
                        emailadres,
                        telefoonnummer,
                        omschrijving,
                        faxnummer,
                    ),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-actor",
        title = "Update actor (beta)",
        description = "Updates the provided fields of an existing actor in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateActor(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty naam: String? = null,
        @PluginActionProperty soortActor: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieActief: Boolean? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
        @PluginActionProperty functie: String? = null,
        @PluginActionProperty emailadres: String? = null,
        @PluginActionProperty telefoonnummer: String? = null,
        @PluginActionProperty omschrijving: String? = null,
        @PluginActionProperty faxnummer: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "actor", resultPvName) {
        openKlantClient.patchActor(
            uuid.toRequiredUuid("uuid"),
            PatchActorRequest(
                naam = naam.trimToNull(),
                soortActor = soortActor.trimToNull()?.let { Actor.SoortActor.fromValue(it) },
                indicatieActief = indicatieActief,
                actoridentificator = identificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
                actorIdentificatie =
                    actorIdentificatieOrNull(
                        functie,
                        emailadres,
                        telefoonnummer,
                        omschrijving,
                        faxnummer,
                    ),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-actor",
        title = "Delete actor (beta)",
        description = "Deletes an actor from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteActor(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("actor", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteActor(it, openKlantProperties)
    }

    // Actorklantcontacten

    @PluginAction(
        key = "get-actorklantcontacten",
        title = "Get actorklantcontacten (beta)",
        description =
            "Retrieves the links between actoren and klantcontacten based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getActorKlantcontacten(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("actorklantcontacten", resultPvName) {
        openKlantClient.getActorKlantcontacten(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-actorklantcontact",
        title = "Get actorklantcontact (beta)",
        description = "Retrieves a single link between an actor and a klantcontact by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getActorKlantcontact(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("actorklantcontact", resultPvName) {
        openKlantClient.getActorKlantcontact(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-actorklantcontact",
        title = "Create actorklantcontact (beta)",
        description = "Links an actor to a klantcontact in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createActorKlantcontact(
        execution: DelegateExecution,
        @PluginActionProperty actorUuid: String? = null,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeReference(Operation.CREATE, "actorklantcontact", resultPvName) {
        openKlantClient.createActorKlantcontact(
            CreateActorKlantcontactRequest(
                actor = actorUuid.toRequiredNestedUuid("actorUuid"),
                klantcontact = klantcontactUuid.toRequiredNestedUuid("klantcontactUuid"),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-actorklantcontact",
        title = "Update actorklantcontact (beta)",
        description = "Repoints an existing actorklantcontact to another actor and/or klantcontact" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateActorKlantcontact(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty actorUuid: String? = null,
        @PluginActionProperty klantcontactUuid: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "actorklantcontact", resultPvName) {
        openKlantClient.patchActorKlantcontact(
            uuid.toRequiredUuid("uuid"),
            PatchActorKlantcontactRequest(
                actor = actorUuid.toNestedUuidIfPresent(),
                klantcontact = klantcontactUuid.toNestedUuidIfPresent(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-actorklantcontact",
        title = "Delete actorklantcontact (beta)",
        description = "Removes the link between an actor and a klantcontact" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteActorKlantcontact(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("actorklantcontact", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteActorKlantcontact(it, openKlantProperties)
    }

    // Betrokkenen

    @PluginAction(
        key = "get-betrokkenen",
        title = "Get betrokkenen (beta)",
        description = "Retrieves betrokkenen from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getBetrokkenen(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("betrokkenen", resultPvName) {
        openKlantClient.getBetrokkenen(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-betrokkene",
        title = "Get betrokkene (beta)",
        description = "Retrieves a single betrokkene from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getBetrokkene(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("betrokkene", resultPvName) {
        openKlantClient.getBetrokkene(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-betrokkene",
        title = "Create betrokkene (beta)",
        description = "Registers a betrokkene for an existing klantcontact in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createBetrokkene(
        execution: DelegateExecution,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty rol: String? = null,
        @PluginActionProperty resultPvName: String,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty initiator: Boolean? = null,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty voorletters: String? = null,
        @PluginActionProperty voornaam: String? = null,
        @PluginActionProperty voorvoegselAchternaam: String? = null,
        @PluginActionProperty achternaam: String? = null,
        @PluginActionProperty organisatienaam: String? = null,
        @PluginActionProperty bezoekadres: Map<String, Any>? = null,
        @PluginActionProperty correspondentieadres: Map<String, Any>? = null,
    ) = execution.storeReference(Operation.CREATE, "betrokkene", resultPvName) {
        openKlantClient.createBetrokkene(
            CreateBetrokkeneRequest(
                hadKlantcontact = klantcontactUuid.toRequiredNestedUuid("klantcontactUuid"),
                rol = Betrokkene.Rol.fromValue(rol.toRequiredString("rol")),
                initiator = initiator ?: false,
                wasPartij = partijUuid.toNestedUuidIfPresent(),
                bezoekadres = bezoekadres.toAdres(),
                correspondentieadres = correspondentieadres.toAdres(),
                contactnaam = contactnaamOrNull(voorletters, voornaam, voorvoegselAchternaam, achternaam),
                organisatienaam = organisatienaam.trimToNull(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-betrokkene",
        title = "Update betrokkene (beta)",
        description = "Updates the provided fields of an existing betrokkene in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateBetrokkene(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty rol: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty initiator: Boolean? = null,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty voorletters: String? = null,
        @PluginActionProperty voornaam: String? = null,
        @PluginActionProperty voorvoegselAchternaam: String? = null,
        @PluginActionProperty achternaam: String? = null,
        @PluginActionProperty organisatienaam: String? = null,
        @PluginActionProperty bezoekadres: Map<String, Any>? = null,
        @PluginActionProperty correspondentieadres: Map<String, Any>? = null,
    ) = execution.storeReference(Operation.UPDATE, "betrokkene", resultPvName) {
        openKlantClient.patchBetrokkene(
            uuid.toRequiredUuid("uuid"),
            PatchBetrokkeneRequest(
                hadKlantcontact = klantcontactUuid.toNestedUuidIfPresent(),
                rol = rol.trimToNull()?.let { Betrokkene.Rol.fromValue(it) },
                initiator = initiator,
                wasPartij = partijUuid.toNestedUuidIfPresent(),
                bezoekadres = bezoekadres.toAdres(),
                correspondentieadres = correspondentieadres.toAdres(),
                contactnaam = contactnaamOrNull(voorletters, voornaam, voorvoegselAchternaam, achternaam),
                organisatienaam = organisatienaam.trimToNull(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-betrokkene",
        title = "Delete betrokkene (beta)",
        description = "Deletes a betrokkene from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteBetrokkene(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("betrokkene", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteBetrokkene(it, openKlantProperties)
    }

    // Bijlagen

    @PluginAction(
        key = "get-bijlagen",
        title = "Get bijlagen (beta)",
        description = "Retrieves bijlagen from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getBijlagen(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("bijlagen", resultPvName) {
        openKlantClient.getBijlagen(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-bijlage",
        title = "Get bijlage (beta)",
        description = "Retrieves a single bijlage from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getBijlage(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("bijlage", resultPvName) {
        openKlantClient.getBijlage(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-bijlage",
        title = "Create bijlage (beta)",
        description = "Registers a bijlage (attachment reference) for a klantcontact in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createBijlage(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
    ) = execution.storeReference(Operation.CREATE, "bijlage", resultPvName) {
        openKlantClient.createBijlage(
            CreateBijlageRequest(
                wasBijlageVanKlantcontact = klantcontactUuid.toNestedUuidIfPresent(),
                bijlageidentificator =
                    bijlageIdentificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-bijlage",
        title = "Update bijlage (beta)",
        description = "Updates the provided fields of an existing bijlage in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateBijlage(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "bijlage", resultPvName) {
        openKlantClient.patchBijlage(
            uuid.toRequiredUuid("uuid"),
            PatchBijlageRequest(
                wasBijlageVanKlantcontact = klantcontactUuid.toNestedUuidIfPresent(),
                bijlageidentificator =
                    bijlageIdentificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-bijlage",
        title = "Delete bijlage (beta)",
        description = "Deletes a bijlage from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteBijlage(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("bijlage", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteBijlage(it, openKlantProperties)
    }

    // Digitale adressen (complements the existing create/update/get-list actions)

    @PluginAction(
        key = "get-digitaal-adres",
        title = "Get digitaal adres (beta)",
        description = "Retrieves a single digitaal adres from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getDigitaalAdres(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("digitaal-adres", resultPvName) {
        openKlantClient.getDigitaalAdres(uuid.toRequiredNestedUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "delete-digitaal-adres",
        title = "Delete digitaal adres (beta)",
        description = "Deletes a digitaal adres from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteDigitaalAdres(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("digitaal-adres", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteDigitaalAdres(it, openKlantProperties)
    }

    // Interne taken

    @PluginAction(
        key = "get-internetaken",
        title = "Get interne taken (beta)",
        description = "Retrieves interne taken from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getInterneTaken(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("internetaken", resultPvName) {
        openKlantClient.getInterneTaken(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-internetaak",
        title = "Get interne taak (beta)",
        description = "Retrieves a single interne taak from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getInterneTaak(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("internetaak", resultPvName) {
        openKlantClient.getInterneTaak(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-internetaak",
        title = "Create interne taak (beta)",
        description = "Creates an interne taak for an existing klantcontact in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createInterneTaak(
        execution: DelegateExecution,
        @PluginActionProperty gevraagdeHandeling: String? = null,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty status: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty nummer: String? = null,
        @PluginActionProperty referentienummer: String? = null,
        @PluginActionProperty toegewezenAanActoren: String? = null,
        @PluginActionProperty toelichting: String? = null,
        @PluginActionProperty afgehandeldOp: String? = null,
    ) = execution.storeReference(Operation.CREATE, "internetaak", resultPvName) {
        openKlantClient.createInterneTaak(
            CreateInterneTaakRequest(
                gevraagdeHandeling = gevraagdeHandeling.toRequiredString("gevraagdeHandeling"),
                aanleidinggevendKlantcontact = klantcontactUuid.toRequiredNestedUuid("klantcontactUuid"),
                status = InterneTaakStatus.fromValue(status.toRequiredString("status")),
                nummer = nummer.trimToNull(),
                referentienummer = referentienummer.trimToNull(),
                toegewezenAanActoren = toegewezenAanActoren.toNestedUuidList(),
                toelichting = toelichting.trimToNull(),
                afgehandeldOp = afgehandeldOp.trimToNull(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-internetaak",
        title = "Update interne taak (beta)",
        description = "Updates the provided fields of an existing interne taak in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateInterneTaak(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty gevraagdeHandeling: String? = null,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty status: String? = null,
        @PluginActionProperty nummer: String? = null,
        @PluginActionProperty referentienummer: String? = null,
        @PluginActionProperty toegewezenAanActoren: String? = null,
        @PluginActionProperty toelichting: String? = null,
        @PluginActionProperty afgehandeldOp: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "internetaak", resultPvName) {
        openKlantClient.patchInterneTaak(
            uuid.toRequiredUuid("uuid"),
            PatchInterneTaakRequest(
                gevraagdeHandeling = gevraagdeHandeling.trimToNull(),
                aanleidinggevendKlantcontact = klantcontactUuid.toNestedUuidIfPresent(),
                status = status.trimToNull()?.let { InterneTaakStatus.fromValue(it) },
                nummer = nummer.trimToNull(),
                referentienummer = referentienummer.trimToNull(),
                toegewezenAanActoren = toegewezenAanActoren.toNestedUuidList(),
                toelichting = toelichting.trimToNull(),
                afgehandeldOp = afgehandeldOp.trimToNull(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-internetaak",
        title = "Delete interne taak (beta)",
        description = "Deletes an interne taak from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteInterneTaak(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("internetaak", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteInterneTaak(it, openKlantProperties)
    }

    // Klantcontacten (complements the existing contact-history and register actions)

    @PluginAction(
        key = "search-klantcontacten",
        title = "Search klantcontacten (beta)",
        description = "Retrieves klantcontacten from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun searchKlantcontacten(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("klantcontacten", resultPvName) {
        openKlantClient.searchKlantcontacten(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-klantcontact",
        title = "Get klantcontact (beta)",
        description = "Retrieves a single klantcontact from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getKlantcontact(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("klantcontact", resultPvName) {
        openKlantClient.getKlantcontact(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-klantcontact",
        title = "Create klantcontact (beta)",
        description =
            "Creates a klantcontact together with its betrokkene and onderwerpobject in one call. " +
                "Both are mandatory, so the klantcontact is always retrievable as contact history." + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createKlantcontact(
        execution: DelegateExecution,
        @PluginActionProperty kanaal: String? = null,
        @PluginActionProperty onderwerp: String? = null,
        @PluginActionProperty taal: String? = null,
        @PluginActionProperty resultPvName: String,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty vertrouwelijk: Boolean? = null,
        @PluginActionProperty referentienummer: String? = null,
        @PluginActionProperty inhoud: String? = null,
        @PluginActionProperty reactie: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieContactGelukt: Boolean? = null,
        @PluginActionProperty plaatsgevondenOp: String? = null,
        @PluginActionProperty metadata: Map<String, String>? = null,
        // Betrokkene, mandatory
        @PluginActionProperty rol: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty initiator: Boolean? = null,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty voorletters: String? = null,
        @PluginActionProperty voornaam: String? = null,
        @PluginActionProperty voorvoegselAchternaam: String? = null,
        @PluginActionProperty achternaam: String? = null,
        @PluginActionProperty organisatienaam: String? = null,
        // Onderwerpobject, mandatory
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
        @PluginActionProperty betrokkeneResultPvName: String? = null,
        @PluginActionProperty onderwerpobjectResultPvName: String? = null,
    ) {
        logger.debug { "Creating klantcontact in Open Klant..." }

        val response =
            openKlantClient.maakKlantcontact(
                KlantcontactCreationRequest(
                    klantcontact =
                        KlantcontactCreationRequest.KlantcontactRequest(
                            kanaal = kanaal.toRequiredString("kanaal"),
                            onderwerp = onderwerp.toRequiredString("onderwerp"),
                            taal = taal.toRequiredString("taal"),
                            vertrouwelijk = vertrouwelijk ?: false,
                            referentienummer = referentienummer.trimToNull(),
                            inhoud = inhoud.trimToNull(),
                            reactie = reactie.trimToNull(),
                            indicatieContactGelukt = indicatieContactGelukt,
                            plaatsgevondenOp = plaatsgevondenOp.trimToNull(),
                            metadata = metadata,
                        ),
                    betrokkene =
                        KlantcontactCreationRequest.BetrokkeneRequest(
                            wasPartij = partijUuid.toNestedUuidIfPresent(),
                            contactnaam = contactnaamOrNull(voorletters, voornaam, voorvoegselAchternaam, achternaam),
                            rol = Betrokkene.Rol.fromValue(rol.toRequiredString("rol")),
                            organisatienaam = organisatienaam.trimToNull(),
                            initiator = initiator ?: false,
                        ),
                    onderwerpobject =
                        KlantcontactCreationRequest.OnderwerpobjectRequest(
                            onderwerpobjectidentificator =
                                Onderwerpobjectidentificator(
                                    objectId = objectId.toRequiredString("objectId"),
                                    codeObjecttype = codeObjecttype.toRequiredString("codeObjecttype"),
                                    codeRegister = codeRegister.toRequiredString("codeRegister"),
                                    codeSoortObjectId = codeSoortObjectId.toRequiredString("codeSoortObjectId"),
                                ),
                        ),
                ),
                openKlantProperties,
            )

        execution.storeUuid(resultPvName, response.klantcontact)
        execution.storeUuid(betrokkeneResultPvName, response.betrokkene)
        execution.storeUuid(onderwerpobjectResultPvName, response.onderwerpobject)

        logger.info {
            "Successfully created klantcontact in Open Klant " +
                "(uuid: '${response.klantcontact.uuid}', url: '${response.klantcontact.url}')"
        }
    }

    @PluginAction(
        key = "update-klantcontact",
        title = "Update klantcontact (beta)",
        description = "Updates the provided fields of an existing klantcontact in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateKlantcontact(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty kanaal: String? = null,
        @PluginActionProperty onderwerp: String? = null,
        @PluginActionProperty taal: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty vertrouwelijk: Boolean? = null,
        @PluginActionProperty nummer: String? = null,
        @PluginActionProperty referentienummer: String? = null,
        @PluginActionProperty inhoud: String? = null,
        @PluginActionProperty reactie: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieContactGelukt: Boolean? = null,
        @PluginActionProperty plaatsgevondenOp: String? = null,
        @PluginActionProperty metadata: Map<String, String>? = null,
    ) = execution.storeReference(Operation.UPDATE, "klantcontact", resultPvName) {
        openKlantClient.patchKlantcontact(
            uuid.toRequiredUuid("uuid"),
            PatchKlantcontactRequest(
                kanaal = kanaal.trimToNull(),
                onderwerp = onderwerp.trimToNull(),
                taal = taal.trimToNull(),
                vertrouwelijk = vertrouwelijk,
                nummer = nummer.trimToNull(),
                referentienummer = referentienummer.trimToNull(),
                inhoud = inhoud.trimToNull(),
                reactie = reactie.trimToNull(),
                indicatieContactGelukt = indicatieContactGelukt,
                plaatsgevondenOp = plaatsgevondenOp.trimToNull(),
                metadata = metadata,
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-klantcontact",
        title = "Delete klantcontact (beta)",
        description = "Deletes a klantcontact from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteKlantcontact(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("klantcontact", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteKlantcontact(it, openKlantProperties)
    }

    // Onderwerpobjecten

    @PluginAction(
        key = "get-onderwerpobjecten",
        title = "Get onderwerpobjecten (beta)",
        description = "Retrieves onderwerpobjecten from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getOnderwerpobjecten(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("onderwerpobjecten", resultPvName) {
        openKlantClient.getOnderwerpobjecten(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-onderwerpobject",
        title = "Get onderwerpobject (beta)",
        description = "Retrieves a single onderwerpobject from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getOnderwerpobject(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("onderwerpobject", resultPvName) {
        openKlantClient.getOnderwerpobject(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-onderwerpobject",
        title = "Create onderwerpobject (beta)",
        description = "Links a klantcontact to a subject (for example a zaak) in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createOnderwerpobject(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty wasKlantcontactUuid: String? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
    ) = execution.storeReference(Operation.CREATE, "onderwerpobject", resultPvName) {
        openKlantClient.createOnderwerpobject(
            CreateOnderwerpobjectRequest(
                klantcontact = klantcontactUuid.toNestedUuidIfPresent(),
                wasKlantcontact = wasKlantcontactUuid.toNestedUuidIfPresent(),
                onderwerpobjectidentificator =
                    onderwerpobjectidentificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-onderwerpobject",
        title = "Update onderwerpobject (beta)",
        description = "Updates the provided fields of an existing onderwerpobject in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateOnderwerpobject(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty klantcontactUuid: String? = null,
        @PluginActionProperty wasKlantcontactUuid: String? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "onderwerpobject", resultPvName) {
        openKlantClient.patchOnderwerpobject(
            uuid.toRequiredUuid("uuid"),
            PatchOnderwerpobjectRequest(
                klantcontact = klantcontactUuid.toNestedUuidIfPresent(),
                wasKlantcontact = wasKlantcontactUuid.toNestedUuidIfPresent(),
                onderwerpobjectidentificator =
                    onderwerpobjectidentificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-onderwerpobject",
        title = "Delete onderwerpobject (beta)",
        description = "Deletes an onderwerpobject from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteOnderwerpobject(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("onderwerpobject", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteOnderwerpobject(it, openKlantProperties)
    }

    // Partijen (complements the existing get-or-create and store-contact-info actions)

    @PluginAction(
        key = "get-partijen",
        title = "Get partijen (beta)",
        description = "Retrieves partijen from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getPartijen(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("partijen", resultPvName) {
        openKlantClient.getPartijen(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-partij",
        title = "Get partij (beta)",
        description = "Retrieves a single partij from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getPartij(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("partij", resultPvName) {
        openKlantClient.getPartij(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-partij",
        title = "Create partij (beta)",
        description =
            "Creates a new partij in Open Klant. A partij-identificator (for example a BSN) is mandatory, " +
                "because 'Get or create Partij' looks partijen up by their identificator." + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createPartij(
        execution: DelegateExecution,
        @PluginActionProperty soortPartij: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty nummer: String? = null,
        @PluginActionProperty interneNotitie: String? = null,
        @PluginActionProperty voorkeurstaal: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieActief: Boolean? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieGeheimhouding: Boolean? = null,
        @PluginActionProperty digitaleAdressen: String? = null,
        @PluginActionProperty voorkeursDigitaalAdres: String? = null,
        @PluginActionProperty rekeningnummers: String? = null,
        @PluginActionProperty voorkeursRekeningnummer: String? = null,
        @PluginActionProperty bezoekadres: Map<String, Any>? = null,
        @PluginActionProperty correspondentieadres: Map<String, Any>? = null,
        @PluginActionProperty voorletters: String? = null,
        @PluginActionProperty voornaam: String? = null,
        @PluginActionProperty voorvoegselAchternaam: String? = null,
        @PluginActionProperty achternaam: String? = null,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
    ) = execution.storeReference(Operation.CREATE, "partij", resultPvName) {
        openKlantClient.createPartij(
            CreatePartijRequest(
                nummer = nummer.trimToNull(),
                interneNotitie = interneNotitie.trimToNull() ?: "",
                digitaleAdressen = digitaleAdressen.toNestedUuidList() ?: emptyList(),
                voorkeursDigitaalAdres = voorkeursDigitaalAdres.toNestedUuidIfPresent(),
                rekeningnummers = rekeningnummers.toNestedUuidList() ?: emptyList(),
                voorkeursRekeningnummer = voorkeursRekeningnummer.toNestedUuidIfPresent(),
                partijIdentificatoren =
                    listOf(
                        mapOf(
                            "partijIdentificator" to
                                Identificator(
                                    objectId = objectId.toRequiredString("objectId"),
                                    codeObjecttype = codeObjecttype.toRequiredString("codeObjecttype"),
                                    codeRegister = codeRegister.toRequiredString("codeRegister"),
                                    codeSoortObjectId = codeSoortObjectId.toRequiredString("codeSoortObjectId"),
                                ),
                        ),
                    ),
                soortPartij = Partij.SoortPartij.fromValue(soortPartij.toRequiredString("soortPartij")),
                indicatieGeheimhouding = indicatieGeheimhouding,
                voorkeurstaal = voorkeurstaal.trimToNull() ?: DEFAULT_VOORKEURSTAAL,
                indicatieActief = indicatieActief ?: true,
                bezoekadres = bezoekadres.toAdres(),
                correspondentieadres = correspondentieadres.toAdres(),
                partijIdentificatie =
                    contactnaamOrNull(voorletters, voornaam, voorvoegselAchternaam, achternaam)
                        ?.let { Partij.PartijIdentificatie(contactnaam = it) },
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-partij",
        title = "Update partij (beta)",
        description = "Updates the provided fields of an existing partij in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updatePartij(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty soortPartij: String? = null,
        @PluginActionProperty nummer: String? = null,
        @PluginActionProperty interneNotitie: String? = null,
        @PluginActionProperty voorkeurstaal: String? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieActief: Boolean? = null,
        @JsonDeserialize(using = StringToBooleanDeserializer::class)
        @PluginActionProperty indicatieGeheimhouding: Boolean? = null,
        @PluginActionProperty digitaleAdressen: String? = null,
        @PluginActionProperty voorkeursDigitaalAdres: String? = null,
        @PluginActionProperty rekeningnummers: String? = null,
        @PluginActionProperty voorkeursRekeningnummer: String? = null,
        @PluginActionProperty bezoekadres: Map<String, Any>? = null,
        @PluginActionProperty correspondentieadres: Map<String, Any>? = null,
        @PluginActionProperty voorletters: String? = null,
        @PluginActionProperty voornaam: String? = null,
        @PluginActionProperty voorvoegselAchternaam: String? = null,
        @PluginActionProperty achternaam: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "partij", resultPvName) {
        openKlantClient.patchPartij(
            uuid.toRequiredUuid("uuid"),
            PatchPartijRequest(
                nummer = nummer.trimToNull(),
                interneNotitie = interneNotitie.trimToNull(),
                digitaleAdressen = digitaleAdressen.toNestedUuidList(),
                voorkeursDigitaalAdres = voorkeursDigitaalAdres.toNestedUuidIfPresent(),
                rekeningnummers = rekeningnummers.toNestedUuidList(),
                voorkeursRekeningnummer = voorkeursRekeningnummer.toNestedUuidIfPresent(),
                soortPartij = soortPartij.trimToNull()?.let { Partij.SoortPartij.fromValue(it) },
                indicatieGeheimhouding = indicatieGeheimhouding,
                voorkeurstaal = voorkeurstaal.trimToNull(),
                indicatieActief = indicatieActief,
                bezoekadres = bezoekadres.toAdres(),
                correspondentieadres = correspondentieadres.toAdres(),
                partijIdentificatie =
                    contactnaamOrNull(voorletters, voornaam, voorvoegselAchternaam, achternaam)
                        ?.let { Partij.PartijIdentificatie(contactnaam = it) },
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-partij",
        title = "Delete partij (beta)",
        description = "Deletes a partij from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deletePartij(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("partij", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deletePartij(it, openKlantProperties)
    }

    // Partij-identificatoren

    @PluginAction(
        key = "get-partij-identificatoren",
        title = "Get partij-identificatoren (beta)",
        description = "Retrieves partij-identificatoren from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getPartijIdentificatoren(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("partij-identificatoren", resultPvName) {
        openKlantClient.getPartijIdentificatoren(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-partij-identificator",
        title = "Get partij-identificator (beta)",
        description = "Retrieves a single partij-identificator from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getPartijIdentificator(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("partij-identificator", resultPvName) {
        openKlantClient.getPartijIdentificator(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-partij-identificator",
        title = "Create partij-identificator (beta)",
        description = "Registers an identifying number (for example a BSN or KVK number) for a partij" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createPartijIdentificator(
        execution: DelegateExecution,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty subIdentificatorVanUuid: String? = null,
    ) = execution.storeReference(Operation.CREATE, "partij-identificator", resultPvName) {
        openKlantClient.createPartijIdentificator(
            CreatePartijIdentificatorRequest(
                partijIdentificator =
                    Identificator(
                        objectId = objectId.toRequiredString("objectId"),
                        codeObjecttype = codeObjecttype.toRequiredString("codeObjecttype"),
                        codeRegister = codeRegister.toRequiredString("codeRegister"),
                        codeSoortObjectId = codeSoortObjectId.toRequiredString("codeSoortObjectId"),
                    ),
                identificeerdePartij = partijUuid.toNestedUuidIfPresent(),
                subIdentificatorVan = subIdentificatorVanUuid.toNestedUuidIfPresent(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-partij-identificator",
        title = "Update partij-identificator (beta)",
        description = "Updates the provided fields of an existing partij-identificator in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updatePartijIdentificator(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty objectId: String? = null,
        @PluginActionProperty codeObjecttype: String? = null,
        @PluginActionProperty codeRegister: String? = null,
        @PluginActionProperty codeSoortObjectId: String? = null,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty subIdentificatorVanUuid: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "partij-identificator", resultPvName) {
        openKlantClient.patchPartijIdentificator(
            uuid.toRequiredUuid("uuid"),
            PatchPartijIdentificatorRequest(
                partijIdentificator = identificatorOrNull(objectId, codeObjecttype, codeRegister, codeSoortObjectId),
                identificeerdePartij = partijUuid.toNestedUuidIfPresent(),
                subIdentificatorVan = subIdentificatorVanUuid.toNestedUuidIfPresent(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-partij-identificator",
        title = "Delete partij-identificator (beta)",
        description = "Deletes a partij-identificator from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deletePartijIdentificator(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("partij-identificator", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deletePartijIdentificator(it, openKlantProperties)
    }

    // Rekeningnummers

    @PluginAction(
        key = "get-rekeningnummers",
        title = "Get rekeningnummers (beta)",
        description = "Retrieves rekeningnummers from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getRekeningnummers(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("rekeningnummers", resultPvName) {
        openKlantClient.getRekeningnummers(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-rekeningnummer",
        title = "Get rekeningnummer (beta)",
        description = "Retrieves a single rekeningnummer from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getRekeningnummer(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("rekeningnummer", resultPvName) {
        openKlantClient.getRekeningnummer(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-rekeningnummer",
        title = "Create rekeningnummer (beta)",
        description = "Registers a bank account number for a partij in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createRekeningnummer(
        execution: DelegateExecution,
        @PluginActionProperty iban: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty bic: String? = null,
    ) = execution.storeReference(Operation.CREATE, "rekeningnummer", resultPvName) {
        openKlantClient.createRekeningnummer(
            CreateRekeningnummerRequest(
                iban = iban.toRequiredString("iban"),
                partij = partijUuid.toNestedUuidIfPresent(),
                bic = bic.trimToNull(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-rekeningnummer",
        title = "Update rekeningnummer (beta)",
        description = "Updates the provided fields of an existing rekeningnummer in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateRekeningnummer(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty iban: String? = null,
        @PluginActionProperty partijUuid: String? = null,
        @PluginActionProperty bic: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "rekeningnummer", resultPvName) {
        openKlantClient.patchRekeningnummer(
            uuid.toRequiredUuid("uuid"),
            PatchRekeningnummerRequest(
                iban = iban.trimToNull(),
                partij = partijUuid.toNestedUuidIfPresent(),
                bic = bic.trimToNull(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-rekeningnummer",
        title = "Delete rekeningnummer (beta)",
        description = "Deletes a rekeningnummer from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteRekeningnummer(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("rekeningnummer", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteRekeningnummer(it, openKlantProperties)
    }

    // Vertegenwoordigingen

    @PluginAction(
        key = "get-vertegenwoordigingen",
        title = "Get vertegenwoordigingen (beta)",
        description = "Retrieves vertegenwoordigingen from Open Klant based on the provided filters" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getVertegenwoordigingen(
        execution: DelegateExecution,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty queryParams: List<KeyValueQueryParam> = emptyList(),
    ) = execution.storeProjection("vertegenwoordigingen", resultPvName) {
        openKlantClient.getVertegenwoordigingen(queryParams.toQuery(), openKlantProperties)
    }

    @PluginAction(
        key = "get-vertegenwoordiging",
        title = "Get vertegenwoordiging (beta)",
        description = "Retrieves a single vertegenwoordiging from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun getVertegenwoordiging(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeProjection("vertegenwoordiging", resultPvName) {
        openKlantClient.getVertegenwoordiging(uuid.toRequiredUuid("uuid"), openKlantProperties)
    }

    @PluginAction(
        key = "create-vertegenwoordiging",
        title = "Create vertegenwoordiging (beta)",
        description = "Records that one partij represents another partij in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun createVertegenwoordiging(
        execution: DelegateExecution,
        @PluginActionProperty vertegenwoordigendePartijUuid: String? = null,
        @PluginActionProperty vertegenwoordigdePartijUuid: String? = null,
        @PluginActionProperty resultPvName: String,
    ) = execution.storeReference(Operation.CREATE, "vertegenwoordiging", resultPvName) {
        openKlantClient.createVertegenwoordiging(
            CreateVertegenwoordigingRequest(
                vertegenwoordigendePartij =
                    vertegenwoordigendePartijUuid.toRequiredNestedUuid("vertegenwoordigendePartijUuid"),
                vertegenwoordigdePartij =
                    vertegenwoordigdePartijUuid.toRequiredNestedUuid("vertegenwoordigdePartijUuid"),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "update-vertegenwoordiging",
        title = "Update vertegenwoordiging (beta)",
        description = "Updates the provided fields of an existing vertegenwoordiging in Open Klant" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun updateVertegenwoordiging(
        execution: DelegateExecution,
        @PluginActionProperty uuid: String? = null,
        @PluginActionProperty resultPvName: String,
        @PluginActionProperty vertegenwoordigendePartijUuid: String? = null,
        @PluginActionProperty vertegenwoordigdePartijUuid: String? = null,
    ) = execution.storeReference(Operation.UPDATE, "vertegenwoordiging", resultPvName) {
        openKlantClient.patchVertegenwoordiging(
            uuid.toRequiredUuid("uuid"),
            PatchVertegenwoordigingRequest(
                vertegenwoordigendePartij = vertegenwoordigendePartijUuid.toNestedUuidIfPresent(),
                vertegenwoordigdePartij = vertegenwoordigdePartijUuid.toNestedUuidIfPresent(),
            ),
            openKlantProperties,
        )
    }

    @PluginAction(
        key = "delete-vertegenwoordiging",
        title = "Delete vertegenwoordiging (beta)",
        description = "Deletes a vertegenwoordiging from Open Klant by its UUID" + BETA_NOTICE,
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START],
    )
    fun deleteVertegenwoordiging(
        @PluginActionProperty uuid: String? = null,
    ) = deleteByUuid("vertegenwoordiging", uuid.toRequiredUuid("uuid")) {
        openKlantClient.deleteVertegenwoordiging(it, openKlantProperties)
    }

    // Helpers

    private val openKlantProperties: OpenKlantProperties
        get() = OpenKlantProperties(klantinteractiesUrl, token)

    /**
     * Runs a write against Open Klant and stores a reference to the affected object.
     *
     * Writes hand back only the UUID: the full response is a snapshot that goes stale as soon as the next action
     * runs, and a process that needs more than the identifier can retrieve the object with the matching get action.
     */
    private fun <T : Referable> DelegateExecution.storeReference(
        operation: Operation,
        resource: String,
        resultPvName: String?,
        supplier: () -> T,
    ) {
        logger.debug { "${operation.gerund} $resource in Open Klant..." }

        val result = supplier()
        storeUuid(resultPvName, result)

        logger.info {
            "Successfully ${operation.pastTense} $resource in Open Klant " +
                "(uuid: '${result.uuid}', url: '${result.url}')"
        }
    }

    /**
     * Runs a read against Open Klant and stores the retrieved object(s) as a JSON projection.
     *
     * Operaton cannot serialize the API DTOs itself, so the result is converted to a [JsonNode] up front.
     */
    private fun <T : Any> DelegateExecution.storeProjection(
        resource: String,
        resultPvName: String?,
        supplier: () -> T,
    ) {
        logger.debug { "Retrieving $resource from Open Klant..." }

        val result = supplier()
        resultPvName.trimToNull()?.let { setVariable(it, objectMapper.valueToTree<JsonNode>(result)) }

        val retrieved = if (result is Collection<*>) "${result.size} $resource" else resource
        logger.info { "Successfully retrieved $retrieved from Open Klant" }
    }

    /**
     * Runs a read and stores the result as the reflected-map form the contact-history actions have
     * always produced.
     *
     * Kept apart from [storeProjection] so existing processes keep seeing the same keys and the same
     * string-encoded booleans; new actions use the Jackson projection instead.
     */
    private fun <T : Any> DelegateExecution.storeReflectedProjection(
        resource: String,
        resultPvName: String?,
        supplier: () -> T,
    ) {
        logger.debug { "Retrieving $resource from Open Klant..." }

        val result = supplier()
        resultPvName.trimToNull()?.let { setVariable(it, reflectionUtil.deepReflectedMapOf(result)) }

        val retrieved = if (result is Collection<*>) "${result.size} $resource" else resource
        logger.info { "Successfully retrieved $retrieved from Open Klant" }
    }

    /** Stores the UUID of [reference] under the configured name, if the action was given one. */
    private fun DelegateExecution.storeUuid(
        resultPvName: String?,
        reference: Referable?,
    ) {
        val variableName = resultPvName.trimToNull() ?: return
        setVariable(variableName, reference?.uuid?.toString())
    }

    /** [DigitaalAdres] predates [Referable] and carries a nullable uuid, so it needs its own overload. */
    private fun DelegateExecution.storeDigitaalAdresUuid(
        resultPvName: String?,
        digitaalAdres: DigitaalAdres,
    ) {
        val variableName = resultPvName.trimToNull() ?: return
        setVariable(variableName, digitaalAdres.uuid?.toString())
    }

    /**
     * The create and update digitaal-adres actions have always published the whole object rather
     * than just its UUID, so they keep doing so.
     */
    private fun DelegateExecution.storeDigitaalAdresJson(
        resultPvName: String?,
        digitaalAdres: DigitaalAdres,
    ) {
        val variableName = resultPvName.trimToNull() ?: return
        // Map to JSON preemptively, as Operaton has issues serializing it itself
        setVariable(variableName, objectMapper.valueToTree<JsonNode>(digitaalAdres))
    }

    private fun deleteByUuid(
        resource: String,
        uuid: UUID,
        delete: (UUID) -> Unit,
    ) {
        logger.debug { "Deleting $resource with uuid '$uuid' from Open Klant..." }
        delete(uuid)
        logger.info { "Successfully deleted $resource with uuid '$uuid' from Open Klant" }
    }

    /** Verb pair used to log the start and the successful completion of a write action. */
    private enum class Operation(
        val gerund: String,
        val pastTense: String,
    ) {
        CREATE("Creating", "created"),
        UPDATE("Updating", "updated"),
    }

    private fun List<KeyValueQueryParam>.toQuery(): OpenKlantQuery = OpenKlantQuery.fromKeyValueQueryParamList(this)

    private fun Map<String, Any>?.toAdres(): Adres? = this?.let { objectMapper.convertValue(it, Adres::class.java) }

    private fun contactnaamOrNull(
        voorletters: String?,
        voornaam: String?,
        voorvoegselAchternaam: String?,
        achternaam: String?,
    ): Contactnaam? =
        Contactnaam(
            voorletters = voorletters.trimToNull(),
            voornaam = voornaam.trimToNull(),
            voorvoegselAchternaam = voorvoegselAchternaam.trimToNull(),
            achternaam = achternaam.trimToNull(),
        ).takeIf {
            listOfNotNull(it.voorletters, it.voornaam, it.voorvoegselAchternaam, it.achternaam).isNotEmpty()
        }

    private fun identificatorOrNull(
        objectId: String?,
        codeObjecttype: String?,
        codeRegister: String?,
        codeSoortObjectId: String?,
    ): Identificator? =
        Identificator(
            objectId = objectId.trimToNull(),
            codeObjecttype = codeObjecttype.trimToNull(),
            codeRegister = codeRegister.trimToNull(),
            codeSoortObjectId = codeSoortObjectId.trimToNull(),
        ).takeIf {
            listOfNotNull(it.objectId, it.codeObjecttype, it.codeRegister, it.codeSoortObjectId).isNotEmpty()
        }

    private fun bijlageIdentificatorOrNull(
        objectId: String?,
        codeObjecttype: String?,
        codeRegister: String?,
        codeSoortObjectId: String?,
    ): BijlageIdentificator? =
        BijlageIdentificator(
            objectId = objectId.trimToNull(),
            codeObjecttype = codeObjecttype.trimToNull(),
            codeRegister = codeRegister.trimToNull(),
            codeSoortObjectId = codeSoortObjectId.trimToNull(),
        ).takeIf {
            listOfNotNull(it.objectId, it.codeObjecttype, it.codeRegister, it.codeSoortObjectId).isNotEmpty()
        }

    /** All four fields are mandatory, so the identificator is either fully configured or left out. */
    private fun onderwerpobjectidentificatorOrNull(
        objectId: String?,
        codeObjecttype: String?,
        codeRegister: String?,
        codeSoortObjectId: String?,
    ): Onderwerpobjectidentificator? {
        val values = listOf(objectId, codeObjecttype, codeRegister, codeSoortObjectId).map { it.trimToNull() }
        if (values.all { it == null }) {
            return null
        }
        return Onderwerpobjectidentificator(
            objectId = values[0].toRequiredString("objectId"),
            codeObjecttype = values[1].toRequiredString("codeObjecttype"),
            codeRegister = values[2].toRequiredString("codeRegister"),
            codeSoortObjectId = values[3].toRequiredString("codeSoortObjectId"),
        )
    }

    private fun actorIdentificatieOrNull(
        functie: String?,
        emailadres: String?,
        telefoonnummer: String?,
        omschrijving: String?,
        faxnummer: String?,
    ): Actor.ActorIdentificatie? =
        Actor
            .ActorIdentificatie(
                functie = functie.trimToNull(),
                emailadres = emailadres.trimToNull(),
                telefoonnummer = telefoonnummer.trimToNull(),
                omschrijving = omschrijving.trimToNull(),
                faxnummer = faxnummer.trimToNull(),
            ).takeIf {
                listOfNotNull(it.functie, it.emailadres, it.telefoonnummer, it.omschrijving, it.faxnummer).isNotEmpty()
            }

    companion object {
        /** Everything beyond the original Open Klant actions is still taking shape. */
        private const val BETA_NOTICE =
            " This action is still in beta and may change in a future release."

        private const val OUTPUT_PARTIJ_UUID = "partijUuid"
        private const val DEFAULT_DIGITALE_ADRES_REFERENCE = "portaalvoorkeur"
        private const val DEFAULT_VOORKEURSTAAL = "nld"
        private val logger = KotlinLogging.logger { }
    }
}
