package com.zxycloud.common.widget.SearchEditText;

import android.support.annotation.NonNull;
import android.view.View;

public interface OnClickRightListener {
    void onClick(View view, String search);
    void onDownDrawable(@NonNull View view);
}
