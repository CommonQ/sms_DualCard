package edu.bupt.mms;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PreviewContactsActivity extends Activity {

	private ListView mList;
	private int mCurrMode;
	private int mItemLayoutID;
	
	private Button selectAll;
	private Button confirmAll;
	
	private String[] data;
	
	private static final String[] DATA= new String[]{"haha","hello","lalalaS"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_contacts);
		mList=(ListView)this.findViewById(R.id.preview_list);
		selectAll=(Button)this.findViewById(R.id.select_all_preview_multiple);
		confirmAll=(Button)this.findViewById(R.id.preview_multiple);
		
		
		Intent intent =getIntent();
		data=(String[]) intent.getExtra("contacts");
		
		
		initList();
		
		
		selectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			
//			ListView list=(ListView)v.getParent();
//		list.getAdapter().getItem(0).

			}

		});

	
	}
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.preview_contacts, menu);
//		return true;
//	}
		
		private void initList(){
			
			mCurrMode = ListView.CHOICE_MODE_MULTIPLE;
			mItemLayoutID=R.layout.listitem_multiple;
			mList.setAdapter(new ArrayAdapter<String>(this, mItemLayoutID,data));
			mList.setItemsCanFocus(false);
			mList.setChoiceMode(mCurrMode);
			
		}

}
	
	
