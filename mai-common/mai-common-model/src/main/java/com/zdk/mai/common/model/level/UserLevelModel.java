package com.zdk.mai.common.model.level;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 11:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_user_level")
public class UserLevelModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "level_id",type = IdType.AUTO)
    private Integer levelId;

    private Long userId;

    private String username;

    private String level;

    private Long points;

}
