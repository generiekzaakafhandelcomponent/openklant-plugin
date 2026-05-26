export interface GetDigitaleAdressenConfig {
    resultPvName: string;
    queryParams: FormIOQueryParam[];
}

interface FormIOQueryParam {
    key: string;
    value: string;
}
