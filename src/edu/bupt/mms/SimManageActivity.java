package edu.bupt.mms;

import edu.bupt.mms.ui.InboxActivity;
import edu.bupt.mms.ui.ManageSimMessages;
import edu.bupt.mms.ui.MessagingPreferenceActivity;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SimManageActivity extends TabActivity implements
		OnTabChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_tab);
		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		//
		TabHost tab_host = getTabHost();
		// LocalActivityManager mlam = new LocalActivityManager(this, false);
		// mlam.dispatchCreate(savedInstanceState);
		// tab_host.setup(mlam );
		tab_host.setup();

		
		
		
		
		
		
		
		
		
TabSpec tabspec2 = tab_host.newTabSpec("TAB_2");
		
		
		
		
		Intent localIntent = new Intent(this, ManageSeperateSim.class);
		Bundle localBundle = new Bundle();
		localBundle.putInt("sub_id", 0);
		localIntent.putExtras(localBundle);
		
		
		
		tabspec2.setIndicator(getString(R.string.card_chinacom));
		tabspec2.setContent(localIntent);
		tab_host.addTab(tabspec2);

		TabSpec tabspec3 = tab_host.newTabSpec("TAB_3");
		
		
		
		
		Intent localIntent2 = new Intent(this, ManageSeperateSim2.class);
		Bundle localBundle2 = new Bundle();
		localBundle2.putInt("sub_id", 1);
		localIntent2.putExtras(localBundle2);
		
		
		tabspec3.setIndicator(getString(R.string.card_chinamoble));
		tabspec3.setContent(localIntent2);
		tab_host.addTab(tabspec3);
		
		
		

//		TabSpec tabspec2 = tab_host.newTabSpec("TAB_1");
//		tabspec2.setIndicator("��һ���й���ţ�");
//		tabspec2.setContent(new Intent(this, PreferenceSim1Activity.class));
//		tab_host.addTab(tabspec2);
//
//		TabSpec tabspec3 = tab_host.newTabSpec("TAB_2");
//		tabspec3.setIndicator("�������й��ƶ���");
//		tabspec3.setContent(new Intent(this, PreferenceSim2Activity.class));
//		tab_host.addTab(tabspec3);

		tab_host.setCurrentTab(0);

		tab_host.setOnTabChangedListener(this);
	}

	@Override
	public void onTabChanged(String srcId) {
		// TODO Auto-generated method stub
		if (srcId.equals("TAB_1")) {

		//	Toast.makeText(this, "TAB_1", Toast.LENGTH_SHORT).show();
			Log.i("tab1", "tab1");

		} else if (srcId.equals("TAB_2")) {
		//	Toast.makeText(this, "TAB_2", Toast.LENGTH_SHORT).show();
			Log.i("tab2", "tab2");

		} 

	}

}
