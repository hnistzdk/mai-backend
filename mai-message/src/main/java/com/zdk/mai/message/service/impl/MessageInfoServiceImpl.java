package com.zdk.mai.message.service.impl;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.message.MessageInfoModel;
import com.zdk.mai.common.model.message.request.MessageSearchRequest;
import com.zdk.mai.common.model.message.vo.MessageVO;
import com.zdk.mai.message.mapper.MessageInfoMapper;
import com.zdk.mai.message.service.IMessageInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 17:34
 */
@Service
public class MessageInfoServiceImpl extends BaseServiceImpl<MessageInfoMapper, MessageInfoModel> implements IMessageInfoService {

}
