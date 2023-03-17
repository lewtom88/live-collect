package com.wy.mapper;

import com.wy.model.vo.KDA;
import com.wy.model.query.KDAQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KDAMapper {

    @Insert("<script>insert into t_live_kda (principal_id,name,comment,kill_count,death,assist,player,player_role,game_result,game_round,create_time, valid) values"
            + "<foreach collection ='list' item='kda' separator =','>"
            + "(#{kda.principalId,jdbcType=VARCHAR},#{kda.name,jdbcType=VARCHAR},#{kda.comment,jdbcType=VARCHAR},#{kda.kill,jdbcType=INTEGER},"
            + "#{kda.death,jdbcType=INTEGER},#{kda.assist,jdbcType=INTEGER},#{kda.player,jdbcType=VARCHAR},"
            + "#{kda.playerRole,jdbcType=VARCHAR},#{kda.gameResult,jdbcType=SMALLINT},"
            + "#{kda.gameRound,jdbcType=SMALLINT},#{kda.createTime,jdbcType=BIGINT},#{kda.valid,jdbcType=TINYINT})"
            + "</foreach></script>")
    void batchInsert(@Param("list") List<KDA> list);

    @Results({
            @Result(property = "principalId", column = "principal_id"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
            @Result(property = "contactNick", column = "contact_nick"),
            @Result(property = "kill", column = "kill_count"),
            @Result(property = "gameResult", column = "game_result"),
            @Result(property = "gameRound", column = "game_round"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
    })
    @Select("<script>select k.id,k.principal_id,k.name,k.comment,k.valid,k.kill_count,k.death,k.assist,k.player,k.player_role," +
            "k.game_result,k.game_round,k.create_time,u.contact_id,u.contact_nick,u.contact_type " +
            "from t_live_kda k LEFT JOIN t_live_user u ON k.principal_id = u.principal_id where 1 = 1" +
            "<if test='query.principalId != null'> and k.principal_id=#{query.principalId}</if>" +
            "<if test='query.name != null'> and k.name=#{query.name}</if>" +
            "<if test='query.comment != null'> and k.comment=#{query.query.comment}</if>" +
            "<if test='query.kill != null'> and k.kill_count = #{query.kill}</if>" +
            "<if test='query.death != null'> and k.death = #{query.death}</if>" +
            "<if test='query.assist != null'> and k.assist = #{query.assist}</if>" +
            "<if test='query.player != null'> and k.player = #{query.player}</if>" +
            "<if test='query.playerRole != null'> and k.player_role = #{query.playerRole}</if>" +
            "<if test='query.gameRound != null'> and k.game_round = #{query.gameRound}</if>" +
            "<if test='query.gameResult != null'> and k.game_result = #{query.gameResult}</if>" +
            "<if test='query.valid != null'> and k.valid = #{query.valid}</if>" +
            "<if test='query.startTime != null'> and k.create_time >= #{query.startTime}</if>" +
            "<if test='query.endTime != null'> and k.create_time &lt;= #{query.endTime}</if>" +
            "<if test='query.contactType != null'> and u.contact_type = #{query.contactType}</if>" +
            "</script>")
    List<KDA> find(@Param("query") KDAQuery query);
}
