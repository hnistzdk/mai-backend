package com.zdk.mai.common.es.model;

import cn.easyes.annotation.HighLight;
import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.Analyzer;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.annotation.rely.IdType;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.es.EsBaseModel;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/21 21:14
 */
@Data
@IndexName("post")
public class EsPostModel extends EsBaseModel {

    /** 贴子ID */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long postId;

    /**
     * 文档标题,不指定类型默认被创建为keyword_text类型,可进行精确查询
     */
    private String title;

    /**
     * 文章内容（纯文本）
     */
    @HighLight(mappingField = "highlightContent",preTag = "<em style='color:red'>")
    @IndexField(fieldType = FieldType.TEXT,analyzer = Analyzer.IK_MAX_WORD,searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String content;
    /**
     * 高亮返回值被映射的字段
     */
    private String highlightContent;


    /** 状态(0禁用 1启动) */
    private Boolean state;

    /** 贴子浏览量 */
    private Long pv;

    /** 置顶(数字大优先级越高) */
    private Long top;

    /** 发帖人id */
    private Long authorId;

    /** 发帖人用户名id */
    private String authorUsername;

    /** 发帖人头像 */
    private String avatar;

    /**
     * 贴子点赞数量
     */
    private Long likeCount;

    /**
     * 该用户是否已点赞此贴子
     */
    private Boolean like;

    /**
     * 贴子评论数
     */
    private Long commentCount;


    private Integer type;

    private String images;
}
