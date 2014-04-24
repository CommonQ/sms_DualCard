package edu.bupt.mms.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.mms.data.Contact;
import edu.bupt.mms.data.ContactList;
import edu.bupt.mms.numberlocate.PhoneStatusRecevier;

import edu.bupt.mms.numberlocate.NumberLocateProvider.NumberRegion;
import edu.bupt.mms.numberlocate.NumberLocateProvider.SpecialService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Telephony.Threads;
import android.util.Log;
import android.widget.Toast;

public class ConversationUtils {

	public static Uri ALL_INBOX = Uri.parse("content://sms/");
	public static Uri ALL_MMS_INBOX = Uri.parse("content://mms/");
	public static HashMap<String, String> CacheMap = new HashMap<String, String>();
	public static HashMap<String, String> CacheCityMap = new HashMap<String, String>();

	public static HashMap<String, String> CityMap = new HashMap<String, String>();

	public static HashMap<String, Bitmap> PersonImageMap = new HashMap<String, Bitmap>();
	
	public static boolean isFinishLoadAllNumber = false;

	public static boolean isReadable = true;
	
	public static int mCountSim1=0;

	public static int mCountSim2=0;
	

	public static HashMap<String, String> getAllNumbers(Context context) {

		HashMap<String, String> CacheCityMaplocal = new HashMap<String, String>();

		String[] projection = new String[] { "_id", "address" };
		Cursor cursor = context.getContentResolver().query(ALL_INBOX,
				projection, null, null, null);

		// isFinishLoadAllNumber=false;

		while (cursor.moveToNext()) {
			String address = cursor.getString(1);

			if (address == null) {

				Log.v("ConversationList for  test!!!", "!!!!!!!!!");
				continue;

			}

			else {
				address = address.trim();
				address = address.replace(" ", "");

				Log.v("ConversationList for test", address);

				if (CacheCityMaplocal.get(address) == null) {

					String strip1 = replacePattern(address,
							"^((\\+{0,1}86){0,1})", ""); // strip
					// +86
					String strip2 = replacePattern(strip1, "(\\-)", ""); // strip
																			// -
					String strip3 = replacePattern(strip2, "(\\ )", ""); // strip
																			// space
					String strip4 = ConversationUtils.replacePattern(strip3,
							"^((\\+{0,1}12520){0,1})", "");
					address = strip4;

					String city = startQuery(context, address);

					Log.v("ConversationList for test",
							"Cache.get(\"address\")==null  address: " + address
									+ "  city: " + city);

					CacheCityMaplocal.put(address, city);

				}

			}

			// isFinishLoadAllNumber=true;
		}

		return CacheCityMaplocal;

	}

	public static String saveNumber(Context context, String number) {

		number = number.trim();
		number = number.replace(" ", "");
		number = number.replace("-", "");

		String strip1 = replacePattern(number, "^((\\+{0,1}86){0,1})", ""); // strip
		// +86
		String strip2 = replacePattern(strip1, "(\\-)", ""); // strip -
		String strip3 = replacePattern(strip2, "(\\ )", ""); // strip space
		String strip4 = ConversationUtils.replacePattern(strip3,
				"^((\\+{0,1}12520){0,1})", "");
		number = strip4;

		if (number.indexOf("+") == 0 || number.indexOf("%2B") == 0) {

			number = number.substring(number.length() - 11);
			Log.v("ConversationUtils", "����number�� " + number);
		} else if (number.indexOf("00") == 0) {

			number = number.substring(number.length() - 11);

		} else if (number.indexOf("**133") == 0) {
			if (number.indexOf("#") != -1 || number.indexOf("%23") != -1) {
				number = number.substring(number.length() - 14,
						number.length() - 3);

				Log.v("ConversationUtils", "%23 address: " + number);
			} else {

				number = number.substring(number.length() - 11);

				Log.v("ConversationUtils", "ɶҲû�� address: " + number);
			}
		}

		else {

			number = number;

		}

		if (CacheCityMap.get(number) == null) {

			String city = startQuery(context, number);

			Log.v("ConversationList for test",
					"Cache.get(\"number\")==null  number: " + number
							+ "  city: " + city);

			CacheCityMap.put(number, city);

			return city;

		} else {

			String city = (String) CacheCityMap.get(number);

			Log.v("ConversationList for test",
					"Cache.get(\"number\")!!!=null  number: " + number
							+ "  city: " + city
							+ " (String) Cache.get(\"number\")"
							+ (String) CacheCityMap.get(number));

			return city;

		}

	}

	public static String formatNumber(String number) {
		StringBuilder sb = new StringBuilder(number);
		if (sb.charAt(0) == '+') {
			return sb.delete(0, 3).substring(0, 7).toString();
		}
		if (sb.charAt(0) == '0') {
			return sb.substring(0, 4);
		}
		if (sb.length() < 7) {
			return sb.toString();
		}
		return sb.substring(0, 7).toString();
	}
	
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	} 

	public static String startQuery(Context context, String number) {
		if (number.length() >= 11) {
			Log.v("ConversationList for test", "number: " + number);
			String formatNumber = formatNumber(number);
			Log.v("ConversationList for test", "formatnumber: " + formatNumber);
			String selection = null;
			String[] projection = null;
			Uri uri = NumberRegion.CONTENT_URI;
			if (formatNumber.length() == 7) {
				if(!isNumeric(formatNumber)){
					return"";
				}
				
				selection = NumberRegion.NUMBER + "=" + formatNumber;
				uri = NumberRegion.CONTENT_URI;
				projection = new String[] { NumberRegion.PROVINCE,
						NumberRegion.CITY, NumberRegion.CARD };
			} else {
				if(!isNumeric(formatNumber)){
					return"";
				}
				selection = NumberRegion.AREACODE + "=" + "'" + formatNumber
						+ "'" + " OR " + NumberRegion.AREACODE + "=" + "'"
						+ formatNumber.substring(0, 3) + "'";
				uri = NumberRegion.CONTENT_URI;
				projection = new String[] { NumberRegion.PROVINCE,
						NumberRegion.CITY };
			}

			Cursor cursor = context.getContentResolver().query(uri, projection,
					selection, null, null);

			if (cursor != null && cursor.moveToNext()) {
				String city = null;
				String localProvince = cursor.getString(0);
				String localCity = cursor.getString(1);
				if (localProvince.equalsIgnoreCase(localCity)) {
					city = cursor.getString(0);
				} else {
					city = cursor.getString(0) + "-" + cursor.getString(1);
				}

				if (formatNumber.length() == 7) {

					String card = cursor.getString(2);

					if (card.contains("联通")) {
						card = "联通";
					} else if (card.contains("移动")) {

						card = "移动";
					} else if (card.contains("电信")) {

						card = "电信";
					}

					//city = city + " " + card;
					city = city + card;
				}
				// String city = cursor.getString(0)+"-"+cursor.getString(1);
				cursor.close();
				Log.v("ConversationList for test", "cursor!=0 city: " + city);
				return city;

			} else {
				Log.v("ConversationList for test", "cursor=0 city: ");

				return "";

			}

		} else if (number.length() <= 5) {
			if(!isNumeric(number)){
				return"";
			}
			String selection = null;
			String[] projection = null;
			Uri uri = SpecialService.CONTENT_URI;

			selection = SpecialService.NUMBER + "=" + number;
			uri = SpecialService.CONTENT_URI;
			projection = new String[] { SpecialService.COMPANY };

			Cursor cursor = context.getContentResolver().query(uri, projection,
					selection, null, null);
			if (cursor != null && cursor.moveToNext()) {

				String city = cursor.getString(0);

				return city;

			} else {

				Log.v("ConversationList for test", "cursor=0 city: ");

				return "";
			}

		}

		else {

			Log.v("ConversationList for test", "cursor=0 city: ");

			return "";

		}

	}

	public static final Uri sAllThreadsUri = Threads.CONTENT_URI.buildUpon()
			.appendQueryParameter("simple", "true").build();

	public static final String[] ALL_THREADS_PROJECTION = { Threads._ID,
			Threads.DATE, Threads.MESSAGE_COUNT, Threads.RECIPIENT_IDS,
			Threads.SNIPPET, Threads.SNIPPET_CHARSET, Threads.READ,
			Threads.ERROR, Threads.HAS_ATTACHMENT, Threads.TYPE };

	private static final int ID = 0;
	private static final int DATE = 1;
	private static final int MESSAGE_COUNT = 2;
	private static final int RECIPIENT_IDS = 3;
	private static final int SNIPPET = 4;
	private static final int SNIPPET_CS = 5;
	private static final int READ = 6;
	private static final int ERROR = 7;
	private static final int HAS_ATTACHMENT = 8;
	private static final int TYPE = 9;

	public static String replacePattern(String origin, String pattern,
			String replace) {
		if(origin!=null){
		// Log.i(TAG, "origin - " + origin);
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(origin);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, replace);
		}

		m.appendTail(sb);
		// Log.i(TAG, "sb.toString() - " + sb.toString());
		return sb.toString();
		}else{
			return null;
		}
	}

	public static ArrayList<Map<String, Object>> getRecipientsIds(
			Context context, long threadId) {

		ArrayList<Long> recipients_ids = new ArrayList<Long>();

		Cursor c = context.getContentResolver().query(sAllThreadsUri,
				ALL_THREADS_PROJECTION, Threads._ID + " = " + threadId, null,
				null);
		
		
		if(c==null||c.getCount()==0)
		return null;

		c.moveToPosition(-1);

		c.moveToNext();

		String spaceSepIds = c.getString(RECIPIENT_IDS);

		// zaizhe ɾ��ݸ������ϵ���Ҳ����ݸ��recipient����IDs

		String[] ids = spaceSepIds.split(" ");
		for (String id : ids) {
			long longId;

			try {
				longId = Long.parseLong(id);

				recipients_ids.add(longId);

			} catch (NumberFormatException ex) {
				// skip this id
				continue;
			}
		}

		ContactList recipients = ContactList.getByIds(
				c.getString(RECIPIENT_IDS), false);

		String[] numbers = recipients.getNumbers();

		ArrayList<Map<String, Object>> recipient_all = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < recipients_ids.size(); i++) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("thread_id", recipients_ids.get(i));
			map.put("number", numbers[i]);
			recipient_all.add(map);
		}

		// Log.v("ConversationUtils",
		// "number: "+recipients.toString()+" exact number: "+numbers[1]);

		c.close();

		return recipient_all;

	}

	public static void changeUnread(Context context) {

		ContentValues values = new ContentValues();
		values.put("read", 1);

		context.getContentResolver().update(ALL_INBOX, values, null, null);

	}

	public static void changeUnreadId(Context context, long id, boolean isSMS) {

		if (isSMS) {

			ContentValues values = new ContentValues();
			values.put("read", 1);

			context.getContentResolver().update(ALL_INBOX, values,
					"_id = " + id, null);

		} else {

			ContentValues values = new ContentValues();
			values.put("read", 1);

			context.getContentResolver().update(ALL_MMS_INBOX, values,
					"_id = " + id, null);

		}

	}

	public static void changeUnreadCard(Context context, int sub) {

		ContentValues values = new ContentValues();

		values.put("read", 1);

		context.getContentResolver().update(ALL_INBOX, values,
				" sub_id = " + sub, null);

	}

	public static void changeUnreadMMS(Context context) {

		ContentValues values = new ContentValues();
		values.put("read", 1);

		context.getContentResolver().update(ALL_MMS_INBOX, values, null, null);

	}

	public static void insertThread(Context context,
			ArrayList<Map<String, Object>> recipient_all, long thread_id) {

		for (int i = 0; i < recipient_all.size(); i++) {


			Cursor c = context.getContentResolver().query(
					ALL_INBOX,
					null,
					"thread_id=" + thread_id + " AND " + "address= '"
							+ recipient_all.get(i).get("number") + "'", null,
					null);

			if (c.getCount() != 0
					&& (Long) recipient_all.get(i).get("thread_id") != thread_id) {

				c.moveToFirst();
				int id = c.getInt(c.getColumnIndex("_id"));
				long threadId = c.getLong(c.getColumnIndex("thread_id"));
				String address = c.getString(c.getColumnIndex("address"));
				int person = c.getInt(c.getColumnIndex("person"));
				long date = c.getLong(c.getColumnIndex("date"));
				long date_sent = c.getLong(c.getColumnIndex("date_sent"));
				int protocol = c.getInt(c.getColumnIndex("protocol"));
				int read = c.getInt(c.getColumnIndex("read"));
				int status = c.getInt(c.getColumnIndex("status"));

				int type = c.getInt(c.getColumnIndex("type"));
				String reply_path_present = c.getString(c
						.getColumnIndex("reply_path_present"));
				String subject = c.getString(c.getColumnIndex("subject"));
				String body = c.getString(c.getColumnIndex("body"));
				String service_center = c.getString(c
						.getColumnIndex("service_center"));
				int service_date = c.getInt(c.getColumnIndex("service_date"));
				int dest_port = c.getInt(c.getColumnIndex("dest_port"));
				int locked = c.getInt(c.getColumnIndex("locked"));
				int sub_id = c.getInt(c.getColumnIndex("sub_id"));
				int error_code = c.getInt(c.getColumnIndex("error_code"));
				int seen = c.getInt(c.getColumnIndex("seen"));
				String recipient_cc_ids = c.getString(c
						.getColumnIndex("recipient_cc_ids"));
				String recipient_bcc_ids = c.getString(c
						.getColumnIndex("recipient_bcc_ids"));
				byte[] sms_pdu = c.getBlob(c.getColumnIndex("sms_pdu"));
				int expiry = c.getInt(c.getColumnIndex("expiry"));
				int pri = c.getInt(c.getColumnIndex("pri"));

				Log.v("ConversationUtils", "id:" + id + " " + threadId + " "
						+ address + person + date + date_sent + protocol + read
						+ status + type + reply_path_present + subject + body
						+ service_center + service_date + dest_port + dest_port
						+ locked + sub_id + error_code + seen
						+ recipient_cc_ids + recipient_bcc_ids + sms_pdu + " "
						+ expiry + pri);

				ContentValues values = new ContentValues();

				values.put("_id", id);

				values.put("thread_id",
						(Long) recipient_all.get(i).get("thread_id"));

				values.put("address", address);

				values.put("person", person);

				values.put("date", date);

				values.put("date_sent", date_sent);

				values.put("protocol", protocol);

				values.put("read", read);

				values.put("status", status);

				values.put("type", type);

				values.put("reply_path_present", reply_path_present);

				values.put("subject", subject);

				values.put("body", body);

				values.put("service_center", service_center);

				values.put("service_date", service_date);

				values.put("dest_port", dest_port);

				values.put("locked", locked);

				values.put("sub_id", sub_id);

				values.put("error_code", error_code);

				values.put("seen", seen);

				values.put("recipient_cc_ids", recipient_cc_ids);

				values.put("recipient_bcc_ids", recipient_bcc_ids);

				values.put("sms_pdu", sms_pdu);

				values.put("expiry", expiry);

				values.put("pri", pri);

				context.getContentResolver().insert(ALL_INBOX, values);

			}

			else {

				c = context.getContentResolver()
						.query(ALL_INBOX,
								null,
								"address= '"
										+ recipient_all.get(i).get("number")
										+ "'", null, null);
				long threadId = c.getLong(c.getColumnIndex("thread_id"));

				if (c.getCount() != 0) {
					c = context.getContentResolver().query(
							ALL_INBOX,
							null,
							"thread_id=" + thread_id + " AND " + "address= '"
									+ recipient_all.get(0).get("number") + "'",
							null, null);

					c.moveToFirst();

					int id = c.getInt(c.getColumnIndex("_id"));
					String address = c.getString(c.getColumnIndex("address"));
					int person = c.getInt(c.getColumnIndex("person"));
					long date = c.getLong(c.getColumnIndex("date"));
					long date_sent = c.getLong(c.getColumnIndex("date_sent"));
					int protocol = c.getInt(c.getColumnIndex("protocol"));
					int read = c.getInt(c.getColumnIndex("read"));
					int status = c.getInt(c.getColumnIndex("status"));

					int type = c.getInt(c.getColumnIndex("type"));
					String reply_path_present = c.getString(c
							.getColumnIndex("reply_path_present"));
					String subject = c.getString(c.getColumnIndex("subject"));
					String body = c.getString(c.getColumnIndex("body"));
					String service_center = c.getString(c
							.getColumnIndex("service_center"));
					int service_date = c.getInt(c
							.getColumnIndex("service_date"));
					int dest_port = c.getInt(c.getColumnIndex("dest_port"));
					int locked = c.getInt(c.getColumnIndex("locked"));
					int sub_id = c.getInt(c.getColumnIndex("sub_id"));
					int error_code = c.getInt(c.getColumnIndex("error_code"));
					int seen = c.getInt(c.getColumnIndex("seen"));
					String recipient_cc_ids = c.getString(c
							.getColumnIndex("recipient_cc_ids"));
					String recipient_bcc_ids = c.getString(c
							.getColumnIndex("recipient_bcc_ids"));
					byte[] sms_pdu = c.getBlob(c.getColumnIndex("sms_pdu"));
					int expiry = c.getInt(c.getColumnIndex("expiry"));
					int pri = c.getInt(c.getColumnIndex("pri"));

					Log.v("ConversationUtils", "id:" + id + " " + threadId
							+ " " + address + person + date + date_sent
							+ protocol + read + status + type
							+ reply_path_present + subject + body
							+ service_center + service_date + dest_port
							+ dest_port + locked + sub_id + error_code + seen
							+ recipient_cc_ids + recipient_bcc_ids + sms_pdu
							+ " " + expiry + pri);

					ContentValues values = new ContentValues();

					values.put("_id", id);

					values.put("thread_id", threadId);

					values.put("address", address);

					values.put("person", person);

					values.put("date", date);

					values.put("date_sent", date_sent);

					values.put("protocol", protocol);

					values.put("read", read);

					values.put("status", status);

					values.put("type", type);

					values.put("reply_path_present", reply_path_present);

					values.put("subject", subject);

					values.put("body", body);

					values.put("service_center", service_center);

					values.put("service_date", service_date);

					values.put("dest_port", dest_port);

					values.put("locked", locked);

					values.put("sub_id", sub_id);

					values.put("error_code", error_code);

					values.put("seen", seen);

					values.put("recipient_cc_ids", recipient_cc_ids);

					values.put("recipient_bcc_ids", recipient_bcc_ids);

					values.put("sms_pdu", sms_pdu);

					values.put("expiry", expiry);

					values.put("pri", pri);

					context.getContentResolver().insert(ALL_INBOX, values);

				}

				else {
					c = context.getContentResolver()
							.query(ALL_INBOX,
									new String[] { "MAX(thread_id)" }, null,
									null, null);
					c.moveToFirst();
					long max_thread_id = c.getLong(0);

					ContentValues values = new ContentValues();

					c = context.getContentResolver().query(
							ALL_INBOX,
							null,
							"thread_id=" + thread_id + " AND " + "address= '"
									+ recipient_all.get(0).get("number") + "'",
							null, null);

					c.moveToFirst();

					String address = c.getString(c.getColumnIndex("address"));
					int person = c.getInt(c.getColumnIndex("person"));
					long date = c.getLong(c.getColumnIndex("date"));
					long date_sent = c.getLong(c.getColumnIndex("date_sent"));
					int protocol = c.getInt(c.getColumnIndex("protocol"));
					int read = c.getInt(c.getColumnIndex("read"));
					int status = c.getInt(c.getColumnIndex("status"));

					int type = c.getInt(c.getColumnIndex("type"));
					String reply_path_present = c.getString(c
							.getColumnIndex("reply_path_present"));
					String subject = c.getString(c.getColumnIndex("subject"));
					String body = c.getString(c.getColumnIndex("body"));
					String service_center = c.getString(c
							.getColumnIndex("service_center"));
					int service_date = c.getInt(c
							.getColumnIndex("service_date"));
					int dest_port = c.getInt(c.getColumnIndex("dest_port"));
					int locked = c.getInt(c.getColumnIndex("locked"));
					int sub_id = c.getInt(c.getColumnIndex("sub_id"));
					int error_code = c.getInt(c.getColumnIndex("error_code"));
					int seen = c.getInt(c.getColumnIndex("seen"));
					String recipient_cc_ids = c.getString(c
							.getColumnIndex("recipient_cc_ids"));
					String recipient_bcc_ids = c.getString(c
							.getColumnIndex("recipient_bcc_ids"));
					byte[] sms_pdu = c.getBlob(c.getColumnIndex("sms_pdu"));
					int expiry = c.getInt(c.getColumnIndex("expiry"));
					int pri = c.getInt(c.getColumnIndex("pri"));

					// Log.v("ConversationUtils",
					// "id:"+id+" "+max_thread_id+" "+address+person+date+date_sent+protocol+read+status+type+reply_path_present+subject+body+service_center+service_date+dest_port+dest_port+locked+sub_id+error_code+seen+recipient_cc_ids+recipient_bcc_ids+sms_pdu+" "+expiry+pri);

					values.put("thread_id", max_thread_id);

					values.put("address", address);

					values.put("person", person);

					values.put("date", date);

					values.put("date_sent", date_sent);

					values.put("protocol", protocol);

					values.put("read", read);

					values.put("status", status);

					values.put("type", type);

					values.put("reply_path_present", reply_path_present);

					values.put("subject", subject);

					values.put("body", body);

					values.put("service_center", service_center);

					values.put("service_date", service_date);

					values.put("dest_port", dest_port);

					values.put("locked", locked);

					values.put("sub_id", sub_id);

					values.put("error_code", error_code);

					values.put("seen", seen);

					values.put("recipient_cc_ids", recipient_cc_ids);

					values.put("recipient_bcc_ids", recipient_bcc_ids);

					values.put("sms_pdu", sms_pdu);

					values.put("expiry", expiry);

					values.put("pri", pri);

					context.getContentResolver().insert(ALL_INBOX, values);

				}

			}

		}

	}

	public static void copyThread(Context context,
			ArrayList<Map<String, Object>> recipient_all, long thread_id) {

		Log.v("ConversationUtils", "recipient_all: " + recipient_all.toString()
				+ " thread_id: " + thread_id);

		for (int i = 0; i < recipient_all.size(); i++) {
			Cursor c = context.getContentResolver().query(
					ALL_INBOX,
					null,
					"thread_id=" + recipient_all.get(i).get("thread_id")
							+ " AND " + "address= '"
							+ recipient_all.get(i).get("number") + "'", null,
					null);

			if (c.getCount() != 0
					&& (Long) recipient_all.get(i).get("thread_id") != thread_id) {

				ContentValues values = new ContentValues();
				values.put("thread_id",
						(Long) recipient_all.get(i).get("thread_id"));

				context.getContentResolver().update(
						ALL_INBOX,
						values,
						"thread_id = " + thread_id + " AND " + "address = '"
								+ recipient_all.get(i).get("number") + "'",
						null);

			}

			else {

				c = context.getContentResolver()
						.query(ALL_INBOX,
								null,
								"address= '"
										+ recipient_all.get(i).get("number")
										+ "'", null, null);

				if (c.getCount() != 0) {

					c.moveToFirst();
					long thread = c.getLong(c.getColumnIndex("thread_id"));

					ContentValues values = new ContentValues();
					values.put("thread_id", thread);

					context.getContentResolver().update(
							ALL_INBOX,
							values,
							"thread_id = " + thread_id + " AND "
									+ "address = '"
									+ recipient_all.get(i).get("number") + "'",
							null);

				}

				else {

					// c =context.getContentResolver().query(ALL_INBOX,
					// null,"address= '"+recipient_all.get(i).get("number")+"'",
					// null, null);

					c = context.getContentResolver()
							.query(ALL_INBOX,
									new String[] { "MAX(thread_id)" }, null,
									null, null);
					c.moveToFirst();
					long max_thread_id = c.getLong(0);

					ContentValues values = new ContentValues();
					values.put("thread_id", max_thread_id);

					context.getContentResolver().update(
							ALL_INBOX,
							values,
							"thread_id = " + thread_id + " AND "
									+ "address = '"
									+ recipient_all.get(i).get("number") + "'",
							null);

					// db.query(DATABASE_TABLE, new String [] {"MAX(price)"},
					// null, null, null, null, null);

				}

			}

		}

	}

	public static boolean copyAllvalues(long thread_id, String address,
			Context context, int position,
			ArrayList<Map<String, Object>> recipient_all) {

		ContentValues values = new ContentValues();
		
		
		
		
		
		
		
		
		
		//String strip1 = ConversationUtils.replacePattern(number,
		//		"^((\\+{0,1}86){0,1})", ""); // strip
		// +86
	//	String strip2 = ConversationUtils.replacePattern(strip1,
		//		"(\\-)", ""); // strip
		// -
		//String strip3 = ConversationUtils.replacePattern(strip2,
		//		"(\\ )", ""); // strip
		// space

		//number = strip3;
		
		
		
		
		
		
		

		Cursor c = context.getContentResolver().query(
				ALL_INBOX,
				null,
				"thread_id=" + thread_id + " AND " + "address= '"
						+ recipient_all.get(position).get("number") + "'",
				null, null);
		
		Log.v("ConversationUtils", "��һ��if thread_id=" + thread_id + " AND " + "address= '"
						+ recipient_all.get(position).get("number") + "'");

		if (c==null||c.getCount() == 0) {
			
			
			
			String number =(String) recipient_all.get(position).get("number");
			String strip1 = ConversationUtils.replacePattern(number,
					"^((\\+{0,1}86){0,1})", "");
			
			 c = context.getContentResolver().query(
					ALL_INBOX,
					null,
					"thread_id=" + thread_id + " AND " + "address= '"
							+ strip1 + "'",
					null, null);
			 Log.v("ConversationUtils", "strip1 thread_id=" + thread_id + " AND " + "address= '"
						+ recipient_all.get(position).get("number") + "'");
			 
			 if(c==null||c.getCount()==0){
				 
				 String strip2 = ConversationUtils.replacePattern(strip1,
							"(\\-)", "");
				 
				 
				 c = context.getContentResolver().query(
							ALL_INBOX,
							null,
							"thread_id=" + thread_id + " AND " + "address= '"
									+ strip2 + "'",
							null, null);
				 
				 Log.v("ConversationUtils", "strip2 thread_id=" + thread_id + " AND " + "address= '"
							+ recipient_all.get(position).get("number") + "'");
				 if(c==null||c.getCount()==0){
					 
					 
					 String strip3 = ConversationUtils.replacePattern(strip2,
								"(\\ )", "");
					 
					 c = context.getContentResolver().query(
								ALL_INBOX,
								null,
								"thread_id=" + thread_id + " AND " + "address= '"
										+ strip3 + "'",
								null, null);
					 Log.v("ConversationUtils", "strip3 thread_id=" + thread_id + " AND " + "address= '"
								+ recipient_all.get(position).get("number") + "'");
					 
					 if(c==null||c.getCount()==0){
						 
						 
						 Toast.makeText(context, "没有已发信息，不能拆分！", Toast.LENGTH_SHORT).show();

							Log.v("ConversationUtils", "thread_id=" + thread_id + " AND " + "address= '"
									+ recipient_all.get(position).get("number") + "'");
							return false;
					 }
					 
				 }
			 }
			
			

			
		}

		

			c.moveToFirst();

			do {

				int person = c.getInt(c.getColumnIndex("person"));
				long date = c.getLong(c.getColumnIndex("date"));
				long date_sent = c.getLong(c.getColumnIndex("date_sent"));
				int protocol = c.getInt(c.getColumnIndex("protocol"));
				int read = c.getInt(c.getColumnIndex("read"));
				int status = c.getInt(c.getColumnIndex("status"));

				int type = c.getInt(c.getColumnIndex("type"));
				String reply_path_present = c.getString(c
						.getColumnIndex("reply_path_present"));
				String subject = c.getString(c.getColumnIndex("subject"));
				String body = c.getString(c.getColumnIndex("body"));
				String service_center = c.getString(c
						.getColumnIndex("service_center"));
				int service_date = c.getInt(c.getColumnIndex("service_date"));
				int dest_port = c.getInt(c.getColumnIndex("dest_port"));
				int locked = c.getInt(c.getColumnIndex("locked"));
				int sub_id = c.getInt(c.getColumnIndex("sub_id"));
				int error_code = c.getInt(c.getColumnIndex("error_code"));
				int seen = c.getInt(c.getColumnIndex("seen"));
				String recipient_cc_ids = c.getString(c
						.getColumnIndex("recipient_cc_ids"));
				String recipient_bcc_ids = c.getString(c
						.getColumnIndex("recipient_bcc_ids"));
				byte[] sms_pdu = c.getBlob(c.getColumnIndex("sms_pdu"));
				int expiry = c.getInt(c.getColumnIndex("expiry"));
				int pri = c.getInt(c.getColumnIndex("pri"));

				// Log.v("ConversationUtils",
				// "id:"+id+" "+max_thread_id+" "+address+person+date+date_sent+protocol+read+status+type+reply_path_present+subject+body+service_center+service_date+dest_port+dest_port+locked+sub_id+error_code+seen+recipient_cc_ids+recipient_bcc_ids+sms_pdu+" "+expiry+pri);

				values.put("address", address);

				values.put("person", person);

				values.put("date", date);

				values.put("date_sent", date_sent);

				values.put("protocol", protocol);

				values.put("read", read);

				values.put("status", status);

				values.put("type", type);

				values.put("reply_path_present", reply_path_present);

				values.put("subject", subject);

				values.put("body", body);

				values.put("service_center", service_center);

				values.put("service_date", service_date);

				values.put("dest_port", dest_port);

				values.put("locked", locked);

				values.put("sub_id", sub_id);

				values.put("error_code", error_code);

				values.put("seen", seen);

				values.put("recipient_cc_ids", recipient_cc_ids);

				values.put("recipient_bcc_ids", recipient_bcc_ids);

				values.put("sms_pdu", sms_pdu);

				values.put("expiry", expiry);

				values.put("pri", pri);

				context.getContentResolver().insert(ALL_INBOX, values);

			} while (c.moveToNext());

			c.close();

			ContentValues delete_values = new ContentValues();

			delete_values.put("address", address);
			// values.put("address", address);
			Uri rawContentUri = context.getContentResolver().insert(ALL_INBOX,
					delete_values);

			long rawContentId = ContentUris.parseId(rawContentUri);

			Log.v("For Conversationlist", "����һ���յ�rawConentId�� " + rawContentId);

			// Log.v("ConversationList", "rawConentId: "+rawContentId);

			// context.getContentResolver().delete(ALL_INBOX,
			// "_id="+rawContentId, null);
			context.getContentResolver().delete(ALL_INBOX,
					"_id= " + rawContentId, null);

			return true;

		

	}

	public static boolean changeThread2(Context context,
			ArrayList<Map<String, Object>> recipient_all, long thread_id) {
		boolean isMessage = true;
		for (int i = 0; i < recipient_all.size(); i++) {

			isMessage = copyAllvalues(thread_id, (String) recipient_all.get(i)
					.get("number"), context, i, recipient_all);

		}
		return isMessage;
	}

	public static void changeThread(Context context,
			ArrayList<Map<String, Object>> recipient_all, long thread_id) {

		Log.v("For Conversationlist",
				"recipient_all: " + recipient_all.toString() + " thread_id: "
						+ thread_id);

		for (int i = 0; i < recipient_all.size(); i++) {
			Cursor c = context.getContentResolver().query(
					ALL_INBOX,
					null,
					"thread_id=" + recipient_all.get(i).get("thread_id")
							+ " AND " + "address= '"
							+ recipient_all.get(i).get("number") + "'", null,
					null);
			Log.v("For Conversationlist", "thread_id="
					+ recipient_all.get(i).get("thread_id") + " AND "
					+ "address= '" + recipient_all.get(i).get("number") + "'");

			Log.v("For Conversationlist",
					"ִ���˵�һ��if֮ǰ�� c.getCount(): "
							+ c.getCount()
							+ " thread_id: "
							+ thread_id
							+ " (Long)recipient_all.get(i).get(\"thread_id\"): "
							+ (Long) recipient_all.get(i).get("thread_id"));

			if (c.getCount() != 0
					&& (Long) recipient_all.get(i).get("thread_id") != thread_id) {

				Log.v("For Conversationlist",
						"ִ���˵�һ��if�� c.getCount(): "
								+ c.getCount()
								+ " thread_id: "
								+ thread_id
								+ " (Long)recipient_all.get(i).get(\"thread_id\"): "
								+ (Long) recipient_all.get(i).get("thread_id"));

				ContentValues values = new ContentValues();
				values.put("thread_id",
						(Long) recipient_all.get(i).get("thread_id"));

				Log.v("For Conversationlist", "�����valuesֵ: "
						+ (Long) recipient_all.get(i).get("thread_id"));

				context.getContentResolver().update(
						ALL_INBOX,
						values,
						"thread_id = " + thread_id + " AND " + "address = '"
								+ recipient_all.get(i).get("number") + "'",
						null);

				Log.v("For Conversationlist",
						"���º�ģ� thread_id = " + thread_id + " AND "
								+ "address = '"
								+ recipient_all.get(i).get("number") + "'");

				Uri rawContentUri = context.getContentResolver().insert(
						ALL_INBOX, values);

				long rawContentId = ContentUris.parseId(rawContentUri);

				Log.v("For Conversationlist", "����һ���յ�rawConentId�� "
						+ rawContentId);

				// Log.v("ConversationList", "rawConentId: "+rawContentId);

				// context.getContentResolver().delete(ALL_INBOX,
				// "_id="+rawContentId, null);
				context.getContentResolver().delete(ALL_INBOX,
						"_id= " + rawContentId, null);
			}

			else {

				Log.v("For Conversationlist", "ִ����else");

				c = context.getContentResolver()
						.query(ALL_INBOX,
								null,
								"address= '"
										+ recipient_all.get(i).get("number")
										+ "'", null, null);

				if (c.getCount() != 0) {

					c.moveToFirst();
					long thread = c.getLong(c.getColumnIndex("thread_id"));

					ContentValues values = new ContentValues();
					values.put("thread_id", thread);

					context.getContentResolver().update(
							ALL_INBOX,
							values,
							"thread_id = " + thread_id + " AND "
									+ "address = '"
									+ recipient_all.get(i).get("number") + "'",
							null);

				}

				else {

					// c =context.getContentResolver().query(ALL_INBOX,
					// null,"address= '"+recipient_all.get(i).get("number")+"'",
					// null, null);

					c = context.getContentResolver()
							.query(ALL_INBOX,
									new String[] { "MAX(thread_id)" }, null,
									null, null);

					long max_thread_id = c.getLong(0);

					ContentValues values = new ContentValues();
					values.put("thread_id", max_thread_id);

					context.getContentResolver().update(
							ALL_INBOX,
							values,
							"thread_id = " + thread_id + " AND "
									+ "address = '"
									+ recipient_all.get(i).get("number") + "'",
							null);

					// db.query(DATABASE_TABLE, new String [] {"MAX(price)"},
					// null, null, null, null, null);

				}

			}

		}

	}

}
