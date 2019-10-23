package com.zxycloud.zszw.fragment.mine.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.zszw.fragment.common.WebViewFragment;
import com.zxycloud.zszw.listener.NetRequestListener;
import com.zxycloud.zszw.model.ResultKnowledgeListBean;
import com.zxycloud.zszw.model.bean.KnowledgeBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.netWork.ApiRequest;
import com.zxycloud.common.utils.netWork.NetBean;
import com.zxycloud.common.widget.BswRecyclerView.BswRecyclerView;
import com.zxycloud.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.zxycloud.common.widget.BswRecyclerView.RecyclerViewHolder;

import java.util.List;

public class KnowledgeListFragment extends BaseBackFragment {
    private BswRecyclerView<KnowledgeBean> konwledgeRv;

    private int pageSize = 20;
    private int pageIndex = 1;

    public static KnowledgeListFragment newInstance() {
        return new KnowledgeListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_show;
    }

    /**
     * 获取场所列表
     */
    private void getQuestionList() {
        if (null == konwledgeRv) {
            return;
        }
        final ApiRequest<ResultKnowledgeListBean> apiRequest = netWork().apiRequest(NetBean.actionGetKnowledgeList, ResultKnowledgeListBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading)
                .setRequestParams("pageSize", pageSize)
                .setRequestParams("pageIndex", pageIndex);
        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener<ResultKnowledgeListBean>() {
            @Override
            public void success(String action, ResultKnowledgeListBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    List<KnowledgeBean> knowledgeBeans = baseBean.getData();
                    konwledgeRv.setData(knowledgeBeans);
                }
            }
        }, apiRequest);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        ViewStub toolbarVs = findViewById(R.id.toolbar_vs);
        toolbarVs.inflate();
        setToolbarTitle(R.string.safety_fire_knowledge).initToolbarNav();

        konwledgeRv = findViewById(R.id.list_rv);
        konwledgeRv.initAdapter(R.layout.item_question_layout, questionCallBack)
                .setLayoutManager()
                .setDecoration();
        getQuestionList();
    }

    private ConvertViewCallBack<KnowledgeBean> questionCallBack = new ConvertViewCallBack<KnowledgeBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final KnowledgeBean knowledgeBean, int position, int layoutTag) {
            holder.setText(R.id.question_tv, knowledgeBean.getTitle());
            holder.setText(R.id.tv_msg, knowledgeBean.getBrief());
            holder.setText(R.id.tv_time, CommonUtils.date().format(knowledgeBean.getCreateTime()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start(WebViewFragment.newInstance(R.string.web_view_title_question, knowledgeBean.getUrl()));
                }
            });
        }

        @Override
        public void loadingFinished() {

        }
    };
}