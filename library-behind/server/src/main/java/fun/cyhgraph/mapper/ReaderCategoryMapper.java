package fun.cyhgraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import fun.cyhgraph.dto.ReaderCatePageDTO;
import fun.cyhgraph.entity.ReaderCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReaderCategoryMapper extends BaseMapper<ReaderCategory> {

    Page<ReaderCategory> page(ReaderCatePageDTO readerCatePageDTO);

    @Select("select amount from r_category where id = #{id}")
    Integer getAmountById(Integer rId);

    @Select("select name from r_category")
    List<String> getNames();

    @Select("select id from r_category")
    List<Integer> getIds();

    @Select("select min(id) from r_category")
    Integer getDefaultId();
}
