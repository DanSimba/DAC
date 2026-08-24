export interface ViacepResponse {
    cep:         string;
    logradouro:  string;
    complemento: string;
    bairro:      string;
    localidade:  string;
    uf:          string;
    estado:      string;
    erro?:       boolean;
}
