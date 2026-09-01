import {PluginSpecification} from '@valtimo/plugin';
import {OpenKlantPluginConfigurationComponent} from './components/open-klant-plugin-configuration/open-klant-plugin-configuration.component';
import {OPEN_KLANT_PLUGIN_LOGO_BASE64} from './assets/open-klant-plugin-logo';
import {GetContactMomentsByBsnComponent} from './components/get-contact-moments-by-bsn/get-contact-moments-by-bsn.component';
import {GetContactMomentsByPartijUuidComponent} from './components/get-contact-moments-by-partij-uuid/get-contact-moments-by-partij-uuid.component';
import {GetContactMomentsByCaseUuidComponent} from './components/open-klant-get-contact-moments-by-case-uuid/open-klant-get-contact-moments-by-case-uuid.component';
import {StoreContactInfoComponent} from './components/open-klant-store-contact-info/open-klant-store-contact-info.component';
import {GetOrCreatePartijComponent} from './components/get-or-create-partij/get-or-create-partij.component';
import {RegisterKlantcontactComponent} from './components/open-klant-register-klantcontact/open-klant-register-klantcontact.component';
import {SetDefaultDigitaalAdresComponent} from './components/set-default-digitaal-adres/set-default-digitaal-adres.component';
import {CreateDigitaalAdresComponent} from './components/create-digitaal-adres/create-digitaal-adres.component';
import {GetDigitaleAdressenComponent} from './components/get-digitale-adressen/get-digitale-adressen.component';
import {UpdateDigitaalAdresComponent} from './components/update-digitaal-adres/update-digitaal-adres.component';
import {ResourceQueryComponent} from './components/resource-query/resource-query.component';
import {ResourceUuidComponent} from './components/resource-uuid/resource-uuid.component';
import {ResourceDeleteComponent} from './components/resource-delete/resource-delete.component';
import {DeletePartijComponent} from './components/delete-partij/delete-partij.component';
import {DeleteKlantcontactComponent} from './components/delete-klantcontact/delete-klantcontact.component';
import {DeleteActorComponent} from './components/delete-actor/delete-actor.component';
import {CreateActorComponent} from './components/create-actor/create-actor.component';
import {UpdateActorComponent} from './components/update-actor/update-actor.component';
import {CreateActorKlantcontactComponent} from './components/create-actorklantcontact/create-actorklantcontact.component';
import {UpdateActorKlantcontactComponent} from './components/update-actorklantcontact/update-actorklantcontact.component';
import {CreateBetrokkeneComponent} from './components/create-betrokkene/create-betrokkene.component';
import {UpdateBetrokkeneComponent} from './components/update-betrokkene/update-betrokkene.component';
import {CreateBijlageComponent} from './components/create-bijlage/create-bijlage.component';
import {UpdateBijlageComponent} from './components/update-bijlage/update-bijlage.component';
import {CreateInterneTaakComponent} from './components/create-internetaak/create-internetaak.component';
import {UpdateInterneTaakComponent} from './components/update-internetaak/update-internetaak.component';
import {CreateKlantcontactComponent} from './components/create-klantcontact/create-klantcontact.component';
import {UpdateKlantcontactComponent} from './components/update-klantcontact/update-klantcontact.component';
import {CreateOnderwerpobjectComponent} from './components/create-onderwerpobject/create-onderwerpobject.component';
import {UpdateOnderwerpobjectComponent} from './components/update-onderwerpobject/update-onderwerpobject.component';
import {CreatePartijComponent} from './components/create-partij/create-partij.component';
import {UpdatePartijComponent} from './components/update-partij/update-partij.component';
import {CreatePartijIdentificatorComponent} from './components/create-partij-identificator/create-partij-identificator.component';
import {UpdatePartijIdentificatorComponent} from './components/update-partij-identificator/update-partij-identificator.component';
import {CreateRekeningnummerComponent} from './components/create-rekeningnummer/create-rekeningnummer.component';
import {UpdateRekeningnummerComponent} from './components/update-rekeningnummer/update-rekeningnummer.component';
import {CreateVertegenwoordigingComponent} from './components/create-vertegenwoordiging/create-vertegenwoordiging.component';
import {UpdateVertegenwoordigingComponent} from './components/update-vertegenwoordiging/update-vertegenwoordiging.component';

const openKlantPluginSpecification: PluginSpecification = {
    pluginId: 'openklant',
    pluginConfigurationComponent: OpenKlantPluginConfigurationComponent,
    pluginLogoBase64: OPEN_KLANT_PLUGIN_LOGO_BASE64,
    functionConfigurationComponents: {
        'get-contact-moments-by-bsn': GetContactMomentsByBsnComponent,
        'get-contact-moments-by-partij-uuid': GetContactMomentsByPartijUuidComponent,
        'get-contact-moments-by-case-uuid': GetContactMomentsByCaseUuidComponent,
        'store-contact-info': StoreContactInfoComponent,
        'get-or-create-partij': GetOrCreatePartijComponent,
        'register-klantcontact': RegisterKlantcontactComponent,
        'set-default-digitaal-adres': SetDefaultDigitaalAdresComponent,
        'create-digitaal-adres': CreateDigitaalAdresComponent,
        'get-digitale-adressen': GetDigitaleAdressenComponent,
        'update-digitaal-adres': UpdateDigitaalAdresComponent,
        'get-actoren': ResourceQueryComponent,
        'get-actorklantcontacten': ResourceQueryComponent,
        'get-betrokkenen': ResourceQueryComponent,
        'get-bijlagen': ResourceQueryComponent,
        'get-internetaken': ResourceQueryComponent,
        'search-klantcontacten': ResourceQueryComponent,
        'get-onderwerpobjecten': ResourceQueryComponent,
        'get-partijen': ResourceQueryComponent,
        'get-partij-identificatoren': ResourceQueryComponent,
        'get-rekeningnummers': ResourceQueryComponent,
        'get-vertegenwoordigingen': ResourceQueryComponent,
        'get-actor': ResourceUuidComponent,
        'get-actorklantcontact': ResourceUuidComponent,
        'get-betrokkene': ResourceUuidComponent,
        'get-bijlage': ResourceUuidComponent,
        'get-digitaal-adres': ResourceUuidComponent,
        'get-internetaak': ResourceUuidComponent,
        'get-klantcontact': ResourceUuidComponent,
        'get-onderwerpobject': ResourceUuidComponent,
        'get-partij': ResourceUuidComponent,
        'get-partij-identificator': ResourceUuidComponent,
        'get-rekeningnummer': ResourceUuidComponent,
        'get-vertegenwoordiging': ResourceUuidComponent,
        'delete-actorklantcontact': ResourceDeleteComponent,
        'delete-betrokkene': ResourceDeleteComponent,
        'delete-bijlage': ResourceDeleteComponent,
        'delete-digitaal-adres': ResourceDeleteComponent,
        'delete-internetaak': ResourceDeleteComponent,
        'delete-onderwerpobject': ResourceDeleteComponent,
        'delete-partij-identificator': ResourceDeleteComponent,
        'delete-rekeningnummer': ResourceDeleteComponent,
        'delete-vertegenwoordiging': ResourceDeleteComponent,
        'delete-partij': DeletePartijComponent,
        'delete-klantcontact': DeleteKlantcontactComponent,
        'delete-actor': DeleteActorComponent,
        'create-actor': CreateActorComponent,
        'update-actor': UpdateActorComponent,
        'create-actorklantcontact': CreateActorKlantcontactComponent,
        'update-actorklantcontact': UpdateActorKlantcontactComponent,
        'create-betrokkene': CreateBetrokkeneComponent,
        'update-betrokkene': UpdateBetrokkeneComponent,
        'create-bijlage': CreateBijlageComponent,
        'update-bijlage': UpdateBijlageComponent,
        'create-internetaak': CreateInterneTaakComponent,
        'update-internetaak': UpdateInterneTaakComponent,
        'create-klantcontact': CreateKlantcontactComponent,
        'update-klantcontact': UpdateKlantcontactComponent,
        'create-onderwerpobject': CreateOnderwerpobjectComponent,
        'update-onderwerpobject': UpdateOnderwerpobjectComponent,
        'create-partij': CreatePartijComponent,
        'update-partij': UpdatePartijComponent,
        'create-partij-identificator': CreatePartijIdentificatorComponent,
        'update-partij-identificator': UpdatePartijIdentificatorComponent,
        'create-rekeningnummer': CreateRekeningnummerComponent,
        'update-rekeningnummer': UpdateRekeningnummerComponent,
        'create-vertegenwoordiging': CreateVertegenwoordigingComponent,
        'update-vertegenwoordiging': UpdateVertegenwoordigingComponent,
    },
    pluginTranslations: {
        nl: {
            title: 'Open Klant',
            description: 'Een plugin voor het ophalen en versturen van Open Klant-gegevens.',

            // Common
            resultPvName: 'Naam van resultaat-procesvariabele',
            resultPvNameTooltip:
                'De procesvariabele waarin de referentie (UUID) naar het aangemaakte of bijgewerkte object wordt opgeslagen.',
            betrokkeneResultPvName: 'Naam van procesvariabele voor betrokkene-UUID',
            onderwerpobjectResultPvName: 'Naam van procesvariabele voor onderwerpobject-UUID',
            bsn: 'BSN',
            partijUuid: 'Partij-UUID',
            caseUuid: 'Open-Zaak-zaak-UUID',
            caseUuidTooltip:
                "Verwijzing naar de UUID van de zaak in Open Zaak, bijvoorbeeld 'pv:zaakUuid'.",
            variableFieldTooltip:
                "Dit veld kan zowel de letterlijke waarde, of het pad naar een (proces)variabele bevatten welke de waarde bevat (b.v. 'pv:/resultaat')",

            // Configuration
            configurationTitle: 'Configuratie van de Open-Klantplugin',
            configurationTitleTooltip:
                'In dit onderdeel configureer je de Open-Klantplugin om eenvoudig gegevens te kunnen verzenden en ophalen.',
            klantinteractiesUrl: 'Klantinteracties-URL',
            token: 'Open-Klanttoken',

            // Store contact info
            'store-contact-info': 'Maak Digitaal Adres (en Partij) aan',
            firstName: 'Voornaam',
            inFix: 'Tussenvoegsel',
            lastName: 'Achternaam',
            emailAddress: 'E-mailadres',

            // Get or create Partij
            'get-or-create-partij': 'Haal Partij op of maak een Partij aan',

            // Get contact moments by BSN
            'get-contact-moments-by-bsn': 'Contactgeschiedenis ophalen op basis van BSN',

            // Get contact moments by Partij UUID
            'get-contact-moments-by-partij-uuid': 'Contactgeschiedenis ophalen op basis van Partij-UUID',

            // Get contact moments by case UUID
            'get-contact-moments-by-case-uuid': 'Contactgeschiedenis ophalen op basis van Open-Zaak-UUID',

            // Register contact moment
            'register-klantcontact': 'Registreer nieuw klantcontact',
            objectTypeId: "Type van het object, bijvoorbeeld: 'zaak'",
            referentienummer: 'Referentienummer',
            kanaal: 'Communicatiekanaal',
            onderwerp: 'Onderwerp',
            inhoud: 'Inhoud',
            reactie: 'Reactie',
            indicatieContactGelukt: 'Indicatie contact gelukt',
            vertrouwelijk: 'Vertrouwelijk (true/false)',
            taal: 'Taal (ISO 639-2/B-formaat)',
            plaatsgevondenOp: 'Plaatsgevonden op (ISO 8601)',
            metadata: 'Metadata',
            voorletters: 'Voorletters',
            voornaam: 'Voornaam',
            voorvoegselAchternaam: 'Voorvoegsel achternaam',
            achternaam: 'Achternaam',
            heeftBetrokkene: 'Heeft betrokkene (niet aanvinken voor een anoniem klantcontact)',

            // Set standaard digitaal adres
            'set-default-digitaal-adres': 'Instellen van standaard digitaal adres',
            digitaalAdres: 'Digitaal adres',
            soortDigitaalAdres: "Soort digitaal adres ('email'/'telefoonnummer'/'overig')",
            verificatieDatum: 'Verificatiedatum (YYYY-MM-DD)',
            verificatieDatumTooltip:
                'Referentie naar de datum waarop het digitale adres is geverifieerd. Moet worden opgegeven in het formaat YYYY-MM-DD.',

            // Create digitaal adres
            'create-digitaal-adres': 'Maak een digitaal adres aan',
            verstrektDoorBetrokkene: 'UUID van digitaal adres verstrekt door betrokkene',
            verstrektDoorBetrokkeneTooltip:
                "Verwijzing naar de UUID van de betrokkene die het digitale adres verstrekt, bijvoorbeeld 'pv:/betrokkeneUuid'",
            verstrektDoorPartij: 'UUID van digitaal adres verstrekt door partij',
            verstrektDoorPartijTooltip:
                "Verwijzing naar de UUID van de partij die het digitale adres verstrekt, bijvoorbeeld 'pv:/partijUuid'",
            adres: 'Adres',
            isStandaardAdres: 'Is standaard adres',
            isStandaardAdresTooltip:
                "Verwijzing naar een booleanwaarde die bepaalt of het digitale adres het standaard adres is (true/false). Indien dit veld leeg wordt gelaten, wordt dit automatisch op 'false' gezet.",
            omschrijving: 'Omschrijving',
            referentie: 'Referentie',
            referentieTooltip: 'Machine-leesbare tag voor unieke identificatie van het digitaal adres.',

            // Update digitaal adres
            'update-digitaal-adres': 'Pas een bestaand digitaal adres aan',

            // Get digitale adressen
            'get-digitale-adressen': 'Digitale adressen ophalen',
            queryParams: 'Query parameters',
            queryParamsTooltip:
                'Verwijzing naar de query parameters voor het ophalen van digitale adressen, bijvoorbeeld \'pv:/queryParams\'. Formaat: [{"key": "<parameternaam>", "value": "<parameterwaarde>"}, ...]',

            // Resource actions
            'get-actoren': 'Actoren ophalen (beta)',
            'get-actor': 'Actor ophalen (beta)',
            'create-actor': 'Actor aanmaken (beta)',
            'update-actor': 'Actor bijwerken (beta)',
            'delete-actor': 'Actor verwijderen (beta)',
            'get-actorklantcontacten': 'Actorklantcontacten ophalen (beta)',
            'get-actorklantcontact': 'Actorklantcontact ophalen (beta)',
            'create-actorklantcontact': 'Actor aan klantcontact koppelen (beta)',
            'update-actorklantcontact': 'Actorklantcontact bijwerken (beta)',
            'delete-actorklantcontact': 'Actorklantcontact verwijderen (beta)',
            'get-betrokkenen': 'Betrokkenen ophalen (beta)',
            'get-betrokkene': 'Betrokkene ophalen (beta)',
            'create-betrokkene': 'Betrokkene aanmaken (beta)',
            'update-betrokkene': 'Betrokkene bijwerken (beta)',
            'delete-betrokkene': 'Betrokkene verwijderen (beta)',
            'get-bijlagen': 'Bijlagen ophalen (beta)',
            'get-bijlage': 'Bijlage ophalen (beta)',
            'create-bijlage': 'Bijlage aanmaken (beta)',
            'update-bijlage': 'Bijlage bijwerken (beta)',
            'delete-bijlage': 'Bijlage verwijderen (beta)',
            'get-digitaal-adres': 'Digitaal adres ophalen (beta)',
            'delete-digitaal-adres': 'Digitaal adres verwijderen (beta)',
            'get-internetaken': 'Interne taken ophalen (beta)',
            'get-internetaak': 'Interne taak ophalen (beta)',
            'create-internetaak': 'Interne taak aanmaken (beta)',
            'update-internetaak': 'Interne taak bijwerken (beta)',
            'delete-internetaak': 'Interne taak verwijderen (beta)',
            'search-klantcontacten': 'Klantcontacten zoeken (beta)',
            'get-klantcontact': 'Klantcontact ophalen (beta)',
            'create-klantcontact': 'Klantcontact aanmaken (beta)',
            'update-klantcontact': 'Klantcontact bijwerken (beta)',
            'delete-klantcontact': 'Klantcontact verwijderen (beta)',
            'get-onderwerpobjecten': 'Onderwerpobjecten ophalen (beta)',
            'get-onderwerpobject': 'Onderwerpobject ophalen (beta)',
            'create-onderwerpobject': 'Onderwerpobject aanmaken (beta)',
            'update-onderwerpobject': 'Onderwerpobject bijwerken (beta)',
            'delete-onderwerpobject': 'Onderwerpobject verwijderen (beta)',
            'get-partijen': 'Partijen ophalen (beta)',
            'get-partij': 'Partij ophalen (beta)',
            'create-partij': 'Partij aanmaken (beta)',
            'update-partij': 'Partij bijwerken (beta)',
            'delete-partij': 'Partij verwijderen (beta)',
            'get-partij-identificatoren': 'Partij-identificatoren ophalen (beta)',
            'get-partij-identificator': 'Partij-identificator ophalen (beta)',
            'create-partij-identificator': 'Partij-identificator aanmaken (beta)',
            'update-partij-identificator': 'Partij-identificator bijwerken (beta)',
            'delete-partij-identificator': 'Partij-identificator verwijderen (beta)',
            'get-rekeningnummers': 'Rekeningnummers ophalen (beta)',
            'get-rekeningnummer': 'Rekeningnummer ophalen (beta)',
            'create-rekeningnummer': 'Rekeningnummer aanmaken (beta)',
            'update-rekeningnummer': 'Rekeningnummer bijwerken (beta)',
            'delete-rekeningnummer': 'Rekeningnummer verwijderen (beta)',
            'get-vertegenwoordigingen': 'Vertegenwoordigingen ophalen (beta)',
            'get-vertegenwoordiging': 'Vertegenwoordiging ophalen (beta)',
            'create-vertegenwoordiging': 'Vertegenwoordiging aanmaken (beta)',
            'update-vertegenwoordiging': 'Vertegenwoordiging bijwerken (beta)',
            'delete-vertegenwoordiging': 'Vertegenwoordiging verwijderen (beta)',
            uuid: 'UUID',
            naam: 'Naam',
            nummer: 'Nummer',
            soortActor: 'Soort actor',
            indicatieActief: 'Indicatie actief (true/false)',
            objectId: 'Object-ID',
            codeObjecttype: 'Code objecttype',
            codeRegister: 'Code register',
            codeSoortObjectId: 'Code soort object-ID',
            functie: 'Functie',
            emailadres: 'E-mailadres',
            telefoonnummer: 'Telefoonnummer',
            faxnummer: 'Faxnummer',
            actorUuid: 'Actor-UUID',
            klantcontactUuid: 'Klantcontact-UUID',
            wasKlantcontactUuid: 'UUID van het voorgaande klantcontact',
            subIdentificatorVanUuid: 'UUID van de bovenliggende partij-identificator',
            rol: 'Rol',
            initiator: 'Initiator (true/false)',
            organisatienaam: 'Organisatienaam',
            bezoekadres: 'Bezoekadres',
            correspondentieadres: 'Correspondentieadres',
            gevraagdeHandeling: 'Gevraagde handeling',
            status: 'Status',
            toegewezenAanActoren: 'Toegewezen aan actoren',
            toelichting: 'Toelichting',
            afgehandeldOp: 'Afgehandeld op (ISO 8601)',
            soortPartij: 'Soort partij',
            interneNotitie: 'Interne notitie',
            voorkeurstaal: 'Voorkeurstaal (ISO 639-2/B)',
            indicatieGeheimhouding: 'Indicatie geheimhouding (true/false)',
            digitaleAdressen: 'Digitale adressen',
            voorkeursDigitaalAdres: 'Voorkeurs digitaal adres (UUID)',
            rekeningnummers: 'Rekeningnummers',
            voorkeursRekeningnummer: 'Voorkeursrekeningnummer (UUID)',
            iban: 'IBAN',
            bic: 'BIC',
            vertegenwoordigendePartijUuid: 'UUID van de vertegenwoordigende partij',
            vertegenwoordigdePartijUuid: 'UUID van de vertegenwoordigde partij',
            soortActorTooltip: 'Eén van: \'medewerker\', \'geautomatiseerde_actor\' of \'organisatorische_eenheid\'.',
            soortPartijTooltip: 'Eén van: \'persoon\', \'organisatie\' of \'contactpersoon\'.',
            rolTooltip: 'Eén van: \'klant\' of \'vertegenwoordiger\'.',
            statusTooltip: 'Eén van: \'te_verwerken\' of \'verwerkt\'.',
            uuidListTooltip: 'Kommagescheiden lijst van UUID\'s, of een verwijzing naar een procesvariabele die deze bevat.',
            adresObjectTooltip: 'Verwijzing naar een procesvariabele met een adresobject, bijvoorbeeld \'pv:bezoekadres\'. Ondersteunde velden: nummeraanduidingId, straatnaam, huisnummer, huisnummertoevoeging, postcode, stad, adresregel1, adresregel2, adresregel3 en land.',
            'resource-queryDescription': 'Haalt standaard alle pagina\'s op en zet het resultaat als JSON in de procesvariabele. Filter met query parameters, of geef zelf \'page\' en \'pageSize\' mee om precies één pagina op te halen. Zonder filter kan dit een zeer groot resultaat opleveren. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'resource-uuidDescription': 'Haalt één object op via zijn UUID en zet de volledige API-respons als JSON in de procesvariabele. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'resource-deleteDescription': 'Verwijdert het object definitief uit Open Klant. Dit kan niet ongedaan worden gemaakt. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'delete-partijDescription': 'Verwijdert de partij definitief uit Open Klant. Let op: Open Klant verwijdert ook alle digitale adressen en rekeningnummers van deze partij. Dit kan niet ongedaan worden gemaakt. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'delete-klantcontactDescription': 'Verwijdert het klantcontact definitief uit Open Klant. Let op: Open Klant verwijdert ook alle betrokkenen, onderwerpobjecten en interne taken van dit klantcontact, en het klantcontact verdwijnt daarmee uit de contactgeschiedenis. Dit kan niet ongedaan worden gemaakt. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'delete-actorDescription': 'Verwijdert de actor definitief uit Open Klant. Koppelingen met klantcontacten verdwijnen mee, maar interne taken die aan deze actor waren toegewezen blijven bestaan zonder toegewezen actor. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-actorDescription': 'Maakt een actor aan: een medewerker, een geautomatiseerde actor of een organisatorische eenheid. Welke velden van de actor-identificatie gelden hangt af van het soort actor: functie, e-mailadres en telefoonnummer voor een medewerker; functie en omschrijving voor een geautomatiseerde actor; omschrijving, e-mailadres, faxnummer en telefoonnummer voor een organisatorische eenheid. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-actorDescription': 'Werkt een bestaande actor bij. Alleen de losse velden die je invult worden verstuurd; de overige losse velden blijven ongewijzigd. Voor de samengestelde velden gelden de uitzonderingen hieronder. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-actorPatchWarning':
                'Let op: Open Klant negeert bij het bijwerken elke wijziging aan de actoridentificatie (functie, e-mailadres, telefoonnummer, omschrijving en faxnummer). Die velden blijven ongewijzigd en je krijgt geen foutmelding. Vul de vier objectidentificatie-velden (object-ID, code objecttype, code register en code soort object-ID) altijd samen in: laat je er één leeg, dan wist Open Klant de andere drie.',
            'create-actorklantcontactDescription': 'Koppelt een bestaande actor aan een bestaand klantcontact. Beide moeten al bestaan; deze actie maakt zelf geen actor of klantcontact aan. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-actorklantcontactDescription': 'Laat een bestaande koppeling naar een andere actor en/of een ander klantcontact wijzen. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-betrokkeneDescription': 'Registreert een persoon of organisatie als betrokkene bij een bestaand klantcontact. Koppel een bestaande partij via het partij-UUID, of laat dat leeg en vul alleen een contactnaam in voor een betrokkene die niet als partij is vastgelegd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-betrokkeneDescription': 'Werkt een bestaande betrokkene bij. Alleen de losse velden die je invult worden verstuurd; de overige losse velden blijven ongewijzigd. Voor de samengestelde velden gelden de uitzonderingen hieronder. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-betrokkenePatchWarning':
                'Let op: vul de naamvelden (voorletters, voornaam, voorvoegsel achternaam en achternaam) altijd samen in, en bezoekadres en correspondentieadres altijd volledig. Open Klant vervangt deze samengestelde velden in hun geheel, dus alles wat je leeg laat wordt gewist.',
            'create-bijlageDescription': 'Legt een verwijzing vast naar een document in een extern register, bijvoorbeeld een document in Open Zaak. Er wordt geen bestand geüpload: alleen de identificatie van het document wordt opgeslagen bij het klantcontact. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-bijlageDescription': 'Werkt een bestaande bijlage bij. Alleen de losse velden die je invult worden verstuurd; de overige losse velden blijven ongewijzigd. Voor de bijlage-identificatie geldt de uitzondering hieronder. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-bijlagePatchWarning':
                'Let op: vul de vier bijlage-identificatievelden (object-ID, code objecttype, code register en code soort object-ID) altijd samen in. Open Klant vervangt dit veld in zijn geheel, dus laat je er één leeg, dan wist Open Klant de andere drie.',
            'create-internetaakDescription': 'Maakt een interne taak aan als opvolging van een bestaand klantcontact, bijvoorbeeld \'terugbellen\'. De taak kan aan een of meer actoren worden toegewezen. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-internetaakDescription': 'Werkt een bestaande interne taak bij, bijvoorbeeld om de status op \'verwerkt\' te zetten. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-klantcontactDescription': 'Maakt een klantcontact aan, samen met de betrokkene en het onderwerpobject, in één aanroep. Betrokkene en onderwerpobject zijn verplicht: zonder betrokkene en onderwerpobject is het klantcontact niet terug te vinden in de contactgeschiedenis. Het onderwerpobject verwijst naar het onderwerp van het gesprek, bijvoorbeeld een zaak in Open Zaak. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-klantcontactDescription': 'Werkt een bestaand klantcontact bij. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-onderwerpobjectDescription': 'Koppelt een bestaand klantcontact aan het onderwerp waar het over ging, bijvoorbeeld een zaak in Open Zaak. Deze koppeling zorgt ervoor dat het klantcontact via het zaak-UUID terug te vinden is. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-onderwerpobjectDescription': 'Werkt een bestaand onderwerpobject bij. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-partijDescription': 'Maakt een nieuwe partij aan. De partij-identificator (bijvoorbeeld een BSN of KVK-nummer) is verplicht, omdat \'Haal Partij op of maak een Partij aan\' partijen op hun identificator opzoekt: zonder identificator is de partij later niet terug te vinden en ontstaan er dubbele partijen. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-partijDescription': 'Werkt een bestaande partij bij. Alleen de losse velden die je invult worden verstuurd; de overige losse velden blijven ongewijzigd. Voor de samengestelde velden gelden de uitzonderingen hieronder. Partij-identificatoren kunnen hier niet worden gewijzigd; gebruik daarvoor de acties voor partij-identificatoren. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-partijPatchWarning':
                'Let op: Open Klant negeert bij het bijwerken elke wijziging aan de naam (voorletters, voornaam, voorvoegsel achternaam en achternaam). Die velden blijven ongewijzigd en je krijgt geen foutmelding. Vul bezoekadres en correspondentieadres altijd volledig in: Open Klant vervangt zo\'n adres in zijn geheel, dus ontbrekende velden worden gewist.',
            'create-partij-identificatorDescription': 'Legt een identificerend nummer vast bij een partij, bijvoorbeeld een BSN uit de BRP of een KVK-nummer uit het handelsregister. Een partij is hiermee op dat nummer op te zoeken. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-partij-identificatorDescription': 'Werkt een bestaande partij-identificator bij. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-rekeningnummerDescription': 'Legt een rekeningnummer (IBAN, optioneel met BIC) vast bij een partij in Open Klant. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-rekeningnummerDescription': 'Werkt een bestaand rekeningnummer bij. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'create-vertegenwoordigingDescription': 'Legt vast dat de ene partij een andere partij vertegenwoordigt, bijvoorbeeld een gemachtigde die namens een inwoner optreedt. Beide partijen moeten al bestaan. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
            'update-vertegenwoordigingDescription': 'Werkt een bestaande vertegenwoordiging bij. Alleen de velden die je invult worden verstuurd; de overige velden blijven ongewijzigd. Deze actie is nog in beta en kan in een toekomstige versie wijzigen.',
        },

        en: {
            title: 'Open Klant',
            description: 'A plugin for retrieving and sending Open Klant data.',

            // Common
            resultPvName: 'Result process variable name',
            resultPvNameTooltip:
                'The process variable that receives the reference (UUID) to the created or updated object.',
            betrokkeneResultPvName: 'Process variable name for the betrokkene UUID',
            onderwerpobjectResultPvName: 'Process variable name for the onderwerpobject UUID',
            bsn: 'BSN (citizen service number)',
            partijUuid: 'Partij UUID',
            caseUuid: 'Open Zaak case UUID',
            caseUuidTooltip:
                "Reference to the UUID of the case in Open Zaak, for example 'pv:zaakUuid'.",
            variableFieldTooltip:
                "This field accepts either a literal value or a path to a (process) variable containing the value (e.g. 'pv:/result')",

            // Configuration
            configurationTitle: 'Open Klant plugin configuration',
            configurationTitleTooltip:
                'In this section, you configure the Open Klant plugin to easily send and retrieve data.',
            klantinteractiesUrl: 'Klantinteracties URL',
            token: 'Open Klant token',

            // Store contact info
            'store-contact-info': "Create 'Digitaal Adres' (digital address) (and Partij)",
            firstName: 'First name',
            inFix: 'Name infix',
            lastName: 'Last name',
            emailAddress: 'Email address',

            // Get or create Partij
            'get-or-create-partij': 'Get Partij or create a Partij',

            // Get contact moments by BSN
            'get-contact-moments-by-bsn': 'Retrieve contact history based on BSN',

            // Get contact moments by Partij UUID
            'get-contact-moments-by-partij-uuid': 'Retrieve contact history based on Partij UUID',

            // Get contact moments by case UUID
            'get-contact-moments-by-case-uuid': 'Retrieve contact history based on Open Zaak case UUID',

            // Register contact moment
            'register-klantcontact': 'Register new klantcontact (customer contact)',
            objectTypeId: "Object type, for example: 'zaak'",
            referentienummer: 'Reference number',
            kanaal: 'Communication channel',
            onderwerp: 'Subject',
            inhoud: 'Message content',
            reactie: 'Reaction',
            indicatieContactGelukt: 'Successfully contacted',
            vertrouwelijk: 'Confidential (true/false)',
            taal: 'Language (ISO 639-2/B format)',
            plaatsgevondenOp: 'Occurred on (ISO 8601)',
            metadata: 'Metadata',
            voorletters: 'Initials',
            voornaam: 'First name',
            voorvoegselAchternaam: 'Name infix',
            achternaam: 'Last name',
            heeftBetrokkene: 'Has an involved party (leave unchecked for an anonymous contact moment)',

            // Set default digital address
            'set-default-digitaal-adres': 'Set default digital address',
            digitaalAdres: 'Digital address',
            soortDigitaalAdres: "Type of digital address ('email'/'telefoonnummer'/'overig')",
            verificatieDatum: 'Verification datum (YYYY-MM-DD)',
            verificatieDatumTooltip:
                'Reference to the date on which the digital address was verified. Must be provided in the format YYYY-MM-DD.',

            // Create digital address
            'create-digitaal-adres': 'Create a digital address',
            verstrektDoorBetrokkene: 'UUID of digital address provided by individual',
            verstrektDoorBetrokkeneTooltip:
                "Reference to the UUID of the individual providing the digital address, for example 'pv:/betrokkeneUuid'",
            verstrektDoorPartij: 'UUID of digital address provided by organization',
            verstrektDoorPartijTooltip:
                "Reference to the UUID of the organization providing the digital address, for example 'pv:/partijUuid'",
            adres: 'Address',
            isStandaardAdres: 'Is default address',
            isStandaardAdresTooltip:
                "Reference to a boolean value indicating whether the digital address is the default address (true/false). If this field is left empty, it will automatically be set to 'false'.",
            omschrijving: 'Description',
            referentie: 'Reference',
            referentieTooltip: 'Machine-readable tag for unique identification of the digital address.',

            // Update digital address
            'update-digitaal-adres': 'Update an existing digital address',

            // Get digitale adressen
            'get-digitale-adressen': 'Retrieve digital addresses',
            queryParams: 'Query parameters',
            queryParamsTooltip:
                'Reference to the query parameters for retrieving digital addresses, for example \'pv:/queryParams\'. Format: [{"key": "<parameter name>", "value": "<parameter value>"}], ...]',

            // Resource actions
            'get-actoren': 'Retrieve actoren (beta)',
            'get-actor': 'Retrieve an actor (beta)',
            'create-actor': 'Create an actor (beta)',
            'update-actor': 'Update an actor (beta)',
            'delete-actor': 'Delete an actor (beta)',
            'get-actorklantcontacten': 'Retrieve actorklantcontacten (beta)',
            'get-actorklantcontact': 'Retrieve an actorklantcontact (beta)',
            'create-actorklantcontact': 'Link an actor to a klantcontact (beta)',
            'update-actorklantcontact': 'Update an actorklantcontact (beta)',
            'delete-actorklantcontact': 'Delete an actorklantcontact (beta)',
            'get-betrokkenen': 'Retrieve betrokkenen (beta)',
            'get-betrokkene': 'Retrieve a betrokkene (beta)',
            'create-betrokkene': 'Create a betrokkene (beta)',
            'update-betrokkene': 'Update a betrokkene (beta)',
            'delete-betrokkene': 'Delete a betrokkene (beta)',
            'get-bijlagen': 'Retrieve bijlagen (beta)',
            'get-bijlage': 'Retrieve a bijlage (beta)',
            'create-bijlage': 'Create a bijlage (beta)',
            'update-bijlage': 'Update a bijlage (beta)',
            'delete-bijlage': 'Delete a bijlage (beta)',
            'get-digitaal-adres': 'Retrieve a digital address (beta)',
            'delete-digitaal-adres': 'Delete a digital address (beta)',
            'get-internetaken': 'Retrieve interne taken (beta)',
            'get-internetaak': 'Retrieve an interne taak (beta)',
            'create-internetaak': 'Create an interne taak (beta)',
            'update-internetaak': 'Update an interne taak (beta)',
            'delete-internetaak': 'Delete an interne taak (beta)',
            'search-klantcontacten': 'Search klantcontacten (beta)',
            'get-klantcontact': 'Retrieve a klantcontact (beta)',
            'create-klantcontact': 'Create a klantcontact (beta)',
            'update-klantcontact': 'Update a klantcontact (beta)',
            'delete-klantcontact': 'Delete a klantcontact (beta)',
            'get-onderwerpobjecten': 'Retrieve onderwerpobjecten (beta)',
            'get-onderwerpobject': 'Retrieve an onderwerpobject (beta)',
            'create-onderwerpobject': 'Create an onderwerpobject (beta)',
            'update-onderwerpobject': 'Update an onderwerpobject (beta)',
            'delete-onderwerpobject': 'Delete an onderwerpobject (beta)',
            'get-partijen': 'Retrieve partijen (beta)',
            'get-partij': 'Retrieve a partij (beta)',
            'create-partij': 'Create a partij (beta)',
            'update-partij': 'Update a partij (beta)',
            'delete-partij': 'Delete a partij (beta)',
            'get-partij-identificatoren': 'Retrieve partij-identificatoren (beta)',
            'get-partij-identificator': 'Retrieve a partij-identificator (beta)',
            'create-partij-identificator': 'Create a partij-identificator (beta)',
            'update-partij-identificator': 'Update a partij-identificator (beta)',
            'delete-partij-identificator': 'Delete a partij-identificator (beta)',
            'get-rekeningnummers': 'Retrieve rekeningnummers (beta)',
            'get-rekeningnummer': 'Retrieve a rekeningnummer (beta)',
            'create-rekeningnummer': 'Create a rekeningnummer (beta)',
            'update-rekeningnummer': 'Update a rekeningnummer (beta)',
            'delete-rekeningnummer': 'Delete a rekeningnummer (beta)',
            'get-vertegenwoordigingen': 'Retrieve vertegenwoordigingen (beta)',
            'get-vertegenwoordiging': 'Retrieve a vertegenwoordiging (beta)',
            'create-vertegenwoordiging': 'Create a vertegenwoordiging (beta)',
            'update-vertegenwoordiging': 'Update a vertegenwoordiging (beta)',
            'delete-vertegenwoordiging': 'Delete a vertegenwoordiging (beta)',
            uuid: 'UUID',
            naam: 'Name',
            nummer: 'Number',
            soortActor: 'Type of actor',
            indicatieActief: 'Active indicator (true/false)',
            objectId: 'Object ID',
            codeObjecttype: 'Object type code',
            codeRegister: 'Register code',
            codeSoortObjectId: 'Object ID type code',
            functie: 'Job title',
            emailadres: 'Email address',
            telefoonnummer: 'Phone number',
            faxnummer: 'Fax number',
            actorUuid: 'Actor UUID',
            klantcontactUuid: 'Klantcontact UUID',
            wasKlantcontactUuid: 'UUID of the preceding klantcontact',
            subIdentificatorVanUuid: 'UUID of the parent partij-identificator',
            rol: 'Role',
            initiator: 'Initiator (true/false)',
            organisatienaam: 'Organisation name',
            bezoekadres: 'Visiting address',
            correspondentieadres: 'Correspondence address',
            gevraagdeHandeling: 'Requested action',
            status: 'Status',
            toegewezenAanActoren: 'Assigned to actoren',
            toelichting: 'Explanation',
            afgehandeldOp: 'Handled on (ISO 8601)',
            soortPartij: 'Type of partij',
            interneNotitie: 'Internal note',
            voorkeurstaal: 'Preferred language (ISO 639-2/B)',
            indicatieGeheimhouding: 'Confidentiality indicator (true/false)',
            digitaleAdressen: 'Digital addresses',
            voorkeursDigitaalAdres: 'Preferred digital address (UUID)',
            rekeningnummers: 'Bank account numbers',
            voorkeursRekeningnummer: 'Preferred bank account number (UUID)',
            iban: 'IBAN',
            bic: 'BIC',
            vertegenwoordigendePartijUuid: 'UUID of the representing partij',
            vertegenwoordigdePartijUuid: 'UUID of the represented partij',
            soortActorTooltip: 'One of: \'medewerker\', \'geautomatiseerde_actor\' or \'organisatorische_eenheid\'.',
            soortPartijTooltip: 'One of: \'persoon\', \'organisatie\' or \'contactpersoon\'.',
            rolTooltip: 'One of: \'klant\' or \'vertegenwoordiger\'.',
            statusTooltip: 'One of: \'te_verwerken\' or \'verwerkt\'.',
            uuidListTooltip: 'Comma-separated list of UUIDs, or a reference to a process variable holding them.',
            adresObjectTooltip: 'Reference to a process variable holding an address object, for example \'pv:bezoekadres\'. Supported fields: nummeraanduidingId, straatnaam, huisnummer, huisnummertoevoeging, postcode, stad, adresregel1, adresregel2, adresregel3 and land.',
            'resource-queryDescription': 'By default this retrieves every page and stores the result as JSON in the process variable. Filter using query parameters, or pass \'page\' and \'pageSize\' yourself to retrieve exactly one page. Without a filter this can produce a very large result. This action is still in beta and may change in a future release.',
            'resource-uuidDescription': 'Retrieves a single object by its UUID and stores the full API response as JSON in the process variable. This action is still in beta and may change in a future release.',
            'resource-deleteDescription': 'Permanently deletes the object from Open Klant. This cannot be undone. This action is still in beta and may change in a future release.',
            'delete-partijDescription': 'Permanently deletes the partij from Open Klant. Note that Open Klant also deletes all of this partij\'s digital addresses and bank account numbers. This cannot be undone. This action is still in beta and may change in a future release.',
            'delete-klantcontactDescription': 'Permanently deletes the klantcontact from Open Klant. Note that Open Klant also deletes all of this klantcontact\'s betrokkenen, onderwerpobjecten and interne taken, which removes the klantcontact from the contact history. This cannot be undone. This action is still in beta and may change in a future release.',
            'delete-actorDescription': 'Permanently deletes the actor from Open Klant. Its links to klantcontacten are removed as well, but interne taken that were assigned to this actor remain, without an assigned actor. This action is still in beta and may change in a future release.',
            'create-actorDescription': 'Creates an actor: an employee, an automated actor or an organisational unit. Which actor identification fields apply depends on the type of actor: job title, email address and phone number for an employee; job title and description for an automated actor; description, email address, fax number and phone number for an organisational unit. This action is still in beta and may change in a future release.',
            'update-actorDescription': 'Updates an existing actor. Only the individual fields you fill in are sent; the remaining individual fields are left untouched. The composite fields are subject to the exceptions below. This action is still in beta and may change in a future release.',
            'update-actorPatchWarning':
                'Note: on update, Open Klant ignores every change to the actor identification (job title, email address, phone number, description and fax number). Those fields stay as they are and no error is reported. Always fill in the four object identification fields (object ID, object type code, register code and object ID type code) together: leave one empty and Open Klant clears the other three.',
            'create-actorklantcontactDescription': 'Links an existing actor to an existing klantcontact. Both must already exist; this action does not create an actor or klantcontact itself. This action is still in beta and may change in a future release.',
            'update-actorklantcontactDescription': 'Points an existing link at a different actor and/or klantcontact. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
            'create-betrokkeneDescription': 'Registers a person or organisation as being involved in an existing klantcontact. Link an existing partij through its UUID, or leave that empty and only fill in a contact name for someone who is not recorded as a partij. This action is still in beta and may change in a future release.',
            'update-betrokkeneDescription': 'Updates an existing betrokkene. Only the individual fields you fill in are sent; the remaining individual fields are left untouched. The composite fields are subject to the exceptions below. This action is still in beta and may change in a future release.',
            'update-betrokkenePatchWarning':
                'Note: always fill in the name fields (initials, first name, surname prefix and surname) together, and the visiting and correspondence addresses completely. Open Klant replaces these composite fields as a whole, so anything you leave empty is cleared.',
            'create-bijlageDescription': 'Records a reference to a document in an external register, for example a document in Open Zaak. No file is uploaded: only the document\'s identification is stored with the klantcontact. This action is still in beta and may change in a future release.',
            'update-bijlageDescription': 'Updates an existing bijlage. Only the individual fields you fill in are sent; the remaining individual fields are left untouched. The bijlage identification is subject to the exception below. This action is still in beta and may change in a future release.',
            'update-bijlagePatchWarning':
                'Note: always fill in the four bijlage identification fields (object ID, object type code, register code and object ID type code) together. Open Klant replaces this field as a whole, so leaving one empty clears the other three.',
            'create-internetaakDescription': 'Creates an interne taak as a follow-up to an existing klantcontact, for example \'call back\'. The task can be assigned to one or more actoren. This action is still in beta and may change in a future release.',
            'update-internetaakDescription': 'Updates an existing interne taak, for example to set its status to \'verwerkt\'. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
            'create-klantcontactDescription': 'Creates a klantcontact together with its betrokkene and onderwerpobject in a single call. Both are mandatory: without them the klantcontact cannot be found in the contact history. The onderwerpobject refers to what the contact was about, for example a case in Open Zaak. This action is still in beta and may change in a future release.',
            'update-klantcontactDescription': 'Updates an existing klantcontact. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
            'create-onderwerpobjectDescription': 'Links an existing klantcontact to the subject it was about, for example a case in Open Zaak. This link is what makes the klantcontact retrievable by case UUID. This action is still in beta and may change in a future release.',
            'update-onderwerpobjectDescription': 'Updates an existing onderwerpobject. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
            'create-partijDescription': 'Creates a new partij. The partij identificator (for example a BSN or KVK number) is mandatory, because \'Get Partij or create a Partij\' looks partijen up by their identificator: without one the partij cannot be found later and duplicates will be created. This action is still in beta and may change in a future release.',
            'update-partijDescription': 'Updates an existing partij. Only the individual fields you fill in are sent; the remaining individual fields are left untouched. The composite fields are subject to the exceptions below. Partij identificatoren cannot be changed here; use the partij-identificator actions for that. This action is still in beta and may change in a future release.',
            'update-partijPatchWarning':
                'Note: on update, Open Klant ignores every change to the name (initials, first name, surname prefix and surname). Those fields stay as they are and no error is reported. Always fill in the visiting and correspondence addresses completely: Open Klant replaces such an address as a whole, so any field you leave empty is cleared.',
            'create-partij-identificatorDescription': 'Records an identifying number for a partij, for example a BSN from the BRP or a KVK number from the trade register. This is what makes a partij findable by that number. This action is still in beta and may change in a future release.',
            'update-partij-identificatorDescription': 'Updates an existing partij identificator. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
            'create-rekeningnummerDescription': 'Records a bank account number (IBAN, optionally with a BIC) for a partij in Open Klant. This action is still in beta and may change in a future release.',
            'update-rekeningnummerDescription': 'Updates an existing rekeningnummer. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
            'create-vertegenwoordigingDescription': 'Records that one partij represents another, for example an authorised representative acting on behalf of a resident. Both partijen must already exist. This action is still in beta and may change in a future release.',
            'update-vertegenwoordigingDescription': 'Updates an existing vertegenwoordiging. Only the fields you fill in are sent; the remaining fields are left untouched. This action is still in beta and may change in a future release.',
        }
    }
};

export {openKlantPluginSpecification};
