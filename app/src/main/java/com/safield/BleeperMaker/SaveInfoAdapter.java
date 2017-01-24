package com.safield.BleeperMaker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

public class SaveInfoAdapter extends BaseAdapter {

    ArrayList<ToneMaker.SaveInfo> saveInfos;
    LayoutInflater layoutInflater;
    Context ctx;
    android.text.format.DateFormat dateFormat;

    public SaveInfoAdapter(Context ctx , ArrayList<ToneMaker.SaveInfo> saveInfos) {
        this.saveInfos = saveInfos;
        this.ctx =  ctx;
        layoutInflater = LayoutInflater.from(ctx);
        dateFormat = new android.text.format.DateFormat();
    }

    @Override
    public int getCount() {
        return saveInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return saveInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.loadfile_listview_item, null);

        ToneMaker.SaveInfo saveInfo = saveInfos.get(i);
        ((TextView)convertView.findViewById(R.id.saveInfoNameText)).setText(saveInfo.name);
        ((TextView)convertView.findViewById(R.id.saveInfoDateText)).setText(dateFormat.format("yyyy-MM-dd hh:mm:ss" , saveInfo.lastModified));

        return convertView;
    }
}
