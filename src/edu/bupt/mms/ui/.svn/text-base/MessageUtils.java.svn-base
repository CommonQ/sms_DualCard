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

import edu.bupt.mms.MmsApp;
import edu.bupt.mms.MmsConfig;
import edu.bupt.mms.R;
import edu.bupt.mms.LogTag;
import edu.bupt.mms.TempFileProvider;
import edu.bupt.mms.data.WorkingMessage;
import edu.bupt.mms.model.MediaModel;
import edu.bupt.mms.model.SlideModel;
import edu.bupt.mms.model.SlideshowModel;
import edu.bupt.mms.transaction.MmsMessageSender;
import edu.bupt.mms.util.AddressUtils;
import com.google.android.mms.ContentType;
import com.google.android.mms.MmsException;
import com.google.android.mms.pdu.CharacterSets;
import com.google.android.mms.pdu.EncodedStringValue;
import com.google.android.mms.pdu.MultimediaMessagePdu;
import com.google.android.mms.pdu.NotificationInd;
import com.google.android.mms.pdu.PduBody;
import com.google.android.mms.pdu.PduHeaders;
import com.google.android.mms.pdu.PduPart;
import com.google.android.mms.pdu.PduPersister;
import com.google.android.mms.pdu.RetrieveConf;
import com.google.android.mms.pdu.SendReq;
import android.database.sqlite.SqliteWrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.CamcorderProfile;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Sms;
import android.telephony.MSimTelephonyManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An utility class for managing messages.
 */
public class MessageUtils {
	interface ResizeImageResultCallback {
		void onResizeResult(PduPart part, boolean append);
	}

	private static final String TAG = LogTag.TAG;
	private static String sLocalNumber;

	public static boolean isShowBJTime = false;

	// Cache of both groups of space-separated ids to their full
	// comma-separated display names, as well as individual ids to
	// display names.
	// TODO: is it possible for canonical address ID keys to be
	// re-used? SQLite does reuse IDs on NULL id_ insert, but does
	// anything ever delete from the mmssms.db canonical_addresses
	// table? Nothing that I could find.
	private static final Map<String, String> sRecipientAddress = new ConcurrentHashMap<String, String>(
			20 /* initial capacity */);

	/**
	 * MMS address parsing data structures
	 */
	// allowable phone number separators
	private static final char[] NUMERIC_CHARS_SUGAR = { '-', '.', ',', '(',
			')', ' ', '/', '\\', '*', '#', '+' };

	private static HashMap numericSugarMap = new HashMap(
			NUMERIC_CHARS_SUGAR.length);

	static {
		for (int i = 0; i < NUMERIC_CHARS_SUGAR.length; i++) {
			numericSugarMap.put(NUMERIC_CHARS_SUGAR[i], NUMERIC_CHARS_SUGAR[i]);
		}
	}

	private MessageUtils() {
		// Forbidden being instantiated.
	}

	public static String getMessageDetails(Context context, Cursor cursor,
			int size) {
		if (cursor == null) {
			return null;
		}

		if ("mms".equals(cursor.getString(MessageListAdapter.COLUMN_MSG_TYPE))) {// 010
																					// mms
																					// or
																					// sms
			int type = cursor
					.getInt(MessageListAdapter.COLUMN_MMS_MESSAGE_TYPE);
			switch (type) {
			case PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND:
				return getNotificationIndDetails(context, cursor);
			case PduHeaders.MESSAGE_TYPE_RETRIEVE_CONF:
			case PduHeaders.MESSAGE_TYPE_SEND_REQ:
				return getMultimediaMessageDetails(context, cursor, size);
			default:
				Log.w(TAG, "No details could be retrieved.");
				return "";
			}
		} else {
			return getTextMessageDetails(context, cursor);
		}
	}

	private static String getNotificationIndDetails(Context context,
			Cursor cursor) {
		StringBuilder details = new StringBuilder();
		Resources res = context.getResources();

		long id = cursor.getLong(MessageListAdapter.COLUMN_ID);
		Uri uri = ContentUris.withAppendedId(Mms.CONTENT_URI, id);
		NotificationInd nInd;

		try {
			nInd = (NotificationInd) PduPersister.getPduPersister(context)
					.load(uri);
		} catch (MmsException e) {
			Log.e(TAG, "Failed to load the message: " + uri, e);
			return context.getResources()
					.getString(R.string.cannot_get_details);
		}

		// Message Type: Mms Notification.
		details.append(res.getString(R.string.message_type_label));
		details.append(res.getString(R.string.multimedia_notification));

		// From: ***
		String from = extractEncStr(context, nInd.getFrom());
		details.append('\n');
		details.append(res.getString(R.string.from_label));
		details.append(!TextUtils.isEmpty(from) ? from : res
				.getString(R.string.hidden_sender_address));

		// Date: ***
		details.append('\n');
		// zaizhe
		if (!MSimTelephonyManager.getDefault().isNetworkRoaming(0)) {
			details.append(res.getString(
					R.string.expire_on,
					MessageUtils.formatTimeStampString(context,
							nInd.getExpiry() * 1000L, true)));
		} else {

			SharedPreferences localSharedPreferences2 = PreferenceManager
					.getDefaultSharedPreferences(context);

//			int which_time = Settings.System.getInt(context
//					.getContentResolver(), Settings.System.AUTO_TIME_ZONE,
//					localSharedPreferences2.getInt(
//							Settings.System.AUTO_TIME_ZONE, 1));
			
			int which_time=localSharedPreferences2.getInt(
					Settings.System.AUTO_TIME_ZONE, 1);

			// int which_time=
			// localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE,
			// 1);

			String location_time = null;
			if (which_time == 0) {
				location_time = res.getString(R.string.beijing_time);;

			} else if (which_time == 1) {
				location_time = context.getString(R.string.local_time);;
			}
			details.append(location_time);
			details.append(res.getString(
					R.string.expire_on,
					MessageUtils.formatTimeStampString(context,
							nInd.getExpiry() * 1000L, true)));

		}
		// Subject: ***
		details.append('\n');
		details.append(res.getString(R.string.subject_label));

		EncodedStringValue subject = nInd.getSubject();
		if (subject != null) {
			details.append(subject.getString());
		}

		// Message class: Personal/Advertisement/Infomational/Auto
		details.append('\n');
		details.append(res.getString(R.string.message_class_label));
		details.append(new String(nInd.getMessageClass()));

		// Message size: *** KB
		details.append('\n');
		details.append(res.getString(R.string.message_size_label));//��Ϣ��С
		details.append(String.valueOf((nInd.getMessageSize() + 1023) / 1024));
		details.append(context.getString(R.string.kilobyte));

		return details.toString();
	}

	private static String getMultimediaMessageDetails(Context context,
			Cursor cursor, int size) {
		int type = cursor.getInt(MessageListAdapter.COLUMN_MMS_MESSAGE_TYPE);
		if (type == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) {
			return getNotificationIndDetails(context, cursor);
		}

		StringBuilder details = new StringBuilder();
		Resources res = context.getResources();

		long id = cursor.getLong(MessageListAdapter.COLUMN_ID);
		Uri uri = ContentUris.withAppendedId(Mms.CONTENT_URI, id);
		MultimediaMessagePdu msg;

		try {
			msg = (MultimediaMessagePdu) PduPersister.getPduPersister(context)
					.load(uri);
		} catch (MmsException e) {
			Log.e(TAG, "Failed to load the message: " + uri, e);
			return context.getResources()
					.getString(R.string.cannot_get_details);
		}

		// Message Type: Text message.010:NOT AT ALL! MMS!
		details.append(res.getString(R.string.message_type_label));
		details.append(res.getString(R.string.multimedia_message));// obviously
																	// mms!!!

		if (msg instanceof RetrieveConf) {
			// From: ***
			String from = extractEncStr(context, ((RetrieveConf) msg).getFrom());
			details.append('\n');
			details.append(res.getString(R.string.from_label));
			details.append(!TextUtils.isEmpty(from) ? from : res
					.getString(R.string.hidden_sender_address));
		}

		// To: ***
		details.append('\n');
		details.append(res.getString(R.string.to_address_label));
		EncodedStringValue[] to = msg.getTo();
		if (to != null) {
			details.append(EncodedStringValue.concat(to));
		} else {
			Log.w(TAG, "recipient list is empty!");
		}

		// Bcc: ***
		if (msg instanceof SendReq) {
			EncodedStringValue[] values = ((SendReq) msg).getBcc();
			if ((values != null) && (values.length > 0)) {
				details.append('\n');
				details.append(res.getString(R.string.bcc_label));
				details.append(EncodedStringValue.concat(values));
			}
		}

		// Date: ***
		details.append('\n');
		int msgBox = cursor.getInt(MessageListAdapter.COLUMN_MMS_MESSAGE_BOX);
		if (msgBox == Mms.MESSAGE_BOX_DRAFTS) {
			details.append(res.getString(R.string.saved_label));
		} else if (msgBox == Mms.MESSAGE_BOX_INBOX) {
			details.append(res.getString(R.string.received_label));
		} else {
			details.append(res.getString(R.string.sent_label));
		}

		// zaizhe
		if (MSimTelephonyManager.getDefault().isNetworkRoaming(0)) {

			SharedPreferences localSharedPreferences2 = PreferenceManager
					.getDefaultSharedPreferences(context);
//
//			int which_time = Settings.System.getInt(context
//					.getContentResolver(), Settings.System.AUTO_TIME_ZONE,
//					localSharedPreferences2.getInt(
//							Settings.System.AUTO_TIME_ZONE, 1));
			
			int which_time=localSharedPreferences2.getInt(
					Settings.System.AUTO_TIME_ZONE, 1);
			

			// int which_time=
			// localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE,
			// 1);

			String location_time = null;
			if (which_time == 0) {
				location_time = res.getString(R.string.beijing_time);

			} else if (which_time == 1) {
				location_time = res.getString(R.string.local_time);
			}
			details.append(location_time);

		}
		details.append(MessageUtils.formatTimeStampString(context,
				msg.getDate() * 1000L, true));// might be the mms show time
												// details010
		// details.append(MessageUtils.dateTimeStampString(context,
		// msg.getDate() * 1000L, true));//010 added(need2B somewhere else)
		// details.append(MessageUtils.justTimeStampString(context,
		// msg.getDate() * 1000L, true));//010 added

		// Subject: ***
		details.append('\n');
		details.append(res.getString(R.string.subject_label));

		EncodedStringValue subject = msg.getSubject();
		if (subject != null) {
			String subStr = subject.getString();
			// Message size should include size of subject.
			size += subStr.length();
			details.append(subStr);
		}

		// Priority: High/Normal/Low
		details.append('\n');
		details.append(res.getString(R.string.priority_label));
		details.append(getPriorityDescription(context, msg.getPriority()));

		// Message size: *** KB
		details.append('\n');
		details.append(res.getString(R.string.message_size_label));//��Ϣ��С
		details.append((size - 1) / 1000 + 1);
		details.append(" KB");

		return details.toString();
	}

	private static String getTextMessageDetails(Context context, Cursor cursor) {
		Log.d(TAG, "getTextMessageDetails");

		StringBuilder details = new StringBuilder();
		Resources res = context.getResources();

		// Message Type: Text message.
		details.append(res.getString(R.string.message_type_label));
		details.append(res.getString(R.string.text_message));

		// Address: ***
		details.append('\n');
		int smsType = cursor.getInt(MessageListAdapter.COLUMN_SMS_TYPE);
		if (Sms.isOutgoingFolder(smsType)) {
			details.append(res.getString(R.string.to_address_label));
		} else {
			details.append(res.getString(R.string.from_label));
		}
		details.append(cursor.getString(MessageListAdapter.COLUMN_SMS_ADDRESS));

		// Sent: ***
		if (smsType == Sms.MESSAGE_TYPE_INBOX) {
			long date_sent = cursor
					.getLong(MessageListAdapter.COLUMN_SMS_DATE_SENT);
			if (date_sent > 0) {
				details.append('\n');

				// zaizhe ?��???????��??????????????��?????
				if (!MSimTelephonyManager.getDefault().isNetworkRoaming(0)) {
					details.append(res.getString(R.string.sent_label));
					details.append(MessageUtils.formatTimeStampString(context,
							date_sent, true));
				} else {

					SharedPreferences localSharedPreferences2 = PreferenceManager
							.getDefaultSharedPreferences(context);
//
//					int which_time = Settings.System.getInt(context
//							.getContentResolver(),
//							Settings.System.AUTO_TIME_ZONE,
//							localSharedPreferences2.getInt(
//									Settings.System.AUTO_TIME_ZONE, 1));
					
					int which_time=localSharedPreferences2.getInt(
							Settings.System.AUTO_TIME_ZONE, 1);

					// int which_time=
					// localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE,
					// 1);

					String location_time = null;
					if (which_time == 0) {
						location_time = res.getString(R.string.beijing_time);

					} else if (which_time == 1) {
						location_time =  res.getString(R.string.local_time);
					}

					details.append(res.getString(R.string.sent_label));
					details.append(location_time);
					details.append(MessageUtils.formatTimeStampString(context,
							date_sent, true));

				}
			}
		}

		// Received: ***
		details.append('\n');
		if (smsType == Sms.MESSAGE_TYPE_DRAFT) {
			details.append(res.getString(R.string.saved_label));
		} else if (smsType == Sms.MESSAGE_TYPE_INBOX) {
			details.append(res.getString(R.string.received_label));
		} else {
			details.append(res.getString(R.string.sent_label));
		}

		long date = cursor.getLong(MessageListAdapter.COLUMN_SMS_DATE);

		if (MSimTelephonyManager.getDefault().isNetworkRoaming(0)) {
			SharedPreferences localSharedPreferences2 = PreferenceManager
					.getDefaultSharedPreferences(context);

//			int which_time = Settings.System.getInt(context
//					.getContentResolver(), Settings.System.AUTO_TIME_ZONE,
//					localSharedPreferences2.getInt(
//							Settings.System.AUTO_TIME_ZONE, 1));
			
			int which_time=localSharedPreferences2.getInt(
					Settings.System.AUTO_TIME_ZONE, 1);

			// int which_time=
			// localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE,
			// 1);

			String location_time = null;
			if (which_time == 0) {
				location_time = res.getString(R.string.beijing_time);

			} else if (which_time == 1) {
				location_time = res.getString(R.string.local_time);
			}

			details.append(location_time);

		}
		details.append(MessageUtils.formatTimeStampString(context, date, true));

		// Delivered: ***
		if (smsType == Sms.MESSAGE_TYPE_SENT) {
			// For sent messages with delivery reports, we stick the delivery
			// time in the
			// date_sent column (see MessageStatusReceiver).
			long dateDelivered = cursor
					.getLong(MessageListAdapter.COLUMN_SMS_DATE_SENT);
			if (dateDelivered > 0) {
				details.append('\n');
				details.append(res.getString(R.string.delivered_label));

				if (MSimTelephonyManager.getDefault().isNetworkRoaming(0)) {
					SharedPreferences localSharedPreferences2 = PreferenceManager
							.getDefaultSharedPreferences(context);

//					int which_time = Settings.System.getInt(context
//							.getContentResolver(),
//							Settings.System.AUTO_TIME_ZONE,
//							localSharedPreferences2.getInt(
//									Settings.System.AUTO_TIME_ZONE, 1));
//					

					int which_time=localSharedPreferences2.getInt(
							Settings.System.AUTO_TIME_ZONE, 1);

					// int which_time=
					// localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE,
					// 1);

					String location_time = null;
					if (which_time == 0) {
						location_time = res.getString(R.string.beijing_time);

					} else if (which_time == 1) {
						location_time = res.getString(R.string.local_time);
					}

					details.append(location_time);
					

				}

				details.append(MessageUtils.formatTimeStampString(context,
						dateDelivered, true));
			}
		}

		// Error code: ***
		int errorCode = cursor.getInt(MessageListAdapter.COLUMN_SMS_ERROR_CODE);
		if (errorCode != 0) {
			details.append('\n')
					.append(res.getString(R.string.error_code_label))
					.append(errorCode);
		}

		return details.toString();
	}

	static private String getPriorityDescription(Context context,
			int PriorityValue) {
		Resources res = context.getResources();
		switch (PriorityValue) {
		case PduHeaders.PRIORITY_HIGH:
			return res.getString(R.string.priority_high);
		case PduHeaders.PRIORITY_LOW:
			return res.getString(R.string.priority_low);
		case PduHeaders.PRIORITY_NORMAL:
		default:
			return res.getString(R.string.priority_normal);
		}
	}

	public static int getAttachmentType(SlideshowModel model) {
		if (model == null) {
			return MessageItem.ATTACHMENT_TYPE_NOT_LOADED;
		}

		int numberOfSlides = model.size();
		if (numberOfSlides > 1) {
			return WorkingMessage.SLIDESHOW;
		} else if (numberOfSlides == 1) {
			// Only one slide in the slide-show.
			SlideModel slide = model.get(0);
			if (slide.hasVideo()) {
				return WorkingMessage.VIDEO;
			}

			if (slide.hasAudio() && slide.hasImage()) {
				return WorkingMessage.SLIDESHOW;
			}

			if (slide.hasAudio()) {
				return WorkingMessage.AUDIO;
			}

			if (slide.hasImage()) {
				return WorkingMessage.IMAGE;
			}
			// zaizhe
			if (slide.hasVcard()) {
				return WorkingMessage.VCARD;
			}

			if (slide.hasText()) {
				return WorkingMessage.TEXT;
			}
		}

		return MessageItem.ATTACHMENT_TYPE_NOT_LOADED;
	}

	// original
	public static String formatTimeStampString(Context context, long when) {
		return formatTimeStampString(context, when, false);
	}

	// original
	public static String formatTimeStampString(Context context, long when,
			boolean fullFormat) {
		Time then = new Time();
		then.set(when);
		Time now = new Time();
		now.setToNow();

		// Basic settings for formatDateTime() we want for all cases.
		int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;

		
		// If the message is from a different year, show the date and year.
		if (then.year != now.year) {
			format_flags |= DateUtils.FORMAT_SHOW_YEAR
					| DateUtils.FORMAT_SHOW_DATE;
		} else if (then.yearDay != now.yearDay) {
			// If it is from a different day than today, show only the date.
			format_flags |= DateUtils.FORMAT_SHOW_DATE;

			// TimeZone localZone =TimeZone.
			
		} else {
			// Otherwise, if the message is from today, show the time.
			format_flags |= DateUtils.FORMAT_SHOW_TIME;
			
		}

		// If the caller has asked for full details, make sure to show the date
		// and time no matter what we've determined above (but still make
		// showing
		// the year only happen if it is a different year from today).
		if (fullFormat) {
			format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
		}

		if (isShowBJTime) {
			Formatter format = new Formatter();

			Formatter format2 = DateUtils.formatDateRange(context, format,
					when, when, format_flags, "Asia/Shanghai");

			// return DateUtils.formatDateTime(context, when, format_flags);//
			// original

			return format2.toString();

		} else {

			return DateUtils.formatDateTime(context, when, format_flags);// original

		}
	}
	/**
	 * 北邮ANT实验室
	 * 010
	 * 
	 * 将会话界面下不同日期的气泡间加上Divider
	 * 此处获取日期
	 * 
	 * 	 * 
	 * */

	// 010date:
	public static String dateTimeStampString(Context context, long when) {
		return dateTimeStampString(context, when, false);
	}

	public static String dateTimeStampString(Context context, long when,
			boolean fullFormat) {

		int date_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;
		//先将日期定义为整形，并获取包含hour在内的绝对时间

		date_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
		// 010去掉时间，只获取年份与日期
				
		if (isShowBJTime){
		Formatter format = new Formatter();

		Formatter format2 = DateUtils.formatDateRange(context, format, when,
				when, date_flags, "Asia/Shanghai");

		// return DateUtils.formatDateTime(context, when, date_flags);//
		// original

		return format2.toString();
		}else{
			return DateUtils.formatDateTime(context, when, date_flags);
			
		}
	}
	/**
	 * 北邮ANT实验室
	 * 010
	 * 
	 * 将会话界面下每个气泡内加上发送时间
	 * 此处获取时间
	 * 
	 * 	 * 
	 * */
	// 010time:
	public static String justTimeStampString(Context context, long when) {
		return justTimeStampString(context, when, false);
	}

	public static String justTimeStampString(Context context, long when,
			boolean fullFormat) {

		int time_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;
		//先将时间定义为整形，并获取包含年份月日在内的绝对时间

		time_flags |= DateUtils.FORMAT_SHOW_TIME;
		// 010只获取时间

		if (isShowBJTime){
		Formatter format = new Formatter();

		Formatter format2 = DateUtils.formatDateRange(context, format, when,
				when, time_flags, "Asia/Shanghai");

		// return DateUtils.formatDateTime(context, when, time_flags);//
		// original

		return format2.toString();
		}else{
			
			 return DateUtils.formatDateTime(context, when, time_flags);
		}
		}

	// 010above

	public static void selectAudio(Activity activity, int requestCode) {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
				activity.getString(R.string.select_audio));
		activity.startActivityForResult(intent, requestCode);
	}

	public static void recordSound(Activity activity, int requestCode,
			long sizeLimit) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(ContentType.AUDIO_AMR);
		intent.setClassName("com.android.soundrecorder",
				"com.android.soundrecorder.SoundRecorder");
		intent.putExtra(
				android.provider.MediaStore.Audio.Media.EXTRA_MAX_BYTES,
				sizeLimit);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void recordVideo(Activity activity, int requestCode,
			long sizeLimit) {
		// The video recorder can sometimes return a file that's larger than the
		// max we
		// say we can handle. Try to handle that overshoot by specifying an 85%
		// limit.
		sizeLimit *= .85F;

		int durationLimit = getVideoCaptureDurationLimit();

		if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
			log("recordVideo: durationLimit: " + durationLimit + " sizeLimit: "
					+ sizeLimit);
		}

		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		intent.putExtra("android.intent.extra.sizeLimit", sizeLimit);
		intent.putExtra("android.intent.extra.durationLimit", durationLimit);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				TempFileProvider.SCRAP_CONTENT_URI);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void capturePicture(Activity activity, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				TempFileProvider.SCRAP_CONTENT_URI);
		activity.startActivityForResult(intent, requestCode);
	}

	private static int getVideoCaptureDurationLimit() {
		CamcorderProfile camcorder = CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW);
		return camcorder == null ? 0 : camcorder.duration;
	}

	public static void selectVideo(Context context, int requestCode) {
		selectMediaByType(context, requestCode, ContentType.VIDEO_UNSPECIFIED,
				true);
	}

	public static void selectImage(Context context, int requestCode) {
		selectMediaByType(context, requestCode, ContentType.IMAGE_UNSPECIFIED,
				false);
	}

	private static void selectMediaByType(Context context, int requestCode,
			String contentType, boolean localFilesOnly) {
		if (context instanceof Activity) {

			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);

			innerIntent.setType(contentType);
			if (localFilesOnly) {
				innerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			}

			Intent wrapperIntent = Intent.createChooser(innerIntent, null);

			((Activity) context).startActivityForResult(wrapperIntent,
					requestCode);
		}
	}

	private static File getUniqueDestination(String base, String extension) {
		File file = new File(base + "." + extension);

		for (int i = 2; file.exists(); i++) {
			file = new File(base + "_" + i + "." + extension);
		}
		return file;
	}

	private static String copyTempVcardPart(Context context, Uri uri,
			String name) {
		InputStream input = null;
		FileOutputStream fout = null;

		String absolutePath = null;

		try {
			input = context.getContentResolver().openInputStream(uri);
			if (input instanceof FileInputStream) {
				FileInputStream fin = (FileInputStream) input;

				String fileName = name;

				Log.v(TAG, "copypart???????????????? uri?? " + uri + " fileName: "
						+ fileName);
				File originalFile = new File(fileName);
				fileName = originalFile.getName(); // Strip the full path of
													// where the "part" is
													// stored down to just the
													// leaf filename.

				// Depending on the location, there may be an
				// extension already on the name or not. If we've got audio, put
				// the attachment
				// in the Ringtones directory.
				String dir = Environment.getExternalStorageDirectory() + "/"
						+ Environment.DIRECTORY_DOWNLOADS + "/";
				String extension;

				File file = getUniqueDestination(dir + fileName, "vcf");

				// make sure the path is valid and directories created for this
				// file.
				File parentFile = file.getParentFile();
				if (!parentFile.exists() && !parentFile.mkdirs()) {
					Log.e(TAG,
							"[MMS] copyPart: mkdirs for "
									+ parentFile.getPath() + " failed!");
					return null;
				}

				fout = new FileOutputStream(file);

				byte[] buffer = new byte[8000];
				int size = 0;
				while ((size = fin.read(buffer)) != -1) {
					fout.write(buffer, 0, size);
				}

				absolutePath = file.getAbsolutePath();

				Log.v(TAG, "absolutePath?? " + absolutePath);

				// Notify other applications listening to scanner events
				// that a media file has been added to the sd card
				// sendBroadcast(new
				// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				// Uri.fromFile(file)));
			}
		} catch (IOException e) {
			// Ignore
			Log.e(TAG, "IOException caught while opening or reading stream", e);
			return null;
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (IOException e) {
					// Ignore
					Log.e(TAG, "IOException caught while closing stream", e);
					return null;
				}
			}
			if (null != fout) {
				try {
					fout.close();
				} catch (IOException e) {
					// Ignore
					Log.e(TAG, "IOException caught while closing stream", e);
					return null;
				}
			}
		}
		return absolutePath;

	}

	public static void viewSimpleSlideshow(Context context,
			SlideshowModel slideshow) {
		if (!slideshow.isSimple() && !slideshow.get(0).hasVcard()) {
			throw new IllegalArgumentException(
					"viewSimpleSlideshow() called on a non-simple slideshow");
		}
		SlideModel slide = slideshow.get(0);
		MediaModel mm = null;
		if (slide.hasImage()) {
			mm = slide.getImage();
		} else if (slide.hasVideo()) {
			mm = slide.getVideo();
		}
		// zaizhe
		else if (slide.hasVcard()) {
			Log.v(TAG, "xianshile ??????!!!");
			mm = slide.getVcard();

			Uri hello = mm.getUri();

			Log.v(TAG, "vcard?? uri??????: " + hello);

			String vcardPath = copyTempVcardPart(context, hello, "temp");

			Log.v(TAG, "vcard??vcardPath??????: " + vcardPath);

			try {
				Intent i = new Intent("edu.bupt.contact.vcard.preview.PREVIEW");
				i.putExtra("file", vcardPath);
				context.startActivity(i);
			} catch (ActivityNotFoundException e) {
				Log.e(TAG, e.toString());
			}

			return;

		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.putExtra("SingleItemOnly", true); // So we don't see
													// "surrounding" images in
													// Gallery

		String contentType;
		contentType = mm.getContentType();
		intent.setDataAndType(mm.getUri(), contentType);
		context.startActivity(intent);
	}

	public static void showErrorDialog(Activity activity, String title,
			String message) {
		if (activity.isFinishing()) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setIcon(R.drawable.ic_sms_mms_not_delivered);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	/**
	 * The quality parameter which is used to compress JPEG images.
	 */
	public static final int IMAGE_COMPRESSION_QUALITY = 95;
	/**
	 * The minimum quality parameter which is used to compress JPEG images.
	 */
	public static final int MINIMUM_IMAGE_COMPRESSION_QUALITY = 50;

	/**
	 * Message overhead that reduces the maximum image byte size. 5000 is a
	 * realistic overhead number that allows for user to also include a small
	 * MIDI file or a couple pages of text along with the picture.
	 */
	public static final int MESSAGE_OVERHEAD = 5000;

	public static void resizeImageAsync(final Context context,
			final Uri imageUri, final Handler handler,
			final ResizeImageResultCallback cb, final boolean append) {

		// Show a progress toast if the resize hasn't finished
		// within one second.
		// Stash the runnable for showing it away so we can cancel
		// it later if the resize completes ahead of the deadline.
		final Runnable showProgress = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, R.string.compressing,
						Toast.LENGTH_SHORT).show();
			}
		};
		// Schedule it for one second from now.
		handler.postDelayed(showProgress, 1000);

		new Thread(new Runnable() {
			@Override
			public void run() {
				final PduPart part;
				try {
					UriImage image = new UriImage(context, imageUri);
					int widthLimit = MmsConfig.getMaxImageWidth();
					int heightLimit = MmsConfig.getMaxImageHeight();
					// In mms_config.xml, the max width has always been declared
					// larger than the max
					// height. Swap the width and height limits if necessary so
					// we scale the picture
					// as little as possible.
					if (image.getHeight() > image.getWidth()) {
						int temp = widthLimit;
						widthLimit = heightLimit;
						heightLimit = temp;
					}

					part = image.getResizedImageAsPart(widthLimit, heightLimit,
							MmsConfig.getMaxMessageSize() - MESSAGE_OVERHEAD);
				} finally {
					// Cancel pending show of the progress toast if necessary.
					handler.removeCallbacks(showProgress);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						cb.onResizeResult(part, append);
					}
				});
			}
		}, "MessageUtils.resizeImageAsync").start();
	}

	public static void showDiscardDraftConfirmDialog(Context context,
			OnClickListener listener) {
		new AlertDialog.Builder(context)
				.setMessage(R.string.discard_message_reason)
				.setPositiveButton(R.string.yes, listener)
				.setNegativeButton(R.string.no, null).show();
	}

	public static String getLocalNumber() {
		if (null == sLocalNumber) {
			sLocalNumber = MmsApp.getApplication().getTelephonyManager()
					.getLine1Number();
		}
		return sLocalNumber;
	}

	public static boolean isLocalNumber(String number) {
		if (number == null) {
			return false;
		}

		// we don't use Mms.isEmailAddress() because it is too strict for
		// comparing addresses like
		// "foo+caf_=6505551212=tmomail.net@gmail.com", which is the 'from'
		// address from a forwarded email
		// message from Gmail. We don't want to treat
		// "foo+caf_=6505551212=tmomail.net@gmail.com" and
		// "6505551212" to be the same.
		if (number.indexOf('@') >= 0) {
			return false;
		}

		return PhoneNumberUtils.compare(number, getLocalNumber());
	}

	public static void handleReadReport(final Context context,
			final Collection<Long> threadIds, final int status,
			final Runnable callback) {
		StringBuilder selectionBuilder = new StringBuilder(Mms.MESSAGE_TYPE
				+ " = " + PduHeaders.MESSAGE_TYPE_RETRIEVE_CONF + " AND "
				+ Mms.READ + " = 0" + " AND " + Mms.READ_REPORT + " = "
				+ PduHeaders.VALUE_YES);

		String[] selectionArgs = null;
		if (threadIds != null) {
			String threadIdSelection = null;
			StringBuilder buf = new StringBuilder();
			selectionArgs = new String[threadIds.size()];
			int i = 0;

			for (long threadId : threadIds) {
				if (i > 0) {
					buf.append(" OR ");
				}
				buf.append(Mms.THREAD_ID).append("=?");
				selectionArgs[i++] = Long.toString(threadId);
			}
			threadIdSelection = buf.toString();

			selectionBuilder.append(" AND (" + threadIdSelection + ")");
		}

		final Cursor c = SqliteWrapper.query(context,
				context.getContentResolver(), Mms.Inbox.CONTENT_URI,
				new String[] { Mms._ID, Mms.MESSAGE_ID },
				selectionBuilder.toString(), selectionArgs, null);

		if (c == null) {
			return;
		}

		final Map<String, String> map = new HashMap<String, String>();
		try {
			if (c.getCount() == 0) {
				if (callback != null) {
					callback.run();
				}
				return;
			}

			while (c.moveToNext()) {
				Uri uri = ContentUris.withAppendedId(Mms.CONTENT_URI,
						c.getLong(0));
				map.put(c.getString(1), AddressUtils.getFrom(context, uri));
			}
		} finally {
			c.close();
		}

		OnClickListener positiveListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (final Map.Entry<String, String> entry : map.entrySet()) {
					MmsMessageSender.sendReadRec(context, entry.getValue(),
							entry.getKey(), status);
				}

				if (callback != null) {
					callback.run();
				}
				dialog.dismiss();
			}
		};

		OnClickListener negativeListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.run();
				}
				dialog.dismiss();
			}
		};

		OnCancelListener cancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (callback != null) {
					callback.run();
				}
				dialog.dismiss();
			}
		};

		confirmReadReportDialog(context, positiveListener, negativeListener,
				cancelListener);
	}

	private static void confirmReadReportDialog(Context context,
			OnClickListener positiveListener, OnClickListener negativeListener,
			OnCancelListener cancelListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setTitle(R.string.confirm);
		builder.setMessage(R.string.message_send_read_report);
		builder.setPositiveButton(R.string.yes, positiveListener);
		builder.setNegativeButton(R.string.no, negativeListener);
		builder.setOnCancelListener(cancelListener);
		builder.show();
	}

	public static String extractEncStrFromCursor(Cursor cursor,
			int columnRawBytes, int columnCharset) {
		String rawBytes = cursor.getString(columnRawBytes);
		int charset = cursor.getInt(columnCharset);

		if (TextUtils.isEmpty(rawBytes)) {
			return "";
		} else if (charset == CharacterSets.ANY_CHARSET) {
			return rawBytes;
		} else {
			return new EncodedStringValue(charset,
					PduPersister.getBytes(rawBytes)).getString();
		}
	}

	public static String extractEncString(String paramString, int paramInt) {
		if (TextUtils.isEmpty(paramString))
			paramString = "";
		while (true) {

			if (paramInt != 0)
				paramString = new EncodedStringValue(paramInt,
						PduPersister.getBytes(paramString)).getString();

			return paramString;
		}

	}

	private static String extractEncStr(Context context,
			EncodedStringValue value) {
		if (value != null) {
			return value.getString();
		} else {
			return "";
		}
	}

	public static ArrayList<String> extractUris(URLSpan[] spans) {
		int size = spans.length;
		ArrayList<String> accumulator = new ArrayList<String>();

		for (int i = 0; i < size; i++) {
			accumulator.add(spans[i].getURL());
		}
		return accumulator;
	}

	/**
	 * Play/view the message attachments. TOOD: We need to save the draft before
	 * launching another activity to view the attachments. This is hacky though
	 * since we will do saveDraft twice and slow down the UI. We should pass the
	 * slideshow in intent extra to the view activity instead of asking it to
	 * read attachments from database.
	 * 
	 * @param activity
	 * @param msgUri
	 *            the MMS message URI in database
	 * @param slideshow
	 *            the slideshow to save
	 * @param persister
	 *            the PDU persister for updating the database
	 * @param sendReq
	 *            the SendReq for updating the database
	 */
	public static void viewMmsMessageAttachment(Activity activity, Uri msgUri,
			SlideshowModel slideshow, AsyncDialog asyncDialog) {
		viewMmsMessageAttachment(activity, msgUri, slideshow, 0, asyncDialog);
	}

	public static void viewMmsMessageAttachment(final Activity activity,
			final Uri msgUri, final SlideshowModel slideshow,
			final int requestCode, AsyncDialog asyncDialog) {
		boolean isSimple = (slideshow == null) ? false : slideshow.isSimple();
		if (isSimple || (slideshow!=null&&slideshow.get(0).hasVcard())) {
			// In attachment-editor mode, we only ever have one slide.
			MessageUtils.viewSimpleSlideshow(activity, slideshow);
		} else {
			// The user wants to view the slideshow. We have to persist the
			// slideshow parts
			// in a background task. If the task takes longer than a half
			// second, a progress dialog
			// is displayed. Once the PDU persisting is done, another runnable
			// on the UI thread get
			// executed to start the SlideshowActivity.
			asyncDialog.runAsync(new Runnable() {
				@Override
				public void run() {
					// If a slideshow was provided, save it to disk first.
					if (slideshow != null) {
						PduPersister persister = PduPersister
								.getPduPersister(activity);
						try {
							PduBody pb = slideshow.toPduBody();
							persister.updateParts(msgUri, pb);
							slideshow.sync(pb);
						} catch (MmsException e) {
							Log.e(TAG, "Unable to save message for preview");
							return;
						}
					}
				}
			}, new Runnable() {
				@Override
				public void run() {
					// Once the above background thread is complete, this
					// runnable is run
					// on the UI thread to launch the slideshow activity.
					launchSlideshowActivity(activity, msgUri, requestCode);
				}
			}, R.string.building_slideshow_title);//
		}
	}

	public static void launchSlideshowActivity(Context context, Uri msgUri,
			int requestCode) {
		// Launch the slideshow activity to play/view.
		Intent intent = new Intent(context, SlideshowActivity.class);
		intent.setData(msgUri);
		if (requestCode > 0 && context instanceof Activity) {
			((Activity) context).startActivityForResult(intent, requestCode);
		} else {
			context.startActivity(intent);
		}

	}

	/**
	 * Debugging
	 */
	public static void writeHprofDataToFile() {
		String filename = Environment.getExternalStorageDirectory()
				+ "/mms_oom_hprof_data";
		try {
			android.os.Debug.dumpHprofData(filename);
			Log.i(TAG, "##### written hprof data to " + filename);
		} catch (IOException ex) {
			Log.e(TAG, "writeHprofDataToFile: caught " + ex);
		}
	}

	// An alias (or commonly called "nickname") is:
	// Nickname must begin with a letter.
	// Only letters a-z, numbers 0-9, or . are allowed in Nickname field.
	public static boolean isAlias(String string) {
		if (!MmsConfig.isAliasEnabled()) {
			return false;
		}

		int len = string == null ? 0 : string.length();

		if (len < MmsConfig.getAliasMinChars()
				|| len > MmsConfig.getAliasMaxChars()) {
			return false;
		}

		if (!Character.isLetter(string.charAt(0))) { // Nickname begins with a
														// letter
			return false;
		}
		for (int i = 1; i < len; i++) {
			char c = string.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '.')) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Given a phone number, return the string without syntactic sugar, meaning
	 * parens, spaces, slashes, dots, dashes, etc. If the input string contains
	 * non-numeric non-punctuation characters, return null.
	 */
	private static String parsePhoneNumberForMms(String address) {
		StringBuilder builder = new StringBuilder();
		int len = address.length();

		for (int i = 0; i < len; i++) {
			char c = address.charAt(i);

			// accept the first '+' in the address
			if (c == '+' && builder.length() == 0) {
				builder.append(c);
				continue;
			}

			if (Character.isDigit(c)) {
				builder.append(c);
				continue;
			}

			if (numericSugarMap.get(c) == null) {
				return null;
			}
		}
		return builder.toString();
	}

	/**
	 * Returns true if the address passed in is a valid MMS address.
	 */
	public static boolean isValidMmsAddress(String address) {
		String retVal = parseMmsAddress(address);
		return (retVal != null);
	}

	/**
	 * parse the input address to be a valid MMS address. - if the address is an
	 * email address, leave it as is. - if the address can be parsed into a
	 * valid MMS phone number, return the parsed number. - if the address is a
	 * compliant alias address, leave it as is.
	 */
	public static String parseMmsAddress(String address) {
		// if it's a valid Email address, use that.
		if (Mms.isEmailAddress(address)) {
			return address;
		}

		// if we are able to parse the address to a MMS compliant phone number,
		// take that.
		String retVal = parsePhoneNumberForMms(address);
		if (retVal != null) {
			return retVal;
		}

		// if it's an alias compliant address, use that.
		if (isAlias(address)) {
			return address;
		}

		// it's not a valid MMS address, return null
		return null;
	}

	private static void log(String msg) {
		Log.d(TAG, "[MsgUtils] " + msg);
	}

}
