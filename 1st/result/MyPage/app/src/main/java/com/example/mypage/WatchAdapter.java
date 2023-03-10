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

        //FreeCode?????? ?????? ?????? ???????????? ?????? ????????? ??? ?????? ????????? ??????
        if (getCurrentList().get(position).getPchrgFreeCode() == "F") {
            ((MyViewHolder) holder).pay.setVisibility(View.GONE);
        } else if (getCurrentList().get(position).getPchrgFreeCode() == "C") {
            ((MyViewHolder) holder).pay.setVisibility(View.VISIBLE);
        }

        //AdultCont?????? ?????? ?????? ???????????? ?????? ????????? ??? ?????? ????????? ??????
        if (getCurrentList().get(position).getIsAdultCont() == true) {
            ((MyViewHolder) holder).adult.setVisibility(View.VISIBLE);
        } else if (getCurrentList().get(position).getIsAdultCont() == false) {
            ((MyViewHolder) holder).adult.setVisibility(View.GONE);
        }

        ((MyViewHolder) holder).price = getCurrentList().get(position).getPrice();


        //Ty1Code?????? ?????? AR,VR,Live????????? ??? ?????? ??????
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
        // ?????? ??????????????? ???????????? null??? ???????????????
        ((MyViewHolder) holder).che.setOnCheckedChangeListener(null);
        // ?????? ???????????? getter??? ?????? ???????????? ????????? ??????, setter??? ?????? ??? ?????? ????????? ?????? ??????????????? set??????
        ((MyViewHolder) holder).che.setChecked(item.getcheck());
        // ??????????????? ???????????? ?????? ?????? ????????? ??????
        ((MyViewHolder) holder).che.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ????????? item??? final ???????????? ?????? ?????? ???????????? ????????? ????????????
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

            //????????? ?????? ?????????
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // ????????? ????????? ????????? ??????.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
            //???????????? ?????? ?????????
            che.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // ????????? ????????? ????????? ??????.
                        if (chListener != null) {
                            chListener.OnCheckClick(che, pos);
                        }
                    }
                }
            });
        }
    }
    //????????? ?????? ?????? ??? ???????????? ?????????????????? ??????
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //???????????? VISIBLE,GONE ????????? ?????? mode??? ??????
    public void setMode(int mode) {
        this.mode = mode;
    }
}

