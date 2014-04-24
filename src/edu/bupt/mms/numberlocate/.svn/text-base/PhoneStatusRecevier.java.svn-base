
package edu.bupt.mms.numberlocate;


import java.io.IOException;

import edu.bupt.mms.R;

import edu.bupt.mms.numberlocate.NumberLocateProvider.NumberRegion;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneStatusRecevier extends BroadcastReceiver {
    private static final String TAG = "PhoneStatusRecevier";
    private static boolean isInComingPhone = false;
    private static boolean isOutComingPhone = false;
    private static String phoneNumber = null;
    private static Context mContext = null;
    private static final int CacheSize = 20;

    private static final int CALL_STATE_RINGING = 1;
    private static final int CALL_STATE_OFFHOOK = 2;
    private static final int CALL_STATE_IDLE = 3;
    private static final int NEW_OUTGOING_CALL = 4;
    
    private static final int SIM1_CALL_STATE_RINGING = 7;
    private static final int SIM2_CALL_STATE_RINGING = 8;
    private static final int SIM1_CALL_STATE_OFFHOOK = 9;
    private static final int SIM2_CALL_STATE_OFFHOOK = 10;
    private static final int NEW_OUTGOING_CALL_STATE_IDLE = 11;

    public static final int ANIMATION_PROCESS = 1116;
    public static final int ANIMATION_FINISH = 1117;
    
    public static int cardOneState = 0;
    public static int cardTwoState = 0;
    
    private static MediaRecorder mRecorder = null; 

    private static Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case NEW_OUTGOING_CALL:{
                    String number = (String)msg.obj;
                    // query from cache
                    String city = queryFromCache(mContext,number);
                    Log.v("number1",""+city);
                    
                    
                    if (TextUtils.isEmpty(city)) {
                        // query from database
                        city = queryRegion(mContext,number);

                        // save as cache
                        saveAsCache(mContext,number,city);
                    }
//                    
                   Log.v("number2",""+city);
                    //String city = new NumberLocate(mContext).getLocation(number);
                    
                    if(cardOneState==0&&cardTwoState==2){
                    	showToast(mContext, city, 2, MSimTelephonyManager.getDefault().getNetworkOperatorName(1), "去电");
                	}
                    if(cardOneState==2&&cardTwoState==0){
                    	showToast(mContext, city, 1, MSimTelephonyManager.getDefault().getNetworkOperatorName(0), "去电");
                	}
                    
                    break;
                }
                case CALL_STATE_RINGING:{
                    String number = (String)msg.obj;
                    // query from cache
                    String city = queryFromCache(mContext,number);
                    if (TextUtils.isEmpty(city)) {
                        // query from database
                        city = queryRegion(mContext,number);
                        // save as cache
                        saveAsCache(mContext,number,city);
                    }
                    if(cardOneState==0&&cardTwoState==1){
                    	showToast(mContext, city, 2, MSimTelephonyManager.getDefault().getNetworkOperatorName(1), "来电");
                	}
                    if(cardOneState==1&&cardTwoState==0){
                    	showToast(mContext, city, 1, MSimTelephonyManager.getDefault().getNetworkOperatorName(0), "来电");
                	}
                    break;
                }
                case CALL_STATE_OFFHOOK:
                case CALL_STATE_IDLE:
                	//Log.v("test","cc" + Setting.getSettingValue(mContext, Setting.AnimatFuncKey) );
                	
                	//if(cardOneState==0&&cardTwoState==0){
                		clearToast(mContext);
                	//}
                	
//                    if (!Setting.getSettingValue(mContext, Setting.AnimatFuncKey)) {
//                        Log.v("test","aa");
//                    	//clearToast(mContext);
//                    } else {
//                        //startAnimation(mContext);
//                        Log.v("test","bb");
//                    }
                    break;
                case NEW_OUTGOING_CALL_STATE_IDLE:              
                		clearToast(mContext);               
                	
//                    if (!Setting.getSettingValue(mContext, Setting.AnimatFuncKey)) {
//                    	//clearToast(mContext);
//                    } else {
//                        //startAnimation(mContext);
//                    }
                    break;
                case ANIMATION_PROCESS:
//                    params.x += 1;
//                    params.y += -1;
//                    if (mWm != null&&toast!=null) {
//                        mWm.updateViewLayout(toast, params);
//                    }
                    break;
                case ANIMATION_FINISH:
                    //clearToast(mContext);
                    break;
            }
        };
    };

    public static String formatNumber(String number) {
        StringBuilder sb = new StringBuilder(number);
        if (sb.charAt(0) == '+') {
            return sb.delete(0, 3).substring(0, 7).toString();
        }
////zaizhe
//        if(sb.indexOf("010")==0){
//        	return sb.substring(0, 3);
//        }
//        
//        if(sb.indexOf("020")==0){
//        	return sb.substring(0, 3);
//        }
//        
//        if(sb.indexOf("030")==0){
//        	return sb.substring(0, 3);
//        }
//        
//        if(sb.indexOf("040")==0){
//        	return sb.substring(0, 3);
//        }
//  //zaizhe      
        if (sb.charAt(0) == '0') {
            return sb.substring(0, 4);
        }
        if (sb.length()<7){
        	return sb.toString();
        }
        return sb.substring(0, 7).toString();
    }

    public static void saveAsCache(Context context, String number, String city) {
        if (!TextUtils.isEmpty(city) && number.length() >= 11) {
            String formatNumber = formatNumber(number);
            SharedPreferences cache = context.getSharedPreferences("number_region", Context.MODE_PRIVATE);
            cache.edit().putString(formatNumber, city).commit();
        }
    }

    static String queryFromCache(Context context, String number) {
        if (TextUtils.isEmpty(number)) return null;
        SharedPreferences cache = context.getSharedPreferences("number_region", Context.MODE_PRIVATE);
        String formatNumber = formatNumber(number);
        return cache.getString(formatNumber, null);
        //return null;
    }

    static String queryRegion(Context context, String number) {
        String city = null;
//        if (!TextUtils.isEmpty(number) && number.length() >= 11) {
//            String formatNumber = formatNumber(number);
//            Cursor cursor = null;
//            if (formatNumber.length() == 7) {
//                String selection = NumberRegion.NUMBER+"="+formatNumber;
//                cursor = context.getContentResolver().query(NumberRegion.CONTENT_URI, null, selection, null, null);
//            } else {
//                String selection = CityCode.CODE+"="+formatNumber+" OR " + CityCode.CODE+"=" + formatNumber.substring(0, 3);
//                cursor = context.getContentResolver().query(CityCode.CONTENT_URI, null, selection, null, null);
//            }
//            //Log.v(TAG, "queryRegion()-->formatNumber="+formatNumber+",cursor:::" + cursor.getCount());
//            if (cursor != null && cursor.getCount() > 0) {
//                cursor.moveToNext();
//                city = cursor.getString(0);
//                cursor.close();
//            }
//        }
    	if (!TextUtils.isEmpty(number) && number.length() >= 11) {
            String formatNumber = PhoneStatusRecevier.formatNumber(number);
            String selection = null;
            String[] projection = null;
            Uri uri = NumberRegion.CONTENT_URI;
            if (formatNumber.length() == 7) {
                selection = NumberRegion.NUMBER+"="+formatNumber;
                uri = NumberRegion.CONTENT_URI;
                projection = new String[]{NumberRegion.CITY};
            } else {
            	 selection = NumberRegion.AREACODE+"="+"'"+formatNumber+"'"+" OR " + NumberRegion.AREACODE+"=" + "'"+formatNumber.substring(0, 3)+"'";
                uri =NumberRegion.CONTENT_URI;
                projection = new String[]{NumberRegion.PROVINCE,NumberRegion.CITY};
            }
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToNext()) {
                city = cursor.getString(0);
                cursor.close();
            }
    	}

        return city;
    }
  
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "intent=" + intent);
        if (!NumberLocateSetting.getSettingValue(context, NumberLocateSetting.RegionFuncKey)) {
            return;
        }
        mContext = context;
        if (intent == null)
            return;
        String action = intent.getAction();
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {// 拨出电话
        	isOutComingPhone = true;
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);          
            Log.v(TAG, "out going number:::" + phoneNumber);           
            cardOneState = MSimTelephonyManager.getDefault().getCallState(0);
            cardTwoState = MSimTelephonyManager.getDefault().getCallState(1);
            //mHandler.obtainMessage(NEW_OUTGOING_CALL, phoneNumber).sendToTarget();
            Log.v("PhoneStatusRecevier1", "callOneState="+cardOneState+"callTwoState="+cardTwoState);
        } else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {// 电话状态改变        	
        	cardOneState = MSimTelephonyManager.getDefault().getCallState(0);
            cardTwoState = MSimTelephonyManager.getDefault().getCallState(1);
            Log.v("PhoneStatusRecevier2", "callOneState="+cardOneState+"callTwoState="+cardTwoState);
            
            if(isOutComingPhone){
            	//phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                mHandler.obtainMessage(NEW_OUTGOING_CALL, phoneNumber).sendToTarget();
                if(cardOneState==0&&cardTwoState==0){
                	isOutComingPhone = false;
                	mHandler.obtainMessage(NEW_OUTGOING_CALL_STATE_IDLE).sendToTarget();
                }
            }
            
            if(cardOneState == TelephonyManager.CALL_STATE_RINGING || cardTwoState == TelephonyManager.CALL_STATE_RINGING){
            	isInComingPhone = true;
            	phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            	mHandler.obtainMessage(CALL_STATE_RINGING, phoneNumber).sendToTarget();
            }
            
            if(isInComingPhone){
            	if(cardOneState==0&&cardTwoState==0){
            		isInComingPhone = false;
            		mHandler.obtainMessage(CALL_STATE_IDLE).sendToTarget();
            	}
            	if(cardOneState==2&&cardTwoState==0){
            		//mHandler.obtainMessage(CALL_STATE_OFFHOOK).sendToTarget();
            	}
                if(cardOneState==0&&cardTwoState==2){
                	//mHandler.obtainMessage(CALL_STATE_OFFHOOK).sendToTarget();
            	}
            }
//            if(cardOneState==0){
//            	switch (cardTwoState) {
//                case TelephonyManager.CALL_STATE_RINGING://来电状态---1
//                    isInComingPhone = true;
//                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    mHandler.obtainMessage(CALL_STATE_RINGING, phoneNumber).sendToTarget();
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK://电话接通状态---2
//                    if (isInComingPhone) {
//                        Log.i(TAG, "incoming phone accept :" + phoneNumber);
//                        mHandler.obtainMessage(CALL_STATE_OFFHOOK).sendToTarget();
//                    }
//                    if (isOutComingPhone){
//                    	mHandler.obtainMessage(NEW_OUTGOING_CALL, phoneNumber).sendToTarget();
//                    }
//                    break;
//                case TelephonyManager.CALL_STATE_IDLE://电话挂断状态---0
//                    if (isInComingPhone) {
//                        Log.i(TAG, "incoming phone hangup");
//                        isInComingPhone = false;
//                    }
//                    if (isOutComingPhone){
//                    	isOutComingPhone = false;
//                    }
//                    mHandler.obtainMessage(CALL_STATE_IDLE).sendToTarget();
//                    break;
//                }
//            }
//            
//            if(cardOneState==1){
//            	switch (cardTwoState) {
//                case TelephonyManager.CALL_STATE_RINGING://来电状态---1
//                    isInComingPhone = true;
//                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    mHandler.obtainMessage(CALL_STATE_RINGING, phoneNumber).sendToTarget();
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK://电话接通状态---2
//                    if (isInComingPhone) {
//                        Log.i(TAG, "incoming phone accept :" + phoneNumber);
//                        mHandler.obtainMessage(CALL_STATE_OFFHOOK).sendToTarget();
//                    }
//                    if (isOutComingPhone){
//                    	mHandler.obtainMessage(NEW_OUTGOING_CALL, phoneNumber).sendToTarget();
//                    }
//                    break;
//                case TelephonyManager.CALL_STATE_IDLE://电话挂断状态---0
//                    if (isInComingPhone) {
//                        Log.i(TAG, "incoming phone hangup");
//                        isInComingPhone = false;
//                    }
//                    if  (isOutComingPhone && cardOneState ==0){
//                    	isOutComingPhone = false;
//                    }
//                    mHandler.obtainMessage(CALL_STATE_IDLE).sendToTarget();
//                    break;
//                }
//            }
//            if(cardOneState==2){
//            	switch (cardTwoState) {
//                case TelephonyManager.CALL_STATE_RINGING://来电状态---1
//                    isInComingPhone = true;
//                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    mHandler.obtainMessage(CALL_STATE_RINGING, phoneNumber).sendToTarget();
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK://电话接通状态---2
//                    if (isInComingPhone) {
//                        Log.i(TAG, "incoming phone accept :" + phoneNumber);
//                        mHandler.obtainMessage(CALL_STATE_OFFHOOK).sendToTarget();
//                    }
//                    if (isOutComingPhone){
//                    	mHandler.obtainMessage(NEW_OUTGOING_CALL, phoneNumber).sendToTarget();
//                    }
//                    break;
//                case TelephonyManager.CALL_STATE_IDLE://电话挂断状态---0
//                    if (isInComingPhone) {
//                        Log.i(TAG, "incoming phone hangup");
//                        isInComingPhone = false;
//                    }
//                    if  (isOutComingPhone && cardOneState ==0){
//                    	isOutComingPhone = false;
//                    }
//                    mHandler.obtainMessage(CALL_STATE_IDLE).sendToTarget();
//                    break;
//                }
//            }                        
        }
    }

    public static WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    static {
        // 将view放在最上层
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 让当前View失去焦点，让后面的界面获得焦点
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.RGBA_8888;// 背景透明
        params.gravity = Gravity.TOP | Gravity.CENTER_VERTICAL;
        params.x = 0;
        params.y = 170;
        params.width = LayoutParams.FILL_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
    }

    private static WindowManager mWm = null;
    private static LinearLayout toast = null;
    private static OnTouchListener listener = new OnTouchListener() {
        int downX,downY,rawX,rawY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            Log.i(TAG, "onTouch()--->action="+action);
            switch(action){
                case MotionEvent.ACTION_DOWN://0
                    downX = params.x;
                    downY = params.y;
                    rawX = (int)event.getRawX();//相对于整个屏幕
                    rawY = (int)event.getRawY();
                    Log.i(TAG, "onTouch()--->down x="+(int)event.getX()+",y="+(int)event.getY());//相对于自己这个view
                    break;
                case MotionEvent.ACTION_MOVE://2
                    int x = (int)event.getRawX();
                    int y = (int)event.getRawY();
                    params.x = downX + x - rawX;
                    params.y = downY + y - rawY;
                    if (toast != null) {
                        mWm.updateViewLayout(toast, params);
                    }
                    break;
                case MotionEvent.ACTION_UP://3
                    break;
            }
            return false;
        }
    };
    private static void showToast(Context context, String code, int cardid, String operator, String call_type) {
        if (TextUtils.isEmpty(code)) {
            code = "未知";
        	//return;
        }
        if(toast==null){
        	mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            toast = (LinearLayout)layoutInflater.inflate(R.layout.toast_layout, null);
            toast.setOnTouchListener(listener);
            if(cardid == 1){
            	((ImageView)toast.findViewById(R.id.imageView_cardid)).setImageResource(R.drawable.card1);
            }else{
            	((ImageView)toast.findViewById(R.id.imageView_cardid)).setImageResource(R.drawable.card2);
            }           
            ((TextView)toast.findViewById(R.id.city)).setText(code);
            ((TextView)toast.findViewById(R.id.operater)).setText(operator);
            ((TextView)toast.findViewById(R.id.call_type)).setText(call_type);
            mWm.addView(toast, params);
        }
        Log.v("test","showToast");
        
//        new Thread(){  
//            public void run(){  
//                System.out.println("Thread is running.");  
//                startRecording();
//            }  
//        }.start();
        
        
    }

    private static void startAnimation(final Context context) {
        if (toast != null) {
            MoveAnimation anim = new MoveAnimation(mHandler);
            anim.start();
        }
    }
    private static void clearToast(Context context) {
        Log.i(TAG, "clearToast()---");
        if (toast != null) {
            if (mWm == null) {
                mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            }
            mWm.removeView(toast);
        }
//        params.x = 0;
//        params.y = 0;
        toast = null;
    }
   
}
