package com.hfi.spaf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfi.spaf.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
