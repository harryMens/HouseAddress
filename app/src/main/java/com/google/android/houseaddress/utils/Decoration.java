package com.google.android.houseaddress.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class Decoration extends RecyclerView.ItemDecoration {
    int decoration;

    public Decoration(int decoration) {
        this.decoration = decoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = decoration;
    }
}