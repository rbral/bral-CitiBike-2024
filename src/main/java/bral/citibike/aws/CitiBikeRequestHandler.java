package bral.citibike.aws;

import bral.citibike.CitiBikeService;
import bral.citibike.CitiBikeServiceFactory;
import bral.citibike.CitiBikeUtils;
import bral.citibike.json.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent,
        CitiBikeRequestHandler.CitiBikeResponse> {

    @Override
    public CitiBikeResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String body = event.getBody();
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(body, CitiBikeRequest.class);

        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationObjects stations = service.getStationInformation().blockingGet();
        StatusObjects statuses = service.getStationStatus().blockingGet();

        CitiBikeUtils utils = new CitiBikeUtils(stations, statuses);
        StationObject startStation = utils.closestStationWithBikes(request.from.lat, request.from.lon);
        StationObject endStation = utils.closestStationWithSlots(request.to.lat, request.to.lon);

        return new CitiBikeResponse(
                request.from,
                new StationInfo(startStation.lat, startStation.lon, startStation.name, startStation.station_id),
                new StationInfo(endStation.lat, endStation.lon, endStation.name, endStation.station_id),
                request.to
        );
    }


    record Coordinates(double lat, double lon) {}

    record CitiBikeRequest(
            Coordinates from,
            Coordinates to
    ) { }

    record StationInfo(
            double lat,
            double lon,
            String name,
            //CHECKSTYLE:OFF
            String station_id //CHECKSTYLE:ON
    ) { }

    record CitiBikeResponse(
            Coordinates from,
            StationInfo start,
            StationInfo end,
            Coordinates to
    ) { }

}
