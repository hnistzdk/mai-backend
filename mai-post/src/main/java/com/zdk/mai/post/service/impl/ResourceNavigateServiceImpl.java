package com.zdk.mai.post.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.file.RemoteFileService;
import com.zdk.mai.common.core.constant.CommonConstants;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.resource.ResourceNavigateModel;
import com.zdk.mai.common.model.resource.request.ResourceNavigateCreateRequest;
import com.zdk.mai.common.model.resource.request.ResourceNavigateSearchRequest;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.post.mapper.ResourceNavigateMapper;
import com.zdk.mai.post.service.IResourceNavigateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 22:36
 */
@Service
public class ResourceNavigateServiceImpl extends BaseServiceImpl<ResourceNavigateMapper,ResourceNavigateModel> implements IResourceNavigateService {

    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public PageInfo<ResourceNavigateModel> getList(ResourceNavigateSearchRequest searchRequest) {

        String category = searchRequest.getCategory();

        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<ResourceNavigateModel> list = lambdaQuery()
                .eq(isOk(category), ResourceNavigateModel::getCategory, category)
                .orderByDesc(ResourceNavigateModel::getId)
                .list();

        return new PageInfo<>(list);
    }

    @Override
    public void create(ResourceNavigateCreateRequest navigateCreateRequest) {

        ResourceNavigateModel model = new ResourceNavigateModel();
        BeanUtils.copyProperties(navigateCreateRequest, model);

        String resourceName = model.getResourceName();
        if (nameExist(null, resourceName)){
            throw new BusinessException(ErrorEnum.UNIQUENESS_ERROR.getCode() , ErrorEnum.UNIQUENESS_ERROR.getMsg());
        }

        baseMapper.insert(model);

    }

    @Override
    public String uploadResourceNavigateLogo(byte[] bytes, String sourceFileName) {

        String base = "/design/resourceNavigate/";

        CommonResult upload = remoteFileService.upload(bytes, sourceFileName, base, SecurityConstants.INNER);

        if (CommonResult.isSuccess(upload)){
            return CommonConstants.DOMAIN_PREFIX + base + sourceFileName;
        }
        return null;
    }

    @Override
    public void update(ResourceNavigateCreateRequest navigateCreateRequest) {

        ResourceNavigateModel model = new ResourceNavigateModel();

        BeanUtils.copyProperties(navigateCreateRequest, model);

        String resourceName = model.getResourceName();
        if (nameExist(model.getId(), resourceName)){
            throw new BusinessException(ErrorEnum.UNIQUENESS_ERROR.getCode() , ErrorEnum.UNIQUENESS_ERROR.getMsg());
        }
        baseMapper.updateById(model);

    }

    @Override
    public void delete(Integer id) {

        baseMapper.deleteById(id);

    }

    @Override
    public List<String> getCategories() {
        List<ResourceNavigateModel> list = lambdaQuery().select(ResourceNavigateModel::getCategory)
                .groupBy(ResourceNavigateModel::getCategory).list();

        return list.stream().map(ResourceNavigateModel::getCategory).collect(Collectors.toList());
    }

    /**
     * 判断资源导航名称是否已经存在
     *
     * @param resourceId   资源导航id
     * @param resourceName 资源导航名称
     * @return
     */
    private boolean nameExist(Integer resourceId, String resourceName) {

        ResourceNavigateModel model = lambdaQuery().eq(ResourceNavigateModel::getResourceName, resourceName).one();
        if (model == null){
            return false;
        }else {
            //更新时的情况
            return resourceId != null && !resourceId.equals(model.getId());
        }
    }
}
