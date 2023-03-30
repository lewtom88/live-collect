package com.wy;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.model.Result;
import com.wy.model.query.ChallengeQuery;
import com.wy.model.query.GameQuery;
import com.wy.model.vo.Challenge;
import com.wy.model.vo.GameRound;
import com.wy.model.vo.User;
import com.wy.service.ChallengeService;
import com.wy.service.LiveService;
import com.wy.util.GameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class GameController {

    @Autowired
    LiveService liveService;

    @Autowired
    ChallengeService challengeService;

    @RequestMapping("/start_timing")
    public Result<Integer> startTiming() {
        Result<Integer> r = new Result<>();
        GameRound game = liveService.loadCurrentGame();
        if (game == null || GameUtils.isClose(game)) {
            r.setErrorMsg("请先设置挑战选手");
            return r;
        }
        if (GameUtils.isTiming(game)) {
            r.setErrorMsg("游戏已经进入计时状态");
            return r;
        }
        liveService.startTiming(game);
        r.setData(game.getId());

        return r;
    }

    @RequestMapping("/timing_game")
    public Result<GameRound> loadTimingGame() {
        GameRound game = liveService.loadCurrentGame();
        Result<GameRound> r = new Result<>();
        if (game == null || GameUtils.isClose(game)) {
            r.setErrorMsg("没有需要结算的比赛");
            return r;
        }
        if (GameUtils.isChallenge(game)) {
            r.setErrorMsg("比赛尚未开始计时，无法结算");
            return r;
        }

        r.setData(game);
        return r;
    }

    @RequestMapping("/close_game")
    public Result<Integer> closeGame(@RequestBody GameRound game) {
        String player = game.getPlayer();
        if (player != null) {
            player = player.split(",")[0];
            game.setPlayer(player);
        }
        int r = liveService.closeGame(game);
        return new Result<>(r);
    }

    @RequestMapping("/last_game")
    public Result<GameRound> loadClosedGame() {
        Result<GameRound> r = new Result<>();
        GameRound game = liveService.loadLastClosed();
        r.setData(game);
        r.setSuccess(true);

        return r;
    }

    @RequestMapping("/query_game")
    public Result<PageInfo<GameRound>> queryGame(@RequestBody GameQuery query) {
        PageHelper.startPage(query.getCurrent(), query.getPageSize());
        List<GameRound> list = liveService.findGames(query);
        List<Integer> gameIds = list.stream().map(g -> g.getId()).collect(Collectors.toList());

        List<Challenge> challenges = challengeService.findByIds(gameIds);
        Map<Integer, StringBuilder> builderMap = new HashMap<>();
        for (Challenge c : challenges) {
            StringBuilder sb = builderMap.getOrDefault(c.getGameId(), new StringBuilder());
            builderMap.put(c.getGameId(), sb);
            if (sb.length() == 0) {
                sb.append(c.getContactType()).append(":");
            }
            sb.append(c.getContactId());
        }
        for (GameRound gameRound : list) {
            StringBuilder sb = builderMap.get(gameRound.getId());
            gameRound.setGameUsers(sb.toString());
        }

        PageInfo<GameRound> pageInfo = new PageInfo(list);

        return new Result<>(pageInfo);
    }

}
