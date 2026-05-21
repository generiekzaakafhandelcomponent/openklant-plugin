package com.ritense.valtimoplugins.openklant.model

import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.SoortDigitaalAdres
import com.ritense.valtimoplugins.openklant.dto.UuidReference
import mu.KotlinLogging

data class AdresInformation(
    val partijReference: UuidReference,
    val adres: String,
    val soortDigitaalAdres: SoortDigitaalAdres,
    val referentie: String,
    val verificatieDatum: String,
) {
    companion object {
        private val logger = KotlinLogging.logger { }

        fun fromActionProperties(
            partijUuid: String,
            adres: String,
            soortDigitaalAdres: String,
            referentie: String,
            verificatieDatum: String,
        ) = AdresInformation(
            partijReference = UuidReference.fromString(partijUuid.trim()),
            adres = adres.trim(),
            soortDigitaalAdres =
                SoortDigitaalAdres.entries
                    .firstOrNull { it.name.equals(soortDigitaalAdres.trim(), ignoreCase = true) }
                    ?: SoortDigitaalAdres.OVERIG
                        .also {
                            logger.warn { "Unknown soortDigitaalAdres: {$soortDigitaalAdres}, using default: {$it}" }
                        },
            referentie = referentie.trim(),
            verificatieDatum = verificatieDatum.trim(),
        )

        fun toDigitaalAdresCreationRequest(
            adresInformation: AdresInformation,
            isStandaardAdres: Boolean = true
        ): DigitaalAdresCreationRequest {
            return DigitaalAdresCreationRequest(
                verstrektDoorPartij = UuidReference(adresInformation.partijReference.uuid),
                adres = adresInformation.adres,
                soortDigitaalAdres = adresInformation.soortDigitaalAdres,
                isStandaardAdres = isStandaardAdres,
                referentie = adresInformation.referentie,
                verificatieDatum = adresInformation.verificatieDatum
            )
        }
    }
}
