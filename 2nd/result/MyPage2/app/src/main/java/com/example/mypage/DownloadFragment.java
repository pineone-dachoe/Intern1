package com.example.mypage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mypage.databinding.FragmentDownloadLayoutBinding;
import java.util.ArrayList;

public class DownloadFragment extends Fragment {

    private FragmentDownloadLayoutBinding binding;
    private DownloadViewModel viewModel;
    public ArrayList<DownloadDto> itemList = new ArrayList<>();
    DownloadAdapter downloadAdapter = new DownloadAdapter(itemList);

    int allcheckedCount;
    int downloadMode;
    int checkCount = 0;
    public static int ts = 0;
    public static String ad= null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadLayoutBinding.inflate(inflater, container, false);
        binding.recyclerView.setItemAnimator(null);

        viewModel = new ViewModelProvider(this).get(DownloadViewModel.class);
        viewModel.addDownloadStatusListener();

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        itemList = viewModel.getList();
        allcheckedCount = itemList.size();
        listCheck();
        downloadMode = 0;
        binding.recyclerView.setAdapter(downloadAdapter);
        downloadAdapter.setMode(0);
        downloadAdapter.setAdapter(itemList);
        binding.recyclerView.setLayoutManager(linearLayoutManager);




        final Observer<DownloadDto> progressObserver = new Observer<DownloadDto>() {
            @Override
            public void onChanged(@Nullable final DownloadDto DownloadDto) {
                if (downloadMode == 0) {
                    if(ad == null)
                    {
                        int o;
                        for(o=0;o<allcheckedCount;o++) {
                            if (DownloadDto.getContId() == itemList.get(o).getContId())
                            {
                                ad = DownloadDto.getContId();
                                ts = o;
                                Log.d("===",Integer.toString(ts));
                            }
                        }
                    }
                    itemList = viewModel.getList();
                    downloadAdapter.setAdapter(itemList);
                    downloadAdapter.notifyItemChanged(ts);

                    if(DownloadDto.getProgress()==100)
                    {
                        ad=null;
                    }
                }
            }
        };
        viewModel.getListStatus().observe(getViewLifecycleOwner(), progressObserver);

        final Observer<DownloadDto> comObserver = new Observer<DownloadDto>() {
            @Override
            public void onChanged(@Nullable final DownloadDto DownloadDto) {
                if (downloadMode == 0) {
                    itemList = viewModel.getList();
                    downloadAdapter.setAdapter(itemList);
                    int o;
                    for(o=0;o<allcheckedCount;o++) {
                        if (DownloadDto.getContId() == itemList.get(o).getContId())
                        {
                            downloadAdapter.notifyItemChanged(o);
                        }
                    }
                }
            }
        };
        viewModel.getcom().observe(getViewLifecycleOwner(), comObserver);

        //?????? ?????? ?????? ?????? ?????????
        binding.deleteModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAdapter.setMode(1);
                downloadMode = 1;
                listMode();
                deleteBtnStyleSet();
                binding.countText.setText("" + checkCount);
                binding.listText.setText("??? ??????(?????? " + itemList.size() + "???)");
                downloadAdapter.notifyDataSetChanged();
            }
        });
        //?????? ?????? ???????????? X?????? ?????? ?????????
        binding.backModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAdapter.setMode(0);
                downloadMode = 0;
                listMode();
                downloadAdapter.notifyDataSetChanged();
            }
        });
        //???????????? ?????? ???????????? ???????????? ?????? ?????? ?????????
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).moveTo(new MainFragment());
            }
        });
        //?????? ?????? ???????????? ?????? ?????????
        binding.allCheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.allCheck.isChecked()) {
                    checkCount = 0;
                    for (int i = 0; i < allcheckedCount; i++) {
                        itemList.get(i).setCheckStatus(true);
                    }

                    for (int t = 0; t < allcheckedCount; t++) {
                        if (itemList.get(t).getCheckStatus()) {
                            checkCount++;
                        }
                    }
                } else {
                    for (int i = 0; i < allcheckedCount; i++) {
                        itemList.get(i).setCheckStatus(false);
                    }
                    for (int t = 0; t < allcheckedCount; t++) {
                        if (!itemList.get(t).getCheckStatus()) {
                            checkCount--;
                        }
                    }
                }
                deleteBtnStyleSet();
                binding.countText.setText("" + checkCount);
                binding.listText.setText("??? ??????(?????? " + allcheckedCount + "???)");
                downloadAdapter.setAdapter(itemList);
                downloadAdapter.notifyDataSetChanged();
            }
        });
        //recyclerview ????????? ????????? ?????? ?????????
        downloadAdapter.setOnItemClickListener(new DownloadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (downloadMode == 1) {
                    if (!itemList.get(position).getCheckStatus()) {
                        itemList.get(position).setCheckStatus(true);
                    } else {
                        itemList.get(position).setCheckStatus(false);
                    }

                    if (itemList.get(position).getCheckStatus()) {
                        checkCount++;
                    } else {
                        checkCount--;
                    }

                    allChecked();
                    deleteBtnStyleSet();

                    downloadAdapter.setAdapter(itemList);
                    downloadAdapter.notifyItemChanged(position);

                    binding.countText.setText("" + checkCount);
                    binding.listText.setText("??? ??????(?????? " + allcheckedCount + "???)");
                }

            }
        });

        //recyclerview ????????? ????????? ????????? ???????????? ?????? ?????????
        downloadAdapter.setOnCheckClickListener(new DownloadAdapter.OnCheckClickListener() {
            @Override
            public void OnCheckClick(CheckBox c, int position) {
                if (downloadMode == 1) {
                    if (itemList.get(position).getCheckStatus()) {
                        checkCount++;
                    } else {
                        checkCount--;
                    }
                    allChecked();
                    deleteBtnStyleSet();
                    downloadAdapter.setAdapter(itemList);
                    downloadAdapter.notifyItemChanged(position);

                    binding.countText.setText("" + checkCount);
                    binding.listText.setText("??? ??????(?????? " + allcheckedCount + "???)");
                }
            }
        });

        //Close?????? ?????? ?????????
        downloadAdapter.setOnCloseBtnClickListener(new DownloadAdapter.OnCloseBtnClickListener() {
            @Override
            public void onCloseBtnClick(ImageButton v, int pos) {
                viewModel.removeDownload(itemList.get(pos));
                itemList = viewModel.getList();
                downloadAdapter.setAdapter(itemList);
                allcheckedCount = itemList.size();
                listCheck();
                downloadAdapter.notifyDataSetChanged();
            }
        });
        //Pause?????? ?????? ?????????
        downloadAdapter.setOnPauseBtnClickListener(new DownloadAdapter.OnPauseBtnClickListener() {
            @Override
            public void onPauseBtnClick(ImageButton v, int pos) {
                viewModel.pauseDownload(itemList.get(pos));
                itemList = viewModel.getList();
                downloadAdapter.setAdapter(itemList);
                downloadAdapter.notifyItemChanged(pos);
            }
        });
        //Play?????? ?????? ?????????
        downloadAdapter.setOnPlayBtnClickListener(new DownloadAdapter.OnPlayBtnClickListener() {
            @Override
            public void onPlayBtnClick(ImageButton v, int pos) {
                viewModel.startDownload(itemList.get(pos));
                downloadAdapter.setAdapter(itemList);
                downloadAdapter.notifyItemChanged(pos);
            }
        });
        //???????????? ?????? ?????? ?????????
        binding.deleteButton.setOnClickListener(view -> {
            CustomDialog dlg = new CustomDialog(getContext());
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dlg.show();
        });

        handleOnBackPressed();
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

    //????????? ????????? ??????
    public class CustomDialog extends Dialog {
        private Context mContext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //???????????? ?????? ??????????????? ????????? ??????
            setContentView(R.layout.custom_dialog_fragment);
            TextView dialogText = findViewById(R.id.dialogText);
            if (checkCount == allcheckedCount) {
                dialogText.setText("???????????? ????????? ?????? ???????????????.\n??????????????? ?????????????????????????");
            } else {
                dialogText.setText(checkCount + "?????? ???????????? ?????????????????????????");
            }

            //??????????????? "???"?????? ?????????
            Button okButtonn = findViewById(R.id.okbutton);
            okButtonn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkCount == allcheckedCount) {
                        viewModel.removeAll();
                    } else {
                        int i;
                        for (i = 0; i < allcheckedCount; i++) {
                            if (itemList.get(i).getCheckStatus()) {
                                viewModel.removeDownload(itemList.get(i));
                            }
                        }
                    }
                    itemList = viewModel.getList();
                    allcheckedCount = itemList.size();

                    downloadAdapter.setMode(0);
                    downloadMode = 0;
                    listMode();
                    listCheck();

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toastLayout));
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    dismiss();
                }
            });

            //??????????????? "?????????"?????? ?????????
            Button canButton = findViewById(R.id.cancelbutton);
            canButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        public CustomDialog(Context mContext) {
            super(mContext);
            this.mContext = mContext;
        }
    }

    //????????? ????????? ?????? ?????? ?????? ?????? ?????????
    public void listCheck() {
        if (allcheckedCount > 0) {
            binding.deleteModeBtn.setVisibility(View.VISIBLE);
            binding.recyclerLayout.setVisibility(View.VISIBLE);
            binding.emptyListLayout.setVisibility(View.GONE);
        } else if (allcheckedCount == 0) {
            binding.deleteModeBtn.setVisibility(View.GONE);
            binding.recyclerLayout.setVisibility(View.GONE);
            binding.emptyListLayout.setVisibility(View.VISIBLE);
        }
    }

    //?????? ????????? ?????? ???????????? ?????? ?????? ?????? ?????????
    public void deleteBtnStyleSet() {
        if (checkCount == 0) {
            binding.deleteButton.setClickable(false);
            binding.deleteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.buttonfa));
        } else {
            binding.deleteButton.setClickable(true);
            binding.deleteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.buttontr));
        }
    }

    //???????????? ?????? ????????????,??????????????? ?????? ?????? ?????? ?????????
    public void listMode() {
        ConstraintLayout layoutWeight;
        layoutWeight = binding.recyclerLayout;
        LinearLayout.LayoutParams params
                = (LinearLayout.LayoutParams) layoutWeight.getLayoutParams();

        if (downloadMode == 0) {
            params.weight = 10;
            binding.titleText.setText("???????????? ??????");
            binding.deleteButtonLayout.setVisibility(View.GONE);
            binding.backModeButton.setVisibility(View.GONE);
            binding.checkLayout.setVisibility(View.GONE);
            binding.deleteModeBtn.setVisibility(View.VISIBLE);
            binding.backButton.setVisibility(View.VISIBLE);
        } else if (downloadMode == 1) {
            params.weight = 8;
            binding.titleText.setText("?????? ????????? ??????");
            binding.deleteButtonLayout.setVisibility(View.VISIBLE);
            binding.backModeButton.setVisibility(View.VISIBLE);
            binding.checkLayout.setVisibility(View.VISIBLE);
            binding.deleteModeBtn.setVisibility(View.GONE);
            binding.backButton.setVisibility(View.GONE);
            binding.allCheck.setChecked(false);
            deleteBtnStyleSet();
            checkCount = 0;
            int a;
            for (a = 0; a < allcheckedCount; a++) {
                itemList.get(a).setCheckStatus(false);
            }
        }
        itemList = viewModel.getList();
        downloadAdapter.setAdapter(itemList);
        downloadAdapter.notifyDataSetChanged();
        binding.recyclerView.scrollToPosition(0);
    }

    //?????? ?????? ?????? ?????????
    public void allChecked() {
        if (checkCount == allcheckedCount) {
            binding.allCheck.setChecked(true);
        } else {
            binding.allCheck.setChecked(false);
        }
    }

    //????????? ???????????? ???????????? ?????? ?????? ?????????
    public void handleOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (downloadMode == 0) {
                    ((MainActivity) requireActivity()).moveTo(new MainFragment());
                } else if (downloadMode == 1) {
                    downloadAdapter.setMode(0);
                    downloadMode = 0;
                    listMode();
                    downloadAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}