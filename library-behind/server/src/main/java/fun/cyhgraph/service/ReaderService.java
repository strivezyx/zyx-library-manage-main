package fun.cyhgraph.service;

import fun.cyhgraph.dto.ReaderDTO;
import fun.cyhgraph.dto.ReaderLoginDTO;
import fun.cyhgraph.dto.ReaderPageDTO;
import fun.cyhgraph.dto.ReaderPasswordDTO;
import fun.cyhgraph.dto.ReaderProfileDTO;
import fun.cyhgraph.dto.ReaderRegisterDTO;
import fun.cyhgraph.entity.Reader;
import fun.cyhgraph.result.PageResult;

import java.util.List;

public interface ReaderService {
    void addReader(ReaderDTO readerDTO);

    PageResult page(ReaderPageDTO readerPageDTO);

    Reader getById(Integer id);

    void update(ReaderDTO readerDTO);

    void deleteBatch(List<Integer> ids);

    Reader login(ReaderLoginDTO readerLoginDTO);

    void updatePassword(Integer id, ReaderPasswordDTO readerPasswordDTO);

    void register(ReaderRegisterDTO readerRegisterDTO);

    void updateProfile(Integer id, ReaderProfileDTO readerProfileDTO);
}
