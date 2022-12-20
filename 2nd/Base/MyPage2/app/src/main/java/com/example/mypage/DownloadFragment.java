package com.example.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mypage.databinding.FragmentDownloadLayoutBinding;

public class DownloadFragment extends Fragment {

    private FragmentDownloadLayoutBinding binding;
    private DownloadViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadLayoutBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(DownloadViewModel.class);

        viewModel.addDownloadStatusListener();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel.removeDownloadStatusListener();
    }
}
