package addresses.trinetics.net.addresses.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.io.IOException;

import addresses.trinetics.net.addresses.R;
import addresses.trinetics.net.addresses.helpers.NetworkRequestHelper;
import addresses.trinetics.net.addresses.ui.fragments.LocationsFragment;


public class MainActivity extends FragmentActivity {
    private Location currentLocation;
    private final float LOCATION_REFRESH_DISTANCE = 100;
    private final long LOCATION_REFRESH_TIME = 30000;
    public static final String ACTION_SEARCH = "action_search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
    }

    private void setLocationsFragment(Location location){
        LocationsFragment locationsFragment = new LocationsFragment();
        locationsFragment.setCurrent(location);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, locationsFragment)
                .commit();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_button) {
            onSearchClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void onSearchClicked(){
        Intent intent = new Intent(ACTION_SEARCH);
        sendBroadcast(intent);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if(currentLocation == null){
                currentLocation = location;
                setLocationsFragment(currentLocation);
            } else {
                currentLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
