package com.wy.mapper;

import com.wy.model.vo.Comment;
import com.wy.model.query.CommentQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("insert into t_live_comment (name,principal_id,comment,gift_id,combo_count,fans_rank,c_type,create_time)" +
            " values (#{name},#{principalId},#{comment},#{giftId},#{comboCount},#{rank},#{type},#{createTime})")
    void insert(Comment ksComment);

    @Insert("<script>insert into t_live_comment (name,principal_id,comment,gift_id,combo_count,fans_rank,c_type,create_time) values " +
            "<foreach collection ='list' item='comm' separator =','> " +
            "(#{comm.name,jdbcType=VARCHAR},#{comm.principalId,jdbcType=VARCHAR},#{comm.comment,jdbcType=VARCHAR}," +
            "#{comm.giftId,jdbcType=INTEGER},#{comm.comboCount,jdbcType=INTEGER},#{comm.rank,jdbcType=INTEGER}," +
            "#{comm.type,jdbcType=SMALLINT},#{comm.createTime,jdbcType=BIGINT})"
            + "</foreach></script>")
    void batchInsert(@Param("list") List<Comment> list);

    @Results({
            @Result(property = "type", column = "c_type"),
            @Result(property = "giftId", column = "gift_id"),
            @Result(property = "comboCount", column = "combo_count"),
            @Result(property = "rank", column = "fans_rank"),
            @Result(property = "principalId", column = "principal_id"),
            @Result(property = "createTime", column = "create_time")
    })
    @Select("<script>select id,principal_id,name,comment,c_type,gift_id,combo_count," +
            "fans_rank,create_time from t_live_comment where 1 = 1" +
            "<if test='query.name != null'> and name=#{query.name}</if>" +
            "<if test='query.principalId != null'> and principal_id=#{query.principalId}</if>" +
            "<if test='query.comment != null'> and comment=#{query.comment}</if>" +
            "<if test='query.rank != null'> and fans_rank=#{query.rank}</if>" +
            "<if test='query.startTime != null'> and create_time >= 1000 * UNIX_TIMESTAMP(STR_TO_DATE(#{query.startTime}, '%Y-%m-%d %H:%i:%s'))</if>" +
            "<if test='query.endTime != null'> and create_time &lt;= 1000 * UNIX_TIMESTAMP(STR_TO_DATE(#{query.endTime}, '%Y-%m-%d %H:%i:%s'))</if>" +
            "<if test='query.type != null'> and c_type=#{query.type}</if>" +
            "order by create_time desc</script>")
    List<Comment> find(@Param("query") CommentQuery query);

}
