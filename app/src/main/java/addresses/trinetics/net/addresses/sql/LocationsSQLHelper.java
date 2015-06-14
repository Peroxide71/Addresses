package addresses.trinetics.net.addresses.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stas on 07.06.15.
 */
public class LocationsSQLHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "locations";
    private final static int DATABASE_VERSION = 1;
    public final static String DATABASE_TABLE = "locations_table";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_UID = "uid";
    public static final String KEY_CITY = "city";
    public static final String KEY_STREET = "street";
    public static final String KEY_IMAGE_LINK = "image_link";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";

    public LocationsSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CITY
                + " TEXT NOT NULL, " + KEY_STREET
                + " TEXT NOT NULL, "+ KEY_IMAGE_LINK
                + " TEXT NOT NULL, "+ KEY_LATITUDE
                + " REAL, "+ KEY_LONGITUDE
                + " REAL, "+ KEY_UID
                + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}