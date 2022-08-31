package com.zynar.starvoca.words;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zynar.starvoca.R;

public class WordsItemDecoration extends RecyclerView.ItemDecoration {
    private int size;

    public WordsItemDecoration(Context context) {
        this.size = context.getResources().getDimensionPixelSize(R.dimen.layout_margin);;
    }

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = size;
        //outRect.top = size;
    }
}
