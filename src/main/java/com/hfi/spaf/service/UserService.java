package com.hfi.spaf.service;

import com.hfi.spaf.dto.common.ApiResponse;
import com.hfi.spaf.dto.system.request.UserDto;
import com.hfi.spaf.entity.User;

/**
 * (User)表服务接口
 *
 * @author tx
 * @since 2023-02-15 15:57:15
 */
public interface UserService {

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    ApiResponse insert(UserDto user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    ApiResponse deleteById(Integer id);

    ApiResponse enquiriesUser(UserDto userdto);
}
