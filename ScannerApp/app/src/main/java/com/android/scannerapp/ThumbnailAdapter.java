package com.android.scannerapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    ArrayList<Thumbnail> thumbnails;
    Context context;

    public ThumbnailAdapter(ArrayList<Thumbnail> thumbnails, Context context) {
        this.thumbnails = thumbnails;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.row_thumbnail, parent,false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(thumbnails.get(position).getName());
        holder.txtDate.setText(thumbnails.get(position).getDay_create());
        holder.imgThumb.setImageResource(thumbnails.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return thumbnails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtDate;
        ImageView imgThumb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView)itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDateCreate);
            imgThumb = itemView.findViewById(R.id.imgThumb);
        }
    }
}
