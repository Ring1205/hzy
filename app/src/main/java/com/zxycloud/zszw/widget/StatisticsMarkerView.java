
package com.zxycloud.zszw.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.zxycloud.zszw.R;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class StatisticsMarkerView extends MarkerView {
    private final TextView tvContent;
    private final Object tag;
    private MarkerFormatCallback callback;

    public StatisticsMarkerView(Context context, int layoutResource, Object tag, MarkerFormatCallback callback) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tv_dialog);
        this.callback = callback;
        this.tag = tag;
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int x = (int) Math.rint(e.getX());
        callback.markerFormat(x, e, tvContent, tag);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    public interface MarkerFormatCallback {
        void markerFormat(int x, Entry entry, TextView tvContent, Object tag);
    }
}
