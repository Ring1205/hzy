package android.support.design.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zxycloud.common.R;

/**
 * @author leiming
 * @date 2019/4/5.
 */
public class CustomTabLayout extends TabLayout {
    private Context mContext;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setTabMaxWidth(int tabMaxWidth) {
        this.tabMaxWidth = tabMaxWidth;
        invalidate();
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        if (null == viewPager) {
            return;
        }
        FragmentPagerAdapter adapter = (FragmentPagerAdapter) viewPager.getAdapter();
        super.setupWithViewPager(viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            //获取每一个tab对象
            Tab tabAt = getTabAt(i);
            //将每一个条目设置我们自定义的视图
            tabAt.setCustomView(R.layout.item_tab_layout);
            //通过tab对象找到自定义视图的ID
            TextView textView = tabAt.getCustomView().findViewById(R.id.tv_tab);
            //默认选中第一个
            if (i == viewPager.getCurrentItem()) {
                // 设置第一个tab的TextView是被选择的样式
                tabAt.getCustomView().findViewById(R.id.tv_tab).setSelected(true);//第一个tab被选中
                //设置选中标签的文字大小
                textView.setTextSize(17);
                textView.setTextColor(mContext.getResources().getColor(R.color.common_color_text));
            }
            textView.setText(adapter.getPageTitle(i));//设置tab上的文字
        }
    }
}
