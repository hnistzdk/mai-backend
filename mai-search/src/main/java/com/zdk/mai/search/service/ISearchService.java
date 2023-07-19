package com.zdk.mai.search.service;

import cn.easyes.core.biz.EsPageInfo;
import com.zdk.mai.common.es.model.EsPostModel;
import com.zdk.mai.common.es.model.EsUserModel;
import com.zdk.mai.common.model.search.request.SearchRequest;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/29 19:02
 */
public interface ISearchService {


    /**
     * 贴子分页查询
     * @param searchRequest
     * @return
     */
    EsPageInfo<EsPostModel> postPage(SearchRequest searchRequest);


    /**
     * 用户分页查询
     * @param searchRequest
     * @return
     */
    EsPageInfo<EsUserModel> userPage(SearchRequest searchRequest);
}
