package com.zynar.starvoca.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zynar.starvoca.AppCSVSupport;
import com.zynar.starvoca.R;

public class CsvLoadApplyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_csv_load_apply, container, false);
    }

    private void loadCSV() {
        /* csv 로드 버튼 클릭 */
        AppCSVSupport appCSVSupport = new AppCSVSupport();
        appCSVSupport.setCsv(requireContext());

    }
}