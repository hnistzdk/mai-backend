package com.zdk.mai.common.model.post.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/28 17:50
 */
@Data
public class PostCreateRequest extends BaseCreateRequest {

    @Length(max = 30,message = "标题长度不能大于30个字符")
    private String title;

    @Length(max = 2000,message = "内容不能大于2000个字符")
    private String content;

    private String markdown;

    private String html;

    private Integer type;

    private List<String> images;
}
