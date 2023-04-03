package com.wy;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.model.Result;
import com.wy.model.query.ChallengeQuery;
import com.wy.model.vo.Challenge;
import com.wy.model.vo.GameRound;
import com.wy.service.ChallengeService;
import com.wy.service.LiveService;
import com.wy.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ChallengeController {

    Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private LiveService liveService;

    @RequestMapping("/query_challenge")
    public Result<PageInfo<Challenge>> queryChallenge(@RequestBody ChallengeQuery query) {
        PageHelper.startPage(query.getCurrent(), query.getPageSize());
        List<Challenge> list = challengeService.findChallenge(query);
        PageInfo<Challenge> pageInfo = new PageInfo(list);

        return new Result<>(pageInfo);
    }

    /**
     * 挑战
     * @param list
     * @return
     */
    @RequestMapping("/challenge_kda")
    public Result<Integer> challengeKDA(@RequestBody List<Integer> list) {
        Result<Integer> r = new Result<>();
        GameRound game = liveService.loadCurrentGame();
        if (game == null || GameUtils.isClose(game)) {
            game = liveService.createGame();
        }

        if (list != null && !list.isEmpty()) {
            challengeService.createGroupByKDA(list, game.getGameId());
        }
        r.setData(game.getGameId());

        return r;
    }

    /**
     * 挑战
     * @param list
     * @return
     */
    @RequestMapping("/challenge_comment")
    public Result<Integer> challengeComment(@RequestBody List<Integer> list) {
        Result<Integer> r = new Result<>();
        GameRound game = liveService.loadCurrentGame();
        if (game == null || GameUtils.isClose(game)) {
            game = liveService.createGame();
        }
        if (list != null && !list.isEmpty()) {
            challengeService.createGroupByComment(list, game.getGameId());
        }
        r.setData(game.getGameId());

        return r;
    }
}


