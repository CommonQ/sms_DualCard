package edu.bupt.mms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//import com.android.calendarcommon.ICalendar;

import edu.bupt.mms.ui.AgendaMultiDeleteAdapter;
import edu.bupt.mms.ui.AgendaMultiDeleteAdapter.ViewHolder;
import edu.bupt.mms.R;
//import edu.bupt.calendar.Utils;


import android.R.menu;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxActivity extends Activity {
	private ListView lv;
	private AgendaMultiDeleteAdapter mAdapter;
	private ArrayList<Map<String, String>> list;
	private Button bt_delete;
	private int checkNum; // ��¼ѡ�е���Ŀ����
	private ArrayList<Long> idlist;
	private Map<String, String> map;
	ActionBar actionBar;
	Context context;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agenda_multi_select);
		actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("选择日历日程");
		actionBar.setDisplayHomeAsUpEnabled(true);

		/* ʵ������ؼ� */
		lv = (ListView) findViewById(R.id.lv);
		bt_delete = (Button) findViewById(R.id.bt_delete);
		list = new ArrayList<Map<String, String>>();
		idlist = new ArrayList<Long>();

		// ΪAdapter׼�����
		initDate();
		// ʵ���Զ����MyAdapter
		mAdapter = new AgendaMultiDeleteAdapter(list, this);
		// ��Adapter
		lv.setAdapter(mAdapter);

		// ��listView�ļ�����
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// ȡ��ViewHolder���������ʡȥ��ͨ�����findViewByIdȥʵ��������Ҫ��cbʵ��Ĳ���
				ViewHolder holder = (ViewHolder) arg1.getTag();
				
				// �ı�CheckBox��״̬
				holder.cb.toggle();
				// ��CheckBox��ѡ��״����¼����
				AgendaMultiDeleteAdapter.getIsSelected().put(arg2,
						holder.cb.isChecked());
				
				// ����ѡ����Ŀ
				if (holder.cb.isChecked() == true) {
					checkNum++;

				} else {
					checkNum--;
				}
			}
		});
		// ��ɾ��ť�ļ�����
		bt_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkNum == 0){
					
				}
				else{
					new AlertDialog.Builder(InboxActivity.this)
					.setTitle("选择日历日程")
					.setMessage("确定要选择这" + checkNum +"条日程吗？")
                    .setIconAttribute(android.R.attr.alertDialogIcon)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Set<Integer> key = AgendaMultiDeleteAdapter
											.getIsSelected().keySet();
									Iterator<Integer> keySetIterator = key
											.iterator();
									long id = 0;
									int len = 0;
									StringBuffer test = new StringBuffer();
									while (keySetIterator.hasNext()) {
										Integer e = keySetIterator.next();
										if (AgendaMultiDeleteAdapter
												.getIsSelected().get(e) == true) {
											
											
											
											
											if(!list.get(e).get("title").equals("")){
											test.append("事件： "+list.get(e).get("title"));
											}
											
											if(!list.get(e).get("year").equals("")){
											test.append(" 时间： "+list.get(e).get("year"));
											}
											
											
											if(!list.get(e).get("time").equals("")){
											
												test.append(" "+list.get(e).get("time"));
											}
												
											if(!list.get(e).get("location").equals("")){
											
												test.append(" 地点："+list.get(e).get("location"));
											}
											
											
											if(!list.get(e).get("description").equals("")){
												test.append(" 描述：  "+list.get(e).get("description")+"\n");
											}
											
										//	Toast.makeText(InboxActivity.this, test.toString(), Toast.LENGTH_LONG).show();
											
//											list.remove(e - len);
//											id = idlist.get(e - len);
//											Log.i("e", Long.toString(e));
//											Log.i("id", Long.toString(id));
//											calendarDelete(id);
//											idlist.remove(e - len);
//											AgendaMultiDeleteAdapter
//													.getIsSelected().put(e,
//															false);
//											checkNum--;
//											len++;

										}
									}
									
									
									
									
									
									Intent intent = getIntent();
									
									intent.putExtra("Calendar", test.toString());
									
									
									Log.v("PhraseTemplateActivity", test.toString());
									InboxActivity.this.setResult(RESULT_OK, intent);
									
									
									InboxActivity.this.finish();
									
									
									
									
									
									
									
									
									
									// Log.i("list", list.toString());
									// Log.i("getIsSelected",
									// mAdapter.getIsSelected().toString());
									// tv_show.setText("��ѡ��0��");
									dataChanged();

								}
							}).setNegativeButton(android.R.string.cancel, null).show();
				} 


			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		// ��Ӳ˵���
		MenuItem add = menu.add(0, 0, 0, "全选");
		MenuItem del = menu.add(0, 1, 1, "反选");
		add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		del.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case 0:
			Log.i("0", "000000000000000");
			for (int i = 0; i < list.size(); i++) {
				AgendaMultiDeleteAdapter.getIsSelected().put(i, true);
			}
			// ������Ϊlist�ĳ���
			checkNum = list.size();
			// ˢ��listview��TextView����ʾ
			dataChanged();
			break;
		case 1:
			Log.i("1", "1111111111111111");
			for (int i = 0; i < list.size(); i++) {
				if (AgendaMultiDeleteAdapter.getIsSelected().get(i)) {
					AgendaMultiDeleteAdapter.getIsSelected().put(i, false);
					checkNum--;
				} else {
					AgendaMultiDeleteAdapter.getIsSelected().put(i, true);
					checkNum++;
				}
			}
			// ˢ��listview��TextView����ʾ
			dataChanged();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void calendarDelete(long id) {
		// TODO Auto-generated method stub
		Cursor curde = getContentResolver().query(Events.CONTENT_URI, null,
				null, null, null);
		while (curde.moveToNext()) {
			Uri deleteUri = null;
			deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
			getContentResolver().delete(deleteUri, null, null);
		}
	}

	// ��ʼ�����
	private void initDate() {
		Cursor cur = getContentResolver().query(Events.CONTENT_URI, null, null,
				null, "dtstart");
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
			long calID = 0;
			String title = null;
			String temp, temp1 = null;
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			
			String location =new String();
			String description=new String();
			
			map = new HashMap<String, String>();
			long stime = 0;
			long etime = 0;

			long rruletime = 0;
			String year;

			String rrule = new String();
			char rrulechar;

			// Get the field values
			calID = cur.getLong(cur
					.getColumnIndexOrThrow(CalendarContract.Events._ID));
			Log.i("calID", String.valueOf(calID));
			idlist.add(calID);

			title = cur.getString(cur
					.getColumnIndexOrThrow(CalendarContract.Events.TITLE));
			Log.i("title", title);
			map.put("title", title);

			
			location =cur.getString(cur.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION));
			
			if(location==null){
				location="";
				
			}
			
			
			map.put("location", location);
			
			
			description=cur.getString(cur.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION));
			
			
			if(description==null){
				description="";
				
			}
			

			map.put("description", description);
			
			
			
			stime = cur.getLong(cur
					.getColumnIndexOrThrow(CalendarContract.Events.DTSTART));
			temp = String
					.valueOf(new SimpleDateFormat("a hh:mm").format(stime));
			sb1.append(temp);
			sb1.append(" - ");
			Log.i("temp", temp);

			etime = cur.getLong(cur
					.getColumnIndexOrThrow(CalendarContract.Events.DTEND));
			temp = String
					.valueOf(new SimpleDateFormat("a hh:mm").format(etime));
			sb1.append(temp);
			map.put("time", sb1.toString());
			
			
			
			
			
		
			
		
				// TODO: handle exception
				temp = String.valueOf(new SimpleDateFormat("yyyy年MM月dd日")
						.format(etime));
				map.put("year", temp);
				Log.i("rrule", "rrule is null");
			
			
				

			list.add(map);
			Log.i("temp", temp);
			Log.i("segmentation", "---------------------------------------");
		}
		cur.close();
		Log.i("list", list.toString());
		Log.i("segmentation", "---------------------------------------");
		Log.i("idlist", String.valueOf(idlist));
	}

	// ˢ��listview��TextView����ʾ
	private void dataChanged() {
		// ֪ͨlistViewˢ��
		mAdapter.notifyDataSetChanged();
		// TextView��ʾ���µ�ѡ����Ŀ
	}

}
