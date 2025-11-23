import {createStore, Reducer} from "@reduxjs/toolkit";
import AppState from "./states/AppState";
import AppAction from "./Action";
import {
    CLEAR_ALL, COPY_STATE, RELOAD_COORDINATES, RELOAD_IMPORT_FILES, RELOAD_LOCATIONS, RELOAD_PERSONS,
    SET_CREATE_COORDINATES, SET_CREATE_LOCATION, SET_CREATE_PERSON, SET_NOTIFICATIONS, SET_SHOW_IMPORT_FILES_HISTORY,
    SET_UPDATE_COORDINATES,
    SET_UPDATE_LOCATION,
    SET_UPDATE_PERSON

} from "../consts/StateConsts";
import PersonDTO from "../dtos/PersonDTO";
import LocationDTO from "../dtos/LocationDTO";
import CoordinatesDTO from "../dtos/CoordinatesDTO";

export const defaultState: AppState = {
    createCoordinates: false,
    createLocation: false,
    createPerson: false,
    showImportFileHistory: false,
    notifications: [] as string[],
    reloadPersons: {},
    reloadLocations: {},
    reloadCoordinates: {},
    reloadImportFiles: {},
};

const reducer: Reducer<AppState, AppAction<PersonDTO | LocationDTO | CoordinatesDTO | boolean | string[] | {}>> = (state: AppState = defaultState, action: AppAction<PersonDTO | LocationDTO | CoordinatesDTO | boolean | string[] | {}>): AppState => {
    switch (action.type){
        case SET_UPDATE_PERSON:
            return {...defaultState, updatedPerson: action.payload as PersonDTO, notifications: state.notifications};
        case SET_UPDATE_LOCATION:
            return {...defaultState, updatedLocation: action.payload as LocationDTO, notifications: state.notifications};
        case SET_UPDATE_COORDINATES:
            return {...defaultState, updatedCoordinates: action.payload as CoordinatesDTO, notifications: state.notifications};
        case SET_CREATE_COORDINATES:
            return {...defaultState, createCoordinates: action.payload as boolean, notifications: state.notifications};
        case SET_CREATE_LOCATION:
            return {...defaultState, createLocation: action.payload as boolean, notifications: state.notifications};
        case SET_CREATE_PERSON:
            return {...defaultState, createPerson: action.payload as boolean, notifications: state.notifications};
        case SET_SHOW_IMPORT_FILES_HISTORY:
            return {...defaultState, showImportFileHistory: action.payload as boolean, notifications: state.notifications}
        case CLEAR_ALL:
            return defaultState;
        case COPY_STATE:
            return {...state};
        case SET_NOTIFICATIONS:
            return {...state, notifications: action.payload as string[]};
        case RELOAD_PERSONS:
            return {...state, reloadPersons: {}};
        case RELOAD_LOCATIONS:
            return {...state, reloadLocations: {}};
        case RELOAD_COORDINATES:
            return {...state, reloadCoordinates: {}};
        case RELOAD_IMPORT_FILES:
            return {...state, reloadImportFiles: {}};
        default:
            return state;
    }
}

const store = createStore(reducer);

export default store;