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
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "contactType", column = "contact_type"),
            @Result(property = "contactId", column = "contact_id"),
    })
    @Select("<script>select c.id,c.principal_id,c.name,c.comment,c.c_type,c.gift_id,c.combo_count,c.fans_rank," +
            "c.create_time,u.contact_type,u.contact_id " +
            "from t_live_comment c LEFT JOIN t_live_user u ON c.principal_id=u.principal_id where 1 = 1" +
            "<if test='query.name != null'> and c.name=#{query.name}</if>" +
            "<if test='query.principalId != null'> and c.principal_id=#{query.principalId}</if>" +
            "<if test='query.comment != null'> and c.comment=#{query.comment}</if>" +
            "<if test='query.rank != null'> and c.fans_rank=#{query.rank}</if>" +
            "<if test='query.startTime != null'> and c.create_time >= 1000 * UNIX_TIMESTAMP(STR_TO_DATE(#{query.startTime}, '%Y-%m-%d %H:%i:%s'))</if>" +
            "<if test='query.endTime != null'> and c.create_time &lt;= 1000 * UNIX_TIMESTAMP(STR_TO_DATE(#{query.endTime}, '%Y-%m-%d %H:%i:%s'))</if>" +
            "<if test='query.type != null'> and c.c_type=#{query.type}</if>" +
            "<if test='query.contactType != null'> and u.contact_type=#{query.contactType}</if>" +
            "order by c.create_time desc</script>")
    List<Comment> find(@Param("query") CommentQuery query);

    @Results({
            @Result(property = "type", column = "c_type"),
            @Result(property = "giftId", column = "gift_id"),
            @Result(property = "comboCount", column = "combo_count"),
            @Result(property = "rank", column = "fans_rank"),
            @Result(property = "principalId", column = "principal_id"),
            @Result(property = "createTime", column = "create_time")
    })
    @Select("select id,principal_id,name,comment,c_type,gift_id,combo_count," +
            "fans_rank,create_time from t_live_comment where id = #{id}")
    Comment findById(Integer id);

}
