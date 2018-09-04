package control;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import model.google.Geocoding;
public interface GeoLocationService {
    @GET("json")
    Call<Geocoding> getAddressInfo(@Query("address") String address, @Query("key") String key);
}
