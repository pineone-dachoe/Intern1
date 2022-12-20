package com.example.mypage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mypage.databinding.ListitemBinding;

public class WatchAdapter extends ListAdapter<WatchDto, RecyclerView.ViewHolder> {
    private ListitemBinding Binding;

    public static int mode;

    public WatchAdapter() {
        super(WatchAdapter.DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<WatchDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WatchDto>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull WatchDto oldlist, @NonNull WatchDto newlist) {
                    return oldlist.getContNm().equals(newlist.getContNm());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(
                        @NonNull WatchDto oldlist, @NonNull WatchDto newlist) {
                    return oldlist.getContId().equals(newlist.getContId());
                }
            };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        Binding = Binding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).titletext.setText(getCurrentList().get(position).getContNm());
        Glide.with(holder.itemView).load(getCurrentList().get(position).getPosterUrl()).into(((MyViewHolder) holder).sumnail);

        //FreeCode값에 따라 해당 컨텐츠가 유료 콘텐츠 일 경우 플래그 표시
        if (getCurrentList().get(position).getPchrgFreeCode() == "F") {
            ((MyViewHolder) holder).pay.setVisibility(View.GONE);
        } else if (getCurrentList().get(position).getPchrgFreeCode() == "C") {
            ((MyViewHolder) holder).pay.setVisibility(View.VISIBLE);
        }

        //AdultCont값에 따라 해당 컨텐츠가 성인 콘텐츠 일 경우 플래그 표시
        if (getCurrentList().get(position).getIsAdultCont() == true) {
            ((MyViewHolder) holder).adult.setVisibility(View.VISIBLE);
        } else if (getCurrentList().get(position).getIsAdultCont() == false) {
            ((MyViewHolder) holder).adult.setVisibility(View.GONE);
        }

        ((MyViewHolder) holder).price = getCurrentList().get(position).getPrice();


        //Ty1Code값에 따라 AR,VR,Live플래그 및 공백 출력
        if (getCurrentList().get(position).getTy1Code() == "AR") {
            ((MyViewHolder) holder).type.setImageResource(R.drawable.flag_ar);
        } else if (getCurrentList().get(position).getTy1Code() == "VR") {
            ((MyViewHolder) holder).type.setImageResource(R.drawable.flag_vr);
        } else if (getCurrentList().get(position).getTy1Code() == "LB") {
            ((MyViewHolder) holder).type.setImageResource(R.drawable.flag_live);
        } else if (getCurrentList().get(position).getTy1Code() == null) {
            ((MyViewHolder) holder).type.setVisibility(View.GONE);
        }

        final WatchDto item = getCurrentList().get(position);
        // 먼저 체크박스의 리스너를 null로 초기화한다
        ((MyViewHolder) holder).che.setOnCheckedChangeListener(null);
        // 모델 클래스의 getter로 체크 상태값을 가져온 다음, setter를 통해 이 값을 아이템 안의 체크박스에 set한다
        ((MyViewHolder) holder).che.setChecked(item.getcheck());
        // 체크박스의 상태값을 알기 위해 리스너 부착
        ((MyViewHolder) holder).che.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 여기의 item은 final 키워드를 붙인 모델 클래스의 객체와 동일하다
                item.setcheck(isChecked);
            }
        });

        if (mode == 0) {
            ((MyViewHolder) holder).che.setVisibility(View.GONE);
        } else if (mode == 1) {
            ((MyViewHolder) holder).che.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    public interface OnCheckClickListener {
        void OnCheckClick(CheckBox c, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    private OnCheckClickListener chListener = null;

    public void setOnCheckClickListener(OnCheckClickListener listener) {
        this.chListener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView sumnail, type, pay, adult;
        TextView titletext;
        public CheckBox che;

        int price;

        public MyViewHolder(@NonNull ListitemBinding binding) {
            super(binding.getRoot());

            sumnail = binding.sumnail;
            titletext = binding.titletext;
            type = binding.typeicon;
            pay = binding.payicon;
            adult = binding.adulticon;
            che = binding.che;

            //아이템 클릭 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
            //체크박스 클릭 리스너
            che.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (chListener != null) {
                            chListener.OnCheckClick(che, pos);
                        }
                    }
                }
            });
        }
    }
    //재활용 되는 경우 각 아이템의 변경사항들을 저장
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //체크박스 VISIBLE,GONE 설정을 위한 mode값 세팅
    public void setMode(int mode) {
        this.mode = mode;
    }
}

