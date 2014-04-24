package edu.bupt.mms.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

import edu.bupt.mms.MmsApp;

import edu.bupt.mms.R;
import edu.bupt.mms.data.Contact;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Toast;

public class RecentListUtils {
	private static final String TAG = "InboxActivity";

	public Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	public Uri SMS_SENTBOX = Uri.parse("content://sms/sent");
	public Uri DRAFT_INBOX = Uri.parse("content://sms/draft");
	public Uri Failed_INBOX = Uri.parse("content://sms/failed");
	// String strUriFailed = "content://sms/failed";
	public Uri SMS_INBOX1 = Uri.parse("content://mms-sms/conversations");
	public Uri ALL_INBOX = Uri.parse("content://sms/");

	public static final String ID = "_id";
	public static final String THREAD = "thread_id";
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String BODY = "body";
	public static final String SUBJECT = "subject";
	public static final String SUB_ID = "sub_id";

	public static final String DISPLAY_NAMES = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;

	// public static final Uri DISPLAY_NAMES_URI=ContactsContract.Contacts.;

	public static final Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

	public static final String TYPE = "type";

	private static int mIdIdx;
	private static int mThreadIdx;
	private static int mAddrIdx;
	private static int mPersonIdx;
	private static int mDateIdx;
	private static int mReadIdx;
	private static int mBodyIdx;
	private static int mSubjectIdx;
	private static int mSub_Id;

	private static int mTypeIdx;

	public String mAddress;
	public String mBody;
	public int mID;
	public int mThreadID;
	public int mDate;
	public int mRead;
	public int mPerson;
	public int mSub;
	public String mAddr;
	public int mType;

	ArrayList<Map<String, Object>> names_id = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> phoness_id = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> calls_id = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> sms_id = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> whole_id = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> normalized_id = new ArrayList<Map<String, Object>>();

	public static String getPeopleTest(String mNumber, Context context) {
		Uri uri = Uri.parse("content://com.android.contacts/data/phones/");
		Cursor cursor = context.getContentResolver().query(
				uri,
				new String[] { ContactsContract.Contacts.DISPLAY_NAME,
						ContactsContract.CommonDataKinds.Phone.NUMBER },
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
						+ mNumber + "'", null, null);

		if (cursor == null) {

			Log.d(TAG, "getPeople null");

			return null;

		}

		Log.d(TAG, "getPeople cursor.getCount() = " + cursor.getCount());

		String name = null;

		for (int i = 0; i < cursor.getCount(); i++)

		{

			cursor.moveToPosition(i);

			// ȡ����ϵ������

			int nameFieldColumnIndex = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			name = cursor.getString(nameFieldColumnIndex);

			Log.i("Contacts", "" + name + " .... " + nameFieldColumnIndex); // ������ʾ
																			// force
																			// close

			// m_TextView.setText("��ϵ������" + name);

		}

		return name;

	}

	public String getPeople(String mNumber, Context context) {

		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,

		ContactsContract.CommonDataKinds.Phone.NUMBER };

		Log.d(TAG, "getPeople ---------");

		// ���Լ���ӵ� msPeers ��

		Cursor cursor = context.getContentResolver().query(

		ContactsContract.CommonDataKinds.Phone.CONTENT_URI,

				projection, // Which columns to return.

				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
						+ mNumber + "'", // WHERE clause.

				null, // WHERE clause value substitution

				null); // Sort order.

		if (cursor == null) {

			Log.d(TAG, "getPeople null");

			return null;

		}

		Log.d(TAG, "getPeople cursor.getCount() = " + cursor.getCount());

		String name = null;
		for (int i = 0; i < cursor.getCount(); i++)

		{

			cursor.moveToPosition(i);

			// ȡ����ϵ������

			int nameFieldColumnIndex = cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);

			name = cursor.getString(nameFieldColumnIndex);

			Log.i("Contacts", "" + name + " .... " + nameFieldColumnIndex); // ������ʾ
																			// force
																			// close

			// m_TextView.setText("��ϵ������" + name);

		}

		return name;

	}

	public void searchCall(Context context) {
		// StringBuilder sb = new StringBuilder();
		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null,
				" date  DESC  limit 20");
		// getContentResolver().query(PHONE_URI, arg1, arg2, arg3, arg4, arg5)
		// getContentResolver.
		cursor.moveToFirst();
		if (cursor.getCount() != 0) {
			do {
				int id = cursor
						.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
				String number = cursor.getString(cursor
						.getColumnIndex(CallLog.Calls.NUMBER));
				
				
				
				String strip1 = ConversationUtils.replacePattern(number,
						"^((\\+{0,1}86){0,1})", ""); // strip
				// +86
				String strip2 = ConversationUtils.replacePattern(strip1, "(\\-)", ""); // strip
																						// -
				String strip3 = ConversationUtils.replacePattern(strip2, "(\\ )", ""); // strip
																						// space
				String strip4 = ConversationUtils.replacePattern(strip3,
						"^((\\+{0,1}12520){0,1})", "");
				number = strip4;
				
				
				int date = cursor.getInt(cursor
						.getColumnIndex(CallLog.Calls.DATE));

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", number);
				map.put("date", date);
				
				
				

				calls_id.add(map);

				// sb.append("id�� "+id+"number�� "+number+"Date�� "+date+"\n");
			} while (cursor.moveToNext());

			for (int i = 0; i < calls_id.size(); i++) {
				// ArrayList<>
				String number = (String) calls_id.get(i).get("number");

				number = PhoneNumberUtils.formatNumber(number, number, MmsApp
						.getApplication().getCurrentCountryIso());
				// int date=(Integer) calls_id.get(i).get("date");

				for (int j = i + 1; j < calls_id.size(); j++) {
					String number2 = (String) calls_id.get(j).get("number");
					if (number2 != null && number2.equals("")) {
						number2 = PhoneNumberUtils.formatNumber(number2,
								number2, MmsApp.getApplication()
										.getCurrentCountryIso());

						if (number.equals(number2)) {
							calls_id.remove(j);
							j--;
						}
					} else {
						calls_id.remove(j);
						j--;
					}

				}

			}

		}

		// for(int i=0;i<calls_id.size();i++){
		//
		// sb.append("number�� "+(String)
		// calls_id.get(i).get("number")+"Date�� "+(Integer)
		// calls_id.get(i).get("date")+"\n");
		//
		// }
		//
		// if(!sb.toString().equals("")){
		// textview.setText(sb.toString());
		// }
		// cursor.close();
		//

	}

	public void searchNumbers(Context context) {
		Cursor cursor = context.getContentResolver().query(ALL_INBOX, null,
				null, null, "date DESC limit 20");
		// Cursor cursor=getContentResolver().query(CallLog.Calls.CONTENT_URI,
		// null, null, null, "date DESC limit 20");

		// CallLog.Calls.
		
		if(cursor==null){
			
			Toast.makeText(context, "请稍后再试", Toast.LENGTH_LONG).show();
			return;
			
		}
		cursor.moveToFirst();

		// StringBuilder sb = new StringBuilder();
		if (cursor.getCount() != 0) {
			do {

				String number = cursor
						.getString(cursor.getColumnIndex(ADDRESS));
				
				Log.v(TAG, "number is? :"+number);
				
				if(number==null||number.equals("")){
					continue;
				}
				
				String strip1 = ConversationUtils.replacePattern(number,
						"^((\\+{0,1}86){0,1})", ""); // strip
				// +86
				String strip2 = ConversationUtils.replacePattern(strip1, "(\\-)", ""); // strip
																						// -
				String strip3 = ConversationUtils.replacePattern(strip2, "(\\ )", ""); // strip
																						// space

				number = strip3;
				
				
				

				int date = cursor.getInt(cursor.getColumnIndex("date"));

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", number);
				map.put("date", date);
				sms_id.add(map);

				// sb.append("��ַ�� "+number+"ʱ�䣺 "+date+"\n");

			} while (cursor.moveToNext());

			cursor.close();

			for (int i = 0; i < sms_id.size(); i++) {
				// ArrayList<>
				String number = (String) sms_id.get(i).get("number");
				Log.v("RecentListUtils for test", "number: " + number);
				if (number != null && !number.equals("")) {
					number = PhoneNumberUtils.formatNumber(number, number,
							MmsApp.getApplication().getCurrentCountryIso());
					// int date=(Integer) calls_id.get(i).get("date");
				} else {
					Log.v("RecentListUtils for test", "wrong number: " + number);

				}

				for (int j = i + 1; j < sms_id.size(); j++) {

					String number2 = (String) sms_id.get(j).get("number");
					Log.v("RecentListUtils for test", "number2: " + number2);
					if (number2 != null && !number2.equals("")) {
						number2 = PhoneNumberUtils.formatNumber((String) sms_id
								.get(j).get("number"), (String) sms_id.get(j)
								.get("number"), MmsApp.getApplication()
								.getCurrentCountryIso());
						Log.v("RecentListUtils for test", "ִ����if number2: "
								+ number2);
						if (number.equals(number2)) {
							sms_id.remove(j);
							j--;
						}
					} else {
						sms_id.remove(j);
						j--;
					}
					// PhoneNumberUtils.formatNumber((String)sms_id.get(j).get("number"),(String)
					// sms_id.get(j).get("number"),
					// MmsApp.getApplication().getCurrentCountryIso())

				}

			}

		}

		// for(int i=0;i<sms_id.size();i++){
		//
		// sb.append("number�� "+(String)
		// sms_id.get(i).get("number")+"Date�� "+(Integer)
		// sms_id.get(i).get("date")+"\n");
		//
		// }
		//
		// if(!sb.toString().equals("")){
		// textview.setText(sb.toString());
		// }
		// cursor.close();
	}

	public void sortDate() {

		int size = 0;
		int c = 0;
		int s = 0;
		whole_id.clear();
		Log.v("RecentListUtils", "whole_id length: " + whole_id.size());
		if ((calls_id.size() + sms_id.size()) >= 20) {

			size = 20;
		}

		else if ((calls_id.size() + sms_id.size()) == 0) {

			size = 0;

		}

		else {
			size = calls_id.size() + sms_id.size();

		}
		Log.v("RecentListUtils", "size length: " + size);
		for (int i = 0; i < size; i++) {

			if (s >= sms_id.size() && c < calls_id.size()) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", calls_id.get(c).get("number"));
				map.put("date", calls_id.get(c).get("date"));
				map.put("source", R.drawable.phone);

				whole_id.add(map);
				c++;

			}

			else if (c >= calls_id.size() && s < sms_id.size()) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", sms_id.get(s).get("number"));
				map.put("date", sms_id.get(s).get("date"));
				map.put("source", R.drawable.recent_list_msg);

				whole_id.add(map);
				s++;

			}

			else if ((Integer) sms_id.get(s).get("date") >= (Integer) calls_id
					.get(c).get("date")) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", sms_id.get(s).get("number"));
				map.put("date", sms_id.get(s).get("date"));
				map.put("source", R.drawable.recent_list_msg);
				whole_id.add(map);
				s++;
				// Log.v("InboxActivity",
				// "s: "+s+" number: "+map.get("number"));

			}

			else {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", calls_id.get(c).get("number"));
				map.put("date", calls_id.get(c).get("date"));

				map.put("source", R.drawable.phone);

				whole_id.add(map);
				c++;
				// Log.v("InboxActivity",
				// "c: "+c+" number: "+map.get("number"));

			}

		}

		Log.v("RecentListUtils", "whole_id length: " + whole_id.size());

	}

	public ArrayList<Map<String, Object>> getRecentList(Context context) {

		searchNumbers(context);
		searchCall(context);

		sortDate();

		for (int i = 0; i < whole_id.size(); i++) {

			
			String tempAddress = (String) whole_id.get(i).get("number");

			String strip1 = ConversationUtils.replacePattern(tempAddress,
					"^((\\+{0,1}86){0,1})", ""); // strip
			// +86
			String strip2 = ConversationUtils.replacePattern(strip1, "(\\-)",
					""); // strip
							// -
			String strip3 = ConversationUtils.replacePattern(strip2, "(\\ )",
					""); // strip
							// space

			tempAddress = strip3;

			Contact cacheContact = Contact.get(tempAddress, true);

			String name = null;
			if (cacheContact!=null&&!cacheContact.getNumber().equalsIgnoreCase(cacheContact.getName())) {
				name = cacheContact.getName();
			} else {

				String subTitle = PhoneNumberUtils.formatNumber(tempAddress,
						tempAddress, MmsApp.getApplication()
								.getCurrentCountryIso());
				name = getPeopleTest(subTitle, context);
			}
			if (name == null || name.equals("")) {
				// name=(String)whole_id.get(i).get("number");
				name = "无名称";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", (String) whole_id.get(i).get("number"));
				map.put("number", name);
				map.put("source", (Integer) whole_id.get(i).get("source"));
				normalized_id.add(map);

			}
			// Log.v(TAG,
			// "number:"+(String)whole_id.get(i).get("number")+"name:"+name);

			else {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", name);
				map.put("number", (String) whole_id.get(i).get("number"));
				map.put("source", (Integer) whole_id.get(i).get("source"));
				normalized_id.add(map);

			}

		}

		Log.v("RecentListUtils",
				"normalized_id length: " + normalized_id.size());
		return normalized_id;

	}

}
