package com.zynar.starvoca.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.CustomListWordsSelectItemBinding;
import com.zynar.starvoca.words.WordsItem;

import java.util.ArrayList;
import java.util.List;

public class WordsCsvRvAdapter extends RecyclerView.Adapter<WordsCsvRvAdapter.ViewHolder> {

    private CustomListWordsSelectItemBinding mBinding;
    private List<WordsItem> wordsItemList;
    private Context context;

    public WordsCsvRvAdapter(Context context, List<WordsItem> wordsItemList) {
        this.context = context;
        this.wordsItemList = wordsItemList;
    }

    @NonNull
    @Override
    public WordsCsvRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = CustomListWordsSelectItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull WordsCsvRvAdapter.ViewHolder holder, int position) {
        WordsItem wordsItem = wordsItemList.get(position);
        mBinding.tvWord.setText(wordsItem.getWord());
        mBinding.tvMeaning.setText(wordsItem.getMeaning());
        mBinding.tvPronunciation.setText(wordsItem.getPronunciation());
        mBinding.tvMemo.setText(wordsItem.getMemo());
        mBinding.tvLanguage.setText(wordsItem.getLanguage());

    }

    @Override
    public int getItemCount() {
        return wordsItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
