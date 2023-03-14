package com.wy.mapper;

import com.wy.model.vo.KDA;
import com.wy.model.query.KDAQuery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KDAMapper {

    @Insert("<script>insert into t_live_kda (principal_id,name,comment,kill_count,death,assist,player,player_role,game_result,game_round,create_time, invalid) values"
            + "<foreach collection ='list' item='kda' separator =','>"
            + "(#{kda.principalId,jdbcType=VARCHAR},#{kda.name,jdbcType=VARCHAR},#{kda.comment,jdbcType=VARCHAR},#{kda.kill,jdbcType=INTEGER},"
            + "#{kda.death,jdbcType=INTEGER},#{kda.assist,jdbcType=INTEGER},#{kda.player,jdbcType=VARCHAR},"
            + "#{kda.playerRole,jdbcType=VARCHAR},#{kda.gameResult,jdbcType=SMALLINT},"
            + "#{kda.gameRound,jdbcType=SMALLINT},#{kda.createTime,jdbcType=BIGINT},#{kda.invalid,jdbcType=TINYINT})"
            + "</foreach></script>")
    void batchInsert(@Param("list") List<KDA> list);


    @Select("<script>select id,name,comment,kill_count,death,assist,player,player_role,game_result,game_round,create_time from t_live_kda where 1 = 1" +
            "<if test='query.principalId != null'> and principal_id=#{query.principalId}</if>" +
            "<if test='query.name != null'> and name=#{query.name}</if>" +
            "<if test='query.comment != null'> and comment=#{query.query.comment}</if>" +
            "<if test='query.kill != null'> and kill_count = #{query.kill}</if>" +
            "<if test='query.death != null'> and death = #{query.death}</if>" +
            "<if test='query.assist != null'> and assist = #{query.assist}</if>" +
            "<if test='query.player != null'> and player = #{query.player}</if>" +
            "<if test='query.playerRole != null'> and player_role = #{query.playerRole}</if>" +
            "<if test='query.gameRound != null'> and game_round = #{query.gameRound}</if>" +
            "<if test='query.gameResult != null'> and game_result = #{query.gameResult}</if>" +
            "<if test='query.invalid != null'> and invalid = #{query.invalid}</if>" +
            "<if test='query.startTime != null'> and create_time >= #{query.startTime}</if>" +
            "<if test='query.endTime != null'> and create_time &lt;= #{query.endTime}</if>" +
            "<if test='query.sortField != null'> order by #{query.sortField}</if>" +
            "<if test='query.sortType != null'> #{query.sortType}</if></script>")
    List<KDA> find(@Param("query") KDAQuery query);
}
