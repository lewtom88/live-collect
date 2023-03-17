package com.wy.mapper;

import com.wy.model.vo.GameRound;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GameRoundMapper {

    @Insert("insert into t_live_game (game_round, create_time) values (#{gameRound}, #{createTime})")
    public void insert(GameRound gameRound);

    @Results({
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "gameRound", column = "game_round")
    })
    @Select("select id, game_round, create_time from t_live_game order by id desc limit 1")
    public GameRound findLatest();
}
