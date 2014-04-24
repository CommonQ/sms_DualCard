package edu.bupt.contacts.msim;

import com.android.internal.telephony.TelephonyProperties;

import android.os.SystemProperties;

public class MultiSimConfig {
	protected static String multiSimConfig =
            SystemProperties.get(TelephonyProperties.PROPERTY_MULTI_SIM_CONFIG);
	
	public static boolean isMultiSimEnabled(){
		return (multiSimConfig.equals("dsds") || multiSimConfig.equals("dsda"));
	}
}
