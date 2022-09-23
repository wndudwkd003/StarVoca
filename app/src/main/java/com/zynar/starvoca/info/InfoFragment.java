package com.zynar.starvoca.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zynar.starvoca.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ListView listView = (ListView) view.findViewById(R.id.list_myinfo);
        List<String> data = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        data.add("내 정보 수정");
        data.add("단어 슬롯 구매");
        data.add("CSV 파일 관리");
        data.add("앱 환경설정");
        data.add("문의하기");
        data.add("개인정보처리방침");
        data.add("로그인");
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4) {
                    inquiry();
                } else {
                    startActivity(new Intent(requireContext(), MyinfoActivity.class).putExtra("list-pos", i));
                }
            }
        });
    }
    private void inquiry() {
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"onheaven003@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "[Star-Voca 문의사항] 제목없음");
        email.putExtra(Intent.EXTRA_TEXT, "StarVoca 문의하기 입니다.\n상세히 작성해주시면 신속한 처리가 가능합니다.\n\n닉네임: \n날짜: "+time+"\n내용: ");
        startActivity(email);
    }
}