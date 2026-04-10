package fun.cyhgraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import fun.cyhgraph.dto.ReaderPageDTO;
import fun.cyhgraph.entity.Reader;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReaderMapper extends BaseMapper<Reader> {

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("insert into reader (name, category_id, sex, w_address, h_address, phone, email, create_time, notes, password) " +
            "values (#{name}, #{categoryId}, #{sex}, #{wAddress}, #{hAddress}, #{phone}, #{email}, #{createTime}, #{notes}, #{password})")
    int insertAutoIncrement(Reader reader);

    Page<Reader> page(ReaderPageDTO readerPageDTO);

    @Select("select * from reader where phone = #{phone}")
    Reader getByPhone(String phone);

    void deleteBatch(List<Integer> ids);

    @Select("select count(id) from reader where category_id = #{id}")
    Integer sumByCategoryId(Integer id);

    @Update("update reader set password = #{password} where id = #{id}")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);
}
