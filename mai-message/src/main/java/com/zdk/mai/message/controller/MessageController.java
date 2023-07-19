package com.zdk.mai.message.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.constant.CommonMessage;
import com.zdk.mai.common.core.constant.CommonStatus;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.NoticeTypeEnum;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.model.message.MessageNoticeModel;
import com.zdk.mai.common.model.message.request.MessageSearchRequest;
import com.zdk.mai.common.model.message.request.MessageUpdateRequest;
import com.zdk.mai.common.model.message.vo.MessageCountVO;
import com.zdk.mai.common.model.message.vo.MessageVO;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.common.security.annotation.RateLimiter;
import com.zdk.mai.message.service.IMessageNoticeService;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 18:39
 */
@Api(tags = "消息接口")
@Slf4j
@Validated
@RestController
public class MessageController {


    @Autowired
    private IMessageNoticeService messageNoticeService;

    @Autowired
    @Qualifier("mailTaskExecutor")
    public Executor taskExecutor;

    @ApiOperation(value = "消息列表", notes = "消息列表")
    @RateLimiter(prefix = "message",key = "list",expireTime = 1,maxCount = 4,rateMessage = "操作过于频繁")
    @GetMapping("/message/list")
    public CommonResult list(@Valid MessageSearchRequest searchRequest){

        PageInfo<MessageNoticeModel> pageInfo = messageNoticeService.list(searchRequest);
        List<MessageVO> voList = messageNoticeService.voList(pageInfo, searchRequest.getNoticeType());

        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }

    @ApiOperation(value = "标记消息为已读", notes = "标记消息为已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageUpdateRequest", value = "messageUpdateRequest", required = true, dataType = "MessageUpdateRequest"),
    })
    @InitRequestArg(argType = ArgType.UPDATE)
    @RateLimiter(prefix = "message",key = "markRead",expireTime = 1,maxCount = 1,rateMessage = "操作过于频繁")
    @PostMapping("/message/markRead")
    public CommonResult markRead(@RequestBody @Valid MessageUpdateRequest messageUpdateRequest){

        messageNoticeService.markRead(messageUpdateRequest);

        return CommonResult.success();
    }

    @ApiOperation(value = "全部标记为已读", notes = "全部标记为已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageUpdateRequest", value = "messageUpdateRequest", required = true, dataType = "MessageUpdateRequest"),
    })
    @InitRequestArg(argType = ArgType.UPDATE)
    @RateLimiter(prefix = "message",key = "allRead",expireTime = 1,maxCount = 1,rateMessage = "操作过于频繁")
    @PostMapping("/message/allRead")
    public CommonResult allRead(@RequestBody @Valid MessageUpdateRequest messageUpdateRequest){

        messageNoticeService.allRead(messageUpdateRequest);

        return CommonResult.success();
    }

}
