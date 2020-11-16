package com.android.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ThumbnailAdapter extends BaseAdapter {

    Context myContext;
    int myLayout;
    List<Thumbnail> arrayThumbnail;

    public ThumbnailAdapter(Context context, int layout, List<Thumbnail> thumbnailList){
        myContext = context;
        myLayout = layout;
        arrayThumbnail = thumbnailList;
    }

    @Override
    public int getCount() {
        return arrayThumbnail.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(myLayout, null);

        //Anh xa va gan gia tri

        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        txtName.setText(arrayThumbnail.get(position).name);

        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDateCreate);
        txtDate.setText(arrayThumbnail.get(position).day_create);

        ImageView imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
        imgThumb.setImageResource(arrayThumbnail.get(position).Image);
        return convertView;
    }
}
