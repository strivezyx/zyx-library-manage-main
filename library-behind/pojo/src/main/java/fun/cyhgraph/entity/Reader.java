package fun.cyhgraph.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("reader")
public class Reader implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer sex;
    @JsonProperty("wAddress")
    private String wAddress;
    @JsonProperty("hAddress")
    private String hAddress;
    private String phone;
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private String notes;
    private Integer categoryId;
    @JsonIgnore
    private String password;
}
