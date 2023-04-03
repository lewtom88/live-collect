package com.wy.mapper;

import com.wy.model.query.ChallengeQuery;
import com.wy.model.vo.Challenge;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChallengeMapper {

    @Insert("<script>insert into t_live_challenge (name,principal_id,game_id,kda_id,create_time) values " +
            "<foreach collection ='list' item='c' separator =','> " +
            "(#{c.name,jdbcType=VARCHAR},#{c.principalId,jdbcType=VARCHAR},#{c.gameId,jdbcType=INTEGER}," +
            "#{c.kdaId,jdbcType=INTEGER},#{c.createTime,jdbcType=BIGINT})"
            + "</foreach></script>")
    void batchInsert(List<Challenge> list);

    @Results({
            @Result(property = "gameId", column = "game_id"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
            @Result(property = "createTime", column = "create_time"),
    })
    @Select("SELECT c.game_id,c.name,u.contact_type,u.contact_id,c.create_time from t_live_challenge c " +
            "LEFT JOIN t_live_user u ON c.principal_id = u.principal_id order by c.create_time desc")
    List<Challenge> find(@Param("query") ChallengeQuery query);

    @Results({
            @Result(property = "gameId", column = "game_id"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
            @Result(property = "createTime", column = "create_time"),
    })
    @Select("<script>SELECT c.game_id,c.name,u.contact_type,u.contact_id,c.create_time from t_live_challenge c " +
            "LEFT JOIN t_live_user u ON c.principal_id = u.principal_id where c.game_id in " +
            "<foreach item='item' index='index' collection='idList' " +
            "open='(' separator=',' close=')'>#{item}</foreach>" +
            "</script>")
    List<Challenge> findByIds(List<Integer> idList);
}
