package bral.citibike.aws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class CitiBikeRequestHandlerTest
{
    @Test
    public void handleRequest() throws IOException
    {
        // given
        String requestJson = new String(Files.readAllBytes(Paths.get("request.json")));
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(requestJson);

        // when
        CitiBikeRequestHandler handler = new CitiBikeRequestHandler();
        CitiBikeRequestHandler.CitiBikeResponse actual = handler.handleRequest(event, null);

        // then
        assertNotNull(actual);
        assertNotNull(actual.start());
        assertNotNull(actual.end());

        Gson gson = new Gson();
        String expectedResponseJson = new String(Files.readAllBytes(Paths.get("response.json")));
        CitiBikeRequestHandler.CitiBikeResponse expected = gson.fromJson(expectedResponseJson,
                CitiBikeRequestHandler.CitiBikeResponse.class);

        assertEquals(expected.from().lat(), actual.from().lat());
        assertEquals(expected.from().lon(), actual.from().lon());
        assertEquals(expected.to().lat(), actual.to().lat());
        assertEquals(expected.to().lon(), actual.to().lon());
    }
}
