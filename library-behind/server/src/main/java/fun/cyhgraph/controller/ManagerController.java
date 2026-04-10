package fun.cyhgraph.controller;

import fun.cyhgraph.constant.JwtClaimsConstant;
import fun.cyhgraph.dto.ManagerDTO;
import fun.cyhgraph.dto.ManagerLoginDTO;
import fun.cyhgraph.entity.Manager;
import fun.cyhgraph.properties.JwtProperties;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.ManagerService;
import fun.cyhgraph.utils.JwtUtil;
import fun.cyhgraph.vo.ManagerLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manager")
@Slf4j
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private JwtProperties jwtProperties;

    @GetMapping("/{id}")
    public Result<Manager> getUserById(@PathVariable Integer id){
        Manager manager = managerService.getManagerById(id);
        return Result.success(manager);
    }

    @PostMapping("/login")
    public Result<ManagerLoginVO> login(@RequestBody ManagerLoginDTO managerLoginDTO){
        log.info("manager login: {}", managerLoginDTO.getName());
        Manager manager = managerService.login(managerLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.MANAGER_ID, manager.getId());
        claims.put(JwtClaimsConstant.ROLE, "MANAGER");

        String token = JwtUtil.createJWT(
                jwtProperties.getManagerSecretKey(),
                jwtProperties.getManagerTtl(),
                claims);

        ManagerLoginVO managerLoginVO = ManagerLoginVO.builder()
                .id(manager.getId())
                .name(manager.getName())
                .token(token)
                .build();
        return Result.success(managerLoginVO);
    }

    @PostMapping("/register")
    public Result register(@RequestBody ManagerLoginDTO managerLoginDTO){
        managerService.register(managerLoginDTO);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody ManagerDTO managerDTO){
        managerService.update(managerDTO);
        return Result.success();
    }
}
