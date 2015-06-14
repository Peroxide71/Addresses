package addresses.trinetics.net.addresses.helpers.observers.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import addresses.trinetics.net.addresses.helpers.observers.AddressObserver;
import addresses.trinetics.net.addresses.models.Address;
import addresses.trinetics.net.addresses.sql.LocationsEntry;

/**
 * Created by stas on 07.06.15.
 */
public class AddressLoader extends AsyncTaskLoader<List<Address>> {
    public static final String FILTER = "content_updated";
    private Context ctx;

    public AddressLoader(Context context) {
        super(context);
        this.ctx = context;
    }

    @Override
    public List<Address> loadInBackground() {
        LocationsEntry entry = new LocationsEntry(ctx);
        entry.open();
        List<Address> result = entry.getAddresses();
        entry.close();
        return result;
    }

    @Override
    public void deliverResult(List<Address> data) {
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        createObserver();
    }

    private BroadcastReceiver createObserver(){
        return new AddressObserver(this, FILTER);
    }
}
