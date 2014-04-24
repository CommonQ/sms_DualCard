package edu.bupt.mms.numberlocate;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class NumberLocateProvider extends ContentProvider {
    private static final String TAG = "NumberLocateProvider";
    private NumberLocateDBHelper dbhelper;

    public static final String AUTHORITY = "edu.bupt.mms";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int NUMBERS_REGION_ID = 102;
    private static final int NUMBERS_REGION = 103;

    private static final int CODE_REGION_ID = 104;
    private static final int CODE_REGION = 105;
    
    private static final int SPECIAL_SERVICE = 106;
    private static final int SPECIAL_SERVICE_ID = 107;
    

    static {
        uriMatcher.addURI(AUTHORITY, "numberregion", NUMBERS_REGION);
        uriMatcher.addURI(AUTHORITY, "numberregion/#", NUMBERS_REGION_ID);
        
        uriMatcher.addURI(AUTHORITY, "specialservice", SPECIAL_SERVICE);
        uriMatcher.addURI(AUTHORITY, "specialservice/#", SPECIAL_SERVICE_ID);
        
    }

    

    public interface NumberRegion{
        public static final String _ID = "_id";
        //zaizhe
        public static final String NUMBER = "PhoneNo";
        public static final String CITY = "PCity";
        //zaizhe
        public static final String PROVINCE="Province";
        //zaizhe
        public static final String CARD="CardP";
        
        public static final String AREACODE="AreaCode";
        
        public static final String ZIPCODE="ZipCode";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/numberregion");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/numberregion";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/numberregion";
    }
    
    
    
    public interface SpecialService{
    	
        public static final String _ID = "_id";
        //zaizhe
        public static final String NUMBER = "PhoneNo";
        
        public static final String COMPANY = "Company";
        

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/specialservice");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/specialservice";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/specialservice";
    }
    
    

    @Override
    public boolean onCreate() {
        Log.i(TAG, "***onCreate()***");//--/data
        dbhelper = new NumberLocateDBHelper(getContext());
        // copy db file 
        NumberLocateDBHelper.copyDbToData(getContext(),false);
        /*
        dbhelper.getWritableDatabase();
        NumberLocateDBHelper.createCodeTable();
        NumberLocateDBHelper.initTables(getContext());
        */
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.i(TAG, "***getType()***");
        switch (uriMatcher.match(uri)) {
            case NUMBERS_REGION_ID:
                return NumberRegion.CONTENT_ITEM_TYPE;
            case NUMBERS_REGION:
                return NumberRegion.CONTENT_TYPE;
            case SPECIAL_SERVICE_ID:
                return SpecialService.CONTENT_ITEM_TYPE;
            case SPECIAL_SERVICE:
                return SpecialService.CONTENT_TYPE;
        }
        throw new IllegalStateException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "***delete()***");
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, "***insert()***");
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
        int matchID = uriMatcher.match(uri);
        Log.i(TAG, "***query()***uri="+uri+",matchID="+matchID+" selection: "+selection);
        
       
        String reglex = "[^0-9]+";
       if( selection.matches(reglex)){
    	   return null;
       }
       // Pattern pattern4 = Pattern.compile(expression8);
        
        
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (matchID) {
            case NUMBERS_REGION_ID:
                qb.setTables(NumberLocateDBHelper.TABLE_NAME);
                long id = ContentUris.parseId(uri);
                qb.appendWhere(NumberRegion._ID + "=" + id);
                break;
            case NUMBERS_REGION:
                qb.setTables(NumberLocateDBHelper.TABLE_NAME);
                break;
            case SPECIAL_SERVICE_ID:
            	//zaizhe
                qb.setTables(NumberLocateDBHelper.TABLE_CODE);
                long codeID = ContentUris.parseId(uri);
                qb.appendWhere(SpecialService._ID + "=" + codeID);
                break;
            case SPECIAL_SERVICE:
            	//zaizhe
                qb.setTables(NumberLocateDBHelper.TABLE_CODE);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return qb.query(dbhelper.getWritableDatabase(), projection, selection, selectionArgs, null, null,
                sortOrder, null);//zhe
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "***update()***");
        throw new UnsupportedOperationException();
    }

}
