package bral.citibike.aws;

import bral.citibike.*;
import bral.citibike.json.StatusObjects;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * https://gbfs.citibikenyc.com/gbfs/en/station_information.json
 * https://gbfs.citibikenyc.com/gbfs/en/station_status.json
 */
public interface LambdaService
{
    @POST("/")
    Single<CitiBikeRequestHandler.CitiBikeResponse> callLambda(@Body CitiBikeRequestHandler.CitiBikeRequest request);

}
