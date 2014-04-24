package edu.bupt.mms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.bupt.mms.CardDetailActivity;
import edu.bupt.mms.CardListActivity;
import edu.bupt.mms.DeletePhrTempActivity;
import edu.bupt.mms.DropDownActivity;
import edu.bupt.mms.R;
import edu.bupt.mms.SeperateCardActivity;

public class ReadSMS extends Activity {
	public EditText editText1;
	public RadioButton button_in, button_draft, button_out, button_have_sent,
			button_rubbish;
	public ImageButton button1;
	public static String input;
	public static int flag = 0;
	public Bundle s;
	public ListView listview;
	// public TextView title;

	public TextView tv_SMSNum;
	public static SimpleAdapter listItemAdapter;
	public ArrayList<HashMap<String, Object>> list;
	public static SMSItem sms;
	public static ContactItem contact;
	public static SMSReader smsReader;

	public static SMSItemFailed smsFailed;
	public static ContactItemFailed contactFailed;

	public static SMSFailedReader smsFailedReader;

	public static int index, indexlong;

	public static ArrayList<Long> delete_IDs = new ArrayList<Long>();

	public static long delete_ID;

	public static final int MENU_DELETE_UNREAD = 0x1;
	public static final int MENU_SEARCH = 0x10;

	private DatePicker datePickerFrom, datePickerTo;

	public static int model_flag = 1;

	public Cursor myCursor;

	private int cfromyear = 0;
	private int cfrommonth = 0;
	private int cfromday = 0;
	private int ctoyear = 0;
	private int ctomonth = 0;
	private int ctoday = 0;

	public int totalSMS = 0;
	public int unreadSMS = 0;

	public static Calendar cfrom;
	public static Calendar cto;

	public boolean isShow = true;

	public LinearLayout mLinearLayout2, mLinearLayoutbox;

	public static Uri ALL_INBOX = Uri.parse("content://sms/");

	SparseBooleanArray array = null;

	Handler mHandler;

	public static String thread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		this.s = savedInstanceState;

		setContentView(R.layout.traditional);
		// 010 editText1 = (EditText)findViewById(R.id.edittext);
		// editText1.setBackgroundResource(R.drawable.search);
		// 010 input = editText1.getText().toString();

		fillListView();

		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listview.setMultiChoiceModeListener(new ModeCallback());

		// title = (TextView)findViewById(R.id.title);
		// title.setVisibility(8);

		getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true,
				new SmsObserver(this, new Handler()));

		// 010 button1 = (ImageButton)findViewById(R.id.button1);
		// button_new = (Button)findViewById(R.id.newSMS);
		button_in = (RadioButton) findViewById(R.id.button_in);
		button_out = (RadioButton) findViewById(R.id.button_out);
		button_draft = (RadioButton) findViewById(R.id.button_draft);
		button_have_sent = (RadioButton) findViewById(R.id.button_have_sent);
		button_rubbish = (RadioButton) findViewById(R.id.button_rubbish);
		// tradition_button = (Button)findViewById(R.id.tradition);
		// speek_button = (Button)findViewById(R.id.speek);
		// mLinearLayout1 = (LinearLayout)findViewById(R.id.model);
		//mLinearLayout2 = (LinearLayout) findViewById(R.id.listhead);
		mLinearLayoutbox = (LinearLayout) findViewById(R.id.listbox);
		tv_SMSNum = (TextView) findViewById(R.id.smsText);
		// mLinearLayout1.setVisibility(8);

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				if (msg.what == 123) {
					list.clear();
					listItemAdapter.notifyDataSetChanged();

					fillListView();

				}

				super.handleMessage(msg);
			}

		};

		button_in.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_in.setBackgroundColor(Color.rgb(162,194,230));
				// button_out.setBackgroundColor(Color.rgb(71,134,198));
				// button_draft.setBackgroundColor(Color.rgb(71,134,198));

				model_flag = 1;
				isShow = true;
				// myCursor = ReadSMS.managedQuery(SMS_INBOX, null, null, null,
				// "date DESC");

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");

				Log.v("ReadSMS for test", "点击了 button_in model_flag="
						+ model_flag);
				fillListView();
				totalSMS = SMSReader.cur00.getCount();
				unreadSMS = SMSReader.unreadCount;

				tv_SMSNum.setText("" + totalSMS + "条信息" + " " + unreadSMS
						+ " 未读");

			}
		});

		button_in.performClick();

		button_out.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_out.setBackgroundColor(Color.rgb(162,194,230));
				// button_in.setBackgroundColor(Color.rgb(71,134,198));
				// button_draft.setBackgroundColor(Color.rgb(71,134,198));
				//
				model_flag = 4;
				isShow = false;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();
				totalSMS = SMSReader.cur00.getCount();

				tv_SMSNum.setText("" + totalSMS + "条信息");

			}
		});

		button_draft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_draft.setBackgroundColor(Color.rgb(162,194,230));
				// button_out.setBackgroundColor(Color.rgb(71,134,198));
				// button_in.setBackgroundColor(Color.rgb(71,134,198));

				model_flag = 5;

				isShow = false;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();
				totalSMS = SMSReader.cur00.getCount();

				tv_SMSNum.setText("" + totalSMS + "条信息");

			}
		});

		button_have_sent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_draft.setBackgroundColor(Color.rgb(162,194,230));
				// button_out.setBackgroundColor(Color.rgb(71,134,198));
				// button_in.setBackgroundColor(Color.rgb(71,134,198));

				model_flag = 6;

				isShow = false;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();
				totalSMS = SMSReader.cur00.getCount();

				tv_SMSNum.setText("" + totalSMS + "条信息");

			}
		});

		button_rubbish.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_draft.setBackgroundColor(Color.rgb(162,194,230));
				// button_out.setBackgroundColor(Color.rgb(71,134,198));
				// button_in.setBackgroundColor(Color.rgb(71,134,198));

				model_flag = 7;
				isShow = false;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();
				totalSMS = SMSFailedReader.cur00.getCount();

				tv_SMSNum.setText("" + totalSMS + "条信息");

			}
		});

		// 010
		// button1.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		//
		//
		//
		// AlertDialog.Builder builder = new Builder(ReadSMS.this);
		//
		// builder.setSingleChoiceItems( //设置单选列表选项
		// R.array.msb,
		// -1,
		// new DialogInterface.OnClickListener() {
		//
		// public void onClick(DialogInterface dialog, int which) {
		// switch (which){
		// case 0:
		// flag=1;
		// break;
		// case 1:
		// flag=2;
		// break;
		// case 2:
		//
		// dialog.dismiss();
		//
		// LayoutInflater inflater = getLayoutInflater();
		// View layout =
		// inflater.inflate(R.layout.searchdialog,(ViewGroup)findViewById(R.id.dialog));
		// Builder searchBuilder = new Builder(ReadSMS.this);
		// searchBuilder.setIcon(android.R.drawable.ic_dialog_info);
		// searchBuilder.setTitle("查询时间");
		// searchBuilder.setView(layout);
		//
		// datePickerFrom=(DatePicker)layout.findViewById(R.id.datePickerFrom);
		// datePickerTo=(DatePicker)layout.findViewById(R.id.datePickerTo);
		// final TextView
		// textView_From=(TextView)layout.findViewById(R.id.textView_From);
		// final TextView
		// textView_To=(TextView)layout.findViewById(R.id.textView_To);
		// searchBuilder.setPositiveButton("OK",new OnClickListener(){
		// public void onClick(DialogInterface dialog,int which){
		// cfrom = Calendar.getInstance();
		// cfrom.set(Calendar.YEAR, cfromyear);
		// cfrom.set(Calendar.MONTH, cfrommonth-1);
		// cfrom.set(Calendar.DAY_OF_MONTH, cfromday);
		// cfrom.set(Calendar.HOUR, 0);
		// cfrom.set(Calendar.MINUTE, 0);
		// cfrom.set(Calendar.SECOND, 0);
		// cfrom.set(Calendar.MILLISECOND, 0);
		// Log.v("cfrom",""+cfrom.getTime());
		// cto = Calendar.getInstance();
		// cto.set(Calendar.YEAR, ctoyear);
		// cto.set(Calendar.MONTH, ctomonth-1);
		// cto.set(Calendar.DAY_OF_MONTH, ctoday);
		// cto.set(Calendar.HOUR, 23);
		// cto.set(Calendar.MINUTE, 59);
		// cto.set(Calendar.SECOND, 59);
		// cto.set(Calendar.MILLISECOND, 0);
		// Log.v("cto",""+cto.getTime());
		// if(cfrom.getTimeInMillis()<cto.getTimeInMillis()){
		// //getRecord(6,cfrom.getTimeInMillis(),cto.getTimeInMillis());
		// flag=4;
		//
		//
		// list.clear();
		// listItemAdapter.notifyDataSetChanged();
		// Log.v("flag44444",""+flag);
		// fillListView();
		//
		// Log.v("cfrom"+cfrom.getTimeInMillis(),"cto"+cto.getTimeInMillis());
		// }else{
		// Toast.makeText(ReadSMS.this, "选择错误", Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
		// searchBuilder.setNegativeButton("cancel",new OnClickListener(){
		// public void onClick(DialogInterface dialog,int which){
		//
		// }
		// });
		//
		//
		// Calendar calendar=Calendar.getInstance();
		// int year=calendar.get(Calendar.YEAR);
		// int monthOfYear=calendar.get(Calendar.MONTH);
		// int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
		// cfromyear=year;
		// cfrommonth=monthOfYear+1;
		// cfromday=dayOfMonth;
		// ctoyear=year;
		// ctomonth=monthOfYear+1;
		// ctoday=dayOfMonth;
		//
		// datePickerFrom.init(year, monthOfYear, dayOfMonth, new
		// OnDateChangedListener(){
		// public void onDateChanged(DatePicker view, int year, int monthOfYear,
		// int dayOfMonth) {
		// //textView_From.setText("您选择的日期是："+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日。");
		// cfromyear=year;
		// cfrommonth=monthOfYear+1;
		// cfromday=dayOfMonth;
		//
		// }
		// });
		// datePickerTo.init(year, monthOfYear, dayOfMonth, new
		// OnDateChangedListener(){
		// public void onDateChanged(DatePicker view, int year, int monthOfYear,
		// int dayOfMonth) {
		// //textView_To.setText("您选择的日期是："+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日。");
		// ctoyear=year;
		// ctomonth=monthOfYear+1;
		// ctoday=dayOfMonth;
		// }
		// });
		//
		// searchBuilder.show();
		//
		// break;
		//
		// }
		// }
		// });
		//
		// builder.setPositiveButton("ok", new
		// DialogInterface.OnClickListener(){
		//
		// public void onClick(DialogInterface dialog, int which) {
		//
		// dialog.dismiss();
		//
		// list.clear();
		// listItemAdapter.notifyDataSetChanged();
		// Log.v("1111","1111");
		// fillListView();
		//
		// }
		//
		//
		// });
		// builder.setNegativeButton("cancle", new
		// DialogInterface.OnClickListener(){
		//
		// public void onClick(DialogInterface dialog, int which) {
		//
		// dialog.dismiss();
		// }
		//
		//
		// });
		//
		// builder.create().show();
		//
		//
		//
		//
		//
		//
		// input = editText1.getText().toString();
		// Log.v("input",input);
		// //flag=1;
		// Log.v("flash","flash");
		//
		// // list.clear();
		// // listItemAdapter.notifyDataSetChanged();
		// // Log.v("1111","1111");
		// // fillListView();
		//
		// }
		// });

		// button_new.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		//
		// Intent intent = new Intent();
		// // intent.setClass(ReadSMS.this, editSMS.class);
		// startActivity(intent);
		// //finish();//停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
		//
		// }
		// });

		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)

			{

				Log.v("model_flag", "" + ReadSMS.model_flag);
				index = arg2;

				// if(ReadSMS.model_flag==2){
				//
				// ReadSMS.model_flag=3;
				// flag=1;
				//
				// if(ReadSMS.contact == null) {
				//
				// thread = ReadSMS.smsReader.get(ReadSMS.index).mAddress;
				// Log.v("thread000",""+thread);
				// }
				// else {
				// thread = ReadSMS.contact.mName;
				// }
				//
				//
				//
				// list.clear();
				// listItemAdapter.notifyDataSetChanged();
				// Log.v("model_flag2",""+ReadSMS.model_flag);
				// fillListView();
				// }

				Intent intent = new Intent();

				intent.setClass(ReadSMS.this, detail.class);

				if (ReadSMS.model_flag == 1 || ReadSMS.model_flag == 4
						|| ReadSMS.model_flag == 5 || model_flag == 6
						|| model_flag == 7)
					startActivity(intent);

			}

		});

		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				indexlong = arg2;
				Log.v("indexlong", "" + indexlong);

				AlertDialog.Builder builder = new Builder(ReadSMS.this);

				builder.setSingleChoiceItems( // 设置单选列表选项
						R.array.msc, 0, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:

									new Thread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											SMSItem smsitem = smsReader
													.get(indexlong);
											long ID = smsitem.mID;

											String SUBJECT = smsitem.mBody;
											String ADDRESS = smsitem.mAddress;

											deleteItems(ID, ReadSMS.this);

											mHandler.sendEmptyMessage(123);

											Log.v("ReadSMS", "ID: " + ID
													+ " SUBJECT: " + SUBJECT
													+ " Address: " + ADDRESS);
										}

									}).start();

									dialog.dismiss();

									break;
								case 1:

									break;
								}
							}
						});

				builder.create().show();

				return false;
			}
		});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// getActionBar().setSubtitle("Long press to start selection");
	}

	private class ModeCallback implements ListView.MultiChoiceModeListener {

		@Override
		public boolean onCreateActionMode(android.view.ActionMode mode,
				Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.list_select_menu, menu);
			mode.setTitle("点击选择");
			setSubtitle(mode);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(android.view.ActionMode mode,
				Menu menu) {
			return true;
		}

		@Override
		public boolean onActionItemClicked(android.view.ActionMode mode,
				MenuItem item) {
			switch (item.getItemId()) {

			case R.id.select_all_to_delete:
				// Toast.makeText(ReadSMS.this, "Shared " +
				// listview.getCheckedItemCount() +
				// " items", Toast.LENGTH_SHORT).show();
				//

				// TODO Auto-generated method stub

				// 做的删除短信不卡的版本！！！
				array = listview.getCheckedItemPositions();

				Log.v("ReadSMS", "发送报告！！！！" + array.size());
				delete_IDs.clear();

				for (int i = 0; i < list.size(); i++) {

					if (array.get(i)) {

						if (ReadSMS.model_flag == 7) {

							SMSItemFailed smsitem = smsFailedReader.get(i);

							long ID = smsitem.mID;

							FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(
									ReadSMS.this, "myBox.db3", 1);

							SQLiteDatabase db = dbHelper.getWritableDatabase();

							String[] args = { String.valueOf(ID) };

							db.delete("failedbox", "_id=?", args);

							db.close();

							mHandler.sendEmptyMessage(123);
							Log.v("ReadSMS for Test", "触发了， ID: " + ID);

						}

						else {

							//
							SMSItem smsitem = smsReader.get(i);

							long id = smsitem.mID;

							delete_IDs.add(id);

							list.remove(i);

							listItemAdapter.notifyDataSetChanged();

							//
							//
							//

							// mHandler.sendEmptyMessage(123);
							Log.v("ReadSMS", "array size: " + array.size()
									+ "selected:  !!: " + i);

						}
					}

				}

				if (ReadSMS.model_flag != 7) {

					new AsyncTask<Void, Void, Void>() {
						protected Void doInBackground(Void... none) {

							// Looper.prepare();

							Log.v("ReadSMS for Test", "在线程中！！！");

							FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(
									ReadSMS.this, "myBox.db3", 1);

							SQLiteDatabase db = dbHelper.getWritableDatabase();

							for (int i = 0; i < delete_IDs.size(); i++) {

								insertToFailedBox(db, delete_IDs.get(i));

								Log.v("ReadSMS for Test", "触发了， ID: "
										+ delete_IDs.get(i));

								deleteItems(delete_IDs.get(i), ReadSMS.this);

								Log.v("ReadSMS for Test",
										"触发了， ID在deleteItems之后: "
												+ delete_IDs.get(i));

							}

							db.close();

							// ConversationUtils.getAllNumbers(ConversationList.this);

							Log.v("ReadSMS for Test", "在线结束！！！");

							return null;

						}
					}.execute();

				}

				// for(int i=0;i<list.size();i++){
				//
				// if(array.get(i)){
				//
				//
				// SMSItem smsitem = smsReader.get(i);
				// long ID =smsitem.mID;
				// deleteItems(ID, ReadSMS.this);
				//
				//
				// mHandler.sendEmptyMessage(123);
				// Log.v("ReadSMS",
				// "array size: "+array.size()+"selected:  !!: "+i);
				//
				// }
				//
				// }

				mode.finish();
				break;

			case R.id.select_all:

				for (int i = 0; i < list.size(); i++) {

					listview.setItemChecked(i, true);
				}

			default:
				Toast.makeText(ReadSMS.this, "Clicked " + item.getTitle(),
						Toast.LENGTH_SHORT).show();
				break;
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(android.view.ActionMode mode) {
		}

		@Override
		public void onItemCheckedStateChanged(android.view.ActionMode mode,
				int position, long id, boolean checked) {
			setSubtitle(mode);

			// SMSItem smsitem = smsReader.get(indexlong);
			// long ID =smsitem.mID;
			//
			// String SUBJECT=smsitem.mBody;
			// String ADDRESS=smsitem.mAddress;
			//
			// //deleteItems(ID, ReadSMS.this);
			//
			//
			// //mHandler.sendEmptyMessage(123);
			//
			// //Log.v("ReadSMS",
			// "ID: "+ID+" SUBJECT: "+SUBJECT+" Address: "+ADDRESS);
			//
			//
			//
			// Toast.makeText(ReadSMS.this, "position: "+position+" body: "+
			// SUBJECT,
			// Toast.LENGTH_SHORT).show();
		}

		private void setSubtitle(ActionMode mode) {
			final int checkedCount = listview.getCheckedItemCount();
			switch (checkedCount) {
			case 0:
				mode.setSubtitle(null);
				break;
			case 1:
				mode.setSubtitle("1个条目已选择");
				break;
			default:
				mode.setSubtitle("" + checkedCount + "个条目已选择");
				break;
			}
		}
	}

	private void deleteItems(long rawContentId, Context context) {
		context.getContentResolver().delete(ALL_INBOX, "_id= " + rawContentId,
				null);
	}

	private void fillListView() {
		listview = (ListView) findViewById(R.id.sms_list);
		Log.v("step1", "step1");
		if (ReadSMS.model_flag == 3) {
			Log.v("step2", "step2");
			Log.v("ReadSMS for test", "ReadSMS.model_flag="
					+ ReadSMS.model_flag);
			list = readAllSMS1();

			// ReadSMS.model_flag=1;
		}

		else if (ReadSMS.model_flag == 7) {

			Log.v("step7777", "step7777");
			Log.v("ReadSMS for test", "ReadSMS.model_flag="
					+ ReadSMS.model_flag);

			list = readAllFailedSMS();

		} else {
			Log.v("step3", "step3");
			Log.v("ReadSMS for test", "ReadSMS.model_flag="
					+ ReadSMS.model_flag);
			list = readAllSMS();
		}
		// listItemAdapter = new SimpleAdapter(this, list,
		// android.R.layout.simple_list_item_2,
		// new String[] {"person","data"},
		// new int[] {android.R.id.text1, android.R.id.text2}
		// );

		listItemAdapter = new SimpleAdapter(this, list, R.layout.listitems,
				new String[] { "person_image", "person", "data" }, new int[] {
						R.id.textView_network, R.id.textView_body,
						R.id.textView_date });

		listview.setAdapter(listItemAdapter);
	}

	public void insertToFailedBox(SQLiteDatabase db, long id) {

		ContentValues values = new ContentValues();
		values = getValues(id);
		db.insert("failedbox", null, values);

	}

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

	
	
	private ArrayList<HashMap<String, Object>> readAllFailedSMS() {
		smsFailedReader = new SMSFailedReader();

		Log.v("step4", "step4");

		int cnt = smsFailedReader.read(this);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < cnt; ++i) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			smsFailed = smsFailedReader.get(i);

			Log.v("ReadSMS", "mAddress: " + smsFailed.mAddress + " "
					+ smsFailed.mID + " " + smsFailed.mThreadID);
			contactFailed = smsFailedReader.getFailedContact(ReadSMS.this,
					smsFailed);
			// contact = smsReader.getContact(ReadSMS.this, smsFailed);
			// contact=null;

			Log.v("ReadSMS for test", "contactFailed.mName=");
			int person_image;

			person_image = R.drawable.ic_contact_picture;

			item.put("person_image", person_image);

			if (contactFailed == null) {

				item.put(
						"person",
						smsFailed.mAddress
								+ left(smsFailed.mBody.toString(), 26));
				Log.v("ReadSMS for test", "contactFailed.mName=null");
			} else {
				item.put(
						"person",
						contactFailed.mName
								+ left(smsFailed.mBody.toString(), 26));
				Log.v("ReadSMS for test", "contact.mName不是null");
			}
			// item.put("data", sms.toString());

			Date date = new Date(smsFailed.mDate);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String formalDate = format.format(date);

			String ss = smsFailed.mRead == 1 ? "已读" : "未读";
			String dd = smsFailed.mSubID == 0 ? "卡一" : "卡二";

			item.put("data", "日期：" + formalDate + "\n" + ss + " " + dd);
			Log.v("item", "" + item);
			list.add(item);
		}

		return list;
	}

	
	
	
	private ArrayList<HashMap<String, Object>> readAllSMS() {
		smsReader = new SMSReader();
		Log.v("step4", "step4");
		int cnt = smsReader.read(this);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < cnt; ++i) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			sms = smsReader.get(i);
			contact = smsReader.getContact(ReadSMS.this, sms);
			Log.v("ReadSMS for test", "contact.mName=");
			int person_image;

			person_image = R.drawable.ic_contact_picture;

			item.put("person_image", person_image);

			if (contact == null) {

				item.put("person", sms.mAddress
						+ left(sms.mBody.toString(), 26));
				Log.v("ReadSMS for test", "contact.mName=null");
			} else {
				item.put("person",
						contact.mName + left(sms.mBody.toString(), 26));
				Log.v("ReadSMS for test", "contact.mName不是null");
			}
			// item.put("data", sms.toString());

			Date date = new Date(sms.mDate);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String formalDate = format.format(date);

			String ss = sms.mRead == 1 ? "已读" : "未读";
			String dd = sms.mSubID == 0 ? "卡一" : "卡二";

			item.put("data", "日期：" + formalDate + "\n" + ss + " " + dd);
			Log.v("item", "" + item);
			list.add(item);
		}
		return list;
	}

	public class SmsObserver extends ContentObserver {
		private Context context;
		private final String[] SMS_PROJECTION = new String[] { "address",
				"person", "date", "type", "body", };

		public SmsObserver(Context context, Handler handler) {
			super(handler);
			this.context = context;
			Log.i("Leo-SmsObserver", "My Oberver on create");
		}

		public void onChange(boolean selfChange) {

			

			Log.i("SmsObserver", "我是因为ContentObserver才收到的新短信！！！");
			
			
			list.clear();
			listItemAdapter.notifyDataSetChanged();

			fillListView();

			Log.i("SmsObserver", "sms onChange###### ");
		}

	}

	private ArrayList<HashMap<String, Object>> readAllSMS1() {

		// 010 editText1.setVisibility(8);
		// 010 button1.setVisibility(8);
		// button_new.setVisibility(8);
		// tradition_button.setVisibility(8);
		// speek_button.setVisibility(8);
		// mLinearLayout1.setVisibility(0);
		mLinearLayout2.setVisibility(8);

		smsReader = new SMSReader();
		int cnt = smsReader.read(this);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < cnt; ++i) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			sms = smsReader.get(i);
			contact = smsReader.getContact(this, sms);

			int person_image;

			person_image = R.drawable.man;

			item.put("person_image", person_image);
			Log.v("person_image", "" + person_image);
			if (contact == null) {
				item.put("person", sms.mAddress);
			} else {
				item.put("person", contact.mName);
			}
			// item.put("data", sms.toString());

			Date date = new Date(sms.mDate);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String formalDate = format.format(date);

			String ss = sms.mRead == 1 ? "已读" : "未读";
			String dd = sms.mSubID == 0 ? "卡一" : "卡二";
			item.put("data", left(sms.mBody.toString(), 2600) + "  日期："
					+ formalDate + "\n" + ss + " " + dd);
			Log.v("item", "" + item);
			list.add(item);
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.traditional_list_menu, menu);

		menu.clear();

		if (isShow) {

			MenuItem itemSearch = menu.add(0, MENU_SEARCH, 0,
					R.string.traditional_search).setIcon(
					R.drawable.ic_menu_search_holo_dark);
			itemSearch.setShowAsAction(1);
			// 010 .setTitle(R.string.menu_search);

			MenuItem item = menu.add(0, MENU_DELETE_UNREAD, 1,
					R.string.traditional_read_all).setIcon(R.drawable.read_ed);
			// 010 item2itemReadall
			// 010 .setTitle(R.string.menu_search);
			// item.setShowAsAction(0);

			// 010 item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// 010
		switch (item.getItemId()) {
		case R.id.action_select_cards:

			Intent intent = new Intent(this, SeperateCardActivity.class);

			startActivity(intent);

			break;
		// 010:
		case MENU_SEARCH: {

			AlertDialog.Builder builder = new Builder(ReadSMS.this);

			builder.setSingleChoiceItems( // 设置单选列表选项
					R.array.msb, -1, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								flag = 1;
								break;
							case 1:
								flag = 2;
								break;
							case 2:

								dialog.dismiss();

								LayoutInflater inflater = getLayoutInflater();
								View layout = inflater.inflate(
										R.layout.searchdialog,
										(ViewGroup) findViewById(R.id.dialog));
								Builder searchBuilder = new Builder(
										ReadSMS.this);
								searchBuilder
										.setIcon(android.R.drawable.ic_dialog_info);
								searchBuilder
										.setTitle(R.string.traditional_search_time);
								searchBuilder.setView(layout);

								datePickerFrom = (DatePicker) layout
										.findViewById(R.id.datePickerFrom);
								datePickerTo = (DatePicker) layout
										.findViewById(R.id.datePickerTo);
								final TextView textView_From = (TextView) layout
										.findViewById(R.id.textView_From);
								final TextView textView_To = (TextView) layout
										.findViewById(R.id.textView_To);
								searchBuilder.setPositiveButton(
										R.string.traditional_ok,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												cfrom = Calendar.getInstance();
												cfrom.set(Calendar.YEAR,
														cfromyear);
												cfrom.set(Calendar.MONTH,
														cfrommonth - 1);
												cfrom.set(
														Calendar.DAY_OF_MONTH,
														cfromday);
												// 010 cfrom.set(Calendar.HOUR,
												// 0);
												// 010
												// cfrom.set(Calendar.MINUTE,
												// 0);
												// 010
												// cfrom.set(Calendar.SECOND,
												// 0);
												// 010
												// cfrom.set(Calendar.MILLISECOND,
												// 0);
												Log.v("cfrom",
														"" + cfrom.getTime());
												cto = Calendar.getInstance();
												cto.set(Calendar.YEAR, ctoyear);
												cto.set(Calendar.MONTH,
														ctomonth - 1);
												cto.set(Calendar.DAY_OF_MONTH,
														ctoday);
												// 010 cto.set(Calendar.HOUR,
												// 23);
												// 010 cto.set(Calendar.MINUTE,
												// 59);
												// 010 cto.set(Calendar.SECOND,
												// 59);
												// 010
												// cto.set(Calendar.MILLISECOND,
												// 0);
												Log.v("cto", "" + cto.getTime());
												if (cfrom.getTimeInMillis() < cto
														.getTimeInMillis()) {
													// getRecord(6,cfrom.getTimeInMillis(),cto.getTimeInMillis());
													flag = 4;

													list.clear();
													listItemAdapter
															.notifyDataSetChanged();
													Log.v("flag44444", ""
															+ flag);
													fillListView();

													Log.v("cfrom"
															+ cfrom.getTimeInMillis(),
															"cto"
																	+ cto.getTimeInMillis());
												} else {
													Toast.makeText(
															ReadSMS.this,
															"选择错误",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
								searchBuilder.setNegativeButton(
										R.string.traditional_cancel,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										});

								Calendar calendar = Calendar.getInstance();
								int year = calendar.get(Calendar.YEAR);
								int monthOfYear = calendar.get(Calendar.MONTH);
								int dayOfMonth = calendar
										.get(Calendar.DAY_OF_MONTH);
								cfromyear = year;
								cfrommonth = monthOfYear + 1;
								cfromday = dayOfMonth;
								ctoyear = year;
								ctomonth = monthOfYear + 1;
								ctoday = dayOfMonth;

								datePickerFrom.init(year, monthOfYear,
										dayOfMonth,
										new OnDateChangedListener() {
											public void onDateChanged(
													DatePicker view, int year,
													int monthOfYear,
													int dayOfMonth) {
												// textView_From.setText("您选择的日期是："+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日。");
												cfromyear = year;
												cfrommonth = monthOfYear + 1;
												cfromday = dayOfMonth;

											}
										});
								datePickerTo.init(year, monthOfYear,
										dayOfMonth,
										new OnDateChangedListener() {
											public void onDateChanged(
													DatePicker view, int year,
													int monthOfYear,
													int dayOfMonth) {
												// textView_To.setText("您选择的日期是："+year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日。");
												ctoyear = year;
												ctomonth = monthOfYear + 1;
												ctoday = dayOfMonth;
											}
										});

								searchBuilder.show();

								break;

							}
						}
					});

			builder.setPositiveButton(R.string.traditional_ok,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();

							list.clear();
							listItemAdapter.notifyDataSetChanged();
							Log.v("1111", "1111");
							fillListView();

						}

					});
			builder.setNegativeButton(R.string.traditional_cancel,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}

					});
			builder.setTitle(R.string.traditional_search);
			builder.create().show();

		}
			// 010ABOVE

			break;

		case MENU_DELETE_UNREAD:

			ConversationUtils.changeUnread(this);

			mHandler.sendEmptyMessage(123);

			return true;
		}
		return false;
	}

}