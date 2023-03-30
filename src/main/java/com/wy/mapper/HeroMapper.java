package com.wy.mapper;

import com.wy.model.vo.Hero;
import com.wy.model.vo.HeroRole;
import com.wy.model.vo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HeroMapper {

    @Select("select id,name,alias from t_live_hero")
    List<Hero> findAllHeros();

    @Results({
            @Result(property = "roleName", column = "role_name")
    })
    @Select("select id, role_name from t_live_role")
    List<Role> findAllRoles();

    @Results({
            @Result(property = "heroId", column = "hero_id"),
            @Result(property = "roleId", column = "role_id"),
    })
    @Select("select id, hero_id, role_id from t_live_hero_role")
    List<HeroRole> findAllHeroRoles();
}
