import {ImportStatusEnum} from "./ImportStatusEnum";

interface ImportFileDTO {
    id: number,
    name: string,
    creationDate: string,
    status: ImportStatusEnum,
    addedPersons?: number,
}

export default ImportFileDTO;