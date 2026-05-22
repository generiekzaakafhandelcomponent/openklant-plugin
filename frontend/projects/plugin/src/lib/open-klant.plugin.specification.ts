import {PluginSpecification} from "@valtimo/plugin";
import {
  OpenKlantPluginConfigurationComponent
} from "./components/open-klant-plugin-configuration/open-klant-plugin-configuration.component";
import {StoreContactInfoComponent} from "./components/open-klant-store-contact-info/open-klant-store-contact-info.component";
import {OPEN_KLANT_PLUGIN_LOGO_BASE64} from "./assets/open-klant-plugin-logo";
import {
  GetContactMomentsByCaseUuidComponent
} from "./components/open-klant-get-contact-moments-by-case-uuid/open-klant-get-contact-moments-by-case-uuid.component";
import {GetContactMomentsByBsnComponent} from "./components/get-contact-moments-by-bsn/get-contact-moments-by-bsn.component";
import {RegisterKlantcontactComponent} from "./components/open-klant-register-klantcontact/open-klant-register-klantcontact.component";
import {GetOrCreatePartijComponent} from "./components/get-or-create-partij/get-or-create-partij.component";
import {SetDefaultDigitaalAdresComponent} from "./components/set-default-digitaal-adres/set-default-digitaal-adres.component";
import {
  GetContactMomentsByPartijUuidComponent
} from "./components/get-contact-moments-by-partij-uuid/get-contact-moments-by-partij-uuid.component";
import {CreateDigitaalAdresComponent} from "./components/create-digitaal-adres/create-digitaal-adres.component";
import {GetDigitaleAdressenComponent} from "./components/get-digitale-adressen/get-digitale-adressen.component";

const openKlantPluginSpecification: PluginSpecification = {
  pluginId: "openklant",
  pluginConfigurationComponent: OpenKlantPluginConfigurationComponent,
  pluginLogoBase64: OPEN_KLANT_PLUGIN_LOGO_BASE64,
  functionConfigurationComponents: {
    "get-contact-moments-by-bsn": GetContactMomentsByBsnComponent,
    "get-contact-moments-by-partij-uuid": GetContactMomentsByPartijUuidComponent,
    "get-contact-moments-by-case-uuid": GetContactMomentsByCaseUuidComponent,
    "store-contact-info": StoreContactInfoComponent,
    "get-or-create-partij": GetOrCreatePartijComponent,
    "register-klantcontact": RegisterKlantcontactComponent,
    "set-default-digitaal-adres": SetDefaultDigitaalAdresComponent,
    "create-digitaal-adres": CreateDigitaalAdresComponent,
    "get-digitale-adressen": GetDigitaleAdressenComponent
  },
  pluginTranslations: {
    nl: {
      title: "Open Klant",
      description: "Een plugin voor het ophalen en versturen van Open Klant-gegevens.",

      // Common
      resultPvName: "Naam van resultaat-procesvariabele",
      bsn: "BSN",
      partijUuid: "Partij-UUID",
      caseUuid: "Open-Zaak-zaak-UUID",
      variableFieldTooltip:
        "Dit veld kan zowel de letterlijke waarde, of het pad naar een (proces)variabele bevatten welke de waarde bevat (b.v. 'pv:/resultaat')",

      // Configuration
      configurationTitle: "Configuratie van de Open-Klantplugin",
      configurationTitleTooltip:
        "In dit onderdeel configureer je de Open-Klantplugin om eenvoudig gegevens te kunnen verzenden en ophalen.",
      klantinteractiesUrl: "Klantinteracties-URL",
      token: "Open-Klanttoken",

      // Store contact info
      "store-contact-info": "Maak Digitaal Adres (en Partij) aan",
      firstName: "Voornaam",
      inFix: "Tussenvoegsel",
      lastName: "Achternaam",
      emailAddress: "E-mailadres",

      // Get or create Partij
      "get-or-create-partij": "Haal Partij op of maak een Partij aan",

      // Get contact moments by BSN
      "get-contact-moments-by-bsn": "Contactgeschiedenis ophalen op basis van BSN",

      // Get contact moments by Partij UUID
      "get-contact-moments-by-partij-uuid": "Contactgeschiedenis ophalen op basis van Partij-UUID",

      // Get contact moments by case UUID
      "get-contact-moments-by-case-uuid": "Contactgeschiedenis ophalen op basis van Open-Zaak-UUID",

      // Get digitale adressen
      "get-digitale-adressen": "Digitale adressen ophalen",
      queryParams: "Query parameters",

      // Register contact moment
      "register-klantcontact": "Registreer nieuw klantcontact",
      objectTypeId: "Type van het object, bijvoorbeeld: 'zaak'",
      referentienummer: "Referentienummer",
      kanaal: "Communicatiekanaal",
      onderwerp: "Onderwerp",
      inhoud: "Inhoud",
      reactie: "Reactie",
      indicatieContactGelukt: "Indicatie contact gelukt",
      vertrouwelijk: "Vertrouwelijk (true/false)",
      taal: "Taal (ISO 639-2/B-formaat)",
      plaatsgevondenOp: "Plaatsgevonden op (ISO 8601)",
      metadata: "Metadata",
      voorletters: "Voorletters",
      voornaam: "Voornaam",
      voorvoegselAchternaam: "Voorvoegsel achternaam",
      achternaam: "Achternaam",
      heeftBetrokkene: "Bevat het klantcontact een betrokkene of is het anoniem?",
      "heeftBetrokkene.betrokkene": "Heeft betrokkene",
      "heeftBetrokkene.anoniem": "Is anoniem",

      // Set standaard digitaal adres
      "set-default-digitaal-adres": "Instellen van standaard digitaal adres",
      digitaalAdres: "Digitaal adres",
      soortDigitaalAdres: "Soort digitaal adres ('email'/'telefoonnummer'/'overig')",
      verificatieDatum: "Verificatiedatum (YYYY-MM-DD)",
      verificatieDatumTooltip:
        "Referentie naar de datum waarop het digitale adres is geverifieerd. Moet worden opgegeven in het formaat YYYY-MM-DD.",

      // Create digitaal adres
      "create-digitaal-adres": "Maak een digitaal adres aan",
      verstrektDoorBetrokkene: "UUID van digitaal adres verstrekt door betrokkene",
      verstrektDoorBetrokkeneTooltip:
        "Verwijzing naar de UUID van de betrokkene die het digitale adres verstrekt, bijvoorbeeld 'pv:/betrokkeneUuid'",
      verstrektDoorPartij: "UUID van digitaal adres verstrekt door partij",
      verstrektDoorPartijTooltip: "Verwijzing naar de UUID van de partij die het digitale adres verstrekt, bijvoorbeeld 'pv:/partijUuid'",
      adres: "Adres",
      isStandaardAdres: "Is standaard adres",
      isStandaardAdresTooltip:
        "Verwijzing naar een booleanwaarde die bepaalt of het digitale adres het standaard adres is (true/false). Indien dit veld leeg wordt gelaten, wordt dit automatisch op 'false' gezet.",
      omschrijving: "Omschrijving",
      referentie: "Referentie",
      referentieTooltip: "Machine-leesbare tag voor unieke identificatie van het digitaal adres.",
    },

    en: {
      title: "Open Klant",
      description: "A plugin for retrieving and sending Open Klant data.",

      // Common
      resultPvName: "Result process variable name",
      bsn: "BSN (citizen service number)",
      partijUuid: "Partij UUID",
      caseUuid: "Open Zaak case UUID",
      variableFieldTooltip:
        "This field accepts either a literal value or a path to a (process) variable containing the value (e.g. 'pv:/result')",

      // Configuration
      configurationTitle: "Open Klant plugin configuration",
      configurationTitleTooltip: "In this section, you configure the Open Klant plugin to easily send and retrieve data.",
      klantinteractiesUrl: "Klantinteracties URL",
      token: "Open Klant token",

      // Store contact info
      "store-contact-info": "Create 'Digitaal Adres' (digital address) (and Partij)",
      firstName: "First name",
      inFix: "Name infix",
      lastName: "Last name",
      emailAddress: "Email address",

      // Get or create Partij
      "get-or-create-partij": "Get Partij or create a Partij",

      // Get contact moments by BSN
      "get-contact-moments-by-bsn": "Retrieve contact history based on BSN",

      // Get contact moments by Partij UUID
      "get-contact-moments-by-partij-uuid": "Retrieve contact history based on Partij UUID",

      // Get contact moments by case UUID
      "get-contact-moments-by-case-uuid": "Retrieve contact history based on Open Zaak case UUID",

      // Register contact moment
      "register-klantcontact": "Register new klantcontact (customer contact)",
      objectTypeId: "Object type, for example: 'zaak'",
      referentienummer: "Reference number",
      kanaal: "Communication channel",
      onderwerp: "Subject",
      inhoud: "Message content",
      reactie: "Reaction",
      indicatieContactGelukt: "Successfully contacted",
      vertrouwelijk: "Confidential (true/false)",
      taal: "Language (ISO 639-2/B format)",
      plaatsgevondenOp: "Occurred on (ISO 8601)",
      metadata: "Metadata",
      voorletters: "Initials",
      voornaam: "First name",
      voorvoegselAchternaam: "Name infix",
      achternaam: "Last name",
      heeftBetrokkene: "Does the contact moment involve an individual or is it anonymous?",
      "heeftBetrokkene.betrokkene": "Has an individual",
      "heeftBetrokkene.anoniem": "Is anonymous",

      // Set default digital address
      "set-default-digitaal-adres": "Set default digital address",
      digitaalAdres: "Digital address",
      soortDigitaalAdres: "Type of digital address ('email'/'telefoonnummer'/'overig')",
      verificatieDatum: "Verification datum (YYYY-MM-DD)",
      verificatieDatumTooltip:
        "Reference to the date on which the digital address was verified. Must be provided in the format YYYY-MM-DD.",

      // Create digital address
      "create-digitaal-adres": "Create a digital address",
      verstrektDoorBetrokkene: "UUID of digital address provided by individual",
      verstrektDoorBetrokkeneTooltip:
        "Reference to the UUID of the individual providing the digital address, for example 'pv:/betrokkeneUuid'",
      verstrektDoorPartij: "UUID of digital address provided by organization",
      verstrektDoorPartijTooltip: "Reference to the UUID of the organization providing the digital address, for example 'pv:/partijUuid'",
      adres: "Address",
      isStandaardAdres: "Is default address",
      isStandaardAdresTooltip:
        "Reference to a boolean value indicating whether the digital address is the default address (true/false). If this field is left empty, it will automatically be set to 'false'.",
      omschrijving: "Description",
      referentie: "Reference",
      referentieTooltip: "Machine-readable tag for unique identification of the digital address.",
    },
  },
};

export {openKlantPluginSpecification};
