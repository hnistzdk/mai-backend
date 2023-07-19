package com.zdk.mai.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.message.MessageInfoModel;
import com.zdk.mai.common.model.message.MessageNoticeModel;
import com.zdk.mai.common.model.message.request.MessageSearchRequest;
import com.zdk.mai.common.model.message.request.MessageUpdateRequest;
import com.zdk.mai.common.model.message.vo.MessageVO;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 17:33
 */
public interface IMessageNoticeService extends IService<MessageNoticeModel> {

    /**
     * 消息分页获取
     * @param searchRequest
     * @return
     */
    PageInfo<MessageNoticeModel> list(MessageSearchRequest searchRequest);


    /**
     * 获取vo
     * @param pageInfo
     * @param noticeType 查询的通知类型
     * @return
     */
    List<MessageVO> voList(PageInfo<MessageNoticeModel> pageInfo, Integer noticeType);

    /**
     * 修改消息状态
     * @param messageUpdateRequest
     */
    void markRead(MessageUpdateRequest messageUpdateRequest);

    /**
     * 全部标记为已读
     * @param messageUpdateRequest
     */
    void allRead(MessageUpdateRequest messageUpdateRequest);

    /**
     * 获取未读消息数量
     * @param noticeType
     * @param messageType
     * @return
     */
    Integer getNotReadMessageCount(Integer noticeType,Integer messageType);

}
