import PersonTable from "./PersonTable";
import TableState from "../../storage/states/TableState";
import Color from "../../dtos/ColorEnum";
import Country from "../../dtos/CountryEnum";
import {useState} from "react";
import FilterOption from "../../dtos/FilterOption";
import OperationType from "../../dtos/OperationType";
import {useDispatch} from "react-redux";
import {SET_CREATE_PERSON, SET_SHOW_IMPORT_FILES_HISTORY} from "../../consts/StateConsts";
import styles from "../../styles/PersonComponent.module.css";
import UploadFile from "./UploadFile";

interface FilterProps {
    name?: string,
    eyeColor?: Color
    hairColor?: Color,
    nationality?: Country,
}

interface SortProps {
    id?: boolean,
    name?: boolean,
    coordinatesId?: boolean,
    creationDate?: boolean,
    eyeColor?: boolean
    hairColor?: boolean,
    locationId?: boolean,
    height?: boolean,
    birthday?: boolean,
    weight?: boolean,
    nationality?: boolean,
}

export default function PersonComponent() {
    const [tableState, setTableState] = useState<TableState>({pageSize: 10, currPage: 1, count: 0});
    const [filterState, setFilterState] = useState<FilterProps>({});
    const [sortState, setSortState] = useState<SortProps>({});
    const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false);
    const [file, setFile] = useState(undefined);
    const dispatcher = useDispatch();

    const applyFilters = () => {
        const newFilters: FilterOption[] =[];
        if (filterState.name && filterState.name !== "") {
            newFilters.push({fieldName: "name", operationType: OperationType.EQUAL, value: filterState.name});
        }
        if (filterState.eyeColor) {
            newFilters.push({fieldName: "eye_color", operationType: OperationType.EQUAL, value: filterState.eyeColor.valueOf().toString()});
        }
        if (filterState.hairColor) {
            newFilters.push({fieldName: "hair_color", operationType: OperationType.EQUAL, value: filterState.hairColor.valueOf().toString()});
        }
        if (filterState.nationality) {
            newFilters.push({fieldName: "nationality", operationType: OperationType.EQUAL, value: filterState.nationality.valueOf().toString()});
        }
        if (sortState.id !== undefined) {
            newFilters.push({fieldName: "id", operationType: sortState.id ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.coordinatesId !== undefined) {
            newFilters.push({fieldName: "coordinates_id", operationType: sortState.coordinatesId ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.creationDate !== undefined) {
            newFilters.push({fieldName: "creation_date", operationType: sortState.creationDate ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.eyeColor !== undefined) {
            newFilters.push({fieldName: "eye_color", operationType: sortState.eyeColor ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.hairColor !== undefined) {
            newFilters.push({fieldName: "hair_color", operationType: sortState.hairColor ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.locationId !== undefined) {
            newFilters.push({fieldName: "location_id", operationType: sortState.locationId ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.height !== undefined) {
            newFilters.push({fieldName: "height", operationType: sortState.height ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.weight !== undefined) {
            newFilters.push({fieldName: "weight", operationType: sortState.weight ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.nationality !== undefined) {
            newFilters.push({fieldName: "nationality", operationType: sortState.nationality ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        setTableState({...tableState, filters: newFilters});
    }

    return (
        <div className={styles.personComponent}>
            <div className={styles.buttonRow}>
                <button
                    className={styles.button}
                    onClick={() => dispatcher({ type: SET_CREATE_PERSON, payload: true })}
                >
                    Создать Person
                </button>
                <button
                    className={styles.button}
                    onClick={() => setIsFilterOpen(!isFilterOpen)}
                >
                    Фильтры/Сортировка
                </button>
                <button
                    className={styles.button}
                    onClick={() => dispatcher({type: SET_SHOW_IMPORT_FILES_HISTORY, payload: true})}
                >
                    История импорта
                </button>
                <UploadFile/>
            </div>
            {isFilterOpen && (
                <div className={styles.filterPanel}>
                    <div className={styles.field}>
                        <label className={styles.label}>{"id"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.id === undefined ? "" : (sortState.id ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, id: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, id: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, id: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"name"}</label>
                        <input
                            className={styles.input}
                            type="text"
                            maxLength={1000}
                            value={filterState.name ?? ""}
                            onChange={(e) => {
                                setFilterState({
                                    ...filterState,
                                    name: e.target.value === "" || /^-?\d+(\.\d+)?$/.test(e.target.value.trim()) ? "" : e.target.value,
                                });
                            }}
                        />
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"coordinates_id"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.coordinatesId === undefined ? "" : (sortState.coordinatesId ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, coordinatesId: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, coordinatesId: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, coordinatesId: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"creation_date"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.creationDate === undefined ? "" : (sortState.creationDate ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, creationDate: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, creationDate: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, creationDate: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"eyeColor"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.eyeColor === undefined ? "" : (sortState.eyeColor ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, eyeColor: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, eyeColor: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, eyeColor: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"hairColor"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, hairColor: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, hairColor: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, hairColor: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"location_id"}</label>
                        <select
                            className={styles.select}
                            value={sortState.locationId === undefined ? "" : (sortState.locationId ? "ASC" : "DESC")}
                            id="sort"
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, locationId: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, locationId: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, locationId: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"height"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.height === undefined ? "" : (sortState.height   ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, height: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, height: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, height: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"birthday"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.birthday === undefined ? "" : (sortState.birthday ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, birthday: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, birthday: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, birthday: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"weight"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.weight === undefined ? "" : (sortState.weight ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, weight: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, weight: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, weight: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div className={styles.field}>
                        <label className={styles.label}>{"nationality"}</label>
                        <select
                            className={styles.select}
                            id="sort"
                            value={sortState.nationality === undefined ? "" : (sortState.nationality ? "ASC" : "DESC")}
                            onChange={(e) => {
                                if (e.target.value === "") {
                                    setSortState({ ...sortState, nationality: undefined });
                                } else if (e.target.value === "ASC") {
                                    setSortState({ ...sortState, nationality: true });
                                } else if (e.target.value === "DESC") {
                                    setSortState({ ...sortState, nationality: false });
                                }
                            }}
                        >
                            <option value="">— выберите —</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <button className={styles.applyButton} onClick={applyFilters}>
                        Применить
                    </button>

                    <button className={styles.applyButton} onClick={() => {
                        setTableState({...tableState, filters: []});
                        setSortState({});
                        setFilterState({});
                    }}>
                        Сбросить
                    </button>
                </div>
            )}
            <PersonTable
                tableState={tableState}
                onChangeTableState={(state: TableState) => setTableState(state)}
            />
        </div>

    )
}
