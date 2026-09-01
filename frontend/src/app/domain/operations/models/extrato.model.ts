import { OperationModel } from "./operation.model";
import { TransferenceModel } from "./transference.model";

export interface ExtratoModel {

    type:'dep'|'sac'|'transf'; //PASSA O TIPO PRA FACILITAR NO CARD
    instance: OperationModel|TransferenceModel;

    id: number; //O ID SERÁ A DATA NO FORMATO AAAAMMDD
}
