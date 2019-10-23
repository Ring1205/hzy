package com.zxycloud.zszw.model;

import com.zxycloud.zszw.model.bean.QuestionBean;
import com.zxycloud.common.base.BaseBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class ResultQuestionListBean extends BaseBean {
    private List<QuestionBean> data;

    public List<QuestionBean> getData() {
        return data;
    }
}
