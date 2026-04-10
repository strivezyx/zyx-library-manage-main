package fun.cyhgraph.controller;

import fun.cyhgraph.constant.JwtClaimsConstant;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.context.BaseContext;
import fun.cyhgraph.dto.*;
import fun.cyhgraph.entity.LendReturn;
import fun.cyhgraph.entity.Reader;
import fun.cyhgraph.entity.ReaderCategory;
import fun.cyhgraph.mapper.ReaderCategoryMapper;
import fun.cyhgraph.properties.JwtProperties;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.BookService;
import fun.cyhgraph.service.BorrowService;
import fun.cyhgraph.service.ReaderService;
import fun.cyhgraph.utils.JwtUtil;
import fun.cyhgraph.vo.ReaderLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reader/client")
@Slf4j
public class ReaderClientController {

    @Autowired
    private ReaderService readerService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowService borrowService;
    @Autowired
    private ReaderCategoryMapper readerCategoryMapper;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<ReaderLoginVO> login(@RequestBody ReaderLoginDTO readerLoginDTO) {
        Reader reader = readerService.login(readerLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.READER_ID, reader.getId());
        claims.put(JwtClaimsConstant.ROLE, "READER");

        String token = JwtUtil.createJWT(
                jwtProperties.getManagerSecretKey(),
                jwtProperties.getManagerTtl(),
                claims
        );

        ReaderLoginVO readerLoginVO = ReaderLoginVO.builder()
                .id(reader.getId())
                .name(reader.getName())
                .phone(reader.getPhone())
                .token(token)
                .build();
        return Result.success(readerLoginVO);
    }

    @PostMapping("/register")
    public Result register(@RequestBody ReaderRegisterDTO readerRegisterDTO) {
        readerService.register(readerRegisterDTO);
        return Result.success();
    }

    @GetMapping("/profile")
    public Result<Reader> profile() {
        Integer readerId = BaseContext.getCurrentId();
        Reader reader = readerService.getById(readerId);
        return Result.success(reader);
    }

    @PutMapping("/profile")
    public Result updateProfile(@RequestBody ReaderProfileDTO readerProfileDTO) {
        Integer readerId = BaseContext.getCurrentId();
        readerService.updateProfile(readerId, readerProfileDTO);
        return Result.success();
    }

    @GetMapping("/book/page")
    public Result<PageResult> bookPage(BookPageDTO bookPageDTO) {
        bookPageDTO.setStatus("0");
        PageResult pageResult = bookService.page(bookPageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/borrow/page")
    public Result<PageResult> myBorrowPage(LendReturnPageDTO lendReturnPageDTO) {
        lendReturnPageDTO.setRId(BaseContext.getCurrentId());
        PageResult pageResult = borrowService.page(lendReturnPageDTO);
        return Result.success(pageResult);
    }

    @PutMapping("/borrow/return/{id}")
    public Result returnBook(@PathVariable Integer id) {
        Integer readerId = BaseContext.getCurrentId();
        LendReturn lendReturn = borrowService.getById(id);
        if (lendReturn == null) {
            return Result.error(MessageConstant.BORROW_NOT_FOUND);
        }
        if (!readerId.equals(lendReturn.getRId())) {
            return Result.error(MessageConstant.BORROW_NOT_OWNER);
        }
        if (lendReturn.getStatus() != null && lendReturn.getStatus() != 0) {
            return Result.error(MessageConstant.BORROW_ALREADY_RETURNED);
        }

        Reader reader = readerService.getById(readerId);
        ReaderCategory readerCategory = readerCategoryMapper.selectById(reader.getCategoryId());
        long lendDays = ChronoUnit.DAYS.between(lendReturn.getLendDate(), LocalDate.now());
        int returnStatus = lendDays >= readerCategory.getLendPeriod() ? 2 : 1;

        LendReturnDTO lendReturnDTO = new LendReturnDTO();
        lendReturnDTO.setId(lendReturn.getId());
        lendReturnDTO.setRId(lendReturn.getRId());
        lendReturnDTO.setBId(lendReturn.getBId());
        lendReturnDTO.setLendDate(lendReturn.getLendDate());
        lendReturnDTO.setReturnDate(LocalDate.now());
        lendReturnDTO.setStatus(returnStatus);
        lendReturnDTO.setNotes("读者自助还书");
        borrowService.update(lendReturnDTO);

        return Result.success();
    }

    @PutMapping("/password")
    public Result updatePassword(@RequestBody ReaderPasswordDTO readerPasswordDTO) {
        Integer readerId = BaseContext.getCurrentId();
        readerService.updatePassword(readerId, readerPasswordDTO);
        return Result.success();
    }
}
