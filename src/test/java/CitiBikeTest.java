import bral.citibike.CitiBikeService;
import bral.citibike.CitiBikeServiceFactory;
import bral.citibike.json.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CitiBikeTest
{
    @Test
    public void getStationInformation()
    {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        // when
        StationObjects stationObjectsResponse = service.getStationInformation().blockingGet();

        // then
        StationObject stationObject = stationObjectsResponse.data.stations.get(0);
        assertNotNull(stationObject.station_id);
        assertNotNull(stationObject.name);
        assertTrue(stationObject.lat != 0);
        assertTrue(stationObject.lon != 0);
        assertTrue(stationObject.capacity >= 0);
    }

    @Test
    public void getStationStatus()
    {
        // given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        // when
        StatusObjects statusObjectsResponse = service.getStationStatus().blockingGet();

        // then
        StatusObject statusObject = statusObjectsResponse.data.stations.get(0);
        assertNotNull(statusObject.station_id);
        assertTrue(statusObject.num_bikes_available >= 0);
        assertTrue(statusObject.num_docks_available >= 0);

    }
}
