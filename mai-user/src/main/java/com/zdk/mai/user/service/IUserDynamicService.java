package com.zdk.mai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicSearchRequest;
import com.zdk.mai.common.model.dynamic.vo.UserDynamicVO;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 12:00
 */
public interface IUserDynamicService extends IService<UserDynamicModel> {

    /**
     * 用户动态列表
     * @param searchRequest
     * @return
     */
    PageInfo<UserDynamicModel> list(DynamicSearchRequest searchRequest);

    /**
     * 动态列表vo
     * @param modelPageInfo
     * @param userId
     * @return
     */
    List<UserDynamicVO> buildVOList(PageInfo<UserDynamicModel> modelPageInfo, Long userId);
}
