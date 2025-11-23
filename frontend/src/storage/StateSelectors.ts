import AppState from "./states/AppState";
import PersonDTO from "../dtos/PersonDTO";
import LocationDTO from "../dtos/LocationDTO";
import CoordinatesDTO from "../dtos/CoordinatesDTO";
import App from "../components/App";

export const selectUpdatedPerson = (state: AppState): PersonDTO | undefined => {
    return state.updatedPerson;
};

export const selectUpdatedLocation = (state: AppState): LocationDTO | undefined => {
    return state.updatedLocation;
};

export const selectUpdatedCoordinates = (state: AppState): CoordinatesDTO | undefined => {
    return state.updatedCoordinates;
};

export const selectCreatePerson = (state: AppState): boolean | undefined => {
    return state.createPerson;
};

export const selectCreateLocation = (state: AppState): boolean | undefined => {
    return state.createLocation;
};

export const selectCreateCoordinates = (state: AppState): boolean | undefined => {
    return state.createCoordinates;
};

export const selectNotifications = (state: AppState): string[] => {
    return state.notifications;
}

export const selectShowImportFileHistory = (state: AppState): boolean => {
    return state.showImportFileHistory;
}

export const selectReloadPersons = (state: AppState): {} => {
    return state.reloadPersons;
}

export const selectReloadLocations = (state: AppState): {} => {
    return state.reloadLocations;
}

export const selectReloadCoordinates = (state: AppState): {} => {
    return state.reloadCoordinates;
}

export const selectReloadImportFiles = (state: AppState): {} => {
    return state.reloadImportFiles;
}