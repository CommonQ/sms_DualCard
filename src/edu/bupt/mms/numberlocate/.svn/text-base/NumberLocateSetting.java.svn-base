package edu.bupt.mms.numberlocate;

import edu.bupt.mms.R;

import edu.bupt.mms.numberlocate.NumberLocateProvider.NumberRegion;
import edu.bupt.mms.numberlocate.NumberLocateProvider.SpecialService;
import edu.bupt.mms.ui.ConversationListItem;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

public class NumberLocateSetting extends Activity implements OnCheckedChangeListener, OnClickListener{
    public static final String RegionFuncKey = "regionFunc";
    public static final String AnimatFuncKey = "animationFunc";
    private Switch regionFunc = null;
    private Switch animatFunc = null;

    private EditText numberInput = null;
    private Button searchBut = null;
    private TextView searchResult = null;

    private AsyncQueryHandler queryHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        animatFunc = (Switch)findViewById(R.id.animation_switch);
        regionFunc = (Switch)findViewById(R.id.number_region_switch);

        numberInput = (EditText)findViewById(R.id.number_input);
        searchBut = (Button)findViewById(R.id.search_but);
        searchResult = (TextView)findViewById(R.id.search_result);
        searchBut.setOnClickListener(this);

        animatFunc.setChecked(getSettingValue(this, AnimatFuncKey));
        regionFunc.setChecked(getSettingValue(this, RegionFuncKey));

        animatFunc.setOnCheckedChangeListener(this);
        regionFunc.setOnCheckedChangeListener(this);

        queryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor != null && cursor.moveToNext()) {
                  //  String city = cursor.getString(0)+" "+cursor.getString(1);
                	String city = cursor.getString(0);
                	cursor.close();
                    searchResult.setText(city);
                    
              //      ConversationListItem.city2=city;
                } else {
                    searchResult.setText(R.string.search_failed);
          //      	 ConversationListItem.city2="";
                }
                searchBut.setEnabled(true);
                numberInput.setEnabled(true);
            }
        };
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.animation_switch:
                saveSetting(this,AnimatFuncKey,isChecked);
                break;
            case R.id.number_region_switch:
                saveSetting(this,RegionFuncKey,isChecked);
                break;
        }
    }

    static boolean getSettingValue(Context context, String key) {
        SharedPreferences cache = context.getSharedPreferences("number_region_setting", Context.MODE_PRIVATE);
        return cache.getBoolean(key, false);
    }

    static void saveSetting(Context context, String key, boolean value) {
        SharedPreferences cache = context.getSharedPreferences("number_region_setting", Context.MODE_PRIVATE);
        cache.edit().putBoolean(key, value).commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_but) {
            String number = numberInput.getText().toString();
            if (TextUtils.isEmpty(number)) {
                return;
            }
            searchResult.setText(R.string.searching);
            searchBut.setEnabled(false);
            numberInput.setEnabled(false);
            startQuery(this, number);
            //Log.v("final","city:"+PhoneStatusRecevier.queryRegion(this, number));
        }
    }
    
    public  void startAsynQuery(String number){
    	
    	
    	 startQuery(this, number);
    }

    private void startQuery(Context context, String number) {
        if (number.length() >= 11) {
            String formatNumber = PhoneStatusRecevier.formatNumber(number);
            Log.v("NumberLocateSetting", "number: "+number+" formatNumber: "+formatNumber);
            
            String selection = null;
            String[] projection = null;
            Uri uri =NumberRegion.CONTENT_URI;
            if (formatNumber.length() == 7) {
                selection = NumberRegion.NUMBER+"="+formatNumber;
                uri = NumberRegion.CONTENT_URI;
                projection = new String[]{NumberRegion.PROVINCE,NumberRegion.CITY, NumberRegion.AREACODE};
            } else {
            	
                Log.v("NumberLocateSetting", "NumberRegion.AREACODE: "+NumberRegion.AREACODE+" formatNumber: "+formatNumber);
               
                selection = NumberRegion.AREACODE+"="+"'"+formatNumber+"'"+" OR " + NumberRegion.AREACODE+"=" + "'"+formatNumber.substring(0, 3)+"'";
                uri = NumberRegion.CONTENT_URI;
                projection = new String[]{NumberRegion.PROVINCE,NumberRegion.CITY};
            }
            queryHandler.startQuery(0, null, uri, projection, selection, null, null);
        } else if(number.length()<=5){
        	
        	 String selection = null;
             String[] projection = null;
             Uri uri =SpecialService.CONTENT_URI;
        	
        	selection = SpecialService.NUMBER+"="+number;
            uri = SpecialService.CONTENT_URI;
            projection = new String[]{SpecialService.COMPANY};
        	
            queryHandler.startQuery(0, null, uri, projection, selection, null, null);
        }
        
        
        else {
            searchResult.setText(R.string.search_failed);
            searchBut.setEnabled(true);
            numberInput.setEnabled(true);
        	
        	//ConversationListItem.city2="";
        }
    }




}
