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

import java.util.HashMap;

import edu.bupt.mms.LogTag;
import edu.bupt.mms.R;
import edu.bupt.mms.data.Contact;
import edu.bupt.mms.data.ContactList;
import edu.bupt.mms.data.Conversation;
import edu.bupt.mms.numberlocate.PhoneStatusRecevier;

import edu.bupt.mms.numberlocate.NumberLocateProvider.NumberRegion;
import edu.bupt.mms.util.SmileyParser;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This class manages the view for given conversation.
 */
public class ConversationListItem extends RelativeLayout implements
		Contact.UpdateListener, Checkable {
	private static final String TAG = "ConversationListItem";
	private static final boolean DEBUG = false;

	private TextView mSubjectView;
	private TextView mType;
	private TextView mFromView;
	private TextView mDateView;
	private View mAttachmentView;
	private View mErrorIndicator;
	private QuickContactBadge mAvatarView;
	static public ImageView mImageView;

	static private Drawable sDefaultContactImage;

	// For posting UI update Runnables from other threads:
	private Handler mHandler = new Handler();

	private Conversation mConversation;

	// String city;

	// HashMap<String, String> Cache = new HashMap<String, String>();

	public static final StyleSpan STYLE_BOLD = new StyleSpan(Typeface.BOLD);

	public ConversationListItem(Context context) {
		super(context);
	}

	public ConversationListItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (sDefaultContactImage == null) {
			sDefaultContactImage = context.getResources().getDrawable(
					R.drawable.ic_contact_picture);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mImageView = (ImageView) findViewById(R.id.choose);

		ConversationListItem.mImageView.setVisibility(8);
		// ConversationListItem.mImageView.setImageResource(R.drawable.choose);
		// Log.v("firstset","firstset");

		mFromView = (TextView) findViewById(R.id.from);
		mSubjectView = (TextView) findViewById(R.id.subject);
		mType = (TextView) findViewById(R.id.simType);
		mDateView = (TextView) findViewById(R.id.date);
		mAttachmentView = findViewById(R.id.attachment);
		mErrorIndicator = findViewById(R.id.error);
		mAvatarView = (QuickContactBadge) findViewById(R.id.avatar);
	}

	public Conversation getConversation() {
		return mConversation;
	}

	/**
	 * Only used for header binding.
	 */
	public void bind(String title, String explain) {
		mFromView.setText(title);
		mSubjectView.setText(explain);
	}

	private CharSequence formatMessage() {
		final int color = android.R.styleable.Theme_textColorSecondary;
		String from = mConversation.getRecipients().formatNames(", ");

		Log.v("formatMessage", "formatMessage: " + from);
		

		SpannableStringBuilder buf = new SpannableStringBuilder(from);
		try {
			if (mConversation.hasUnreadMessages()
					&& mConversation.getUnReadMessageCount(mConversation
							.getThreadId()) != 0) {
				if (mConversation.getUnReadMessageCount(mConversation
						.getThreadId()) != 0) {
					buf.setSpan(STYLE_BOLD, 0, buf.length(),
							Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

					buf.append("  ");// 010 make it on
					buf.append(mContext.getResources().getString(
							R.string.message_count_format,
							mConversation.getUnReadMessageCount(mConversation
									.getThreadId())));

					buf.append(" /");// 010未读"/"已读之间的分隔
				} else {
					mConversation.setHasUnreadMessages(false);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		
		if (mConversation.getMessageCount() >= 1) {
			int before = buf.length();

			buf.append(mContext.getResources().getString(
					R.string.message_count_format,
					mConversation.getMessageCount()));

			buf.setSpan(new ForegroundColorSpan(mContext.getResources()
					.getColor(R.color.message_count_color)), before, buf
					.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		if (mConversation.hasDraft()) {
			buf.append(mContext.getResources().getString(
					R.string.draft_separator));
			int before = buf.length();
			int size;
			buf.append(mContext.getResources().getString(R.string.has_draft));
			size = android.R.style.TextAppearance_Small;
			buf.setSpan(new TextAppearanceSpan(mContext, size, color), before,
					buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			buf.setSpan(new ForegroundColorSpan(mContext.getResources()
					.getColor(R.drawable.text_color_red)), before,
					buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}

		// Unread messages are shown in bold

		return buf;
	}

	private String startQuery(String number) {
		if (number.length() >= 11) {
			Log.v("ConversationList for test", "number: " + number);
			String formatNumber = PhoneStatusRecevier.formatNumber(number);
			Log.v("ConversationList for test", "formatnumber: " + formatNumber);
			String selection = null;
			String[] projection = null;
			Uri uri = NumberRegion.CONTENT_URI;
			if (formatNumber.length() == 7) {
				selection = NumberRegion.NUMBER + "=" + formatNumber;
				uri = NumberRegion.CONTENT_URI;
				projection = new String[] { NumberRegion.CITY };
			} else {
				selection = NumberRegion.AREACODE + "=" + "'" + formatNumber
						+ "'" + " OR " + NumberRegion.AREACODE + "=" + "'"
						+ formatNumber.substring(0, 3) + "'";
				uri = NumberRegion.CONTENT_URI;
				projection = new String[] { NumberRegion.CITY };
			}

			Cursor cursor = this.mContext.getContentResolver().query(uri,
					projection, selection, null, null);

			if (cursor != null && cursor.moveToNext()) {
				String city = cursor.getString(0);
				cursor.close();
				Log.v("ConversationList for test", "cursor!=0 city: " + city);
				return city;

			} else {
				Log.v("ConversationList for test", "cursor=0 city: ");

				return "";

			}

		} else {

			Log.v("ConversationList for test", "cursor=0 city: ");

			return "";

		}

	}

	private void updateAvatarView() {
		Drawable avatarDrawable;
		if (mConversation.getRecipients().size() == 1) {
			
			
			Contact contact = mConversation.getRecipients().get(0);
			
			//to be continued
			avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);

			if (contact.existsInDatabase()) {// 010查看联系人是否存在于通讯录
				mAvatarView.assignContactUri(contact.getUri());
			} else {
				mAvatarView.assignContactFromPhone(contact.getNumber(), true);
			}
		} else {
			// TODO get a multiple recipients asset (or do something else)
			avatarDrawable = sDefaultContactImage;
			mAvatarView.assignContactUri(null);
		}
		mAvatarView.setImageDrawable(avatarDrawable);
		mAvatarView.setVisibility(View.VISIBLE);
	}

	private void updateFromView() {
		mFromView.setText(formatMessage());
		updateAvatarView();

	}

	public void onUpdate(Contact updated) {
		if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
			Log.v(TAG, "onUpdate: " + this + " contact: " + updated);
		}
		mHandler.post(new Runnable() {
			public void run() {
				updateFromView();
			}
		});
	}

	public final void bind(Context context, final Conversation conversation) {
		// if (DEBUG) Log.v(TAG, "bind()");

		mConversation = conversation;

		updateBackground();

		LayoutParams attachmentLayout = (LayoutParams) mAttachmentView
				.getLayoutParams();
		boolean hasError = conversation.hasError();
		// When there's an error icon, the attachment icon is left of the error
		// icon.
		// When there is not an error icon, the attachment icon is left of the
		// date text.
		// As far as I know, there's no way to specify that relationship in xml.
		if (hasError) {
			attachmentLayout.addRule(RelativeLayout.LEFT_OF, R.id.error);
		} else {
			attachmentLayout.addRule(RelativeLayout.LEFT_OF, R.id.date);
		}

		boolean hasAttachment = conversation.hasAttachment();
		mAttachmentView.setVisibility(hasAttachment ? VISIBLE : GONE);

		// Date
		mDateView.setText(MessageUtils.formatTimeStampString(context,
				conversation.getDate()));

		// mConversation.ensureThreadId();
		if (mConversation != null) {
			if (conversation.getSimType(mConversation.getThreadId()) == 0)
				mType.setText(context.getString(R.string.card_one));

			else if (conversation.getSimType(mConversation.getThreadId()) == 1)
				mType.setText(context.getString(R.string.card_two));

			else if (conversation.getSimType(mConversation.getThreadId()) == 2)
				mType.setText("");

		} else {
			mType.setText("");
		}
		// From.
		mFromView.setText(formatMessage());

		// Register for updates in changes of any of the contacts in this
		// conversation.
		ContactList contacts = conversation.getRecipients();

		if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
			Log.v(TAG, "bind: contacts.addListeners " + this);
		}
		Contact.addListener(this);

		// Subject
		SmileyParser parser = SmileyParser.getInstance();
		mSubjectView.setText(parser.addSmileySpans(conversation.getSnippet()));
		LayoutParams subjectLayout = (LayoutParams) mSubjectView
				.getLayoutParams();
		// We have to make the subject left of whatever optional items are shown
		// on the right.
		subjectLayout.addRule(RelativeLayout.LEFT_OF,
				hasAttachment ? R.id.attachment : (hasError ? R.id.error
						: R.id.date));

		// Transmission error indicator.
		mErrorIndicator.setVisibility(hasError ? VISIBLE : GONE);

		updateAvatarView();
	}

	private void updateBackground() {
		int backgroundId;
		if (mConversation.isChecked()) {
			backgroundId = R.drawable.list_selected_holo_light;
		} else if (mConversation.hasUnreadMessages()) {
			backgroundId = R.drawable.conversation_item_background_unread;
		} else {
			backgroundId = R.drawable.conversation_item_background_read;
		}
		Drawable background = mContext.getResources().getDrawable(backgroundId);
		setBackground(background);
	}

	public final void unbind() {
		if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
			Log.v(TAG, "unbind: contacts.removeListeners " + this);
		}
		// Unregister contact update callbacks.
		Contact.removeListener(this);
	}

	public void setChecked(boolean checked) {
		mConversation.setIsChecked(checked);
		updateBackground();
	}

	public void updatechoose() {
		mImageView.setVisibility(8);
		updateAvatarView();
		Log.v("updatechoose", "updatechoose");
	}

	public boolean isChecked() {
		return mConversation.isChecked();
	}

	public void toggle() {
		mConversation.setIsChecked(!mConversation.isChecked());
	}
}
