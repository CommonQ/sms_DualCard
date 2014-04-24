/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.mms.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.mms.LogTag;
import edu.bupt.mms.MmsApp;
import edu.bupt.mms.R;
import edu.bupt.mms.SettingTabActivity;
import edu.bupt.mms.TestActivity;
import edu.bupt.mms.data.Contact;
import edu.bupt.mms.data.ContactList;
import edu.bupt.mms.data.Conversation;
import edu.bupt.mms.data.WorkingMessage;
import edu.bupt.mms.data.Conversation.ConversationQueryHandler;
import edu.bupt.mms.numberlocate.NumberLocateSetting;

import edu.bupt.mms.transaction.MessagingNotification;
import edu.bupt.mms.transaction.SmsRejectedReceiver;
import edu.bupt.mms.util.DraftCache;
import edu.bupt.mms.util.Recycler;

import com.google.android.mms.pdu.PduHeaders;

import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.database.sqlite.SqliteWrapper;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.app.AlertDialog.Builder;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.ContactsContract.Contacts;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Threads;
import android.text.format.Time;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

/**
 * This activity provides a list view of existing conversations.
 */
public class ConversationList extends ListActivity implements
		DraftCache.OnDraftChangedListener {
	private static final String TAG = "ConversationList";
	private static final boolean DEBUG = false;
	private static final boolean LOCAL_LOGV = DEBUG;

	private static final int THREAD_LIST_QUERY_TOKEN = 1701;
	private static final int UNREAD_THREADS_QUERY_TOKEN = 1702;
	public static final int DELETE_CONVERSATION_TOKEN = 1801;
	public static final int HAVE_LOCKED_MESSAGES_TOKEN = 1802;
	private static final int DELETE_OBSOLETE_THREADS_TOKEN = 1803;
	public static Uri ALL_INBOX = Uri.parse("content://sms/");
	// IDs of the context menu items for the list of conversations.
	public static final int MENU_DELETE = 0;
	public static final int MENU_VIEW = 1;
	public static final int MENU_VIEW_CONTACT = 2;
	public static final int MENU_ADD_TO_CONTACTS = 3;

	public int flag = 0;

	private ThreadListQueryHandler mQueryHandler;
	private ConversationListAdapter mListAdapter;
	private SharedPreferences mPrefs;
	private Handler mHandler;
	private boolean mNeedToMarkAsSeen;
	private TextView mUnreadConvCount;
	// private MenuItem mSearchItem;
	// private SearchView mSearchView;

	/**
	 * 北邮ANT实验室 zzz
	 * 
	 * 类描述： 支持搜索信息
	 * 
	 * （功能52）
	 * 
	 * */

	private int cfromyear = 0;
	private int cfrommonth = 0;
	private int cfromday = 0;
	private int cfromhour = 0;
	private int ctoyear = 0;
	private int ctomonth = 0;
	private int ctoday = 0;
	private int ctohour = 0;
	public static Calendar cfrom;
	public static Calendar cto;
	private DatePicker datePickerFrom, datePickerTo;
	private TimePicker timePickerFrom, timePickerTo;
	// 初始化起止时间和DatePicker、TimePicker

	// ArrayList<Long> threadIds = new ArrayList<Long>();

	static private final String CHECKED_MESSAGE_LIMITS = "checked_message_limits";

	public Uri SMS_Conversation = Uri
			.parse("content://mms-sms/conversations?simple=true");

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

	// qq

	OnCreateNewMessageListener listener = new OnCreateNewMessageListener() {

		@Override
		public void onCreateNew() {
			// TODO Auto-generated method stub

			createNewMessage();

		}

	};

	private CreateNewMessageReceiver receiver = new CreateNewMessageReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.conversation_list_screen);

		mQueryHandler = new ThreadListQueryHandler(getContentResolver());

		ListView listView = getListView();
		listView.setOnCreateContextMenuListener(mConvListOnCreateContextMenuListener);

		listView.setOnKeyListener(mThreadListKeyListener);
		// listView.setChoiceMode(2);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		listView.setMultiChoiceModeListener(new ModeCallback());

		// Tell the list view which view to display when the list is empty
		listView.setEmptyView(findViewById(R.id.empty));// 正在加载...

		// mListAdapter.

		initListAdapter();

		setupActionBar();

		setTitle(R.string.app_label);

		mHandler = new Handler();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean checkedMessageLimits = mPrefs.getBoolean(
				CHECKED_MESSAGE_LIMITS, false);
		if (DEBUG)
			Log.v(TAG, "checkedMessageLimits: " + checkedMessageLimits);
		if (!checkedMessageLimits || DEBUG) {
			runOneTimeStorageLimitCheckForLegacyMessages();
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction("edu.bupt.createnewmessage");
		registerReceiver(receiver, filter);//不明崩

	}

	private void setupActionBar() {
		ActionBar actionBar = getActionBar();

		ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(
				R.layout.conversation_list_actionbar, null);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(v, new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
						| Gravity.RIGHT));

		mUnreadConvCount = (TextView) v.findViewById(R.id.unread_conv_count);
	}

	private final ConversationListAdapter.OnContentChangedListener mContentChangedListener = new ConversationListAdapter.OnContentChangedListener() {
		@Override
		public void onContentChanged(ConversationListAdapter adapter) {
			startAsyncQuery();
		}
	};

	private void initListAdapter() {
		mListAdapter = new ConversationListAdapter(this, null);
		mListAdapter.setOnContentChangedListener(mContentChangedListener);
		setListAdapter(mListAdapter);
		getListView().setRecyclerListener(mListAdapter);

		MapWorkerTask taskOne = new MapWorkerTask();

		taskOne.execute();

		PersonImageTask tasktwo = new PersonImageTask();

		tasktwo.execute();

		ConversationUtils.isFinishLoadAllNumber = false;

		// �ж�ʱ����ʾ����
		SharedPreferences localSharedPreferences2 = PreferenceManager
				.getDefaultSharedPreferences(this);

		int which_time = localSharedPreferences2.getInt(
				Settings.System.AUTO_TIME_ZONE, 1);

		if (which_time == 0) {
			MessageUtils.isShowBJTime = true;
		} else {
			MessageUtils.isShowBJTime = false;
		}

		//
		// CityWorkerTask tasktwo = new CityWorkerTask();
		// tasktwo.execute();

		//
		// new AsyncTask<Void, Void, Void>() {
		// protected Void doInBackground(Void... none) {
		//
		// Looper.prepare();
		//
		//
		//
		//
		//
		// Log.v("ConversationUtils for test", "���߳��У�����");
		//
		//
		//
		//
		// HashMap<String, String> CacheCityMap =
		// ConversationUtils.getAllNumbers(ConversationList.this);
		//
		//
		//
		// Log.v("ConversationUtils for test", "���߽�����");
		//
		//
		//
		//
		//
		//
		// return CacheCityMap;
		//
		// }
		// }.execute();
		//
		//

		// new Thread(new Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		//
		// Looper.prepare();
		//
		//
		// new Handler().post(new Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// Log.v("ConversationUtils for test", "���߳��У�����");
		//
		//
		//
		//
		// ConversationUtils.getAllNumbers(ConversationList.this);
		//
		//
		//
		// Log.v("ConversationUtils for test", "���߽�����");
		//
		//
		// }
		//
		// });
		//
		//
		// }
		//
		// }).start();
		//

		// new Thread( new Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		//
		// Log.v("ConversationUtils for test", "���߳��У�����");
		//
		//
		//
		//
		// ConversationUtils.getAllNumbers(ConversationList.this);
		//
		//
		// Log.v("ConversationUtils for test", "���߽�����");
		//
		// }
		//
		// }).start();

	}

	/**
	 * Checks to see if the number of MMS and SMS messages are under the limits
	 * for the recycler. If so, it will automatically turn on the recycler
	 * setting. If not, it will prompt the user with a message and point them to
	 * the setting to manually turn on the recycler.
	 */
	public synchronized void runOneTimeStorageLimitCheckForLegacyMessages() {
		if (Recycler.isAutoDeleteEnabled(this)) {
			if (DEBUG)
				Log.v(TAG, "recycler is already turned on");
			// The recycler is already turned on. We don't need to check
			// anything or warn
			// the user, just remember that we've made the check.
			markCheckedMessageLimit();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (Recycler.checkForThreadsOverLimit(ConversationList.this)) {
					if (DEBUG)
						Log.v(TAG, "checkForThreadsOverLimit TRUE");
					// Dang, one or more of the threads are over the limit. Show
					// an activity
					// that'll encourage the user to manually turn on the
					// setting. Delay showing
					// this activity until a couple of seconds after the
					// conversation list appears.
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(ConversationList.this,
									WarnOfStorageLimitsActivity.class);
							startActivity(intent);
						}
					}, 2000);
				} else {
					if (DEBUG)
						Log.v(TAG,
								"checkForThreadsOverLimit silently turning on recycler");
					// No threads were over the limit. Turn on the recycler by
					// default.
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putBoolean(
									MessagingPreferenceActivity.AUTO_DELETE,
									true);
							editor.apply();
						}
					});
				}
				// Remember that we don't have to do the check anymore when
				// starting MMS.
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						markCheckedMessageLimit();
					}
				});
			}
		}, "ConversationList.runOneTimeStorageLimitCheckForLegacyMessages")
				.start();
	}

	/**
	 * Mark in preferences that we've checked the user's message limits. Once
	 * checked, we'll never check them again, unless the user wipe-data or
	 * resets the device.
	 */
	private void markCheckedMessageLimit() {
		if (DEBUG)
			Log.v(TAG, "markCheckedMessageLimit");
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(CHECKED_MESSAGE_LIMITS, true);
		editor.apply();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// Handle intents that occur after the activity has already been
		// created.
		startAsyncQuery();
	}

	@Override
	protected void onStart() {
		super.onStart();

		MessagingNotification.cancelNotification(getApplicationContext(),
				SmsRejectedReceiver.SMS_REJECTED_NOTIFICATION_ID);

		DraftCache.getInstance().addOnDraftChangedListener(this);

		mNeedToMarkAsSeen = true;

		startAsyncQuery();

		// We used to refresh the DraftCache here, but
		// refreshing the DraftCache each time we go to the ConversationList
		// seems overly
		// aggressive. We already update the DraftCache when leaving CMA in
		// onStop() and
		// onNewIntent(), and when we delete threads or delete all in CMA or
		// this activity.
		// I hope we don't have to do such a heavy operation each time we enter
		// here.

		// we invalidate the contact cache here because we want to get updated
		// presence
		// and any contact changes. We don't invalidate the cache by observing
		// presence and contact
		// changes (since that's too untargeted), so as a tradeoff we do it
		// here.
		// If we're in the middle of the app initialization where we're loading
		// the conversation
		// threads, don't invalidate the cache because we're in the process of
		// building it.
		// TODO: think of a better way to invalidate cache more surgically or
		// based on actual
		// TODO: changes we care about
		/*
		 * if (!Conversation.loadingThreads()) { Contact.invalidateCache(); }
		 */
	}

	@Override
	protected void onStop() {
		super.onStop();

		DraftCache.getInstance().removeOnDraftChangedListener(this);

		// Simply setting the choice mode causes the previous choice mode to
		// finish and we exit
		// multi-select mode (if we're in it) and remove all the selections.
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		mListAdapter.changeCursor(null);
	}

	@Override
	public void onDraftChanged(final long threadId, final boolean hasDraft) {
		// Run notifyDataSetChanged() on the main thread.
		mQueryHandler.post(new Runnable() {
			@Override
			public void run() {
				if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
					log("onDraftChanged: threadId=" + threadId + ", hasDraft="
							+ hasDraft);
				}
				mListAdapter.notifyDataSetChanged();
			}
		});
	}

	private void startAsyncQuery() {
		try {
			((TextView) (getListView().getEmptyView()))
					.setText(R.string.loading_conversations);

			Conversation.startQueryForAll(mQueryHandler,
					THREAD_LIST_QUERY_TOKEN);
			Conversation.startQuery(mQueryHandler, UNREAD_THREADS_QUERY_TOKEN,
					Threads.READ + "=0");
		} catch (SQLiteException e) {
			SqliteWrapper.checkSQLiteException(this, e);
		}
	}

	// SearchView.OnQueryTextListener mQueryTextListener = new
	// SearchView.OnQueryTextListener() {
	// @Override
	// public boolean onQueryTextSubmit(String query) {
	// Intent intent = new Intent();
	// intent.setClass(ConversationList.this, SearchActivity.class);
	// intent.putExtra(SearchManager.QUERY, query);
	// startActivity(intent);
	// // mSearchItem.collapseActionView();
	// return true;
	// }
	//
	// @Override
	// public boolean onQueryTextChange(String newText) {
	// return false;
	// }
	// };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.conversation_list_menu, menu);

		// mSearchItem = menu.findItem(R.id.search);
		// mSearchView = (SearchView) mSearchItem.getActionView();

		// mSearchView.setOnQueryTextListener(mQueryTextListener);
		// mSearchView.setQueryHint(getString(R.string.search_hint));
		// mSearchView.setIconifiedByDefault(true);
		// SearchManager searchManager = (SearchManager)
		// getSystemService(Context.SEARCH_SERVICE);

		// if (searchManager != null) {
		// SearchableInfo info =
		// searchManager.getSearchableInfo(this.getComponentName());
		// mSearchView.setSearchableInfo(info);
		// }

		MenuItem cellBroadcastItem = menu.findItem(R.id.action_cell_broadcasts);
		if (cellBroadcastItem != null) {
			// Enable link to Cell broadcast activity depending on the value in
			// config.xml.
			boolean isCellBroadcastAppLinkEnabled = this
					.getResources()
					.getBoolean(
							com.android.internal.R.bool.config_cellBroadcastAppLinks);
			try {
				if (isCellBroadcastAppLinkEnabled) {
					PackageManager pm = getPackageManager();
					if (pm.getApplicationEnabledSetting("com.android.cellbroadcastreceiver") == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
						isCellBroadcastAppLinkEnabled = false; // CMAS app
																// disabled
					}
				}
			} catch (IllegalArgumentException ignored) {
				isCellBroadcastAppLinkEnabled = false; // CMAS app not installed
			}
			if (!isCellBroadcastAppLinkEnabled) {
				cellBroadcastItem.setVisible(false);
			}
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_delete_all);
		if (item != null) {
			item.setVisible(mListAdapter.getCount() > 0);
		}
		if (!LogTag.DEBUG_DUMP) {
			item = menu.findItem(R.id.action_debug_dump);
			if (item != null) {
				item.setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onSearchRequested() {
		// if (mSearchItem != null) {
		// mSearchItem.expandActionView();
		// }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_compose_new:
			createNewMessage();
			finish();
			break;
		case R.id.action_delete_all:
			// The invalid threadId of -1 means all threads here.
			confirmDeleteThread(-1L, mQueryHandler);
			break;
		case R.id.action_settings:
			// Intent intent = new Intent(this,
			// MessagingPreferenceActivity.class);
			Intent intent = new Intent(this, SettingTabActivity.class);
			// startActivityIfNeeded(intent, -1);
			startActivityIfNeeded(intent, -1);
			break;

		/**
		 * 北邮ANT实验室
		 * 
		 * 010
		 * 
		 * 支持搜索信息
		 * 
		 * （功能52）
		 * 
		 * */
		case R.id.action_searchtime:
			// Intent modelintent = new Intent(this, InboxActivity.class);

			AlertDialog.Builder builder = new Builder(ConversationList.this);

			builder.setSingleChoiceItems( // ���õ�ѡ�б�ѡ��
					R.array.msb, -1, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							switch (which) {

							case 0:

								LayoutInflater address_inflater = getLayoutInflater();

								View address_layout = address_inflater.inflate(
										R.layout.searchbody, null);

								Builder addressBuilder = new Builder(
										ConversationList.this);

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

												String body = addressText
														.getText().toString();

												Intent intent = new Intent();
												intent.setClass(
														ConversationList.this,
														SearchActivity.class);
												intent.putExtra(
														SearchManager.QUERY,
														body);
												startActivityIfNeeded(intent, 0);

												// fillSearchResult(flag,model_flag);
												// flag=0;
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
								try {
									addressBuilder.create().show();
								} catch (Exception e) {

									e.printStackTrace();

								}
								dialog.dismiss();

								break;

							case 1:

								dialog.dismiss();

								LayoutInflater number_inflater = getLayoutInflater();

								View numbder_layout = number_inflater.inflate(
										R.layout.searchbody, null);

								Builder numbderBuilder = new Builder(
										ConversationList.this);

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

												String body = numbderText
														.getText().toString();

												// fillSearchResult(flag,model_flag);
												// flag=0;

												Intent intent = new Intent();
												intent.setClass(
														ConversationList.this,
														SearchActivity.class);
												intent.putExtra(
														SearchManager.QUERY,
														body);
												startActivity(intent);

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

								try {
									numbderBuilder.create().show();
								} catch (Exception e) {
									e.printStackTrace();

								}
								break;

							case 2:

								dialog.dismiss();

								LayoutInflater body_inflater = getLayoutInflater();

								View body_layout = body_inflater.inflate(
										R.layout.searchbody, null);

								Builder bodyBuilder = new Builder(
										ConversationList.this);

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

												String body = bodyText
														.getText().toString();

												// fillSearchResult(flag,model_flag);
												// flag=0;

												Intent intent = new Intent();
												intent.setClass(
														ConversationList.this,
														SearchActivity.class);
												intent.putExtra(
														SearchManager.QUERY,
														body);
												startActivity(intent);

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

								try {
									bodyBuilder.create().show();
								} catch (Exception e) {

									e.printStackTrace();
								}
								break;

							case 3:// 按时间段进行搜索

								LayoutInflater inflater = getLayoutInflater();
								View layout = inflater.inflate(
										R.layout.searchdialog,
										(ViewGroup) findViewById(R.id.dialog));
								Builder searchBuilder = new Builder(
										ConversationList.this);
								searchBuilder
										.setIcon(android.R.drawable.ic_dialog_info);
								searchBuilder
										.setTitle(R.string.traditional_search_time);
								searchBuilder.setView(layout);

								// 初始化datePicker timePicker
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
								datePickerFrom.setMaxDate(now.toMillis(false));
								datePickerTo.setMaxDate(now.toMillis(false));
								// 设置搜索时间不超过当日，24小时制

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
														ctohour);
												cto.set(Calendar.MINUTE, 59);
												cto.set(Calendar.SECOND, 59);
												cto.set(Calendar.MILLISECOND, 0);

												Log.v("cto", "" + cto.getTime());

												// 010 获取起始、结束时间，精确到小时
												// 开始的分钟数设为00 结束设为59

												if (cfrom.getTimeInMillis() < cto
														.getTimeInMillis()) {

													fillSearchResult();

													// zaizhe

													Log.v("cfrom"
															+ cfrom.getTimeInMillis(),
															"cto"
																	+ cto.getTimeInMillis());
												} else {
													Toast.makeText(
															ConversationList.this,
															"选择错误",
															Toast.LENGTH_SHORT)
															.show();
												}
												// 起大于止时提示错误

												dialog.dismiss();

											}
										});
								searchBuilder.setNegativeButton(
										R.string.traditional_cancel,
										new OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												dialog.dismiss();

											}
										});

								Calendar calendar = Calendar.getInstance();
								int year = calendar.get(Calendar.YEAR);
								int monthOfYear = calendar.get(Calendar.MONTH);
								int dayOfMonth = calendar
										.get(Calendar.DAY_OF_MONTH);

								int hourOfDay = calendar
										.get(Calendar.HOUR_OF_DAY);
								int minute = calendar.get(Calendar.MINUTE);
								int second = calendar.get(Calendar.SECOND);
								int millisecond = calendar
										.get(Calendar.MILLISECOND);

								cfromyear = year;
								cfrommonth = monthOfYear + 1;
								cfromday = dayOfMonth;
								cfromhour = hourOfDay;

								ctoyear = year;
								ctomonth = monthOfYear + 1;
								ctoday = dayOfMonth;
								ctohour = hourOfDay;
								// 获取搜索时的实际时间，并对搜索起止时间进行赋值

								datePickerFrom.init(year, monthOfYear,
										dayOfMonth,
										new OnDateChangedListener() {
											public void onDateChanged(
													DatePicker view, int year,
													int monthOfYear,
													int dayOfMonth) {
												// textView_From.setText("��ѡ��������ǣ�"+year+"��"+(monthOfYear+1)+"��"+dayOfMonth+"�ա�");
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
												cfromhour = hourOfDay;
											}
										});

								datePickerTo.init(year, monthOfYear,
										dayOfMonth,
										new OnDateChangedListener() {
											public void onDateChanged(
													DatePicker view, int year,
													int monthOfYear,
													int dayOfMonth) {
												// textView_To.setText("��ѡ��������ǣ�"+year+"��"+(monthOfYear+1)+"��"+dayOfMonth+"�ա�");
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
												ctohour = hourOfDay;
											}
										});

								try {
									searchBuilder.create().show();
								} catch (Exception e) {
									e.printStackTrace();
								}
								dialog.dismiss();

								break;

							}

						}
					});

			builder.setTitle(R.string.traditional_search);
			builder.create().show();

			break;

		case R.id.action_traditional:
			// Intent modelintent = new Intent(this, InboxActivity.class);
			Intent modelintent = new Intent(this, TraditionalActivity.class);

			startActivityIfNeeded(modelintent, -1);
			break;

		case R.id.action_test:
			// Intent modelintent = new Intent(this, InboxActivity.class);
			Intent modelintent2 = new Intent(this, TestActivity.class);
			startActivityIfNeeded(modelintent2, -1);
			break;

		case R.id.action_delete_unread:
			// Intent modelintent = new Intent(this, InboxActivity.class);

			ConversationUtils.changeUnread(this);
			mListAdapter.notifyDataSetChanged();

			break;

		case R.id.action_debug_dump:
			LogTag.dumpInternalTables(this);
			break;
		case R.id.action_cell_broadcasts:
			Intent cellBroadcastIntent = new Intent(Intent.ACTION_MAIN);
			cellBroadcastIntent
					.setComponent(new ComponentName(
							"com.android.cellbroadcastreceiver",
							"com.android.cellbroadcastreceiver.CellBroadcastListActivity"));
			cellBroadcastIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(cellBroadcastIntent);
			} catch (ActivityNotFoundException ignored) {
				Log.e(TAG,
						"ActivityNotFoundException for CellBroadcastListActivity");
			}
			return true;
		default:
			return true;
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Note: don't read the thread id data from the ConversationListItem
		// view passed in.
		// It's unreliable to read the cached data stored in the view because
		// the ListItem
		// can be recycled, and the same view could be assigned to a different
		// position
		// if you click the list item fast enough. Instead, get the cursor at
		// the position
		// clicked and load the data from the cursor.
		// (ConversationListAdapter extends CursorAdapter, so
		// getItemAtPosition() should
		// return the cursor object, which is moved to the position passed in)
		Cursor cursor = (Cursor) getListView().getItemAtPosition(position);
		Conversation conv = Conversation.from(this, cursor);
		cursor.close();
		cursor = null;
		long tid = conv.getThreadId();

		if (LogTag.VERBOSE) {
			Log.d(TAG, "onListItemClick: pos=" + position + ", view=" + v
					+ ", tid=" + tid);
		}

		openThread(tid);
	}

	private void createNewMessage() {
		startActivity(ComposeMessageActivity.createIntent(this, 0));
	}

	private void openThread(long threadId) {
		startActivity(ComposeMessageActivity.createIntent(this, threadId));
	}

	public static Intent createAddContactIntent(String address) {
		// address must be a single recipient
		Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
		intent.setType(Contacts.CONTENT_ITEM_TYPE);
		if (Mms.isEmailAddress(address)) {
			intent.putExtra(ContactsContract.Intents.Insert.EMAIL, address);
		} else {
			intent.putExtra(ContactsContract.Intents.Insert.PHONE, address);
			intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
					ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		return intent;
	}

	private final OnCreateContextMenuListener mConvListOnCreateContextMenuListener = new OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {

			Log.v("onCreateContextMenu", "�����ˣ��� ");

			Cursor cursor = mListAdapter.getCursor();
			if (cursor == null || cursor.getPosition() < 0) {
				return;
			}
			Conversation conv = Conversation
					.from(ConversationList.this, cursor);
			cursor.close();
			cursor = null;
			ContactList recipients = conv.getRecipients();
			menu.setHeaderTitle(recipients.formatNames(","));

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.add(0, MENU_VIEW, 0, R.string.menu_view);

			// Only show if there's a single recipient
			if (recipients.size() == 1) {
				// do we have this recipient in contacts?
				if (recipients.get(0).existsInDatabase()) {
					menu.add(0, MENU_VIEW_CONTACT, 0,
							R.string.menu_view_contact);
				}
				// else {
				// menu.add(0, MENU_ADD_TO_CONTACTS, 0,
				// R.string.menu_add_to_contacts);
				// }
			}
			menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Cursor cursor = mListAdapter.getCursor();
		if (cursor != null && cursor.getPosition() >= 0) {
			Conversation conv = Conversation
					.from(ConversationList.this, cursor);
			long threadId = conv.getThreadId();
			switch (item.getItemId()) {
			case MENU_DELETE: {
				confirmDeleteThread(threadId, mQueryHandler);
				break;
			}
			case MENU_VIEW: {
				openThread(threadId);
				break;
			}
			case MENU_VIEW_CONTACT: {
				Contact contact = conv.getRecipients().get(0);
				Intent intent = new Intent(Intent.ACTION_VIEW, contact.getUri());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(intent);
				break;
			}
			case MENU_ADD_TO_CONTACTS: {
				String address = conv.getRecipients().get(0).getNumber();
				startActivity(createAddContactIntent(address));
				break;
			}
			default:
				break;
			}
		}
		return super.onContextItemSelected(item);
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
		if (DEBUG)
			Log.v(TAG, "onConfigurationChanged: " + newConfig);
	}

	/**
	 * Start the process of putting up a dialog to confirm deleting a thread,
	 * but first start a background query to see if any of the threads or thread
	 * contain locked messages so we'll know how detailed of a UI to display.
	 * 
	 * @param threadId
	 *            id of the thread to delete or -1 for all threads
	 * @param handler
	 *            query handler to do the background locked query
	 */
	public static void confirmDeleteThread(long threadId,
			AsyncQueryHandler handler) {
		ArrayList<Long> threadIds = null;
		if (threadId != -1) {
			threadIds = new ArrayList<Long>();
			threadIds.add(threadId);
		}
		confirmDeleteThreads(threadIds, handler);
	}

	/**
	 * Start the process of putting up a dialog to confirm deleting threads, but
	 * first start a background query to see if any of the threads contain
	 * locked messages so we'll know how detailed of a UI to display.
	 * 
	 * @param threadIds
	 *            list of threadIds to delete or null for all threads
	 * @param handler
	 *            query handler to do the background locked query
	 */
	public static void confirmDeleteThreads(Collection<Long> threadIds,
			AsyncQueryHandler handler) {
		Conversation.startQueryHaveLockedMessages(handler, threadIds,
				HAVE_LOCKED_MESSAGES_TOKEN);
	}

	/**
	 * Build and show the proper delete thread dialog. The UI is slightly
	 * different depending on whether there are locked messages in the thread(s)
	 * and whether we're deleting single/multiple threads or all threads.
	 * 
	 * @param listener
	 *            gets called when the delete button is pressed
	 * @param threadIds
	 *            the thread IDs to be deleted (pass null for all threads)
	 * @param hasLockedMessages
	 *            whether the thread(s) contain locked messages
	 * @param context
	 *            used to load the various UI elements
	 */
	public static void confirmDeleteThreadDialog(
			final DeleteThreadListener listener, Collection<Long> threadIds,
			boolean hasLockedMessages, Context context) {
		View contents = View.inflate(context,
				R.layout.delete_thread_dialog_view, null);
		TextView msg = (TextView) contents.findViewById(R.id.message);

		if (threadIds == null) {
			msg.setText(R.string.confirm_delete_all_conversations);
		} else {
			// Show the number of threads getting deleted in the confirmation
			// dialog.
			int cnt = threadIds.size();
			msg.setText(context.getResources().getQuantityString(
					R.plurals.confirm_delete_conversation, cnt, cnt));
		}

		final CheckBox checkbox = (CheckBox) contents
				.findViewById(R.id.delete_locked);
		if (!hasLockedMessages) {
			checkbox.setVisibility(View.GONE);
		} else {
			listener.setDeleteLockedMessage(checkbox.isChecked());
			checkbox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.setDeleteLockedMessage(checkbox.isChecked());
				}
			});
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.confirm_dialog_title)
				.setIconAttribute(android.R.attr.alertDialogIcon)
				.setCancelable(true)
				.setPositiveButton(R.string.delete, listener)
				.setNegativeButton(R.string.no, null).setView(contents)
				.create().show();
	}

	private final OnKeyListener mThreadListKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DEL: {
					long id = getListView().getSelectedItemId();
					if (id > 0) {
						confirmDeleteThread(id, mQueryHandler);
					}
					return true;
				}
				}
			}
			return false;
		}
	};

	public static class DeleteThreadListener implements OnClickListener {
		private final Collection<Long> mThreadIds;
		private final ConversationQueryHandler mHandler;
		private final Context mContext;
		private boolean mDeleteLockedMessages;

		public DeleteThreadListener(Collection<Long> threadIds,
				ConversationQueryHandler handler, Context context) {
			mThreadIds = threadIds;
			mHandler = handler;
			mContext = context;
		}

		public void setDeleteLockedMessage(boolean deleteLockedMessages) {
			mDeleteLockedMessages = deleteLockedMessages;
		}

		@Override
		public void onClick(DialogInterface dialog, final int whichButton) {
			MessageUtils.handleReadReport(mContext, mThreadIds,
					PduHeaders.READ_STATUS__DELETED_WITHOUT_BEING_READ,
					new Runnable() {
						@Override
						public void run() {
							int token = DELETE_CONVERSATION_TOKEN;
							if (mThreadIds == null) {
								Conversation.startDeleteAll(mHandler, token,
										mDeleteLockedMessages);
								DraftCache.getInstance().refresh();
							} else {
								for (long threadId : mThreadIds) {
									Conversation.startDelete(mHandler, token,
											mDeleteLockedMessages, threadId);
									DraftCache.getInstance().setDraftState(
											threadId, false);
								}
							}
						}
					});
			dialog.dismiss();
		}
	}

	private final Runnable mDeleteObsoleteThreadsRunnable = new Runnable() {
		@Override
		public void run() {
			if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
				LogTag.debug("mDeleteObsoleteThreadsRunnable getSavingDraft(): "
						+ DraftCache.getInstance().getSavingDraft());
			}
			if (DraftCache.getInstance().getSavingDraft()) {
				// We're still saving a draft. Try again in a second. We don't
				// want to delete
				// any threads out from under the draft.
				mHandler.postDelayed(mDeleteObsoleteThreadsRunnable, 1000);
			} else {
				Conversation.asyncDeleteObsoleteThreads(mQueryHandler,
						DELETE_OBSOLETE_THREADS_TOKEN);
			}
		}
	};

	private final class ThreadListQueryHandler extends ConversationQueryHandler {
		public ThreadListQueryHandler(ContentResolver contentResolver) {
			super(contentResolver);
		}

		// Test code used for various scenarios where its desirable to insert a
		// delay in
		// responding to query complete. To use, uncomment out the block below
		// and then
		// comment out the @Override and onQueryComplete line.
		// @Override
		// protected void onQueryComplete(final int token, final Object cookie,
		// final Cursor cursor) {
		// mHandler.postDelayed(new Runnable() {
		// public void run() {
		// myonQueryComplete(token, cookie, cursor);
		// }
		// }, 2000);
		// }
		//
		// protected void myonQueryComplete(int token, Object cookie, Cursor
		// cursor) {

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case THREAD_LIST_QUERY_TOKEN:
				mListAdapter.changeCursor(cursor);

				if (mListAdapter.getCount() == 0) {
					((TextView) (getListView().getEmptyView()))
							.setText(R.string.no_conversations);
					// 如果无会话，在查询一遍
					startAsyncQuery();
				}

				if (mNeedToMarkAsSeen) {
					mNeedToMarkAsSeen = false;
					Conversation
							.markAllConversationsAsSeen(getApplicationContext());

					// Delete any obsolete threads. Obsolete threads are threads
					// that aren't
					// referenced by at least one message in the pdu or sms
					// tables. We only call
					// this on the first query (because of mNeedToMarkAsSeen).
					mHandler.post(mDeleteObsoleteThreadsRunnable);
				}
				break;

			case UNREAD_THREADS_QUERY_TOKEN:
				int count = 0;
				if (cursor != null) {
					count = cursor.getCount();
					cursor.close();
				}
				// mUnreadConvCount.setText(count > 0 ? Integer.toString(count)
				// : null);
				break;

			case HAVE_LOCKED_MESSAGES_TOKEN:
				@SuppressWarnings("unchecked")
				Collection<Long> threadIds = (Collection<Long>) cookie;
				confirmDeleteThreadDialog(new DeleteThreadListener(threadIds,
						mQueryHandler, ConversationList.this), threadIds,
						cursor != null && cursor.getCount() > 0,
						ConversationList.this);
				if (cursor != null) {
					cursor.close();
				}
				break;

			default:
				Log.e(TAG, "onQueryComplete called with unknown token " + token);
			}
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			super.onDeleteComplete(token, cookie, result);
			switch (token) {
			case DELETE_CONVERSATION_TOKEN:
				long threadId = cookie != null ? (Long) cookie : -1; // default
																		// to
																		// all
																		// threads

				if (threadId == -1) {
					// Rebuild the contacts cache now that all threads and their
					// associated unique
					// recipients have been deleted.
					Contact.init(ConversationList.this);
				} else {
					// Remove any recipients referenced by this single thread
					// from the
					// contacts cache. It's possible for two or more threads to
					// reference
					// the same contact. That's ok if we remove it. We'll
					// recreate that contact
					// when we init all Conversations below.
					synchronized (this) {
						Conversation conv = Conversation.get(
								ConversationList.this, threadId, false);
						if (conv != null) {
							ContactList recipients = conv.getRecipients();
							for (Contact contact : recipients) {
								contact.removeFromCache();
							}
						}
					}
				}
				// Make sure the conversation cache reflects the threads in the
				// DB.
				Conversation.init(ConversationList.this);

				// Update the notification for new messages since they
				// may be deleted.
				MessagingNotification.nonBlockingUpdateNewMessageIndicator(
						ConversationList.this,
						MessagingNotification.THREAD_NONE, false);
				// Update the notification for failed messages since they
				// may be deleted.
				MessagingNotification
						.nonBlockingUpdateSendFailedNotification(ConversationList.this);

				// Make sure the list reflects the delete
				startAsyncQuery();
				break;

			case DELETE_OBSOLETE_THREADS_TOKEN:
				// Nothing to do here.
				break;
			}
		}
	}

	/**
	 * 北邮ANT实验室 by010 QQ
	 * 
	 * 对群发信息进行拆分
	 * 
	 * （功能43）
	 * 
	 * 根据短信TreadId来获取其Uri，找到数据库中对应信息的address，重新插入到一对一会话气泡中，并删除原有信息
	 * */

	private void divideSmsMessage(Conversation conv) {

		Log.v("ConversationUtils for test", "���߳��У�����");

		// long threadid = conv.getThreadId();

		long threadid = conv.getThreadId();// 获取要拆分短信的TreadId（会话序号）
		Log.v("ConversationUtils for test", "threadid =" + threadid);
		Uri conversationUri = conv.getUri();// 获取该TreadId对应的Uri
		Log.v("ConversationUtils for test", "conversationUri ="
				+ conversationUri);

		Cursor recpCursor = ConversationList.this.getContentResolver().query(
				conversationUri, MessageListAdapter.PROJECTION, null, null,
				null);
		// 通过查找数据库来获取群发短信的对应项
		if (recpCursor != null && recpCursor.getCount() != 0) {

			while (recpCursor.moveToNext()) {
				long id = recpCursor.getLong(MessageListAdapter.COLUMN_ID);
				String address = recpCursor
						.getString(MessageListAdapter.COLUMN_SMS_ADDRESS);// 获取电话
				String body = recpCursor
						.getString(MessageListAdapter.COLUMN_SMS_BODY);// 获取内容
				long date = recpCursor
						.getLong(MessageListAdapter.COLUMN_SMS_DATE);// 获取日期
				int type = recpCursor
						.getInt(MessageListAdapter.COLUMN_SMS_TYPE);// 获取信息类型（应为已发）
				int subId = recpCursor.getInt(MessageListAdapter.COLUMN_SUB_ID);// 获取卡标识
				Log.v("ConversationUtils for test", "id =" + id + "address=: "
						+ address + " body: " + body + " date" + date
						+ " type: " + type + " subId:" + subId);

				synchronized (this) {
					if (type == 1) {// 当信息为已接收，实际不应出现
						Sms.Inbox.addMessage(
								ConversationList.this.getContentResolver(),
								address, body, null, date, true /* read */,
								subId);
						Uri deleteUri = Sms.Inbox.addMessage(
								ConversationList.this.getContentResolver(),
								address, body, null, date, true /* read */,
								subId);
						ConversationList.this.getContentResolver().delete(
								deleteUri, null, null);

					} else {// 当信息为已发
						Sms.Sent.addMessage(
								ConversationList.this.getContentResolver(),
								address, body, null, date, subId);// 将信息插入到一对一会话中

						Uri deleteUri = Sms.Sent.addMessage(
								ConversationList.this.getContentResolver(),
								address, body, null, date, subId);// 将群发信息中该条信息删除

						ConversationList.this.getContentResolver().delete(
								deleteUri, null, null);// 删除群发会话（内容空）

					}

				}

			}

			recpCursor.close();

			final ArrayList<Long> threadIds = new ArrayList<Long>();
			;
			if (threadid != -1) {

				threadIds.add(threadid);
			}

			MessageUtils.handleReadReport(ConversationList.this, threadIds,
					PduHeaders.READ_STATUS__DELETED_WITHOUT_BEING_READ,
					new Runnable() {
						@Override
						public void run() {
							int token = DELETE_CONVERSATION_TOKEN;
							if (threadIds.size() == 0) {
								Conversation.startDeleteAll(mQueryHandler,
										token, true);
								DraftCache.getInstance().refresh();
							} else {
								for (long threadId : threadIds) {
									Conversation.startDelete(mQueryHandler,
											token, true, threadId);
									DraftCache.getInstance().setDraftState(
											threadId, false);
								}
							}
						}
					});// 010没看懂

		} else {

			Log.v("ConversationUtils for test",
					"recpCurso==null||recpCursor.getCount()==0");

		}

	}

	/**
	 * 北邮ANT实验室 by010 QQ
	 * 
	 * 对群发信息进行长按响应
	 * 
	 * （功能43）
	 * 
	 * 根据短信接收者的个数来确定对长按操作的响应： 当会话接收者唯一，长按可删除；当不唯一，可选择操作类型
	 * */
	private class ModeCallback implements ListView.MultiChoiceModeListener {
		private View mMultiSelectActionBarView;
		private TextView mSelectedConvCount;
		private HashSet<Long> mSelectedThreadIds;

		@Override
		public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {

			Cursor cursor = mListAdapter.getCursor();
			if (cursor == null || cursor.getPosition() < 0) {
				return true;
			}
			final Conversation conv = Conversation.from(ConversationList.this,
					cursor);
			ContactList recipients = conv.getRecipients();

			// Log.v("ConversationList",
			// "Thread id: "+recipients_all.toString());

			if (recipients.size() > 1) {// 当某个会话的接收者大于1人
				AlertDialog.Builder builder = new Builder(ConversationList.this);

				builder.setSingleChoiceItems( // 给出长按会话的两个选项：拆分或多删
						R.array.msd, -1, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:// 当选择多条删除

									MenuInflater inflater = getMenuInflater();

									inflater.inflate(
											R.menu.conversation_multi_select_menu,
											menu);

									if (mMultiSelectActionBarView == null) {
										mMultiSelectActionBarView = LayoutInflater
												.from(ConversationList.this)
												.inflate(
														R.layout.conversation_list_multi_select_actionbar,
														null);

										mSelectedConvCount = (TextView) mMultiSelectActionBarView
												.findViewById(R.id.selected_conv_count);
									}
									mode.setCustomView(mMultiSelectActionBarView);
									((TextView) mMultiSelectActionBarView
											.findViewById(R.id.title))
											.setText(R.string.select_conversations);

									dialog.dismiss();
									break;
								case 1:// 当选择拆分信息

									divideSmsMessage(conv);

									mode.finish();
									dialog.dismiss();
									break;
								}
							}
						});

				AlertDialog mPreviewDialog = builder.create();

				mPreviewDialog.setCanceledOnTouchOutside(false);

				mPreviewDialog.show();

				// builder.create().show();

			}

			else {// 当接收者唯一，直接响应为多条删除操作

				MenuInflater inflater = getMenuInflater();

				inflater.inflate(R.menu.conversation_multi_select_menu, menu);

				if (mMultiSelectActionBarView == null) {
					mMultiSelectActionBarView = LayoutInflater.from(
							ConversationList.this).inflate(
							R.layout.conversation_list_multi_select_actionbar,
							null);

					mSelectedConvCount = (TextView) mMultiSelectActionBarView
							.findViewById(R.id.selected_conv_count);
				}
				mode.setCustomView(mMultiSelectActionBarView);
				((TextView) mMultiSelectActionBarView.findViewById(R.id.title))
						.setText(R.string.select_conversations);

			}

			mSelectedThreadIds = new HashSet<Long>();
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			if (mMultiSelectActionBarView == null) {
				ViewGroup v = (ViewGroup) LayoutInflater
						.from(ConversationList.this)
						.inflate(
								R.layout.conversation_list_multi_select_actionbar,
								null);
				mode.setCustomView(v);

				mSelectedConvCount = (TextView) v
						.findViewById(R.id.selected_conv_count);
			}

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.delete:
				if (mSelectedThreadIds.size() > 0) {
					confirmDeleteThreads(mSelectedThreadIds, mQueryHandler);
				}
				mode.finish();
				break;
			case R.id.select_all_conversation:

				ListView listView = getListView();

				for (int i = 0; i < mListAdapter.getCount(); i++) {

					listView.setItemChecked(i, true);

				}
				// listView.setItemChecked(i, true);

				Log.v("ConversationList",
						"List size:!!!:  " + mListAdapter.getCount());

				break;
			default:
				break;
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ConversationListAdapter adapter = (ConversationListAdapter) getListView()
					.getAdapter();
			adapter.uncheckAll();
			mSelectedThreadIds = null;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			ListView listView = getListView();
			final int checkedCount = listView.getCheckedItemCount();
			mSelectedConvCount.setText(Integer.toString(checkedCount));

			Cursor cursor = (Cursor) listView.getItemAtPosition(position);
			Conversation conv = Conversation
					.from(ConversationList.this, cursor);
//			cursor.close();
//			cursor = null;
			conv.setIsChecked(checked);
			long threadId = conv.getThreadId();

			if (checked) {
				mSelectedThreadIds.add(threadId);
			} else {
				mSelectedThreadIds.remove(threadId);
			}
		}

	}

	private void log(String format, Object... args) {
		String s = String.format(format, args);
		Log.d(TAG, "[" + Thread.currentThread().getId() + "] " + s);
	}

	private class CityWorkerTask extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			// TODO Auto-generated method stub
			// super.onPostExecute(result);

			Log.v("ConversationUtils for test", "onPostExecute ���߳��У�����");

			ConversationUtils.CacheCityMap = result;
			ConversationUtils.isFinishLoadAllNumber = true;

			// Intent intent = new Intent();
			//
			// intent.setAction("edu.bupt.mms.filllistview");
			//
			// sendBroadcast(intent);
			//

			Log.v("ConversationUtils for test", "onPostExecute ���߽�����");

		}

		@Override
		protected HashMap<String, String> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			// Looper.prepare();

			Log.v("ConversationUtils for test", "doInBackground ���߳��У�����");

			HashMap<String, String> CacheCityMap = ConversationUtils
					.getAllNumbers(ConversationList.this);

			Log.v("ConversationUtils for test", "doInBackground ���߽�����");

			return CacheCityMap;
		}

	}

	private void fillSearchResult() {

		// Cursor cursor = activity.managedQuery(SMS_INBOX, null, "date" +
		// " <= " + ReadSMS.cto.getTimeInMillis() + " and " + " date "+
		// " >= "+ReadSMS.cfrom.getTimeInMillis(), null, "date DESC");

		Cursor cursor = this.getContentResolver().query(
				ALL_INBOX,
				null,
				"date" + " <= " + ConversationList.cto.getTimeInMillis()
						+ " and " + " date " + " >= "
						+ ConversationList.cfrom.getTimeInMillis(), null,
				"date DESC");

		if (cursor == null || cursor.getCount() == 0) {

			Toast.makeText(ConversationList.this, "无所查数据", Toast.LENGTH_SHORT)
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

				Contact cacheContact = Contact.get(listitem.mAddress, true);
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

						// contractcontact=getContact(context, listitem);

						Log.v("SMSHelper for test", "contractcontact.mName: "
								+ contractcontact.mName);

						listitem.mName = contractcontact.mName;

						Log.v("SMSHelper for test", "contractcontact.mName: "
								+ contractcontact.mName);

					}

				}

				mSmsList.add(listitem);

			} while (cursor.moveToNext());

			cursor.close();
			cursor = null;

			Intent intent = new Intent(ConversationList.this,
					SearchResultActivity.class);

			intent.putExtra("mlist", mSmsList);

			intent.putExtra("ConversationList", true);
			// intent.put
			startActivity(intent);

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

		// zaizhe 暂时去掉查四遍数据库的功能以提高效率

		// -
		// Cursor pCurFormat = getContentResolver().query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		// ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
		// new String[] { fomatNumber(phoneNumber) }, null);
		// while (pCurFormat.moveToNext()) {
		// contactidList
		// .add(pCurFormat.getString(pCurFormat
		// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		// }
		// pCurFormat.close();
		//
		// // +86
		// phoneNumber = replacePattern(phoneNumber, "^((\\+{0,1}86){0,1})",
		// ""); // strip
		// // +86
		// Cursor pCur86 = getContentResolver().query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		// ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
		// new String[] { phoneNumber }, null);
		// while (pCur86.moveToNext()) {
		// contactidList
		// .add(pCur86.getString(pCur86
		// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		// }
		// pCur86.close();
		//
		// // -
		// Cursor pCur86Format = getContentResolver().query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		// ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
		// new String[] { fomatNumber(phoneNumber) }, null);
		// while (pCur86Format.moveToNext()) {
		// contactidList
		// .add(pCur86Format.getString(pCur86Format
		// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
		// }
		// pCur86Format.close();
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

	public Bitmap loadContactPhoto(ContentResolver cr, long id) {
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, id);
		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(cr, uri);
		if (input == null) {
			return BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_contact_picture);
		}
		return BitmapFactory.decodeStream(input);
	}

	private class PersonImageTask extends
			AsyncTask<Void, Void, HashMap<String, Bitmap>> {

		@Override
		protected void onPostExecute(HashMap<String, Bitmap> result) {
			// TODO Auto-generated method stub

			ConversationUtils.PersonImageMap = result;

			Log.v("ConversationUtils for test",
					"ConversationUtils.PersonImageMap onPostExecute ���߽�����");

		}

		@Override
		protected HashMap<String, Bitmap> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			// getContactidFromNumber(mEmbeddedID);
			HashMap<String, Bitmap> CacheImageMaplocal = null;
			try {
				CacheImageMaplocal = getAllNumbers(ConversationList.this);
			} catch (Exception e) {

				CacheImageMaplocal = getAllNumbers(ConversationList.this);
				e.printStackTrace();

			}
			return CacheImageMaplocal;
		}

	}

	public HashMap<String, Bitmap> getAllNumbers(Context context) {

		HashMap<String, Bitmap> CacheImageMaplocal = new HashMap<String, Bitmap>();

		String[] projection = new String[] { "_id", "address" };
		Cursor cursor = context.getContentResolver().query(ALL_INBOX,
				projection, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String address = cursor.getString(1);

				if (address == null) {

					Log.v("ConversationList for  test!!!", "!!!!!!!!!");
					continue;

				} else {

					ArrayList<String> ImageIds = getContactidFromNumber(address);

					if (ImageIds.size() != 0) {

						Bitmap imageBitmap = loadContactPhoto(
								context.getContentResolver(),
								Long.valueOf(ImageIds.get(0)));

						String strip1 = replacePattern(address,
								"^((\\+{0,1}86){0,1})", ""); // strip
						// +86
						String strip2 = replacePattern(strip1, "(\\-)", ""); // strip
																				// -
						String strip3 = replacePattern(strip2, "(\\ )", ""); // strip
																				// space
						String strip4 = ConversationUtils.replacePattern(
								strip3, "^((\\+{0,1}12520){0,1})", "");
						address = strip4;

						CacheImageMaplocal.put(address, imageBitmap);

					}

				}

			}
			cursor.close();
			cursor = null;
		}

		return CacheImageMaplocal;
	}

	private class MapWorkerTask extends
			AsyncTask<Void, Void, HashMap<String, ArrayList<SMSListItem>>> {

		@Override
		protected void onPostExecute(
				HashMap<String, ArrayList<SMSListItem>> result) {
			// TODO Auto-generated method stub
			// super.onPostExecute(result);

			Log.v("ConversationUtils for test",
					"MapWorkerTask ���߳�onPostExecute�У�����");

			TraditionalActivity.all_list = result;

			// Log.v("ConversationUtils for test",
			// "MapWorkerTask ���߳�onPostExecute�󣡣��� "
			// +TraditionalActivity.mlist.get(0).mBody);

		}

		@Override
		protected HashMap<String, ArrayList<SMSListItem>> doInBackground(
				Void... arg0) {
			// TODO Auto-generated method stub

			Log.v("ConversationUtils for test",
					"MapWorkerTask ���߳�doInBackground�У�����");

			SMSHelper helper = new SMSHelper();
			HashMap<String, ArrayList<SMSListItem>> mlist = helper
					.readAll(ConversationList.this);

			Log.v("ConversationUtils for test",
					"MapWorkerTask ���߳�doInBackground�󣡣���");

			return mlist;
		}

	}

	class CreateNewMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			createNewMessage();

		}

	}

}

interface OnCreateNewMessageListener {

	void onCreateNew();

}
