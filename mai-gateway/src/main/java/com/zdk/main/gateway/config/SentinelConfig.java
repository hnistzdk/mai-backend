package com.zdk.main.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.zdk.mai.common.core.constant.SentinelFlowApiConstants;
import com.zdk.mai.common.core.constant.ServiceNameConstants;
import com.zdk.main.gateway.handler.SentinelFallbackHandler;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/12 16:28
 */
@Configuration
public class SentinelConfig {

    public static final long ONE_DAY = 60 * 60 * 24;
    public static final long ONE_HOUR = 60 * 60;
    public static final long ONE_MINUTE = 60;
    public static final long ONE_SECOND = 1;



    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelFallbackHandler sentinelGatewayExceptionHandler() {
        return new SentinelFallbackHandler();
    }

    @PostConstruct
    public void doInit() {
        // 加载网关限流规则
        initGatewayRules();
    }

    /**
     * 网关限流规则
     */
    private void initGatewayRules() {
        //加载限流规则
        Set<GatewayFlowRule> rules = new HashSet<>();
        //所有接口  一秒不能超10次
        rules.add(new GatewayFlowRule(SentinelFlowApiConstants.ALL_API)
                .setCount(10)
                .setIntervalSec(ONE_SECOND)
        );

        //头像更新接口 一天 500次
        rules.add(new GatewayFlowRule(SentinelFlowApiConstants.USER_AVATAR_UPLOAD_API)
                .setCount(500)
                .setIntervalSec(ONE_DAY)
        );

        //头像更新接口 一天 500次
        rules.add(new GatewayFlowRule(SentinelFlowApiConstants.USER_INFO_UPDATE_API)
                .setCount(500)
                .setIntervalSec(ONE_DAY)
        );

        GatewayRuleManager.loadRules(rules);

        // 加载限流分组
        initCustomizedApis();
    }

    /**
     * 限流分组
     */
    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        // 所有接口
        ApiDefinition allApi = new ApiDefinition(SentinelFlowApiConstants.ALL_API)
                .setPredicateItems(new HashSet<ApiPredicateItem>() {
                    private static final long serialVersionUID = 1L;
                    {
                        add(new ApiPathPredicateItem()
                                .setPattern(SentinelFlowApiConstants.ALL_API)
                                .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                        );
                    }
                });

        // 用户头像上传
        ApiDefinition userProfileApi = new ApiDefinition(SentinelFlowApiConstants.USER_AVATAR_UPLOAD_API)
                .setPredicateItems(new HashSet<ApiPredicateItem>() {
            private static final long serialVersionUID = 1L;
            {
                add(new ApiPathPredicateItem()
                        .setPattern(SentinelFlowApiConstants.USER_AVATAR_UPLOAD_API)
                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                );
            }
        });

        // 用户信息更新
        ApiDefinition userInfoUpdateApi = new ApiDefinition(SentinelFlowApiConstants.USER_INFO_UPDATE_API)
                .setPredicateItems(new HashSet<ApiPredicateItem>() {
                    private static final long serialVersionUID = 1L;
                    {
                        add(new ApiPathPredicateItem()
                                .setPattern(SentinelFlowApiConstants.USER_INFO_UPDATE_API)
                        );
                    }
                });

        definitions.add(userProfileApi);
        definitions.add(userInfoUpdateApi);
        definitions.add(allApi);
        // 加载限流分组
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

}
