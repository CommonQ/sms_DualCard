package edu.bupt.mms.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.bupt.mms.DeletePhrTempActivity;
import edu.bupt.mms.LogTag;
import edu.bupt.mms.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PhraseTemplateActivity extends Activity {

	
	private String[] names= new String[]{"你好吗？","祝身体健康！","新年快乐！","一路顺风","晚安！","有空吗？","生日快乐！","请查收电子邮件","我现在有事，请稍后联系","马上到!","在哪？","贷款已到","再见！","吃饭了吗？","圣诞快乐！"};
	private String[] data={};
	
	//private String[] names=getResources().getStringArray(R.array.default_phrase_template);
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	SimpleAdapter simpleAdapter=null;
	ListView list=null;
	List<Map<String,Object>> listitems=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phrase_template);
	
		preferences = getSharedPreferences("phrasetemplate",Context.MODE_WORLD_WRITEABLE);
		
		
		editor= preferences.edit();
		
		//preferences.contains(arg0)
		
		if(!preferences.contains("isCreated")){
		
			
			Log.v("PhraseTemplateActivity", "�����˳�ʼ����");
			editor.putBoolean("isCreated", true);
		
			
			
			
			
			
			Set<String> siteno = new HashSet<String>(); 
			
		
			for(int i=0;i<names.length;i++){
				
				siteno.add(names[i]);
				Log.v("PhraseTemplateActivity", "�����˳�ʼ����"+names[i]);
			}
			
			
			
			editor.putStringSet("template", siteno);
			editor.commit();
		
			Log.v("PhraseTemplateActivity", "siteno��С�� "+siteno.toString());
			
			
		
		}
		
		
		//editor.putStringSet("", arg1);
		
	
		
		
		listitems = new ArrayList<Map<String,Object>>();
		
		
		// PreferenceManager.
		
		
		Set<String> siteno2 = new HashSet<String>();  
		siteno2 =preferences.getStringSet("template", siteno2);
		Log.v("PhraseTemplateActivity", "oncreate siteno2��С�� "+siteno2.toString());
		if(siteno2.size()>0){
			
			data = (String[]) siteno2.toArray(new String[siteno2.size()]);
		}
		
		
		
		
		for(int i=0;i<data.length;i++){
			Map<String,Object> listitem = new HashMap<String, Object>();
			listitem.put("personname", data[i]);
			listitems.add(listitem);
			
			
		}
		
		simpleAdapter = new SimpleAdapter(this, listitems, R.layout.phrase_template_item, new String[]{"personname"}, new int[]{R.id.desc});
		
		
		list = (ListView) findViewById(R.id.mylist);
		list.setAdapter(simpleAdapter);
	
		
		
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				
				
				
				Intent intent = getIntent();
				
				intent.putExtra("template", data[position]);
				
				
				Log.v("PhraseTemplateActivity", data[position]);
				PhraseTemplateActivity.this.setResult(RESULT_OK, intent);
				
				
				PhraseTemplateActivity.this.finish();
				
			
				
				
				
				
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phrase_template, menu);
		return true;
	}

	
	public void addItems(){
	
		Set<String> siteno2 = new HashSet<String>();  
		siteno2 =preferences.getStringSet("template", siteno2);
		Log.v("PhraseTemplateActivity", "addItems siteno2��С�� "+siteno2.toString());
		if(siteno2.size()>0){
			
			data = (String[]) siteno2.toArray(new String[siteno2.size()]);
		}
		
		
		listitems.clear();
		
		for(int i=0;i<data.length;i++){
			Map<String,Object> listitem = new HashMap<String, Object>();
			listitem.put("personname", data[i]);
			listitems.add(listitem);
			
			
		}
		
	}
	
	
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		  switch(item.getItemId()) {
          case R.id.action_add_template:
             
        	  
        	  LayoutInflater factory = LayoutInflater.from(this);
        	  final View view = factory.inflate(R.layout.edit_dialog_item, null);
        	  final EditText edit=(EditText)view.findViewById(R.id.edit_dialog);
        	  
        	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
              builder.setTitle("输入新短信模板")
                  .setIconAttribute(android.R.attr.alertDialogIcon)
                  .setCancelable(true)
                  .setPositiveButton(getString(R.string.yes),new OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
						SharedPreferences preferences= getSharedPreferences("phrasetemplate",Context.MODE_WORLD_WRITEABLE);
						SharedPreferences.Editor editor=preferences.edit();
						
						String newTemplate = edit.getText().toString().trim();
						
						Set<String> siteno3 = new HashSet<String>(); 
						
						siteno3=preferences.getStringSet("template", siteno3);

						Log.v("PhraseTemplateActivity", "���֮���siteno3�� "+siteno3.toString());
						
						siteno3.add(newTemplate);
						
					//	editor.clear();
//						editor.remove("template");
//						
//						editor.putStringSet("template", siteno);
//						
//					
//						editor.commit();

						Log.v("PhraseTemplateActivity", "���֮���newTemplate�� "+newTemplate);
						
						
						Log.v("PhraseTemplateActivity", "���֮���siteno�� "+siteno3.toString());
						
						 editor.remove("template");
			        	  editor.putStringSet("template", siteno3);
			        	  editor.commit();
			        	  
						
						
						

						Set<String> siteno2 = new HashSet<String>(); 
						
						siteno2=preferences.getStringSet("template", siteno2);
						Log.v("PhraseTemplateActivity", "onclick ���siteno2��С�� "+siteno2.toString());
						
						addItems();
						
						simpleAdapter.notifyDataSetChanged();
						
						
					}
                	  
                  })
                  .setNegativeButton(getString(R.string.no), null).setView(view).create().show();
                  
                  // builder.setPositiveButton(text, listener)
              
        	  Log.v("PhraseTemplate", "��Ӽ����ˣ�����");
        	  
        	  
              break;
          case R.id.action_delete_template:
              // The invalid threadId of -1 means all threads here.
        	  
        	  Intent delete_intent = new Intent(PhraseTemplateActivity.this,DeletePhrTempActivity.class);
        	  startActivityForResult(delete_intent, 456);
        	  
        	  
        	  
        	  Log.v("PhraseTemplate", "ɾ������ˣ���");
              break;
         
          default:
              return true;
      }
      return false;
	}
	
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data){
		 
		 if(requestCode==456){
			 
			 
			 addItems();
				
			
			 simpleAdapter.notifyDataSetChanged();
			 
		 }
		 
		 
		 
	 }
	
	

}
