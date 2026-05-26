export interface GetDigitaleAdressenConfig {
    resultPvName: string;
    queryParams: KeyValueQueryParam[];
}

interface KeyValueQueryParam {
    key: string;
    value: string;
}
