package edu.bupt.mms.ui;

import java.util.ArrayList;
import java.util.Map;

import edu.bupt.mms.MmsApp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;











class SMSItemFailed extends SMSItem {
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
	
	


	public SMSItemFailed(Cursor cur) {
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



class ContactItemFailed extends ContactItem {
	public String mName;
}










public class SMSFailedReader {

	
	
	FailedBoxDBHelper dbHelper;
private ArrayList<SMSItemFailed> mSmsList = new ArrayList<SMSItemFailed>();
	
	public static int unreadCount;
	
public SMSFailedReader() {
		
	}
	
	
public static Cursor cur00;
SMSItemFailed get(int idx) {
	return mSmsList.get(idx);
}

int count() {
	return mSmsList.size();
}





public ContactItemFailed getFailedContact(Context context, SMSItemFailed sms){
	
	
	
	
	
	
	String address=sms.mAddress;
	
	Log.v("SMSReader for test", "1 address: "+address);
	String name=null;
	
	

	
	if(address==null){
	
	ArrayList<Map<String,Object>> recipients_all = ConversationUtils.getRecipientsIds(context, sms.mThreadID);
	
	Log.v("SMSReader for test", "address等于null recipients_all: "+recipients_all.toString());
	
	StringBuilder sb = new StringBuilder();

	for(int i=0;i<recipients_all.size();i++){
		
		String number =(String) recipients_all.get(i).get("number");
		ContactItemFailed	item=searchNames(number,name,context);
		if(item!=null){
			sb.append(item.mName+", ");
		}
		
	}
	
	if(sb.toString().equals("")){
		return null;
	}
	
	else{
		ContactItemFailed	item= new ContactItemFailed();
		item.mName=sb.toString();
		return item;
		
	}

	}
	
	
	else{
		
		ContactItemFailed	item=searchNames(address,name,context);
		
		
		if(item==null){
			return null;
		}
		else{
		return item;
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
}














public ContactItemFailed searchNames(String address,String name,Context context){
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
		ContactItemFailed item = new ContactItemFailed();
	
		item.mName=name;
		
		return item;
	
	}
	else{
	return null;
	}
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








int unReadCount(){
	
	return unreadCount;
}

	
	
String[] selectionArgs = { ReadSMS.input };
//String[] selectionArgs = {"123"};//我加的

String[] selectionArgsbody = { "'%"+ReadSMS.input+"%'" };

	

int read(Activity activity) {
	
	dbHelper = new FailedBoxDBHelper(activity,"myBox.db3",1);
	
	SQLiteDatabase db = dbHelper.getWritableDatabase();
	
	 unreadCount=0;
	 
	 
	 cur00=db.query("failedbox", null,null, null, null, null, null);
	 
	 
	
	
	 if( cur00 != null && 
				cur00.moveToFirst()) {
				SMSItemFailed.initIdx(cur00);
				do {
					SMSItemFailed item = new SMSItemFailed(cur00);
					mSmsList.add(item);
				} while(cur00.moveToNext());
			}
			
	 
	 
	 
	 
	 return count();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
}
