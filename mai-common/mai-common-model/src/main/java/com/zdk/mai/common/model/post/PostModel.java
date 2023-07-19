package com.zdk.mai.common.model.post;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/28 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_post")
public class PostModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    /** 贴子ID */
    @TableId(value = "post_id",type = IdType.ASSIGN_ID)
    private Long postId;

    /** 题图 */
    private String titleMap;

    /** 标题 */
    private String title;

    /** 贴子内容 */
    private String content;

    /** 贴子html内容 */
    private String html;

    /** 贴子markdown内容 */
    private String markdown;

    /** 状态(0禁用 1启动) */
    private Boolean state;

    /** 贴子浏览量 */
    private Long pv;

    /** 置顶(数字大优先级越高) */
    private Boolean top;

    /**
     * 1是贴子 2是职言
     */
    private Integer type;

    /**
     * 贴子图片链接 ,分割
     */
    private String images;

    /** 发帖人id */
    private Long authorId;

    /** 发帖人用户名id */
    private String authorUsername;

}
