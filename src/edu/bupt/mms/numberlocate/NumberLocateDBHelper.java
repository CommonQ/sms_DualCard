package edu.bupt.mms.numberlocate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.bupt.mms.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class NumberLocateDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "NumberLocateDBHelper";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "number_region.db";
    public static final String TABLE_NAME = "Haoduan";
    public static final String TABLE_CODE = "special_service";

    private static final String ASSETS_DB = "db/" + DB_NAME;

    public static final String DB_PATH = Environment.getDataDirectory().getAbsolutePath() + "/data/edu.bupt.mms/" + DB_NAME;

    private Context mContext;
    public NumberLocateDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i(TAG, "***NumberLocateDBHelper()***");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "***onCreate()***");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "***onUpgrade()***oldVersion="+oldVersion+",newVersion="+newVersion);
        int version = oldVersion;
        if (version != DB_VERSION) {
            copyDbToData(mContext,true);
        }
    }

    private static SQLiteDatabase mDb;
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDb == null) {
            mDb = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return mDb;
    }
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return getWritableDatabase();
    }

    static void copyDbToData(Context context,boolean cover) {
        Log.i(TAG, "***copyDbToData()***");
        File file = new File(DB_PATH);
        if (cover) {
            if (file.exists()) file.delete();
        } else {
            if (file.exists()) return;
        }
        InputStream is = null;
        try {
            is = context.getAssets().open(ASSETS_DB);
            FileOutputStream fos = new FileOutputStream(DB_PATH,false);
            int count = 0;
            byte[] buffer = new byte[1024*10];
            while ((count = is.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
            is.close();
            fos.close();
            buffer = null;
        } catch (IOException e) {
            Log.e(TAG, "***copyDbToData() IO error***");
            e.printStackTrace();
        }
    }

    static void createCodeTable() {
        mDb.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "code STRING NOT NULL,"
                + "city STRING NOT NULL,"
                + "province STRING NOT NULL);");
    }

    static void initTables(Context context) {
        Log.i(TAG, "***initTables()***");
        final long curTime = System.currentTimeMillis();
        InputStream inputStream = null;
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            inputStream = context.getResources().openRawResource(R.raw.code);
            isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);
            String line = null;
            String province = null;
            while ((line = br.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    int codeStart = line.indexOf("0");
                    //province
                    if (codeStart == -1) {
                        province = line;
                        Log.i(TAG, "***initTables()***province="+province);
                    } else {
                        String city = line.substring(0, codeStart);
                        String code = line.substring(codeStart);
                        mDb.execSQL("INSERT INTO " + TABLE_NAME + " (code,city,province) VALUES ('"+ code + "','" + city + "','" + province + "');");
                    }
                }
            }
            inputStream.close();
            isr.close();
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "***initTables() IO error***");
            e.printStackTrace();
        }
        Log.e(TAG,"***initTables:pay time:" + (System.currentTimeMillis() - curTime) + "***");
    }

}
