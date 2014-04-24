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

package edu.bupt.mms;

import edu.bupt.mms.R;
import android.database.sqlite.SqliteWrapper;
import edu.bupt.mms.data.Contact;
import edu.bupt.mms.transaction.MessagingNotification;
import edu.bupt.mms.ui.ConversationList;
import edu.bupt.mms.ui.ConversationUtils;
import edu.bupt.mms.ui.SimMessageListAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony.Sms;
import android.telephony.MSimSmsManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a list of the SMS messages stored on the ICC.
 */
public class ManageSeperateSim extends Activity implements
		View.OnCreateContextMenuListener {
	private static final Uri ICC_URI = Uri.parse("content://sms/icc");
	private static final Uri ICC_URI2 = Uri.parse("content://sms/icc2");
	private static final String TAG = "ManageSeperateSim";
	private static final int MENU_COPY_TO_PHONE_MEMORY = 0;
	private static final int MENU_DELETE_FROM_SIM = 1;
	private static final int MENU_VIEW = 2;
	private static final int OPTION_MENU_DELETE_ALL = 0;
	private static final int MENU_ADD_ADDRESS_TO_CONTACTS = 32;

	private static final int SHOW_LIST = 0;
	private static final int SHOW_EMPTY = 1;
	private static final int SHOW_BUSY = 2;
	private int mState;

	private int mCount = 0;

	private ContentResolver mContentResolver;
	private Cursor mCursor = null;
	private ListView mSimList;
	private TextView mMessage;
	private TextView mHeader;
	private SimMessageListAdapter mListAdapter = null;
	private AsyncQueryHandler mQueryHandler = null;
	private Uri sim_uri = ICC_URI;

	private int mSubid;

	public static final int SIM_FULL_NOTIFICATION_ID = 234;

	private final ContentObserver simChangeObserver = new ContentObserver(
			new Handler()) {
		@Override
		public void onChange(boolean selfUpdate) {
			refreshMessageList();
		}
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Bundle localBundle = getIntent().getExtras();

		if (localBundle != null) {
			this.mSubid = localBundle.getInt("sub_id", 0);

		}

		if (this.mSubid != 0) {

			this.sim_uri = ICC_URI2;

		} else {

			this.sim_uri = ICC_URI;

		}

		mContentResolver = getContentResolver();
		mQueryHandler = new QueryHandler(mContentResolver, this);
		setContentView(R.layout.sim_list);
		mSimList = (ListView) findViewById(R.id.messages);

		mMessage = (TextView) findViewById(R.id.empty_message);

		mHeader = (TextView) findViewById(R.id.fixed_header_text);
		mHeader.setVisibility(View.VISIBLE);
		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);

		init();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);

		init();
	}

	private void init() {
		MessagingNotification.cancelNotification(getApplicationContext(),
				SIM_FULL_NOTIFICATION_ID);

		// ConversationUtils.mCountSim1=0;
		// ConversationUtils.mCountSim2=0;
		updateState(SHOW_BUSY);

		// Cursor c= ManageSeperateSim.this.getContentResolver().query(ICC_URI,
		// null, null, null, null);
		// if(c!=null){
		// ConversationUtils.mCountSim1=c.getCount();
		// }
		// c.close();
		// Cursor c2=
		// ManageSeperateSim.this.getContentResolver().query(ICC_URI2, null,
		// null, null, null);
		// if(c2!=null){
		// ConversationUtils.mCountSim2=c2.getCount();
		// }
		// c2.close();
		//
		startQuery();
	}

	private class QueryHandler extends AsyncQueryHandler {
		private final ManageSeperateSim mParent;

		public QueryHandler(ContentResolver contentResolver,
				ManageSeperateSim parent) {
			super(contentResolver);
			mParent = parent;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			mCursor = cursor;
			if (mCursor != null) {
				if (!mCursor.moveToFirst()) {
					// Let user know the SIM is empty
					updateState(SHOW_EMPTY);
				} else if (mListAdapter == null) {
					// Note that the MessageListAdapter doesn't support
					// auto-requeries. If we
					// want to respond to changes we'd need to add a line like:
					// mListAdapter.setOnDataSetChangedListener(mDataSetChangedListener);
					// See ComposeMessageActivity for an example.
					mListAdapter = new SimMessageListAdapter(mParent, mCursor,
							mSimList, false, null);

					mSimList.setAdapter(mListAdapter);
					mSimList.setOnCreateContextMenuListener(mParent);

					if (mSubid == 0) {

						ConversationUtils.mCountSim1 = mCursor.getCount();

						Log.v("ManageSeperateSim", "mSubid==0 :"
								+ ConversationUtils.mCountSim1);

					} else if (mSubid == 1) {
						ConversationUtils.mCountSim2 = mCursor.getCount();
						Log.v("ManageSeperateSim", "mSubid==1 :"
								+ ConversationUtils.mCountSim1);

					}
					mCount = ConversationUtils.mCountSim1
							+ ConversationUtils.mCountSim2;
					updateState(SHOW_LIST);
				} else {
					mListAdapter.changeCursor(mCursor);
					if (mSubid == 0) {

						ConversationUtils.mCountSim1 = mCursor.getCount();

						Log.v("ManageSeperateSim", "mSubid==0 :"
								+ ConversationUtils.mCountSim1);

					} else if (mSubid == 1) {
						ConversationUtils.mCountSim2 = mCursor.getCount();
						Log.v("ManageSeperateSim", "mSubid==1 :"
								+ ConversationUtils.mCountSim1);

					}
					mCount = ConversationUtils.mCountSim1
							+ ConversationUtils.mCountSim2;
					// mCount = mCursor.getCount();
					updateState(SHOW_LIST);
				}
				startManagingCursor(mCursor);
			} else {
				// Let user know the SIM is empty
				updateState(SHOW_EMPTY);
			}
			// Show option menu when query complete.
			invalidateOptionsMenu();
		}
	}

	private void startQuery() {
		try {
			mQueryHandler.startQuery(0, null, this.sim_uri, null, null, null,
					null);
		} catch (SQLiteException e) {
			SqliteWrapper.checkSQLiteException(this, e);
		}
	}

	private void refreshMessageList() {
		updateState(SHOW_BUSY);
		if (mCursor != null) {
			stopManagingCursor(mCursor);
			mCursor.close();
		}
		startQuery();
	}

	private void buildAddAddressToContactMenuItem(Menu menu) {
		// Look for the first recipient we don't have a contact for and create a
		// menu item to
		// add the number to contacts.

		Intent intent = ConversationList.createAddContactIntent("15510356266");
		menu.add(0, MENU_ADD_ADDRESS_TO_CONTACTS, 0,
				R.string.menu_add_to_contacts)
				.setIcon(android.R.drawable.ic_menu_add).setIntent(intent);

		// 010 added else if

	}

	public void AddContactToPhone(ContextMenu menu,
			ContextMenu.ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = null;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException exception) {
			Log.e(TAG, "Bad menuInfo.", exception);
			return;
		}
		final Cursor cursor = (Cursor) mListAdapter.getItem(info.position);
		int id = info.position;

		String address = cursor.getString(cursor
				.getColumnIndexOrThrow("address"));

		Log.v("ManageSimMessages", "id: " + id + " address:" + address);

		address = address.trim();
		address = address.replace(" ", "");

		Log.v("ComposeMessageActivity for test", address);

		String strip1 = ConversationUtils.replacePattern(address,
				"^((\\+{0,1}86){0,1})", ""); // strip
		// +86
		String strip2 = ConversationUtils.replacePattern(strip1, "(\\-)", ""); // strip
		// -
		String strip3 = ConversationUtils.replacePattern(strip2, "(\\ )", ""); // strip
		// space

		address = strip3;
		Contact cacheContact = Contact.get(address, true);
		if (cacheContact.getNumber().equalsIgnoreCase(cacheContact.getName())) {
			Log.v("ManageSimMessages", "cacheContact.getName(): "
					+ cacheContact.getName() + " cacheContact.getNumber():"
					+ cacheContact.getNumber());

			Intent intent = ConversationList.createAddContactIntent(address);

			menu.add(0, MENU_ADD_ADDRESS_TO_CONTACTS, 0,
					R.string.menu_add_to_contacts)
					.setIcon(android.R.drawable.ic_menu_add).setIntent(intent);

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(0, MENU_COPY_TO_PHONE_MEMORY, 0,
				R.string.sim_copy_to_phone_memory);
		menu.add(0, MENU_DELETE_FROM_SIM, 0, R.string.sim_delete);

		try {
			AddContactToPhone(menu, menuInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// buildAddAddressToContactMenuItem(menu);

		// TODO: Enable this once viewMessage is written.
		// menu.add(0, MENU_VIEW, 0, R.string.sim_view);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException exception) {
			Log.e(TAG, "Bad menuInfo.", exception);
			return false;
		}

		final Cursor cursor = (Cursor) mListAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case MENU_COPY_TO_PHONE_MEMORY:
			copyToPhoneMemory(cursor);
			return true;
		case MENU_DELETE_FROM_SIM:
			confirmDeleteDialog(new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					updateState(SHOW_BUSY);
					deleteFromSim(cursor);
					dialog.dismiss();
				}
			}, R.string.confirm_delete_SIM_message);
			return true;
		case MENU_VIEW:
			viewMessage(cursor);
			return true;

		case MENU_ADD_ADDRESS_TO_CONTACTS:
			Intent mAddContactIntent = item.getIntent();
			startActivityForResult(mAddContactIntent, 0);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerSimChangeObserver();
	}

	@Override
	public void onPause() {
		super.onPause();
		mContentResolver.unregisterContentObserver(simChangeObserver);
	}

	private void registerSimChangeObserver() {
		mContentResolver.registerContentObserver(this.sim_uri, true,
				simChangeObserver);
	}

	private void copyToPhoneMemory(Cursor cursor) {
		String address = cursor.getString(cursor
				.getColumnIndexOrThrow("address"));
		String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));

		Long date = 0L;

		int dateIndex = body.indexOf("***");
		if (dateIndex != -1) {
			String dateTemp = body.substring(dateIndex + 3);

			date = Long.valueOf(dateTemp);

			body = body.substring(0, dateIndex);

		} else {

			date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
		}

		try {
			if (isIncomingMessage(cursor)) {
				Sms.Inbox.addMessage(mContentResolver, address, body, null,
						date, true /* read */,this.mSubid);


				

			} else {
				Sms.Sent.addMessage(mContentResolver, address, body, null, date,this.mSubid);

				

			}
		} catch (SQLiteException e) {
			SqliteWrapper.checkSQLiteException(this, e);
		}
	}

	private boolean isIncomingMessage(Cursor cursor) {
		int messageStatus = cursor.getInt(cursor
				.getColumnIndexOrThrow("status"));

		return (messageStatus == SmsManager.STATUS_ON_ICC_READ)
				|| (messageStatus == SmsManager.STATUS_ON_ICC_UNREAD);
	}

	private void deleteFromSim(Cursor cursor) {
		String messageIndexString = cursor.getString(cursor
				.getColumnIndexOrThrow("index_on_icc"));
		Uri simUri = this.sim_uri.buildUpon().appendPath(messageIndexString)
				.build();

		SqliteWrapper.delete(this, mContentResolver, simUri, null, null);

		// MSimSmsManager.getDefault().c
	}

	private void deleteAllFromSim() {
		Cursor cursor = (Cursor) mListAdapter.getCursor();

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int count = cursor.getCount();

				for (int i = 0; i < count; ++i) {
					deleteFromSim(cursor);
					cursor.moveToNext();
				}
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if ((null != mCursor) && (mCursor.getCount() > 0)
				&& mState == SHOW_LIST) {
			menu.add(0, OPTION_MENU_DELETE_ALL, 0,
					R.string.menu_delete_messages).setIcon(
					android.R.drawable.ic_menu_delete);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTION_MENU_DELETE_ALL:
			confirmDeleteDialog(new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					updateState(SHOW_BUSY);
					deleteAllFromSim();
					dialog.dismiss();
				}
			}, R.string.confirm_delete_all_SIM_messages);
			break;

		case android.R.id.home:
			// The user clicked on the Messaging icon in the action bar. Take
			// them back from
			// wherever they came from
			finish();
			break;
		}

		return true;
	}

	private void confirmDeleteDialog(OnClickListener listener, int messageId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm_dialog_title);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.yes, listener);
		builder.setNegativeButton(R.string.no, null);
		builder.setMessage(messageId);

		builder.show();
	}

	private void updateState(int state) {
		if (mState == state) {
			return;
		}

		mState = state;
		switch (state) {
		case SHOW_LIST:
			mSimList.setVisibility(View.VISIBLE);
			mMessage.setVisibility(View.GONE);
			setTitle(getString(R.string.sim_manage_messages_title) + " "
					+ mCount + "/" + 80);
			mHeader.setText(getString(R.string.sim_manage_messages_title) + " "
					+ mCount + "/" + 80);
			setProgressBarIndeterminateVisibility(false);
			mSimList.requestFocus();
			break;
		case SHOW_EMPTY:
			mSimList.setVisibility(View.GONE);
			mMessage.setVisibility(View.VISIBLE);
			setTitle(getString(R.string.sim_manage_messages_title));
			mHeader.setText(getString(R.string.sim_manage_messages_title) + " "
					+ mCount + "/" + 80);

			setProgressBarIndeterminateVisibility(false);
			break;
		case SHOW_BUSY:
			mSimList.setVisibility(View.GONE);
			mMessage.setVisibility(View.GONE);
			setTitle(getString(R.string.refreshing));
			mHeader.setText(getString(R.string.sim_manage_messages_title) + " "
					+ mCount + "/" + 80);

			setProgressBarIndeterminateVisibility(true);
			break;
		default:
			Log.e(TAG, "Invalid State");
		}
	}

	private void viewMessage(Cursor cursor) {
		// TODO: Add this.
	}
}
