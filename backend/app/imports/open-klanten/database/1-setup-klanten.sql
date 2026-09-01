/*
 * Copyright 2026 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

INSERT INTO accounts_user
VALUES (1, 'pbkdf2_sha256$150000$804RI2AKro7g$4bToWwzuZO7OtruzIi6VRshCwshQVgxFTwF7ZOYowRg=',
        '2021-09-22 21:28:40.15254+00', true, 'admin', '', '', 'admin@exmaple.com', true, true,
        '2021-09-22 14:13:43.383088+00');

INSERT INTO token_tokenauth (token, contact_person, email, organization, last_modified, created, application,
                             administration, identifier)
VALUES ('5bf819967d9fdd00d326ce20774768b4182285e5', 'ritense', 'support@ritense.com', 'Ritense',
        '2025-07-18 14:54:21.492 +0200', '2025-07-18 14:54:21.492 +0200', 'GZAC', '', 'valtimo_client');

INSERT INTO contactgegevens_persoon(adres_adresregel1, adres_adresregel2, adres_adresregel3, adres_land, uuid,
                                    geboortedatum, geslachtsnaam, geslacht, voorvoegsel, voornamen,
                                    adres_nummeraanduiding_id, adres_huisnummer, adres_huisnummertoevoeging,
                                    adres_postcode, adres_stad, adres_straatnaam)
VALUES ('', '', '', 'NL', '5200dd6f-95c3-4448-8e20-0f7afe498f48', '1870-01-01', 'Klaver', 'm', 'van', 'Peter', '', '1',
        'A', '1111 AA', 'Amsterdam', 'Pettenflet');

BEGIN;

-- actoren
INSERT INTO klantinteracties_actor (id, actoridentificator_object_id, uuid, naam, soort_actor, indicatie_actief, actoridentificator_code_objecttype, actoridentificator_code_register, actoridentificator_code_soort_object_id) VALUES (1, 'medewerker-001', '9d5afe2c-8574-444c-afc1-8c6f1c6ba504', 'Sanne Bakker', 'medewerker', true, 'medewerker', 'msc', 'personeelsnummer');
INSERT INTO klantinteracties_actor (id, actoridentificator_object_id, uuid, naam, soort_actor, indicatie_actief, actoridentificator_code_objecttype, actoridentificator_code_register, actoridentificator_code_soort_object_id) VALUES (2, 'afd-burgerzaken', 'ec1441b1-af40-4356-aff7-87ad3e30ea77', 'Afdeling Burgerzaken', 'organisatorische_eenheid', true, 'organisatorische_eenheid', 'obj', 'afdelingscode');
INSERT INTO klantinteracties_actor (id, actoridentificator_object_id, uuid, naam, soort_actor, indicatie_actief, actoridentificator_code_objecttype, actoridentificator_code_register, actoridentificator_code_soort_object_id) VALUES (3, 'gzac-bot', '8554a11b-e563-4c97-9292-e0248ddd48ec', 'GZAC Automatische Verwerker', 'geautomatiseerde_actor', true, 'applicatie', 'obj', 'applicatieid');

-- categorieen
INSERT INTO klantinteracties_categorie (id, uuid, naam) VALUES (1, '4a6582a2-b16b-4daf-93a3-743bfe7dce2e', 'Ondernemer');
INSERT INTO klantinteracties_categorie (id, uuid, naam) VALUES (2, '3ddf802c-97a7-494a-b76a-d0670a79bd8f', 'Woningeigenaar');
INSERT INTO klantinteracties_categorie (id, uuid, naam) VALUES (3, 'f5b30498-48ae-421c-b8ab-05b00c05a339', 'Vip');

-- partijen
INSERT INTO klantinteracties_partij (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, uuid, nummer, interne_notitie, soort_partij, indicatie_geheimhouding, voorkeurstaal, indicatie_actief, voorkeurs_digitaal_adres_id, voorkeurs_rekeningnummer_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (1, '', '', '', '', 'NL', '', '', '', '', '', 'b39c60c0-6575-42bf-b9e2-5fb4f4c56f3f', '0000000001', '', 'persoon', NULL, '', false, NULL, NULL, NULL, '', '', '', '', NULL, '', '', '', '');
INSERT INTO klantinteracties_partij (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, uuid, nummer, interne_notitie, soort_partij, indicatie_geheimhouding, voorkeurstaal, indicatie_actief, voorkeurs_digitaal_adres_id, voorkeurs_rekeningnummer_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (2, '', 'Marktplein 12', '1234 AB Testdorp', '', 'NL', '', 'Postbus 99', '1234 ZZ Testdorp', '', 'NL', '500da7fc-b49a-4b2f-b748-22b73fcd958b', NULL, 'Testorganisatie voor de openklant-plugin', 'organisatie', false, 'nld', true, 3, 2, 12, '', '1234 AB', 'Testdorp', 'Marktplein', 99, '', '1234 ZZ', 'Testdorp', 'Postbus');
INSERT INTO klantinteracties_partij (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, uuid, nummer, interne_notitie, soort_partij, indicatie_geheimhouding, voorkeurstaal, indicatie_actief, voorkeurs_digitaal_adres_id, voorkeurs_rekeningnummer_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (3, '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', '0f6f7a88-6e5d-4e22-aecc-6fb12b69f71e', NULL, '', 'persoon', false, 'nld', true, 1, 1, 5, '', '1234 AC', 'Testdorp', 'Kerkstraat', 5, '', '1234 AC', 'Testdorp', 'Kerkstraat');
INSERT INTO klantinteracties_partij (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, uuid, nummer, interne_notitie, soort_partij, indicatie_geheimhouding, voorkeurstaal, indicatie_actief, voorkeurs_digitaal_adres_id, voorkeurs_rekeningnummer_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (4, '', 'Molenweg 42', '5678 BB Anderdorp', '', 'NL', '', 'Molenweg 42', '5678 BB Anderdorp', '', 'NL', 'e62b6e2e-8846-4bde-a2e7-7b613e7a19a7', NULL, 'Inactieve partij met geheimhouding - edge case', 'persoon', true, 'eng', false, NULL, NULL, 42, '', '5678 BB', 'Anderdorp', 'Molenweg', 42, '', '5678 BB', 'Anderdorp', 'Molenweg');
INSERT INTO klantinteracties_partij (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, uuid, nummer, interne_notitie, soort_partij, indicatie_geheimhouding, voorkeurstaal, indicatie_actief, voorkeurs_digitaal_adres_id, voorkeurs_rekeningnummer_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (5, '', 'Marktplein 12', '1234 AB Testdorp', '', 'NL', '', 'Marktplein 12', '1234 AB Testdorp', '', 'NL', '66344354-ac31-4a00-b7a7-c0919afdd1f1', NULL, '', 'contactpersoon', false, 'nld', true, NULL, NULL, 12, '', '1234 AB', 'Testdorp', 'Marktplein', 12, '', '1234 AB', 'Testdorp', 'Marktplein');

-- partijen: soort-specifieke gegevens
INSERT INTO klantinteracties_persoon (id, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, partij_id) VALUES (1, '', 'Peter', 'van', 'Klaver', 1);
INSERT INTO klantinteracties_persoon (id, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, partij_id) VALUES (2, 'A.M.', 'Anne', 'de', 'Vries', 3);
INSERT INTO klantinteracties_persoon (id, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, partij_id) VALUES (3, 'J.', 'Jan', '', 'Jansen', 4);
INSERT INTO klantinteracties_organisatie (id, naam, partij_id) VALUES (1, 'Bakkerij De Gouden Korst B.V.', 2);
INSERT INTO klantinteracties_contactpersoon (id, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, partij_id, uuid, werkte_voor_partij_id) VALUES (1, 'K.', 'Karim', 'el', 'Amrani', 5, '27266773-064e-4095-a39b-57c15e6287de', 2);

-- partij-identificatoren
INSERT INTO klantinteracties_partijidentificator (id, uuid, andere_partij_identificator, partij_identificator_code_objecttype, partij_identificator_code_soort_object_id, partij_identificator_object_id, partij_identificator_code_register, partij_id, sub_identificator_van_id) VALUES (1, 'ac2a3e9d-d3bb-483b-8085-b2aeecb0b169', '', 'natuurlijk_persoon', 'bsn', '569312863', 'brp', 1, NULL);
INSERT INTO klantinteracties_partijidentificator (id, uuid, andere_partij_identificator, partij_identificator_code_objecttype, partij_identificator_code_soort_object_id, partij_identificator_object_id, partij_identificator_code_register, partij_id, sub_identificator_van_id) VALUES (2, 'fef3a432-1686-41a7-aafb-572dc0b5d3ae', '', 'natuurlijk_persoon', 'bsn', '123456782', 'brp', 3, NULL);
INSERT INTO klantinteracties_partijidentificator (id, uuid, andere_partij_identificator, partij_identificator_code_objecttype, partij_identificator_code_soort_object_id, partij_identificator_object_id, partij_identificator_code_register, partij_id, sub_identificator_van_id) VALUES (3, 'a00d29ab-937a-4c19-af07-b5e473e3c286', '', 'natuurlijk_persoon', 'bsn', '111222333', 'brp', 4, NULL);
INSERT INTO klantinteracties_partijidentificator (id, uuid, andere_partij_identificator, partij_identificator_code_objecttype, partij_identificator_code_soort_object_id, partij_identificator_object_id, partij_identificator_code_register, partij_id, sub_identificator_van_id) VALUES (4, '0da7adc9-292e-4986-983f-a7de774f7d71', '', 'niet_natuurlijk_persoon', 'kvk_nummer', '12345678', 'hr', 2, NULL);
INSERT INTO klantinteracties_partijidentificator (id, uuid, andere_partij_identificator, partij_identificator_code_objecttype, partij_identificator_code_soort_object_id, partij_identificator_object_id, partij_identificator_code_register, partij_id, sub_identificator_van_id) VALUES (5, 'a95739f0-41fb-456c-af22-9be9148960af', '', 'vestiging', 'vestigingsnummer', '123456789012', 'hr', 2, 4);

-- rekeningnummers
INSERT INTO klantinteracties_rekeningnummer (id, uuid, iban, bic, partij_id) VALUES (1, '1eabc21b-acde-49a6-b5e2-b95f47f0a732', 'NL91ABNA0417164300', 'ABNANL2A', 3);
INSERT INTO klantinteracties_rekeningnummer (id, uuid, iban, bic, partij_id) VALUES (2, '06228782-1e22-485d-9d36-c7de8b59de25', 'NL39RABO0300065264', 'RABONL2U', 2);

-- digitale adressen
INSERT INTO klantinteracties_digitaaladres (id, uuid, soort_digitaal_adres, adres, omschrijving, betrokkene_id, partij_id, is_standaard_adres, referentie, verificatie_datum) VALUES (1, 'a63af088-c58c-4e57-b42d-681f68f613b3', 'email', 'anne.devries@example.nl', 'Privé e-mail', NULL, 3, true, 'anne-mail-1', '2026-01-15');
INSERT INTO klantinteracties_digitaaladres (id, uuid, soort_digitaal_adres, adres, omschrijving, betrokkene_id, partij_id, is_standaard_adres, referentie, verificatie_datum) VALUES (2, '8cb64fe3-4d7a-48e3-ad2a-16e2705d9885', 'telefoonnummer', '0612345678', 'Mobiel', NULL, 3, false, 'anne-tel-1', NULL);
INSERT INTO klantinteracties_digitaaladres (id, uuid, soort_digitaal_adres, adres, omschrijving, betrokkene_id, partij_id, is_standaard_adres, referentie, verificatie_datum) VALUES (3, '4a8a55b7-d7e0-4aff-a4c0-0281cf460398', 'email', 'info@goudenkorst.nl', 'Algemeen', NULL, 2, true, 'org-mail-1', NULL);
INSERT INTO klantinteracties_digitaaladres (id, uuid, soort_digitaal_adres, adres, omschrijving, betrokkene_id, partij_id, is_standaard_adres, referentie, verificatie_datum) VALUES (4, '2439eab4-4c7b-4455-9daa-6f1189f04974', 'overig', 'https://example.nl/berichtenbox/jan', 'Berichtenbox', NULL, 4, true, 'jan-overig-1', NULL);
INSERT INTO klantinteracties_digitaaladres (id, uuid, soort_digitaal_adres, adres, omschrijving, betrokkene_id, partij_id, is_standaard_adres, referentie, verificatie_datum) VALUES (5, '04d2d871-ef5f-4c17-8dc4-1691ae296ded', 'telefoonnummer', '0201234567', 'Werk', NULL, 5, true, 'karim-tel-1', NULL);
INSERT INTO klantinteracties_digitaaladres (id, uuid, soort_digitaal_adres, adres, omschrijving, betrokkene_id, partij_id, is_standaard_adres, referentie, verificatie_datum) VALUES (6, 'fe1b9f1e-3214-49f6-b2d6-bdcf5c489985', 'email', 'pieter.post@example.nl', 'Opgegeven aan de balie', 4, NULL, false, 'pieter-mail-1', NULL);

-- categorie-relaties
INSERT INTO klantinteracties_categorierelatie (id, uuid, begin_datum, eind_datum, categorie_id, partij_id) VALUES (1, '6c93da97-ca8f-4b7c-b6da-b3e64f409b72', '2026-01-01', '2026-12-31', 1, 2);
INSERT INTO klantinteracties_categorierelatie (id, uuid, begin_datum, eind_datum, categorie_id, partij_id) VALUES (2, '826e5b2e-ac10-48d7-8f0b-57fbcd2650c3', '2025-06-01', NULL, 2, 3);
INSERT INTO klantinteracties_categorierelatie (id, uuid, begin_datum, eind_datum, categorie_id, partij_id) VALUES (3, '86db354a-f3b4-4279-93ee-b75f289afd7b', '2026-03-01', NULL, 3, 3);

-- klantcontacten
INSERT INTO klantinteracties_klantcontact (id, uuid, nummer, kanaal, onderwerp, inhoud, indicatie_contact_gelukt, taal, vertrouwelijk, plaatsgevonden_op, metadata, referentienummer, reactie) VALUES (1, '18ac5733-ac6b-4d4a-860e-0177668784d7', NULL, 'telefoon', 'Vraag over status aanvraag', 'Mevrouw De Vries vraagt naar de status van haar aanvraag.', true, 'nld', false, '2026-04-01 09:30:00+00', '{"bron": "telefonie-koppeling", "duur_seconden": "245"}', NULL, 'Toegezegd dat een medewerker binnen 5 werkdagen terugbelt.');
INSERT INTO klantinteracties_klantcontact (id, uuid, nummer, kanaal, onderwerp, inhoud, indicatie_contact_gelukt, taal, vertrouwelijk, plaatsgevonden_op, metadata, referentienummer, reactie) VALUES (2, 'e4197cee-f621-4ca4-9277-22a4a78e29bf', NULL, 'email', 'Bezwaar tegen aanslag', 'Per e-mail bezwaar ontvangen met bijlage.', true, 'nld', true, '2026-04-05 14:15:00+00', '{"bron": "mailbox", "message_id": "<abc123@example.nl>"}', NULL, '');
INSERT INTO klantinteracties_klantcontact (id, uuid, nummer, kanaal, onderwerp, inhoud, indicatie_contact_gelukt, taal, vertrouwelijk, plaatsgevonden_op, metadata, referentienummer, reactie) VALUES (3, '79cb3ef1-4cb0-4b03-bf0a-9a8f30f512a6', NULL, 'balie', 'Melding openbare ruimte', 'Melding van kapotte lantaarnpaal aan de Molenweg.', true, 'nld', false, '2026-04-10 11:00:00+00', '{}', NULL, 'Melding doorgezet naar de buitendienst.');
INSERT INTO klantinteracties_klantcontact (id, uuid, nummer, kanaal, onderwerp, inhoud, indicatie_contact_gelukt, taal, vertrouwelijk, plaatsgevonden_op, metadata, referentienummer, reactie) VALUES (4, 'dc120115-ae2d-4e95-9b78-77bc58bc7645', NULL, 'telefoon', 'Terugbelverzoek', 'Poging tot terugbellen, niet opgenomen.', false, 'nld', false, '2026-04-11 08:05:00+00', '{}', NULL, '');
INSERT INTO klantinteracties_klantcontact (id, uuid, nummer, kanaal, onderwerp, inhoud, indicatie_contact_gelukt, taal, vertrouwelijk, plaatsgevonden_op, metadata, referentienummer, reactie) VALUES (5, '8bbc8057-3b23-49a8-b12d-5f796521aabf', NULL, 'contactformulier', 'Vraag via contactformulier', 'Aangemaakt via het maak-klantcontact endpoint.', true, 'nld', false, '2026-04-12 13:45:00+00', '{"bron": "maak-klantcontact"}', NULL, '');

-- betrokkenen
INSERT INTO klantinteracties_betrokkene (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, uuid, rol, organisatienaam, initiator, klantcontact_id, partij_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (1, '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', 'A.M.', 'Anne', 'de', 'Vries', 'c652f91c-3283-4485-87e5-67978a8ecd49', 'klant', '', true, 1, 3, 5, '', '1234 AC', 'Testdorp', 'Kerkstraat', 5, '', '1234 AC', 'Testdorp', 'Kerkstraat');
INSERT INTO klantinteracties_betrokkene (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, uuid, rol, organisatienaam, initiator, klantcontact_id, partij_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (2, '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', 'A.M.', 'Anne', 'de', 'Vries', 'acc4f331-5f13-4d70-af20-f35e7f952d3b', 'klant', '', true, 2, 3, 5, '', '1234 AC', 'Testdorp', 'Kerkstraat', 5, '', '1234 AC', 'Testdorp', 'Kerkstraat');
INSERT INTO klantinteracties_betrokkene (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, uuid, rol, organisatienaam, initiator, klantcontact_id, partij_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (3, '', 'Marktplein 12', '1234 AB Testdorp', '', 'NL', '', 'Marktplein 12', '1234 AB Testdorp', '', 'NL', 'K.', 'Karim', 'el', 'Amrani', '365aae2b-6fbd-4575-a15e-beae53f13648', 'vertegenwoordiger', 'Bakkerij De Gouden Korst B.V.', false, 2, 5, 12, '', '1234 AB', 'Testdorp', 'Marktplein', 12, '', '1234 AB', 'Testdorp', 'Marktplein');
INSERT INTO klantinteracties_betrokkene (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, uuid, rol, organisatienaam, initiator, klantcontact_id, partij_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (4, '', 'Molenweg 42', '5678 BB Anderdorp', '', 'NL', '', 'Molenweg 42', '5678 BB Anderdorp', '', 'NL', 'P.', 'Pieter', '', 'Post', 'd56b1d0c-2271-489e-bc82-9da9433289ca', 'klant', '', true, 3, NULL, 42, '', '5678 BB', 'Anderdorp', 'Molenweg', 42, '', '5678 BB', 'Anderdorp', 'Molenweg');
INSERT INTO klantinteracties_betrokkene (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, uuid, rol, organisatienaam, initiator, klantcontact_id, partij_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (5, '', 'Molenweg 42', '5678 BB Anderdorp', '', 'NL', '', 'Molenweg 42', '5678 BB Anderdorp', '', 'NL', 'J.', 'Jan', '', 'Jansen', '8520bb87-9d48-4d45-bf12-c25644e7396b', 'klant', '', true, 4, 4, 42, '', '5678 BB', 'Anderdorp', 'Molenweg', 42, '', '5678 BB', 'Anderdorp', 'Molenweg');
INSERT INTO klantinteracties_betrokkene (id, bezoekadres_nummeraanduiding_id, bezoekadres_adresregel1, bezoekadres_adresregel2, bezoekadres_adresregel3, bezoekadres_land, correspondentieadres_nummeraanduiding_id, correspondentieadres_adresregel1, correspondentieadres_adresregel2, correspondentieadres_adresregel3, correspondentieadres_land, contactnaam_voorletters, contactnaam_voornaam, contactnaam_voorvoegsel_achternaam, contactnaam_achternaam, uuid, rol, organisatienaam, initiator, klantcontact_id, partij_id, bezoekadres_huisnummer, bezoekadres_huisnummertoevoeging, bezoekadres_postcode, bezoekadres_stad, bezoekadres_straatnaam, correspondentieadres_huisnummer, correspondentieadres_huisnummertoevoeging, correspondentieadres_postcode, correspondentieadres_stad, correspondentieadres_straatnaam) VALUES (6, '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', '', 'Kerkstraat 5', '1234 AC Testdorp', '', 'NL', 'A.M.', 'Anne', 'de', 'Vries', '60f0d813-7299-40c7-8a12-39e87eaef66c', 'klant', '', true, 5, 3, 5, '', '1234 AC', 'Testdorp', 'Kerkstraat', 5, '', '1234 AC', 'Testdorp', 'Kerkstraat');

-- actorklantcontacten
INSERT INTO klantinteracties_actorklantcontact (id, uuid, actor_id, klantcontact_id) VALUES (1, '22cc532d-0676-4144-9166-444737bbe266', 1, 1);
INSERT INTO klantinteracties_actorklantcontact (id, uuid, actor_id, klantcontact_id) VALUES (2, '815c84c9-f25c-4ee2-8dfd-54e1e0370e81', 2, 2);
INSERT INTO klantinteracties_actorklantcontact (id, uuid, actor_id, klantcontact_id) VALUES (3, '6d29d1ba-2611-4788-b1e9-470628c2db8d', 3, 3);

-- bijlagen
INSERT INTO klantinteracties_bijlage (id, bijlageidentificator_object_id, uuid, klantcontact_id, bijlageidentificator_code_objecttype, bijlageidentificator_code_register, bijlageidentificator_code_soort_object_id) VALUES (1, '3d8e14a7-9026-4e84-ad71-3a4f2b2d5c63', '2d9e7ac3-79e2-4273-a34b-01c2e2aa87fa', 2, 'enkelvoudiginformatieobject', 'drc', 'uuid');
INSERT INTO klantinteracties_bijlage (id, bijlageidentificator_object_id, uuid, klantcontact_id, bijlageidentificator_code_objecttype, bijlageidentificator_code_register, bijlageidentificator_code_soort_object_id) VALUES (2, 'foto-lantaarnpaal-001', 'a667bf9a-ea75-4c55-98fb-79f14d786e63', 3, 'enkelvoudiginformatieobject', 'drc', 'documentnummer');

-- onderwerpobjecten
INSERT INTO klantinteracties_onderwerpobject (id, onderwerpobjectidentificator_object_id, uuid, klantcontact_id, was_klantcontact_id, onderwerpobjectidentificator_code_objecttype, onderwerpobjectidentificator_code_register, onderwerpobjectidentificator_code_soort_object_id) VALUES (1, '1b6cf2b5-6f04-4c62-8b5f-1e2d0f0b3a41', '495acd77-c581-41fd-bd39-5e43e5c60347', 1, NULL, 'zaak', 'openzaak', 'uuid');
INSERT INTO klantinteracties_onderwerpobject (id, onderwerpobjectidentificator_object_id, uuid, klantcontact_id, was_klantcontact_id, onderwerpobjectidentificator_code_objecttype, onderwerpobjectidentificator_code_register, onderwerpobjectidentificator_code_soort_object_id) VALUES (2, '1b6cf2b5-6f04-4c62-8b5f-1e2d0f0b3a41', 'bdc02c8b-9a66-4067-93f7-753e8b808f10', 2, NULL, 'zaak', 'openzaak', 'uuid');
INSERT INTO klantinteracties_onderwerpobject (id, onderwerpobjectidentificator_object_id, uuid, klantcontact_id, was_klantcontact_id, onderwerpobjectidentificator_code_objecttype, onderwerpobjectidentificator_code_register, onderwerpobjectidentificator_code_soort_object_id) VALUES (3, '2c7d0396-8f15-4d73-9c60-2f3e1a1c4b52', 'c417d501-087a-4f4f-b3b4-f1a511ac2441', 3, NULL, 'zaak', 'openzaak', 'uuid');
INSERT INTO klantinteracties_onderwerpobject (id, onderwerpobjectidentificator_object_id, uuid, klantcontact_id, was_klantcontact_id, onderwerpobjectidentificator_code_objecttype, onderwerpobjectidentificator_code_register, onderwerpobjectidentificator_code_soort_object_id) VALUES (4, 'PRODUCT-0001', 'fa48b9f4-27f6-4b90-9e55-c3a0bf1329b4', 3, NULL, 'product', 'obj', 'productnummer');
INSERT INTO klantinteracties_onderwerpobject (id, onderwerpobjectidentificator_object_id, uuid, klantcontact_id, was_klantcontact_id, onderwerpobjectidentificator_code_objecttype, onderwerpobjectidentificator_code_register, onderwerpobjectidentificator_code_soort_object_id) VALUES (5, '2c7d0396-8f15-4d73-9c60-2f3e1a1c4b52', '0c3670d2-0656-4de8-8272-89a772a69fce', 5, NULL, 'zaak', 'openzaak', 'uuid');

-- internetaken
INSERT INTO klantinteracties_internetaak (id, uuid, nummer, gevraagde_handeling, toelichting, status, toegewezen_op, klantcontact_id, afgehandeld_op, referentienummer) VALUES (1, 'da6210d2-eecd-4690-a6de-661af0c9595c', NULL, 'Terugbellen over status aanvraag', 'Klant verwacht binnen 5 werkdagen een reactie.', 'te_verwerken', '2026-09-01 13:45:11.461483+00', 1, NULL, NULL);
INSERT INTO klantinteracties_internetaak (id, uuid, nummer, gevraagde_handeling, toelichting, status, toegewezen_op, klantcontact_id, afgehandeld_op, referentienummer) VALUES (2, '5c791c06-8278-4b56-b167-4f6bb4dc4522', NULL, 'Bezwaar in behandeling nemen', 'Bezwaarschrift met bijlage ontvangen.', 'verwerkt', '2026-09-01 13:45:11.497607+00', 2, '2026-04-08 16:00:00+00', '2026000002');
INSERT INTO klantinteracties_internetaak (id, uuid, nummer, gevraagde_handeling, toelichting, status, toegewezen_op, klantcontact_id, afgehandeld_op, referentienummer) VALUES (3, '52344b74-c8a4-4b99-b605-eb56c9bed3f2', NULL, 'Lantaarnpaal laten repareren', 'Automatisch doorgezet naar de buitendienst.', 'te_verwerken', '2026-09-01 13:45:11.528847+00', 3, NULL, NULL);
INSERT INTO klantinteracties_internetakenactorenthoughmodel (id, actor_id, internetaak_id) VALUES (1, 1, 1);
INSERT INTO klantinteracties_internetakenactorenthoughmodel (id, actor_id, internetaak_id) VALUES (2, 2, 2);
INSERT INTO klantinteracties_internetakenactorenthoughmodel (id, actor_id, internetaak_id) VALUES (3, 3, 3);

-- vertegenwoordigingen
INSERT INTO klantinteracties_vertegenwoordigden (id, uuid, vertegenwoordigde_partij_id, vertegenwoordigende_partij_id) VALUES (1, '57de8464-7930-48b7-b4ac-c73e2b14afb6', 2, 5);
INSERT INTO klantinteracties_vertegenwoordigden (id, uuid, vertegenwoordigde_partij_id, vertegenwoordigende_partij_id) VALUES (2, 'bc05910a-c382-4359-9d4c-09745dbdfada', 4, 3);

-- Zet de sequences achter de hierboven ingevoegde id's
DO
$$
    DECLARE
        tbl text;
        seq text;
    BEGIN
        FOR tbl IN SELECT tablename
                   FROM pg_tables
                   WHERE schemaname = 'public'
                     AND tablename LIKE 'klantinteracties_%'
            LOOP
                seq := pg_get_serial_sequence(tbl, 'id');
                CONTINUE WHEN seq IS NULL;
                EXECUTE format(
                        'SELECT setval(%L, coalesce(m, 1), m IS NOT NULL) FROM (SELECT max(id) AS m FROM %I) s',
                        seq, tbl);
            END LOOP;
    END
$$;

COMMIT;
