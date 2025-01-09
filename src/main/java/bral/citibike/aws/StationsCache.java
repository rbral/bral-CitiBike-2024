package bral.citibike.aws;

import bral.citibike.CitiBikeService;
import bral.citibike.CitiBikeServiceFactory;
import bral.citibike.json.StationObjects;
import com.google.gson.Gson;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class StationsCache
{
    private final String bucketName = "bral.citibike.bbnn";
    private static final String stationsKey = "stations.json";

    private S3Client s3Client;
    private CitiBikeService service;
    private final Gson gson;

    private StationObjects stationObjects;
    private Instant lastModified;


    public StationsCache(S3Client s3Client)
    {
        this.s3Client = s3Client;
        service = new CitiBikeServiceFactory().getService();
        this.gson = new Gson();
    }

    public StationObjects getStations()
    {
        System.out.println("Attempting to fetch stations...");
        if (stationObjects != null)
        {
            // Case 1: If stations != null and lastModified time is LESS than 1 hour
            if (Duration.between(lastModified, Instant.now()).toHours() < 1)
            {
                return stationObjects;
            } else {
                // Case 2: If stations != null and lastModified time is GREATER than 1 hour
                stationObjects = service.getStationInformation().blockingGet();

                // save it to a file
                /*String json = gson.toJson(stationObjects);
                try (FileWriter writer = new FileWriter(STATIONS_KEY)) {
                    writer.write(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                lastModified = Instant.now();
                uploadToS3(stationObjects);
                return stationObjects;
            }
        } else {
            // stationObjects == null
            if (!s3LastModifiedOverAnHour())
            {
                // Case 3: If stations == null and S3’s lastModified date is LESS than 1 hour
                stationObjects = readFromS3();
                lastModified = Instant.now();
                return stationObjects;
            } else {
                // Case 4: If stations == null and S3’s lastModified date is GREATER than 1 hour
                stationObjects = service.getStationInformation().blockingGet();
                lastModified = Instant.now();
                uploadToS3(stationObjects);
                return stationObjects;

            }
        }
    }

    private void uploadToS3(StationObjects stationObjects)
    {
        String content = gson.toJson(stationObjects);
        Region region = Region.US_EAST_2;
        s3Client = S3Client.builder()
                .region(region)
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(stationsKey)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    }

    private StationObjects readFromS3()
    {
        s3Client = S3Client.create();

        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(stationsKey)
                .build();

        InputStream in = s3Client.getObject(getObjectRequest);

        stationObjects = gson.fromJson(new InputStreamReader(in), StationObjects.class);

        return stationObjects;
    }

    private boolean s3LastModifiedOverAnHour()
    {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(stationsKey)
                .build();

        try {
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            Instant lastModified = headObjectResponse.lastModified();
            return Duration.between(lastModified, Instant.now()).toHours() > 1;
        } catch (Exception e) {
            // either the file doesn't exist in S3 or you don't have access to it.
            return true;
        }
    }
}
