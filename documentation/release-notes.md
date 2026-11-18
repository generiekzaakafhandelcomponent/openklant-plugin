# Release notes

Overzicht van wijzigingen per versie van de Open Klant-plugin.

## Nog niet uitgebracht

- **Zoekwaarden worden volledig gecodeerd.** Filterwaarden in `queryParams` (en de filters voor klantcontacten) gaan
  als URI-variabele naar Open Klant. Een `+` kwam eerder aan als spatie, een `&` begon een nieuwe parameter en een `{`
  gaf een fout. Let op: wie een waarde zelf al codeerde (bijvoorbeeld `%2B` voor `+`), moet dat weghalen; anders wordt
  de waarde dubbel gecodeerd. Lege filters voor klantcontacten worden niet meer meegestuurd.
- **Alle pagina's voor digitale adressen en klantcontacten.** `get-digitale-adressen` met een filter en het ophalen
  van klantcontacten (onder meer de contactgeschiedenis) gaven alleen de eerste 100 resultaten. Nu komen alle pagina's
  terug, zoals bij de andere lijstacties. `get-digitale-adressen` zonder filter geeft nog steeds alleen de eerste
  pagina.
- **Gewijzigd gedrag bij meer dan 100 pagina's.** Alle lijstacties die alle pagina's ophalen, geven nu een fout als er
  meer dan 100 pagina's zijn. Voorheen kapte de plugin de lijst daar stil af (alleen een melding in het log), en
  rekende een proces verder met een onvolledige lijst. Filter verder, of haal met `page` één pagina op. Een resultaat
  dat tijdens het ophalen naar de volgende pagina verschuift (doordat er intussen iets is aangemaakt), komt nu één keer
  terug in plaats van twee keer.

## 2.8.2

Bugfix: updateDigitaleAdressenForPartij was overbodig & de patchPartij call resulteerde in een Bad Request vanuit 
OpenKlant. Foutieve patchPartij overload is weggehaald, updateDigitaleAdressenForPartij is weggehaald. De functionele 
werking van createAndStoreNewPartij / updateExistingPartij is hetzelfde gebleven.

## 2.8.1

Ondersteuning voor Valtimo 13.48.0.

## 2.8.0

Alle resources van de klantinteracties-API zijn nu als plugin-actie beschikbaar. Waar de plugin eerder 10 acties had,
zijn dat nu 67.

## 2.7.0

De Open Klant-plugin vereist geen ENV-variabelen meer. De klantinteracties-URL en het token worden nu runtime uit de
plugin-configuratie gehaald.

## 2.6.2

Fix voor 'Registreer nieuw klantcontact': de instelling 'Heeft betrokkene' werd niet opgeslagen in de
proceskoppeling, waardoor de service task altijd faalde.

## 2.6.1

Ondersteuning voor Valtimo 13.42.0.

## 2.6.0
Voor aanmaken van een klantcontact, character limiet voor "reactie" en de "metadata" values verhoogd naar 4000 en 255 tekens. 

## 2.5.0
Acties toegevoegd om digitale adressen aan te maken, op te halen en bij te werken, met aanvullende documentatie.

## 2.4.0
Verbeteringen aan het aanmaken van partijen en aan het procesverloop bij klantcontact.

## 2.3.3
Vertaling- en configuratieschermfix voor Open Klant v2.3.3.

## 2.3.1
Hotfix voor de authenticatie-header bij REST-calls op Valtimo v13.

## 2.0.0
Nieuwe release op Valtimo v13, gebaseerd op de v12-versie van de plugin.

## 1.1.2
Configuratiemetadata toegevoegd en Maven-naam en -beschrijving rechtgezet.

## 1.0.0
Eerste publieke release: klantgegevens beheren in Open Klant.
