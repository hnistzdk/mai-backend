package com.zdk.mai.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zdk.mai.common.model.message.MessageNoticeModel;
import com.zdk.mai.common.model.message.request.MessageUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 17:37
 */
@Mapper
public interface MessageNoticeMapper extends BaseMapper<MessageNoticeModel> {

    /**
     * 全部已读
     * @param messageUpdateRequest
     */
    void allRead(MessageUpdateRequest messageUpdateRequest);

    /**
     * 消息分页(慢查询 不使用)
     * @param receiveId
     * @param noticeType
     * @param messageType
     * @return
     */
    List<MessageNoticeModel> list(@Param("receiveId") Long receiveId, @Param("noticeType") Integer noticeType, @Param("messageType") Integer messageType);


    /**
     * 获取未读消息数量
     * @param noticeType
     * @param messageType
     * @param receiveId
     * @return
     */
    Integer getNotReadMessageCount(@Param("noticeType") Integer noticeType,@Param("messageType") Integer messageType,@Param("receiveId") Long receiveId);

}
