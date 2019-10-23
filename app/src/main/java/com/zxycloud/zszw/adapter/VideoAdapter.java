package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.bean.VideoBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {
    private Context context;
    private OnItemClickListener mClickListener;
    private List<VideoBean> data;

    public void setData(List<VideoBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public VideoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoHolder(LayoutInflater.from(context).inflate(R.layout.item_video_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder videoHolder, int i) {
        //设置item的高度跟随宽度走
//        ViewGroup.LayoutParams parm = videoHolder.itemView.getLayoutParams();
//        parm.height = CommonUtils.measureScreen().getScreenWidth(context) / 3 - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;
//        videoHolder.itemView.setLayoutParams(parm);

        if (data.get(i).hasVideoImg()) {
            CommonUtils.glide().loadImageView(context, data.get(i).getVideoImg(), videoHolder.ivAlbum);
        } else {
            CommonUtils.glide().loadImageView(context, R.mipmap.bg_video, videoHolder.ivAlbum);
        }

        videoHolder.tvVideo.setText(data.get(i).getVideoName());

        videoHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(videoHolder.getAdapterPosition(), v, videoHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        ImageView ivAlbum;
        TextView tvVideo;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.iv_album);
            tvVideo = itemView.findViewById(R.id.tv_video);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
