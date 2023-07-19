package com.zdk.mai.common.model.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/5 16:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_role")
public class RoleModel implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    private Long roleId;

    /** 角色名称*/
    private String roleName;

    /** 角色权限字符串 ,号分割*/
    private String roleKey;

    /** 角色显示顺序*/
    private Integer roleSort;

    /** 创建者 */
    private Long createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private Long updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
