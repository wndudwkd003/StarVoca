package com.zynar.starvoca.words;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.R;
import com.zynar.starvoca.vocabulary.VocaItem;

import java.util.List;
import java.util.Locale;

public class WordsRVAdapter extends RecyclerView.Adapter<WordsRVAdapter.ViewHolder> {

    private List<WordsItem> wordsItems;
    private List<VocaItem> vocaItems;
    private final Context context;
    private final int condition;
    TextToSpeech tts;
    public WordsRVAdapter(Context context, int i) {
        this.context = context;
        this.condition = i; // 내 단어 탭에서 실행 0, 단어장에서 실행 1
    }

    @NonNull
    @Override
    public WordsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_words_item, parent, false);
        return new ViewHolder(holder);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWordsItems(List<WordsItem> wordsItems) {
        this.wordsItems = wordsItems;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVocaItems(List<VocaItem> vocaItems) {
        this.vocaItems = vocaItems;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull WordsRVAdapter.ViewHolder holder, int position) {
        // 연결된 레이아웃에 값 설정
        holder.tv_word.setText(wordsItems.get(position).getWord());
        holder.tv_meaning.setText(wordsItems.get(position).getMeaning());
        holder.tv_pronunciation.setText(wordsItems.get(position).getPronunciation());
        holder.tv_memo.setText(wordsItems.get(position).getMemo());
        holder.tv_language.setText(wordsItems.get(position).getLanguage());

        if (wordsItems.get(position).getCondition() == 0) {
            holder.tv_condition.setText(R.string.words_condition_hard);
        } else if (wordsItems.get(position).getCondition() == 1) {
            holder.tv_condition.setText(R.string.words_condition_usually);
        } else if (wordsItems.get(position).getCondition() == 2) {
            holder.tv_condition.setText(R.string.words_condition_easy);
        }

        // 발음과 메모 없으면 숨기기
        if (holder.tv_pronunciation.length() == 0) holder.tv_pronunciation.setVisibility(View.GONE);
        if (holder.tv_memo.getText().length() == 0) holder.tv_memo.setVisibility(View.GONE);

        // 사운드 버튼 클릭시
        holder.imb_sound.setOnClickListener(v -> {
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) { // OnInitListener를 통해서 TTS 초기화
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.KOREA); // TTS언어 한국어로 설정

                        if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                            Log.e("TTS", "This Language is not supported");
                        } else {
                            speakOut(position);// onInit에 음성출력할 텍스트를 넣어줌
                        }
                    } else {
                        Log.e("TTS", "Initialization Failed!");
                    }
                }
            });
        });
    }

    private void speakOut(int i){
        CharSequence text = wordsItems.get(i).getWord();
        tts.setPitch((float)1); // 음성 톤 높이 지정

        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String chatSpeed = preferences.getString("sound_play", "1.0");

        tts.setSpeechRate(Float.parseFloat(chatSpeed)); // 음성 속도 지정

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @Override
    public int getItemCount() {
        return wordsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 클릭 플래그
        private boolean meaning_view_flag = false;

        private final TextView tv_word;
        private final TextView tv_meaning;
        private final TextView tv_pronunciation;
        private final TextView tv_memo;
        private final TextView tv_condition;
        private final TextView tv_language;
        private final ImageButton imb_edit, imb_sound;
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
            imb_sound = itemView.findViewById(R.id.imb_play_sound);

            if (condition == 1) imb_edit.setVisibility(View.INVISIBLE);

            // 수정 버튼 클릭
            imb_edit.setOnClickListener(view -> {
                // 아이템 위치
                int pos = getAbsoluteAdapterPosition();
                // 해당 아이템 불러오기
                WordsItem wordsItem = wordsItems.get(pos);

                // 선택지 만들기 수정하기, 삭제하기
                String[] strChoiceItems = {context.getString(R.string.WordsAddToVoca), context.getString(R.string.edit), context.getString(R.string.delete)};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.words_sel_question);
                builder.setItems(strChoiceItems, (dialogInterface, i) -> {
                    if (i == 0) {
                        Dialog dialog = new Dialog(view.getContext());
                        dialog.setContentView(R.layout.custom_dialog_words_chb);
                        rv_check_box = dialog.findViewById(R.id.rv_check_box);
                        WordsChbRVAdapter wordsChbRVAdapter = new WordsChbRVAdapter(vocaItems, wordsItems, context, pos);
                        WordsItemDecoration wordsItemDecoration = new WordsItemDecoration(context);
                        rv_check_box.addItemDecoration(wordsItemDecoration);
                        rv_check_box.setHasFixedSize(true);
                        rv_check_box.setAdapter(wordsChbRVAdapter);
                        TextView tv_save = dialog.findViewById(R.id.tv_save);
                        tv_save.setOnClickListener(view1 -> {
                            dialog.onBackPressed();
                            wordsChbRVAdapter.getSparseBooleanArray();
                            Toast.makeText(context, "단어장이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                        dialog.show();
                    } else if (i == 1) {
                        // 수정하기
                        Intent intent = new Intent(context, WordsAddActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("position", pos);
                        context.startActivity(intent);

                    } else if (i == 2) {
                        // 삭제하기

                        AppDatabase db = AppDatabase.getInstance(context);

                        List<VocaItem> vocaItemList = db.vocaDao().getVocaItems();
                        for (VocaItem vocaItem : vocaItemList) {
                            List<Integer> wordsId = new Gson().fromJson(vocaItem.getWordsId(), new TypeToken<List<Integer>>() {
                            }.getType());
                            wordsId.remove(Integer.valueOf(wordsItem.getId()));
                            vocaItem.setWordsId(new Gson().toJson(wordsId));
                            db.vocaDao().updateVoca(vocaItem);
                        }


                        db.wordsDao().deleteWords(wordsItem);
                        wordsItems.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(context, "목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            });

            // 리사이클러뷰 아이템 클릭
            itemView.setOnClickListener(view -> {
                if(!meaning_view_flag) {
                    tv_meaning.setVisibility(View.VISIBLE);
                    meaning_view_flag = true;
                } else {
                    tv_meaning.setVisibility(View.GONE);
                    meaning_view_flag = false;
                }

            });
        }
    }


}
