package bral.citibike.aws;

import bral.citibike.CitiBikeService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LambdaServiceFactory
{
    public LambdaService getService()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://blw5eok2nolyyraol7a2hvvudy0mrnqg.lambda-url.us-east-2.on.aws/")
                // Configure Retrofit to use Gson to turn the Json into Objects
                .addConverterFactory(GsonConverterFactory.create())
                // Configure Retrofit to use Rx
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return retrofit.create(LambdaService.class);
    }
}
