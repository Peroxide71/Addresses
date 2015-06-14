package addresses.trinetics.net.addresses.helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import addresses.trinetics.net.addresses.models.Address;
import addresses.trinetics.net.addresses.sql.LocationsEntry;

/**
 * Created by stas on 07.06.15.
 */
public class NetworkRequestHelper {
    private static NetworkRequestHelper instance;
    private final String SERVER_URL = "http://public.trinetix.net/test.json";

    private NetworkRequestHelper() {

    }

    public static NetworkRequestHelper getInstance() {
        if(instance == null){
            instance = new NetworkRequestHelper();
        }
        return instance;
    }

    public HttpResponse getAddressesFromServer() throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(SERVER_URL);

        //Perform the request and check the status code
        HttpResponse response = client.execute(post);
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == 200) {
            return response;
        }
        return null;
    }
}
