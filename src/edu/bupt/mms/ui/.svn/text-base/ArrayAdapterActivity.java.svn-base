package edu.bupt.mms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;








import edu.bupt.mms.R;

import edu.bupt.mms.ui.ArrayAdapterActivity.ListVIewAdapter.ViewHolder;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ArrayAdapterActivity extends ListActivity {    
    
    private ListVIewAdapter ladapter;  
    private List<String> strList = new ArrayList<String>();  
    private List<Boolean> boolList = new ArrayList<Boolean>();  
   
    public ArrayList<HashMap<String, Object>> list;
    
    public SimpleAdapter listItemAdapter;
    
    boolean visflag = true;    
    ListView lv;   
    public static SMSReader mReader;
    public static SMSItem msms;
    public static ContactItem mcontact;
    static String str[] ={"姓名1","姓名2","姓名3","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名","姓名"};    
    CheckBox cb;    
    {    
        for(int i=0;i<str.length;i++)    
        {    
            strList.add(str[i]);  
            boolList.add(false);  
        }    
    }    
    
    
    @Override    
    protected void onCreate(Bundle savedInstanceState)    
    {    
            
        super.onCreate(savedInstanceState);    
                
        ReadSMS.model_flag=2;
        
        ladapter = new ListVIewAdapter(this);    
        //ladapter = ReadSMS.listItemAdapter;     
        
        lv = this.getListView();   
        
       // fillListView();
        
        lv.setAdapter(ladapter);    
          
        lv.setScrollBarStyle(1);    
    
        lv.setOnItemClickListener(new OnItemClickListener()    
        {    
    
            @Override    
            public void onItemClick(AdapterView<?> parent,    
                    View view, int position, long id)    
            {    
                if(visflag)  
                {  
                    ViewHolder viewHolder = (ViewHolder) view.getTag();  
                    viewHolder.cb.toggle();  
                    if(viewHolder.cb.isChecked())  
                    {  
  
                        boolList.set(position, true);  
                    }else{  
                        boolList.set(position, false);  
                    }  
                }  
            }    
        });    
            
    }    
        
    
    private ArrayList<HashMap<String, Object>> readAllSMS1() {
    	
    
    	
    	mReader = new SMSReader();
    	int cnt = mReader.read(this);
    	 
    	 Log.v("cnt",""+cnt);
    	 Log.v("mReader",""+mReader);
    	 ReadSMS.model_flag=2;
    	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>> ();    	
    	for(int i = 0; i < cnt; ++i) {
   			HashMap<String, Object> item = new HashMap<String, Object>();
   			msms = mReader.get(i);
   			mcontact = mReader.getContact(this, msms);
   			 Log.v("mcontact",""+mcontact);
//   			 int person_image;
//   			 
//   			person_image = R.drawable.man;
   			
   			//item.put("person_image",person_image);
   			//Log.v("person_image",""+person_image);
   			if(mcontact == null) {
   				item.put("person", msms.mAddress);
   			}
   			else {
   				item.put("person", mcontact.mName);
   			}
   			
   			


   			
//   			Date date=new Date(msms.mDate);
//   			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//   			String formalDate=format.format(date);

   			
   			//item.put("data", left(msms.mBody.toString(),2600)+"  ���ڣ�"+formalDate);
   			Log.v("item",""+item);
   			list.add(item);
    	}
    	return list;
    }
    
    private void fillListView() {
    	//listview = (ListView) findViewById(R.id.sms_list);

    		list = readAllSMS1();



    	
    	listItemAdapter = new SimpleAdapter(this, list,    
                R.layout.listitems, 
//                new String[] {"person_image","person","data"},    
//                new int[] {R.id.textView_network,R.id.textView_body, R.id.textView_date}  
    	 new String[] {"person"},    
         new int[] {R.id.textView}   
            ); 
    	
    	lv.setAdapter(listItemAdapter);
    }
    
    private static String left(String str, int byte_len) {  
        if (str.getBytes().length < byte_len) {  
            return str;  
        }  
   
        String substr = str;  
        while (substr.getBytes().length > byte_len) {  
            substr = substr.substring(0, substr.length() - 1);  
        }  
        return substr;  
    }  
    
    class ListVIewAdapter extends BaseAdapter    
    {    
            
        Context c;    
        LayoutInflater mInflater ;    
        ListVIewAdapter(Context context)    
        {    
            c = context;    
            mInflater = getLayoutInflater();    
        }    
        @Override    
        public int getCount()    
        {    
            return strList.size();    
        }    
    
        @Override    
        public Object getItem(int position)    
        {    
            return strList.get(position);    
        }    
    
        @Override    
        public long getItemId(int position)    
        {    
            return position;    
        }    
            
    
        @Override    
        public View getView(final int position, View convertView,    
                ViewGroup parent)    
        {    
            ViewHolder holder = null ;    
            if(convertView == null)    
            {    
                  
                holder = new ViewHolder();  
                convertView  = mInflater.inflate(R.layout.list_item, null);  
                holder.tv = (TextView)convertView.findViewById(R.id.textView);    
                holder.cb = (CheckBox)convertView.findViewById(R.id.checkBox);  
                convertView.setTag(holder);  
                  
            }else{  
                holder = (ViewHolder) convertView.getTag();  
            }   
            holder.tv.setText(strList.get(position));    
            holder.cb.setChecked(boolList.get(position));    
                
            if(visflag)    
            {    
                holder.cb.setVisibility(View.VISIBLE);    
            }    
            else    
            {    
                holder.cb.setVisibility(View.INVISIBLE);    
            }    
            
            return convertView;    
        }    
        class ViewHolder    
        {    
            TextView tv;    
            CheckBox cb;    
        }    
            
    }    
    
    @Override    
    public boolean onCreateOptionsMenu(Menu menu)    
    {    
            
        menu.add(0, 0, 0, "批量处理");    
        menu.add(0, 1, 0, "确定删除");    
        return super.onCreateOptionsMenu(menu);    
    }    
    
    @Override    
    public boolean onOptionsItemSelected(MenuItem item)    
    {    
        switch(item.getItemId())    
        {    
            case 0:  // ��������    
                {    
                    if(visflag)    
                    {    
                        visflag = false;    
                        for(int i=0; i<boolList.size();i++)  
                        {  
                            boolList.set(i, false);  
                        }  
                    }    
                    else    
                    {    
                        visflag = true;    
                    }    
                    this.ladapter.notifyDataSetInvalidated();    
                    break;    
                }    
            case 1: //ȷ��ɾ��    
                {    
                    if(boolList.size()>0)    
                    {    
                        if(visflag)  
                        {  
                            for(int location=0; location<boolList.size(); )  
                            {  
                                if(boolList.get(location))  
                                {  
                                    boolList.remove(location);  
                                    strList.remove(location);  
                                    continue;  
                                }  
                                location++;  
                            }  
                        }  
                            
                    }  
                    this.ladapter.notifyDataSetChanged();  
                    break;    
                }    
        }    
        return super.onOptionsItemSelected(item);    
    }    
        
        

}
