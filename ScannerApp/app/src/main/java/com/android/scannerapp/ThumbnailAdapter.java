package com.android.scannerapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThumbnailAdapter extends ArrayAdapter<File> {
    Context context;
    ArrayList<File> al_img;
    ViewHolder viewHolder;

    public ThumbnailAdapter(Context context, ArrayList<File> al_img)
    {
        super(context,R.layout.row_thumbnail,al_img);
        this.context = context;
        this.al_img = al_img;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(al_img.size() > 0)
        {
            return al_img.size();
        }
        else
        {
            return 1;
        }
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
       if(view == null){
           view = LayoutInflater.from(getContext()).inflate(R.layout.row_thumbnail,parent,false);
           viewHolder = new ViewHolder();
           viewHolder.tv_filename = (TextView) view.findViewById(R.id.txtName);


           view.setTag(viewHolder);
       }
       else
       {
           viewHolder = (ViewHolder)view.getTag();
       }
       viewHolder.tv_filename.setText(al_img.get(position).getName());
      return view;
    }

    public class ViewHolder
    {
        TextView tv_filename;

    }
}
