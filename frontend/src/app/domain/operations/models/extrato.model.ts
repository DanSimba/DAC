import { OperationModel } from "./operation.model";
import { TransferenceModel } from "./transference.model";

export interface ExtratoModel {
    type: OperationModel|TransferenceModel;
    id: number; //O ID SERÁ A DATA NO FORMATO AAAAMMDD
}
