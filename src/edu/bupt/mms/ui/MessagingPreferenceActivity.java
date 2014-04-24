/*
 * Copyright (C) 2007-2008 Esmertec AG.
 * Copyright (C) 2007-2008 The Android Open Source Project
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
import edu.bupt.mms.SimManageActivity;
import edu.bupt.mms.TestActivity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.bupt.mms.util.Recycler;

/**
 * With this activity, users can set preferences for MMS and SMS and can access
 * and manipulate SMS messages stored on the SIM.
 */
public class MessagingPreferenceActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private final String TAG = "MessagingPreferenceActivity";
	// Symbolic names for the keys used for preference lookup
	public static final String MMS_DELIVERY_REPORT_MODE = "pref_key_mms_delivery_reports";
	public static final String EXPIRY_TIME = "pref_key_mms_expiry";
	public static final String PRIORITY = "pref_key_mms_priority";
	public static final String READ_REPORT_MODE = "pref_key_mms_read_reports";
	public static final String SMS_DELIVERY_REPORT_MODE = "pref_key_sms_delivery_reports";
	public static final String NOTIFICATION_ENABLED = "pref_key_enable_notifications";
	public static final String NOTIFICATION_VIBRATE = "pref_key_vibrate";
	public static final String NOTIFICATION_VIBRATE_WHEN = "pref_key_vibrateWhen";
	public static final String NOTIFICATION_RINGTONE = "pref_key_ringtone";
	public static final String AUTO_RETRIEVAL = "pref_key_mms_auto_retrieval";
	public static final String RETRIEVAL_DURING_ROAMING = "pref_key_mms_retrieval_during_roaming";
	public static final String AUTO_DELETE = "pref_key_auto_delete";

	// Menu entries
	private static final int MENU_RESTORE_DEFAULTS = 1;

	private Preference mSmsLimitPref;
	private Preference mSmsDeliveryReportPref;
	//zaizhe
	private CheckBoxPreference mSmsDeliveryPref;
	private Preference mMmsLimitPref;
	private Preference mMmsDeliveryReportPref;
	private Preference mMmsReadReportPref;
	private Preference mManageSimPref;
	//private Preference mManageSim2Pref;
	private Preference mClearHistoryPref;
	private ListPreference mVibrateWhenPref;
	private CheckBoxPreference mEnableNotificationsPref;
	private Recycler mSmsRecycler;
	private Recycler mMmsRecycler;
	private static final int CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG = 3;
	private CharSequence[] mVibrateEntries;
	private CharSequence[] mVibrateValues;

	// 010:在设置项里添加字号控制（功能24）
	private Preference mMsgViewPref;

	private int cs;
	//private int sizeof;
	// 010above
	
	private Preference mPhrasePref;

	// zaizhe
	private Preference mPriorityPref;
	
	private Preference mTimeSchedulePref;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		loadPrefs();

	//	ActionBar actionBar = getActionBar();
	//W	actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Since the enabled notifications pref can be changed outside of this
		// activity,
		// we have to reload it whenever we resume.
		setEnabledNotificationsPref();
		registerListeners();
	}

	private void loadPrefs() {
		addPreferencesFromResource(R.xml.preferences);

		// 010:
		mMsgViewPref = findPreference("pref_key_set_sizeof");//在设置项里添加字号控制（功能24）
		mPhrasePref=findPreference("pref_key_phrase_template");
		mPriorityPref = findPreference("pref_key_priority");
		
		mTimeSchedulePref = findPreference("pref_key_time_schedule");


		mManageSimPref = findPreference("pref_key_manage_sim_messages");
		//mManageSim2Pref = findPreference("pref_key_manage_sim2_messages");
		mSmsLimitPref = findPreference("pref_key_sms_delete_limit");
		mSmsDeliveryReportPref = findPreference("pref_key_sms_delivery_reports");
		//zaizhe
		mSmsDeliveryPref = ((CheckBoxPreference)findPreference("pref_key_sms_delivery_reports"));
		mMmsDeliveryReportPref = findPreference("pref_key_mms_delivery_reports");
		mMmsReadReportPref = findPreference("pref_key_mms_read_reports");
		mMmsLimitPref = findPreference("pref_key_mms_delete_limit");
		mClearHistoryPref = findPreference("pref_key_mms_clear_history");
		mEnableNotificationsPref = (CheckBoxPreference) findPreference(NOTIFICATION_ENABLED);
		mVibrateWhenPref = (ListPreference) findPreference(NOTIFICATION_VIBRATE_WHEN);

		mVibrateEntries = getResources().getTextArray(
				R.array.prefEntries_vibrateWhen);
		mVibrateValues = getResources().getTextArray(
				R.array.prefValues_vibrateWhen);

		setMessagePreferences();

	}

	private void restoreDefaultPreferences() {
		PreferenceManager.getDefaultSharedPreferences(this).edit().clear()
				.apply();
		setPreferenceScreen(null);
		loadPrefs();

		// NOTE: After restoring preferences, the auto delete function (i.e.
		// message recycler)
		// will be turned off by default. However, we really want the default to
		// be turned on.
		// Because all the prefs are cleared, that'll cause:
		// ConversationList.runOneTimeStorageLimitCheckForLegacyMessages to get
		// executed the
		// next time the user runs the Messaging app and it will either turn on
		// the setting
		// by default, or if the user is over the limits, encourage them to turn
		// on the setting
		// manually.
	}

	private void setMessagePreferences() {
        if (!MmsApp.getApplication().getTelephonyManager().hasIccCard()) {
            // No SIM card, remove the SIM-related prefs
            PreferenceCategory smsCategory =
                (PreferenceCategory)findPreference("pref_key_sms_settings");
            smsCategory.removePreference(mManageSimPref);
            
            
           //zaizhe
        }  
//        if (!MSimTelephonyManager.getDefault().hasIccCard(1)) {
//            // No SIM card, remove the SIM-related prefs
//            PreferenceCategory smsCategory =
//                (PreferenceCategory)findPreference("pref_key_sms_settings");
//            smsCategory.removePreference(mManageSim2Pref);
//            
//            
//           
//        }
        
        
        
		if (this.mSmsDeliveryPref != null) {
			if (Settings.System.getInt(getContentResolver(),
					"SMS_DELIVERY_REPORTS", 0) == 0)

				this.mSmsDeliveryPref.setChecked(false);

			else
				this.mSmsDeliveryPref.setChecked(true);

		}
        
        
        //zaizhe �ж��Ƿ����Σ�������Σ����������ȼ�Ϊ0�����ûҡ�
        if (TelephonyManager.getDefault().isNetworkRoaming()&&(MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_EVDO_0||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_EVDO_A||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_EVDO_B||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_CDMA||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_1xRTT)) {
            // No SIM card, remove the SIM-related prefs
        	
        	
        	
        	
        	
        	
        	
        	
        	//MSimTelephonyManager.getDefault().getCdmaEriIconMode(arg0)
        	
        	
        	
        	
            PreferenceCategory smsCategory =
                (PreferenceCategory)findPreference("pref_key_sms_settings");
            smsCategory.removePreference(mPriorityPref);
            
            Settings.System.putInt(MessagingPreferenceActivity.this.getContentResolver(), "SMS_PRIORITY", 0);
			
          
            
            
            
            
            
        }  
        
        //zaizhe
        
        if(!MSimTelephonyManager.getDefault().isNetworkRoaming(0)){
        	
//        	 PreferenceCategory smsCategory =
//                     (PreferenceCategory)findPreference("pref_key_view_settings");
//                 smsCategory.removePreference(mTimeSchedulePref);
                 mTimeSchedulePref.setEnabled(false);
                 MessageUtils.isShowBJTime=false;
                 
//                 Settings.System.putInt(MessagingPreferenceActivity.this.getContentResolver(),
// 						Settings.System.AUTO_TIME_ZONE, 1);
//
// 				Settings.System.putInt(MessagingPreferenceActivity.this.getContentResolver(),
// 						Settings.System.AUTO_TIME, 1);
        	
        	
        	
        }
        
        
        	//zaizhe!!!!
        if (!MmsConfig.getSMSDeliveryReportsEnabled()||TelephonyManager.getDefault().isNetworkRoaming()&&(MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_EVDO_0||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_EVDO_A||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_EVDO_B||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_CDMA||MSimTelephonyManager.getDefault().getNetworkType(0)==TelephonyManager.NETWORK_TYPE_1xRTT)) {
            PreferenceCategory smsCategory =
                (PreferenceCategory)findPreference("pref_key_sms_settings");
            smsCategory.removePreference(mSmsDeliveryReportPref);
            if (!MmsApp.getApplication().getTelephonyManager().hasIccCard()) {
                getPreferenceScreen().removePreference(smsCategory);
            }
        }

        if (!MmsConfig.getMmsEnabled()) {
            // No Mms, remove all the mms-related preferences
            PreferenceCategory mmsOptions =
                (PreferenceCategory)findPreference("pref_key_mms_settings");
            getPreferenceScreen().removePreference(mmsOptions);

            PreferenceCategory storageOptions =
                (PreferenceCategory)findPreference("pref_key_storage_settings");
            storageOptions.removePreference(findPreference("pref_key_mms_delete_limit"));
        } else {
            if (!MmsConfig.getMMSDeliveryReportsEnabled()) {
                PreferenceCategory mmsOptions =
                    (PreferenceCategory)findPreference("pref_key_mms_settings");
                mmsOptions.removePreference(mMmsDeliveryReportPref);
            }
            if (!MmsConfig.getMMSReadReportsEnabled()) {
                PreferenceCategory mmsOptions =
                    (PreferenceCategory)findPreference("pref_key_mms_settings");
                mmsOptions.removePreference(mMmsReadReportPref);
            }
        }

        setEnabledNotificationsPref();

        // If needed, migrate vibration setting from a previous version
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(NOTIFICATION_VIBRATE_WHEN) &&
                sharedPreferences.contains(NOTIFICATION_VIBRATE)) {
            int stringId = sharedPreferences.getBoolean(NOTIFICATION_VIBRATE, false) ?
                    R.string.prefDefault_vibrate_true :
                    R.string.prefDefault_vibrate_false;
            mVibrateWhenPref.setValue(getString(stringId));
        }

        mSmsRecycler = Recycler.getSmsRecycler();
        mMmsRecycler = Recycler.getMmsRecycler();

        // Fix up the recycler's summary with the correct values
        setSmsDisplayLimit();
        setMmsDisplayLimit();

        adjustVibrateSummary(mVibrateWhenPref.getValue());
        
        
        
        
        

    	SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    
        
        int which=	Settings.System.getInt(this.getContentResolver(), "SMS_PRIORITY", localSharedPreferences.getInt("SmsPriority", 0));
    	
    	String location=null;
		if(which==0){
			location="普通";
			
		}else if(which==1){
			location="交互";
		}else if(which==2){
			location="急";
		}else if(which==3){
			location="紧急";
		}
		
		mPriorityPref.setSummary(location);
		
		
		
		
		
		
		

    	SharedPreferences localSharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
    
        

//		int which_time = Settings.System.getInt(this.getContentResolver(),
//				Settings.System.AUTO_TIME_ZONE,
//				localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE, 1));
//		
    	
		int which_time=localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE, 1);
    	
       // int which_time=	localSharedPreferences2.getInt(Settings.System.AUTO_TIME_ZONE, 1);
    	
    	String location_time=null;
		if(which_time==0){
			location_time=this.getString(R.string.beijing_time);
			
		}else if(which_time==1){
			location_time=this.getString(R.string.local_time);
		}
		
		
		
		
		mTimeSchedulePref.setSummary(location_time);
		
		
		
		
		
		
		
		
        
        
        
    }

	private void setEnabledNotificationsPref() {
		// The "enable notifications" setting is really stored in our own prefs.
		// Read the
		// current value and set the checkbox to match.
		mEnableNotificationsPref.setChecked(getNotificationEnabled(this));
	}

	private void setSmsDisplayLimit() {
		mSmsLimitPref.setSummary(getString(R.string.pref_summary_delete_limit,
				mSmsRecycler.getMessageLimit(this)));
		
	}

	private void setMmsDisplayLimit() {
		mMmsLimitPref.setSummary(getString(R.string.pref_summary_delete_limit,
				mMmsRecycler.getMessageLimit(this)));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();
		menu.add(0, MENU_RESTORE_DEFAULTS, 0, R.string.restore_default);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_RESTORE_DEFAULTS:
			restoreDefaultPreferences();
			return true;

		case android.R.id.home:
			// The user clicked on the Messaging icon in the action bar. Take
			// them back from
			// wherever they came from
			finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == mSmsLimitPref) {
			new NumberPickerDialog(this, mSmsLimitListener,
					mSmsRecycler.getMessageLimit(this),
					mSmsRecycler.getMessageMinLimit(),
					mSmsRecycler.getMessageMaxLimit(),
					R.string.pref_title_sms_delete).show();
		} else if (preference == mMmsLimitPref) {
			new NumberPickerDialog(this, mMmsLimitListener,
					mMmsRecycler.getMessageLimit(this),
					mMmsRecycler.getMessageMinLimit(),
					mMmsRecycler.getMessageMaxLimit(),
					R.string.pref_title_mms_delete).show();
		} else if (preference == mManageSimPref) {

			Intent localIntent = new Intent(this, SimManageActivity.class);
//			Bundle localBundle = new Bundle();
//			localBundle.putInt("sub_id", 0);
//			localIntent.putExtras(localBundle);
			startActivity(localIntent);

			// startActivity(new Intent(this, ManageSimMessages.class));
		} 
//		else if (preference == mManageSim2Pref) {
//
//			Intent localIntent = new Intent(this, ManageSimMessages.class);
//			Bundle localBundle = new Bundle();
//			localBundle.putInt("sub_id", 1);
//			localIntent.putExtras(localBundle);
//			startActivity(localIntent);
//
//			// startActivity(new Intent(this, ManageSimMessages.class));
//		}
		
		else if (preference == this.mSmsDeliveryPref) {
			ContentResolver localContentResolver11 = getContentResolver();
			if (this.mSmsDeliveryPref.isChecked()){
				
				Settings.System.putInt(localContentResolver11, "SMS_DELIVERY_REPORTS", 1);
				
			}else{
				
				Settings.System.putInt(localContentResolver11, "SMS_DELIVERY_REPORTS", 0);
				
			}
			

			
		}

		else if (preference == mPriorityPref) {
			AlertDialog.Builder builder = new Builder(
					MessagingPreferenceActivity.this);

			SharedPreferences localSharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);

			int order = Settings.System.getInt(this.getContentResolver(),
					"SMS_PRIORITY",
					localSharedPreferences.getInt("SmsPriority", 0));
			builder.setSingleChoiceItems( // ���õ�ѡ�б�ѡ��
					R.array.msg, order, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences.Editor localEditor = PreferenceManager
									.getDefaultSharedPreferences(
											MessagingPreferenceActivity.this)
									.edit();
							localEditor.putInt("SmsPriority", which);
							localEditor.commit();

							Settings.System.putInt(
									MessagingPreferenceActivity.this
											.getContentResolver(),
									"SMS_PRIORITY", which);

							String location = null;
							if (which == 0) {
								location = "普通";

							} else if (which == 1) {
								location = "交互";
							} else if (which == 2) {
								location = "急";
							} else if (which == 3) {
								location = "紧急";
							}

							mPriorityPref.setSummary(location);

							dialog.dismiss();

						}
					});

			builder.setNegativeButton(getString(R.string.no), new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					arg0.dismiss();
				}

			});
			builder.show();
		}
		
		
		else if (preference == mTimeSchedulePref) {
			AlertDialog.Builder builder = new Builder(
					MessagingPreferenceActivity.this);

			SharedPreferences localSharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);

//			int order = Settings.System.getInt(this.getContentResolver(),
//					Settings.System.AUTO_TIME_ZONE,
//					localSharedPreferences.getInt(Settings.System.AUTO_TIME_ZONE, 1));
			
			int order=localSharedPreferences.getInt(Settings.System.AUTO_TIME_ZONE, 1);
			
			//int order = localSharedPreferences.getInt(Settings.System.AUTO_TIME_ZONE, 1);
			builder.setSingleChoiceItems( // ���õ�ѡ�б�ѡ��
					R.array.msh, order, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences.Editor localEditor = PreferenceManager
									.getDefaultSharedPreferences(
											MessagingPreferenceActivity.this)
									.edit();
							localEditor.putInt(Settings.System.AUTO_TIME_ZONE, which);
							localEditor.commit();

							if(which==0){
								
								MessageUtils.isShowBJTime=true;
								
								
//								Settings.System.putInt(
//										MessagingPreferenceActivity.this
//												.getContentResolver(),
//												Settings.System.AUTO_TIME_ZONE, which);
//								
//								Settings.System.putInt(
//										MessagingPreferenceActivity.this
//												.getContentResolver(),
//												Settings.System.AUTO_TIME, which);
//
//								AlarmManager timeZone = (AlarmManager) getSystemService(ALARM_SERVICE);
//								timeZone.setTimeZone("Asia/Shanghai");

	
	
							}else if(which==1){
								
//								Settings.System.putInt(
//										MessagingPreferenceActivity.this
//												.getContentResolver(),
//												Settings.System.AUTO_TIME_ZONE, which);
//								
//								Settings.System.putInt(
//										MessagingPreferenceActivity.this
//												.getContentResolver(),
//												Settings.System.AUTO_TIME, which);
								
								MessageUtils.isShowBJTime=false;
								
								
							}
							
//							Settings.System.putInt(
//									MessagingPreferenceActivity.this
//											.getContentResolver(),
//											Settings.System.AUTO_TIME_ZONE, which);

							String location = null;
							if (which == 0) {
								location = MessagingPreferenceActivity.this.getString(R.string.beijing_time);

							} else if (which == 1) {
								location = MessagingPreferenceActivity.this.getString(R.string.local_time);
							}
							mTimeSchedulePref.setSummary(location);

							dialog.dismiss();

						}
					});

			builder.setNegativeButton(getString(R.string.no), new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					arg0.dismiss();
				}

			});
			builder.show();
		}

		else if (preference == mClearHistoryPref) {
			showDialog(CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG);
			return true;
		} else if (preference == mEnableNotificationsPref) {
			// Update the actual "enable notifications" value that is stored in
			// secure settings.
			enableNotifications(mEnableNotificationsPref.isChecked(), this);
		}else if (preference == mPhrasePref) {
			// Update the actual "enable notifications" value that is stored in
			// secure settings.
			Intent intent = new Intent(MessagingPreferenceActivity.this,
					PhraseTemplateActivity.class);
			// startActivity(intent);
			startActivity(intent);
		} 
		
		//在设置项里添加字号控制（功能24）
		else if (preference == mMsgViewPref) {

			AlertDialog.Builder builder = new Builder(
					MessagingPreferenceActivity.this);
			if (ComposeMessageActivity.textSize == 18)
				cs = 1;
			else if (ComposeMessageActivity.textSize == 15)
				cs = 0;
			else if (ComposeMessageActivity.textSize == 21)
				cs = 2;
			//获取当前字号
			builder.setSingleChoiceItems( 
					R.array.mse, cs, new DialogInterface.OnClickListener() {
						//字号选项默认对应当前字号
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								ComposeMessageActivity.setTextSize(15);
								dialog.dismiss();
								break;
								//选择小号时，字号设置为15
							case 1:
								ComposeMessageActivity.setTextSize(18);
								dialog.dismiss();
								break;
								//选择标准时，字号设置为18
							case 2:
								ComposeMessageActivity.setTextSize(21);
								dialog.dismiss();
								break;
								//选择大号时，字号设置为21
							}
						}
					});
			// builder.setPositiveButton(R.string.conversation_ok,
			// new DialogInterface.OnClickListener() {
			//
			// public void onClick(DialogInterface dialog, int which) {
			//
			// ComposeMessageActivity.setTextSize(sizeof);
			// dialog.dismiss();
			//
			// }
			//
			// });
			builder.setNegativeButton(R.string.conversation_cancel,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
							//选择取消时，字号不变，退出Dialog
						}

					});
			builder.setTitle(R.string.menu_sizeof_content);//设置Dialog标题文字
			builder.create().show();
			// 010above to control the text size only in the chat
			// box by settings

		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	NumberPickerDialog.OnNumberSetListener mSmsLimitListener = new NumberPickerDialog.OnNumberSetListener() {
		public void onNumberSet(int limit) {
			mSmsRecycler.setMessageLimit(MessagingPreferenceActivity.this,
					limit);
			setSmsDisplayLimit();
		}
	};

	NumberPickerDialog.OnNumberSetListener mMmsLimitListener = new NumberPickerDialog.OnNumberSetListener() {
		public void onNumberSet(int limit) {
			mMmsRecycler.setMessageLimit(MessagingPreferenceActivity.this,
					limit);
			setMmsDisplayLimit();
		}
	};

	//
	// NumberPickerDialog.OnNumberSetListener mMsgViewListener =
	// new NumberPickerDialog.OnNumberSetListener() {
	// public void onNumberSet(int size) {
	// Log.i(TAG, "" + size);
	//
	// }
	// };
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG:
			return new AlertDialog.Builder(MessagingPreferenceActivity.this)
					.setTitle(R.string.confirm_clear_search_title)
					.setMessage(R.string.confirm_clear_search_text)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SearchRecentSuggestions recent = ((MmsApp) getApplication())
											.getRecentSuggestions();
									if (recent != null) {
										recent.clearHistory();
									}
									dialog.dismiss();
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.setIcon(android.R.drawable.ic_dialog_alert).create();
		}
		return super.onCreateDialog(id);
	}

	public static boolean getNotificationEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean notificationsEnabled = prefs.getBoolean(
				MessagingPreferenceActivity.NOTIFICATION_ENABLED, true);
		return notificationsEnabled;
	}

	public static void enableNotifications(boolean enabled, Context context) {
		// Store the value of notifications in SharedPreferences
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();

		editor.putBoolean(MessagingPreferenceActivity.NOTIFICATION_ENABLED,
				enabled);

		editor.apply();
	}

	private void registerListeners() {
		mVibrateWhenPref.setOnPreferenceChangeListener(this);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		boolean result = false;
		if (preference == mVibrateWhenPref) {
			adjustVibrateSummary((String) newValue);
			result = true;
		}
		return result;
	}

	private void adjustVibrateSummary(String value) {
		int len = mVibrateValues.length;
		for (int i = 0; i < len; i++) {
			if (mVibrateValues[i].equals(value)) {
				mVibrateWhenPref.setSummary(mVibrateEntries[i]);
				return;
			}
		}
		mVibrateWhenPref.setSummary(null);
	}
}
