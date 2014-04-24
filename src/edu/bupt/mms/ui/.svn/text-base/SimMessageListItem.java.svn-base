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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SqliteWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Profile;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Threads;
import android.telephony.PhoneNumberUtils;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.method.HideReturnsTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.bupt.contacts.msim.MultiSimConfig;
import edu.bupt.mms.LogTag;
import edu.bupt.mms.MmsApp;
import edu.bupt.mms.R;
import edu.bupt.mms.data.Contact;
import edu.bupt.mms.data.Conversation;
import edu.bupt.mms.data.WorkingMessage;
import edu.bupt.mms.model.SlideModel;
import edu.bupt.mms.model.SlideshowModel;
import edu.bupt.mms.transaction.Transaction;
import edu.bupt.mms.transaction.TransactionBundle;
import edu.bupt.mms.transaction.TransactionService;
import edu.bupt.mms.util.DownloadManager;
import edu.bupt.mms.util.ItemLoadedCallback;
import edu.bupt.mms.util.SmileyParser;
import edu.bupt.mms.util.ThumbnailManager.ImageLoaded;
import com.google.android.mms.ContentType;
import com.google.android.mms.pdu.PduHeaders;

/**
 * This class provides view of a message in the messages list.
 */
public class SimMessageListItem extends LinearLayout implements
		SlideViewInterface, OnClickListener {
	public static final String EXTRA_URLS = "edu.bupt.mms.ExtraUrls";

	private static final String TAG = "SimMessageListItem";
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_DONT_LOAD_IMAGES = false;

	static final int MSG_LIST_EDIT = 1;
	static final int MSG_LIST_PLAY = 2;
	static final int MSG_LIST_DETAILS = 3;

	private View mMmsView;
	private ImageView mImageView;
	private ImageView mLockedIndicator;
	private ImageView mDeliveredIndicator;
	private ImageView mSim1Indicator;// 010
	private ImageView mSim2Indicator;// 010
	// private ImageView mDividerImageView; // 010
	private ImageView mDetailsIndicator;
	private ImageButton mSlideShowButton;
	// private ImageButton mVcardShowButton;//010
	private TextView mBodyTextView;
	private View mDateDividerView;
	private TextView mDateDividerTextView;// 010
	private TextView mNewTimeView;// 010
	private Button mDownloadButton;
	private TextView mDownloadingLabel;
	private Handler mHandler;
	private MessageItem mMessageItem;
	private String mDefaultCountryIso;
	// private TextView mDateView; 010
	public View mMessageBlock;
	// public View mMessageDivider;// 010
	private Path mPathRight;
	private Path mPathLeft;
	private Paint mPaint;
	private QuickContactDivot mAvatar;
	private boolean mIsLastItemInList;
	static private Drawable sDefaultContactImage;
	private Presenter mPresenter;
	private int mPosition; // for debugging
	private ImageLoadedCallback mImageLoadedCallback;

	public SimMessageListItem(Context context) {
		super(context);
		mDefaultCountryIso = MmsApp.getApplication().getCurrentCountryIso();

		if (sDefaultContactImage == null) {
			sDefaultContactImage = context.getResources().getDrawable(
					R.drawable.ic_contact_picture);

		}
	}

	public SimMessageListItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		int color = mContext.getResources().getColor(R.color.timestamp_color);
		mColorSpan = new ForegroundColorSpan(color);
		mDefaultCountryIso = MmsApp.getApplication().getCurrentCountryIso();

		if (sDefaultContactImage == null) {
			sDefaultContactImage = context.getResources().getDrawable(
					R.drawable.ic_contact_picture);
		}
	}
//
//	protected void addLinkify() {
//
//		// Linkify.addLinks(mBodyTextView, Linkify.WEB_URLS);
//		Linkify.addLinks(mBodyTextView, Linkify.EMAIL_ADDRESSES);
//		Linkify.addLinks(mBodyTextView, Linkify.PHONE_NUMBERS);
//		Linkify.addLinks(mBodyTextView, Linkify.MAP_ADDRESSES);
//
//		// String expression ="[a-zA-Z0-9]+\\.+[a-zA-Z0-9]+";
//		//
//		// String
//		// expression2="^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$";
//		//
//
//		String expression3 = "\\b(([\\w-]+://?|www[.])[^\\s()<>]+(?:\\([\\w\\d]+\\)|([^[:punct:]\\s]|/)))";
//
//		String expression4 = "(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z].)[a-zA-Z0-9\\-\\.]+\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&amp;%\\$#\\=~_\\-]+))*";
//
//		String expression5 = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
//
//		String expression6 = "([a-zA-Z]\\:)(\\\\[^\\\\/:*?<>\"|]*(?<![ ]))*(\\.[a-zA-Z]{2,6})";
//
//		
//		
//		//String expression7 = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,?^=%&amp;:/~\\+#]*[\\w\\-\\?^=%&amp;/~\\+#])?";
//
//	
//		String expression7=  "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
//		Pattern pattern3 = Pattern.compile(expression3 
//				+ "|" + expression5+ "|" + expression4 + "|" + expression6 + "|" + expression7);
//
//		// Pattern pattern4 = Pattern.compile(expression4);
//
//		// Pattern pattern5 = Pattern.compile(expression5);
//
//		// Pattern pattern6 = Pattern.compile(expression6);
//
//		// Pattern pattern7 = Pattern.compile(expression7);
//
//		Linkify.addLinks(mBodyTextView, pattern3, "http://");
//
//		// Linkify.addLinks(mBodyTextView, pattern4, "http://");
//
//		// Linkify.addLinks(mBodyTextView, pattern5, "http://");
//
//		// Linkify.addLinks(mBodyTextView, pattern6, "http://");
//
//		// Linkify.addLinks(mBodyTextView, pattern7,"http://");
//
//	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mBodyTextView = (TextView) findViewById(R.id.text_view);
	//	addLinkify();
		mNewTimeView = (TextView) findViewById(R.id.date_view);// 010
		// mDateView = (TextView) findViewById(R.id.date_view);//original
		mDateDividerView = (View) findViewById(R.id.message_divider);
		mDateDividerTextView = (TextView) findViewById(R.id.date_divider_view);// 010date_divider_view
		mLockedIndicator = (ImageView) findViewById(R.id.locked_indicator);
		mDeliveredIndicator = (ImageView) findViewById(R.id.delivered_indicator);
		mSim1Indicator = (ImageView) findViewById(R.id.sim1_indicator);// 010
		mSim2Indicator = (ImageView) findViewById(R.id.sim2_indicator);// 010
		// mDividerImageView = (ImageView) findViewById(R.id.message_divider);
		// //010
		mDetailsIndicator = (ImageView) findViewById(R.id.details_indicator);
		mAvatar = (QuickContactDivot) findViewById(R.id.avatar);
		mMessageBlock = findViewById(R.id.message_block);
		// mMessageDivider = findViewById(R.id.message_divider);// 010
	}

	public void bind(MessageItem msgItem, boolean isLastItem, int position) {
		mMessageItem = msgItem;

		mIsLastItemInList = isLastItem;
		mPosition = position;

		setLongClickable(false);
		setClickable(false); // let the list view handle clicks on the item
								// normally. When
								// clickable is true, clicks bypass the listview
								// and go straight
								// to this listitem. We always want the listview
								// to handle the
								// clicks first.

		switch (msgItem.mMessageType) {
		case PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND:
			bindNotifInd();
			break;
		default:
			bindCommonMessage();
			break;
		}

		// Log.v(TAG, "TextSize = " + mBodyTextView.getTextSize()
		// + ", LineSpacing = " + mBodyTextView.getLineSpacingExtra());
		// 010

	}

	public void unbind() {
		// Clear all references to the message item, which can contain
		// attachments and other
		// memory-intensive objects
		mMessageItem = null;
		if (mImageView != null) {
			// Because #setOnClickListener may have set the listener to an
			// object that has the
			// message item in its closure.
			mImageView.setOnClickListener(null);
		}
		if (mSlideShowButton != null) {
			// Because #drawPlaybackButton sets the tag to mMessageItem
			mSlideShowButton.setTag(null);
		}
		// leave the presenter in case it's needed when rebound to a different
		// MessageItem.
		if (mPresenter != null) {
			mPresenter.cancelBackgroundLoading();
		}
	}

	public MessageItem getMessageItem() {
		return mMessageItem;
	}

	public void setMsgListItemHandler(Handler handler) {
		mHandler = handler;
	}

	private void bindNotifInd() {
		showMmsView(false);

		String msgSizeText = mContext.getString(R.string.message_size_label)
				+ String.valueOf((mMessageItem.mMessageSize + 1023) / 1024)
				+ mContext.getString(R.string.kilobyte);

		mBodyTextView.setText(formatMessage(mMessageItem,
				mMessageItem.mContact, null, mMessageItem.mSubscription,
				mMessageItem.mSubject, mMessageItem.mHighlight,
				mMessageItem.mTextContentType));

		// zaizhe
		// Linkify.addLinks(mBodyTextView, Linkify.WEB_URLS);
	//	addLinkify();
		mBodyTextView.setTextSize(ComposeMessageActivity.textSize);// 010

		// mDateView.setText(msgSizeText + " " +
		// mMessageItem.mTimestamp);//010deleted

		// 010:
		// if
		// ("mms".equals(cursor.getString(MessageListAdapter.COLUMN_MSG_TYPE)));
		mNewTimeView.setText(msgSizeText + " " + mMessageItem.mNewTimestamp);
		// mDateDividerTextView.setText(msgSizeText + " "
		// + mMessageItem.mDatestamp);
		mDateDividerTextView.setText(mMessageItem.mDatestamp);
		// mDateTextView.setTextSize(ComposeMessageActivity.textSize);
		// 010above

		switch (mMessageItem.getMmsDownloadStatus()) {

		case DownloadManager.STATE_DOWNLOADING:
			showDownloadingAttachment();
			// mDateDividerTextView.setText(mMessageItem.mDatestamp);// 010added
			//
			// try {
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// // 010TAG
			// // Log.e(TAG,"thread_id = "+ Conversation。);
			// }
			// } catch (Exception e) {
			// mDateDividerView.setVisibility(View.GONE);
			//
			// }
			// // 010
			break;
		case DownloadManager.STATE_UNKNOWN:
			// mDateDividerTextView.setText(mMessageItem.mDatestamp);// 010added
			//
			// try {
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// // 010TAG
			// // Log.e(TAG,"thread_id = "+ Conversation。);
			// }
			// } catch (Exception e) {
			// mDateDividerView.setVisibility(View.GONE);
			// }
			// // 010

		case DownloadManager.STATE_UNSTARTED:
			// mDateDividerTextView.setText(mMessageItem.mDatestamp);// 010added
			//
			// // 防止突然出现彩信
			// try {
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// // 010TAG
			// // Log.e(TAG,"thread_id = "+ Conversation。);
			// }
			// } catch (Exception e) {
			//
			// mDateDividerView.setVisibility(View.GONE);
			//
			// }
			// // 010
			DownloadManager downloadManager = DownloadManager.getInstance();
			boolean autoDownload = downloadManager.isAuto();
			boolean dataSuspended = (MmsApp.getApplication()
					.getTelephonyManager().getDataState() == TelephonyManager.DATA_SUSPENDED);

			// If we're going to automatically start downloading the mms
			// attachment, then
			// don't bother showing the download button for an instant before
			// the actual
			// download begins. Instead, show downloading as taking place.
			if (autoDownload && !dataSuspended) {
				showDownloadingAttachment();
				break;
			}
		case DownloadManager.STATE_TRANSIENT_FAILURE:
			// mDateDividerTextView.setText(mMessageItem.mDatestamp);// 010added
			//
			// try {
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// // 010TAG
			// // Log.e(TAG,"thread_id = "+ Conversation。);
			// }
			// } catch (Exception e) {
			// mDateDividerView.setVisibility(View.GONE);
			// }
			// // 010
		case DownloadManager.STATE_PERMANENT_FAILURE:
			// mDateDividerTextView.setText(mMessageItem.mDatestamp);// 010added
			//
			// try {
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// // 010TAG
			// // Log.e(TAG,"thread_id = "+ Conversation。);
			// }
			// } catch (Exception e) {
			// mDateDividerView.setVisibility(View.GONE);
			// }
			// // 010
		default:
			setLongClickable(true);
			inflateDownloadControls();
			// try {
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// // 010TAG
			// // Log.e(TAG,"thread_id = "+ Conversation。);
			// }
			// } catch (Exception e) {
			// mDateDividerView.setVisibility(View.GONE);
			// }
			// // 010
			mDownloadingLabel.setVisibility(View.GONE);
			mDownloadButton.setVisibility(View.VISIBLE);
			mDownloadButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// try {
					// if
					// (ComposeMessageActivity.items.get(mPosition).visibility
					// == false) {
					// mDateDividerView.setVisibility(View.GONE);
					// } else {
					// mDateDividerView.setVisibility(View.VISIBLE);
					// // 010TAG
					// // Log.e(TAG,"thread_id = "+ Conversation。);
					// }
					// } catch (Exception e) {
					// mDateDividerView.setVisibility(View.GONE);
					// }
					// // 010
					mDownloadingLabel.setVisibility(View.VISIBLE);
					mDownloadButton.setVisibility(View.GONE);
					Intent intent = new Intent(mContext,
							TransactionService.class);
					intent.putExtra(TransactionBundle.URI,
							mMessageItem.mMessageUri.toString());
					intent.putExtra(TransactionBundle.TRANSACTION_TYPE,
							Transaction.RETRIEVE_TRANSACTION);
					if (MultiSimConfig.isMultiSimEnabled()) {
						Log.d(TAG, "Download button pressed for sub="
								+ mMessageItem.mSubscription);
						intent.putExtra(Mms.SUB_ID, mMessageItem.mSubscription);

						Log.d(TAG,
								"Manual download is always silent transaction");
						Intent silentIntent = new Intent(mContext,
								edu.bupt.mms.ui.SelectMmsSubscription.class);
						silentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						silentIntent.putExtras(intent); // copy all extras
						mContext.startService(silentIntent);
					} else {
						mContext.startService(intent);
					}

				}
			});
			break;
		}

		// Hide the indicators.
		mLockedIndicator.setVisibility(View.GONE);
		mDeliveredIndicator.setVisibility(View.GONE);// 010report
		mDetailsIndicator.setVisibility(View.GONE);
		// updateAvatarView(mMessageItem.mAddress, false);
	}

	private void showDownloadingAttachment() {
		inflateDownloadControls();

		 try {
		Time now = new Time();
		now.setToNow();
		if ((mPosition >= ComposeMessageActivity.items.size())
				&& !ComposeMessageActivity.areSameDay(
						ComposeMessageActivity.items.get(mPosition - 1).date,
						now.toMillis(false), mContext)) {
			mDateDividerView.setVisibility(View.GONE);
			Log.e(TAG,"1 append");
		} else if ((mPosition >= ComposeMessageActivity.items.size())
				&& ComposeMessageActivity.areSameDay(
						ComposeMessageActivity.items.get(mPosition - 1).date,
						now.toMillis(false), mContext)) {
			mDateDividerView.setVisibility(View.VISIBLE);
			Log.e(TAG,"2 append");
		} else if ((mPosition < ComposeMessageActivity.items.size())
				&& ComposeMessageActivity.items.get(mPosition).visibility == false) {
			mDateDividerView.setVisibility(View.GONE);
			Log.e(TAG,"3 append");
		} else if (ComposeMessageActivity.items.get(mPosition).visibility == true) {
			mDateDividerView.setVisibility(View.VISIBLE);
			// 010TAG
			 Log.e(TAG,"4 append");
		}

		 }

		 catch (Exception e) {
			 //010TAG
			 Log.e(TAG,"error append");
			
			// if (ComposeMessageActivity.items.get(mPosition).visibility ==
			// false) {
			// mDateDividerView.setVisibility(View.GONE);
			// } else {
			// mDateDividerView.setVisibility(View.VISIBLE);
			// }
//		} catch (Exception e) {
			mDateDividerView.setVisibility(View.GONE);
			}
//		}
		// 010
		mDownloadingLabel.setVisibility(View.VISIBLE);
		mDownloadButton.setVisibility(View.GONE);
	}

	private void updateAvatarView(String addr, boolean isSelf) {
		Drawable avatarDrawable;

		Log.v("SimMessageListItem updateAvatarView", "Entered!!");

		if (isSelf || !TextUtils.isEmpty(addr)) {
			Contact contact = isSelf ? Contact.getMe(false) : Contact.get(addr,
					false);
			avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);

			if (isSelf) {
				mAvatar.assignContactUri(Profile.CONTENT_URI);

				Log.v("SimMessageListItem updateAvatarView isSelf", "Entered!!"
						+ Profile.CONTENT_URI);

			} else {
				if (contact.existsInDatabase()) {
					mAvatar.assignContactUri(contact.getUri());

					Log.v("SimMessageListItem updateAvatarView contact.existsInDatabase()",
							"Entered!!" + contact.getUri());

				} else {
					mAvatar.assignContactFromPhone(contact.getNumber(), true);

					Log.v("SimMessageListItem updateAvatarView !contact.existsInDatabase()",
							"Entered!!" + contact.getNumber());

				}
			}
		} else {
			avatarDrawable = sDefaultContactImage;
			Log.v("SimMessageListItem updateAvatarView !contact.existsInDatabase()",
					"Entered!! 最后的else");

		}
		// zaizhe
		// sDefaultContactImage =
		// getResources().getDrawable(R.drawable.ic_contact_picture);
		// avatarDrawable = sDefaultContactImage;
		// zaizhe
		Log.v("SimMessageListItem", "加载中！！！");
		mAvatar.setImageDrawable(avatarDrawable);
	}

	private void bindCommonMessage() {
		if (mDownloadButton != null) {
			// mDateDividerView.setVisibility(View.GONE);// 010可能会有问题
			mDownloadButton.setVisibility(View.GONE);
			mDownloadingLabel.setVisibility(View.GONE);
		}
		// Since the message text should be concatenated with the sender's
		// address(or name), I have to display it here instead of
		// displaying it by the Presenter.
		mBodyTextView.setTransformationMethod(HideReturnsTransformationMethod
				.getInstance());

		mBodyTextView.setTextSize(ComposeMessageActivity.textSize);

		boolean isSelf = Sms.isOutgoingFolder(mMessageItem.mBoxId);
		String addr = isSelf ? null : mMessageItem.mAddress;
		// updateAvatarView(addr, isSelf);

		// Get and/or lazily set the formatted message from/on the
		// MessageItem. Because the MessageItem instances come from a
		// cache (currently of size ~50), the hit rate on avoiding the
		// expensive formatMessage() call is very high.
		CharSequence formattedMessage = mMessageItem
				.getCachedFormattedMessage();
		if (formattedMessage == null) {
			formattedMessage = formatMessage(mMessageItem,
					mMessageItem.mContact, mMessageItem.mBody,
					mMessageItem.mSubscription, mMessageItem.mSubject,
					mMessageItem.mHighlight, mMessageItem.mTextContentType);
			mMessageItem.setCachedFormattedMessage(formattedMessage);
		}
		mBodyTextView.setText(formattedMessage);

		// zaizhe
		// Linkify.addLinks(mBodyTextView, Linkify.WEB_URLS);
	//	addLinkify();
		// mBodyTextView.setTextSize(50);

		// Debugging code to put the URI of the image attachment in the body of
		// the list item.
		if (DEBUG) {
			String debugText = null;
			if (mMessageItem.mSlideshow == null) {
				debugText = "NULL slideshow";
			} else {
				SlideModel slide = ((SlideshowModel) mMessageItem.mSlideshow)
						.get(0);
				if (slide == null) {
					debugText = "NULL first slide";
				} else if (!slide.hasImage()) {
					debugText = "Not an image";
				} else {
					debugText = slide.getImage().getUri().toString();
				}
			}
			mBodyTextView.setText(mPosition + ": " + debugText);

			// zaizhe
			// Linkify.addLinks(mBodyTextView, Linkify.WEB_URLS);
	//		addLinkify();
		}

		// If we're in the process of sending a message (i.e. pending), then we
		// show a "SENDING..."
		// string in place of the timestamp.
		// mDateView.setText(mMessageItem.isSending() ? mContext.getResources()
		// .getString(R.string.sending_message) :
		// mMessageItem.mTimestamp);//original

		// 010 just try
		Time now = new Time();
		now.setToNow();
		// mContext.getResources()
		// .getString(R.string.sending_message)
		mDateDividerTextView.setText(mMessageItem.isSending() ? now
				.format3339(true) : mMessageItem.mDatestamp);

		Log.v("zzz", " mMessageItem.mDatestamp -" + mMessageItem.mDatestamp);
		// 010
		mNewTimeView.setText(mMessageItem.isSending() ? mContext.getResources()
				.getString(R.string.sending_message)
				: mMessageItem.mNewTimestamp);

		if (mMessageItem.isSms()) {
			showMmsView(false);
			mMessageItem.setOnPduLoaded(null);
		} else {
			if (DEBUG) {
				Log.v(TAG, "bindCommonMessage for item: " + mPosition + " "
						+ mMessageItem.toString()
						+ " mMessageItem.mAttachmentType: "
						+ mMessageItem.mAttachmentType);
			}
			if (mMessageItem.mAttachmentType != WorkingMessage.TEXT) {
				setImage(null, null);
				setOnClickListener(mMessageItem);
				drawPlaybackButton(mMessageItem);
			} else {
				showMmsView(false);
			}
			if (mMessageItem.mSlideshow == null) {
				mMessageItem
						.setOnPduLoaded(new MessageItem.PduLoadedCallback() {
							public void onPduLoaded(MessageItem messageItem) {
								if (DEBUG) {
									Log.v(TAG,
											"PduLoadedCallback in SimMessageListItem for item: "
													+ mPosition
													+ " "
													+ (mMessageItem == null ? "NULL"
															: mMessageItem
																	.toString())
													+ " passed in item: "
													+ (messageItem == null ? "NULL"
															: messageItem
																	.toString()));
								}
								if (messageItem != null
										&& mMessageItem != null
										&& messageItem.getMessageId() == mMessageItem
												.getMessageId()) {
									mMessageItem
											.setCachedFormattedMessage(null);
									bindCommonMessage();
								}
							}
						});
			} else {
				if (mPresenter == null) {
					mPresenter = PresenterFactory.getPresenter(
							"MmsThumbnailPresenter", mContext, this,
							mMessageItem.mSlideshow);
				} else {
					mPresenter.setModel(mMessageItem.mSlideshow);
					mPresenter.setView(this);
				}
				if (mImageLoadedCallback == null) {
					mImageLoadedCallback = new ImageLoadedCallback(this);
				} else {
					mImageLoadedCallback.reset(this);
				}
				mPresenter.present(mImageLoadedCallback);
			}
		}
		drawRightStatusIndicator(mMessageItem);

		requestLayout();
	}

	static private class ImageLoadedCallback implements
			ItemLoadedCallback<ImageLoaded> {
		private long mMessageId;
		private final SimMessageListItem mListItem;

		public ImageLoadedCallback(SimMessageListItem listItem) {
			mListItem = listItem;
			mMessageId = listItem.getMessageItem().getMessageId();
		}

		public void reset(SimMessageListItem listItem) {
			mMessageId = listItem.getMessageItem().getMessageId();
		}

		public void onItemLoaded(ImageLoaded imageLoaded, Throwable exception) {
			if (DEBUG_DONT_LOAD_IMAGES) {
				return;
			}
			// Make sure we're still pointing to the same message. The list item
			// could have
			// been recycled.
			MessageItem msgItem = mListItem.mMessageItem;
			if (msgItem != null && msgItem.getMessageId() == mMessageId) {
				if (imageLoaded.mIsVideo) {
					mListItem.setVideoThumbnail(null, imageLoaded.mBitmap);
				} else {
					mListItem.setImage(null, imageLoaded.mBitmap);
				}
			}
		}
	}

	@Override
	public void startAudio() {
		// TODO Auto-generated method stub
	}

	@Override
	public void startVideo() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setAudio(Uri audio, String name, Map<String, ?> extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setImage(String name, Bitmap bitmap) {
		showMmsView(true);

		try {
			mImageView.setImageBitmap(bitmap);
			mImageView.setVisibility(VISIBLE);
		} catch (java.lang.OutOfMemoryError e) {
			Log.e(TAG, "setImage: out of memory: ", e);
		}
	}

	private void showMmsView(boolean visible) {
		if (mMmsView == null) {
			mMmsView = findViewById(R.id.mms_view);
			// if mMmsView is still null here, that mean the mms section hasn't
			// been inflated

			if (visible && mMmsView == null) {
				// inflate the mms view_stub
				View mmsStub = findViewById(R.id.mms_layout_view_stub);
				mmsStub.setVisibility(View.VISIBLE);
				mMmsView = findViewById(R.id.mms_view);// 010 have2change
			}
		}

		// original
		if (mMmsView != null) {
			if (mImageView == null) {
				mImageView = (ImageView) findViewById(R.id.image_view);
			}
			if (mSlideShowButton == null) {
				mSlideShowButton = (ImageButton) findViewById(R.id.play_slideshow_button);
				// mSlideShowButton.setImageResource(R.drawable.man);
			}
			mMmsView.setVisibility(visible ? View.VISIBLE : View.GONE);
			mImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	private void inflateDownloadControls() {
		if (mDownloadButton == null) {
			// inflate the download controls
			findViewById(R.id.mms_downloading_view_stub).setVisibility(VISIBLE);

			mDateDividerView = (View) findViewById(R.id.message_divider);
			mDateDividerTextView = (TextView) findViewById(R.id.date_divider_view);// 010date_divider_view
			// mMessageDivider = findViewById(R.id.message_divider);// 010
			mDownloadButton = (Button) findViewById(R.id.btn_download_msg);
			mDownloadingLabel = (TextView) findViewById(R.id.label_downloading);
		}
	}

	private LineHeightSpan mSpan = new LineHeightSpan() {
		@Override
		public void chooseHeight(CharSequence text, int start, int end,
				int spanstartv, int v, FontMetricsInt fm) {
			fm.ascent -= 10;
		}
	};

	TextAppearanceSpan mTextSmallSpan = new TextAppearanceSpan(mContext,
			android.R.style.TextAppearance_Small);

	ForegroundColorSpan mColorSpan = null; // set in ctor

	private CharSequence formatMessage(MessageItem msgItem, String contact,
			String body, int subId, String subject, Pattern highlight,
			String contentType) {
		SpannableStringBuilder buf = new SpannableStringBuilder();

		// if (MSimTelephonyManager.getDefault().isMultiSimEnabled()) {我注视的
		// if (MultiSimConfig.isMultiSimEnabled()) {
		// // Log.d(TAG, "formatMessage");//010
		// // buf.append((subId == 0) ? "卡一:" : "卡二:");
		// // buf.append("\n");
		//
		// }
		// 010注释掉上面的
		boolean hasSubject = !TextUtils.isEmpty(subject);
		SmileyParser parser = SmileyParser.getInstance();
		if (hasSubject) {
			CharSequence smilizedSubject = parser.addSmileySpans(subject);
			// Can't use the normal getString() with extra arguments for string
			// replacement
			// because it doesn't preserve the SpannableText returned by
			// addSmileySpans.
			// We have to manually replace the %s with our text.
			buf.append(TextUtils.replace(
					mContext.getResources().getString(R.string.inline_subject),
					new String[] { "%s" },
					new CharSequence[] { smilizedSubject }));
		}

		if (!TextUtils.isEmpty(body)) {
			// Converts html to spannable if ContentType is "text/html".
			if (contentType != null
					&& ContentType.TEXT_HTML.equals(contentType)) {
				buf.append("\n");
				buf.append(Html.fromHtml(body));
			} else {
				if (hasSubject) {
					buf.append(" - ");
				}
				buf.append(parser.addSmileySpans(body));
			}
		}

		if (highlight != null) {
			Matcher m = highlight.matcher(buf.toString());
			while (m.find()) {
				buf.setSpan(new StyleSpan(Typeface.BOLD), m.start(), m.end(), 0);// 010
																					// want2learn
			}
		}
		return buf;
	}

	private void drawPlaybackButton(MessageItem msgItem) {
		switch (msgItem.mAttachmentType) {
		case WorkingMessage.SLIDESHOW:
		case WorkingMessage.AUDIO:
		case WorkingMessage.VIDEO:
		case WorkingMessage.VCARD:
			// Show the 'Play' button and bind message info on it.
			mSlideShowButton.setTag(msgItem);
			// Set call-back for the 'Play' button.
			mSlideShowButton.setOnClickListener(this);
			mSlideShowButton.setVisibility(View.VISIBLE);// 010 triangle?
			setLongClickable(true);

			// When we show the mSlideShowButton, this list item's
			// onItemClickListener doesn't
			// get called. (It gets set in ComposeMessageActivity:
			// mMsgListView.setOnItemClickListener) Here we explicitly set the
			// item's
			// onClickListener. It allows the item to respond to embedded html
			// links and at the
			// same time, allows the slide show play button to work.
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onMessageListItemClick();
				}
			});
			break;
		default:
			mSlideShowButton.setVisibility(View.GONE);
			break;
		}
	}

	// OnClick Listener for the playback button
	@Override
	public void onClick(View v) {
		sendMessage(mMessageItem, MSG_LIST_PLAY);
	}

	private void sendMessage(MessageItem messageItem, int message) {
		if (mHandler != null) {
			Message msg = Message.obtain(mHandler, message);
			msg.obj = messageItem;
			msg.sendToTarget(); // See
								// ComposeMessageActivity.mMessageListItemHandler.handleMessage
		}
	}

	public void onMessageListItemClick() {
		// If the message is a failed one, clicking it should reload it in the
		// compose view,
		// regardless of whether it has links in it
		if (mMessageItem != null && mMessageItem.isOutgoingMessage()
				&& mMessageItem.isFailedMessage()) {

			// Assuming the current message is a failed one, reload it into the
			// compose view so
			// the user can resend it.
			sendMessage(mMessageItem, MSG_LIST_EDIT);
			return;
		}

		// Check for links. If none, do nothing; if 1, open it; if >1, ask user
		// to pick one
		// 010 attention here ： link！！
		final URLSpan[] spans = mBodyTextView.getUrls();

		if (spans.length == 0) {
			// sendMessage(mMessageItem, MSG_LIST_DETAILS); // show the message
			// details dialog
			// 010 deleted 2 cancel the respond of single click
		} else if (spans.length == 1) {
			spans[0].onClick(mBodyTextView);
		} else {
			ArrayAdapter<URLSpan> adapter = new ArrayAdapter<URLSpan>(mContext,
					android.R.layout.select_dialog_item, spans) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					try {
						URLSpan span = getItem(position);
						String url = span.getURL();
						Uri uri = Uri.parse(url);
						TextView tv = (TextView) v;
						Drawable d = mContext.getPackageManager()
								.getActivityIcon(
										new Intent(Intent.ACTION_VIEW, uri));
						if (d != null) {
							d.setBounds(0, 0, d.getIntrinsicHeight(),
									d.getIntrinsicHeight());
							tv.setCompoundDrawablePadding(10);
							tv.setCompoundDrawables(d, null, null, null);
						}
						final String telPrefix = "tel:";
						if (url.startsWith(telPrefix)) {
							url = PhoneNumberUtils.formatNumber(
									url.substring(telPrefix.length()),
									mDefaultCountryIso);
						}
						tv.setText(url);
					} catch (android.content.pm.PackageManager.NameNotFoundException ex) {
						// it's ok if we're unable to set the drawable for this
						// view - the user
						// can still use it
					}
					return v;
				}
			};

			AlertDialog.Builder b = new AlertDialog.Builder(mContext);

			DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(DialogInterface dialog, int which) {
					if (which >= 0) {
						spans[which].onClick(mBodyTextView);
					}
					dialog.dismiss();
				}
			};

			b.setTitle(R.string.select_link_title);
			b.setCancelable(true);
			b.setAdapter(adapter, click);

			b.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public final void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					});

			b.show();
		}
	}

	private void setOnClickListener(final MessageItem msgItem) {
		switch (msgItem.mAttachmentType) {
		case WorkingMessage.IMAGE:
		case WorkingMessage.VIDEO:
			mImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sendMessage(msgItem, MSG_LIST_PLAY);
				}
			});
			mImageView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return v.showContextMenu();

				}
			});
			break;

		default:
			mImageView.setOnClickListener(null);
			break;
		}
	}

	private void drawRightStatusIndicator(MessageItem msgItem) {
		Log.w(TAG, "drawRightStatusIndicator");
		// Locked icon
		if (msgItem.mLocked) {
			mLockedIndicator.setImageResource(R.drawable.ic_lock_message_sms);
			mLockedIndicator.setVisibility(View.VISIBLE);
		} else {
			mLockedIndicator.setVisibility(View.GONE);
		}

		// Delivery icon - we can show a failed icon for both sms and mms, but
		// for an actual
		// delivery, we only show the icon for sms. We don't have the
		// information here in mms to
		// know whether the message has been delivered. For mms,
		// msgItem.mDeliveryStatus set
		// to MessageItem.DeliveryStatus.RECEIVED simply means the setting
		// requesting a
		// delivery report was turned on when the message was sent. Yes, it's
		// confusing!
		if ((msgItem.isOutgoingMessage() && msgItem.isFailedMessage())
				|| msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.FAILED) {
			mDeliveredIndicator
					.setImageResource(R.drawable.ic_list_alert_sms_failed);
			mDeliveredIndicator.setVisibility(View.VISIBLE);

		} else if (msgItem.isSms()
				&& msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.RECEIVED) {
			mDeliveredIndicator
					.setImageResource(R.drawable.ic_sms_mms_delivered);
			mDeliveredIndicator.setVisibility(View.VISIBLE);
		} else {
			mDeliveredIndicator.setVisibility(View.GONE);
		}
		// 010learned

		// Message details icon - this icon is shown both for sms and mms
		// messages. For mms,
		// we show the icon if the read report or delivery report setting was
		// set when the
		// message was sent. Showing the icon tells the user there's more
		// information
		// by selecting the "View report" menu.
		if (msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.INFO
				|| msgItem.mReadReport
				|| (msgItem.isMms() && msgItem.mDeliveryStatus == MessageItem.DeliveryStatus.RECEIVED)) {
			mDetailsIndicator.setImageResource(R.drawable.ic_sms_mms_details);
			mDetailsIndicator.setVisibility(View.VISIBLE);
		} else {
			mDetailsIndicator.setVisibility(View.GONE);
		}

		// 010
		// 010卡标识

		if (msgItem.mSubscription == 0) {
			mSim1Indicator.setImageResource(R.drawable.sim1);
			mSim1Indicator.setVisibility(View.VISIBLE);
			mSim2Indicator.setVisibility(View.GONE);
		} else if (msgItem.mSubscription == 1) {
			mSim2Indicator.setImageResource(R.drawable.sim2);
			mSim2Indicator.setVisibility(View.VISIBLE);
			mSim1Indicator.setVisibility(View.GONE);
		}

		// 010time hide 4 debugging
		// mDividerImageView.setImageResource(R.drawable.date_divider);
		// mDividerImageView.setVisibility(View.VISIBLE);

		// 010 make divider 4 each day
		 try {
		Time now = new Time();
		now.setToNow();
		if ((mPosition >= ComposeMessageActivity.items.size())
				&& !ComposeMessageActivity.areSameDay(
						ComposeMessageActivity.items.get(mPosition - 1).date,
						now.toMillis(false), mContext)) {
			mDateDividerView.setVisibility(View.GONE);
			Log.e(TAG,"1 append; SameDay Visibility? : "+ComposeMessageActivity.areSameDay(
					ComposeMessageActivity.items.get(mPosition - 1).date,
					now.toMillis(false), mContext));
		} else if ((mPosition >= ComposeMessageActivity.items.size())
				&& ComposeMessageActivity.areSameDay(
						ComposeMessageActivity.items.get(mPosition - 1).date,
						now.toMillis(false), mContext)) {
			mDateDividerView.setVisibility(View.VISIBLE);
			Log.e(TAG,"2 append; SameDay Visibility? : "+ComposeMessageActivity.areSameDay(
					ComposeMessageActivity.items.get(mPosition - 1).date,
					now.toMillis(false), mContext));
		} else if ((mPosition < ComposeMessageActivity.items.size())
				&& ComposeMessageActivity.items.get(mPosition).visibility == false) {
			mDateDividerView.setVisibility(View.GONE);
			Log.e(TAG,"3 append");
		} else if (ComposeMessageActivity.items.get(mPosition).visibility == true) {
			mDateDividerView.setVisibility(View.VISIBLE);
			// 010TAG
			 Log.e(TAG,"4 append");
		}

		 }

		 catch (IndexOutOfBoundsException e) {
			 //010TAG
			 Log.e(TAG,"error append");
			 mDateDividerView.setVisibility(View.VISIBLE);
			}
		// // Time now = new Time();
		// // now.setToNow();
		// // if (ComposeMessageActivity.areSameDay(
		// // ComposeMessageActivity.items.get(mPosition - 1).date,
		// // now.toMillis(false), mContext)
		// // && ComposeMessageActivity.items.get(mPosition) == null) {
		// // // if (ComposeMessageActivity.items.get(mPosition - 1).date ==
		// // // now
		// // // .toMillis(false)
		// // // && ComposeMessageActivity.items.get(mPosition) == null) {
		// // mDateDividerView.setVisibility(View.GONE);
		// // } else if (!ComposeMessageActivity.areSameDay(
		// // ComposeMessageActivity.items.get(mPosition - 1).date,
		// // now.toMillis(false), mContext)
		// // && ComposeMessageActivity.items.get(mPosition) == null) {
		// // // else if (ComposeMessageActivity.items.get(mPosition - 1).date
		// // // != now
		// // // .toMillis(false)
		// // // && ComposeMessageActivity.items.get(mPosition) == null) {
		// // mDateDividerView.setVisibility(View.VISIBLE);
		// mDateDividerView.setVisibility(View.VISIBLE);
		//
		// // }
		// }

		// Time now = new Time();
		// now.setToNow();
		// Log.e(TAG, " visibility: " + ComposeMessageActivity.areSameDay(
		// ComposeMessageActivity.items.get(mPosition - 1).date,
		// now.toMillis(false), mContext));
		// 010above
	}

	@Override
	public void setImageRegionFit(String fit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setImageVisibility(boolean visible) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setText(String name, String text) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setTextVisibility(boolean visible) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setVideo(String name, Uri uri) {
	}

	@Override
	public void setVideoThumbnail(String name, Bitmap bitmap) {
		showMmsView(true);

		try {
			mImageView.setImageBitmap(bitmap);
			mImageView.setVisibility(VISIBLE);
		} catch (java.lang.OutOfMemoryError e) {
			Log.e(TAG, "setVideo: out of memory: ", e);
		}
	}

	@Override
	public void setVideoVisibility(boolean visible) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopAudio() {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopVideo() {
		// TODO Auto-generated method stub
	}

	@Override
	public void reset() {
	}

	@Override
	public void setVisibility(boolean visible) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pauseAudio() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseVideo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void seekAudio(int seekTo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void seekVideo(int seekTo) {
		// TODO Auto-generated method stub

	}

	/**
	 * Override dispatchDraw so that we can put our own background and border
	 * in. This is all complexity to support a shared border from one item to
	 * the next.
	 */
	@Override
	public void dispatchDraw(Canvas c) {
		super.dispatchDraw(c);

		// This custom border is causing our scrolling fps to drop from 60+ to
		// the mid 40's.
		// Commenting out for now until we come up with a new UI design that
		// doesn't require
		// the border.
		return;

		// View v = mMessageBlock;
		// if (v != null) {
		// Path path = null;
		// if (mAvatar.getPosition() == Divot.RIGHT_UPPER) {
		// if (mPathRight == null) {
		// float r = v.getWidth() - 1;
		// float b = v.getHeight();
		//
		// mPathRight = new Path();
		// mPathRight.moveTo(0, mAvatar.getCloseOffset());
		// mPathRight.lineTo(0, 0);
		// mPathRight.lineTo(r, 0);
		// mPathRight.lineTo(r, b);
		// mPathRight.lineTo(0, b);
		// mPathRight.lineTo(0, mAvatar.getFarOffset());
		// }
		// path = mPathRight;
		// } else if (mAvatar.getPosition() == Divot.LEFT_UPPER) {
		// if (mPathLeft == null) {
		// float r = v.getWidth() - 1;
		// float b = v.getHeight();
		//
		// mPathLeft = new Path();
		// mPathLeft.moveTo(r, mAvatar.getCloseOffset());
		// mPathLeft.lineTo(r, 0);
		// mPathLeft.lineTo(0, 0);
		// mPathLeft.lineTo(0, b);
		// mPathLeft.lineTo(r, b);
		// mPathLeft.lineTo(r, mAvatar.getFarOffset());
		// }
		// path = mPathLeft;
		// }
		// if (mPaint == null) {
		// mPaint = new Paint();
		// mPaint.setColor(0xffcccccc);
		// mPaint.setStrokeWidth(1F);
		// mPaint.setStyle(Paint.Style.STROKE);
		// mPaint.setColor(0xff00ff00); // turn on for debugging, draws lines in
		// green
		// }
		// c.translate(v.getX(), v.getY());
		// c.drawPath(path, mPaint);
		// }
	}

}
