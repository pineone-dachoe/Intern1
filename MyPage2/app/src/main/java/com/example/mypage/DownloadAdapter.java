package com.example.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mypage.databinding.DownloaditemBinding;

import java.util.ArrayList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.CustomViewHolder> {
    private ArrayList<DownloadDto> list;
    public static int mode;

    public DownloadAdapter(ArrayList<DownloadDto> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloaditemBinding binding = DownloaditemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding);

    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final DownloadDto item = list.get(position);

        Glide.with(holder.downImage).load(list.get(position).getPosterUrl()).centerInside().into((holder).downImage);

        if (item.getTy1Code() == "VR") {
            holder.typeIcon.setVisibility(View.VISIBLE);
            holder.typeIcon.setImageResource(R.drawable.flag_vr);
        } else if (item.getTy1Code() == "AR") {
            holder.typeIcon.setVisibility(View.VISIBLE);
            holder.typeIcon.setImageResource(R.drawable.flag_ar);
        } else if (item.getTy1Code() == "LB") {
            holder.typeIcon.setVisibility(View.VISIBLE);
            holder.typeIcon.setImageResource(R.drawable.flag_live);
        } else {
            holder.typeIcon.setImageResource(0);
            holder.typeIcon.setVisibility(View.GONE);
        }

        if (item.getIsAdultCont() == false) {
            holder.adultIcon.setVisibility(View.GONE);
        } else {
            holder.adultIcon.setVisibility(View.VISIBLE);
        }

        if (mode == 0) {
            if (item.getStatus() == "ADDED") {
                holder.checkBox.setVisibility(View.GONE);
                holder.closeBtn.setVisibility(View.VISIBLE);
                holder.closeBtnLay.setVisibility(View.VISIBLE);
                holder.playBtn.setVisibility(View.GONE);
                holder.pausedBtn.setVisibility(View.VISIBLE);
            } else if (item.getStatus() == "PAUSED") {
                holder.checkBox.setVisibility(View.GONE);
                holder.pausedBtn.setVisibility(View.GONE);
                holder.closeBtn.setVisibility(View.VISIBLE);
                holder.closeBtnLay.setVisibility(View.VISIBLE);

                holder.playBtn.setVisibility(View.VISIBLE);
            } else if (item.getStatus() == "PROGRESS") {
                holder.checkBox.setVisibility(View.GONE);
                holder.pausedBtn.setVisibility(View.VISIBLE);
                holder.closeBtn.setVisibility(View.VISIBLE);
                holder.closeBtnLay.setVisibility(View.VISIBLE);
                holder.playBtn.setVisibility(View.GONE);
            } else if (item.getStatus() == "COMPLETED") {
                holder.checkBox.setVisibility(View.GONE);
                holder.closeBtn.setVisibility(View.GONE);
                holder.closeBtnLay.setVisibility(View.GONE);
                holder.playBtn.setVisibility(View.GONE);
                holder.pausedBtn.setVisibility(View.GONE);
            }

        } else if (mode == 1) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.closeBtn.setVisibility(View.GONE);
            holder.pausedBtn.setVisibility(View.GONE);
            holder.playBtn.setVisibility(View.GONE);
            holder.closeBtnLay.setVisibility(View.GONE);
        }

        holder.titleText.setText(item.getContNm());

        if (item.getProgress() >= 0) {
            holder.closeBtn.getBackground().setLevel(item.getProgress() * 100);
        }
        // 먼저 체크박스의 리스너를 null로 초기화한다
        holder.checkBox.setOnCheckedChangeListener(null);
        // 모델 클래스의 getter로 체크 상태값을 가져온 다음, setter를 통해 이 값을 아이템 안의 체크박스에 set한다
        holder.checkBox.setChecked(item.getCheckStatus());
        // 체크박스의 상태값을 알기 위해 리스너 부착
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 여기의 item은 final 키워드를 붙인 모델 클래스의 객체와 동일하다
                item.setCheckStatus(isChecked);
            }
        });
    }

    public interface OnCheckClickListener {
        void OnCheckClick(CheckBox c, int pos);
    }

    private static OnCheckClickListener chListener = null;

    public void setOnCheckClickListener(OnCheckClickListener listener) {
        this.chListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private static OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnCloseBtnClickListener {
        void onCloseBtnClick(ImageButton v, int pos);
    }

    private static OnCloseBtnClickListener closeBtnListener = null;

    public void setOnCloseBtnClickListener(OnCloseBtnClickListener listener) {
        this.closeBtnListener = listener;
    }

    public interface OnPauseBtnClickListener {
        void onPauseBtnClick(ImageButton v, int pos);
    }

    private static OnPauseBtnClickListener pauseBtnListener = null;

    public void setOnPauseBtnClickListener(OnPauseBtnClickListener listener) {
        this.pauseBtnListener = listener;
    }

    public interface OnPlayBtnClickListener {
        void onPlayBtnClick(ImageButton v, int pos);
    }

    private static OnPlayBtnClickListener playBtnListener = null;

    public void setOnPlayBtnClickListener(OnPlayBtnClickListener listener) {
        this.playBtnListener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        ImageView typeIcon, adultIcon, downImage;
        CheckBox checkBox;
        ImageButton pausedBtn, closeBtn, playBtn;
        ConstraintLayout closeBtnLay;

        public CustomViewHolder(DownloaditemBinding binding) {
            super(binding.getRoot());
            typeIcon = binding.typeIcon;
            adultIcon = binding.adultFlag;
            titleText = binding.titleText;
            downImage = binding.downImage;
            checkBox = binding.itemCheck;
            pausedBtn = binding.pausedButton;
            playBtn = binding.playButton;
            closeBtn = binding.closeButton;
            closeBtnLay = binding.closeButtonLayout;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
            checkBox.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (chListener != null) {
                            chListener.OnCheckClick(checkBox, pos);
                        }
                    }
                }
            });
            closeBtn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.

                        if (closeBtnListener != null) {
                            closeBtnListener.onCloseBtnClick(closeBtn, pos);
                        }
                    }
                }
            });
            pausedBtn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (pauseBtnListener != null) {
                            pauseBtnListener.onPauseBtnClick(pausedBtn, pos);
                        }
                    }
                }
            });
            playBtn.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (playBtnListener != null) {
                            playBtnListener.onPlayBtnClick(playBtn, pos);
                        }
                    }
                }
            });
        }
    }
    public void setAdapter(ArrayList<DownloadDto> list2) {
        list = list2;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
}