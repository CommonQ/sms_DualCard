package edu.bupt.mms.ui;

import java.util.ArrayList;
import java.util.Map;

import edu.bupt.mms.MmsApp;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;

class SMSItem {
	public static final String ID = "_id";
	public static final String THREAD = "thread_id";
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String BODY = "body";
	public static final String SUBJECT = "subject";
	public static final String SUB_ID="sub_id";

	public String mAddress;
	public String mBody;
	public String mSubject;
	public long mID;
	public long mThreadID;
	public long mDate;
	public long mRead;
	public long mPerson;
	public long mSubID;
	

	private static int mIdIdx;
	private static int mThreadIdx;
	private static int mAddrIdx;
	private static int mPersonIdx;
	private static int mDateIdx;
	private static int mReadIdx;
	private static int mBodyIdx;
	private static int mSubjectIdx;
	private static int mSubIDIdx;
	
	
	
	public SMSItem(){
		
	}

	public SMSItem(Cursor cur) {
		mID = cur.getLong(mIdIdx);
		mThreadID = cur.getLong(mThreadIdx);
		mAddress = cur.getString(mAddrIdx);
		mPerson = cur.getLong(mPersonIdx);
		mDate = cur.getLong(mDateIdx);
		mRead = cur.getLong(mReadIdx);
		mBody = cur.getString(mBodyIdx);
		mSubject = cur.getString(mSubjectIdx);
		mSubID=cur.getInt(mSubIDIdx);
	}
	
	public static void initIdx(Cursor cur) {
		mIdIdx = cur.getColumnIndex( ID );
		mThreadIdx = cur.getColumnIndex( THREAD );
		mAddrIdx = cur.getColumnIndex( ADDRESS );
		mPersonIdx = cur.getColumnIndex( PERSON );
		mDateIdx = cur.getColumnIndex( DATE );
		mReadIdx = cur.getColumnIndex( READ );
		mBodyIdx = cur.getColumnIndex( BODY );
		mSubjectIdx = cur.getColumnIndex( SUBJECT );
		mSubIDIdx=cur.getColumnIndex(SUB_ID);
	}
	
	public String toString() {
		String ret = ID + ":" + String.valueOf(mID) + " " +
			THREAD + ":" + String.valueOf(mThreadID) + " " +   
			ADDRESS + ":" + mAddress + " " + 
			PERSON + ":" + String.valueOf(mPerson) + " " + 
			DATE + ":" + String.valueOf(mDate) + " " +
			READ + ":" + String.valueOf(mRead) + " " + 
			SUBJECT + ":" + mSubject + " " + 
			BODY + ":" + mBody; 
		return ret;
	}
}

class ContactItem {
	public String mName;
}

public class SMSReader {
	 
    
	public Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	public Uri SMS_SENTBOX = Uri.parse("content://sms/sent");
	public Uri DRAFT_INBOX = Uri.parse("content://sms/draft");
	public Uri SMS_OUTBOX = Uri.parse("content://sms/outbox");
	public Uri SMS_FAILED = Uri.parse("content://sms/failed");
	public Uri SMS_INBOX1 = Uri.parse("content://mms-sms/conversations");
	//public Uri SMS_INBOX1 = Uri.parse("content://mms-sms/conversations?simple=true");
	
	
	
	private ArrayList<SMSItem> mSmsList = new ArrayList<SMSItem>();
	
	public static int unreadCount;
	
	
	public SMSReader() {
		
	}
	public static Cursor cur00;
	
	
	SMSItem get(int idx) {
		return mSmsList.get(idx);
	}
	
	int count() {
		return mSmsList.size();
	}
	
	int unReadCount(){
		
		return unreadCount;
	}
	
	
	String[] selectionArgs = { ReadSMS.input };
	//String[] selectionArgs = {"123"};//我加的

	String[] selectionArgsbody = { "'%"+ReadSMS.input+"%'" };
	
	 int read(Activity activity) {
		 
		
		 unreadCount=0;
		 
		 
Log.v("hehe","hehe");
		if(ReadSMS.flag==0){
			 if(ReadSMS.model_flag==1){
			 cur00 = activity.managedQuery(SMS_INBOX, null, null, null, "date DESC");
			 
			 cur00.moveToFirst();
			 do{
				 
				int read= cur00.getInt(cur00.getColumnIndex("read"));
				if(read==0){
					unreadCount++;
				}
				Log.v("hehe","read: "+read);
			 }while(cur00.moveToNext());
			 Log.v("hehe5","hehe5");
			 }
			 else if(ReadSMS.model_flag==2)
			 cur00 = activity.managedQuery(SMS_INBOX1, null, null, null, null);
			 
			 
			 else if(ReadSMS.model_flag==4){
				 cur00 = activity.managedQuery(SMS_SENTBOX, null, null, null, "date DESC");
			 
				// cur00 = activity.managedQuery(SMS_INBOX, null, null, null, "date DESC");
				 
				 cur00.moveToFirst();
				 
			 }
			 else if(ReadSMS.model_flag==5)
				 cur00 = activity.managedQuery(DRAFT_INBOX, null, null, null, "date DESC");
			 
			 else if(ReadSMS.model_flag==6)
				 cur00 = activity.managedQuery(SMS_OUTBOX, null, null, null, "date DESC");
			 else if(ReadSMS.model_flag==7)
				 cur00 = activity.managedQuery(SMS_FAILED, null, null, null, "date DESC");
			 
		Log.v("flag=0","flag=0");
		//ReadSMS.flag=1;
		}
		else if(ReadSMS.flag==1){
			Log.v("hehe1","hehe1");
			 if(ReadSMS.model_flag==1)
	             cur00 = activity.managedQuery(SMS_INBOX, null, "address" + "=?", selectionArgs, "date DESC");
	         else if(ReadSMS.model_flag==2)
	        	 cur00 = activity.managedQuery(SMS_INBOX1, null, "address" + "=?", selectionArgs, "date DESC");
			 
			 
	         else if(ReadSMS.model_flag==3){
	        	 Log.v("hehe2","hehe2");
			 
			 
			 selectionArgs[0] = ReadSMS.thread ;
			 cur00 = activity.managedQuery(SMS_INBOX, null, "address" + "=?", selectionArgs, "date DESC");
			 
			 //ReadSMS.model_flag=1;
	         }
			
	         
	        
			 
		    Log.v("flag=1",""+cur00.getCount());
		    ReadSMS.flag=0;
		}
		
		 
		 
		
		else if(ReadSMS.flag==2){
			if(ReadSMS.model_flag==1)
	         cur00 = activity.managedQuery(SMS_INBOX, null, "body" + " LIKE '%"+ReadSMS.input+"%'", null, "date DESC");
			else if(ReadSMS.model_flag==2)
			 cur00 = activity.managedQuery(SMS_INBOX1, null, "body" + " LIKE '%"+ReadSMS.input+"%'", null, "date DESC");
		    Log.v("flag=2",""+cur00.getCount());
		    ReadSMS.flag=0;
		}
		
		else if(ReadSMS.flag==4){
       	 Log.v("hehe4","hehe4");
       	if(ReadSMS.model_flag==1)
       		cur00 = activity.managedQuery(SMS_INBOX, null, "date" + " <= " + ReadSMS.cto.getTimeInMillis() + " and " + " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null, "date DESC");
       	
       	
       	
       	//QQ
       	else if(ReadSMS.model_flag==4)
       	cur00 = activity.managedQuery(SMS_SENTBOX, null, "date" + " <= " + ReadSMS.cto.getTimeInMillis() + " and " + " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null, "date DESC");
		    Log.v("flag=2",""+cur00.getCount());
		    ReadSMS.flag=0;
        }
		
		if( cur00 != null && 
			cur00.moveToFirst()) {
			SMSItem.initIdx(cur00);
			do {
				SMSItem item = new SMSItem(cur00);
				mSmsList.add(item);
			} while(cur00.moveToNext());
		}
		return count();
	}
	 
	 
	 
	 


		public String getPeopleTest(String mNumber,Context context){
			Uri uri = Uri.parse("content://com.android.contacts/data/phones/"); 
			Cursor cursor = context.getContentResolver().query(uri, new String[] {ContactsContract.Contacts.DISPLAY_NAME,
	        		ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.NUMBER+ " = '" + mNumber + "'", null, null);
			
			
			 if( cursor == null ) {  
				  
	             Log.d("SMSReader for test", "getPeople null");  
	 
	             return null;  
	 
	         }  
	 
	         Log.d("SMSReader for test", "getPeople cursor.getCount() = " + cursor.getCount());  
	 
	         String name=null;
	         
	         
	         
	         for( int i = 0; i < cursor.getCount(); i++ )  
	   		  
	         {  
	 
	             cursor.moveToPosition(i);  
	 
	                 
	 
	             // 取得联系人名字  
	 
	             int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);     
	 
	             name = cursor.getString(nameFieldColumnIndex);  
	 
	             Log.i("Contacts", "" + name + " .... " + nameFieldColumnIndex); // 这里提示 force close  
	 
	            // m_TextView.setText("联系人姓名：" + name);  
	 
	         }
	         
			
			
			return name;
			
		}
		

	 
	 
	 
	 
	 
		
		
		ContactItem getContact(Context context, SMSItem sms){
			
			String address=sms.mAddress;
			
			Log.v("SMSReader for test", "1 address: "+address);
			String name=null;
			
			
		
			
			if(address==null){
			
			ArrayList<Map<String,Object>> recipients_all = ConversationUtils.getRecipientsIds(context, sms.mThreadID);
			
			Log.v("SMSReader for test", "address等于null recipients_all: "+recipients_all.toString());
			
			StringBuilder sb = new StringBuilder();
		
			for(int i=0;i<recipients_all.size();i++){
				
				String number =(String) recipients_all.get(i).get("number");
				ContactItem	item=searchNames(number,name,context);
				if(item!=null){
					sb.append(item.mName+", ");
				}
				
			}
			
			if(sb.toString().equals("")){
				return null;
			}
			
			else{
				ContactItem	item= new ContactItem();
				item.mName=sb.toString();
				return item;
				
			}
		
			}
			
			
			else{
				
				ContactItem	item=searchNames(address,name,context);
				
				
				if(item==null){
					return null;
				}
				else{
				return item;
				}
			}
			

			
			
		}
		
		
		public ContactItem searchNames(String address,String name,Context context){
			String  normalized_address = PhoneNumberUtils.formatNumber(address,address,
                    MmsApp.getApplication().getCurrentCountryIso());
			
			
		
			 name=	getPeopleTest(normalized_address, context);
			
			Log.v("SMSReader for test", "2 address: "+address+" name: "+name);
			
			
			if(name==null){
				
				name=getPeopleTest(address, context);
				Log.v("SMSReader for test", "3 address: "+address+" name: "+name);
			}
			
			
			if(name==null){
				
				address=address.trim();
				name=getPeopleTest(address, context);
				Log.v("SMSReader for test", "4 address: "+address+" name: "+name);
			}
			
			if(name==null){
				
				address=address.replace(" ", "");
				name=getPeopleTest(address, context);
				Log.v("SMSReader for test", "5 address: "+address+" name: "+name);
				
			}
			
			if(name!=null){
				ContactItem item = new ContactItem();
			
				item.mName=name;
				
				return item;
			
			}
			else{
			return null;
			}
		}
		
		
		
	 
		
//		
//	ContactItem getContact(Activity activity, final SMSItem sms) {
//		Log.v("ReadSMS for test", "sms.mPerson: "+sms.mPerson);
//		
//		if(sms.mPerson == 0) return null;
//
//		
//		Cursor cur = activity.managedQuery(ContactsContract.Contacts.CONTENT_URI, 
//				new String[] {PhoneLookup.DISPLAY_NAME}, 
//				" _id=?", 
//				//" address=?", 
//				new String[] {String.valueOf(sms.mPerson)}, null);
//		if(cur != null &&
//			cur.moveToFirst()) {
//			int idx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
//			ContactItem item = new ContactItem();
//			item.mName = cur.getString(idx);
//			return item;
//		}
//		return null;
//	}
//


}
