package com.example.mypage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mypage.databinding.FragmentWatchLayoutBinding;

import java.util.ArrayList;


public class WatchFragment extends Fragment implements MainActivity.onKeyBackPressedListener {


    private FragmentWatchLayoutBinding binding;
    private WatchViewModel viewModel;
    private ArrayList<WatchDto> itemList = new ArrayList<>();
    int mod = 0;
    int count = 0;
    WatchAdapter watchAdapter = new WatchAdapter();
    Dialog Custom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Custom = new Dialog(getContext());       // Dialog 초기화
        Custom.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        Custom.setContentView(R.layout.fragment_custom_dialog);

        viewModel = new ViewModelProvider(this).get(WatchViewModel.class);
        itemList = viewModel.getList();

        binding = FragmentWatchLayoutBinding.inflate(inflater, container, false);
        binding.recy.setItemAnimator(null);

        Button addbtn = binding.addlist;
        ImageButton back = binding.back;
        ImageButton mode = binding.mode;

        ImageButton backmode = binding.backmode;
        Button delete = binding.delete;
        CheckBox allChe = binding.cheall;

        WatchList();

        watchAdapter.setMode(0);
        watchAdapter.submitList(itemList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        binding.recy.setAdapter(watchAdapter);
        binding.recy.setLayoutManager(linearLayoutManager);

        //데이터 추가 버튼 클릭시 리스트 추가 및 출력, 불필요한 항목들 GONE 처리
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.addData();
                itemList = viewModel.getList();
                WatchList();
                watchAdapter.submitList(itemList);

            }
        });

        //일반 모드 뒤로가기 이미지 버튼 클릭시 메인 프래그먼트 화면으로 돌아가는 기능
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).moveTo(new MainFragment());
            }
        });

        //버튼 클릭시 삭제 모드로 화면 전환
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                watchAdapter.setMode(1);
                mod = 1;
                ButtonChe();

                ListMode();
                watchAdapter.notifyDataSetChanged();
                watchAdapter.submitList(itemList);
            }
        });

        //삭제모드에서 시청목록 확인 모드로 화면 전환
        backmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mod = 0;
                watchAdapter.setMode(0);
                ListMode();
                watchAdapter.notifyDataSetChanged();
                watchAdapter.submitList(itemList);
            }
        });

        //체크박스 전체 선택,해제 클릭 이벤트 처리
        allChe.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = watchAdapter.getCurrentList().size();
                if (allChe.isChecked()==true) {
                    for (int i = 0; i < cnt; i++) {
                        itemList.get(i).setcheck(true);
                    }
                    int a = itemList.size();
                    int t;
                    count = 0;
                    for (t = 0; t < a; t++) {
                        if (itemList.get(t).getcheck() == true) {
                            count++;
                        }
                    }
                } else {
                    for (int i = 0; i < cnt; i++) {
                        itemList.get(i).setcheck(false);
                    }
                    int a = itemList.size();
                    int t;
                    for (t = 0; t < a; t++) {
                        if (itemList.get(t).getcheck() == false) {
                            count--;
                        }
                    }
                }
                ButtonChe();
                binding.counttext.setText(count + "개 선택(전체 " + itemList.size() + "개)");
                watchAdapter.notifyDataSetChanged();
            }
        });

        //recyclerview 리스트 아이템 클릭 이벤트
        watchAdapter.setOnItemClickListener(new WatchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                //mod값에 따라 구분하여 아이템 클릭 이벤트 구분
                if (mod == 0) {
                    if (itemList.get(position).getPrice() == 0) {
                        Toast toast = Toast.makeText(getContext(), "무료 콘텐츠 " +
                                "입니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), itemList.get(position).getPrice() + "원 입니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else if (mod == 1) {
                    if (itemList.get(position).getcheck() == false) {
                        itemList.get(position).setcheck(true);
                    } else {
                        itemList.get(position).setcheck(false);
                    }
                    if (itemList.get(position).getcheck() == true) {
                        count++;
                    } else {
                        count--;
                    }
                    if (count == itemList.size()) {
                        allChe.setChecked(true);
                    } else {
                        allChe.setChecked(false);
                    }
                    ButtonChe();
                    watchAdapter.notifyItemChanged(position);
                    binding.counttext.setText(count + "개 선택(전체 " + itemList.size() + "개)");
                }
            }
        });

        //recyclerview 리스트 아이템 내부의 체크박스 클릭 이벤트
        watchAdapter.setOnCheckClickListener(new WatchAdapter.OnCheckClickListener() {
            @Override
            public void OnCheckClick(CheckBox c, int position) {
                if (mod == 1) {
                    if (itemList.get(position).getcheck() == true) {
                        count++;
                    } else {
                        count--;
                    }

                    //전체 아이템 선택시 전체 선택 체크박스 체크 확인
                    int a = itemList.size();
                    if (count == a) {
                        allChe.setChecked(true);
                    } else {
                        allChe.setChecked(false);
                    }
                    ButtonChe();
                    watchAdapter.notifyItemChanged(position);
                    binding.counttext.setText(count + "개 선택(전체 " + itemList.size() + "개)");
                }
            }
        });

        //삭제하기 버튼 클릭 이벤트
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dlg = new CustomDialog(getContext());
                dlg.show();
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //체크 항목 수가 0일때 버튼 비활성화
    public void ButtonChe() {
        if (count == 0) {
            binding.delete.setClickable(false);
            binding.delete.setBackground(getResources().getDrawable((R.drawable.buttonfa)));
        } else {
            binding.delete.setClickable(true);
            binding.delete.setBackground(getResources().getDrawable((R.drawable.buttontr)));

        }
    }

    //리스트가 비어있는지 확인
    public void WatchList()
    {
        if (itemList.size() > 0) {
            binding.mode.setVisibility(View.VISIBLE);
            binding.con.setVisibility(View.VISIBLE);
            binding.nolist.setVisibility(View.GONE);
        } else if (itemList.size() == 0) {
            binding.nolist.setVisibility(View.VISIBLE);
            binding.mode.setVisibility(View.GONE);
            binding.con.setVisibility(View.GONE);
        }
    }

    //시청목록,삭제모드에 따라 UI항목 Visibility설정
    public void ListMode() {
        if (mod == 0) {
            count = 0;

            RelativeLayout hbar;
            hbar = binding.con;
            LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams) hbar.getLayoutParams();
            params.weight = 10;

            binding.titletext.setText("시청 목록");
            binding.delbutton.setVisibility(View.GONE);
            binding.backmode.setVisibility(View.GONE);
            binding.chea.setVisibility(View.GONE);
            binding.mode.setVisibility(View.VISIBLE);
            binding.back.setVisibility(View.VISIBLE);
        } else if (mod == 1) {
            count = 0;

            RelativeLayout hbar;
            hbar = binding.con;
            LinearLayout.LayoutParams params
                    = (LinearLayout.LayoutParams) hbar.getLayoutParams();
            params.weight = 8;

            binding.counttext.setText(count + "개 선택(전체 " + itemList.size() + "개)");
            binding.titletext.setText("삭제");
            binding.delbutton.setVisibility(View.VISIBLE);
            binding.backmode.setVisibility(View.VISIBLE);
            binding.chea.setVisibility(View.VISIBLE);
            binding.mode.setVisibility(View.GONE);
            binding.back.setVisibility(View.GONE);

            binding.cheall.setChecked(false);
            int a;
            for (a = 0; a < itemList.size(); a++) {
                itemList.get(a).setcheck(false);
            }
        }
    }

    //커스텀 다이얼로그 세팅
    public class CustomDialog extends Dialog {

        private Context mContext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.fragment_custom_dialog);
            TextView contxt = findViewById(R.id.txt_contents);
            if (count == itemList.size()) {
                contxt.setText("시청목록이 모두 삭제됩니다.\n전체삭제를 진행하시겠습니까?");
            } else {
                contxt.setText(count + "개의 콘텐츠를 삭제하시겠습니까?");
            }

            //다이얼로그 "네"버튼 이벤트
            Button okbtn = findViewById(R.id.okbutton);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count == itemList.size()) {
                        viewModel.deleteAll();
                    } else {
                        int cn;
                        cn = itemList.size();

                        int i;
                        String t;
                        for (i = 0; i < cn; i++) {
                            if (itemList.get(i).getcheck() == true) {
                                t = itemList.get(i).getContId();
                                viewModel.deleteById(t);
                            }
                        }
                    }
                    count = 0;
                    binding.cheall.setChecked(false);
                    itemList = viewModel.getList();
                    watchAdapter.setMode(0);
                    mod = 0;
                    ListMode();
                    WatchList();
                    watchAdapter.submitList(itemList);
                    watchAdapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(getContext(), "삭제가 완료되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    dismiss();
                }
            });

            //다이얼로그 "아니요"버튼 이벤트
            Button canbtn = findViewById(R.id.cancelbutton);
            canbtn.setOnClickListener(new View.OnClickListener() {
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

    //삭제모드에서 뒤로가기 버튼 클릭시 시청목록으로 돌아오는 이벤트 처리
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).setOnKeyBackPressedListener(this);
    }
    @Override
    public void onBack() {
        MainActivity activity = (MainActivity) getActivity();
        activity.setOnKeyBackPressedListener(null);
        if (mod == 1) {
            count = 0;
            binding.cheall.setChecked(false);
            itemList = viewModel.getList();
            watchAdapter.setMode(0);
            mod = 0;
            ListMode();
            WatchList();
            watchAdapter.submitList(itemList);
            watchAdapter.notifyDataSetChanged();
        }
    }
}
