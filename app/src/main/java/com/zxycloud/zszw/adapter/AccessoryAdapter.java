package com.zxycloud.zszw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.listener.OnLongClickListener;
import com.zxycloud.zszw.model.PathTypeBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class AccessoryAdapter extends RecyclerView.Adapter<AccessoryAdapter.AccessoryHolder> {
    public static final String TYPE_RECORD = "type_record";
    public static final String TYPE_VIDEO = "type_video";
    public static final String TYPE_PHOTO = "type_photo";

    private Context context;
    private ArrayList<PathTypeBean> imgPath;
    private OnItemClickListener mClickListener;
    private OnLongClickListener mLongClickListener;
    private UploadFileOnListener uploadFileOnListener;

    public AccessoryAdapter(Context context) {
        this.context = context;
        imgPath = new ArrayList<>();
    }

    public void addData(PathTypeBean bean) {
        imgPath.add(bean);
        if (uploadFileOnListener != null)
            uploadFileOnListener.upLoadFile(bean);
        notifyDataSetChanged();
    }

    public void deleteData(int bean) {
        imgPath.remove(bean);
        notifyDataSetChanged();
    }

    public List<PathTypeBean> getPathData() {
        return imgPath;
    }

    @NonNull
    @Override
    public AccessoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AccessoryHolder(LayoutInflater.from(context).inflate(R.layout.item_accessory_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AccessoryHolder accessoryHolder, int i) {
        //设置item的高度跟随宽度走
        ViewGroup.LayoutParams parm = accessoryHolder.itemView.getLayoutParams();
        parm.height = CommonUtils.measureScreen().getScreenWidth(context) / 3 - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;
        accessoryHolder.itemView.setLayoutParams(parm);

        PathTypeBean bean = imgPath.get(i);
        if (TYPE_RECORD.equals(bean.getType())) {
            accessoryHolder.ivPlay.setVisibility(View.VISIBLE);
            CommonUtils.glide().loadImageView(context, R.drawable.ic_volume_up_black_32dp, accessoryHolder.ivPlay);
            CommonUtils.glide().loadImageView(context, R.drawable.timg, accessoryHolder.ivAlbum);
        } else if (TYPE_VIDEO.equals(bean.getType())) {
            accessoryHolder.ivPlay.setVisibility(View.VISIBLE);
            CommonUtils.glide().loadImageView(context, R.drawable.ic_play_arrow_play_48dp, accessoryHolder.ivPlay);
            if (bean.getDataPath() != null)
                CommonUtils.glide().loadImageViewThumbnail(context, bean.getVideoImgPath(), accessoryHolder.ivAlbum);
            else
                CommonUtils.glide().loadImageViewThumbnail(context, bean.getLoadUrl(), accessoryHolder.ivAlbum, 0);
        } else if (TYPE_PHOTO.equals(bean.getType())) {
            accessoryHolder.ivPlay.setVisibility(View.GONE);
            if (bean.getDataPath() != null)
                CommonUtils.glide().loadImageViewThumbnail(context, bean.getDataPath(), accessoryHolder.ivAlbum);
            else
                CommonUtils.glide().loadImageViewThumbnail(context, bean.getLoadUrl(), accessoryHolder.ivAlbum, 0);
        }

        accessoryHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onItemClick(accessoryHolder.getAdapterPosition(), v, accessoryHolder);
            }
        });
        accessoryHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null)
                    mLongClickListener.onItemLongClick(accessoryHolder.getAdapterPosition(), v, accessoryHolder);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgPath != null ? imgPath.size() : 0;
    }

    class AccessoryHolder extends RecyclerView.ViewHolder {
        ImageView ivAlbum, ivPlay;

        public AccessoryHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.iv_album);
            ivPlay = itemView.findViewById(R.id.iv_play);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener, OnLongClickListener mLongClickListener, UploadFileOnListener uploadFileOnListener) {
        this.mClickListener = itemClickListener;
        this.mLongClickListener = mLongClickListener;
        this.uploadFileOnListener = uploadFileOnListener;
    }

    public interface UploadFileOnListener {
        void upLoadFile(PathTypeBean bean);
    }
}
