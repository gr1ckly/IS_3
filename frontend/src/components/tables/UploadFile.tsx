import {useDispatch, useSelector} from "react-redux";
import {selectNotifications} from "../../storage/StateSelectors";
import {useRef, useState} from "react";
import {SET_NOTIFICATIONS} from "../../consts/StateConsts";
import {MAX_FILE_SIZE} from "../../consts/HttpConsts";
import ImportFileService from "../../services/ImportFileService";
import styles from "../../styles/UploadFile.module.css";

export default function UploadFile() {
    const dispatcher = useDispatch();
    const notification = useSelector(selectNotifications);
    const [file, setFile] = useState<File | undefined>(undefined);
    const fileInputRef = useRef<HTMLInputElement | null>(null);

    const resetFileSelection = () => {
        setFile(undefined);
        if (fileInputRef.current) {
            fileInputRef.current.value = "";
        }
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const f = e.target.files?.[0];
        if (!f) {
            resetFileSelection();
            return;
        }

        if (!f.name.toLowerCase().endsWith(".yaml") && !f.name.toLowerCase().endsWith(".yml")) {
            resetFileSelection();
            dispatcher({type: SET_NOTIFICATIONS, payload: [...notification, "Разрешены только файлы с расширением .yaml/.yml"]});
            return;
        }

        if (f.size > MAX_FILE_SIZE * 1024 * 1024) {
            resetFileSelection();
            dispatcher({type: SET_NOTIFICATIONS, payload: [...notification, `Размер файла не должен превышать ${MAX_FILE_SIZE} МБ`]});
            return;
        }
        setFile(f);
    };

    const handleUpload = async () => {
        if (!file) {
            dispatcher({type: SET_NOTIFICATIONS, payload: [...notification, "Пожалуйста, выберите файл для загрузки"]});
            return;
        }

        const fileId = await ImportFileService.uploadFile(file);
        if (fileId > 0) {
            resetFileSelection();
            dispatcher({type: SET_NOTIFICATIONS, payload: [...notification, `File started handling, id: ${fileId}`]});
            return;
        }

        resetFileSelection();
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notification, "Error while loading file"]});
    };


    return (
        <div className={styles.uploadBar}>
            <input
                className={styles.fileInput}
                type="file"
                accept=".yaml,.yml"
                ref={fileInputRef}
                onChange={handleFileChange}
            />

            <button
                className={styles.uploadButton}
                onClick={handleUpload}
                disabled={!file}
            >
                Загрузить файл
            </button>

            <span className={styles.fileName}>{file === undefined ? "" : file.name}</span>
        </div>
    );
}
