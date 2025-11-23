import PersonDTO from "../../dtos/PersonDTO";
import LocationDTO from "../../dtos/LocationDTO";
import CoordinatesDTO from "../../dtos/CoordinatesDTO";

interface AppState {
    updatedPerson?: PersonDTO;
    updatedLocation?: LocationDTO;
    updatedCoordinates?: CoordinatesDTO;
    createCoordinates: boolean;
    createLocation: boolean;
    createPerson: boolean;
    showImportFileHistory: boolean;
    notifications: string[];
    reloadPersons: {};
    reloadLocations: {};
    reloadCoordinates: {};
    reloadImportFiles: {};
}

export default AppState;