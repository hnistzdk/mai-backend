package com.zdk.mai.common.datasource.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdk.mai.common.datasource.utils.ParaValidator;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/28 17:30
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M,T> implements ParaValidator {
}
