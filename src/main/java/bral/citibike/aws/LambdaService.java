package bral.citibike.aws;

import bral.citibike.*;
import bral.citibike.json.StatusObjects;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LambdaService
{
    @POST("/")
    Single<CitiBikeRequestHandler.CitiBikeResponse> callLambda(@Body CitiBikeRequestHandler.CitiBikeRequest request);

}
