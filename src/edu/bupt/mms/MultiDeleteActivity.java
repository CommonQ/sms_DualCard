package edu.bupt.mms;

import java.util.ArrayList;
import java.util.HashMap;

import edu.bupt.mms.transaction.MessagingNotification;
import edu.bupt.mms.ui.ComposeMessageActivity;
import edu.bupt.mms.ui.MessageUtils;
import edu.bupt.mms.ui.PhraseTemplateActivity;
import edu.bupt.mms.ui.SMSListItem;
import edu.bupt.mms.ui.TraditionalActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MultiDeleteActivity extends ListActivity {

	public static final String TAG = "MultiDeleteActivity";

	public static final String MMS_PART_URI = "content://mms/part/";

	ArrayList<MultiDeleteItem> items = new ArrayList<MultiDeleteItem>();

	SparseBooleanArray isSelected = new SparseBooleanArray();

	Intent intent;

	Button selectAll;
	Button deleteButton;
	DifferentItemAdapter mAdapter;

	ArrayList<Integer> smsList = new ArrayList<Integer>();
	ArrayList<Integer> mmsList = new ArrayList<Integer>();

	BackgroundQueryHandler mQueryHandler;

	public static final int QUERY_DELETE = 1001;

	long threadId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_multi_delete);

		intent = this.getIntent();

		items = (ArrayList<MultiDeleteItem>) intent.getExtra("mlist");

		threadId = items.get(0).threadId;
		Log.v(TAG,
				"items.size: " + items.size() + " tostring: "
						+ items.toString());

		mAdapter = new DifferentItemAdapter();

		setContentView(R.layout.listview_fixed_headerandfooter);

		final ListView lv = (ListView) findViewById(android.R.id.list);

		lv.setAdapter(mAdapter);

		selectAll = (Button) findViewById(R.id.select_all_delete_multiple);

		deleteButton = (Button) findViewById(R.id.delete_multiple);

		mQueryHandler = new BackgroundQueryHandler(this.getContentResolver());

		selectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				for (int i = 0; i < lv.getCount(); i++) {

					isSelected.put(i, true);

				}

				mAdapter.notifyDataSetChanged();

			}

		});

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (lv.getCount() == isSelected.size()) {

					//ComposeMessageActivity.confirmDeleteThread(items.get(0).threadId);

				} else {

					for (int i = 0; i < lv.getCount(); i++) {

						if (isSelected.get(i)) {

							if (items.get(i).smsType.equalsIgnoreCase("sms")) {

								smsList.add(items.get(i).MsgId);

							} else if (items.get(i).smsType
									.equalsIgnoreCase("mms")) {
								mmsList.add(items.get(i).MsgId);
							}

						}

					}

					if (smsList.size() > 0) {

						for (int i = 0; i < smsList.size(); i++) {
							// MultiDeleteActivity.this.getContentResolver()
							// .delete(TraditionalActivity.ALL_INBOX,
							// "_id= " + smsList.get(i), null);
							//

							mQueryHandler.startDelete(QUERY_DELETE, null,
									TraditionalActivity.ALL_INBOX, "_id= "
											+ smsList.get(i)
											+ " and locked = 0", null);

						}

					}

					if (mmsList.size() > 0) {

						for (int i = 0; i < mmsList.size(); i++) {
							// MultiDeleteActivity.this.getContentResolver()
							// .delete(TraditionalActivity.ALL_MMS_INBOX,
							// "_id= " + mmsList.get(i), null);
							//

							mQueryHandler.startDelete(QUERY_DELETE, null,
									TraditionalActivity.ALL_MMS_INBOX, "_id= "
											+ mmsList.get(i)
											+ " and locked = 0", null);

						}

					}

					MessagingNotification.nonBlockingUpdateNewMessageIndicator(
							MultiDeleteActivity.this,
							MessagingNotification.THREAD_NONE, false);

				}

				Intent intent = getIntent();

				Log.v("MultiDeleteActivity", "MultiDeleteActivity intent");
				MultiDeleteActivity.this.setResult(RESULT_OK, intent);

				MultiDeleteActivity.this.finish();

			}

		});

		// lv.addFooterView(v);

	}

	private Cursor initCursor(int mid) {

		String selection = "mid=" + mid;
		Cursor resultCur = getContentResolver().query(Uri.parse(MMS_PART_URI),
				null, selection, null, null);

		return resultCur;
	}

	private String[] splitToArray(String string, String seq) {

		int index = string.indexOf(seq);
		String[] result = new String[] { string.substring(0, index),
				string.substring(index + 1) };

		return result;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub

		CheckBox check = (CheckBox) v.getTag();

		Log.v(TAG, "���click on this : " + position);

		// isSelected.put(position, true);

		check.toggle();

		if (check.isChecked()) {
			isSelected.put(position, true);
		} else {

			isSelected.put(position, false);

		}

		super.onListItemClick(l, v, position, id);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.multi_delete, menu);
	// return true;
	// }

	public class DifferentItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (items.get(position).type == 1) {

				if (items.get(position).smsType.equals("sms")) {

					convertView = LayoutInflater.from(MultiDeleteActivity.this)
							.inflate(R.layout.multi_delete_item_recv, parent,
									false);

					TextView text = (TextView) convertView
							.findViewById(R.id.text_view_multidelete);

					TextView date = (TextView) convertView
							.findViewById(R.id.date_view_multidelete);

					CheckBox check = (CheckBox) convertView
							.findViewById(R.id.choose_multidelete);

					String mTimestamp = MessageUtils.formatTimeStampString(
							MultiDeleteActivity.this, items.get(position).date);

					text.setText(items.get(position).mBody);
					date.setText(mTimestamp);

					if (isSelected.get(position)) {

						check.setChecked(true);

					} else if (!isSelected.get(position)) {

						check.setChecked(false);

					}

					convertView.setTag(check);
					return convertView;
				} else {

					convertView = LayoutInflater.from(MultiDeleteActivity.this)
							.inflate(R.layout.multi_delete_item_recv, parent,
									false);

					TextView text = (TextView) convertView
							.findViewById(R.id.text_view_multidelete);

					TextView date = (TextView) convertView
							.findViewById(R.id.date_view_multidelete);

					CheckBox check = (CheckBox) convertView
							.findViewById(R.id.choose_multidelete);

					text.setText(items.get(position).mSubject);

					String mTimestamp = MessageUtils.formatTimeStampString(
							MultiDeleteActivity.this,
							items.get(position).date * 1000);

					date.setText(mTimestamp);

					ViewStub mmsStub = (ViewStub) convertView
							.findViewById(R.id.mms_layout_view_stub_multidelete);
					View inflated = mmsStub.inflate();

					ImageView mImageView = (ImageView) inflated
							.findViewById(R.id.image_view);

					int mid = items.get(position).MsgId;

					Log.v(TAG, "mid :" + mid);

					Cursor curPart = null;
					try {
						curPart = initCursor(mid);
						if (curPart != null) {

							boolean isfind = false;
							if (curPart.getCount() >= 3) {
								while (curPart.moveToNext()) {

									String contentType = curPart
											.getString(curPart
													.getColumnIndexOrThrow("ct"));
									if (contentType
											.equalsIgnoreCase("text/x-vcard")) {

										mImageView
												.setImageResource(R.drawable.mms_vcard);
										isfind = true;
									break;

									}

								}

								if (!isfind) {
									mImageView
											.setImageResource(R.drawable.mms_ppt);
									isfind = false;

								}
							} else {
								curPart.moveToPosition(-1);

								while (curPart.moveToNext()) {

									String contentType = curPart
											.getString(curPart
													.getColumnIndexOrThrow("ct"));

									String[] types = splitToArray(contentType,
											"/");
									if (types[0].equalsIgnoreCase("image")) {
										mImageView
												.setImageResource(R.drawable.mms_image);
										break;
									}else if (types[0]
											.equalsIgnoreCase("audio")) {
										mImageView
												.setImageResource(R.drawable.mms_record);
										break;
									}
									else if (contentType
											.equalsIgnoreCase("text/x-vcard")) {

										mImageView
												.setImageResource(R.drawable.mms_vcard);
										
									break;

									} 
									
									
									else if (types[0]
											.equalsIgnoreCase("video")) {
										mImageView
												.setImageResource(R.drawable.mms_video);
										break;
									}  else if (types[0]
											.equalsIgnoreCase("text")) {
										mImageView
												.setImageResource(R.drawable.mms_txt);
										break;
									} else if (contentType
											.equalsIgnoreCase("application/ogg")) {

										mImageView
												.setImageResource(R.drawable.mms_record);
										break;

									}else{
										mImageView.setImageResource(R.drawable.qiqiu2_200x200);

										
									}

									Log.v(TAG,
											"contentType :" + contentType
													+ "curPart.getCount(): "
													+ curPart.getCount());

								}

							}

							

						}

					} catch (Exception e) {

						e.printStackTrace();
						mImageView.setImageResource(R.drawable.qiqiu2_200x200);


					} finally {

						curPart.close();
					}

					
					if (isSelected.get(position)) {

						check.setChecked(true);

					} else if (!isSelected.get(position)) {

						check.setChecked(false);

					}

					convertView.setTag(check);
					return convertView;
				}

			} else if (items.get(position).type == 2) {

				if (items.get(position).smsType.equals("sms")) {
					convertView = LayoutInflater.from(MultiDeleteActivity.this)
							.inflate(R.layout.multi_delete_item_send, parent,
									false);

					TextView text = (TextView) convertView
							.findViewById(R.id.text_view_multidelete);

					TextView date = (TextView) convertView
							.findViewById(R.id.date_view_multidelete);

					CheckBox check = (CheckBox) convertView
							.findViewById(R.id.choose_multidelete);

					String mTimestamp = MessageUtils.formatTimeStampString(
							MultiDeleteActivity.this, items.get(position).date);

					text.setText(items.get(position).mBody);
					date.setText(mTimestamp);

					if (isSelected.get(position)) {

						check.setChecked(true);

					} else if (!isSelected.get(position)) {

						check.setChecked(false);

					}

					convertView.setTag(check);
					return convertView;
				} else {

					convertView = LayoutInflater.from(MultiDeleteActivity.this)
							.inflate(R.layout.multi_delete_item_send, parent,
									false);

					TextView text = (TextView) convertView
							.findViewById(R.id.text_view_multidelete);

					TextView date = (TextView) convertView
							.findViewById(R.id.date_view_multidelete);

					text.setText(items.get(position).mSubject);

					CheckBox check = (CheckBox) convertView
							.findViewById(R.id.choose_multidelete);

					String mTimestamp = MessageUtils.formatTimeStampString(
							MultiDeleteActivity.this,
							items.get(position).date * 1000);

					date.setText(mTimestamp);

					ViewStub mmsStub = (ViewStub) convertView
							.findViewById(R.id.mms_layout_view_stub_multidelete);
					View inflated = mmsStub.inflate();

					ImageView mImageView = (ImageView) inflated
							.findViewById(R.id.image_view);

					int mid = items.get(position).MsgId;

					Log.v(TAG, "mid :" + mid);

					Cursor curPart = null;
					try {
						curPart = initCursor(mid);
						if (curPart != null) {

							boolean isfind = false;
							if (curPart.getCount() >= 3) {
								while (curPart.moveToNext()) {

									String contentType = curPart
											.getString(curPart
													.getColumnIndexOrThrow("ct"));
									if (contentType
											.equalsIgnoreCase("text/x-vcard")) {

										mImageView
												.setImageResource(R.drawable.mms_vcard);
										isfind = true;
									break;

									}

								}

								if (!isfind) {
									mImageView
											.setImageResource(R.drawable.mms_ppt);
									isfind = false;

								}
							} else {
								curPart.moveToPosition(-1);

								while (curPart.moveToNext()) {

									String contentType = curPart
											.getString(curPart
													.getColumnIndexOrThrow("ct"));

									String[] types = splitToArray(contentType,
											"/");
									if (types[0].equalsIgnoreCase("image")) {
										mImageView
												.setImageResource(R.drawable.mms_image);
										break;
									}else if (types[0]
											.equalsIgnoreCase("audio")) {
										mImageView
												.setImageResource(R.drawable.mms_record);
										break;
									}
									else if (contentType
											.equalsIgnoreCase("text/x-vcard")) {

										mImageView
												.setImageResource(R.drawable.mms_vcard);
										
									break;

									} 
									
									
									else if (types[0]
											.equalsIgnoreCase("video")) {
										mImageView
												.setImageResource(R.drawable.mms_video);
										break;
									}  else if (types[0]
											.equalsIgnoreCase("text")) {
										mImageView
												.setImageResource(R.drawable.mms_txt);
										break;
									} else if (contentType
											.equalsIgnoreCase("application/ogg")) {

										mImageView
												.setImageResource(R.drawable.mms_record);
										break;

									}else{
										mImageView.setImageResource(R.drawable.qiqiu2_200x200);

										
									}

									Log.v(TAG,
											"contentType :" + contentType
													+ "curPart.getCount(): "
													+ curPart.getCount());

								}

							}

							

						}

					} catch (Exception e) {

						e.printStackTrace();

						mImageView.setImageResource(R.drawable.qiqiu2_200x200);


					} finally {

						curPart.close();
					}

					//mImageView.setImageResource(R.drawable.qiqiu2_200x200);

					if (isSelected.get(position)) {

						check.setChecked(true);

					} else if (!isSelected.get(position)) {

						check.setChecked(false);

					}
					convertView.setTag(check);
					return convertView;

				}
			}

			return convertView;

			
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return super.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return super.getViewTypeCount();
		}

	}

	private String[] mTitles = { "Henry IV (1)", "Henry V", "Henry VIII",
			"Richard II", "Richard III", "Merchant of Venice", "Othello",
			"King Lear", "--------", "--------", "--------", "--------",
			"--------", "--------", "--------", "--------", "--------",
			"--------", "--------" };

	private String[] mContent = {
			null,

			"Hear him but reason in divinity,"
					+ "And all-admiring with an inward wish"
					+ "You would desire the king were made a prelate:"
					+ "Hear him debate of commonwealth affairs,"
					+ "You would say it hath been all in all his study:"
					+ "List his discourse of war, and you shall hear"
					+ "A fearful battle render'd you in music:",

			"I come no more to make you laugh: things now,"
					+ "That bear a weighty and a serious brow,",

			"First, heaven be the record to my speech!"
					+ "In the devotion of a subject's love,"
					+ "Tendering the precious safety of my prince,"
					+ "And free from other misbegotten hate,"
					+ "Come I appellant to this princely presence.",

			null,

			"To bait fish withal: if it will feed nothing else,"
					+ "it will feed my revenge. He hath disgraced me, and"
					+ "hindered me half a million; laughed at my losses,"
					+ "mocked at my gains, scorned my nation, thwarted my"
					+ "bargains, cooled my friends, heated mine"
					+ "enemies; and what's his reason? ",

			"Virtue! a fig!",

			null, "--------", null, "--------", null, "--------", null,
			"--------", null, "--------", null, "--------" };

	private class BackgroundQueryHandler extends AsyncQueryHandler {

		public BackgroundQueryHandler(ContentResolver arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			// TODO Auto-generated method stub
			super.onDeleteComplete(token, cookie, result);

			switch (token) {

			case QUERY_DELETE:

				Log.v(TAG, "ִ���� QUERY_DELETE");

				MessagingNotification.nonBlockingUpdateNewMessageIndicator(
						MultiDeleteActivity.this,
						MessagingNotification.THREAD_NONE, false);

				new Thread(new Runnable() {
					@Override
					public void run() {
						MessagingNotification.updateSendFailedNotificationForThread(
								MultiDeleteActivity.this, threadId);
					}
				}, "MultiDeleteActivity.updateSendFailedNotification").start();

				break;

			}

		}

		@Override
		public void startQuery(int token, Object cookie, Uri uri,
				String[] projection, String selection, String[] selectionArgs,
				String orderBy) {
			// TODO Auto-generated method stub
			super.startQuery(token, cookie, uri, projection, selection,
					selectionArgs, orderBy);
		}

	}

}
