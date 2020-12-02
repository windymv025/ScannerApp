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


    public ThumbnailAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public ThumbnailAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ThumbnailAdapter(@NonNull Context context, int resource, @NonNull File[] objects) {
        super(context, resource, objects);
    }

    public ThumbnailAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull File[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ThumbnailAdapter(@NonNull Context context, int resource, @NonNull List<File> objects) {
        super(context, resource, objects);
    }

    public ThumbnailAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<File> objects) {
        super(context, resource, textViewResourceId, objects);
    }
}
