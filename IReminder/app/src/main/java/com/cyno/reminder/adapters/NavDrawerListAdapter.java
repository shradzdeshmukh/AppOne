package com.cyno.reminder.adapters;


import java.util.ArrayList;

import com.cyno.reminder.models.NavDrawerItem;
import com.cyno.reminder.R;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

    private Context context;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        super(context ,R.layout.drawer_list_item , navDrawerItems);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            mHolder = new ViewHolder();
            mHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            mHolder.tvCount = (TextView) convertView.findViewById(R.id.counter);
            mHolder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(mHolder);
        }else{
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.ivIcon.setImageResource(getItem(position).getIcon());
        mHolder.tvTitle.setText(getItem(position).getTitle());

        String count = getItem(position).getCount();
        if(Integer.valueOf(count) == 0)
            mHolder.tvCount.setVisibility(View.GONE);
        else{
            mHolder.tvCount.setVisibility(View.VISIBLE);
            mHolder.tvCount.setText(count);
        }

        // displaying count
        // check whether it set visible or not
//        if(getItem(position).getCounterVisibility()){
            mHolder.tvCount.setText(getItem(position).getCount());
//        }else{
//            // hide the counter view
//            mHolder.tvCount.setVisibility(View.GONE);
//        }

        return convertView;
    }

    private static class ViewHolder{
        private TextView tvTitle;
        private TextView tvCount;
        private ImageView ivIcon;
    }
}
