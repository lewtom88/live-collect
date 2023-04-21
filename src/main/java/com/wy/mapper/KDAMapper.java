package com.wy.mapper;

import com.wy.model.vo.KDA;
import com.wy.model.query.KDAQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KDAMapper {

    @Insert("<script>insert into t_live_kda (principal_id,name,comment,kill_count,death,assist,player,player_role,game_result,game_id,create_time, status) values"
            + "<foreach collection ='list' item='kda' separator =','>"
            + "(#{kda.principalId,jdbcType=VARCHAR},#{kda.name,jdbcType=VARCHAR},#{kda.comment,jdbcType=VARCHAR},#{kda.kill,jdbcType=INTEGER},"
            + "#{kda.death,jdbcType=INTEGER},#{kda.assist,jdbcType=INTEGER},#{kda.player,jdbcType=VARCHAR},"
            + "#{kda.playerRole,jdbcType=VARCHAR},#{kda.gameResult,jdbcType=SMALLINT},"
            + "#{kda.gameId,jdbcType=SMALLINT},#{kda.createTime,jdbcType=BIGINT},#{kda.status,jdbcType=TINYINT})"
            + "</foreach></script>")
    void batchInsert(@Param("list") List<KDA> list);

    @Results({
            @Result(property = "principalId", column = "principal_id"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
            @Result(property = "kill", column = "kill_count"),
            @Result(property = "gameResult", column = "game_result"),
            @Result(property = "gameId", column = "game_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
    })
    @Select("<script>select k.id,k.principal_id,k.name,k.comment,k.status,k.kill_count,k.death,k.assist,k.player,k.player_role," +
            "k.game_result,k.game_id,k.create_time,u.contact_id,u.contact_type " +
            "from t_live_kda k LEFT JOIN t_live_user u ON k.principal_id = u.principal_id where 1 = 1" +
            "<if test='query.principalId != null'> and k.principal_id=#{query.principalId}</if>" +
            "<if test='query.name != null'> and k.name=#{query.name}</if>" +
            "<if test='query.comment != null'> and k.comment=#{query.query.comment}</if>" +
            "<if test='query.kill != null'> and k.kill_count = #{query.kill}</if>" +
            "<if test='query.death != null'> and k.death = #{query.death}</if>" +
            "<if test='query.assist != null'> and k.assist = #{query.assist}</if>" +
            "<if test='query.player != null and query.player != \"\"'> and k.player = #{query.player}</if>" +
            "<if test='query.playerRole != null'> and k.player_role = #{query.playerRole}</if>" +
            "<if test='query.gameId != null'> and k.game_id = #{query.gameId}</if>" +
            "<if test='query.gameResult != null'> and k.game_result = #{query.gameResult}</if>" +
            "<if test='query.errorFlag != null'> and k.player is null</if>" +
            "<if test='query.status != null'> and k.status in " +
            "<foreach item='item' index='index' collection='query.status' open='(' separator=',' close=')'>#{item}</foreach>" +
            "</if>" +
            "<if test='query.startTime != null'> and k.create_time >= #{query.startTime}</if>" +
            "<if test='query.endTime != null'> and k.create_time &lt;= #{query.endTime}</if>" +
            "<if test='query.contactType != null'> and u.contact_type = #{query.contactType}</if>" +
            "</script>")
    List<KDA> find(@Param("query") KDAQuery query);

    @Results({
            @Result(property = "principalId", column = "principal_id"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
            @Result(property = "kill", column = "kill_count"),
            @Result(property = "gameResult", column = "game_result"),
            @Result(property = "gameId", column = "game_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
    })
    @Select("<script>select k.id,k.principal_id,k.name,k.comment,k.status,k.kill_count,k.death," +
            "k.assist,k.player,k.player_role,k.game_result,k.game_id,k.create_time " +
            "from t_live_kda k where k.id=#{id}</script>")
    KDA findById(Integer id);

    @Update("update t_live_kda set status = #{status} where id = #{id}")
    void update(KDA kda);

    @Update("update t_live_kda set winner = 1 where create_time >= #{createTime} and game_result = #{gameResult}" +
            " and game_id = #{gameId} and player = #{player} and kill_count = #{kill} " +
            "and death = #{death} and assist = #{assist}")
    void updateWinner(KDA kda);

    @Select("select count(0) from t_live_kda where create_time >= #{start} and create_time <= #{end}")
    int statCount(long start, long end);
}
