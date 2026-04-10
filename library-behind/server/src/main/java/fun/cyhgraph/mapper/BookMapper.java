package fun.cyhgraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import fun.cyhgraph.dto.BookPageDTO;
import fun.cyhgraph.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    Page<Book> page(BookPageDTO bookPageDTO);

    void deleteBatch(List<Integer> ids);

    Integer countBorrowedByIds(@Param("ids") List<Integer> ids);

    @Update("update book set status = IF(status = 1, 0, 1) where id = #{id}")
    void status(Integer id);

    @Select("select count(id) from book where category_id = #{id}")
    Integer sumByCategoryId(Integer id);
}
