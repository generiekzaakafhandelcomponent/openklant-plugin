export interface CreateInterneTaakConfig {
    gevraagdeHandeling: string;
    klantcontactUuid: string;
    status: string;
    nummer?: string;
    referentienummer?: string;
    toegewezenAanActoren?: string;
    toelichting?: string;
    afgehandeldOp?: string;
    resultPvName: string;
}
