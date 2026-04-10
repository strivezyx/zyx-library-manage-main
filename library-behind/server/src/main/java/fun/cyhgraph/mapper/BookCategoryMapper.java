package fun.cyhgraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import fun.cyhgraph.dto.BookCatePageDTO;
import fun.cyhgraph.entity.BookCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {

    Page<BookCategory> page(BookCatePageDTO bookCatePageDTO);

    @Select("select name from b_category")
    List<String> getNames();

    @Select("select id from b_category")
    List<Integer> getIds();
}
