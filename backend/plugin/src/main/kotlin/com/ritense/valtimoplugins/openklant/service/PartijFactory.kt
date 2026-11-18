package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.dto.Contactnaam
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.Identificator
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.model.KvkIdentificator
import com.ritense.valtimoplugins.openklant.model.PartijInformation

class PartijFactory {
    fun createFromBsn(partijInformation: PartijInformation): CreatePartijRequest =
        CreatePartijRequest(
            nummer = null,
            interneNotitie = "",
            digitaleAdressen = emptyList(),
            voorkeursDigitaalAdres = null,
            rekeningnummers = emptyList(),
            voorkeursRekeningnummer = null,
            partijIdentificatoren = listOf(getPartijIdentificator(partijInformation)),
            soortPartij = Partij.SoortPartij.PERSOON,
            indicatieGeheimhouding = false,
            voorkeurstaal = "nld",
            indicatieActief = true,
            bezoekadres = null,
            correspondentieadres = null,
            partijIdentificatie = getPartijIdentificatie(partijInformation),
        )

    /** An organisatie from the Handelsregister, identified by its KVK-nummer. */
    fun createOrganisatie(
        kvkNummer: String,
        naam: String?,
    ): CreatePartijRequest =
        CreatePartijRequest(
            nummer = null,
            interneNotitie = "",
            digitaleAdressen = emptyList(),
            voorkeursDigitaalAdres = null,
            rekeningnummers = emptyList(),
            voorkeursRekeningnummer = null,
            partijIdentificatoren =
                listOf(
                    mapOf(
                        "partijIdentificator" to
                            Identificator(
                                objectId = kvkNummer,
                                codeObjecttype = KvkIdentificator.CODE_OBJECTTYPE,
                                codeRegister = KvkIdentificator.CODE_REGISTER,
                                codeSoortObjectId = KvkIdentificator.CODE_SOORT_OBJECT_ID,
                            ),
                    ),
                ),
            soortPartij = Partij.SoortPartij.ORGANISATIE,
            indicatieGeheimhouding = false,
            voorkeurstaal = "nld",
            indicatieActief = true,
            bezoekadres = null,
            correspondentieadres = null,
            partijIdentificatie = Partij.PartijIdentificatie(naam = naam.orEmpty()),
        )

    private fun getPartijIdentificator(partijInformation: PartijInformation): Map<String, Identificator> {
        val identificator =
            Identificator(
                objectId = partijInformation.bsn,
                codeObjecttype = "natuurlijk_persoon",
                codeRegister = "brp",
                codeSoortObjectId = "bsn",
            )
        return mapOf("partijIdentificator" to identificator)
    }

    private fun getPartijIdentificatie(partijInformation: PartijInformation): Partij.PartijIdentificatie {
        val contactnaam =
            Contactnaam(
                voorletters = partijInformation.voorletters,
                voornaam = partijInformation.voornaam,
                voorvoegselAchternaam = partijInformation.voorvoegselAchternaam,
                achternaam = partijInformation.achternaam,
            )
        return Partij.PartijIdentificatie(contactnaam)
    }
}
