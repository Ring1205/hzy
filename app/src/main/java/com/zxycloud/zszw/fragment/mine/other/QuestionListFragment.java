package com.zxycloud.zszw.fragment.mine.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.fragment.common.WebViewFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultQuestionListBean;
import com.zxycloud.zszw.model.bean.QuestionBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

public class QuestionListFragment extends BaseBackFragment {
    private BswRecyclerView<QuestionBean> questionRv;
    private int pageSize = 20;
    private int pageIndex = 1;

    public static QuestionListFragment newInstance() {
        return new QuestionListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_show;
    }

    /**
     * 获取场所列表
     */
    private void getQuestionList() {
        if (null == questionRv) {
            return;
        }
        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener<ResultQuestionListBean>() {
            @Override
            public void success(String action, ResultQuestionListBean resultQuestionListBean, Object tag) {
                if (resultQuestionListBean.isSuccessful()) {
                    if (tag == null)
                        tag = 1;
                    questionRv.setData(resultQuestionListBean.getData(), (Integer) tag, pageSize);
                }
            }
        }, netWork().apiRequest(NetBean.actionGetQuestionList, ResultQuestionListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                .setRequestParams("pageSize", pageSize)
                .setRequestParams("pageIndex", pageIndex));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (null == bundle) {
            // ViewPager缓存的时候，取缓存时保存的数据，以免用户锚点失效
            return;
        }
        ViewStub toolbarVs = findViewById(R.id.toolbar_vs);
        toolbarVs.inflate();
        setToolbarTitle(R.string.common_problem).initToolbarNav();

        questionRv = findViewById(R.id.list_rv);
        questionRv.initAdapter(R.layout.item_question_layout, questionCallBack)
                .setLayoutManager()
                .setDecoration();
        getQuestionList();
    }

    private ConvertViewCallBack<QuestionBean> questionCallBack = new ConvertViewCallBack<QuestionBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final QuestionBean questionBean, int position, int layoutTag) {
            holder.setText(R.id.question_tv, questionBean.getTitle());
            holder.setText(R.id.tv_msg, questionBean.getBrief());
            holder.setText(R.id.tv_time, CommonUtils.date().format(questionBean.getCreateTime()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start(WebViewFragment.newInstance(R.string.web_view_title_question, questionBean.getUrl()));
                }
            });
        }

        @Override
        public void loadingFinished() {

        }
    };
}