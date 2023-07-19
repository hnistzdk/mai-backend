package com.zdk.mai.api.post;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.constant.ServiceNameConstants;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description 贴子服务暴露接口
 * @Author zdk
 * @Date 2023/2/28 17:40
 */
@FeignClient(value = ServiceNameConstants.POST_SERVICE)
public interface RemotePostService {

    /**
     * 获取贴子详情
     * @param id
     * @return
     */
    @GetMapping("/post/detail/{id}")
    CommonResult<PostDetailVO> detail(@PathVariable("id") Long id);

    /**
     * 获取用户贴子有关成就信息
     * @param userId
     * @param source
     * @return
     */
    @GetMapping("/post/userAchievement/{userId}")
    CommonResult<UserAchievementVO> userAchievement(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 批量获取贴子
     * @param ids
     * @param source
     * @return
     */
    @GetMapping("/post/getByIds")
    CommonResult<List<PostDetailVO>> getByIds(@RequestParam("ids") List<Long> ids,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
