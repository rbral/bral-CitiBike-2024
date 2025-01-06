package bral.citibike;

import bral.citibike.json.StationObjects;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import bral.citibike.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CitiBikeUtilsTest
{
    @Test
    public void findStationStatus()
    {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StatusObjects statusesResponse = service.getStationStatus().blockingGet();
        String stationId = statusesResponse.data.stations.get(0).station_id;
        CitiBikeUtils utils = new CitiBikeUtils(null, statusesResponse);

        // when
        StatusObject status = utils.findStationStatus(stationId);

        // then
        assertEquals(stationId, status.station_id);
    }

    @Test
    public void closestStationWithBikes() throws IOException
    {
        // given
        String jsonStation = new String(Files.readAllBytes(Paths.get("stations_mock.json")));
        StationObjects stationsResponse = new Gson().fromJson(jsonStation, StationObjects.class);
        String jsonStatus = new String(Files.readAllBytes(Paths.get("statuses_mock.json")));
        StatusObjects statusesResponse = new Gson().fromJson(jsonStatus, StatusObjects.class);

        CitiBikeUtils utils = new CitiBikeUtils(stationsResponse, statusesResponse);

        double lat = stationsResponse.data.stations.get(3).lat;
        double lon = stationsResponse.data.stations.get(3).lon;
        String stationId = stationsResponse.data.stations.get(3).station_id;

        // when
        StationObject station = utils.closestStationWithBikes(lat, lon);

        // then
        assertEquals(stationId, station.station_id);
    }

    @Test
    public void closestStationWithSlots()
    {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationObjects stationsResponse = service.getStationInformation().blockingGet();
        StatusObjects statusesResponse = service.getStationStatus().blockingGet();
        CitiBikeUtils utils = new CitiBikeUtils(stationsResponse, statusesResponse);

        double lat = stationsResponse.data.stations.get(3).lat;
        double lon = stationsResponse.data.stations.get(3).lon;
        String stationId = stationsResponse.data.stations.get(3).station_id;

        // when
        StationObject station = utils.closestStationWithSlots(lat, lon);

        // then
        assertEquals(stationId, station.station_id);
    }

}
