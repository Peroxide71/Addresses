package addresses.trinetics.net.addresses.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import addresses.trinetics.net.addresses.models.Address;

/**
 * Created by stas on 14.06.15.
 */
public class AddressesParser {
    private static AddressesParser instance;

    private AddressesParser() {
    }

    public static AddressesParser getInstance() {
        if (instance == null) {
            instance = new AddressesParser();
        }
        return instance;
    }

    public List<Address> parseAdressesFromResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        List<Address> addresses = null;

        try {
            //Read the server response and attempt to parse it as JSON
            Reader reader = new InputStreamReader(content);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JsonParser parser = new JsonParser();
            JsonArray jArray = parser.parse(reader).getAsJsonArray();

            addresses = new ArrayList<>();
            for(JsonElement element : jArray){
                Address address = gson.fromJson(element , Address.class);
                addresses.add(address);
            }
            content.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return addresses;
    }
}
