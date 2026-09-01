export interface CreatePartijConfig {
    soortPartij: string;
    objectId: string;
    codeObjecttype: string;
    codeRegister: string;
    codeSoortObjectId: string;
    nummer?: string;
    interneNotitie?: string;
    voorkeurstaal?: string;
    indicatieActief?: string;
    indicatieGeheimhouding?: string;
    digitaleAdressen?: string;
    voorkeursDigitaalAdres?: string;
    rekeningnummers?: string;
    voorkeursRekeningnummer?: string;
    bezoekadres?: string;
    correspondentieadres?: string;
    voorletters?: string;
    voornaam?: string;
    voorvoegselAchternaam?: string;
    achternaam?: string;
    resultPvName: string;
}
