package edu.bupt.mms.ui;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.Map;

import edu.bupt.mms.R;
  
import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.CheckBox;  
import android.widget.ImageView;
import android.widget.TextView;  
  
public class AgendaMultiDeleteAdapter extends BaseAdapter {  
    // 填充数据的list  
    private ArrayList<Map<String,String>> list;  
    // 用来控制CheckBox的�?中状�? 
    private static HashMap<Integer, Boolean> isSelected;  
    // 上下�? 
    private Context context;  
    // 用来导入布局  
    private LayoutInflater inflater = null;  
  
    // 构�?�? 
    public AgendaMultiDeleteAdapter(ArrayList<Map<String, String>> list, Context context) {  
        this.context = context;  
        this.list = list;  
        inflater = LayoutInflater.from(context);  
        isSelected = new HashMap<Integer, Boolean>();  
        // 初始化数�? 
        initDate();  
    }  
  
    // 初始化isSelected的数�? 
    private void initDate() {  
        for (int i = 0; i < list.size(); i++) {  
            getIsSelected().put(i, false);  
        }  
    }  
  
    @Override  
    public int getCount() {  
        return list.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return list.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder = null;  
        if (convertView == null) {  
            // 获得ViewHolder对象  
            holder = new ViewHolder();  
            // 导入布局并赋值给convertview  
            convertView = inflater.inflate(R.layout.agendamultideleteadapter, null);  

            holder.iv = (ImageView) convertView.findViewById(R.id.bluebt);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);  
            holder.tt = (TextView) convertView.findViewById(R.id.item_tt); 
            holder.yy = (TextView) convertView.findViewById(R.id.item_yy); 
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);  
            // 为view设置标签  
            convertView.setTag(holder);  
        } else {  
            // 取出holder  
            holder = (ViewHolder) convertView.getTag();  
        }  
        // 设置list中TextView的显�? 
        
        holder.iv.setImageDrawable(context.getResources().getDrawable(R.drawable.bluebt));
        holder.tv.setText(list.get(position).get("title"));  
        holder.tt.setText(list.get(position).get("time"));
        holder.yy.setText(list.get(position).get("year"));  
        // 根据isSelected来设置checkbox的�?中状�? 
        holder.cb.setChecked(getIsSelected().get(position));  
        return convertView;  
    }  
  
    public static HashMap<Integer, Boolean> getIsSelected() {  
        return isSelected;  
    }  
  
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {  
        AgendaMultiDeleteAdapter.isSelected = isSelected;  
    }  
  
    public static class ViewHolder {  
    	ImageView iv;
        TextView tv;  
        TextView tt;
        TextView yy;
        CheckBox cb;  
    }  
}  