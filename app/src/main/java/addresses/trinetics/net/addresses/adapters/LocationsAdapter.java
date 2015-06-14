package addresses.trinetics.net.addresses.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import addresses.trinetics.net.addresses.R;
import addresses.trinetics.net.addresses.models.Address;

/**
 * Created by stas on 07.06.15.
 */
public class LocationsAdapter extends BaseAdapter implements Filterable {
    private List<Address> addressList;
    private List<Address> filteredAddressList;
    private LayoutInflater inflater;
    private Context context;
    private ImageLoader imageLoader;

    public LocationsAdapter(List<Address> addressList, Context context) {
        this.addressList = addressList;
        this.filteredAddressList = addressList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        initImageLoader(context);
    }

    public static void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder).resetViewBeforeLoading()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(options)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    results.values = addressList;
                    results.count = addressList.size();
                } else {
                    ArrayList<Address> filteredResultContactsList = new ArrayList<>();
                    for(Address address : addressList){
                        if(address.getCity().toLowerCase().contains(charSequence)){
                            filteredResultContactsList.add(address);
                        }
                    }
                    results.values = filteredResultContactsList;
                    results.count = filteredResultContactsList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredAddressList = (ArrayList<Address>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return filteredAddressList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filteredAddressList.get(position).getUid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddressViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.location_item, null);
            holder = new AddressViewHolder();
            holder.city = (TextView)convertView.findViewById(R.id.textViewCity);
            holder.street = (TextView)convertView.findViewById(R.id.textViewStreet);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewItemPicture);
            convertView.setTag(holder);
        } else {
            holder = (AddressViewHolder)convertView.getTag();
        }
        Address address = filteredAddressList.get(position);
        if(address != null){
            holder.city.setText(address.getCity());
            holder.street.setText(address.getStreet());
            imageLoader.displayImage(address.getImageURL(), holder.imageView);
        }
        return convertView;
    }

    private class AddressViewHolder{
        public ImageView imageView;
        public TextView city;
        public TextView street;
    }
}
