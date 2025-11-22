import {useState, useEffect, useCallback} from "react";
import { useDispatch, useSelector } from "react-redux";
import styles from "../../styles/PersonTable.module.css";
import TableState from "../../storage/states/TableState";
import PersonDTO from "../../dtos/PersonDTO";
import PersonService from "../../services/PersonService";
import OperationType from "../../dtos/OperationType";
import {
    COPY_STATE,
    RELOAD_LOCATIONS,
    RELOAD_PERSONS,
    SET_NOTIFICATIONS,
    SET_UPDATE_PERSON
} from "../../consts/StateConsts";
import {selectNotifications, selectReloadPersons} from "../../storage/StateSelectors";

interface Props {
    tableState: TableState;
    onChangeTableState: (newState: TableState) => void;
}

export default function PersonTable(props: Readonly<Props>) {
    const dispatcher = useDispatch();
    const reloadPersons = useSelector(selectReloadPersons);
    const [persons, setPersons] = useState<PersonDTO[]>([]);
    const [localTableState, setLocalTableState] = useState(props.tableState);
    useEffect(() => {
        setLocalTableState({ ...props.tableState });
    }, [props.tableState]);
    const stateNotifications = useSelector(selectNotifications);

    useEffect( () => {
        const currFilters = localTableState.filters ?? [];
        PersonService.getCount(...currFilters).then((newCount: number) => {
            let adjustedState = { ...localTableState };
            if (newCount !== localTableState.count) {
                if (newCount <= (localTableState.currPage - 1) * localTableState.pageSize) {
                    adjustedState.currPage = Math.max(Math.trunc(((newCount - 1) / localTableState.pageSize) + 1), 1);
                }
                adjustedState.count = newCount;
                setLocalTableState(adjustedState);
                props.onChangeTableState(adjustedState);
            }
        });
    }, [localTableState.filters, reloadPersons]);

    useEffect(() => {
        const currFilters = localTableState.filters ?? [];
        PersonService.searchPersons(
            Math.trunc((localTableState.currPage - 1) * localTableState.pageSize),
            Math.trunc(localTableState.pageSize),
            ...currFilters
        ).then((persons: PersonDTO[]) => {setPersons(persons)});

    }, [localTableState.currPage, localTableState.pageSize, localTableState.filters, reloadPersons]);

    const handleNext = () => {
        if (localTableState) {
            setLocalTableState({ ...localTableState, currPage: localTableState.currPage + 1 });
            props.onChangeTableState({ ...localTableState, currPage: localTableState.currPage + 1 });
        }
    };

    const handlePrev = () => {
        if (localTableState && localTableState.currPage > 1) {
            setLocalTableState({ ...localTableState, currPage: localTableState.currPage - 1 });
            props.onChangeTableState({ ...localTableState, currPage: localTableState.currPage - 1 });
        }
    };

    return (
        <div className={styles.tableWrapper}>
        <table className={styles.table}>
            <thead className={styles.thead}>
            <tr className={styles.tr}>
                <th className={styles.th}>Id</th>
                <th className={styles.th}>name</th>
                <th className={styles.th}>coordinates_id</th>
                <th className={styles.th}>creation_date</th>
                <th className={styles.th}>eye_color</th>
                <th className={styles.th}>hair_color</th>
                <th className={styles.th}>location_id</th>
                <th className={styles.th}>height</th>
                <th className={styles.th}>birthday</th>
                <th className={styles.th}>weight</th>
                <th className={styles.th}>nationality</th>
                <th className={styles.th}></th>
                <th className={styles.th}></th>
            </tr>
            </thead>
            <tbody className={styles.tbody}>
            {persons.map(person => (
                <tr className={styles.tr} key={person.id ?? Math.random()}>
                    <th className={styles.th}>{person.id ?? ""}</th>
                    <th className={styles.th}>{person.name}</th>
                    <th className={styles.th}>{person.coordinatesId}</th>
                    <th className={styles.th}>
                        {person.creationDate ? new Date(person.creationDate).toLocaleString() : ""}
                    </th>
                    <th className={styles.th}>{person.eyeColor?.toString() ?? ""}</th>
                    <th className={styles.th}>{person.hairColor}</th>
                    <th className={styles.th}>{person.locationId ?? ""}</th>
                    <th className={styles.th}>{person.height}</th>
                    <th className={styles.th}>
                        {person.birthday ? (person.birthday.includes("T") ? person.birthday.split("T")[0] : person.birthday) : ""}
                    </th>
                    <th className={styles.th}>{person.weight}</th>
                    <th className={styles.th}>{person.nationality.toString()}</th>
                    <th className={styles.th}>
                        <button
                            className={styles.deleteButton}
                            onClick={async () => {
                                const number = await PersonService.deletePerson({
                                    fieldName: "id",
                                    operationType: OperationType.EQUAL,
                                    value: person.id?.toString(),
                                });
                                if (number === -1) {
                                    dispatcher({type: SET_NOTIFICATIONS, payload: [...stateNotifications, "Ошибка при удалении Person"]});
                                } else {
                                    dispatcher({type: RELOAD_PERSONS, payload: {}});
                                }
                            }}
                        >
                            Удалить
                        </button>
                    </th>
                    <th className={styles.th}>
                        <button
                            className={styles.updateButton}
                            onClick={() => dispatcher({ type: SET_UPDATE_PERSON, payload: person })}
                        >
                            Обновить
                        </button>
                    </th>
                </tr>
            ))}
            </tbody>
        </table>
            {localTableState.currPage > 1 && (
                <button className={styles.pageButton} onClick={handlePrev}>
                    prev
                </button>
            )}
            {localTableState.pageSize <= localTableState.count && (
                <label className={styles.pageLabel}>{localTableState.currPage}</label>
            )}
            {localTableState.currPage * localTableState.pageSize < localTableState.count && (
                <button className={styles.pageButton} onClick={handleNext}>
                    next
                </button>
            )}
        </div>
    );
}
