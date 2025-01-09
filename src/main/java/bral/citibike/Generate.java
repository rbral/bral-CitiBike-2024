package bral.citibike;

import bral.citibike.aws.StationsCache;
import bral.citibike.json.StationObjects;
import com.google.gson.Gson;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.FileWriter;
import java.io.IOException;

public class Generate
{
    public static void main(String[] args) {
        System.out.println("Starting the process to generate stations.json...");

        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationObjects stationsResponse = service.getStationInformation().blockingGet();

        Gson gson = new Gson();
        String json = gson.toJson(stationsResponse);

        // Save it to a file
        try (FileWriter writer = new FileWriter("stations.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*Region region = Region.US_EAST_2;
        S3Client s3Client = S3Client.builder()
                .region(region)
                .build();
        StationsCache cache = new StationsCache(s3Client);

        // This will generate `stations.json` in the current working directory
        cache.getStations();*/
    }

}
