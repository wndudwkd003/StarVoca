package com.zynar.starvoca.info;

import android.content.Context;
import android.util.Log;
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

    /* 어댑터에 필요한 변수들 */
    private CustomListWordsSelectItemBinding mBinding;
    private List<WordsItem> wordsItemList;
    private Context context;

    /* 리스너 인터페이스 구현부 */
    public interface CheckBoxClickListener {
        void onClickCheckBox(int flag, int pos);
    }

    /* 체크박스 리스너 */
    private CheckBoxClickListener mCheckBoxClickListener;

    /* 어댑터 생성자 */
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

        /* 체크박스 리스너 */
        holder.checkBox.setOnClickListener(v -> {
            if(holder.checkBox.isChecked()) {
                // 체크가 되어 있음
                mCheckBoxClickListener.onClickCheckBox(1, position);
            }
            else {
                // 체크가 되어있지 않음
                mCheckBoxClickListener.onClickCheckBox(0, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordsItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    /* 리스너 메소드 */
    public void setOnCheckBoxClickListener(CheckBoxClickListener clickCheckBoxListener) {
        this.mCheckBoxClickListener = clickCheckBoxListener;
    }

}
