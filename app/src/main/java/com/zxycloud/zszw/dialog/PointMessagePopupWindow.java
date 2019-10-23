package com.zxycloud.zszw.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.adapter.AccessoryAdapter;
import com.zxycloud.zszw.event.type.VideoShowType;
import com.zxycloud.zszw.fragment.common.ImagePagerFragment;
import com.zxycloud.zszw.fragment.common.PlayVideoFragment;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskDetailsFragment;
import com.zxycloud.zszw.listener.OnItemClickListener;
import com.zxycloud.zszw.model.PathTypeBean;
import com.zxycloud.common.base.fragment.SupportFragment;
import com.zxycloud.common.widget.RecordPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class PointMessagePopupWindow extends PopupWindow implements OnItemClickListener, View.OnClickListener {
    private View mView;
    private Context mContext; // 上下文参数
    private RecyclerView recyclerView;
    private AccessoryAdapter pathAdapter;
    private SupportFragment mFragment;

    public PointMessagePopupWindow(SupportFragment fragment) {
        this.mContext = fragment.getContext();
        this.mFragment = fragment;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.task_point_pop, null);

        recyclerView = mView.findViewById(R.id.recycler_add_accessory);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        pathAdapter = new AccessoryAdapter(mContext);
        pathAdapter.setOnItemClickListener(this, null, null);
        recyclerView.setAdapter(pathAdapter);

        mView.findViewById(R.id.iv_dismiss).setOnClickListener(this);

        // 导入布局
        this.setContentView(mView);
        // 设置动画效果
        this.setAnimationStyle(R.style.popwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dismiss:
                dismiss();
                break;
        }
    }

    public void show() {
        showAtLocation(LayoutInflater.from(mContext).inflate(R.layout.activity_base, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void setData(String msg, List<String> imgs){
        EditText editText = mView.findViewById(R.id.et_note);
        editText.setText(msg);
        for (String img : imgs) {
            PathTypeBean bean = new PathTypeBean();
            bean.setDataPath(img);
            bean.setType(AccessoryAdapter.TYPE_PHOTO);
            pathAdapter.addData(bean);
        }
    }

    @Override
    public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
        switch (pathAdapter.getPathData().get(position).getType()) {
            case AccessoryAdapter.TYPE_RECORD:
                RecordPopupWindow recordPopupWindow = new RecordPopupWindow(mContext, pathAdapter.getPathData().get(position).getDataPath());
                recordPopupWindow.show(LayoutInflater.from(mContext).inflate(R.layout.activity_base, null));
                break;
            case AccessoryAdapter.TYPE_PHOTO:
                ArrayList<String> paths = new ArrayList<>();
                paths.add(pathAdapter.getPathData().get(position).getDataPath());
                mFragment.startForResult(ImagePagerFragment.newInstance(position, paths), 7001);
                break;
            case AccessoryAdapter.TYPE_VIDEO:
                mFragment.start(PlayVideoFragment.newInstance(VideoShowType.PLAY_TYPE_INSTALL_VIDEO, pathAdapter.getPathData().get(position).getDataPath()));
                break;
        }
        ((TaskDetailsFragment)mFragment).setSubmitPopupWindow(this);
        dismiss();
    }

}
