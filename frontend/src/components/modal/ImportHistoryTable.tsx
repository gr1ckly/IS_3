import {useDispatch, useSelector} from "react-redux";
import {useEffect, useState} from "react";
import {SET_SHOW_IMPORT_FILES_HISTORY} from "../../consts/StateConsts";
import styles from "../../styles/ImportHistoryTable.module.css";
import {selectReloadImportFiles} from "../../storage/StateSelectors";
import TableState from "../../storage/states/TableState";
import ImportFileDTO from "../../dtos/ImportFileDTO";
import ImportFileService from "../../services/ImportFileService";

export default function ImportHistoryTable() {
    const dispatcher = useDispatch();
    const reloadImportFiles = useSelector(selectReloadImportFiles);
    const [importFiles, setImportFiles] = useState<ImportFileDTO[]>([]);
    const [localTableState, setLocalTableState] = useState<TableState>({
        pageSize: 10,
        currPage: 1,
        count: 0,
    });

    useEffect(() => {
        ImportFileService.getCount().then((newCount: number) => {
            const adjustedState = { ...localTableState };
            if (newCount !== localTableState.count) {
                if (newCount <= (localTableState.currPage - 1) * localTableState.pageSize) {
                    adjustedState.currPage = Math.max(Math.trunc(((newCount - 1) / localTableState.pageSize) + 1), 1);
                }
                adjustedState.count = newCount;
                setLocalTableState(adjustedState);
            }
        });
    }, [reloadImportFiles]);

    useEffect(() => {
        ImportFileService.searchImportFiles(
            Math.trunc((localTableState.currPage - 1) * localTableState.pageSize),
            Math.trunc(localTableState.pageSize)
        ).then((files: ImportFileDTO[]) => setImportFiles(files));

    }, [localTableState.currPage, localTableState.pageSize, reloadImportFiles]);

    const handleNext = () => {
        setLocalTableState({ ...localTableState, currPage: localTableState.currPage + 1 });
    };

    const handlePrev = () => {
        if (localTableState.currPage > 1) {
            setLocalTableState({ ...localTableState, currPage: localTableState.currPage - 1 });
        }
    };

    return (
        <div className={styles.historyCard}>
            <div className={styles.header}>
                <span className={styles.title}>История импортов</span>
                <button
                    className={styles.closeButton}
                    onClick={() => dispatcher({type: SET_SHOW_IMPORT_FILES_HISTORY, payload: false})}
                >
                    Закрыть
                </button>
            </div>

            <div className={styles.tableWrapper}>
                <table className={styles.table}>
                    <thead className={styles.thead}>
                    <tr>
                        <th className={styles.headerCell}>id</th>
                        <th className={styles.headerCell}>name</th>
                        <th className={styles.headerCell}>creationDate</th>
                        <th className={styles.headerCell}>status</th>
                        <th className={styles.headerCell}>addedPersons</th>
                    </tr>
                    </thead>
                    <tbody className={styles.body}>
                    {importFiles.map(file => (
                        <tr className={styles.row} key={file.id ?? Math.random()}>
                            <th className={styles.cell}>{file.id}</th>
                            <th className={styles.cell}>{file.name}</th>
                            <th className={styles.cell}>{file.creationDate ? new Date(file.creationDate).toLocaleString() : ""}</th>
                            <th className={styles.cell}>{file.status}</th>
                            <th className={styles.cell}>
                                {file.addedPersons !== undefined ? file.addedPersons : ""}
                            </th>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className={styles.pagination}>
                {localTableState.currPage > 1 && (
                    <button className={styles.pageButton} onClick={handlePrev}>
                        Предыдущая
                    </button>
                )}
                {localTableState.pageSize <= localTableState.count && (
                    <label className={styles.pageLabel}>{localTableState.currPage}</label>
                )}
                {localTableState.currPage * localTableState.pageSize < localTableState.count && (
                    <button className={styles.pageButton} onClick={handleNext}>
                        Следующая
                    </button>
                )}
            </div>
        </div>
    );
}
