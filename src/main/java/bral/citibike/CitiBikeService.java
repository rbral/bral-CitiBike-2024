package bral.citibike;

import bral.citibike.json.StationObjects;
import bral.citibike.json.StatusObjects;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * https://gbfs.citibikenyc.com/gbfs/en/station_information.json
 * https://gbfs.citibikenyc.com/gbfs/en/station_status.json
 */
public interface CitiBikeService
{
    @GET("/gbfs/en/station_information.json")
    Single<StationObjects> getStationInformation();

    @GET("/gbfs/en/station_status.json")
    Single<StatusObjects> getStationStatus();

}
