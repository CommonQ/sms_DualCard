package edu.bupt.mms;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Map;
import java.util.TimeZone;

import com.google.android.mms.MmsException;

import edu.bupt.mms.model.SlideshowModel;
import edu.bupt.mms.numberlocate.NumberLocateSetting;
import edu.bupt.mms.ui.AsyncDialog;
import edu.bupt.mms.ui.ComposeMessageActivity;
import edu.bupt.mms.ui.ConversationList;
import edu.bupt.mms.ui.ConversationUtils;
import edu.bupt.mms.ui.FailedBoxDBHelper;
import edu.bupt.mms.ui.MessageUtils;
import edu.bupt.mms.ui.MessagingPreferenceActivity;
import edu.bupt.mms.ui.SearchResultActivity;
import edu.bupt.mms.ui.TraditionalActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.provider.Telephony.Mms;
import android.provider.Telephony.MmsSms;
import android.provider.Telephony.Sms;
import android.provider.Telephony.MmsSms.PendingMessages;
import android.provider.Telephony.Sms.Conversations;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SqliteWrapper;
import android.telephony.MSimSmsManager;
import android.telephony.MSimTelephonyManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 北邮ANT实验室 by010 QQ
 * 
 * 卡需求管理
 * 
 * （功能序号无）
 * 
 * 未能实现漫游时对两个目录下的卡信息读取，但是可以实现存取发送、接收的卡信息
 * */
public class TestActivity extends Activity {

	public static Uri ALL_INBOX = Uri.parse("content://sms/");
	private static final Uri ICC_URI = Uri.parse("content://sms/icc2");

	public static final Uri CONVERSATION = Uri
			.parse("content://mms-sms/conversations/");

	public static boolean isRoaming = false;

	private static final Context mContext = MmsApp.getApplication()
			.getApplicationContext();

	FailedBoxDBHelper dbHelper;

	Button insert = null;

	Button insert_values = null;

	String[] arrayOfString4 = new String[7];
	// static final String[]
	// PROJECTION={"_id","thread_id","address","body","type","read","sms_pdu"};

	static final String[] PROJECTION = new String[] {
			// TODO: should move this symbol into
			// edu.bupt.mms.telephony.Telephony.
			MmsSms.TYPE_DISCRIMINATOR_COLUMN,
			BaseColumns._ID,
			Conversations.THREAD_ID,
			// For SMS
			Sms.ADDRESS, Sms.BODY, Sms.SUB_ID, Sms.DATE, Sms.DATE_SENT,
			Sms.READ, Sms.TYPE, Sms.STATUS, Sms.LOCKED,
			Sms.ERROR_CODE,
			// For MMS
			Mms.SUBJECT, Mms.SUBJECT_CHARSET, Mms.DATE, Mms.DATE_SENT,
			Mms.READ, Mms.MESSAGE_TYPE, Mms.MESSAGE_BOX, Mms.DELIVERY_REPORT,
			Mms.READ_REPORT, PendingMessages.ERROR_TYPE, Mms.LOCKED, Mms.STATUS };

	static final String[] SERVICE_CENTER_PROJECTION = { "service_center" };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {

			// ArrayList<String> numbers =(ArrayList<String>)
			// data.getExtra("ret");

			Uri uri = (Uri) data.getExtra("ret");

			Log.v("TestActivity", "" + uri);

		}

	}

	public static boolean copySmsToSim(Context paramContext, int paramInt,
			long paramLong) {

		Log.v("TestActivity", "copySmsToSim + modeId: " + paramInt);
		Cursor localCursor = SqliteWrapper.query(paramContext, paramContext
				.getContentResolver(), ContentUris.withAppendedId(
				Telephony.Sms.CONTENT_URI, paramLong), PROJECTION, null, null,
				null);

		if (localCursor != null) {
			localCursor.moveToFirst();
			if (localCursor.getInt(4) != 1) {
				copyOutgoingSmsToSim(paramContext, localCursor, paramInt);

			} else {

				// copyIncomingSmsToSim(localCursor, paramInt);

			}

		}

		localCursor.close();

		return false;

	}

	public static boolean copyMessage(Context context, int subId, long id) {

		Log.v("TestActivity", "copySmsToSim + modeId: " + subId);

		Cursor localCursor = SqliteWrapper.query(context,
				context.getContentResolver(),
				ContentUris.withAppendedId(ALL_INBOX, id), null, null, null,
				null);

		if (localCursor != null && localCursor.moveToFirst()) {
			String service_center = localCursor.getString(localCursor
					.getColumnIndex("service_center"));
			String address = localCursor.getString(localCursor
					.getColumnIndex("address"));
			long date = localCursor.getLong(localCursor.getColumnIndex("date"));
			int type = localCursor.getInt(localCursor.getColumnIndex("type"));
			String body = localCursor.getString(localCursor
					.getColumnIndex("body"));
			int sub_id = localCursor.getInt(localCursor
					.getColumnIndex("sub_id"));
			int status = 5;
			if (type == 1) {
				status = 7;

			} else {
				status = 5;
			}

			// zaizhe�ҽ���servicecenter�ֶ�������ʱ����ʾ���ֶκðɣ���
			body += "***" + date;

			Log.v("zzz", "service_center: " + service_center + " date: " + date);
			SmsMessage.SubmitPdu localSubmitPdu = getSubmitPdu(service_center,
					address, body, false, sub_id);

			// copyMessageToIcc(localSubmitPdu.encodedScAddress,
			// localSubmitPdu.encodedMessage, 1, 0);
			// Log.e("Mms", "pdu.encodedMessage.length = " +
			// localSubmitPdu.encodedScAddress.length);
			boolean isSuccess = MSimSmsManager.getDefault().copyMessageToIcc(
					localSubmitPdu.encodedScAddress,
					localSubmitPdu.encodedMessage, status, sub_id);
			if (localSubmitPdu == null || isSuccess == false)
				Log.v("zzz", "isSuccess = " + isSuccess);
			else
				Log.v("zzz", "isSuccess = " + isSuccess);

			ArrayList<SmsMessage> hello = MSimSmsManager.getDefault()
					.getAllMessagesFromIcc(0);

			SmsMessage ss = hello.get(0);

			localCursor.close();
			return isSuccess;

		} else
			return false;
	}

	public static boolean copyMessageToIcc(byte[] paramArrayOfByte1,
			byte[] paramArrayOfByte2, int paramInt1, int paramInt2) {
		boolean bool = false;
		Object[] arrayOfObject = new Object[4];
		arrayOfObject[0] = paramArrayOfByte1;
		arrayOfObject[1] = paramArrayOfByte2;
		arrayOfObject[2] = Integer.valueOf(paramInt1);
		arrayOfObject[3] = Integer.valueOf(paramInt2);
		Class[] arrayOfClass = new Class[4];
		arrayOfClass[0] = byte[].class;
		arrayOfClass[1] = byte[].class;
		arrayOfClass[2] = Integer.TYPE;
		arrayOfClass[3] = Integer.TYPE;

		if (paramArrayOfByte2 != null) {

			try {
				Log.e("DualMode", "130121 reflect call copyMessageToIcc smsc:"
						+ paramArrayOfByte1 + ",pdu:" + paramArrayOfByte2
						+ ",status:" + paramInt1 + ",subscription:" + paramInt2);
				Object localObject = LoadMethod(null,
						GetMmsZteExtendsInterfaceObj(mContext),
						"copyMessageToIcc", arrayOfObject, arrayOfClass);
				if (localObject != null) {
					bool = ((Boolean) localObject).booleanValue();
					Log.e("DualMode", "130121 copyMessageToIcc obj!=null");
				}
				Log.e("DualMode", "130121 copyMessageToIcc statusRet:" + bool);
				return bool;
			} catch (Exception localException) {
				Log.e("DualMode",
						"130121 copyMessageToIcc catch exception  error:"
								+ localException);
				bool = false;
				localException.printStackTrace();

			}
		}
		return bool;

	}

	public static SmsMessage.SubmitPdu getSubmitPdu(String paramString1,
			String paramString2, String paramString3, boolean paramBoolean,
			int paramInt) {

		Object[] arrayOfObject = new Object[5];
		arrayOfObject[0] = paramString1;
		arrayOfObject[1] = paramString2;
		arrayOfObject[2] = paramString3;
		arrayOfObject[3] = Boolean.valueOf(paramBoolean);
		arrayOfObject[4] = Integer.valueOf(paramInt);
		Class[] arrayOfClass = new Class[5];
		arrayOfClass[0] = String.class;
		arrayOfClass[1] = String.class;
		arrayOfClass[2] = String.class;
		arrayOfClass[3] = Boolean.TYPE;
		arrayOfClass[4] = Integer.TYPE;
		Object localObject1 = null;
		SmsMessage.SubmitPdu localSubmitPdu = null;

		try {
			Object localObject2 = LoadMethod(null,
					GetMmsZteExtendsInterfaceObj(mContext), "getSubmitPdu",
					arrayOfObject, arrayOfClass);
			localObject1 = localObject2;
			if (localObject1 != null) {
				localSubmitPdu = (SmsMessage.SubmitPdu) localObject1;

				Log.v("GetSubmitPdu", "localSubmitPdu !=null"
						+ localSubmitPdu.encodedMessage);
			}
			return localSubmitPdu;
		} catch (Exception localException) {
			Log.e("DualMode", "LoadMethod:Telephony.getSubmitPdu  error:"
					+ localException);
			localException.printStackTrace();
			return null;
		}

	}

	public static Object GetMmsZteExtendsInterfaceObj(Context paramContext) {

		Object localObject1 = null;
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = paramContext;
		Class[] arrayOfClass = new Class[1];
		arrayOfClass[0] = Context.class;
		Object localObject2 = null;
		try {
			localObject2 = LoadMethod(
					"com.zte.mmsZteExtendsInterface.InterfaceLayer", null,
					"getInstance", arrayOfObject, arrayOfClass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localObject1 = localObject2;

		return localObject1;

	}

	public static Object LoadMethod(String paramString1, Object paramObject,
			String paramString2, Object[] paramArrayOfObject,
			Class[] paramArrayOfClass) throws Exception {

		if (paramObject == null) {

			Class localClass = Class.forName(paramString1);

			Object localObject2;

			localObject2 = localClass
					.getMethod(paramString2, paramArrayOfClass).invoke(
							paramObject, paramArrayOfObject);

			return localObject2;

		} else {

			Class localClass = paramObject.getClass();

			Object localObject2;

			localObject2 = localClass
					.getMethod(paramString2, paramArrayOfClass).invoke(
							paramObject, paramArrayOfObject);

			return localObject2;
		}

	}

	public static boolean copyOutgoingSmsToSim(Context paramContext,
			Cursor paramCursor, int paramInt) {

		// String str = getMessageServiceCenter(paramContext,
		// paramCursor.getLong(1));
		String str = "+8613800100500";
		Log.i("Mms", "ServiceCenter of copyOutgoingSmsToSim=" + str
				+ paramCursor.getLong(0) + paramCursor.getString(2)
				+ paramCursor.getString(3) + paramCursor.getLong(4)
				+ paramCursor.getLong(5) + paramCursor.getLong(6));

		// zaizhe
		// û����paramInt�����ʾ��һ���ǿ������ֶΣ���֪��������˫���ֻ��Ƿ���Ӱ�졣��Ըû��Ӱ�졣���о��÷���ķ�����

		SmsMessage.SubmitPdu localSubmitPdu = SmsMessage.getSubmitPdu(
				"+8613800100500", paramCursor.getString(2),
				paramCursor.getString(3), false);
		Log.i("Mms", "�ɹ����𣿣�" + str);
		SmsManager.getDefault().copyMessageToIcc(
				localSubmitPdu.encodedScAddress, localSubmitPdu.encodedMessage,
				paramCursor.getInt(4));
		Log.i("Mms", "�ɹ����𣿣� encodedMessage:"
				+ localSubmitPdu.encodedMessage + " encodedScAddress: "
				+ localSubmitPdu.encodedScAddress);
		// MSimSmsManager.getDefault().copyMessageToIcc(localSubmitPdu.encodedScAddress,
		// localSubmitPdu.encodedMessage, paramCursor.getInt(4), paramInt);

		Log.i("Mms", "�ɹ����𣡣�" + str);

		// SmsMessage.SubmitPdu localSubmitPdu=getSubmitPdu(str,
		// paramCursor.getString(2), paramCursor.getString(3), false, paramInt);

		return false;

	}

	public static String getMessageServiceCenter(Context paramContext,
			long paramLong) {
		Cursor localCursor = null;

		localCursor = SqliteWrapper.query(paramContext,
				paramContext.getContentResolver(), Telephony.Sms.CONTENT_URI,
				SERVICE_CENTER_PROJECTION, "thread_id = " + paramLong, null,
				"date DESC");

		String result = null;

		if (localCursor != null) {
			localCursor.moveToFirst();

			String str = localCursor.getString(0);

			result = str;

		}

		localCursor.close();

		return result;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		dbHelper = new FailedBoxDBHelper(this, "myBox.db3", 1);

		insert = (Button) findViewById(R.id.insert_test);
		insert_values = (Button) findViewById(R.id.insert_values);

		insert_values.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View source) {

				Log.v("TestActivity", "thread Start!");

				// Object
				// object1=GetMmsZteExtendsInterfaceObj(TestActivity.this);
				// String str = getMessageServiceCenter(mContext,
				// "+8613010314501");

				SmsMessage.SubmitPdu localSubmitPdu = getSubmitPdu(null,
						"18612978001", "�Ѷ�����һ�£���", false, 1);

				copyMessageToIcc(localSubmitPdu.encodedScAddress,
						localSubmitPdu.encodedMessage, 1, 1);
				// Log.e("Mms", "pdu.encodedMessage.length = " +
				// localSubmitPdu.encodedScAddress.length);
				// MSimSmsManager.getDefault().copyMessageToIcc(null,
				// localSubmitPdu.encodedMessage, 1, 1);
				if (localSubmitPdu == null)
					Toast.makeText(getApplicationContext(),
							"localSubmitPdu is null", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(
							getApplicationContext(),
							"localSubmitPdu isn't null"
									+ localSubmitPdu.encodedMessage,
							Toast.LENGTH_LONG).show();

				// int aa = MSimTelephonyManager.getDefault().getNetworkType(0);
				// // MSimTelephonyManager.getDefault().
				// int cc = TelephonyManager.NETWORK_TYPE_EVDO_A;
				//
				// String aa1 = MSimTelephonyManager.getDefault()
				// .getNetworkTypeName(0);
				// int bb = TelephonyManager.getDefault().getNetworkType();
				//
				// String bb1 =
				// TelephonyManager.getDefault().getNetworkTypeName();
				//
				// boolean ccc = false;
				//
				// if (MSimTelephonyManager.getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_EVDO_0
				// || MSimTelephonyManager.getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_EVDO_A
				// || MSimTelephonyManager.getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_EVDO_B
				// || MSimTelephonyManager.getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_CDMA
				// || MSimTelephonyManager.getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_EVDO_0) {
				//
				// ccc = true;
				// } else {
				//
				// ccc = false;
				// }
				//
				// boolean ddd = false;
				//
				// if (TelephonyManager.getDefault().isNetworkRoaming()) {
				//
				// ddd = true;
				// } else {
				//
				// ddd = false;
				// }
				//
				// boolean eee = false;
				//
				// if (TelephonyManager.getDefault().isNetworkRoaming()
				// && (MSimTelephonyManager.getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_EVDO_0
				// || MSimTelephonyManager.getDefault()
				// .getNetworkType(0) == TelephonyManager.NETWORK_TYPE_EVDO_A
				// || MSimTelephonyManager.getDefault()
				// .getNetworkType(0) == TelephonyManager.NETWORK_TYPE_EVDO_B
				// || MSimTelephonyManager.getDefault()
				// .getNetworkType(0) == TelephonyManager.NETWORK_TYPE_CDMA ||
				// MSimTelephonyManager
				// .getDefault().getNetworkType(0) ==
				// TelephonyManager.NETWORK_TYPE_EVDO_0)) {
				//
				// eee = true;
				// } else {
				//
				// eee = false;
				// }
				//
				// Toast.makeText(
				// TestActivity.this,
				// "getNetworkType(0): " + aa + "getNetworkTypeName(0): "
				// + aa1 + " getNetworkOperator():" + bb
				// + " getNetworkTypeName():" + bb1 + " isctoc: "
				// + ccc + " isnetworkroaming: " + ddd
				// + " isctocnetwork: " + eee, Toast.LENGTH_LONG)
				// .show();

				// Uri threadUri = ContentUris.withAppendedId(CONVERSATION, 1);
				//
				// ArrayList<MultiDeleteItem> items = new
				// ArrayList<MultiDeleteItem>();
				//
				//
				// Cursor
				// cursor=TestActivity.this.getContentResolver().query(threadUri,
				// PROJECTION, null, null, null);
				//
				// if(cursor.moveToFirst()){
				//
				// do{
				// if(cursor.getString(0).equalsIgnoreCase("sms")){
				//
				// MultiDeleteItem item=new MultiDeleteItem();
				//
				// item.smsType=cursor.getString(0);
				//
				// item.mAddress=cursor.getString(3);
				//
				// item.mBody=cursor.getString(4);
				//
				// item.date=cursor.getLong(6);
				//
				// item.type=cursor.getInt(9);
				//
				// item.MsgId=cursor.getInt(1);
				//
				// items.add(item);
				//
				//
				//
				//
				// }else if(cursor.getString(0).equalsIgnoreCase("mms")){
				//
				// MultiDeleteItem item=new MultiDeleteItem();
				//
				//
				// item.smsType=cursor.getString(0);
				// item.MsgId=cursor.getInt(1);
				//
				// String mmsSub =cursor.getString(13);
				//
				// if(mmsSub!=null&&mmsSub.equals("")){
				// try {
				// mmsSub = new String(mmsSub.getBytes("ISO8859_1"),
				// "utf-8");
				// } catch (UnsupportedEncodingException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }else{
				//
				// mmsSub="";
				//
				// }
				// item.mSubject=mmsSub;
				//
				// item.date=cursor.getLong(15);
				//
				// item.type=cursor.getInt(19);
				//
				// items.add(item);
				//
				// // item.mSubject=cursor.getInt(1);
				//
				//
				//
				// }
				//
				// // Log.v("TestACtivity", cursor.getLong(0));
				//
				//
				// String type = cursor.getString(0);
				//
				// String msgId = cursor.getString(3);
				//
				// Log.v("TestActivity", "ca0: "+type+" msgid: "+msgId);
				//
				// }while(cursor.moveToNext());
				//
				//
				//
				//
				//
				// }
				//
				//
				//
				// Intent intent = new Intent(TestActivity.this,
				// MultiDeleteActivity.class);
				//
				// intent.putExtra("mlist", items);
				// //intent.put
				// startActivity(intent);

				//
				// Intent clickIntent = new Intent(TestActivity.this,
				// MultiDeleteActivity.class);
				//
				// startActivity(clickIntent);

				//
				// Settings.System.putInt(TestActivity.this.getContentResolver(),
				// Settings.System.AUTO_TIME_ZONE, 1);
				//
				// Settings.System.putInt(TestActivity.this.getContentResolver(),
				// Settings.System.AUTO_TIME, 1);
				//
				// isRoaming=false;

				//
				// Intent testIntent = new
				// Intent(TestActivity.this,NumberLocateSetting.class);
				//
				//
				// startActivity(testIntent);
				//

				// TODO Auto-generated method stub

				// String NetworkOperator=
				// MSimTelephonyManager.getDefault().getNetworkOperator(0);

				//
				// int NetworkOperator=
				// MSimTelephonyManager.getDefault().getNetworkType(0);
				// //
				// MSimTelephonyManager.getDefault().getNetworkOperator(subscription)
				// Log.v("TestActivity",
				// "current NetworkOperator: "+NetworkOperator);
				//
				// int NetworkOperator1=
				// MSimTelephonyManager.getDefault().getNetworkType(1);
				//
				//
				// //int evdo_a= TelephonyManager.NETWORK_TYPE_EVDO_A;
				// Log.v("TestActivity",
				// "current NetworkOperator1: "+NetworkOperator1);
				//
				//
				//
				// String getNetworkOperatorName=
				// MSimTelephonyManager.getDefault().getNetworkTypeName(0);
				//
				// Log.v("TestActivity",
				// "current getNetworkOperatorName: "+getNetworkOperatorName);
				//
				//
				// String getNetworkOperatorName1=
				// MSimTelephonyManager.getDefault().getNetworkTypeName(1);
				//
				// Log.v("TestActivity",
				// "current getNetworkOperatorName1: "+getNetworkOperatorName1);
				//
				// String aa =
				// MSimTelephonyManager.getDefault().getNetworkOperator(0);
				// String aa1=
				// MSimTelephonyManager.getDefault().getNetworkOperator(1);
				// String bb=
				// MSimTelephonyManager.getDefault().getNetworkTypeName(0);
				// String bb1=
				// MSimTelephonyManager.getDefault().getNetworkTypeName(1);
				//
				//
				//
				// Toast.makeText(TestActivity.this,"current NetworkOperator: "+NetworkOperator+
				// "current NetworkOperator1: "+NetworkOperator1+"current getNetworkOperatorName: "+getNetworkOperatorName+"current getNetworkOperatorName1: "+getNetworkOperatorName1+"getNetworkOperator: "+aa+"  "+aa1+" getNetworkOperator:"+bb+" "+bb1,
				// Toast.LENGTH_LONG).show();
				//
				//
				// //
				// // String iso=MmsApp.getApplication().getCurrentCountryIso();
				// //
				// // Log.v("TestActivity", "current iso: "+iso);
				// //
				// //
				// // String iso2=TimeZone.getDefault().getDisplayName();
				// //
				// //
				// // Log.v("TestActivity", "current TimeZone: "+iso2);
				//
				// // Intent onClickIntent = new Intent(TestActivity.this,
				// ComposeMessageActivity.class);
				// // onClickIntent.putExtra("thread_id",Long.parseLong("3"));
				// // onClickIntent.putExtra("highlight","�����");
				// // onClickIntent.putExtra("select_id",Long.parseLong("272"));
				// // startActivity(onClickIntent);
				//
				// //Log.v("SearchActivity for test!!!!",
				// "thread_id: "+threadId+"highlight: "+searchString+"select_id:"+rowid);
				//
				//
				//
				// // Intent intent = new
				// Intent("com.android.contacts.action.GET_MULTIPLE_PHONES",ContactsContract.Contacts.CONTENT_URI);
				// //
				// // intent.putExtra("package_vcard", 1);
				// //
				// //
				// //intent.setAction("com.android.contacts.action.GET_MULTIPLE_PHONES",ContactsContract.Contacts.CONTENT_URI);
				// //
				// //// Intent(ACTION_GET_MULTIPLE_PHONES,
				// //// // ContactsContract.Contacts.CONTENT_URI);
				// //
				// //
				// // startActivityForResult(intent, 0);
				//
				//
				//
				//
				//
				// // MessageUtils.viewMmsMessageAttachment(
				// // TestActivity.this,
				// // msgItem.mMessageUri, msgItem.mSlideshow,
				// // getAsyncDialog());
				//
				// // String mms="content://mms/44";
				// // Uri uri =Uri.parse(mms);
				// // SlideshowModel mSlideshow = null;
				// // try {
				// //
				// // mSlideshow =
				// SlideshowModel.createFromMessageUri(TestActivity.this, uri);
				// // } catch (MmsException e) {
				// // // TODO Auto-generated catch block
				// // e.printStackTrace();
				// // }
				// //
				// // AsyncDialog mAsyncDialog = new
				// AsyncDialog(TestActivity.this);
				// //
				// // MessageUtils.viewMmsMessageAttachment(
				// // TestActivity.this,
				// // uri, mSlideshow,
				// // mAsyncDialog);
				//
				//
				//
				//
				//
				//
				//
				//

			}

		});

		insert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View source) {

				Log.v("TestActivity", "thread Start!");

				if (ComposeMessageActivity.selectMode == 1) {
					ComposeMessageActivity.selectMode = 2;
				} else if (ComposeMessageActivity.selectMode == 2) {
					ComposeMessageActivity.selectMode = 1;
				}

				// SmsMessage.SubmitPdu localSubmitPdu =getSubmitPdu(null,
				// "18612978001",
				// "����null servicex 18612978001", false,1);
				//
				//
				// //copyMessageToIcc(localSubmitPdu.encodedScAddress,
				// localSubmitPdu.encodedMessage, 1, 0);
				// // Log.e("Mms", "pdu.encodedMessage.length = " +
				// localSubmitPdu.encodedScAddress.length);
				// MSimSmsManager.getDefault().copyMessageToIcc(null,
				// localSubmitPdu.encodedMessage, 5, 1);
				// if(localSubmitPdu==null)
				// Toast.makeText(getApplicationContext(),
				// "localSubmitPdu is null", Toast.LENGTH_LONG).show();
				// else
				// Toast.makeText(getApplicationContext(),
				// "localSubmitPdu isn't null"+localSubmitPdu.encodedMessage,
				// Toast.LENGTH_LONG).show();
				//
				//

				// SmsManager sManager = SmsManager.getDefault();
				// SmsMessage.SubmitPdu pdu =
				// SmsMessage.getSubmitPdu("+8613010314501", "10655752",
				// "welcome to you.", false);
				// Log.v("TestActivity", "Copy Start!");
				// MSimSmsManager.getDefault().copyMessageToIcc(pdu.encodedScAddress,pdu.encodedMessage,1,0);
				// Log.v("TestActivity", "Copy Finish!");

				// String sSc=null;
				// ContentValues values = new ContentValues();
				// values.put("type", Integer.valueOf(2));
				// values.put("address", "13953331331");
				// values.put("body", "have a try");
				// values.put("date", Long.valueOf("1377857126411"));
				// values.put("service_center_address", sSc);
				// Uri rawContentUri =
				// TestActivity.this.getContentResolver().insert(Uri.parse("content://sms/icc"),
				// values);
				//
				//

				// Intent intent = new Intent(TestActivity.this,
				// SimManageActivity.class);
				// startActivity(intent);

				// TODO Auto-generated method stub

				// if (MessageUtils.isShowBJTime == false)
				// MessageUtils.isShowBJTime = true;
				// else
				// MessageUtils.isShowBJTime = false;

				// Time now = new Time();
				//
				// now.setToNow();
				//
				// long millis = now.toMillis(false);
				//
				// int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				// | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;
				//
				// // format_flags |= DateUtils.FORMAT_SHOW_YEAR
				// // | DateUtils.FORMAT_SHOW_DATE;
				//
				// format_flags |= DateUtils.FORMAT_SHOW_TIME;
				// Formatter format2=new Formatter();
				//
				// format2 =DateUtils.formatDateRange(getApplicationContext(),
				// format2, millis, millis, format_flags, "Europe/paris");
				//
				//
				// // SimpleDateFormat ha = new SimpleDateFormat();
				//
				// Toast.makeText(getApplicationContext(),
				// format2.toString()+" Time.getCurrentTimezone():"+Time.getCurrentTimezone(),
				// Toast.LENGTH_LONG).show();
				//
				//

				// Intent intent = new Intent(TestActivity.this,
				// NumberLocateSetting.class);
				//
				// startActivity(intent);

				//
				// AlarmManager timeZone = (AlarmManager)
				// getSystemService(ALARM_SERVICE);
				// timeZone.setTimeZone("Europe/Paris");
				//
				// Settings.System.putInt(TestActivity.this.getContentResolver(),
				// Settings.System.AUTO_TIME_ZONE, 0);
				//
				// Settings.System.putInt(TestActivity.this.getContentResolver(),
				// Settings.System.AUTO_TIME, 0);
				//
				// isRoaming=true;
				//
				//
				// ContentValues values =getValues(71);
				//
				// Log.v("TestActivity", values.toString());
				//
				// ContentResolver solver
				// =TestActivity.this.getContentResolver();
				// Uri uri=solver.insert(ICC_URI, values);
				//
				// Log.v("TestActivity","uri: "+ uri);
				//

				// String sSc = null;
				// Uri uSim = null;
				// ContentValues values = new ContentValues();
				// values.put("type", Integer.valueOf(2));
				// values.put("address", "13953331331");
				// values.put("body", "����д��һ��");
				// values.put("data", Long.valueOf("1377857126411"));
				// values.put("service_center", sSc);

				// Log.("ComposeMessageActivity", "start copy msg to UIM");
				// sSc =
				// TestActivity.this.getContentResolver.insert(Uri.parse("content://sms/icc",
				// values));

				// SmsManager localSmsManager = SmsManager.getDefault();

				// TestActivity.this.getContentResolver().insert(Uri.parse("content://sms/icc"),
				// values);

				// copySmsToSim(TestActivity.this,0,72);

				// SmsMessage.getSubmitPdu(scAddress, destinationAddress,
				// destinationPort, data, statusReportRequested)

				// MSimSmsManager.getDefault().copyMessageToIcc(arg0, arg1,
				// arg2, arg3)
				// String a ="hello motto";
				//
				// byte[] b=a.getBytes();
				//
				// // MSimSmsManager.getDefault().copyMessageToIcc(null, b, 1,
				// 0);
				// SmsManager.getDefault().copyMessageToIcc(null, b, 1);

				// Intent intent = new Intent(TestActivity.this,
				// TraditionalActivity.class);
				//
				// startActivity(intent);
				//

				// SQLiteDatabase db = dbHelper.getWritableDatabase();
				//
				// ContentValues values = new ContentValues();
				// values = getValues(199);
				//
				// db.insert("failedbox", null, values);
				//
				// db.close();

				//
				// ContentValues values = new ContentValues();
				// values=getFailedValues(db,1);
				//
				// insertToSms(values);

				// deleteFailedValues(db,1);
				// deleteValues(207);

				// insertToFailedBox(db,60);

				// insertToFailedBox(db,169);
				// insertToFailedBox(db,160);
				// insertToFailedBox(db,182);
				// db.close();

			}

		});

		//
		// ContentValues values = new ContentValues();
		//
		// values.put("type",2);

		// getContentResolver().update(ALL_INBOX, values, "_id=199", null);

		// insertValues(60);

	}

	public ContentValues getFailedValues(SQLiteDatabase db, long id) {
		String[] args = { String.valueOf(id) };
		Cursor c = db.query("failedbox", null, "_id=?", args, null, null, null);

		c.moveToFirst();

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
		String service_center = c.getString(c.getColumnIndex("service_center"));
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

		Log.v("ConversationUtils", "id:" + id + " " + threadId + " " + address
				+ person + date + date_sent + protocol + read + status + type
				+ reply_path_present + subject + body + service_center
				+ service_date + dest_port + dest_port + locked + sub_id
				+ error_code + seen + recipient_cc_ids + recipient_bcc_ids
				+ sms_pdu + " " + expiry + pri);

		ContentValues values = new ContentValues();

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

		return values;

	}

	public void deleteFailedValues(SQLiteDatabase db, long id) {

		String[] args = { String.valueOf(id) };

		db.delete("failedbox", "_id=?", args);

	}

	public void insertToSms(ContentValues values) {

		getContentResolver().insert(ALL_INBOX, values);

		ContentValues delete_values = new ContentValues();

		delete_values.put("address", (String) values.get("address"));
		// values.put("address", address);
		Uri rawContentUri = getContentResolver().insert(ALL_INBOX,
				delete_values);

		long rawContentId = ContentUris.parseId(rawContentUri);

		Log.v("For Conversationlist", "����һ���յ�rawConentId�� " + rawContentId);

		// Log.v("ConversationList", "rawConentId: "+rawContentId);

		// context.getContentResolver().delete(ALL_INBOX, "_id="+rawContentId,
		// null);
		getContentResolver().delete(ALL_INBOX, "_id= " + rawContentId, null);

	}

	public void insertToFailedBox(SQLiteDatabase db, long id) {

		ContentValues values = new ContentValues();
		values = getValues(id);
		db.insert("failedbox", null, values);

	}

	// ɾ�������ݿ�ĳ����Ϣ����idΪ����

	public void deleteValues(long id) {

		getContentResolver().delete(ALL_INBOX, "_id= " + id, null);

	}

	// �õ�������ݿ��������Ϣ����idΪ����

	public ContentValues getValues(long id) {

		Cursor c = getContentResolver().query(ALL_INBOX, null, "_id=" + id,
				null, null);

		c.moveToFirst();

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
		String service_center = c.getString(c.getColumnIndex("service_center"));
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

		// String sms_pdu = c.getString(c.getColumnIndex("sms_pdu"));
		int expiry = c.getInt(c.getColumnIndex("expiry"));
		int pri = c.getInt(c.getColumnIndex("pri"));

		Log.v("ConversationUtils", "id:" + id + " " + threadId + " " + address
				+ person + date + date_sent + protocol + read + status + type
				+ reply_path_present + subject + body + service_center
				+ service_date + dest_port + dest_port + locked + sub_id
				+ error_code + seen + recipient_cc_ids + recipient_bcc_ids
				+ sms_pdu + " " + expiry + pri);

		ContentValues values = new ContentValues();

		// values.put("thread_id",threadId);

		values.put("address", address);

		// values.put("person", person);

		values.put("date", date);

		// values.put("date_sent", date_sent);

		// values.put("protocol", protocol);

		// values.put("read", read);

		// values.put("status", status);

		values.put("type", type);

		// values.put("reply_path_present", reply_path_present);

		// values.put("subject", subject);

		values.put("body", body);

		values.put("service_center", service_center);

		// values.put("service_date", service_date);

		// values.put("dest_port", dest_port);

		// values.put("locked", locked);

		// values.put("sub_id", sub_id);

		// values.put("error_code",error_code);

		// values.put("seen", seen);

		// values.put("recipient_cc_ids", recipient_cc_ids);

		// values.put("recipient_bcc_ids", recipient_bcc_ids);

		// values.put("sms_pdu", sms_pdu);

		// values.put("expiry", expiry);

		// values.put("pri", pri);

		return values;

	}

	// ���������ݿ�ĳ����Ϣ����idΪ����
	public void insertValues(long id) {

		Cursor c = getContentResolver().query(ALL_INBOX, null, "_id=" + id,
				null, null);

		c.moveToFirst();

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
		String service_center = c.getString(c.getColumnIndex("service_center"));
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

		Log.v("ConversationUtils", "id:" + id + " " + threadId + " " + address
				+ person + date + date_sent + protocol + read + status + type
				+ reply_path_present + subject + body + service_center
				+ service_date + dest_port + dest_port + locked + sub_id
				+ error_code + seen + recipient_cc_ids + recipient_bcc_ids
				+ sms_pdu + " " + expiry + pri);

		ContentValues values = new ContentValues();

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

		getContentResolver().insert(ALL_INBOX, values);

		ContentValues delete_values = new ContentValues();

		delete_values.put("address", address);
		// values.put("address", address);
		Uri rawContentUri = getContentResolver().insert(ALL_INBOX,
				delete_values);

		long rawContentId = ContentUris.parseId(rawContentUri);

		Log.v("For Conversationlist", "����һ���յ�rawConentId�� " + rawContentId);

		// Log.v("ConversationList", "rawConentId: "+rawContentId);

		// context.getContentResolver().delete(ALL_INBOX, "_id="+rawContentId,
		// null);
		getContentResolver().delete(ALL_INBOX, "_id= " + rawContentId, null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
