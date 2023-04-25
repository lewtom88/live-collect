package com.wy.service;

import com.wy.mapper.ChallengeMapper;
import com.wy.mapper.KDAMapper;
import com.wy.model.query.ChallengeQuery;
import com.wy.model.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChallengeService {

    @Autowired
    private KDAMapper kdaMapper;

    @Autowired
    private ChallengeMapper challengeMapper;

    @Autowired
    private LiveService liveService;

    public void createGroupByKDA(List<Integer> list, Integer gameId) {
        Long current = System.currentTimeMillis();
        List<Challenge> challenges = new ArrayList<>();
        for (Integer kdaId : list) {
            KDA kda = kdaMapper.findById(kdaId);
            Challenge c = new Challenge();
            c.setName(kda.getName());
            c.setPrincipalId(kda.getPrincipalId());
            c.setGameId(gameId);
            c.setKdaId(kdaId);
            c.setCreateTime(current);
            challenges.add(c);
            kda.setStatus(KDA.STATUS_CHALLENGE);
            kdaMapper.update(kda);
        }
        if (!challenges.isEmpty()) {
            challengeMapper.batchInsert(challenges);
        }
    }

    public void createGroupByComment(List<Integer> commentIdList, Integer gameId) {
        Long current = System.currentTimeMillis();
        List<Challenge> challenges = new ArrayList<>();
        for (Integer id : commentIdList) {
            Comment comment = liveService.findById(id);
            Challenge c = new Challenge();
            c.setName(comment.getName());
            c.setPrincipalId(comment.getPrincipalId());
            c.setGameId(gameId);
            c.setCreateTime(current);
            challenges.add(c);
        }
        if (!challenges.isEmpty()) {
            challengeMapper.batchInsert(challenges);
        }
    }

    public List<Challenge> findChallenge(ChallengeQuery query) {
        return challengeMapper.find(query);
    }

    public List<Challenge> findByIds(List<Integer> idList) {
        if (idList == null || idList.isEmpty()) {
            return new ArrayList<>();
        }
        return challengeMapper.findByIds(idList);
    }

}
