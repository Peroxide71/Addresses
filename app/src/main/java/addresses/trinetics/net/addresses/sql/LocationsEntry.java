package addresses.trinetics.net.addresses.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import addresses.trinetics.net.addresses.models.Address;

/**
 * Created by stas on 07.06.15.
 */
public class LocationsEntry {

    private LocationsSQLHelper mHelper;
    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public LocationsEntry(Context c) {
        mContext = c;
    }

    public LocationsEntry open() {
        mHelper = new LocationsSQLHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if(mHelper != null){
            mHelper.close();
        }
        mDatabase.close();
    }

    public void clearTable(){
        mDatabase.execSQL("delete from " + LocationsSQLHelper.DATABASE_TABLE);
    }

    public long addAddress(Address address) {
        ContentValues cv = new ContentValues();
        cv.put(LocationsSQLHelper.KEY_UID, address.getUid());
        cv.put(LocationsSQLHelper.KEY_CITY, address.getCity());
        cv.put(LocationsSQLHelper.KEY_STREET, address.getStreet());
        cv.put(LocationsSQLHelper.KEY_IMAGE_LINK, address.getImageURL());
        cv.put(LocationsSQLHelper.KEY_LATITUDE, address.getLatitude());
        cv.put(LocationsSQLHelper.KEY_LONGITUDE, address.getLongitude());
        return mDatabase.insert(LocationsSQLHelper.DATABASE_TABLE, null, cv);
    }

    public List<Address> getAddresses() {
        Address address;
        String[] columns = new String[] {LocationsSQLHelper.KEY_CITY,
                LocationsSQLHelper.KEY_STREET,
                LocationsSQLHelper.KEY_IMAGE_LINK,
                LocationsSQLHelper.KEY_UID,
                LocationsSQLHelper.KEY_LATITUDE,
                LocationsSQLHelper.KEY_LONGITUDE,
        };
        Cursor c = mDatabase.query(LocationsSQLHelper.DATABASE_TABLE, columns, null, null, null,
                null, null, null);
        List<Address> result =  new ArrayList<>();
        int iCity = c.getColumnIndex(LocationsSQLHelper.KEY_CITY);
        int iStreet = c.getColumnIndex(LocationsSQLHelper.KEY_STREET);
        int iUID = c.getColumnIndex(LocationsSQLHelper.KEY_UID);
        int iImageLink = c.getColumnIndex(LocationsSQLHelper.KEY_IMAGE_LINK);
        int iLatitude = c.getColumnIndex(LocationsSQLHelper.KEY_LATITUDE);
        int iLongitude= c.getColumnIndex(LocationsSQLHelper.KEY_LONGITUDE);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            address = new Address();
            address.setCity(c.getString(iCity));
            address.setStreet(c.getString(iStreet));
            address.setImageURL(c.getString(iImageLink));
            address.setUid(c.getInt(iUID));
            address.setLatitude(c.getDouble(iLatitude));
            address.setLongitude(c.getDouble(iLongitude));
            result.add(address);
        }
        c.close();
        return result;
    }

}