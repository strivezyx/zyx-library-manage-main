package fun.cyhgraph.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.dto.BookDTO;
import fun.cyhgraph.dto.BookPageDTO;
import fun.cyhgraph.entity.Book;
import fun.cyhgraph.exception.BaseException;
import fun.cyhgraph.mapper.BookMapper;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 新增书籍
     * @param bookDTO
     */
    public void addBook(BookDTO bookDTO) {
        if (bookDTO.getId() != null) {
            Book existing = bookMapper.selectById(bookDTO.getId());
            if (existing != null) {
                throw new BaseException(MessageConstant.BOOK_ID_EXISTS);
            }
        }
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        book.setStatus(0); // 刚开始添加，书还没被借出
        bookMapper.insert(book);
    }

    /**
     * 条件书籍分页查询
     * @param bookPageDTO
     * @return
     */
    public PageResult page(BookPageDTO bookPageDTO) {
        PageHelper.startPage(bookPageDTO.getPage(), bookPageDTO.getPageSize());
        Page<Book> bookPage = bookMapper.page(bookPageDTO);
        return new PageResult(bookPage.getTotal(), bookPage.getResult());
    }

    /**
     * 根据id查询书籍
     * @param id
     * @return
     */
    public Book getById(Integer id) {
        return bookMapper.selectById(id);
    }

    /**
     * 修改书籍信息
     * @param bookDTO
     */
    public void update(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        bookMapper.updateById(book);
    }

    /**
     * 根据id修改书籍状态
     * @param id
     */
    public void status(Integer id) {
        bookMapper.status(id);
    }

    /**
     * 批量删除书籍
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Integer borrowedCount = bookMapper.countBorrowedByIds(ids);
        if (borrowedCount != null && borrowedCount > 0) {
            throw new BaseException(MessageConstant.BOOK_BORROWED_CANNOT_DELETE);
        }
        bookMapper.deleteBatch(ids);
    }

}
