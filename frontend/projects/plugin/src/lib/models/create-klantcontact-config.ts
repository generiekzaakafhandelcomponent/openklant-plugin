export interface CreateKlantcontactConfig {
    kanaal: string;
    onderwerp: string;
    taal: string;
    vertrouwelijk?: string;
    referentienummer?: string;
    inhoud?: string;
    reactie?: string;
    indicatieContactGelukt?: string;
    plaatsgevondenOp?: string;
    metadata?: string;
    rol: string;
    initiator?: string;
    partijUuid?: string;
    voorletters?: string;
    voornaam?: string;
    voorvoegselAchternaam?: string;
    achternaam?: string;
    organisatienaam?: string;
    objectId: string;
    codeObjecttype: string;
    codeRegister: string;
    codeSoortObjectId: string;
    resultPvName: string;
    betrokkeneResultPvName?: string;
    onderwerpobjectResultPvName?: string;
}
