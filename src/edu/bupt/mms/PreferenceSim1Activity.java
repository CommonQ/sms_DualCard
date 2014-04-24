package edu.bupt.mms;

import edu.bupt.mms.ui.ComposeMessageActivity;
import edu.bupt.mms.ui.ManageSimMessages;
import edu.bupt.mms.ui.MessageUtils;
import edu.bupt.mms.ui.MessagingPreferenceActivity;
import edu.bupt.mms.ui.NumberPickerDialog;
import edu.bupt.mms.util.Recycler;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

public class PreferenceSim1Activity extends PreferenceActivity implements
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

	
	//private Preference mSmsDeliveryReportPref;
	//zaizhe
	//private CheckBoxPreference mSmsDeliveryPref;
	
	private Preference mManageSimPref;
	//private Preference mManageSim2Pref;
	
	private Recycler mSmsRecycler;
	private Recycler mMmsRecycler;
	private static final int CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG = 3;
	private CharSequence[] mVibrateEntries;
	private CharSequence[] mVibrateValues;


	// zaizhe
//	private Preference mPriorityPref;
	
	

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
	//	setEnabledNotificationsPref();
	
	}

	private void loadPrefs() {
		addPreferencesFromResource(R.xml.preferences_sim1);

		
	//	mPriorityPref = findPreference("pref_key_priority");
		
		


		mManageSimPref = findPreference("pref_key_manage_sim_messages");
		
	//	mSmsDeliveryReportPref = findPreference("pref_key_sms_delivery_reports");
		//zaizhe
	//	mSmsDeliveryPref = ((CheckBoxPreference)findPreference("pref_key_sms_delivery_reports"));
	

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
//        
        
        
		
        //zaizhe �ж��Ƿ����Σ�������Σ����������ȼ�Ϊ0�����ûҡ�
      
        
        //zaizhe
        
        
        
        
      

       

       // setEnabledNotificationsPref();

        // If needed, migrate vibration setting from a previous version
       
        mSmsRecycler = Recycler.getSmsRecycler();
        mMmsRecycler = Recycler.getMmsRecycler();

        // Fix up the recycler's summary with the correct values
       
        
        
        
        

    
		
		
		
		
		
		

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
		  if (preference == mManageSimPref) {

			Intent localIntent = new Intent(this, ManageSimMessages.class);
			Bundle localBundle = new Bundle();
			localBundle.putInt("sub_id", 0);
			localIntent.putExtras(localBundle);
			startActivity(localIntent);

			// startActivity(new Intent(this, ManageSimMessages.class));
		} 

	
		
		
		
		



		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	

	

	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CONFIRM_CLEAR_SEARCH_HISTORY_DIALOG:
			return new AlertDialog.Builder(PreferenceSim1Activity.this)
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

	

	

	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		return false;
	}

	
}
