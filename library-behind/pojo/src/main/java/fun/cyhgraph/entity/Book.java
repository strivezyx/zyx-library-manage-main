package fun.cyhgraph.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("book")
public class Book implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String author;
    private String press;
    private LocalDate publishDate;
    private BigDecimal price;
    private Integer pageNumber;
    private String keywords;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private Integer status;
    private String notes;
    private Integer categoryId;

}
