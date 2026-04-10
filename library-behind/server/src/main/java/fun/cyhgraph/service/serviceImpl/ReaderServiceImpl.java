package fun.cyhgraph.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.dto.ReaderDTO;
import fun.cyhgraph.dto.ReaderLoginDTO;
import fun.cyhgraph.dto.ReaderPageDTO;
import fun.cyhgraph.dto.ReaderPasswordDTO;
import fun.cyhgraph.dto.ReaderProfileDTO;
import fun.cyhgraph.dto.ReaderRegisterDTO;
import fun.cyhgraph.entity.Reader;
import fun.cyhgraph.exception.PasswordErrorException;
import fun.cyhgraph.exception.ReaderNotFoundException;
import fun.cyhgraph.mapper.ReaderCategoryMapper;
import fun.cyhgraph.mapper.ReaderMapper;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.service.ReaderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class ReaderServiceImpl implements ReaderService {

    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_ADDRESS = "未填写";
    private static final int DEFAULT_SEX = 1;

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private ReaderCategoryMapper readerCategoryMapper;

    public void addReader(ReaderDTO readerDTO) {
        Reader reader = new Reader();
        BeanUtils.copyProperties(readerDTO, reader);
        reader.setId(null);
        reader.setPassword(DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes()));
        readerMapper.insertAutoIncrement(reader);
    }

    public PageResult page(ReaderPageDTO readerPageDTO) {
        PageHelper.startPage(readerPageDTO.getPage(), readerPageDTO.getPageSize());
        Page<Reader> readerPage = readerMapper.page(readerPageDTO);
        return new PageResult(readerPage.getTotal(), readerPage.getResult());
    }

    public Reader getById(Integer id) {
        return readerMapper.selectById(id);
    }

    public void update(ReaderDTO readerDTO) {
        Reader reader = new Reader();
        BeanUtils.copyProperties(readerDTO, reader);
        readerMapper.updateById(reader);
    }

    public void deleteBatch(List<Integer> ids) {
        readerMapper.deleteBatch(ids);
    }

    @Override
    public Reader login(ReaderLoginDTO readerLoginDTO) {
        Reader reader = readerMapper.getByPhone(readerLoginDTO.getPhone());
        if (reader == null) {
            throw new ReaderNotFoundException(MessageConstant.READER_NOT_FOUND);
        }

        String inputPassword = DigestUtils.md5DigestAsHex(readerLoginDTO.getPassword().getBytes());
        String storedPassword = reader.getPassword();

        if (storedPassword == null || storedPassword.isBlank()) {
            String defaultPassword = DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes());
            if (!inputPassword.equals(defaultPassword)) {
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
            }
            return reader;
        }

        if (!inputPassword.equals(storedPassword)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        return reader;
    }

    @Override
    public void updatePassword(Integer id, ReaderPasswordDTO readerPasswordDTO) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new ReaderNotFoundException(MessageConstant.READER_NOT_FOUND);
        }

        String oldPwd = DigestUtils.md5DigestAsHex(readerPasswordDTO.getOldPwd().getBytes());
        if (!oldPwd.equals(reader.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        String newPwd = DigestUtils.md5DigestAsHex(readerPasswordDTO.getNewPwd().getBytes());
        readerMapper.updatePassword(id, newPwd);
    }

    @Override
    public void register(ReaderRegisterDTO readerRegisterDTO) {
        Reader reader = new Reader();
        BeanUtils.copyProperties(readerRegisterDTO, reader);
        reader.setId(null);

        String name = readerRegisterDTO.getName();
        if (name == null || name.isBlank()) {
            reader.setName(readerRegisterDTO.getPhone());
        }

        Integer defaultCategoryId = readerCategoryMapper.getDefaultId();
        reader.setCategoryId(defaultCategoryId == null ? 1 : defaultCategoryId);
        reader.setSex(DEFAULT_SEX);

        String wAddress = readerRegisterDTO.getWAddress();
        String hAddress = readerRegisterDTO.getHAddress();
        if ((wAddress == null || wAddress.isBlank()) && hAddress != null && !hAddress.isBlank()) {
            wAddress = hAddress;
        }
        if ((hAddress == null || hAddress.isBlank()) && wAddress != null && !wAddress.isBlank()) {
            hAddress = wAddress;
        }
        if (wAddress == null || wAddress.isBlank()) {
            wAddress = DEFAULT_ADDRESS;
        }
        if (hAddress == null || hAddress.isBlank()) {
            hAddress = DEFAULT_ADDRESS;
        }
        reader.setWAddress(wAddress);
        reader.setHAddress(hAddress);

        String password = DigestUtils.md5DigestAsHex(readerRegisterDTO.getPassword().getBytes());
        reader.setPassword(password);
        readerMapper.insertAutoIncrement(reader);
    }

    @Override
    public void updateProfile(Integer id, ReaderProfileDTO readerProfileDTO) {
        Reader existing = readerMapper.selectById(id);
        if (existing == null) {
            throw new ReaderNotFoundException(MessageConstant.READER_NOT_FOUND);
        }

        Reader reader = new Reader();
        reader.setId(id);
        reader.setPhone(readerProfileDTO.getPhone());
        reader.setEmail(readerProfileDTO.getEmail());
        reader.setNotes(readerProfileDTO.getNotes());
        reader.setWAddress(readerProfileDTO.getWAddress());
        reader.setHAddress(readerProfileDTO.getHAddress());
        readerMapper.updateById(reader);
    }
}
