import { OperationModel } from "./operation.model";
import { TransferenceModel } from "./transference.model";

export interface ExtratoModel {
    type: 'dep'|'sac'|'transf';
    dest_acc?:string;
    id: number; //O ID SERÁ A DATA NO FORMATO AAAAMMDD
}
