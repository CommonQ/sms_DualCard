package edu.bupt.mms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import edu.bupt.mms.R;
import edu.bupt.mms.TestActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultActivity extends Activity {

	public static final String TAG = "SearchResultActivity";
	
	public ListView listview;
	ArrayList<HashMap<String, Object>> list= new ArrayList<HashMap<String, Object>>();
	ArrayList<SMSListItem> mSmsList=new ArrayList<SMSListItem>();
	public SimpleAdapter listItemAdapter;
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_result);
		
	
		 
		intent =this.getIntent();
		
		 
		mSmsList =(ArrayList<SMSListItem>) intent.getExtra("mlist");
		
		//BundleData bundleData= (BundleData) data.getSerializable("bundleData");
		
		
		 
		 for(int i=0;i<mSmsList.size();i++)
		 Log.v(TAG, "mSmsList: "+mSmsList.get(i).mName);
		
		 
		 listview = (ListView) findViewById(R.id.mylist);
		 
		 list = readSearchResult(mSmsList);
		 
		 listItemAdapter = new SimpleAdapter(this, list,    
	                R.layout.listitems, 
	                new String[] {"person_image","person","data"},    
	                new int[] {R.id.textView_network,R.id.textView_body, R.id.textView_date}   
	            );
			
			listview.setAdapter(listItemAdapter);
			
			
			
		       listview.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						// TODO Auto-generated method stub
						
					
						Boolean isConversation=(Boolean) intent.getExtra("ConversationList");
						
						
						if(isConversation!=null&&isConversation==true){
							
							
							Intent onClickIntent = new Intent(SearchResultActivity.this, ComposeMessageActivity.class);
			                
							
							long threadId = mSmsList.get(position).mThreadID;
							long selectedId = mSmsList.get(position).mID;
							
							
							onClickIntent.putExtra("thread_id",threadId);
			              //  onClickIntent.putExtra("highlight","�����");
			                onClickIntent.putExtra("select_id",selectedId);
			               
			                startActivity(onClickIntent);
							
							
							
							
						}else{
						
						Intent intent2 = new Intent();

						intent2.setClass(SearchResultActivity.this, detail.class);
						
						Bundle data = new Bundle();
						
						SMSListItem sms  =mSmsList.get(position);
						
						String name =sms.mName;
						
						String address = sms.mAddress;
						
						String body =sms.mBody;
						
						long date = sms.mDate;
						
						data.putString("name", name);
						data.putString("address", address);
						data.putString("body", body);
						data.putLong("date", date);
						//������Ҫ��
						data.putInt("model_flag", 1);
						intent2.putExtra("sms", data);
						
						Log.v("TraditionalActivity", "name: "+name+" address: "+address+" body: "+body+" date: "+date);
						
						startActivity(intent2);
						
						
						
						
						}
						
						
						
						
					}
		        	
		        });
		 
		
	}
	
	
	private ArrayList<HashMap<String, Object>> readSearchResult(ArrayList<SMSListItem> mlist){
		
		

		
		ArrayList<HashMap<String, Object>> list= new ArrayList<HashMap<String, Object>>();
		
		
		
		
		
		for(int i = 0; i < mlist.size(); ++i){
			
			
			
			HashMap<String, Object> item = new HashMap<String, Object>();
			SMSListItem sms  =mlist.get(i);
			//ContactItemTraditional contractcontact = helper.getContact(this, sms);
			
			int person_image;
  			 
			person_image = R.drawable.ic_contact_picture;
			
			item.put("person_image",person_image);
			
			
			

			
			

			
			if(sms.mName == null) {
   				
   				item.put("person", sms.mAddress+left(sms.mBody.toString(),26));
   				Log.v("ReadSMS for test", "contractcontact.mName=null");
   			}
   			else {
   				item.put("person", sms.mName+left(sms.mBody.toString(),26));
   				Log.v("ReadSMS for test", "contractcontact.mName����null");
   			}
			
			
			Date date=new Date(sms.mDate);
   			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   			String formalDate=format.format(date);

   			String ss=sms.mRead==1?"已读":"未读";
   			String dd=sms.mSubID==0?SearchResultActivity.this.getString(R.string.card_one):SearchResultActivity.this.getString(R.string.card_two);
   			
   			
   			
   			
   			item.put("data", "日期： "+formalDate+"\n"+ss+" "+dd);
   			Log.v("item",""+item);
   			list.add(item);
			
		}
		
		
		
		
//		int person_image = R.drawable.ic_contact_picture;
//		
//		item.put("person_image",person_image);
//		
//		item.put("person", "����");
//		
////		    Date date=new Date();
////			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////			String formalDate=format.format(date);
//		
//		    String	formalDate="2013-09-13";
//			item.put("data", "���ڣ�"+formalDate+"\n");
//			
//			Log.v("item",""+item);
//			list2.add(item);
//		
		return list;
	}
	
	
	
	 private static String left(String str, int byte_len) {  
	        if (str.getBytes().length < byte_len) {  
	            return str;  
	        }  
	   
	        String substr = str;  
	        while (substr.getBytes().length > byte_len) {  
	            substr = substr.substring(0, substr.length() - 1);  
	        }  
	        return substr;  
	    }  
	
	

}
