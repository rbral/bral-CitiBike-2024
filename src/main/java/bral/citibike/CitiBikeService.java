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
    Single<StationObjects> getStationInformation(
            /*@Query("station_id") String station_id,
            @Query("name") String name,
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("capacity") int capacity*/
    );

    @GET("/gbfs/en/station_status.json")
    Single<StatusObjects> getStationStatus(
            /*@Query("station_id") String station_id,
            @Query("num_bikes_available") int num_bikes_available,
            @Query("num_docks_available") int num_docks_available*/
    );
}
