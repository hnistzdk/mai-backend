package com.zdk.mai.search.service.impl;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import com.zdk.mai.common.core.enums.SortRuleEnum;
import com.zdk.mai.common.es.mapper.EsPostMapper;
import com.zdk.mai.common.es.mapper.EsUserMapper;
import com.zdk.mai.common.es.model.EsPostModel;
import com.zdk.mai.common.es.model.EsUserModel;
import com.zdk.mai.common.model.search.request.SearchRequest;
import com.zdk.mai.search.service.ISearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/29 19:02
 */
@SuppressWarnings({"unchecked"})
@Service
public class SearchServiceImpl implements ISearchService {

    @Resource
    private EsPostMapper searchEsPostMapper;

    @Resource
    private EsUserMapper searchEsUserMapper;

    @Override
    public EsPageInfo<EsPostModel> postPage(SearchRequest searchRequest){
        String query = searchRequest.getQuery();
        Integer type = searchRequest.getType();
        String sortRule = searchRequest.getSortRule();

        LambdaEsQueryWrapper<EsPostModel> wrapper = new LambdaEsQueryWrapper<>();

        wrapper.eq(EsPostModel::getType, type)
                .and(e->e.match(EsPostModel::getContent, query)
                        .or()
                        .like(EsPostModel::getTitle, query))
                .orderByDesc(sortRule.equals(SortRuleEnum.HOTTEST.getName()),EsPostModel::getPv)
                .orderByDesc(sortRule.equals(SortRuleEnum.NEWEST.getName()),EsPostModel::getUpdateTime);

        return searchEsPostMapper.pageQuery(wrapper, searchRequest.getCurrentPage(), searchRequest.getPageSize());
    }

    @Override
    public EsPageInfo<EsUserModel> userPage(SearchRequest searchRequest) {
        String query = searchRequest.getQuery();

        LambdaEsQueryWrapper<EsUserModel> wrapper = new LambdaEsQueryWrapper<>();

        wrapper.eq(EsUserModel::getDelFlag, false)
                .and(e->e
                        .match(EsUserModel::getUsername, query)
                        .or()
                        .match(EsUserModel::getNickname, query)
                        .or()
                        .match(EsUserModel::getPosition, query)
                        .or()
                        .match(EsUserModel::getCompany, query)
                        .or()
                        .match(EsUserModel::getSelfIntroduction, query)
                ).orderByAsc(EsUserModel::getUserId);

        return searchEsUserMapper.pageQuery(wrapper, searchRequest.getCurrentPage(), searchRequest.getPageSize());
    }
}
