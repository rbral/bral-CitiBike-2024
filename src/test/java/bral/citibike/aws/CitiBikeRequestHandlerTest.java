package bral.citibike.aws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CitiBikeRequestHandlerTest
{
    @Test
    public void handleRequest()
    {
        // given
        Gson gson = new Gson();
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        String input = """
                {        
                  "from": {
                    "lat": 40.8211,
                    "lon": -73.9359
                  },
                  "to": {
                    "lat": 40.7190,
                    "lon": -73.9585
                  }
                }           
                """;
        event.setBody(input);

        // when
        CitiBikeRequestHandler handler = new CitiBikeRequestHandler();
        CitiBikeRequestHandler.CitiBikeResponse response = handler.handleRequest(event, null);

        // then
        assertNotNull(response);
        assertNotNull(response.start());
        assertNotNull(response.end());
        assertEquals(40.8211, response.from().lat());
        assertEquals(-73.9359, response.from().lon());
        assertEquals(40.7190, response.to().lat());
        assertEquals(-73.9585, response.to().lon());
    }
}
