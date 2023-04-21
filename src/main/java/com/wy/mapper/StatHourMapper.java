package com.wy.mapper;

import com.wy.model.vo.StatHour;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatHourMapper {

    @Insert("insert into t_live_stat_hour (watch_count,comment_count,kda_count,gift_count,like_count," +
            "game_count,bonus_total,contact_update_count,create_time,update_time) " +
            "values (#{watchCount},#{commentCount},#{kdaCount},#{giftCount},#{likeCount}," +
            "#{gameCount},#{bonusTotal},#{contactUpdateCount},#{createTime},#{updateTime})")
    void insert(StatHour statHour);

    @Results({
            @Result(property = "watchCount", column = "watch_count"),
            @Result(property = "commentCount", column = "watch_count"),
            @Result(property = "kdaCount", column = "kda_count"),
            @Result(property = "giftCount", column = "gift_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "gameCount", column = "game_count"),
            @Result(property = "bonusCount", column = "bonus_count"),
            @Result(property = "contactUpdateCount", column = "contact_update_count"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
    })
    @Select("select * from t_live_stat_hour order by id desc")
    StatHour findLast();

    @Select("select sum(comment_count) from t_live_stat_hour")
    int statComment();

    @Select("select sum(game_count) from t_live_stat_hour")
    int statGame();

    @Select("select sum(bonus_total) from t_live_stat_hour")
    int statBonus();

    @Select("select sum(kda_count) from t_live_stat_hour")
    int statKDA();

    @Results({
            @Result(property = "watchCount", column = "watch_count"),
            @Result(property = "commentCount", column = "watch_count"),
            @Result(property = "kdaCount", column = "kda_count"),
            @Result(property = "giftCount", column = "gift_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "gameCount", column = "game_count"),
            @Result(property = "bonusCount", column = "bonus_count"),
            @Result(property = "contactUpdateCount", column = "contact_update_count"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
    })
    @Select("select * from t_live_stat_hour order by id")
    List<StatHour> findAll();

    @Select("select sum(gift_count) from t_live_stat_hour")
    int statGift();
}
