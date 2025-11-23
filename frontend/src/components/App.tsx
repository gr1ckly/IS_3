import React, {useEffect} from 'react';
import '../App.css';
import SpecialOperationsComponent from "./specialOperations/SpecialOperations";
import PersonComponent from "./tables/PersonComponent";
import LocationComponent from "./tables/LocationComponent";
import CoordinatesComponent from "./tables/CoordinatesComponent";
import Popup from "./modal/Popup";
import {useDispatch, useSelector} from "react-redux";
import {
    COPY_STATE,
    RELOAD_COORDINATES, RELOAD_IMPORT_FILES,
    RELOAD_LOCATIONS,
    RELOAD_PERSONS,
    SET_NOTIFICATIONS
} from "../consts/StateConsts";
import {BASE_URL, SSE_PATH} from "../consts/HttpConsts";
import styles from "../styles/App.module.css";
import Notification from "./modal/Notification";
import {selectNotifications} from "../storage/StateSelectors";

function App() {
    const dispatcher = useDispatch();
    const notifications = useSelector(selectNotifications);

    useEffect(() => {
        const eventSource = new EventSource(BASE_URL + SSE_PATH);

        eventSource.onopen = () => console.log("SSE подключено");

        eventSource.addEventListener("person", (e) => {
            console.log("SSE обновление person:", e.data);
            dispatcher({ type: RELOAD_PERSONS, payload: {} });
        });

        eventSource.addEventListener("location", (e) => {
            console.log("SSE обновление location:", e.data);
            dispatcher({ type: RELOAD_LOCATIONS, payload: {} });
        });

        eventSource.addEventListener("coordinates", (e) => {
            console.log("SSE обновление coordinates:", e.data);
            dispatcher({ type: RELOAD_COORDINATES, payload: {} });
        });

        eventSource.addEventListener("import_file", (e) => {
            console.log("SSE обновление import_file:", e.data);
            dispatcher({ type: RELOAD_IMPORT_FILES, payload: {} });
        });

        eventSource.addEventListener("import_file_status", (e) => {
            dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, e.data]});
        })

        eventSource.onerror = (err) => {
            console.error("Ошибка SSE:", err);
        };

        return () => {
            console.log("SSE закрыто");
            eventSource.close();
        };
    }, [dispatcher]);

    return (
        <div className={styles.App}>
            <Notification/>
            <Popup/>
            <div className={styles.layout}>
                <div className={styles.person}>
                    <PersonComponent />
                </div>
                <div className={styles.middleRow}>
                    <div className={styles.location}>
                        <LocationComponent />
                    </div>
                    <div className={styles.coordinates}>
                        <CoordinatesComponent />
                    </div>
                </div>
                <div className={styles.special}>
                    <SpecialOperationsComponent />
                </div>
            </div>
        </div>
    )
}

export default App;
