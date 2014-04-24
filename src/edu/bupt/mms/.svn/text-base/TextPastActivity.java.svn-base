package edu.bupt.mms;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Menu;
import android.widget.EditText;

public class TextPastActivity extends Activity {
	
	
	
	
	
	

	mEditText edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_past);
		
		
		ActionBar actionbar = getActionBar();
		
		actionbar.setTitle("复制文字");
		
		mEditText edit = (mEditText)findViewById(R.id.text_paste);
		
	
		edit.setActivity(this);
		
		Intent mIntent =getIntent();
		
		Bundle data = mIntent.getBundleExtra("paste_bundle");
		
		
		String text = data.getString("paste");
		
		edit.setText(text);
		
		
		
	//	edit.showContextMenu(x, y, metaState);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_past, menu);
		
		
		
		return true;
	}
	
	
	
	
		
	

}
