package edu.bupt.mms.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.bupt.mms.R;


//import com.kl.android.ReadSMS.editSMS.sendListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class detail extends Activity{

	   
    private TextView tv_titlePeople;   
    private TextView tv_content,tv_date; 
    private String people;
    private String formalDate;
    String name;
    String address;
    String body;
    long date;
    int model_flag;
    public static boolean isReadable=false;
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) { 
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.detail);   
       
        tv_titlePeople = (TextView)findViewById(R.id.title);
        tv_content = (TextView)findViewById(R.id.content);
        tv_date = (TextView)findViewById(R.id.date);
        
     
        Intent intent =this.getIntent();
        
     
        Bundle data =  intent.getBundleExtra("sms");
        
       
        
		name = data.getString("name");
		
		address = data.getString("address");
		
		body =data.getString("body");
		
		date = data.getLong("date");
		
		model_flag=data.getInt("model_flag");
		
		//Date date2=new Date(date);
		
		String formalDate = MessageUtils.formatTimeStampString(
				detail.this, date);
			
		//SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      			
		//String formalDate=format.format(date);
		
		if(name==null&&address!=null){
		
			name=address;
			
		}
		if(name==null&&address==null){
			name="无名称";
			address="无地址";
					
		}
		
		if(address==null){
			address="无地址";
		}
		if(name==null){
			name="无名称";
		}
        
        if(!address.equals("无地址")){
		
		
		String strip1 = ConversationUtils.replacePattern(address,
				"^((\\+{0,1}86){0,1})", ""); // strip
		// +86
		String strip2 = ConversationUtils.replacePattern(strip1, "(\\-)",
				""); // strip
		// -
		String strip3 = ConversationUtils.replacePattern(strip2, "(\\ )",
				""); // strip
		String strip4 = ConversationUtils.replacePattern(strip3,
				"^((\\+{0,1}12520){0,1})", "");
		String number = strip4;
        
      			
		String lin_city = "";

		if (ConversationUtils.isReadable) {

			if (ConversationUtils.CityMap.get(number) != null) {

				lin_city = ConversationUtils.CityMap.get(number);

				Log.v("ComposeMessageActivity for test", "lin_city: "
						+ lin_city);

			} else {

				CityWorkDetailTask cityTask = new CityWorkDetailTask(number);

				cityTask.execute();

			}

		} else {

			lin_city = "";
		}
		
		
        
		
         
		if(model_flag==1){
			 tv_titlePeople.setText("发件人"+name+" "+lin_city);	
		}else{
			
			 tv_titlePeople.setText("收件人"+name+" "+lin_city);
	         
		}
        }   else{
        	
        	
        	if(model_flag==1){
   			 tv_titlePeople.setText("发件人"+name);	
   		}else{
   			
   			 tv_titlePeople.setText("收件人"+name);
   	         
   		}
        	
        }
      			
              
        tv_content.setText("   "+body);
              
              
        tv_date.setText(""+formalDate);
        	
        	
        	
      
    }	
    
    
    
    private class CityWorkDetailTask extends AsyncTask<Void, Void, String> {

    	String number;
    	
    	
    	
    	public CityWorkDetailTask(String number) {

			this.number = number;
			

		}
    	
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			
			
			detail.isReadable=false;
			
			
			
			if (ConversationUtils.CityMap.get(number) != null) {
				return ConversationUtils.CityMap.get(number);
			} else {
				number = number.trim();
				number = number.replace(" ", "");

				Log.v("ComposeMessageActivity for test", number);

				String strip1 = ConversationUtils.replacePattern(number,
						"^((\\+{0,1}86){0,1})", ""); // strip
				// +86
				String strip2 = ConversationUtils.replacePattern(strip1,
						"(\\-)", ""); // strip
				// -
				String strip3 = ConversationUtils.replacePattern(strip2,
						"(\\ )", ""); // strip
				// space
				String strip4 = ConversationUtils.replacePattern(strip3,
						"^((\\+{0,1}12520){0,1})", "");
				number = strip4;

				String city = ConversationUtils.startQuery(
						detail.this, number);

				Log.v("ComposeMessageActivity for test", "city: " + city);
				return city;
			}
			
			
		//	return null;
			
			
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
			
			
			ConversationUtils.CityMap.put(number, result);
			ConversationUtils.isReadable = true;
			
			if(model_flag==1){
				 tv_titlePeople.setText("发件人： "+name+" "+result);	
			}else{
				
				 tv_titlePeople.setText("收件人： "+name+" "+result);
		         
			}
			//super.onPostExecute(result);
		}
		
		
		
    	
    }
}
