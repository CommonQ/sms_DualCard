package edu.bupt.mms;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.EditText;

public class mEditText extends EditText {
	
	Activity activity;
	ClipboardManager clip;
	public mEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		
	}
	
	
	public void setActivity(Activity activity){
		
		
		this.activity=activity;
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu);
		
		
		
		
	}
	
	
	

}
