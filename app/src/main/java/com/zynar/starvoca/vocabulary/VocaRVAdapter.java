package com.zynar.starvoca.vocabulary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zynar.starvoca.R;

import java.util.List;

public class VocaRVAdapter extends RecyclerView.Adapter<VocaRVAdapter.ViewHolder> {

    private final List<VocaItem> vocaItems;
    private final Context context;

    public VocaRVAdapter(List<VocaItem> vocaItems, Context context) {
        this.vocaItems = vocaItems;
        this.context = context;

    }

    @NonNull
    @Override
    public VocaRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_voca_item, parent, false);
        return new ViewHolder(holder);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VocaRVAdapter.ViewHolder holder, int position) {
        // 레이아웃 값 설정
        holder.tv_voca.setText(vocaItems.get(position).getVoca());
        holder.tv_explanation.setText(vocaItems.get(position).getExplanation());
        List<Integer> wordsId = new Gson().fromJson(vocaItems.get(position).getWordsId(), new TypeToken<List<Integer>>(){}.getType());
        holder.tv_voca_count.setText(Integer.toString(wordsId.size()));
    }

    @Override
    public int getItemCount() {
        return vocaItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_voca;
        private final TextView tv_explanation;
        private final TextView tv_voca_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_voca = itemView.findViewById(R.id.tv_voca);
            tv_explanation = itemView.findViewById(R.id.tv_explanation);
            ToggleButton tgb_favorites = itemView.findViewById(R.id.tgb_favorites);
            ImageButton imb_edit = itemView.findViewById(R.id.imb_edit);
            tv_voca_count = itemView.findViewById(R.id.tv_voca_count);

            imb_edit.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                VocaItem vocaItem = vocaItems.get(pos);

                String[] strChoiceItems = {context.getString(R.string.voca_info_edit), context.getString(R.string.voca_delete), context.getString(R.string.voca_share)};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.voca_sel_question);
                builder.setItems(strChoiceItems, (dialogInterface, i) -> {
                    if (i == 0) {
                        // 수정하기
                        Intent intent = new Intent(context, VocaAddActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("position", pos);
                        context.startActivity(intent);
                    } else if (i == 1) {
                        // 삭제하기
                        VocaDatabase vocaDatabase = VocaDatabase.getInstance(context);
                        VocaDao vocaDao = vocaDatabase.vocaDao();
                        vocaDao.deleteVoca(vocaItems.get(pos));
                        vocaItems.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(context, "목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    } else if (i == 2) {
                        // 단어장 공유
                    }
                });
                builder.show();
            });

            // 단어장 클릭
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, VocaReadActivity.class);
                intent.putExtra("parentPos", getAbsoluteAdapterPosition());
                context.startActivity(intent);
            });
        }
    }
}
