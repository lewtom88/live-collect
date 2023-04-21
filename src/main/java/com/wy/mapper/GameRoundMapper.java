package com.wy.mapper;

import com.wy.model.query.GameQuery;
import com.wy.model.vo.GameRound;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameRoundMapper {

    @Insert("insert into t_live_game (user_count,game_result,player,kill_count,death,assist,create_time) " +
            "values (#{userCount},#{gameResult},#{player},#{kill},#{death},#{assist},#{createTime})")
    @Options(useGeneratedKeys=true, keyColumn = "id", keyProperty = "gameId")
    Integer insert(GameRound gameRound);

    @Results({
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "userCount", column = "user_count"),
            @Result(property = "gameResult", column = "game_result"),
            @Result(property = "kill", column = "kill_count"),
            @Result(property = "gameId", column = "id"),
    })
    @Select("select id,user_count,game_result,player,kill_count,death,assist,create_time,update_time " +
            "from t_live_game order by id desc limit 1")
    GameRound findCurrent();

    @Results({
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "userCount", column = "user_count"),
            @Result(property = "gameResult", column = "game_result"),
            @Result(property = "kill", column = "kill_count"),
            @Result(property = "gameId", column = "id"),
    })
    @Select("select id,user_count,game_result,player,kill_count,death,assist,create_time,update_time " +
            "from t_live_game where update_time is not null order by id desc limit 1")
    GameRound findLastClosed();


    @Update("update t_live_game set user_count=#{userCount},game_result=#{gameResult}," +
            "player=#{player},kill_count=#{kill},death=#{death},assist=#{assist},play_mode=#{playMode}," +
            "bonus_mode=#{bonusMode},bonus_amount=#{bonusAmount},update_time=#{updateTime} where id=#{gameId}")
    void updateGame(GameRound gameRound);

    @Update("update t_live_game set create_time=#{createTime} where id=#{gameId}")
    void updateTime(GameRound gameRound);

    @Results({
            @Result(property = "userCount", column = "user_count"),
            @Result(property = "gameResult", column = "game_result"),
            @Result(property = "playMode", column = "play_mode"),
            @Result(property = "kill", column = "kill_count"),
            @Result(property = "bonusAmount", column = "bonus_amount"),
            @Result(property = "bonusMode", column = "bonus_mode"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "gameId", column = "id"),
    })
    @Select("select * from t_live_game order by create_time desc")
    List<GameRound> findGames(GameQuery query);

    @Select("select count(0) from t_live_game where create_time >= #{start} and create_time <= #{end}")
    int statCount(long start, long end);

    @Select("select sum(bonus_amount) from t_live_game where create_time >= #{start} and create_time <= #{end}")
    int statBonus(long start, long end);
}
