package addresses.trinetics.net.addresses.models;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stas on 07.06.15.
 */
public class Address implements Comparable<Address>{
    private String city;
    private String street;
    @SerializedName("image")
    private String imageURL;
    private int uid;
    private double latitude;
    private double longitude;
    private float distanceToCurrent;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDistanceToCurrent() {
        return distanceToCurrent;
    }

    public void setDistanceToCurrent(float distanceToCurrent) {
        this.distanceToCurrent = distanceToCurrent;
    }

    @Override
    public int compareTo(@NonNull Address another) {
        return Float.compare(distanceToCurrent, another.distanceToCurrent);
    }
}
