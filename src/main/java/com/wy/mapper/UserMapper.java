package com.wy.mapper;

import com.wy.model.query.UserQuery;
import com.wy.model.vo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("<script>insert into t_live_user (principal_id,name,intimacy_level," +
            "fans_group_intimacy_level,wealth_grade,badge_key,create_time) values " +
            "<foreach collection ='list' item='user' separator =','> " +
            "(#{user.principalId,jdbcType=VARCHAR},#{user.name,jdbcType=VARCHAR}," +
            "#{user.intimacyLevel,jdbcType=INTEGER},#{user.fansGroupIntimacyLevel,jdbcType=INTEGER}," +
            "#{user.wealthGrade,jdbcType=INTEGER},#{user.badgeKey,jdbcType=VARCHAR},#{user.createTime,jdbcType=BIGINT}) " +
            "</foreach>" +
            "ON duplicate KEY UPDATE " +
            "name=values(name), intimacy_level=values(intimacy_level), fans_group_intimacy_level=values(fans_group_intimacy_level)," +
            "wealth_grade=values(wealth_grade), badge_key=values(badge_key)" +
            "</script>")
    void batchSaveOrUpdateIntimacy(@Param("list") List<User> list);

    @Insert("<script>insert into t_live_user (principal_id,name,contact_type,contact_nick,create_time,update_time) values " +
            "<foreach collection ='list' item='user' separator =','> " +
            "(#{user.principalId,jdbcType=VARCHAR},#{user.name,jdbcType=VARCHAR}," +
            "#{user.contactType,jdbcType=VARCHAR},#{user.contactNick,jdbcType=VARCHAR}," +
            "#{user.createTime,jdbcType=BIGINT},#{user.createTime,jdbcType=BIGINT}) " +
            "</foreach>" +
            "ON duplicate KEY UPDATE " +
            "name=values(name), contact_type=values(contact_type), contact_nick=values(contact_nick)," +
            "update_time=values(create_time)" +
            "</script>")
    void batchSaveOrUpdateContact(@Param("list") List<User> list);

    @Results({
            @Result(property = "principalId", column = "principal_id"),
            @Result(property = "intimacyLevel", column = "intimacy_level"),
            @Result(property = "fansGroupIntimacyLevel", column = "fans_group_intimacy_level"),
            @Result(property = "wealthGrade", column = "wealth_grade"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
            @Result(property = "contactNick", column = "contact_nick"),
            @Result(property = "badgeKey", column = "badge_key"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
    })
    @Select("<script>select id,principal_id,name,intimacy_level,fans_group_intimacy_level,wealth_grade," +
            "badge_key,contact_id,contact_nick,contact_type,create_time,update_time from t_live_user where 1 = 1" +
            "<if test='query.name != null'> and name=#{query.name}</if>" +
            "<if test='query.principalId != null'> and principal_id=#{query.principalId}</if>" +
            "<if test='query.contactType != null'> and contact_type=#{query.contactType}</if>" +
            "<if test='query.keyword != null and query.keyword != \"\"'> and (name=#{query.keyword} or principal_id=#{query.keyword})</if>" +
            "order by update_time desc</script>")
    List<User> findUsers(@Param("query") UserQuery userQuery);
}
