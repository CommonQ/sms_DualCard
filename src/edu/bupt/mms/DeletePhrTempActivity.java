package edu.bupt.mms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.bupt.mms.ui.ConversationList;
import edu.bupt.mms.ui.PhraseTemplateActivity;



import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DeletePhrTempActivity extends Activity {

	

	HashMap<String, Object> itemIsChecked = new HashMap<String, Object>();
	//private String[] names= new String[]{"�����","���տ��֣�","���ģ�","�Է�����","�?","������֣�","���ϵ���","�п���","���������£��Ժ���ϵ","һ·˳�磡","�ټ�","����յ����ʼ�","����ѵ�","ʥ������!","ף���彡����"};
	private String[] data={};
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	MySimpleAdapter simpleAdapter=null;
	ListView list=null;
	List<Map<String,Object>> listitems=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_delete_phr_temps);
		setContentView(R.layout.activity_delete_phr_temps);
		
		

		preferences = getSharedPreferences("phrasetemplate",Context.MODE_WORLD_WRITEABLE);
		
		
		editor= preferences.edit();
		
		
	
	
		
		
		listitems = new ArrayList<Map<String,Object>>();
		
		
		
	
		
		
		
		Set<String> siteno2 = new HashSet<String>();  
		siteno2 =preferences.getStringSet("template", siteno2);
		Log.v("PhraseTemplateActivity", "siteno2��С�� "+siteno2.toString());
		if(siteno2.size()>0){
			
			data = (String[]) siteno2.toArray(new String[siteno2.size()]);
		}
		
		
		
		
		for(int i=0;i<data.length;i++){
			Map<String,Object> listitem = new HashMap<String, Object>();
			listitem.put("personname", data[i]);
			listitems.add(listitem);
			
			
		}
		
		simpleAdapter = new MySimpleAdapter(this, listitems, R.layout.phrase_delete_template, new String[]{"personname"}, new int[]{R.id.delete_checkBox});
		
		
		list = (ListView) findViewById(R.id.mylist2);
		
		View loadView =getLayoutInflater().inflate(R.layout.footer_delete, null);
		//list.addFooterView(loadView);
		list.setAdapter(simpleAdapter);
		
		
		
		
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
		//	CheckBox check =	(CheckBox) simpleAdapter.getItem(position);
				
				if(itemIsChecked.get(""+position)!=null){
					
					itemIsChecked.remove(""+position);
				}
				
				else{
				
				itemIsChecked.put(""+position, true);
				
				}
			
				simpleAdapter.notifyDataSetChanged();
				
				
			Log.v("DeletePhrTempActivity", "check: "+position);
			}
			
		});
		
		
		
	}
	
	
	 class MySimpleAdapter extends SimpleAdapter{

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			
			
                   LayoutInflater mInflater = getLayoutInflater();
			
			
			convertView= (View) mInflater.inflate(R.layout.phrase_delete_template, null);	
			
			CheckBox mCheck =(CheckBox) convertView.findViewById(R.id.delete_checkBox);
			
              
			if((Boolean)itemIsChecked.get(""+position)!=null&&(Boolean)itemIsChecked.get(""+position)==true){
				
				
				Log.v("MySimpleAdapter", "setChcked: "+position+ "is true?"+mCheck.isChecked());
				mCheck.setChecked(true);
			
			}
			
			
			return super.getView(position, convertView, parent);
		}
		 
		
		
		 
		 
		 
	 }
	
	

	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_phr_temp, menu);
		
		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		  switch(item.getItemId()) {
          case R.id.action_delete_items:
             
        	  Set<String> siteno2 = new HashSet<String>();  
      		
        	  siteno2 =preferences.getStringSet("template", siteno2);
        	  
        	  String[] linshi_data=null;
        	  ArrayList<String> linshi_data_2= new ArrayList<String>();
        	  if(siteno2.size()>0){
      			
      			linshi_data = (String[]) siteno2.toArray(new String[siteno2.size()]);
      		}
        	 
        	  
        	  Log.v("MySimpleAdapter", siteno2.toString());
        	  
        	  for(int i=0;i<linshi_data.length;i++){
        		  linshi_data_2.add(linshi_data[i]);
        	  Log.v("MySimpleAdapter", linshi_data[i]);
        	  }
        	  
        	  
        	  
        	  
        	  
        	  for(int i=0;i<linshi_data.length;i++){
        		  
        		  
        		  if(itemIsChecked.get(""+i)!=null){
        			  
        			  linshi_data[i]="";
        			  
        			  
        		  }
        		  
        		  
        		  
        		  
        		  
        	  }
        	  
        	  Set<String> siteno = new HashSet<String>();  
      		
        	
        	  for(int i=0;i<linshi_data.length;i++){
        		  
        		  
        		  Log.v("MySimpleAdapter", linshi_data[i]);
        		 
        		  if(!linshi_data[i].equals("")){
        			  
        			  
        			  siteno.add(linshi_data[i]);
        		  }
        		  
        		  
        	  }
        	  
        	  editor.remove("template");
        	  editor.putStringSet("template", siteno);
        	  editor.commit();
        	  
        	 
        	  
        	  
        	  DeletePhrTempActivity.this.setResult(456);
        	  
        	  DeletePhrTempActivity.this.finish();
        	  
        	  
              break;

         
          default:
              return true;
      }
      return false;
	}

}
