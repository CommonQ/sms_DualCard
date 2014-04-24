package edu.bupt.mms.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.mms.MmsApp;
import edu.bupt.mms.data.Contact;
import edu.bupt.mms.numberlocate.NumberLocateProvider.NumberRegion;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

public class SMSHelper {

	
	public static final String TAG="SMSHelper";
	
	public static Uri ALL_INBOX = Uri.parse("content://sms/");
	public static Uri ALL_MMS_INBOX = Uri.parse("content://mms/");
	
	//ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
	
	public static int unreadCount_IN;
	public static int unreadCount_IN_MMS;
	

	public static int unreadCount_OUT;
	public static int unreadCount_OUT_MMS;

	public static int unreadCount_DRAFT;
	public static int unreadCount_DRAFT_MMS;
	

	public static int unreadCount_SEND;
	public static int unreadCount_SEND_MMS;
	

	public static int unreadCount_RUBBISH;
	public static int unreadCount_RUBBISH_MMS;
	
	
	
	
	
	
	

	public static int readCount_IN;
	public static int readCount_IN_MMS;
	

	public static int readCount_OUT;
	public static int readCount_OUT_MMS;
	

	public static int readCount_DRAFT;
	public static int readCount_DRAFT_MMS;
	

	public static int readCount_SEND;
	public static int readCount_SEND_MMS;
	

	public static int readCount_RUBBISH;
	public static int readCount_RUBBISH_MMS;
	
	
	
	
	
	
	
	public static int unreadCount_IN_SUB1=0;
	public static int unreadCount_IN_SUB2=0;
	

	public static int readCount_IN_SUB1=0;
	public static int readCount_IN_SUB2=0;
	
	public static int unreadCount_OUT_SUB1=0;
	public static int unreadCount_OUT_SUB2=0;
	
	public static int readCount_OUT_SUB1=0;
	public static int readCount_OUT_SUB2=0;
	
	public static int unreadCount_DRAFT_SUB1=0;
	public static int unreadCount_DRAFT_SUB2=0;
	
	
	public static int readCount_DRAFT_SUB1=0;
	public static int readCount_DRAFT_SUB2=0;
	
	
	public static int unreadCount_SEND_SUB1=0;
	public static int unreadCount_SEND_SUB2=0;
	
	
	public static int readCount_SEND_SUB1=0;
	public static int readCount_SEND_SUB2=0;
	
	
	public static int unreadCount_RUBBISH_SUB1=0;
	public static int unreadCount_RUBBISH_SUB2=0;
	

	public static int readCount_RUBBISH_SUB1=0;
	public static int readCount_RUBBISH_SUB2=0;;
	
	
	
	
	
	public SMSHelper(){
		
	}
	
	
	
	HashMap<String, ArrayList<SMSListItem>> readAll(Context context){

		HashMap<String ,ArrayList<SMSListItem> > all_list = new HashMap<String ,ArrayList<SMSListItem> >();
			
		
		 ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		 
		 mSmsList=read(context);
		 
		// mSmsList=readMMS(context);
		 
		 all_list.put("inbox", mSmsList);
		 
		
		 ArrayList<SMSListItem> mSmsList2 = new ArrayList<SMSListItem>();
		
		
		 mSmsList2=read2(context);
		// mSmsList2=readMMS(context);
		 
		 all_list.put("outbox", mSmsList2);
		 
		 
		 ArrayList<SMSListItem> mSmsList3 = new ArrayList<SMSListItem>();
		 
		 mSmsList3=read3(context);
		// mSmsList3=readMMS(context);
		 
		 all_list.put("draftbox", mSmsList3);
		 
		 
 
		 ArrayList<SMSListItem> mSmsList4 = new ArrayList<SMSListItem>();
		 
		 mSmsList4=read4(context);
		 
		// mSmsList4=readMMS(context);
		 all_list.put("sendbox", mSmsList4);
		 
		 
		 
ArrayList<SMSListItem> mSmsList5 = new ArrayList<SMSListItem>();
		 
		 mSmsList5=read5(context);
		 Log.v("TraditionalActivity", "mSmsList5: "+mSmsList5.size());

        // mSmsList5=readMMS(context);
		 all_list.put("rubbishbox", mSmsList5);
		 
		 
 
		 ArrayList<SMSListItem> mSmsList_mms = new ArrayList<SMSListItem>();
		 
 

		 mSmsList_mms=readMMS(context);
		 
		// mSmsList=readMMS(context);
		 
		 all_list.put("inbox_mms", mSmsList_mms);
		 
		 
		 
		 
		 
		 
		 ArrayList<SMSListItem> mSmsList_mms2 = new ArrayList<SMSListItem>();
		 
		 

		 mSmsList_mms2=readMMS2(context);
		 
		// mSmsList=readMMS(context);
		 
		 all_list.put("outbox_mms", mSmsList_mms2);
		 
		 
		 
		 
		 ArrayList<SMSListItem> mSmsList_mms3 = new ArrayList<SMSListItem>();
		 
		 

		 mSmsList_mms3=readMMS3(context);
		 
		// mSmsList=readMMS(context);
		 
		 all_list.put("draftbox_mms", mSmsList_mms3);
		 
		 
		 
		 ArrayList<SMSListItem> mSmsList_mms4 = new ArrayList<SMSListItem>();
		 
		 

		 mSmsList_mms4=readMMS4(context);
		 
		// mSmsList=readMMS(context);
		 
		 all_list.put("sendbox_mms", mSmsList_mms4);
		 
		 
		 ArrayList<SMSListItem> mSmsList_mms5 = new ArrayList<SMSListItem>();
		 
		 

		 mSmsList_mms5=readMMS5(context);
		 
		// mSmsList=readMMS(context);
		 
		 //zaizhe ������ȥдrubbish���ŵ���ݿ�
		 all_list.put("rubbishbox_mms", mSmsList_mms5);
		 
		 
		 
		 ArrayList<SMSListItem> mSmsList_all = new ArrayList<SMSListItem>();
		 mSmsList_all =sortDate(mSmsList,mSmsList_mms);
		 all_list.put("inbox_all", mSmsList_all);
		 
		 
		 ArrayList<SMSListItem> mSmsList_all2 = new ArrayList<SMSListItem>();
		 mSmsList_all2 =sortDate(mSmsList2,mSmsList_mms2);
		 all_list.put("outbox_all", mSmsList_all2);
		 
		 
		 ArrayList<SMSListItem> mSmsList_all3 = new ArrayList<SMSListItem>();
		 mSmsList_all3 =sortDate(mSmsList3,mSmsList_mms3);
		 all_list.put("draftbox_all", mSmsList_all3);
		 
		 ArrayList<SMSListItem> mSmsList_all4 = new ArrayList<SMSListItem>();
		 mSmsList_all4 =sortDate(mSmsList4,mSmsList_mms4);
		 all_list.put("sendbox_all", mSmsList_all4);
		 
		 ArrayList<SMSListItem> mSmsList_all5 = new ArrayList<SMSListItem>();
		 mSmsList_all5 =sortDate(mSmsList5,mSmsList_mms5);
		 all_list.put("rubbishbox_all", mSmsList_all5);
		 
		 
		 
		 
		 
		 
		 
		 
		 
		return all_list;
	}
	
	
	
	
	public ArrayList<SMSListItem> sortDate(ArrayList<SMSListItem>  mlist1, ArrayList<SMSListItem> mlist2){
		
		
		ArrayList<SMSListItem> whole_list = new ArrayList<SMSListItem>();
		
		int length=0;
		
		int list1Size=0;
		
		int list2Size=0;
		
		
		length=mlist1.size()+mlist2.size();
		
		Log.v(TAG, "length: "+length);
		
		if(length==0){
			return whole_list;
		}else{
			
			
			for(int i=0; i<length; i++){
				
				if(list1Size>=mlist1.size()&&list2Size<mlist2.size()){
					
					whole_list.add(mlist2.get(list2Size));
					
					list2Size++;
					
					
					
					
				}else if(list2Size>=mlist2.size()&&list1Size<mlist1.size()){
					
					whole_list.add(mlist1.get(list1Size));
					
					list1Size++;
					
					
				}else if((long)mlist1.get(list1Size).mDate>=(long)mlist2.get(list2Size).mDate){
					
					
					
					whole_list.add(mlist1.get(list1Size));
					
					list1Size++;
					
					
					
				}else{
					
					
					whole_list.add(mlist2.get(list2Size));
					
					list2Size++;
					
					
					
				}
				
				
				
				
				
				
				
				
				
				
				
			}
			
			
			
			
			
			
			
		}
		
		
		
		
		
		
		
		
		
		
		return whole_list;
		
		
		
		
	}
	
	
	
//	
//	public ArrayList<SMSListItem> readDate(Context context ,int type) {
//		// TODO Auto-generated method stub
//		
//		 ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
//		 
//		 
//		 
//		 
//		 
//			try{
//				Cursor cursor =context.getContentResolver().query(ALL_INBOX, null, "type = ?",projection, "date DESC");
//				
//				
//				if(cursor!=null){
//					readCount_SEND=cursor.getCount();
//					cursor.moveToFirst();
//					
//					do{
//						
//						
//						
//
//						SMSItemTraditional item = new SMSItemTraditional();
//						
//						
//						SMSListItem listitem = item.getItems(cursor);
//						
//						Log.v("SMSHelper for test", "listitem: ");
//						
//					
//						
//
////						if(listitem.mRead==0){
////							
////							unreadCount_SEND++;
////							
////						}
//						
//						
//						Contact cacheContact = Contact.get(listitem.mAddress,true);
//						if (cacheContact != null) {
//						
//						String	mName = cacheContact.getName();
//							
//							Log.v("SMSHelper for test", "mName : "+ mName);
//							
//							listitem.mName=mName;
//						
//						}else{
//							
//							
//							ContactItemTraditional contractcontact =new ContactItemTraditional();
//							//	getContact(context, listitem);
//						
//						contractcontact=getContact(context, listitem);
//							
//						
//						if(contractcontact!=null){
//							
//							//contractcontact=getContact(context, listitem);
//						
//						
//						
//						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
//						
//						
//						listitem.mName=contractcontact.mName;
//						
//						
//						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
//						
//						
//						
//						
//						}
//							
//						}
//						
//						
//						
//						
//						mSmsList.add(listitem);
//						
//						
//					}while(cursor.moveToNext());
//					
//					
//				}
//				
//				
//				
//				cursor.close();
//				
//				
//				}
//		 
//		 
//		
//		return null;
//	}
//
//	
	
	
	
	

ArrayList<SMSListItem> read5(Context context) {
	
	FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(context,"myBox.db3",1);
	
	SQLiteDatabase db = dbHelper.getWritableDatabase();
	
	unreadCount_RUBBISH=0;
	readCount_RUBBISH=0;
	
	ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
	 Cursor cursor=db.query("failedbox", null,null, null, null, null, null);
	 
	 Log.v("TraditionalActivity", "readCount_RUBBISH: "+readCount_RUBBISH);
	
	 if(cursor!=null&&cursor.getCount()!=0){
			readCount_RUBBISH=cursor.getCount();
			
			
			Log.v("TraditionalActivity", "cursor!=null readCount_RUBBISH: "+readCount_RUBBISH);
			cursor.moveToFirst();
			
			do{
				
				
				

				SMSItemTraditional item = new SMSItemTraditional();
				
				
				SMSListItem listitem = item.getItems(cursor);
				
				listitem.isSMS=true;
				
				Log.v("SMSHelper for test", "listitem: ");
				
			
				

//				if(listitem.mRead==0){
//					
//					unreadCount_SEND++;
//					
//				}
				
				
				Contact cacheContact = Contact.get(listitem.mAddress,true);
				if (cacheContact != null) {
				
				String	mName = cacheContact.getName();
					
					Log.v("SMSHelper for test", "mName : "+ mName);
					
					listitem.mName=mName;
				
				}else{
					
					
					ContactItemTraditional contractcontact =new ContactItemTraditional();
					//	getContact(context, listitem);
				
				contractcontact=getContact(context, listitem);
					
				
				if(contractcontact!=null){
					
					//contractcontact=getContact(context, listitem);
				
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				listitem.mName=contractcontact.mName;
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				
				
				}
					
				}
				
				
				
				
				mSmsList.add(listitem);
				
				
			}while(cursor.moveToNext());
			cursor.close();
			cursor=null;
			
	 
	 
	 
	 }
	
	// readCount_RUBBISH=0;
	 
	 cursor.close();
	
	 db.close();
		



return mSmsList;


	
	
	
	
	
	
	
	
	
	
	
	
}
	

	
	
	

	ArrayList<SMSListItem> read4(Context context){
		

		
		

		 ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		// unreadCount_SEND=0;
		 readCount_SEND=0;
		 
		 
		 
		String[] projection = new String[]{"4"};
		
		
		try{
		Cursor cursor =context.getContentResolver().query(ALL_INBOX, null, "type = ?",projection, "date DESC");
		
		
		if(cursor!=null&&cursor.getCount()!=0){
			readCount_SEND=cursor.getCount();
			cursor.moveToFirst();
			
			do{
				
				
				

				SMSItemTraditional item = new SMSItemTraditional();
				
				
				SMSListItem listitem = item.getItems(cursor);
				
				
				listitem.isSMS=true;
				Log.v("SMSHelper for test", "listitem: ");
				
			
				

//				if(listitem.mRead==0){
//					
//					unreadCount_SEND++;
//					
//				}
				
				
				
				Contact cacheContact = Contact.get(listitem.mAddress,true);
				if (cacheContact != null) {
				
				String	mName = cacheContact.getName();
					
					Log.v("SMSHelper for test", "mName : "+ mName);
					
					listitem.mName=mName;
				
				}else{
					
					
					ContactItemTraditional contractcontact =new ContactItemTraditional();
					//	getContact(context, listitem);
				
				contractcontact=getContact(context, listitem);
					
				
				if(contractcontact!=null){
					
					//contractcontact=getContact(context, listitem);
				
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				listitem.mName=contractcontact.mName;
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				
				
				}
					
				}
				
				
				
				
				mSmsList.add(listitem);
				
				
			}while(cursor.moveToNext());
			cursor.close();
			cursor=null;
			
			
		}
		
		
		
		cursor.close();
		
		
		}
		
		catch(Exception e){
			
			
			Log.e(TAG, "Error in cursor!!");
			
			
			
		}
		
		
		
		
		return mSmsList;
		
		
		
		
		
	
		
		
	}
	
	
	

	ArrayList<SMSListItem> read3(Context context){
		

		
		

		 ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		// unreadCount_DRAFT=0;
		 readCount_DRAFT=0;
		 
		 
		String[] projection = new String[]{"3"};
		
		
		try{
		Cursor cursor =context.getContentResolver().query(ALL_INBOX, null, "type = ?",projection, "date DESC");
		
		
		if(cursor!=null&&cursor.getCount()!=0){
			readCount_DRAFT=cursor.getCount();
			cursor.moveToFirst();
			
			do{
				
				
				

				SMSItemTraditional item = new SMSItemTraditional();
				
				
				SMSListItem listitem = item.getItems(cursor);
				
				listitem.isSMS=true;
				
				
				
				Log.v("SMSHelper for test", "listitem: ");
				
				
				
				

//				if(listitem.mRead==0){
//					
//					unreadCount_DRAFT++;
//					
//				}
				
				
				Contact cacheContact = Contact.get(listitem.mAddress,true);
				if (cacheContact != null) {
				
				String	mName = cacheContact.getName();
					
					Log.v("SMSHelper for test", "mName : "+ mName);
					
					listitem.mName=mName;
				
				}else{
					
					
					ContactItemTraditional contractcontact =new ContactItemTraditional();
					//	getContact(context, listitem);
				
				contractcontact=getContact(context, listitem);
					
				
				if(contractcontact!=null){
					
					//contractcontact=getContact(context, listitem);
				
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				listitem.mName=contractcontact.mName;
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				
				
				}
					
				}
				
				
				
				mSmsList.add(listitem);
				
				
			}while(cursor.moveToNext());
			cursor.close();
			cursor=null;
			
			
		}
		
		
		for(int i=0;i<mSmsList.size();i++){
			
			SMSListItem item=	mSmsList.get(i);
			long ThreadId=item.mThreadID;
			long date = item.mDate;
			for(int j=i+1;j<mSmsList.size();j++){
				if(ThreadId==mSmsList.get(j).mThreadID){
					
					if(date>=mSmsList.get(j).mDate){
						mSmsList.remove(j);
					}else{
						mSmsList.remove(i);
					}
					
				}
				
			}
			
		}
		cursor.close();
		
		
		}
		
		catch(Exception e){
			
			
			Log.e(TAG, "Error in cursor!!");
			
			
			
		}
		
		
		
		
		return mSmsList;
		
		
		
		
		
	
		
		
	}
	
	
	
	
	
	ArrayList<SMSListItem> read2(Context context){
		

		
		

		 ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		// unreadCount_OUT=0;
		 readCount_OUT=0;
		 
		 
		String[] projection = new String[]{"2"};
		
		
		try{
		Cursor cursor =context.getContentResolver().query(ALL_INBOX, null, "type = ?",projection, "date DESC");
		
		
		if(cursor!=null&&cursor.getCount()!=0){
			readCount_OUT=cursor.getCount();
			cursor.moveToFirst();
			
			do{
				
				
				

				SMSItemTraditional item = new SMSItemTraditional();
				
				
				SMSListItem listitem = item.getItems(cursor);
				
				listitem.isSMS=true;
				
				Log.e("SMSHelper for test", "listitem: ");
				
				

//				if(listitem.mRead==0){
//					
//					unreadCount_OUT++;
//					
//				}

				
				
				
				Contact cacheContact = Contact.get(listitem.mAddress,true);
				if (cacheContact != null) {
				
				String	mName = cacheContact.getName();
					
					Log.e("SMSHelper for test", "mName : "+ mName);
					
					listitem.mName=mName;
				
				}else{
					
					
					ContactItemTraditional contractcontact =new ContactItemTraditional();
					//	getContact(context, listitem);
				
				contractcontact=getContact(context, listitem);
					
				
				if(contractcontact!=null){
					
					//contractcontact=getContact(context, listitem);
				
				
				
				Log.e("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				listitem.mName=contractcontact.mName;
				
				
				Log.e("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				
				
				}
					
				}
				
				
				
				
				
				
				
				mSmsList.add(listitem);
				
				
			}while(cursor.moveToNext());
			
			cursor.close();
			cursor=null;
		}
		
		
		
		cursor.close();
		
		
		}
		
		catch(Exception e){
			
			
			Log.e(TAG, "Error in cursor!!");
			
			
			
		}
		
		
		
		
		return mSmsList;
		
		
		
		
		
	
		
		
	}
	
	
	
	
	
	ArrayList<SMSListItem> readMMS(Context context){
		
		
		ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		 unreadCount_IN_MMS=0;
		 readCount_IN_MMS=0;
		
		
		 String[] projection = new String[]{"1"};
		
		 
		 
		 
		
		
		
		 try{
				Cursor cursor =context.getContentResolver().query(ALL_MMS_INBOX, null, "msg_box = ?",projection, "date DESC");
				
				
				if(cursor!=null&&cursor.getCount()!=0){
					readCount_IN_MMS=cursor.getCount();
					cursor.moveToFirst();
					
					do{
						
						
						

						SMSItemTraditional item = new SMSItemTraditional();
						
						
						SMSListItem listitem = item.getMMSItems(context, cursor);
						
						
						listitem.isSMS=false;
						
						if(listitem.mRead==0){
							
							unreadCount_IN_MMS++;
							
						}
						
						Log.v("SMSHelper for test", "listitem: ");
						
						
						
						
						
						
						Contact cacheContact = Contact.get(listitem.mAddress,true);
						if (cacheContact != null) {
						
						String	mName = cacheContact.getName();
							
							Log.v("SMSHelper for test", "mName : "+ mName);
							
							listitem.mName=mName;
						
						}else{
							
							
							ContactItemTraditional contractcontact =new ContactItemTraditional();
							//	getContact(context, listitem);
						
						contractcontact=getContact(context, listitem);
							
						
						if(contractcontact!=null){
							
							//contractcontact=getContact(context, listitem);
						
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						listitem.mName=contractcontact.mName;
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						
						
						}
							
						}
						
						
						
						
						
						
						
						
						
						
						
						
						mSmsList.add(listitem);
						
						
					}while(cursor.moveToNext());
					
					cursor.close();
					cursor=null;
				}
				
				
				
				cursor.close();
				
				
				}
				
				catch(Exception e){
					
					
					Log.e(TAG, "Error in cursor!!");
					
					
					
				}
				
				
				
				
				return mSmsList;
				
		
		
	}
	
	

	ArrayList<SMSListItem> readMMS2(Context context){
		
		
		ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		 unreadCount_OUT_MMS=0;
		 readCount_OUT_MMS=0;
		
		
		 String[] projection = new String[]{"2"};
		
		 
		 
		 
		
		
		
		 try{
				Cursor cursor =context.getContentResolver().query(ALL_MMS_INBOX, null, "msg_box = ?",projection, "date DESC");
				
				
				if(cursor!=null&&cursor.getCount()!=0){
					readCount_OUT_MMS=cursor.getCount();
					cursor.moveToFirst();
					
					do{
						
						
						

						SMSItemTraditional item = new SMSItemTraditional();
						
						
						SMSListItem listitem = item.getMMSItems(context, cursor);
						
						
						listitem.isSMS=false;
						
						if(listitem.mRead==0){
							
							unreadCount_OUT_MMS++;
							
						}
						
						Log.v("SMSHelper for test", "listitem: ");
						
						
						
						
						
						
						Contact cacheContact = Contact.get(listitem.mAddress,true);
						if (cacheContact != null) {
						
						String	mName = cacheContact.getName();
							
							Log.v("SMSHelper for test", "mName : "+ mName);
							
							listitem.mName=mName;
						
						}else{
							
							
							ContactItemTraditional contractcontact =new ContactItemTraditional();
							//	getContact(context, listitem);
						
						contractcontact=getContact(context, listitem);
							
						
						if(contractcontact!=null){
							
							//contractcontact=getContact(context, listitem);
						
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						listitem.mName=contractcontact.mName;
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						
						
						}
							
						}
						
						
						
						
						
						
						
						
						
						
						
						
						mSmsList.add(listitem);
						
						
					}while(cursor.moveToNext());
					cursor.close();
					cursor=null;
					
				}
				
				
				
				cursor.close();
				
				
				}
				
				catch(Exception e){
					
					
					Log.e(TAG, "Error in cursor!!");
					
					
					
				}
				
				
				
				
				return mSmsList;
				
		
		
	}
	
	
	

	ArrayList<SMSListItem> readMMS3(Context context){
		
		
		ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		 unreadCount_DRAFT_MMS=0;
		 readCount_DRAFT_MMS=0;
		
		
		 String[] projection = new String[]{"3"};
		
		 
		 
		 
		
		
		
		 try{
				Cursor cursor =context.getContentResolver().query(ALL_MMS_INBOX, null, "msg_box = ?",projection, "date DESC");
				
				
				if(cursor!=null&&cursor.getCount()!=0){
					readCount_DRAFT_MMS=cursor.getCount();
					cursor.moveToFirst();
					
					do{
						
						
						

						SMSItemTraditional item = new SMSItemTraditional();
						
						
						SMSListItem listitem = item.getMMSItems(context, cursor);
						
						listitem.isSMS=false;
						
						
						if(listitem.mRead==0){
							
							unreadCount_DRAFT_MMS++;
							
						}
						
						Log.v("SMSHelper for test", "listitem: ");
						
						
						
						
						
						
						Contact cacheContact = Contact.get(listitem.mAddress,true);
						if (cacheContact != null) {
						
						String	mName = cacheContact.getName();
							
							Log.v("SMSHelper for test", "mName : "+ mName);
							
							listitem.mName=mName;
						
						}else{
							
							
							ContactItemTraditional contractcontact =new ContactItemTraditional();
							//	getContact(context, listitem);
						
						contractcontact=getContact(context, listitem);
							
						
						if(contractcontact!=null){
							
							//contractcontact=getContact(context, listitem);
						
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						listitem.mName=contractcontact.mName;
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						
						
						}
							
						}
						
						
						
						
						
						
						
						
						
						
						
						
						mSmsList.add(listitem);
						
						
					}while(cursor.moveToNext());
					cursor.close();
					cursor=null;
					
				}
				
				
				
				cursor.close();
				
				
				}
				
				catch(Exception e){
					
					
					Log.e(TAG, "Error in cursor!!");
					
					
					
				}
				
				
				
				
				return mSmsList;
				
		
		
	}
	

	ArrayList<SMSListItem> readMMS4(Context context){
		
		
		ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		 unreadCount_SEND_MMS=0;
		 readCount_SEND_MMS=0;
		
		
		 String[] projection = new String[]{"4"};
		
		 
		 
		 
		
		
		
		 try{
				Cursor cursor =context.getContentResolver().query(ALL_MMS_INBOX, null, "msg_box = ?",projection, "date DESC");
				
				
				if(cursor!=null&&cursor.getCount()!=0){
					readCount_SEND_MMS=cursor.getCount();
					cursor.moveToFirst();
					
					do{
						
						
						

						SMSItemTraditional item = new SMSItemTraditional();
						
						
						SMSListItem listitem = item.getMMSItems(context, cursor);
						
						
						
						listitem.isSMS=false;
						
						if(listitem.mRead==0){
							
							unreadCount_SEND_MMS++;
							
						}
						
						Log.v("SMSHelper for test", "listitem: ");
						
						
						
						
						
						
						Contact cacheContact = Contact.get(listitem.mAddress,true);
						if (cacheContact != null) {
						
						String	mName = cacheContact.getName();
							
							Log.v("SMSHelper for test", "mName : "+ mName);
							
							listitem.mName=mName;
						
						}else{
							
							
							ContactItemTraditional contractcontact =new ContactItemTraditional();
							//	getContact(context, listitem);
						
						contractcontact=getContact(context, listitem);
							
						
						if(contractcontact!=null){
							
							//contractcontact=getContact(context, listitem);
						
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						listitem.mName=contractcontact.mName;
						
						
						Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
						
						
						
						
						}
							
						}
						
						
						
						
						
						
						
						
						
						
						
						
						mSmsList.add(listitem);
						
						
					}while(cursor.moveToNext());
					
					cursor.close();
					cursor=null;
				}
				
				
				
				cursor.close();
				
				
				}
				
				catch(Exception e){
					
					
					Log.e(TAG, "Error in cursor!!");
					
					
					
				}
				
				
				
				
				return mSmsList;
				
		
		
	}
	
	
	ArrayList<SMSListItem> readMMS5(Context context){
		// ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		 unreadCount_RUBBISH_MMS=0;
		 readCount_RUBBISH_MMS=0;
	//	return mSmsList;
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

		
		FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(context,"myBox.db3",1);
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		 unreadCount_RUBBISH_MMS=0;
		 readCount_RUBBISH_MMS=0;
		 
		ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		 Cursor cursor=db.query("mmsfailedbox", null,null, null, null, null, null);
		 
		 
		
		 if(cursor!=null&&cursor.getCount()!=0){
			 readCount_RUBBISH_MMS=cursor.getCount();
				cursor.moveToFirst();
				
				do{
					
					
					

					SMSItemTraditional item = new SMSItemTraditional();
					
					
					SMSListItem listitem = item.getFailedMMSItems(context,cursor);
					
					
				
					
					
					
					
					listitem.isSMS=false;
					
					Log.v("SMSHelper for test", "listitem: ");
					
				
					

//					if(listitem.mRead==0){
//						
//						unreadCount_SEND++;
//						
//					}
					
					
					Contact cacheContact = Contact.get(listitem.mAddress,true);
					if (cacheContact != null) {
					
					String	mName = cacheContact.getName();
						
						Log.v("SMSHelper for test", "mName : "+ mName);
						
						listitem.mName=mName;
					
					}else{
						
						
						ContactItemTraditional contractcontact =new ContactItemTraditional();
						//	getContact(context, listitem);
					
					contractcontact=getContact(context, listitem);
						
					
					if(contractcontact!=null){
						
						//contractcontact=getContact(context, listitem);
					
					
					
					Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
					
					
					listitem.mName=contractcontact.mName;
					
					
					Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
					
					
					
					
					}
						
					}
					
					
					
					
					mSmsList.add(listitem);
					
					
				}while(cursor.moveToNext());
				cursor.close();
				cursor=null;
		 
		 
		 
		 }
		
		
		 
		 cursor.close();
		
		 db.close();
			



	return mSmsList;


		
		
		
		
		
		
		
		
		
		
		
		

	}
	
	
	ArrayList<SMSListItem> read(Context context){
		
		

		 ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();
		
		 unreadCount_IN=0;
		 readCount_IN=0;
		 
		 
		String[] projection = new String[]{"1"};
		
		
		try{
		Cursor cursor =context.getContentResolver().query(ALL_INBOX, null, "type = ?",projection, "date DESC");
		//010 want2learn
		
		if(cursor!=null&&cursor.getCount()!=0){
			readCount_IN=cursor.getCount();
			cursor.moveToFirst();
			
			do{
				
				
				

				SMSItemTraditional item = new SMSItemTraditional();
				
				
				SMSListItem listitem = item.getItems(cursor);
				listitem.isSMS=true;
				
				if(listitem.mRead==0){
					
					unreadCount_IN++;
					
				}
				
				Log.v("SMSHelper for test", "listitem: ");
				
				
				
				
				
				
				Contact cacheContact = Contact.get(listitem.mAddress,true);
				if (cacheContact != null) {
				
				String	mName = cacheContact.getName();
					
					Log.v("SMSHelper for test", "mName : "+ mName);
					
					listitem.mName=mName;
				
				}else{
					
					
					ContactItemTraditional contractcontact =new ContactItemTraditional();
					//	getContact(context, listitem);
				
				contractcontact=getContact(context, listitem);
					
				
				if(contractcontact!=null){
					
					//contractcontact=getContact(context, listitem);
				
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				listitem.mName=contractcontact.mName;
				
				
				Log.v("SMSHelper for test", "contractcontact.mName: "+contractcontact.mName);
				
				
				
				
				}
					
				}
				
				
				
				
				
				
				
				
				
				
				
				
				mSmsList.add(listitem);
				
				
			}while(cursor.moveToNext());
			
			cursor.close();
			cursor=null;
		}
		
		
		
		cursor.close();
		
		
		}
		
		catch(Exception e){
			
			
			Log.e(TAG, "Error in cursor!!");
			
			
			
		}
		
		
		
		
		return mSmsList;
		
		
		
		
		
	}




	
	
	
	
	
	

public ContactItemTraditional getContact(Context context, SMSListItem sms){
	
	String address=sms.mAddress;
	
	Log.v("SMSReader for test", "1 address: "+address);
	String name=null;
	
	

	
	if(address==null){
	
	ArrayList<Map<String,Object>> recipients_all = ConversationUtils.getRecipientsIds(context, sms.mThreadID);
	
	Log.v("SMSReader for test", "address����null recipients_all: "+recipients_all.toString());
	
	StringBuilder sb = new StringBuilder();

	for(int i=0;i<recipients_all.size();i++){
		
		String number =(String) recipients_all.get(i).get("number");
		ContactItemTraditional	item=searchNames(number,name,context);
		if(item!=null){
			sb.append(item.mName+", ");
		}
		
	}
	
	if(sb.toString().equals("")){
		return null;
	}
	
	else{
		ContactItemTraditional	item= new ContactItemTraditional();
		item.mName=sb.toString();
		return item;
		
	}

	}
	
	
	else{
		
		ContactItemTraditional	item=searchNames(address,name,context);
		
		
		if(item==null){
			
			
			
			
			Log.v("SMSReader for test", "7 address: "+address+" name: "+name+"����ʧ���ˣ���������");
			
			
			return null;
		
		}
		else{
			
		
			return item;
		
		}
	}
	

	
	
}




public ContactItemTraditional searchNames(String address,String name,Context context){
	
	
	String strip1 = ConversationUtils.replacePattern(address,
			"^((\\+{0,1}86){0,1})", ""); // strip
	// +86
	String strip2 = ConversationUtils.replacePattern(strip1, "(\\-)", ""); // strip
																			// -
	String strip3 = ConversationUtils.replacePattern(strip2, "(\\ )", ""); // strip
																			// space
	String strip4 = ConversationUtils.replacePattern(strip3,
			"^((\\+{0,1}12520){0,1})", "");
	address = strip4;
	
	
	
	
	
	
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
		ContactItemTraditional item = new ContactItemTraditional();
	
		item.mName=name;
		
		return item;
	
	}
	else{
		
		Log.v("SMSReader for test", "6 address: "+address+" name: "+name+"����ʧ���ˣ���������");
		
		
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

             

         // ȡ����ϵ������  

         int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);     

         name = cursor.getString(nameFieldColumnIndex);  

         Log.i("Contacts", "" + name + " .... " + nameFieldColumnIndex); // ������ʾ force close  

        // m_TextView.setText("��ϵ������" + name);  

     }
     
     cursor.close();
		cursor=null;
     
	
	
	return name;
	
}







	
	
	
	
}


class ContactItemTraditional {
	public String mName;
}






class SMSItemTraditional {
	public static final String ID = "_id";
	public static final String THREAD = "thread_id";
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String BODY = "body";
	public static final String SUBJECT = "subject";
	public static final String SUB_ID="sub_id";
	public static final String LOCKED="locked";

	
	public String mAddress;
	public String mBody;
	public String mSubject;
	public long mID;
	public long mThreadID;
	public long mDate;
	public long mRead;
	public long mPerson;
	public long mSubID;
    public int mLocked;
	

	private static int mIdIdx;
	private static int mThreadIdx;
	private static int mAddrIdx;
	private static int mPersonIdx;
	private static int mDateIdx;
	private static int mReadIdx;
	private static int mBodyIdx;
	private static int mSubjectIdx;
	private static int mSubIDIdx;
	private static int mLockedIdx;
	

	
	

public SMSItemTraditional(){
	
}





public SMSListItem getMMSItems(Context context,Cursor cur){
	
	int mIdIdx = cur.getColumnIndex( ID );
	int mThreadIdx = cur.getColumnIndex( THREAD );
	//int mAddrIdx = cur.getColumnIndex( ADDRESS );
	//int mPersonIdx = cur.getColumnIndex( PERSON );
	int mDateIdx = cur.getColumnIndex( DATE );
	int mReadIdx = cur.getColumnIndex( READ );
	//int mBodyIdx = cur.getColumnIndex( BODY );
	//int mSubjectIdx = cur.getColumnIndex( SUBJECT );
	int mSubIDIdx=cur.getColumnIndex(SUB_ID);
	int mSubjectIdx = cur.getColumnIndex("sub");
	int mLockedIdx=cur.getColumnIndex(LOCKED);
	
	
	
	
	
	
	
	long mID = cur.getLong(mIdIdx);
	long mThreadID = cur.getLong(mThreadIdx);
	
	
	//String	mAddress = cur.getString(mAddrIdx);
	//long mPerson = cur.getLong(mPersonIdx);
	long mDate = cur.getLong(mDateIdx);
	long mRead = cur.getLong(mReadIdx);
	
	
	
	//String mBody = cur.getString(mBodyIdx);
	String mSubject = cur.getString(mSubjectIdx);
	long mSubID=cur.getInt(mSubIDIdx);
	int mLocked=cur.getInt(mLockedIdx);
	
	
	
	Uri uriAddr = null;
	Cursor curAddr = null;
	String mAddress=null;
	String mBody=null;
	uriAddr = Uri.parse("content://mms/" + mID + "/addr");
	curAddr = context.getContentResolver().query(uriAddr, null,
			"msg_id=" + mID, null, null);
	if (curAddr != null && curAddr.moveToFirst()) {
		mAddress = curAddr.getString(curAddr
				.getColumnIndex("address"));
		if(mAddress.equals("insert-address-token")){
			
			curAddr.moveToLast();
			
			mAddress=curAddr.getString(curAddr
				.getColumnIndex("address"));
			
		}
	}else{
		
		mAddress="无地址";
	}
	
	
	
	
	if (mSubject != null&&!mSubject.equals("")) {
		try {
			mSubject = new String(mSubject.getBytes("ISO8859_1"),
					"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}else{
		
		mSubject="无主题";
		
	}
	
	mBody="content://mms/" + mID;
	
	
	
	
	
	
	
	
	
	
 SMSListItem listItem = new SMSListItem();
	
	listItem.mID=mID;
	
	listItem.mThreadID=mThreadID;
	
	listItem.mAddress=mAddress;
	
	//zaizhe ����Ĭ�ϵ�Ϊ0�ɣ� ������ô��ƣ�����û�õ�
	listItem.mPerson=0;
	
	listItem.mDate=mDate*1000;
	
	listItem.mRead =mRead;
	
	listItem.mBody=mBody;
	
	listItem.mSubject=mSubject;
	
	listItem.mSubID=mSubID;
	
	listItem.mName=null;
	listItem.mLocked=mLocked;
	
	
	
	
	
	return listItem;
	
	
	
	
}





public SMSListItem getFailedMMSItems(Context context,Cursor cur){
	
	int mIdIdx = cur.getColumnIndex("msg_id" );
	int mThreadIdx = cur.getColumnIndex( THREAD );
	//int mAddrIdx = cur.getColumnIndex( ADDRESS );
	//int mPersonIdx = cur.getColumnIndex( PERSON );
	int mDateIdx = cur.getColumnIndex( DATE );
	int mReadIdx = cur.getColumnIndex( READ );
	//int mBodyIdx = cur.getColumnIndex( BODY );
	//int mSubjectIdx = cur.getColumnIndex( SUBJECT );
	int mSubIDIdx=cur.getColumnIndex(SUB_ID);
	int mSubjectIdx = cur.getColumnIndex("sub");
	
	int mNameIdx = cur.getColumnIndex("name");
	
	int mAddrIdx = cur.getColumnIndex( "address" );
	
	
	
	
	
	
	long mID = cur.getLong(mIdIdx);
	long mThreadID = cur.getLong(mThreadIdx);
	
	Log.v("SMSHelper for test!!!","mID: "+mID+" mIdIdx: "+mIdIdx);
	//String	mAddress = cur.getString(mAddrIdx);
	//long mPerson = cur.getLong(mPersonIdx);
	long mDate = cur.getLong(mDateIdx);
	long mRead = cur.getLong(mReadIdx);
	
	
	
	//String mBody = cur.getString(mBodyIdx);
	String mSubject = cur.getString(mSubjectIdx);
	long mSubID=cur.getInt(mSubIDIdx);
	String mName = cur.getString(mNameIdx);
	String mAddress = cur.getString(mAddrIdx);
	
	
	
	
//	Uri uriAddr = null;
//	Cursor curAddr = null;
//	String mAddress=null;
	String mBody=null;
//	uriAddr = Uri.parse("content://mms/" + mID + "/addr");
//	
//	Log.v("SMSHelper for test!!!","uriAddr :"+uriAddr);
//	curAddr = context.getContentResolver().query(uriAddr, null,
//			"msg_id=" + mID, null, null);
//	if (curAddr != null && curAddr.moveToFirst()) {
//		mAddress = curAddr.getString(curAddr
//				.getColumnIndex("address"));
//		if(mAddress.equals("insert-address-token")){
//			
//			curAddr.moveToLast();
//			
//			mAddress=curAddr.getString(curAddr
//				.getColumnIndex("address"));
//			
//		}
//	}else{
//		
//		mAddress="�޵�ַ";
//	}
//	
//	
//	
//	
	if (mSubject != null&&!mSubject.equals("")) {
		try {
			mSubject = new String(mSubject.getBytes("ISO8859_1"),
					"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}else{
		
		mSubject="无主题";
		
	}
	
	mBody="content://mms/" + mID;
	
	
	
	
	
	
	
	
	
	
 SMSListItem listItem = new SMSListItem();
	
	listItem.mID=mID;
	
	listItem.mThreadID=mThreadID;
	
	listItem.mAddress=mAddress;
	
	//zaizhe ����Ĭ�ϵ�Ϊ0�ɣ� ������ô��ƣ�����û�õ�
	listItem.mPerson=0;
	
	listItem.mDate=mDate*1000;
	
	listItem.mRead =mRead;
	
	listItem.mBody=mBody;
	
	listItem.mSubject=mSubject;
	
	listItem.mSubID=mSubID;
	
	listItem.mName=mName;
	
	
	
	
	
	return listItem;
	
	
	
	
}



public SMSListItem getItems(Cursor cur){
	
	int mIdIdx = cur.getColumnIndex( ID );
	int mThreadIdx = cur.getColumnIndex( THREAD );
	int mAddrIdx = cur.getColumnIndex( ADDRESS );
	int mPersonIdx = cur.getColumnIndex( PERSON );
	int mDateIdx = cur.getColumnIndex( DATE );
	int mReadIdx = cur.getColumnIndex( READ );
	int mBodyIdx = cur.getColumnIndex( BODY );
	int mSubjectIdx = cur.getColumnIndex( SUBJECT );
	int mSubIDIdx=cur.getColumnIndex(SUB_ID);
	
	int mLockedIdx=cur.getColumnIndex(LOCKED);
	
	
	
	
	
	long mID = cur.getLong(mIdIdx);
	long mThreadID = cur.getLong(mThreadIdx);
	String	mAddress = cur.getString(mAddrIdx);
	long mPerson = cur.getLong(mPersonIdx);
	long mDate = cur.getLong(mDateIdx);
	long mRead = cur.getLong(mReadIdx);
	String mBody = cur.getString(mBodyIdx);
	String mSubject = cur.getString(mSubjectIdx);
	long mSubID=cur.getInt(mSubIDIdx);
	int mLocked=cur.getInt(mLockedIdx);
	
	
	SMSListItem listItem = new SMSListItem();
	
	listItem.mID=mID;
	
	listItem.mThreadID=mThreadID;
	
	listItem.mAddress=mAddress;
	
	listItem.mPerson=mPerson;
	
	listItem.mDate=mDate;
	
	listItem.mRead =mRead;
	
	listItem.mBody=mBody;
	
	listItem.mSubject=mSubject;
	
	listItem.mSubID=mSubID;
	
	listItem.mName=null;
	listItem.mLocked=mLocked;
	
	
	return listItem;
	
	
}











}