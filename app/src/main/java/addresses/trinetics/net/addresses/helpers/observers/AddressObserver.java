package addresses.trinetics.net.addresses.helpers.observers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by stas on 07.06.15.
 */
public class AddressObserver extends BroadcastReceiver {
    private AsyncTaskLoader loader;

    public AddressObserver(AsyncTaskLoader loader, String filter) {
        this.loader = loader;
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(filter);
        this.loader.getContext().registerReceiver(this, intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loader.onContentChanged();
    }
}
