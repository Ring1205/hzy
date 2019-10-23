package com.zxycloud.zszw.base;

import android.view.LayoutInflater;

import com.zxycloud.zszw.MainActivity;
import com.zxycloud.zszw.R;
import com.zxycloud.zszw.fragment.service.patrol.task.TaskCaptureFragment;
import com.zxycloud.zszw.listener.OnNewIntentListener;
import com.zxycloud.common.widget.ScanPopupWindow;

public abstract class BaseNFCFragment extends BaseBackFragment {
    private ScanPopupWindow scanPopupWindow;

    public void jumpCaptureFragment(String taskId) {
        start(TaskCaptureFragment.newInstance(taskId));
    }

    public void showScanPopupWindow(OnNewIntentListener listener) {
            scanPopupWindow = new ScanPopupWindow(getContext());
            scanPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
            ((MainActivity) getContext()).setmOnScanListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanPopupWindow != null)
            scanPopupWindow.dismiss();
    }
}
