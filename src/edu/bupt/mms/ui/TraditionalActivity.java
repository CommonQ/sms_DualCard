package edu.bupt.mms.ui;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.mms.MmsException;

import edu.bupt.mms.R;
import edu.bupt.mms.TestActivity;

import edu.bupt.mms.data.Contact;
import edu.bupt.mms.data.ContactList;
import edu.bupt.mms.model.SlideshowModel;
import edu.bupt.mms.ui.ReadSMS.SmsObserver;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony.Mms;
import android.provider.Telephony.MmsSms;
import android.provider.Telephony.Sms.Conversations;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.Time;//010
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.TimePicker.OnTimeChangedListener;//010
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;//010
import android.widget.Toast;

public class TraditionalActivity extends Activity {

	private static final String TAG = "TraditionalActivity";

	// public static final StyleSpan STYLE_BOLD = new
	// StyleSpan(Typeface.ITALIC);

	public static int greycolor = Color.parseColor("#828282");
	public EditText editText1;
	public RadioButton button_in, button_draft, button_out, button_have_sent,
			button_rubbish;
	public ImageButton button1;
	public static String input;
	public static int flag = 0;
	public static int sort_flag = 0;
	public Bundle s;
	public ListView listview;
	public TextView tv_SMSNum;
	public SimpleAdapter listItemAdapter;
	public ArrayList<HashMap<String, Object>> list;
	// public static SMSItem sms;
	// public static ContactItem contact;
	public static SMSReader smsReader;

	public static int index, indexlong;

	public static boolean isStart = false;

	public static final int MENU_DELETE_UNREAD = 0x1;
	public static final int MENU_NEW_MSG = 0x100;// 010
	public static final int MENU_SEARCH = 0x10;
	public static final int MENU_SORT = 0x11;
	public static final int MENU_BACK_TO_CONVERSATION = 0x14;
	private DatePicker datePickerFrom, datePickerTo;
	private TimePicker timePickerFrom, timePickerTo;// 010

	public static int model_flag = 1;
	public static int current_model_flag = 1;

	public Cursor myCursor;

	private int cfromyear = 0;
	private int cfrommonth = 0;
	private int cfromday = 0;
	private int cfromhour = 0;// 010
	private int ctoyear = 0;
	private int ctomonth = 0;
	private int ctoday = 0;
	private int ctohour = 0;// 010

	public int totalSMS = 0;
	public int unreadSMS = 0;
	public LinearLayout mLinearLayout2, mLinearLayoutbox;

	public static ArrayList<SMSListItem> mlist = new ArrayList<SMSListItem>();

	public static ArrayList<SMSListItem> mlist2 = new ArrayList<SMSListItem>();

	public static HashMap<String, ArrayList<SMSListItem>> all_list = new HashMap<String, ArrayList<SMSListItem>>();

	public static boolean isFinished = false;
	
	private static boolean DEBUG = false;

	public static ArrayList<Long> delete_IDs = new ArrayList<Long>();

	public static long delete_ID;

	SparseBooleanArray array = null;

	public static Uri ALL_INBOX = Uri.parse("content://sms/");
	public static Uri ALL_MMS_INBOX = Uri.parse("content://mms/");
	public static Calendar cfrom;
	public static Calendar cto;

	public static String body;
	public static String name;
	
	private SmsObserver observer;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.traditional_list_menu, menu);

		menu.clear();

		MenuItem itemNew = menu.add(0, MENU_NEW_MSG, 0, R.string.new_message)
				.setIcon(R.drawable.ic_menu_msg_compose_holo_dark);
		// itemNew.setShowAsAction(itemNew.SHOW_AS_ACTION_ALWAYS); // 010 new
		// msg
		itemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		MenuItem itemSearch = menu.add(0, MENU_SEARCH, 1,
				R.string.traditional_search).setIcon(
				R.drawable.ic_menu_search_holo_dark);
		// itemSearch.setShowAsAction(MENU_SEARCH);
		itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		MenuItem item = menu.add(0, MENU_DELETE_UNREAD, 2,
				R.string.traditional_read_all).setIcon(R.drawable.read_ed);

		MenuItem itemSort = menu
				.add(0, MENU_SORT, 3, R.string.traditional_sort).setIcon(
						R.drawable.read_ed);

		MenuItem itemBack = menu.add(0, MENU_BACK_TO_CONVERSATION, 4,
				R.string.back_to_conversation).setIcon(R.drawable.read_ed);

		// 010 item2itemReadall
		// 010 .setTitle(R.string.menu_search);
		// item.setShowAsAction(0);

		// 010 item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;

	}

	// 010 added4createnew
	private void createNewMessage() {
		startActivity(ComposeMessageActivity.createIntent(this, 0));
//		//随便加一下试试的
//		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {

		case MENU_NEW_MSG:// 010
			if(DEBUG)
			Log.v(TAG, "MENU_NEW_MSG");
			
			Intent newIntent = new Intent("edu.bupt.createnewmessage");
			sendBroadcast(newIntent,0);
			
			getContentResolver().unregisterContentObserver(observer);
			finish();
			
			//createNewMessage();

			return true;

		case MENU_DELETE_UNREAD:

			if (sort_flag == 1) {

				ConversationUtils.changeUnread(this);

				fillNewMessage();

				return true;
			} else if (sort_flag == 2) {

				ConversationUtils.changeUnreadMMS(this);

				fillNewMessage();

				return true;

			} else if (sort_flag == 0) {

				ConversationUtils.changeUnread(this);
				ConversationUtils.changeUnreadMMS(this);
				return true;
			} else if (sort_flag == 3 || sort_flag == 4) {

				ConversationUtils.changeUnreadCard(this, sort_flag);

			}

		case MENU_BACK_TO_CONVERSATION:

			finish();

			return true;

		case MENU_SORT:

			AlertDialog.Builder sortbuilder = new Builder(
					TraditionalActivity.this);

			sortbuilder.setSingleChoiceItems(
					// 设置单选列表选项
					R.array.msf, sort_flag,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							switch (which) {

							case 0:
								sort_flag = 0;
								fillListView();
								dialog.dismiss();

								break;

							case 1:

								sort_flag = 1;
								fillListView();
								dialog.dismiss();
								break;

							case 2:

								sort_flag = 2;
								fillListView();
								dialog.dismiss();
								break;

							case 3:
								sort_flag = 3;
								fillListView();

								dialog.dismiss();

								break;

							case 4:

								sort_flag = 4;

								fillListView();

								dialog.dismiss();

								break;

							}
						}
					});

			sortbuilder.setTitle(R.string.traditional_sort);
			sortbuilder.create().show();

			return true;

		case MENU_SEARCH: {

			AlertDialog.Builder builder = new Builder(TraditionalActivity.this);

			builder.setSingleChoiceItems( // 设置单选列表选项
					R.array.msb, -1, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							switch (which) {

							// Log.v("TraditionalActivity", "which:"+which);

							case 0:
								flag = 5;
								// zaizhe
								if(DEBUG)
								Log.v("TraditionalActivity", "which:" + which
										+ "flag: " + flag);

								dialog.dismiss();
								LayoutInflater address_inflater = getLayoutInflater();

								View address_layout = address_inflater.inflate(
										R.layout.searchbody, null);

								Builder addressBuilder = new Builder(
										TraditionalActivity.this);

								addressBuilder
										.setIcon(android.R.drawable.ic_dialog_info);
								addressBuilder
										.setTitle(R.string.traditional_search_address);
								addressBuilder.setView(address_layout);

								final TextView addressText = (TextView) address_layout
										.findViewById(R.id.body);

								addressBuilder.setPositiveButton(
										R.string.traditional_ok,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												body = addressText.getText()
														.toString();

												fillSearchResult(flag,
														model_flag);
												flag = 0;
												dialog.dismiss();

											}
										});

								addressBuilder.setNegativeButton(
										R.string.traditional_cancel,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												dialog.dismiss();

											}
										});
								// zaizhe
								// fillSearchResult(flag,model_flag);
								// flag=0;
								addressBuilder.show();

								break;

							case 1:

								flag = 1;
								// zaizhe

								Log.v("TraditionalActivity", "which:" + which
										+ "flag: " + flag);

								dialog.dismiss();
								LayoutInflater number_inflater = getLayoutInflater();

								View numbder_layout = number_inflater.inflate(
										R.layout.searchbody, null);

								Builder numbderBuilder = new Builder(
										TraditionalActivity.this);

								numbderBuilder
										.setIcon(android.R.drawable.ic_dialog_info);
								numbderBuilder
										.setTitle(R.string.traditional_search_address);
								numbderBuilder.setView(numbder_layout);

								final TextView numbderText = (TextView) numbder_layout
										.findViewById(R.id.body);

								numbderBuilder.setPositiveButton(
										R.string.traditional_ok,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												body = numbderText.getText()
														.toString();

												fillSearchResult(flag,
														model_flag);
												flag = 0;
												dialog.dismiss();

											}
										});

								numbderBuilder.setNegativeButton(
										R.string.traditional_cancel,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												dialog.dismiss();

											}
										});
								// zaizhe
								// fillSearchResult(flag,model_flag);
								// flag=0;
								numbderBuilder.show();

								break;

							case 2:
								flag = 2;

								dialog.dismiss();
								LayoutInflater body_inflater = getLayoutInflater();

								View body_layout = body_inflater.inflate(
										R.layout.searchbody, null);

								Builder bodyBuilder = new Builder(
										TraditionalActivity.this);

								bodyBuilder
										.setIcon(android.R.drawable.ic_dialog_info);
								bodyBuilder
										.setTitle(R.string.traditional_search_body);
								bodyBuilder.setView(body_layout);

								final TextView bodyText = (TextView) body_layout
										.findViewById(R.id.body);

								bodyBuilder.setPositiveButton(
										R.string.traditional_ok,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												body = bodyText.getText()
														.toString();

												fillSearchResult(flag,
														model_flag);
												flag = 0;

											}
										});

								bodyBuilder.setNegativeButton(
										R.string.traditional_cancel,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												dialog.dismiss();

											}
										});
								// zaizhe
								// fillSearchResult(flag,model_flag);
								// flag=0;
								bodyBuilder.show();

								break;
							case 3:

								dialog.dismiss();

								LayoutInflater inflater = getLayoutInflater();
								View layout = inflater.inflate(
										R.layout.searchdialog,
										(ViewGroup) findViewById(R.id.dialog));
								Builder searchBuilder = new Builder(
										TraditionalActivity.this);
								searchBuilder
										.setIcon(android.R.drawable.ic_dialog_info);
								searchBuilder
										.setTitle(R.string.traditional_search_time);
								searchBuilder.setView(layout);

								datePickerFrom = (DatePicker) layout
										.findViewById(R.id.datePickerFrom);
								timePickerFrom = (TimePicker) layout
										.findViewById(R.id.timePickerFrom);
								timePickerFrom.setIs24HourView(true);
								datePickerTo = (DatePicker) layout
										.findViewById(R.id.datePickerTo);
								timePickerTo = (TimePicker) layout
										.findViewById(R.id.timePickerTo);
								timePickerTo.setIs24HourView(true);
								Time now = new Time();
								now.setToNow();
								datePickerFrom.setMaxDate(now.toMillis(false));// 010
								datePickerTo.setMaxDate(now.toMillis(false));// 010

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
												// 010
												cfrom.set(Calendar.HOUR_OF_DAY,
														cfromhour);
												cfrom.set(Calendar.MINUTE, 0);
												cfrom.set(Calendar.SECOND, 0);
												cfrom.set(Calendar.MILLISECOND,
														0);
												Log.v("cfrom",
														"" + cfrom.getTime());
												cto = Calendar.getInstance();
												cto.set(Calendar.YEAR, ctoyear);
												cto.set(Calendar.MONTH,
														ctomonth - 1);
												cto.set(Calendar.DAY_OF_MONTH,
														ctoday);
												cto.set(Calendar.HOUR_OF_DAY,
														ctohour);// 010
												cto.set(Calendar.MINUTE, 59);
												cto.set(Calendar.SECOND, 59);
												cto.set(Calendar.MILLISECOND, 0);
												Log.v("cto", "" + cto.getTime());// 010

												if (cfrom.getTimeInMillis() < cto
														.getTimeInMillis()) {
													// getRecord(6,cfrom.getTimeInMillis(),cto.getTimeInMillis());
													flag = 3;

													// list.clear();
													// listItemAdapter
													// .notifyDataSetChanged();
													// Log.v("flag44444", ""
													// + flag);
													// fillListView();

													// zaizhe
													fillSearchResult(flag,
															model_flag);
													flag = 0;

													Log.v("cfrom"
															+ cfrom.getTimeInMillis(),
															"cto"
																	+ cto.getTimeInMillis());
												} else {
													Toast.makeText(
															TraditionalActivity.this,
															R.string.select_error,
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
								// 010
								int hourOfDay = calendar
										.get(Calendar.HOUR_OF_DAY);
								int minute = calendar.get(Calendar.MINUTE);
								int second = calendar.get(Calendar.SECOND);
								int millisecond = calendar
										.get(Calendar.MILLISECOND);

								cfromyear = year;
								cfrommonth = monthOfYear + 1;
								cfromday = dayOfMonth;
								cfromhour = hourOfDay;// 010

								ctoyear = year;
								ctomonth = monthOfYear + 1;
								ctoday = dayOfMonth;
								ctohour = hourOfDay;// 010

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
								timePickerFrom
										.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
											@Override
											public void onTimeChanged(
													TimePicker view,
													int hourOfDay, int minute) {
												cfromhour = hourOfDay;// 010
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
								timePickerTo
										.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
											@Override
											public void onTimeChanged(
													TimePicker view,
													int hourOfDay, int minute) {
												ctohour = hourOfDay;// 010
											}
										});
								searchBuilder.create().show();

								break;

							}
						}
					});
			// zaizhe
			// builder.setPositiveButton(R.string.traditional_ok,
			// new DialogInterface.OnClickListener() {
			//
			// public void onClick(DialogInterface dialog, int which) {
			//
			// dialog.dismiss();
			//
			// fillSearchResult(flag,model_flag);
			// flag=0;
			//
			// // list.clear();
			// // listItemAdapter.notifyDataSetChanged();
			// // Log.v("1111", "1111");
			// // fillListView();
			//
			// }
			//
			// });
			// builder.setNegativeButton(R.string.traditional_cancel,
			// new DialogInterface.OnClickListener() {
			//
			// public void onClick(DialogInterface dialog, int which) {
			//
			// dialog.dismiss();
			// }
			//
			// });
			builder.setTitle(R.string.traditional_search);
			builder.create().show();

		}

		}

		return true;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		isStart = true;
		setContentView(R.layout.traditional);

		// 010 editText1 = (EditText)findViewById(R.id.edittext);
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
		// 010 mLinearLayout2 = (LinearLayout)findViewById(R.id.listhead);
		mLinearLayoutbox = (LinearLayout) findViewById(R.id.listbox);
		tv_SMSNum = (TextView) findViewById(R.id.smsText);

		try {
			fillListView();
		} catch (NullPointerException e) {

			Toast.makeText(TraditionalActivity.this,
					R.string.information_is_loading, Toast.LENGTH_LONG).show();
			this.finish();

		}
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listview.setMultiChoiceModeListener(new ModeCallback());
		 observer=	new SmsObserver(this, new Handler());
		getContentResolver().registerContentObserver(MmsSms.CONTENT_URI, true,
				observer);

		// getContentResolver().registerContentObserver(
		// Uri.parse("content://mms"), true,
		// new MmsObserver(this, new Handler()));

		button_in.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_in.setBackgroundColor(Color.rgb(162,194,230));
				// button_out.setBackgroundColor(Color.rgb(71,134,198));
				// button_draft.setBackgroundColor(Color.rgb(71,134,198));

				model_flag = 1;

				// myCursor = ReadSMS.managedQuery(SMS_INBOX, null, null, null,
				// "date DESC");
				try {
					list.clear();
				} catch (NullPointerException e) {

					Toast.makeText(TraditionalActivity.this,
							R.string.information_is_loading, Toast.LENGTH_LONG)
							.show();
					TraditionalActivity.this.finish();

				}

				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");

				Log.v("ReadSMS for test", "点击了 button_in model_flag="
						+ model_flag);

				fillListView();

				// totalSMS = SMSHelper.readCount_IN;
				// unreadSMS = SMSHelper.unreadCount_IN;
				if (sort_flag == 1) {
					totalSMS = SMSHelper.readCount_IN;
					unreadSMS = SMSHelper.unreadCount_IN;
				} else if (sort_flag == 2) {
					totalSMS = SMSHelper.readCount_IN_MMS;
					unreadSMS = SMSHelper.unreadCount_IN_MMS;
				} else if (sort_flag == 0) {
					int count_sms = SMSHelper.readCount_IN;
					int count_mms = SMSHelper.readCount_IN_MMS;
					int uncount_sms = SMSHelper.unreadCount_IN;
					int uncount_mms = SMSHelper.unreadCount_IN_MMS;
					totalSMS = count_sms + count_mms;
					unreadSMS = uncount_sms + uncount_mms;

				} else if (sort_flag == 3) {
					totalSMS = SMSHelper.readCount_IN_SUB1;
					unreadSMS = SMSHelper.unreadCount_IN_SUB1;

				} else if (sort_flag == 4) {

					totalSMS = SMSHelper.readCount_IN_SUB2;
					unreadSMS = SMSHelper.unreadCount_IN_SUB2;
				}
				String sss = TraditionalActivity.this
						.getString(R.string.traditional_unread_count);
				String ssss = TraditionalActivity.this
						.getString(R.string.traditional_count);// 010

				tv_SMSNum
						.setText(" " + totalSMS + ssss + " " + unreadSMS + sss);

			}
		});
		try {
			button_in.performClick();
		} catch (NullPointerException e) {

			Toast.makeText(TraditionalActivity.this,
					R.string.information_is_loading, Toast.LENGTH_LONG).show();
			TraditionalActivity.this.finish();

		}
		button_out.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// button_out.setBackgroundColor(Color.rgb(162,194,230));
				// button_in.setBackgroundColor(Color.rgb(71,134,198));
				// button_draft.setBackgroundColor(Color.rgb(71,134,198));
				//
				model_flag = 2;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");

				fillListView();
				if (sort_flag == 1) {

					totalSMS = SMSHelper.readCount_OUT;
				} else if (sort_flag == 2) {
					totalSMS = SMSHelper.readCount_OUT_MMS;
				} else if (sort_flag == 0) {

					int count_sms = SMSHelper.readCount_OUT;
					int count_mms = SMSHelper.readCount_OUT_MMS;
					totalSMS = count_sms + count_mms;
				} else if (sort_flag == 3) {
					totalSMS = SMSHelper.readCount_OUT_SUB1;
					unreadSMS = SMSHelper.unreadCount_OUT_SUB1;

				} else if (sort_flag == 4) {

					totalSMS = SMSHelper.readCount_OUT_SUB2;
					unreadSMS = SMSHelper.unreadCount_OUT_SUB2;
				}
				String ssss = TraditionalActivity.this
						.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}
		});

		//草稿箱
		button_draft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				model_flag = 3;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();

				if (sort_flag == 1) {

					totalSMS = SMSHelper.readCount_DRAFT;
				} else if (sort_flag == 2) {
					totalSMS = SMSHelper.readCount_DRAFT_MMS;
				} else if (sort_flag == 0) {

					int count_sms = SMSHelper.readCount_DRAFT;
					int count_mms = SMSHelper.readCount_DRAFT_MMS;
					totalSMS = count_sms + count_mms;
				} else if (sort_flag == 3) {
					totalSMS = SMSHelper.readCount_DRAFT_SUB1;
					unreadSMS = SMSHelper.unreadCount_DRAFT_SUB1;

				} else if (sort_flag == 4) {

					totalSMS = SMSHelper.readCount_DRAFT_SUB2;
					unreadSMS = SMSHelper.unreadCount_DRAFT_SUB2;
				}
				String ssss = TraditionalActivity.this
						.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}
		});

		button_have_sent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				model_flag = 4;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();
				if (sort_flag == 1) {

					totalSMS = SMSHelper.readCount_SEND;
				} else if (sort_flag == 2) {

					totalSMS = SMSHelper.readCount_SEND_MMS;
				} else if (sort_flag == 0) {

					int count_sms = SMSHelper.readCount_SEND;
					int count_mms = SMSHelper.readCount_SEND_MMS;
					totalSMS = count_sms + count_mms;
				} else if (sort_flag == 3) {
					totalSMS = SMSHelper.readCount_SEND_SUB1;
					unreadSMS = SMSHelper.unreadCount_SEND_SUB1;

				} else if (sort_flag == 4) {

					totalSMS = SMSHelper.readCount_SEND_SUB2;
					unreadSMS = SMSHelper.unreadCount_SEND_SUB2;
				}
				String ssss = TraditionalActivity.this
						.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}
		});

		button_rubbish.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				model_flag = 5;

				list.clear();
				listItemAdapter.notifyDataSetChanged();
				Log.v("tradition", "tradition");
				fillListView();

				if (sort_flag == 1) {
					totalSMS = SMSHelper.readCount_RUBBISH;
					Log.v(TAG, "readCount_RUBBISH: " + totalSMS);
				} else if (sort_flag == 2) {
					totalSMS = SMSHelper.readCount_RUBBISH_MMS;
					Log.v(TAG, "readCount_RUBBISH_MMS: " + totalSMS);
				} else if (sort_flag == 0) {

					int count_sms = SMSHelper.readCount_RUBBISH;
					int count_mms = SMSHelper.readCount_RUBBISH_MMS;

					Log.v(TAG, "readCount_RUBBISH " + count_sms
							+ "readCount_RUBBISH_MMS: " + count_mms);

					totalSMS = count_sms + count_mms;
				} else if (sort_flag == 3) {
					totalSMS = SMSHelper.readCount_RUBBISH_SUB1;
					unreadSMS = SMSHelper.unreadCount_RUBBISH_SUB1;

				} else if (sort_flag == 4) {

					totalSMS = SMSHelper.readCount_RUBBISH_SUB2;
					unreadSMS = SMSHelper.unreadCount_RUBBISH_SUB2;
				}
				String ssss = TraditionalActivity.this
						.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (model_flag == 5) {

					Toast.makeText(TraditionalActivity.this,
							R.string.rubbish_information_failure,
							Toast.LENGTH_LONG).show();

					return;
				}

				if (mlist.get(position).isSMS == true) {

					if (mlist.get(position).mRead == 0) {

						ConversationUtils.changeUnreadId(
								TraditionalActivity.this,
								mlist.get(position).mID,
								mlist.get(position).isSMS);

						fillNewMessage();

					}

					Intent intent = new Intent();

					intent.setClass(TraditionalActivity.this, detail.class);

					Bundle data = new Bundle();

					SMSListItem sms = mlist.get(position);

					String name = sms.mName;

					String address = sms.mAddress;

					String body = sms.mBody;

					long date = sms.mDate;

					data.putString("name", name);
					data.putString("address", address);
					data.putString("body", body);
					data.putLong("date", date);
					data.putInt("model_flag", model_flag);
					intent.putExtra("sms", data);

					Log.v("TraditionalActivity", "name: " + name + " address: "
							+ address + " body: " + body + " date: " + date);

					startActivity(intent);

				}

				else if (mlist.get(position).isSMS == false) {

					if (mlist.get(position).mRead == 0) {

						// ConversationUtils.changeUnreadId2(TraditionalActivity.this,
						// (int)mlist.get(position).mID);

						ConversationUtils.changeUnreadId(
								TraditionalActivity.this,
								mlist.get(position).mID,
								mlist.get(position).isSMS);

						fillNewMessage();

					}

					String stringUri = mlist.get(position).mBody;

					linkToSlideshow(stringUri);

					Log.v(TAG, "mlist body: " + s);

				}

			}

		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		isStart = false;
		super.onDestroy();

	}

	private void linkToSlideshow(String stringUri) {

		String mms = stringUri;
		Uri uri = Uri.parse(mms);
		SlideshowModel mSlideshow = null;
		try {

			mSlideshow = SlideshowModel.createFromMessageUri(
					TraditionalActivity.this, uri);
		} catch (MmsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AsyncDialog mAsyncDialog = new AsyncDialog(TraditionalActivity.this);

		try {
			MessageUtils.viewMmsMessageAttachment(TraditionalActivity.this,
					uri, mSlideshow, mAsyncDialog);
		} catch (NullPointerException e) {

			Toast.makeText(TraditionalActivity.this,
					R.string.rubbish_information_unreadable, Toast.LENGTH_LONG)
					.show();

		}

	}

	private class RefreshWorkerTask extends
			AsyncTask<Void, Void, HashMap<String, ArrayList<SMSListItem>>> {

		@Override
		protected void onPostExecute(
				HashMap<String, ArrayList<SMSListItem>> result) {
			// TODO Auto-generated method stub
			// super.onPostExecute(result);

			Log.v("ConversationUtils for test",
					"RefreshWorkerTask 在线程onPostExecute中！！！");

			TraditionalActivity.all_list = result;

			list.clear();

			fillListView();

			Log.v("ConversationUtils for test",
					"RefreshWorkerTask 在线程onPostExecute后！！！");

		}

		@Override
		protected HashMap<String, ArrayList<SMSListItem>> doInBackground(
				Void... arg0) {
			// TODO Auto-generated method stub

			Log.v("ConversationUtils for test",
					"RefreshWorkerTask 在线程doInBackground中！！！");

			SMSHelper helper = new SMSHelper();
			HashMap<String, ArrayList<SMSListItem>> mlist = helper
					.readAll(TraditionalActivity.this);

			Log.v("ConversationUtils for test",
					"RefreshWorkerTask 在线程doInBackground后！！！");

			return mlist;
		}

	}

	private void fillNewMessage() {

		RefreshWorkerTask refreshTask = new RefreshWorkerTask();

		refreshTask.execute();

	}

	// zaizhe
	private void fillSearchResult(int flag, int model_flag) {
		if (flag == 3) {

			// 收件箱
			if (model_flag == 1) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(
						ALL_INBOX,
						null,
						"type = 1 and date" + " <= "
								+ TraditionalActivity.cto.getTimeInMillis()
								+ " and " + " date " + " >= "
								+ TraditionalActivity.cfrom.getTimeInMillis(),
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 2) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(
						ALL_INBOX,
						null,
						"type = 2 and date" + " <= "
								+ TraditionalActivity.cto.getTimeInMillis()
								+ " and " + " date " + " >= "
								+ TraditionalActivity.cfrom.getTimeInMillis(),
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 3) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(
						ALL_INBOX,
						null,
						"type = 3 and date" + " <= "
								+ TraditionalActivity.cto.getTimeInMillis()
								+ " and " + " date " + " >= "
								+ TraditionalActivity.cfrom.getTimeInMillis(),
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 4) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(
						ALL_INBOX,
						null,
						"type = 4 and date" + " <= "
								+ TraditionalActivity.cto.getTimeInMillis()
								+ " and " + " date " + " >= "
								+ TraditionalActivity.cfrom.getTimeInMillis(),
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 5) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(this,
						"myBox.db3", 1);

				SQLiteDatabase db = dbHelper.getWritableDatabase();

				// unreadCount_RUBBISH=0;

				// ArrayList<SMSListItem> mSmsList = new
				// ArrayList<SMSListItem>();
				Cursor cursor = db.query(
						"failedbox",
						null,
						"date" + " <= "
								+ TraditionalActivity.cto.getTimeInMillis()
								+ " and " + " date " + " >= "
								+ TraditionalActivity.cfrom.getTimeInMillis(),
						null, null, null, "date DESC");

				// Cursor cursor = this.getContentResolver().query(ALL_INBOX,
				// null, "type = 1 and date" + " <= " +
				// TraditionalActivity.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+TraditionalActivity.cfrom.getTimeInMillis(),
				// null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			}

		} else if (flag == 2) {

			if (model_flag == 1) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(ALL_INBOX,
						null, "type = 1 and body" + " LIKE '%" + body + "%'",
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this, "无所查数据",
							Toast.LENGTH_SHORT).show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 2) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(ALL_INBOX,
						null, "type = 2 and body" + " LIKE '%" + body + "%'",
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 3) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(ALL_INBOX,
						null, "type = 3 and body" + " LIKE '%" + body + "%'",
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 4) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				Cursor cursor = this.getContentResolver().query(ALL_INBOX,
						null, "type = 4 and body" + " LIKE '%" + body + "%'",
						null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 5) {

				// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date"
				// + " <= " + ReadSMS.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+ReadSMS.cfrom.getTimeInMillis(), null,
				// "date DESC");

				FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(this,
						"myBox.db3", 1);

				SQLiteDatabase db = dbHelper.getWritableDatabase();

				// unreadCount_RUBBISH=0;

				// ArrayList<SMSListItem> mSmsList = new
				// ArrayList<SMSListItem>();
				Cursor cursor = db.query("failedbox", null, "body" + " LIKE '%"
						+ body + "%'", null, null, null, "date DESC");

				// Cursor cursor = this.getContentResolver().query(ALL_INBOX,
				// null, "type = 1 and date" + " <= " +
				// TraditionalActivity.cto.getTimeInMillis() + " and " +
				// " date "+ " >= "+TraditionalActivity.cfrom.getTimeInMillis(),
				// null, "date DESC");

				if (cursor == null || cursor.getCount() == 0) {

					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					cursor.moveToFirst();

					ArrayList<SMSListItem> mSmsList = new ArrayList<SMSListItem>();

					do {

						SMSItemTraditional item = new SMSItemTraditional();

						SMSListItem listitem = item.getItems(cursor);

						Log.v("SMSHelper for test", "listitem: ");

						// if(listitem.mRead==0){
						//
						// unreadCount_SEND++;
						//
						// }

						Contact cacheContact = Contact.get(listitem.mAddress,
								true);
						if (cacheContact != null) {

							String mName = cacheContact.getName();

							Log.v("SMSHelper for test", "mName : " + mName);

							listitem.mName = mName;

						} else {

							ContactItemTraditional contractcontact = new ContactItemTraditional();
							// getContact(context, listitem);

							SMSHelper helper = new SMSHelper();

							contractcontact = helper.getContact(this, listitem);

							if (contractcontact != null) {

								// contractcontact=getContact(context,
								// listitem);

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

								listitem.mName = contractcontact.mName;

								Log.v("SMSHelper for test",
										"contractcontact.mName: "
												+ contractcontact.mName);

							}

						}

						mSmsList.add(listitem);

					} while (cursor.moveToNext());
					cursor.close();
					cursor=null;

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", mSmsList);
					// intent.put
					startActivity(intent);

				}

			}

		} else if (flag == 1) {

			if (model_flag == 1) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				Log.v("TraditionalActivity", "flag==1 : ");

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mAddress.contains(body)) {

						Log.v("TraditionalActivity",
								"mAddress: " + addressList.get(i).mAddress
										+ "mName: " + addressList.get(i).mName);

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 2) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mAddress.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 3) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mAddress.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 4) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mAddress.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 5) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mAddress.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			}

		} else if (flag == 5) {

			if (model_flag == 1) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				Log.v("TraditionalActivity", "flag==1 : ");

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mName.contains(body)) {

						Log.v("TraditionalActivity",
								"mAddress: " + addressList.get(i).mName
										+ "mName: " + addressList.get(i).mName);

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 2) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mName.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 3) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mName.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 4) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mName.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			} else if (model_flag == 5) {

				ArrayList<SMSListItem> addressList = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> addressResultList = new ArrayList<SMSListItem>();

				addressList = mlist;

				for (int i = 0; i < addressList.size(); i++) {

					if (addressList.get(i).mName.contains(body)) {

						SMSListItem item = addressList.get(i);

						addressResultList.add(item);

					}

				}

				if (addressResultList.size() == 0) {
					Toast.makeText(TraditionalActivity.this,
							R.string.no_information_search, Toast.LENGTH_SHORT)
							.show();

				} else {

					Intent intent = new Intent(TraditionalActivity.this,
							SearchResultActivity.class);

					intent.putExtra("mlist", addressResultList);
					// intent.put
					startActivity(intent);

				}

			}

		}

	}

	private ArrayList<String> getContactidFromNumber(String phoneNumber) {
		Log.d(TAG, "getContactidFromNumber");
		ArrayList<String> contactidList = new ArrayList<String>();
		Cursor pCur = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { phoneNumber }, null);
		while (pCur.moveToNext()) {
			contactidList
					.add(pCur.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		}
		pCur.close();
		pCur=null;

		// -
		Cursor pCurFormat = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { fomatNumber(phoneNumber) }, null);
		while (pCurFormat.moveToNext()) {
			contactidList
					.add(pCurFormat.getString(pCurFormat
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		}
		pCurFormat.close();
		pCurFormat=null;

		// +86
		phoneNumber = replacePattern(phoneNumber, "^((\\+{0,1}86){0,1})", ""); // strip
																				// +86
		Cursor pCur86 = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { phoneNumber }, null);
		while (pCur86.moveToNext()) {
			contactidList
					.add(pCur86.getString(pCur86
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		}
		pCur86.close();
		pCur86=null;

		// -
		Cursor pCur86Format = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { fomatNumber(phoneNumber) }, null);
		while (pCur86Format.moveToNext()) {
			contactidList
					.add(pCur86Format.getString(pCur86Format
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		}
		pCur86Format.close();
		pCur86Format=null;
		return contactidList;
	}

	private String fomatNumber(String input) {
		if (input.startsWith("1")) {
			if (input.length() == 1) {
				return input;
			} else if (input.length() > 1 && input.length() < 5) {
				return input.substring(0, 1) + "-"
						+ input.substring(1, input.length());
			} else if (input.length() >= 5 && input.length() < 8) {
				return input.substring(0, 1) + "-" + input.substring(1, 4)
						+ "-" + input.substring(4, input.length());
			} else if (input.length() >= 8) {
				return input.substring(0, 1) + "-" + input.substring(1, 4)
						+ "-" + input.substring(4, 7) + "-"
						+ input.substring(7, input.length());
			}
		} else {
			if (input.length() <= 3) {
				return input;
			} else if (input.length() > 3 && input.length() < 7) {
				return input.substring(0, 3) + "-"
						+ input.substring(3, input.length());
			} else if (input.length() >= 7) {
				return input.substring(0, 3) + "-" + input.substring(3, 6)
						+ "-" + input.substring(6, input.length());
			}
		}
		return "";
	}

	class MyTraditionalSimpleAdapter extends SimpleAdapter {

		public MyTraditionalSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ImageView mPersonImage;
			TextView mBodyView;
			TextView mDateView;
			if (convertView == null) {

				LayoutInflater mInflater = getLayoutInflater();

				convertView = (View) mInflater
						.inflate(R.layout.listitems, null);

				mPersonImage = (ImageView) convertView
						.findViewById(R.id.textView_network);

				mBodyView = (TextView) convertView
						.findViewById(R.id.textView_body);

				mDateView = (TextView) convertView
						.findViewById(R.id.textView_date);

				convertView.setTag(R.id.tag_first, mPersonImage);
				convertView.setTag(R.id.tag_second, mBodyView);
				convertView.setTag(R.id.tag_third, mDateView);
			} else {

				mPersonImage = (ImageView) convertView.getTag(R.id.tag_first);
				mBodyView = (TextView) convertView.getTag(R.id.tag_second);
				mDateView = (TextView) convertView.getTag(R.id.tag_third);

			}

			String address = mlist.get(position).mAddress;

			Log.v(TAG, "mlist.get(position).mAddress: "
					+ mlist.get(position).mAddress);

			if (address != null && !address.equals("")) {

				String strip1 = replacePattern(address, "^((\\+{0,1}86){0,1})",
						""); // strip
				// +86
				String strip2 = replacePattern(strip1, "(\\-)", ""); // strip
																		// -
				String strip3 = replacePattern(strip2, "(\\ )", ""); // strip
																		// space
				String strip4 = ConversationUtils.replacePattern(strip3,
						"^((\\+{0,1}12520){0,1})", "");
				address = strip4;

				Bitmap ImageP = ConversationUtils.PersonImageMap.get(address);

				if (ImageP == null) {

					mPersonImage
							.setImageResource(R.drawable.ic_contact_picture);
				} else {

					mPersonImage.setImageBitmap(ImageP);
				}

			} else {

				mPersonImage.setImageResource(R.drawable.ic_contact_picture);

			}
			if (mlist.get(position).mRead == 0) {
				mBodyView.setTextColor(Color.BLUE);
				mDateView.setTextColor(Color.BLUE);
			} else {

				// int greycolor =Color.parseColor("#828282");

				mBodyView.setTextColor(Color.BLACK);
				mDateView.setTextColor(greycolor);

			}
			// if(position%2==0)
			// mPersonImage.setImageResource( R.drawable.ic_contact_picture);
			// else
			// mPersonImage.setImageResource(R.drawable.box_sent);
			return super.getView(position, convertView, parent);
		}

	}

	private String replacePattern(String origin, String pattern, String replace) {
		Log.i(TAG, "origin - " + origin);
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(origin);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, replace);
		}

		m.appendTail(sb);
		Log.i(TAG, "sb.toString() - " + sb.toString());
		return sb.toString();
	}

	private void fillListView() {

		listview = (ListView) findViewById(R.id.sms_list);

		list = readAllSMS();

		//
		// Log.v(TAG, " "+list.get(0).get("person")+" "+
		// list.get(0).get("data")+" "+list.get(0).get("person_image"));
		// list.get(0).get("person");
		//
		// list.get(0).get("data");
		//
		//
		// list.get(0).get("person_image");

		listItemAdapter = new MyTraditionalSimpleAdapter(this, list,
				R.layout.listitems, new String[] { "person", "data",
						"mms_image" }, new int[] { R.id.textView_body,
						R.id.textView_date, R.id.textView_mms });

		listview.setAdapter(listItemAdapter);

		if (sort_flag == 1) {
			if (model_flag == 1) {

				totalSMS = SMSHelper.readCount_IN;
				unreadSMS = SMSHelper.unreadCount_IN;

				String sss = this.getString(R.string.traditional_unread_count);// 010
				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum
						.setText(" " + totalSMS + ssss + " " + unreadSMS + sss);

			}

			else if (model_flag == 2) {

				totalSMS = SMSHelper.readCount_OUT;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

			else if (model_flag == 3) {

				totalSMS = SMSHelper.readCount_DRAFT;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 4) {

				totalSMS = SMSHelper.readCount_SEND;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 5) {

				totalSMS = SMSHelper.readCount_RUBBISH;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

		} else if (sort_flag == 2) {

			if (model_flag == 1) {

				totalSMS = SMSHelper.readCount_IN_MMS;
				unreadSMS = SMSHelper.unreadCount_IN_MMS;

				String sss = this.getString(R.string.traditional_unread_count);// 010
				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum
						.setText(" " + totalSMS + ssss + " " + unreadSMS + sss);

			}

			else if (model_flag == 2) {

				totalSMS = SMSHelper.readCount_OUT_MMS;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

			else if (model_flag == 3) {

				totalSMS = SMSHelper.readCount_DRAFT_MMS;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 4) {

				totalSMS = SMSHelper.readCount_SEND_MMS;

				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 5) {

				totalSMS = SMSHelper.readCount_RUBBISH_MMS;

				String ssss = this.getString(R.string.traditional_count);// 010

			}

		} else if (sort_flag == 0) {

			if (model_flag == 1) {

				int count_SMS = SMSHelper.readCount_IN;

				int count_MMS = SMSHelper.readCount_IN_MMS;

				int uncount_SMS = SMSHelper.unreadCount_IN;

				int uncount_MMS = SMSHelper.unreadCount_IN_MMS;

				totalSMS = count_SMS + count_MMS;

				unreadSMS = uncount_SMS + uncount_MMS;
				// totalSMS = SMSHelper.readCount_IN;
				// unreadSMS = SMSHelper.unreadCount_IN;

				String sss = this.getString(R.string.traditional_unread_count);// 010
				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum
						.setText(" " + totalSMS + ssss + " " + unreadSMS + sss);

			}

			else if (model_flag == 2) {

				int count_SMS = SMSHelper.readCount_OUT;

				int count_MMS = SMSHelper.readCount_OUT_MMS;

				totalSMS = count_SMS + count_MMS;

				// totalSMS = SMSHelper.readCount_OUT;
				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

			else if (model_flag == 3) {

				int count_SMS = SMSHelper.readCount_DRAFT;

				int count_MMS = SMSHelper.readCount_DRAFT_MMS;

				totalSMS = count_SMS + count_MMS;

				// totalSMS = SMSHelper.readCount_DRAFT;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 4) {

				int count_SMS = SMSHelper.readCount_SEND;

				int count_MMS = SMSHelper.readCount_SEND_MMS;

				totalSMS = count_SMS + count_MMS;

				// totalSMS = SMSHelper.readCount_SEND;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 5) {

				int count_SMS = SMSHelper.readCount_RUBBISH;

				int count_MMS = SMSHelper.readCount_RUBBISH_MMS;

				totalSMS = count_SMS + count_MMS;

				// totalSMS = SMSHelper.readCount_RUBBISH;

				Log.v(TAG, "readCount_RUBBISH: " + count_SMS
						+ "readCount_RUBBISH_MMS " + count_MMS);

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

		} else if (sort_flag == 3) {
			if (model_flag == 1) {

				totalSMS = SMSHelper.readCount_IN_SUB1;
				unreadSMS = SMSHelper.unreadCount_IN_SUB1;

				String sss = this.getString(R.string.traditional_unread_count);// 010
				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum
						.setText(" " + totalSMS + ssss + " " + unreadSMS + sss);

			}

			else if (model_flag == 2) {

				totalSMS = SMSHelper.readCount_OUT_SUB1;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

			else if (model_flag == 3) {

				totalSMS = SMSHelper.readCount_DRAFT_SUB1;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 4) {

				totalSMS = SMSHelper.readCount_SEND_SUB1;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 5) {

				totalSMS = SMSHelper.readCount_RUBBISH_SUB1;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

		} else if (sort_flag == 4) {
			if (model_flag == 1) {

				totalSMS = SMSHelper.readCount_IN_SUB2;
				unreadSMS = SMSHelper.unreadCount_IN_SUB2;

				String sss = this.getString(R.string.traditional_unread_count);// 010
				String ssss = this.getString(R.string.traditional_count);// 010

				tv_SMSNum
						.setText(" " + totalSMS + ssss + " " + unreadSMS + sss);

			}

			else if (model_flag == 2) {

				totalSMS = SMSHelper.readCount_OUT_SUB2;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

			else if (model_flag == 3) {

				totalSMS = SMSHelper.readCount_DRAFT_SUB2;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 4) {

				totalSMS = SMSHelper.readCount_SEND_SUB2;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);
			}

			else if (model_flag == 5) {

				totalSMS = SMSHelper.readCount_RUBBISH_SUB2;

				String ssss = this.getString(R.string.traditional_count);// 010
				tv_SMSNum.setText(" " + totalSMS + ssss);

			}

		}

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

	private ArrayList<HashMap<String, Object>> readAllSMS() {

		// SMSHelper helper = new SMSHelper();

		if (sort_flag == 0) {

			if (model_flag == 1) {

				mlist = all_list.get("inbox_all");

			} else if (model_flag == 2) {

				mlist = all_list.get("outbox_all");

			} else if (model_flag == 3) {

				mlist = all_list.get("draftbox_all");

			} else if (model_flag == 4) {

				mlist = all_list.get("sendbox_all");

			} else if (model_flag == 5) {

				mlist = all_list.get("rubbishbox_all");

			}

		} else if (sort_flag == 1) {

			if (model_flag == 1) {

				mlist = all_list.get("inbox");

			} else if (model_flag == 2) {

				mlist = all_list.get("outbox");

			} else if (model_flag == 3) {

				mlist = all_list.get("draftbox");

			} else if (model_flag == 4) {

				mlist = all_list.get("sendbox");

			} else if (model_flag == 5) {

				mlist = all_list.get("rubbishbox");

			}

		} else if (sort_flag == 2) {

			if (model_flag == 1) {

				mlist = all_list.get("inbox_mms");

			} else if (model_flag == 2) {

				mlist = all_list.get("outbox_mms");

			} else if (model_flag == 3) {

				mlist = all_list.get("draftbox_mms");

			} else if (model_flag == 4) {

				mlist = all_list.get("sendbox_mms");

			} else if (model_flag == 5) {

				mlist = all_list.get("rubbishbox_mms");

			}

		} else if (sort_flag == 3) {

			if (model_flag == 1) {
				SMSHelper.unreadCount_IN_SUB1 = 0;
				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("inbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 0) {

						result_list.add(temp_list.get(i));

						if (temp_list.get(i).mRead == 0) {

							SMSHelper.unreadCount_IN_SUB1++;

						}
					}

				}
				SMSHelper.readCount_IN_SUB1 = result_list.size();

				mlist = result_list;

			} else if (model_flag == 2) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("outbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 0) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_OUT_SUB1 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("outbox_all");

			} else if (model_flag == 3) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("draftbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 0) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_DRAFT_SUB1 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("draftbox_all");

			} else if (model_flag == 4) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("sendbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 0) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_SEND_SUB1 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("sendbox_all");

			} else if (model_flag == 5) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("rubbishbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 0) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_RUBBISH_SUB1 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("rubbishbox_all");

			}

		} else if (sort_flag == 4) {

			if (model_flag == 1) {

				SMSHelper.unreadCount_IN_SUB2 = 0;

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("inbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 1) {

						result_list.add(temp_list.get(i));

						if (temp_list.get(i).mRead == 0) {

							SMSHelper.unreadCount_IN_SUB2++;

						}

					}

				}
				SMSHelper.readCount_IN_SUB2 = result_list.size();
				mlist = result_list;

			} else if (model_flag == 2) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("outbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 1) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_OUT_SUB2 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("outbox_all");

			} else if (model_flag == 3) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("draftbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 1) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_DRAFT_SUB2 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("draftbox_all");

			} else if (model_flag == 4) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("sendbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 1) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_SEND_SUB2 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("sendbox_all");

			} else if (model_flag == 5) {

				ArrayList<SMSListItem> temp_list = new ArrayList<SMSListItem>();

				ArrayList<SMSListItem> result_list = new ArrayList<SMSListItem>();

				temp_list = all_list.get("rubbishbox_all");

				for (int i = 0; i < temp_list.size(); i++) {

					if (temp_list.get(i).mSubID == 1) {

						result_list.add(temp_list.get(i));

					}

				}
				SMSHelper.readCount_RUBBISH_SUB2 = result_list.size();
				mlist = result_list;

				// mlist = all_list.get("rubbishbox_all");

			}

		}

		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < mlist.size(); ++i) {

			HashMap<String, Object> item = new HashMap<String, Object>();
			SMSListItem sms = mlist.get(i);
			// ContactItemTraditional contractcontact = helper.getContact(this,
			// sms);

			int person_image;

			person_image = R.drawable.ic_contact_picture;

			item.put("person_image", person_image);

			int mms_image;

			mms_image = R.drawable.ic_attachment_universal_small;

			if (!mlist.get(i).isSMS) {

				item.put("mms_image", mms_image);
			}

			// String city = null;

			if (sms.mName == null) {
				// item.put("person",
				// sms.mAddress+left(sms.mBody.toString(),26));
				// 010
				if (sort_flag == 1) {
					item.put("person",
							sms.mAddress + "\n"
									+ left(sms.mBody.toString(), 26));
			
					Log.v("ReadSMS for test", "contractcontact.mName=null");
				} else if (sort_flag == 2) {
					item.put(
							"person",
							sms.mAddress + "\n"
									+ left(sms.mSubject.toString(), 26));
												
					Log.v("ReadSMS for test", "contractcontact.mName=null");
				} else if (sort_flag == 0 || sort_flag == 3 || sort_flag == 4) {
					if (sms.isSMS == true) {

						// SpannableStringBuilder buf = new
						// SpannableStringBuilder(sms.mAddress + "\n"
						// + left(sms.mBody.toString(), 26)+"测试！");
						// buf.setSpan(STYLE_BOLD, 0, buf.length(),
						// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						//

						item.put(
								"person",
								sms.mAddress + "\n"
										+ left(sms.mBody.toString(), 26));
					
						// item.put(
						// "person",
						// buf);
						Log.v("ReadSMS for test", "contractcontact.mName=null");
					} else {
						// SpannableStringBuilder buf = new
						// SpannableStringBuilder(sms.mAddress + "\n"
						// + left(sms.mSubject.toString(), 26)+"测试！");
						// buf.setSpan(STYLE_BOLD, 0, buf.length(),
						// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

						item.put(
								"person",
								sms.mAddress + "\n"
										+ left(sms.mSubject.toString(), 26));
			
						// item.put(
						// "person",
						// buf);
						Log.v("ReadSMS for test", "contractcontact.mName=null");
					}

				}
			} else {
				// item.put("person", sms.mName+left(sms.mBody.toString(),26));
				if (sort_flag == 1) {
					item.put("person",
							sms.mName + "\n" + left(sms.mBody.toString(), 26));
	
					Log.v("ReadSMS for test", "contractcontact.mName不是null");
				} else if (sort_flag == 2) {
					item.put("person",
							sms.mName + "\n"
									+ left(sms.mSubject.toString(), 26));

					Log.v("ReadSMS for test", "contractcontact.mName不是null");
				} else if (sort_flag == 0 || sort_flag == 3 || sort_flag == 4) {
					if (sms.isSMS == true) {

						// SpannableStringBuilder buf = new
						// SpannableStringBuilder(
						// sms.mName + "\n"
						// + left(sms.mBody.toString(), 26)+"测试！");
						// buf.setSpan(STYLE_BOLD, 0, buf.length(),
						// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

						item.put(
								"person",
								sms.mName + "\n"
										+ left(sms.mBody.toString(), 26));

						// item.put(
						// "person",
						// buf);
						Log.v("ReadSMS for test", "contractcontact.mName不是null");
					} else {
						// SpannableStringBuilder buf = new
						// SpannableStringBuilder(
						// sms.mName + "\n"
						// + left(sms.mSubject.toString(), 26)+"测试！");
						// buf.setSpan(STYLE_BOLD, 0, buf.length(),
						// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						//

						item.put(
								"person",
								sms.mName + "\n"
										+ left(sms.mSubject.toString(), 26));

						// item.put(
						// "person",
						// buf);
						Log.v("ReadSMS for test", "contractcontact.mName不是null");

					}

				}
			}

			// Date date = new Date(sms.mDate);

			String formalDate = MessageUtils.formatTimeStampString(
					TraditionalActivity.this, sms.mDate);

			// SimpleDateFormat format = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			// String formalDate = format.format(date);

			// 010 Date date = new Date(sms.mDate);
			String ss = sms.mRead == 1 ? "" : getString(R.string.status_unread);// 010
																				// only
																				// mark
																				// the
																				// unread
			// String ss=sms.mRead==1?"已读":"未读";//original
			// 010这里需要先判断是草稿还是其他箱体
			String dd = null;
			if (model_flag == 3) dd = "";
			else if (sort_flag == 0 || sort_flag == 1 || sort_flag == 2 || sort_flag == 4) 
				dd = sms.mSubID == 0 ? TraditionalActivity.this
				.getString(R.string.card_one)
				: TraditionalActivity.this.getString(R.string.card_two);
				
//			if (model_flag != 3)
///				dd = sms.mSubID == 0 ? TraditionalActivity.this
//						.getString(R.string.card_one)
//						: TraditionalActivity.this.getString(R.string.card_two);
//			else
//				dd = "";

			item.put("data", getString(R.string.traditional_date) + formalDate
					+ "\n" + ss + " " + dd);
			Log.v("item", "" + item);
			list.add(item);

		}

		// int person_image = R.drawable.ic_contact_picture;
		//
		// item.put("person_image",person_image);
		//
		// item.put("person", "曲凯明");
		//
		// // Date date=new Date();
		// // SimpleDateFormat format=new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// // String formalDate=format.format(date);
		//
		// String formalDate="2013-09-13";
		// item.put("data", "日期："+formalDate+"\n");
		//
		// Log.v("item",""+item);
		// list2.add(item);
		//
		return list;

	}

	public class SmsObserver extends ContentObserver {
		private Context context;

		public SmsObserver(Context context, Handler handler) {
			super(handler);
			this.context = context;
			Log.i("Leo-SmsObserver", "My Oberver on create");
		}

		public void onChange(boolean selfChange) {

			synchronized(this){
			fillNewMessage();
			}
			if(DEBUG)
			Log.v("SmsObserver", "sms onChange###### ");
		}

	}

	public class MmsObserver extends ContentObserver {
		private Context context;

		public MmsObserver(Context context, Handler handler) {
			super(handler);
			this.context = context;
			if(DEBUG)
			Log.i("SmsObserver", "My Oberver on create");
		}

		public void onChange(boolean selfChange) {

			//fillNewMessage();
			if(DEBUG)
			Log.v("SmsObserver", "Mms onChange###### ");
		}

	}

	public void insertToFailedBox(SQLiteDatabase db, long id) {

		ContentValues values = new ContentValues();
		values = getValues(id);
		// syncronized
		db.insert("failedbox", null, values);

	}

	public void insertToFailedMmsBox(SQLiteDatabase db, long id,
			SMSListItem delete_item) {

		ContentValues values = new ContentValues();

		values = getMmsValues(id);

		values.put("name", delete_item.mName);

		values.put("address", delete_item.mAddress);

		// syncronized
		db.insert("mmsfailedbox", null, values);

	}

	public ContentValues getMmsValues(long id) {

		Cursor c = getContentResolver().query(ALL_MMS_INBOX, null, "_id=" + id,
				null, null);

		c.moveToFirst();
		long threadId = c.getLong(c.getColumnIndex(Conversations.THREAD_ID));

		String subject = c.getString(c.getColumnIndex(Mms.SUBJECT));

		int subjectCharSet = c.getInt(c.getColumnIndex(Mms.SUBJECT_CHARSET));

		long date = c.getLong(c.getColumnIndex(Mms.DATE));
		long dateSent = c.getLong(c.getColumnIndex(Mms.DATE_SENT));
		int read = c.getInt(c.getColumnIndex(Mms.READ));

		int mType = c.getInt(c.getColumnIndex(Mms.MESSAGE_TYPE));
		int mBox = c.getInt(c.getColumnIndex(Mms.MESSAGE_BOX));

		int dReport = c.getInt(c.getColumnIndex(Mms.DELIVERY_REPORT));

		int readReport = c.getInt(c.getColumnIndex(Mms.READ_REPORT));

		int mLocked = c.getInt(c.getColumnIndex(Mms.LOCKED));

		int st = c.getInt(c.getColumnIndex(Mms.STATUS));

		c.close();

		ContentValues values = new ContentValues();

		values.put("msg_id", id);

		values.put(Conversations.THREAD_ID, threadId);

		values.put(Mms.SUBJECT, subject);

		values.put(Mms.SUBJECT_CHARSET, subjectCharSet);

		values.put(Mms.DATE, date);

		values.put(Mms.DATE_SENT, dateSent);

		values.put(Mms.READ, read);

		values.put(Mms.MESSAGE_TYPE, mType);
		values.put(Mms.MESSAGE_BOX, mBox);
		values.put(Mms.DELIVERY_REPORT, dReport);
		values.put(Mms.READ_REPORT, readReport);
		values.put(Mms.LOCKED, mLocked);
		values.put(Mms.STATUS, st);

		return values;

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

		c.close();
		if(DEBUG)
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

	private void deleteItems(long rawContentId, Context context) {
		context.getContentResolver().delete(ALL_INBOX, "_id= " + rawContentId,
				null);
	}

	private class ModeCallback implements ListView.MultiChoiceModeListener {

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub

			switch (item.getItemId()) {

			case R.id.select_all:

				for (int i = 0; i < list.size(); i++) {

					listview.setItemChecked(i, true);
				}
				break;

			case R.id.select_all_to_delete:

				if (model_flag == 5) {

					array = listview.getCheckedItemPositions();
					if(DEBUG)
					Log.v("ReadSMS", "发送报告！！！！" + array.size());

					FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(
							TraditionalActivity.this, "myBox.db3", 1);

					SQLiteDatabase db = dbHelper.getWritableDatabase();

					for (int i = 0; i < list.size(); i++) {

						if (array.get(i)) {

							SMSListItem smsitem = mlist.get(i);

							long ID = smsitem.mID;

							String[] args = { String.valueOf(ID) };

							if (smsitem.isSMS) {

								db.delete("failedbox", "_id=?", args);
								if(DEBUG)
								Log.v(TAG, "failedbox _id: " + ID);
							} else {

								if(DEBUG)
								Log.v(TAG, "failedbox _msg_id: " + ID);
								db.delete("mmsfailedbox", "msg_id=?", args);
							}
							if(DEBUG)
							Log.v("ReadSMS for Test", "触发了， ID: " + ID);

						}
					}

					db.close();

					fillNewMessage();

				} else {

					array = listview.getCheckedItemPositions();
					if(DEBUG)
					Log.v("ReadSMS", "发送报告！！！！" + array.size());

					FailedBoxDBHelper dbHelper = new FailedBoxDBHelper(
							TraditionalActivity.this, "myBox.db3", 1);

					SQLiteDatabase db = dbHelper.getWritableDatabase();

					for (int i = 0; i < list.size(); i++) {

						if (array.get(i)) {

							SMSListItem delete_item = mlist.get(i);
							if(DEBUG)
							Log.v(TAG, "delete_item.mLocked: "
									+ delete_item.mLocked
									+ " delete_item.address: "
									+ delete_item.mAddress);

							if (delete_item.mLocked == 1) {

								Toast.makeText(
										TraditionalActivity.this,
										getString(R.string.cannot_delete_unlock),
										Toast.LENGTH_SHORT).show();
								continue;

							}
							// 彩信暂不可删除到废件箱！！
							if (delete_item.isSMS) {

								long id = delete_item.mID;

								insertToFailedBox(db, id);
								if(DEBUG)
								Log.v("ReadSMS for Test", "触发了， ID: " + id);

								deleteItems(id, TraditionalActivity.this);
								if(DEBUG)
								Log.v("ReadSMS for Test",
										"触发了， ID在deleteItems之后: " + id);

								// db.close();

								//
								// delete_IDs.add(id);
								//
								// list.remove(i);
								//

							} else {

								insertToFailedMmsBox(db, delete_item.mID,
										delete_item);
								if(DEBUG)
								Log.v("ReadSMS for Test",
										"触发了， insertToFailedMmsBox ID: "
												+ delete_item.mID);

								deleteMMS(delete_item.mID);
								if(DEBUG)
								Log.v("ReadSMS for Test",
										"触发了， ID在deleteMMS之后: "
												+ delete_item.mID);

							}

						}

					}

					db.close();

					fillNewMessage();

				}

				mode.finish();
				break;

			}

			return true;
		}

		private void deleteMMS(long mid) {

			String selection = "_id=" + mid;
			getContentResolver().delete(ALL_MMS_INBOX, selection, null);

		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.list_select_menu, menu);
			mode.setTitle(getString(R.string.traditional_select_click));
			setSubtitle(mode);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onItemCheckedStateChanged(android.view.ActionMode mode,
				int position, long id, boolean checked) {
			// TODO Auto-generated method stub
			setSubtitle(mode);
		}

		private void setSubtitle(ActionMode mode) {
			final int checkedCount = listview.getCheckedItemCount();
			switch (checkedCount) {
			case 0:
				mode.setSubtitle(null);
				break;
			case 1:
				mode.setSubtitle("1"
						+ getString(R.string.traditional_select_click_number));
				break;
			default:
				mode.setSubtitle("" + checkedCount
						+ getString(R.string.traditional_select_click_number));
				break;
			}
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// We override this method to avoid restarting the entire
		// activity when the keyboard is opened (declared in
		// AndroidManifest.xml). Because the only translatable text
		// in this activity is "New Message", which has the full width
		// of phone to work with, localization shouldn't be a problem:
		// no abbreviated alternate words should be needed even in
		// 'wide' languages like German or Russian.

		super.onConfigurationChanged(newConfig);
		if(DEBUG)
		Log.v(TAG, "onConfigurationChanged: " + newConfig);
	}

}
