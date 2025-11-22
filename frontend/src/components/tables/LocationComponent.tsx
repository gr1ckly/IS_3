import {useDispatch, useSelector} from "react-redux";
import {useEffect, useState} from "react";
import FilterOption from "../../dtos/FilterOption";
import OperationType from "../../dtos/OperationType";
import {
    COPY_STATE,
    RELOAD_LOCATIONS,
    SET_CREATE_LOCATION,
    SET_NOTIFICATIONS,
    SET_UPDATE_LOCATION
} from "../../consts/StateConsts";
import LocationDTO from "../../dtos/LocationDTO";
import TableState from "../../storage/states/TableState";
import LocationService from "../../services/LocationService";
import styles from "../../styles/LocationComponent.module.css";
import {selectNotifications, selectReloadCoordinates, selectReloadLocations} from "../../storage/StateSelectors";


interface FilterProps {
    name?: string,
}

interface SortProps {
    id?: boolean,
    name?: boolean,
    x?: boolean,
    y?: boolean,
}

export default function LocationComponent () {
    const dispatcher = useDispatch();
    const [locations, setLocations] = useState<LocationDTO[]>([]);
    const notifications = useSelector(selectNotifications) ?? [];
    const reloadLocations = useSelector(selectReloadLocations);

    const [tableState, setTableState] = useState<TableState>({pageSize: 5, currPage: 1, count: 0});
    const [filterState, setFilterState] = useState<FilterProps>({});
    const [sortState, setSortState] = useState<SortProps>({});
    const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false);

    const applyFilters = () => {
        const newFilters: FilterOption[] =[];
        if (filterState.name && filterState.name !== "") {
            newFilters.push({fieldName: "name", operationType: OperationType.EQUAL, value: filterState.name});
        }
        if (sortState.id !== undefined) {
            newFilters.push({fieldName: "id", operationType: sortState.id ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.x !== undefined) {
            newFilters.push({fieldName: "x", operationType: sortState.x ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        if (sortState.y !== undefined) {
            newFilters.push({fieldName: "y", operationType: sortState.y ? OperationType.SORTED : OperationType.SORTED_DESC});
        }
        setTableState({...tableState, filters: newFilters});
    }

    useEffect(() => {
        const currFilters = tableState.filters ?? [];
        LocationService.getCount(...currFilters).then((newCount: number) => {
            if (newCount !== tableState.count) {
                let nextPage = tableState.currPage;
                if (newCount <= (tableState.currPage - 1) * tableState.pageSize) {
                    nextPage = Math.max(Math.trunc(((newCount - 1) / tableState.pageSize) + 1), 1);
                }
                setTableState({ ...tableState, count: newCount, currPage: nextPage });
            }
        });
    }, [tableState.filters, reloadLocations])

    useEffect(() => {
        const currFilters = tableState.filters ?? [];
        LocationService.searchLocations(Math.trunc((tableState.currPage - 1) * tableState.pageSize), tableState.pageSize, ...currFilters).then((newLocations: LocationDTO[]) => {
            setLocations(newLocations);
        });
    }, [tableState.currPage, tableState.pageSize, tableState.filters, reloadLocations]);

    const handleNext = async () => {
        if (tableState) {
            setTableState({...tableState, currPage: tableState.currPage + 1});
        }
    }

    const handlePrev = async () => {
        if (tableState && tableState.currPage > 1) {
            setTableState({...tableState, currPage: tableState.currPage - 1});
        }
    }

    return (
            <div className={styles.LocationComponent}>
                <div className={styles.header}>
                <button
                    className={styles.button}
                    onClick={() => dispatcher({ type: SET_CREATE_LOCATION, payload: true })}
                >
                    Создать Location
                </button>

                <button
                    className={styles.button}
                    onClick={() => setIsFilterOpen(!isFilterOpen)}
                >
                    Фильтры/Сортировка
                </button>
                </div>
                {isFilterOpen && (
                    <div className={styles.filters}>
                        <div className={styles.field}>
                            <span className={styles.label}>id</span>
                            <select
                                className={styles.select}
                                value={sortState.id === undefined ? "" : (sortState.id ? "ASC" : "DESC")}
                                onChange={(e) => {
                                    const val = e.target.value;
                                    setSortState({
                                        ...sortState,
                                        id: val === "" ? undefined : val === "ASC",
                                    });
                                }}
                            >
                                <option value="">— выберите —</option>
                                <option value="ASC">ASC</option>
                                <option value="DESC">DESC</option>
                            </select>
                        </div>

                        <div className={styles.field}>
                            <span className={styles.label}>name</span>
                            <input
                                className={styles.input}
                                type="text"
                                maxLength={1000}
                                value={filterState.name ?? ""}
                                onChange={(e) =>
                                    setFilterState({
                                        ...filterState,
                                        name: e.target.value === "" || /^-?\d+(\.\d+)?$/.test(e.target.value.trim()) ? "" : e.target.value,
                                    })
                                }
                            />
                        </div>

                        <div className={styles.field}>
                            <span className={styles.label}>x:</span>
                            <select
                                className={styles.select}
                                value={sortState.x === undefined ? "" : (sortState.x ? "ASC" : "DESC")}
                                onChange={(e) => {
                                    const val = e.target.value;
                                    setSortState({
                                        ...sortState,
                                        x: val === "" ? undefined : val === "ASC",
                                    });
                                }}
                            >
                                <option value="">— выберите —</option>
                                <option value="ASC">ASC</option>
                                <option value="DESC">DESC</option>
                            </select>
                        </div>

                        <div className={styles.field}>
                            <span className={styles.label}>y:</span>
                            <select
                                className={styles.select}
                                value={sortState.y === undefined ? "" : (sortState.y ? "ASC" : "DESC")}
                                onChange={(e) => {
                                    const val = e.target.value;
                                    setSortState({
                                        ...sortState,
                                        y: val === "" ? undefined : val === "ASC",
                                    });
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

                <div className={styles.table}>
                    <table>
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>X</th>
                            <th>Y</th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {locations.map((location) => (
                            <tr key={location.id}>
                                <td>{location.id ?? ""}</td>
                                <td>{location.name}</td>
                                <td>{location.x}</td>
                                <td>{location.y}</td>
                                <td>
                                    <button
                                        className={styles.deleteButton}
                                        onClick={async () => {
                                            if (location.id){
                                                const number = await LocationService.deleteLocation(location.id);
                                                if (number === -1) {
                                                    dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, "Ошибка при удалении Location. Попробуйте сначала убрать все зависимости, а потом попробовать снова."]});
                                                } else {
                                                    dispatcher({type: RELOAD_LOCATIONS, payload: {}});
                                                }
                                        }}}>
                                        Удалить
                                    </button>
                                </td>
                                <td>
                                    <button
                                        className={styles.updateButton}
                                        onClick={() =>
                                            dispatcher({
                                                type: SET_UPDATE_LOCATION,
                                                payload: location,
                                            })
                                        }
                                    >
                                        Обновить
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <div className={styles.pagination}>
                        {tableState.currPage > 1 && (
                            <button className={styles.pageButton} onClick={handlePrev}>prev</button>
                        )}
                        {tableState.pageSize <= tableState.count && (
                            <label className={styles.page}>{tableState.currPage}</label>
                        )}
                        {tableState.currPage * tableState.pageSize < tableState.count && (
                            <button className={styles.pageButton} onClick={handleNext}>next</button>
                        )}
                    </div>
                </div>
            </div>
    )
}
