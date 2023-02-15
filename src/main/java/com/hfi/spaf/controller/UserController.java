package com.hfi.spaf.controller;

import com.hfi.spaf.dto.common.ApiResponse;
import com.hfi.spaf.dto.system.request.UserDto;
import com.hfi.spaf.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * (User)表控制层
 *
 * @author txjava
 * @since 2023-02-15 15:57:15
 */
@RestController
@RequestMapping("user")
@Api(tags = {"【用户管理接口】"})
@Valid
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;


    /**
     * 新增数据
     *
     * @param user 实体
     * @return 新增结果
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增用户信息")
    public ApiResponse addUser(@RequestBody @Validated(UserDto.ct.class) UserDto user) {
        return userService.insert(user);
    }

    /**
     * 查询数据
     *
     * @return 新增结果
     */
    @PostMapping(value = "/enquiries")
    @ApiOperation(value = "查询用户信息")
    public ApiResponse enquiriesUser(@RequestBody UserDto userdto) {
        return userService.enquiriesUser(userdto);
    }


    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    @ApiOperation(value = "删除用户信息")
    public ApiResponse deleteUserById(Integer id) {
        return userService.deleteById(id);
    }


}

