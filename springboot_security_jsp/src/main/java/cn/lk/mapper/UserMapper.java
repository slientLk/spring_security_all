package cn.lk.mapper;

import cn.lk.domain.SysUser;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<SysUser> {

    @Select("select * from sys_user where username = #{username}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "roles", column = "id", javaType = List.class,
                    many = @Many(select = "cn.lk.mapper.RoleMapper.findByUid"))
    })
    public SysUser findByUsername(String username);
}
