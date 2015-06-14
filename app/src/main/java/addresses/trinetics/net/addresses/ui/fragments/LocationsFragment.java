package addresses.trinetics.net.addresses.ui.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import addresses.trinetics.net.addresses.R;
import addresses.trinetics.net.addresses.adapters.LocationsAdapter;
import addresses.trinetics.net.addresses.helpers.AddressesParser;
import addresses.trinetics.net.addresses.helpers.NetworkRequestHelper;
import addresses.trinetics.net.addresses.helpers.observers.loaders.AddressLoader;
import addresses.trinetics.net.addresses.models.Address;
import addresses.trinetics.net.addresses.sql.LocationsEntry;
import addresses.trinetics.net.addresses.ui.activities.MainActivity;


public class LocationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Address>>{
    private ListView addressesListView;
    private EditText searchInput;
    private RelativeLayout searchLayout;
    private ImageButton stopSearchButton;
    private SearchReceiver searchReceiver;
    private ProgressBar pullToRefreshProgressBar;
    private float initialTouchPosition;
    private int pullLimit;
    private ProgressBar progressBarMain;
    private boolean isTaskRunning;
    private MainActivity mActivity;
    private Location current;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(rootView);
        initValues();
        return rootView;
    }

    private void initViews(View rootView){
        addressesListView = (ListView) rootView.findViewById(R.id.listViewLocations);
        searchInput = (EditText) rootView.findViewById(R.id.etSearch);
        searchLayout = (RelativeLayout) rootView.findViewById(R.id.rlSearch);
        stopSearchButton = (ImageButton) rootView.findViewById(R.id.bCloseSearch);
        pullToRefreshProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBarPullToRefresh);
        progressBarMain = (ProgressBar) rootView.findViewById(R.id.progressBarMain);
    }

    private void initValues(){
        stopSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSearch();
            }
        });
        searchReceiver = new SearchReceiver();
        pullLimit = getScreenHeight() / 5;
        if(!isTaskRunning){
            new LoadLocationsTask().execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter searchFilter = new IntentFilter(MainActivity.ACTION_SEARCH);
        mActivity.registerReceiver(searchReceiver, searchFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(searchReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity)activity;
    }

    private void setLocations(List<Address> data){
        for(Address address : data){
            Location location = new Location("");
            location.setLongitude(address.getLongitude());
            location.setLatitude(address.getLatitude());
            float distance = location.distanceTo(current);
            address.setDistanceToCurrent(distance);
        }
        Collections.sort(data);
    }

    private void setSearchListener(){
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ((LocationsAdapter)addressesListView.getAdapter()).getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void resetFilter(){
        ((LocationsAdapter)addressesListView.getAdapter()).getFilter().filter("");
    }

    private void stopSearch(){
        searchInput.setText("");
        searchLayout.setVisibility(View.GONE);
    }

    private int getScreenHeight(){
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    @Override
    public Loader<List<Address>> onCreateLoader(int id, Bundle args) {
        return new AddressLoader(mActivity);
    }

    @Override
    public void onLoadFinished(Loader<List<Address>> loader, List<Address> data) {
        setLocations(data);
        LocationsAdapter locationsAdapter = new LocationsAdapter(data, mActivity);
        addressesListView.setAdapter(locationsAdapter);
        resetFilter();
        setSearchListener();
        addressesListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!ViewCompat.canScrollVertically(v, -1)){

                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            initialTouchPosition = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(event.getY() - initialTouchPosition > pullLimit / 5){
                                pullToRefreshProgressBar.setVisibility(View.VISIBLE);
                                pullToRefreshProgressBar.setProgress((int)(100 * (event.getY() - initialTouchPosition)/pullLimit));
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if(event.getY() - initialTouchPosition > pullLimit && !isTaskRunning){
                                new LoadLocationsTask().execute();
                            }
                            initialTouchPosition = 0;
                            pullToRefreshProgressBar.setVisibility(View.GONE);
                            break;
                    }
                } else {
                    pullToRefreshProgressBar.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<Address>> loader) {

    }

    private class LoadLocationsTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            isTaskRunning = true;
            progressBarMain.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{
                HttpResponse response = NetworkRequestHelper.getInstance().getAddressesFromServer();
                if(response != null) {
                    List<Address> addressList = AddressesParser.getInstance().parseAdressesFromResponse(response);
                    updateDBContent(addressList);
                }
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //Done caching, now starting to load content from DB to UI.
            mActivity.getSupportLoaderManager().initLoader(0, null, LocationsFragment.this);
            Intent broadcast = new Intent();
            broadcast.setAction(AddressLoader.FILTER);
            //Sending broadcast to notify the observer, it's time to re-load content from DB.
            mActivity.sendBroadcast(broadcast);
            progressBarMain.setVisibility(View.GONE);
            isTaskRunning = false;
        }
    }

    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MainActivity.ACTION_SEARCH)){
                searchLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateDBContent(List<Address> addresses){
        LocationsEntry entry = new LocationsEntry(mActivity);
        entry.open();
        entry.clearTable();
        for(Address address : addresses){
            entry.addAddress(address);
        }
        entry.close();
    }

    public void setCurrent(Location current) {
        this.current = current;
    }
}
