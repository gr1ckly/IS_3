import {BASE_URL, IMPORT_FILES_PATH} from "../consts/HttpConsts";
import ImportFileDTO from "../dtos/ImportFileDTO";

class ImportFileService {
    public static async getCount() : Promise<number> {
        const finalUrl: string = BASE_URL + IMPORT_FILES_PATH + "/get_count";
        const response: Response = await fetch(finalUrl, {
            method: "GET",
            headers: {
                'Accept' : "application/json",
            },
        });
        if (!response.ok) {
            console.log("Error while get count import files: " + response.status);
            return -1;
        }

        return await response.json();
    }

    public static async getDownloadLink(id: number) : Promise<string> {
        const finalUrl: string = BASE_URL + IMPORT_FILES_PATH + "/" + id + "/download";
        const response: Response = await fetch(finalUrl, {
            method: "GET",
            headers: {
                'Accept' : "application/json",
            },
        });
        if (!response.ok) {
            console.log("Error while getting download link file_id: " + id);
            return "";
        }

        return await response.text();
    }

    public static async searchImportFiles(offset: number, limit: number) : Promise<ImportFileDTO[]> {
        const params = new URLSearchParams();
        params.append('offset', String(offset));
        params.append('limit', String(limit));
        const finalUrl: string = BASE_URL + IMPORT_FILES_PATH + "/search_files?" + params.toString();
        const response: Response = await fetch(finalUrl, {
            method: "GET",
            headers: {
                'Accept' : "application/json",
            },
        });
        if (!response.ok) {
            console.log("Error while search locations: " + response.status);
            return [];
        }

        return await response.json();
    }

    public static async uploadFile(file: File) : Promise<number> {
        const formData = new FormData();
        formData.append("file", file);
        const finalUrl: string = BASE_URL + IMPORT_FILES_PATH + "/import";
        const response: Response = await fetch(finalUrl, {
            method: "POST",
            headers: {
                'Accept' : "application/json",
            },
            body: formData
        });
        if (!response.ok) {
            console.log(`Error while import file ${file.name}: ` + response.status);
            return -1;
        }
        return await response.json();
    }
}

export default ImportFileService;