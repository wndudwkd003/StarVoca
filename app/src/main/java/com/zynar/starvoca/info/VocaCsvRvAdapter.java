package com.zynar.starvoca.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zynar.starvoca.databinding.CustomListVocaSelectItemBinding;
import com.zynar.starvoca.databinding.CustomListWordsSelectItemBinding;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.util.List;

public class VocaCsvRvAdapter extends RecyclerView.Adapter<VocaCsvRvAdapter.ViewHolder> {

    private CustomListVocaSelectItemBinding mBinding;
    private List<VocaItem> vocaItemList;
    private Context context;

    public VocaCsvRvAdapter(Context context, List<VocaItem> vocaItemList) {
        this.context = context;
        this.vocaItemList = vocaItemList;
    }

    @NonNull
    @Override
    public VocaCsvRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = CustomListVocaSelectItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull VocaCsvRvAdapter.ViewHolder holder, int position) {
        VocaItem vocaItem = vocaItemList.get(position);
        mBinding.tvVoca.setText(vocaItem.getVoca());

    }

    @Override
    public int getItemCount() {
        return vocaItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
