package com.zynar.starvoca.words;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zynar.starvoca.R;
import com.zynar.starvoca.vocabulary.VocaDao;
import com.zynar.starvoca.vocabulary.VocaDatabase;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.vocabulary.VocaRVAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordsChbRVAdapter extends RecyclerView.Adapter<WordsChbRVAdapter.ViewHolder> {

    // 단어장들
    private List<VocaItem> vocaItems;
    private VocaDatabase vocaDatabase;
    private VocaDao vocaDao;
    private ArrayList<WordsItem> wordsItems;
    private Context context;
    private WordsItem wordsItem;
    //
    private SparseBooleanArray sparseBooleanArray;
    int parentPos;

    public WordsChbRVAdapter(List<VocaItem> vocaItems, ArrayList<WordsItem> wordsItems, Context context, int i) {
        this.vocaItems = vocaItems;
        this.context = context;
        this.wordsItems = wordsItems;
        this.parentPos = i;
        sparseBooleanArray = new SparseBooleanArray();
        vocaDatabase = VocaDatabase.getInstance(context);
        vocaDao = vocaDatabase.vocaDao();
    }


    @NonNull
    @Override
    public WordsChbRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_chb_voca_item, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull WordsChbRVAdapter.ViewHolder holder, int position) {
        int pos = position;
        wordsItem = wordsItems.get(parentPos);
        // 리사이클러뷰에 있는 치크 박스의 타이틀을 단어장으로 만듬
        holder.chb_voca.setText(vocaItems.get(position).getVoca());

        // 단어장에 해당 단어가 들어있는지 체크
        List<Integer> wordsId = new Gson().fromJson(vocaItems.get(position).getWordsId(), new TypeToken<List<Integer>>(){}.getType());

        sparseBooleanArray.append(vocaItems.get(position).getId(), false);
        for(int i : wordsId) {
            if(i == wordsItem.getId()) {
                // 단어장에 해당 단어가 들어있으면 체크하고 리스트에 추가
                sparseBooleanArray.put(vocaItems.get(position).getId(), true);
                holder.chb_voca.setChecked(sparseBooleanArray.get(vocaItems.get(position).getId()));
                break;
            }
        }
        holder.chb_voca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.chb_voca.isChecked()){
                    sparseBooleanArray.put(vocaItems.get(pos).getId(), false);
                } else {
                    sparseBooleanArray.put(vocaItems.get(pos).getId(), true);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return vocaItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox chb_voca;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chb_voca = itemView.findViewById(R.id.chb_voca);
        }
    }

    public void getSparseBooleanArray() {


        int i = sparseBooleanArray.size();
        for(int u = 0; u<i; u++) {
            int vocaId = sparseBooleanArray.keyAt(u);
            int pos = sparseBooleanArray.indexOfKey(vocaId);
            Boolean isChecked = sparseBooleanArray.get(vocaId);
            // 내림차순이라서 i-pos-1로 해야함 시발 이거때문에 ㅈㄴ 고생함
            VocaItem vocaItem = vocaItems.get(i-pos-1);

            List<Integer> wordsId = new Gson().fromJson(vocaItem.getWordsId(), new TypeToken<List<Integer>>(){}.getType());

            // 체크되어있고 리스트에 wordId가 없으면 add
            if(isChecked == true && !wordsId.contains(wordsItem.getId())) {
                wordsId.add(wordsItem.getId());
            } else if (isChecked == false) {
                wordsId.remove(Integer.valueOf(wordsItem.getId()));
            }

            vocaItem.setWordsId(new Gson().toJson(wordsId));
            vocaDao.updateVoca(vocaItem);
            notifyItemChanged(pos);
            notifyItemRemoved(pos);

        }

    }

}
