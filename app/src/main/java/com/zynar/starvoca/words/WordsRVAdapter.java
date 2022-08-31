package com.zynar.starvoca.words;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.zynar.starvoca.R;
import com.zynar.starvoca.vocabulary.VocaItem;

import java.util.ArrayList;
import java.util.List;

public class WordsRVAdapter extends RecyclerView.Adapter<WordsRVAdapter.ViewHolder> {

    // 아이템
    private ArrayList<WordsItem> wordsItems;
    private List<VocaItem> vocaItems;
    private Context context;
    private WordsDBHelper wordsDBHelper;
    private WordsItemDecoration wordsItemDecoration;
    private int condition;

    public WordsRVAdapter(ArrayList<WordsItem> wordsItems, List<VocaItem> vocaItems, Context context, int i) {
        this.wordsItems = wordsItems;
        this.context = context;
        this.vocaItems = vocaItems;
        this.wordsDBHelper = new WordsDBHelper(context);
        this.condition = i;
    }

    @NonNull
    @Override
    public WordsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 홀더 연결
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_words_item, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull WordsRVAdapter.ViewHolder holder, int position) {
        // 연결된 레이아웃에 값 설정
        holder.tv_word.setText(wordsItems.get(position).getWord());
        holder.tv_meaning.setText(wordsItems.get(position).getMeaning());
        holder.tv_pronunciation.setText(wordsItems.get(position).getPronunciation());
        holder.tv_memo.setText(wordsItems.get(position).getMemo());
        holder.tv_language.setText(wordsItems.get(position).getLanguage());

        if(wordsItems.get(position).getCondition() == 0) {
            holder.tv_condition.setText(R.string.words_condition_hard);
        } else if(wordsItems.get(position).getCondition() == 1) {
            holder.tv_condition.setText(R.string.words_condition_usually);
        } else if(wordsItems.get(position).getCondition() == 2) {
            holder.tv_condition.setText(R.string.words_condition_easy);
        }

        // 발음과 메모 없으면 숨기기
        if(holder.tv_pronunciation.length() == 0) holder.tv_pronunciation.setVisibility(View.GONE);
        if(holder.tv_memo.getText().length() == 0) holder.tv_memo.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return wordsItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // 클릭 플래그
        private boolean meaning_view_flag = false;

        // 뷰 홀더로 레이아웃 연결
        private TextView tv_word;
        private TextView tv_meaning;
        private TextView tv_pronunciation;
        private TextView tv_memo;
        private TextView tv_condition;
        private TextView tv_language;
        private ImageButton imb_edit;
        private RecyclerView rv_check_box;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_word = itemView.findViewById(R.id.tv_word);
            tv_meaning = itemView.findViewById(R.id.tv_meaning);
            tv_pronunciation = itemView.findViewById(R.id.tv_pronunciation);
            tv_memo = itemView.findViewById(R.id.tv_memo);
            tv_condition = itemView.findViewById(R.id.tv_condition);
            tv_language = itemView.findViewById(R.id.tv_language);
            imb_edit = itemView.findViewById(R.id.imb_edit);

            if (condition == 1) imb_edit.setVisibility(View.INVISIBLE);

            imb_edit.setOnClickListener(new View.OnClickListener() {
                // 수정 버튼 클릭
                @Override
                public void onClick(View view) {
                    // 아이템 위치
                    int pos = getAbsoluteAdapterPosition();
                    // 해당 아이템 불러오기
                    WordsItem wordsItem = wordsItems.get(pos);

                    // 선택지 만들기 수정하기, 삭제하기
                    String[] strChoiceItems = {context.getString(R.string.WordsAddToVoca), context.getString(R.string.edit), context.getString(R.string.delete)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.words_sel_question);
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                Dialog dialog = new Dialog(view.getContext(), android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.custom_dialog_words_chb);
                                dialog.setTitle(R.string.word_add_to_voca);
                                rv_check_box = dialog.findViewById(R.id.rv_check_box);
                                WordsChbRVAdapter wordsChbRVAdapter = new WordsChbRVAdapter(vocaItems, wordsItems, context, pos);
                                wordsItemDecoration = new WordsItemDecoration(context);
                                rv_check_box.addItemDecoration(wordsItemDecoration);
                                rv_check_box.setHasFixedSize(true);
                                rv_check_box.setAdapter(wordsChbRVAdapter);
                                Button btn_save = dialog.findViewById(R.id.btn_save);
                                btn_save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.onBackPressed();
                                        wordsChbRVAdapter.getSparseBooleanArray();
                                        Toast.makeText(context, "단어장이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.show();
                            }
                            else if(i == 1) {
                                // 수정하기

                                Intent intent = new Intent(context, WordsAddActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("position", pos);
                                context.startActivity(intent);

                            } else if(i == 2) {
                                // 삭제하기
                                wordsDBHelper.deleteWords(wordsItem.getId());
                                wordsItems.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(context, "목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
            });


            // 리사이클러뷰 아이템 클릭
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(meaning_view_flag == false) {
                        tv_meaning.setVisibility(view.VISIBLE);
                        meaning_view_flag = true;
                    } else {
                        tv_meaning.setVisibility(view.GONE);
                        meaning_view_flag = false;
                    }


                }
            });
        }
    }


}
