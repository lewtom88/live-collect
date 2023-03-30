package com.wy.util;

import com.wy.model.vo.GameRound;

public class GameUtils {

    /**
     * createTime        updateTime
     * null              null                    挑战状态
     * not_null          null                    计时状态
     * not_null          not_null                结算后状态
     *
     * @param game
     * @return
     */
    public static boolean isChallenge(GameRound game) {
        return game.getCreateTime() == null && game.getUpdateTime() == null;
    }

    public static boolean isTiming(GameRound game) {
        return game.getCreateTime() != null && game.getUpdateTime() == null;
    }

    public static boolean isClose(GameRound game) {
        return game.getCreateTime() != null && game.getUpdateTime() != null;
    }
}
