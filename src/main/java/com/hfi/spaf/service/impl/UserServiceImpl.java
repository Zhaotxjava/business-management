package com.hfi.spaf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.hfi.spaf.dto.common.ApiResponse;
import com.hfi.spaf.dto.system.request.UserDto;
import com.hfi.spaf.entity.User;
import com.hfi.spaf.mapper.UserMapper;
import com.hfi.spaf.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author txjava
 * @since 2023-02-15 15:57:15
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public ApiResponse insert(UserDto userDto) {
        if(userMapper.selectCount(new QueryWrapper<User>().lambda()
                .eq(User::getPhone, userDto.getPhone()))>0){
            return ApiResponse.fail("请勿重复提交!");
        }
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        userMapper.insert(user);
        return ApiResponse.success("成功!");
    }


    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public ApiResponse deleteById(Integer id) {
        if (userMapper.deleteById(id) > 0){
            return ApiResponse.success("删除成功！") ;
        }
       return  ApiResponse.fail("删除失败！");
    }

    @Override
    public ApiResponse enquiriesUser(UserDto userdto) {
        PageHelper.startPage(userdto.getPageNum(), userdto.getPageSize());
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderByDesc("create_time");
        return ApiResponse.success(userMapper.selectList(wrapper));
    }
}
