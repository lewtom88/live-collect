package com.wy.service;

import com.wy.mapper.CommentMapper;
import com.wy.mapper.GameRoundMapper;
import com.wy.mapper.KDAMapper;
import com.wy.mapper.UserMapper;
import com.wy.model.query.GameQuery;
import com.wy.model.vo.Comment;
import com.wy.model.vo.GameRound;
import com.wy.model.vo.KDA;
import com.wy.model.vo.User;
import com.wy.model.query.CommentQuery;
import com.wy.model.query.KDAQuery;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class LiveService {

    Logger logger = LoggerFactory.getLogger(LiveService.class);

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private KDAMapper kdaMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GameRoundMapper gameRoundMapper;

    @Autowired
    private HeroService heroService;

    private GameRound currentGame;

    private Set<String> guessUsers = new HashSet<>();

    /**
     * 完全匹配
     * 例如： 上官婉儿 32 1 2
     *
     */
    private Pattern pFullMatch = Pattern.compile(
            "^([\u4e00-\u9fa5]{1,4}|[a-zA-z]{1,15})\\s*[赢输]?\\s*\\d+(\\s+|-)\\d+(\\s+|-)\\d+$");

    /**
     * 后半部分匹配
     * KDA不大于99
     * 必须包含关键字输或赢
     * 大小写不敏感
     *
     */
    private Pattern pEndMath = Pattern.compile(
            ".+([赢输]|ying|shu)\\s*\\d{1,2}(\\s+|-)\\d{1,2}(\\s+|-)\\d{1,2}$", Pattern.CASE_INSENSITIVE);

    @Test
    public void testFullMath() {
        Assert.assertTrue(pFullMatch.matcher("asddd输 121  2-2").find());
        Assert.assertTrue(pFullMatch.matcher("asddd 赢 12-2-2").find());
        Assert.assertTrue(pFullMatch.matcher("上官婉儿 32 1 2").find());

        Assert.assertFalse(pFullMatch.matcher("asdddddddddddddd").find());
        Assert.assertFalse(pFullMatch.matcher("上官婉儿了").find());
    }

    @Test
    public void testEndMatch() {
        Assert.assertTrue(pEndMath.matcher("feishu 12-12-2").find());
        Assert.assertTrue(pEndMath.matcher("fying 12-12-2").find());
        Assert.assertTrue(pEndMath.matcher("fYing 12-12-2").find());
        Assert.assertTrue(pEndMath.matcher("asddd输 12  2-2").find());
        Assert.assertTrue(pEndMath.matcher("lubanshu3-2-2").find());
        Assert.assertTrue(pEndMath.matcher("上官婉儿 shu 32 1 2").find());
        Assert.assertFalse(pEndMath.matcher("上官婉儿 shu 321 1 2").find());
    }

    /**
     * 开启下一场比赛
     */
    public GameRound createGame() {
        GameRound newGame = new GameRound();
        Integer id = gameRoundMapper.insert(newGame);
        newGame.setId(id);
        currentGame = newGame;

        return newGame;
    }

    public GameRound loadCurrentGame() {
        GameRound lastGame = gameRoundMapper.findCurrent();
        if (lastGame != null) {
            lastGame.setUserCount(guessUsers.size());
        }
        return lastGame;
    }

    public GameRound loadLastClosed() {
        return gameRoundMapper.findLastClosed();
    }

    public List<GameRound> findGames(GameQuery query) {
        return gameRoundMapper.findGames(query);
    }

    /**
     *
     * @return
     */
    public int closeGame(GameRound gameRound) {
        gameRound.setUpdateTime(System.currentTimeMillis());
        gameRoundMapper.updateGame(gameRound);

        //更新Winner
        KDA kda = new KDA();
        kda.setCreateTime(gameRound.getCreateTime());
        kda.setGameId(gameRound.getId());
        kda.setGameResult(gameRound.getGameResult());
        kda.setPlayer(gameRound.getPlayer());
        kda.setKill(gameRound.getKill());
        kda.setDeath(gameRound.getDeath());
        kda.setAssist(gameRound.getAssist());
        kdaMapper.updateWinner(kda);

        return gameRound.getId();
    }

    /**
     * 给WebSocket拿到的数据用
     * @param list
     */
    public void create(List<Comment> list) {
        long current = System.currentTimeMillis();
        List<KDA> kdaList = new ArrayList<>();
        List<User> contactList = new ArrayList<>();
        for (Comment c : list) {
            c.setCreateTime(current);
            if (isGuessingMessage(c.getComment())) {
                c.setType(Comment.TYPE_GUESSING);
                KDA kda = new KDA();
                kda.setName(c.getName());
                kda.setPrincipalId(c.getPrincipalId());
                kda.setComment(c.getComment());
                kda.setCreateTime(current);
                if (guessUsers.contains(kda.getPrincipalId())) {
                    kda.setStatus(KDA.STATUS_INVALID);
                    logger.warn("The user has guessed at this round: {0}", kda.getName());
                } else {
                    kda.setStatus(KDA.STATUS_VALID);
                    guessUsers.add(kda.getPrincipalId());
                }
                parseGuessing1(kda);
                if (currentGame != null) {
                    kda.setGameId(currentGame.getId());
                }

                kdaList.add(kda);
            } else if (isContactMessage(c.getComment())) {
                c.setType(Comment.TYPE_CONTACT_UPDATE);
                User user = new User();
                user.setCreateTime(current);
                user.setPrincipalId(c.getPrincipalId());
                user.setName(c.getName());
                user.setContactType(isQQ(c.getComment()) ? "QQ" : "微信");
                user.setContactNick(c.getComment().substring(3));
                contactList.add(user);
            }
        }

        if (!list.isEmpty()) {
            commentMapper.batchInsert(list);
        }
        if (!kdaList.isEmpty()) {
            kdaMapper.batchInsert(kdaList);
        }
        if (!contactList.isEmpty()) {
            userMapper.batchSaveOrUpdateContact(contactList);
        }
    }

    private boolean isGuessingMessage(String key) {
        if (key == null) {
            return false;
        }
        return pEndMath.matcher(key).find();
    }

    private boolean isContactMessage(String key) {
        if (key == null || key.length() < 4) {
            return false;
        }

        return isQQ(key) || isWeChat(key);
    }

    private boolean isQQ(String key) {
        key = key.toLowerCase();
        return key.startsWith("qq ") || key.startsWith("qq:") || key.startsWith("qq：");
    }

    private boolean isWeChat(String key) {
        return key.startsWith("微信 ") || key.startsWith("微信:") || key.startsWith("微信：");
    }

    @Test
    public void test1() {
        Assert.assertTrue(isGuessingMessage("射手赢 3 2 1"));
        Assert.assertFalse(isGuessingMessage("射手赢 3 2 "));

        KDA kda = new KDA();
        kda.setComment("射手赢 3 2 1");
        LiveService cs = new LiveService();
        cs.parseGuessing1(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda = new KDA();
        kda.setComment("     射手 3 2 1   ");
        cs.parseGuessing1(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda = new KDA();
        kda.setComment("射手 赢    3   2 1  ");
        cs.parseGuessing1(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda = new KDA();
        kda.setComment("射手赢 3-2-1");
        cs.parseGuessing1(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);
    }

    @Test
    public void test() {
        KDA kda = new KDA();
        kda.setComment("kda 射手赢 3 2 1");
        LiveService cs = new LiveService();
        cs.parseGuessing(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getPlayerRole(), "射");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda.setComment("kda 射手 3 2 1");
        cs.parseGuessing(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getPlayerRole(), "射");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda.setComment("kda 射手 赢 3 2 1");
        cs.parseGuessing(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getPlayerRole(), "射");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda.setComment("KDA 射手赢 3-2-1");
        cs.parseGuessing(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getPlayerRole(), "射");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda.setComment("kda 3-2-1 射手赢");
        cs.parseGuessing(kda);
        Assert.assertEquals(kda.getPlayer(), "射手");
        Assert.assertEquals(kda.getPlayerRole(), "射");
        Assert.assertEquals(kda.getGameResult(), "赢");
        Assert.assertEquals(kda.getKill().intValue(), 3);
        Assert.assertEquals(kda.getDeath().intValue(), 2);
        Assert.assertEquals(kda.getAssist().intValue(), 1);

        kda.setComment("kda 13-22-10 边路输");
        cs.parseGuessing(kda);
        Assert.assertEquals(kda.getPlayer(), "边路");
        Assert.assertEquals(kda.getPlayerRole(), "战");
        Assert.assertEquals(kda.getGameResult(), "输");
        Assert.assertEquals(kda.getKill().intValue(), 13);
        Assert.assertEquals(kda.getDeath().intValue(), 22);
        Assert.assertEquals(kda.getAssist().intValue(), 10);
    }

    /**
     * 4 cases:
     *
     * lubanshu3-2-2
     * 鲁班赢 3 2 1
     * 鲁班 赢 3 2 1
     * 鲁班赢 3-2-1
     *
     * @param kda
     */
    private void parseGuessing1(KDA kda) {
        String c = kda.getComment().toLowerCase();
        int index = c.lastIndexOf("[赢输]|ying|shu");
        String heroName = c.substring(0, index);
        String realName = heroService.matchHero(heroName);
        kda.setName(realName);

        String kdaResult;
        String gameResult = c.substring(index, index + 1);
        if (gameResult.equals("y")) {
            gameResult = "赢";
            kdaResult = c.substring(index + 5);
        } else if (gameResult.equals("s")) {
            gameResult = "输";
            kdaResult = c.substring(index + 4);
        } else {
            kdaResult = c.substring(index + 1);
        }

        String[] ca = kdaResult.split("\\s+|-+");

        kda.setGameResult(gameResult);
        kda.setKill(Integer.parseInt(ca[0]));
        kda.setDeath(Integer.parseInt(ca[1]));
        kda.setAssist(Integer.parseInt(ca[2]));
    }

    private void parseGuessing(KDA kda) {
        String c = kda.getComment();
        String[] ca = c.split(" |-");
        if (ca.length < 5) {
            logger.error("Failed to parse the guessing. {}", c);
            return;
        }
        if (isNumeric(ca[1])) {
            parseKDA(ca[1], ca[2], ca[3], kda);
            if (ca.length > 5) {
                parsePlayer(ca[4], ca[5], kda);
            } else {
                parsePlayer(ca[3], ca[4], kda);
            }
        } else {
            parsePlayer(ca[1], ca[2], kda);
            if (ca.length > 5) {
                parseKDA(ca[3], ca[4], ca[5], kda);
            } else {
                parseKDA(ca[2], ca[3], ca[4], kda);
            }
        }
    }

    private void parsePlayer(String s0, String s1, KDA kda) {
        String s = isNumeric(s0) ? s1 : s0;
        if (s.contains("输")) {
            kda.setPlayer(s.replaceAll("输", ""));
            kda.setPlayerRole(convertPlayer(kda.getPlayer()));
            kda.setGameResult("输");
        } else if (s.contains("赢")) {
            kda.setPlayer(s.replaceAll("赢", ""));
            kda.setPlayerRole(convertPlayer(kda.getPlayer()));
            kda.setGameResult("赢");
        } else {
            kda.setPlayer(s0);
            kda.setPlayerRole(convertPlayer(kda.getPlayer()));
            if (!isNumeric(s1)) {
                kda.setGameResult(s1);
            } else {
                //默认是赢
                kda.setComment("赢");
            }
        }
    }

    private void parseKDA(String s, String s1, String s2, KDA kda) {
        kda.setKill(Integer.parseInt(s));
        if (isNumeric(s1)) {
            kda.setDeath(Integer.parseInt(s1));
        }
        if (isNumeric(s2)) {
            kda.setAssist(Integer.parseInt(s2));
        }
    }

    private static boolean isNumeric(final String str) {
        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }

    private String convertPlayer(String player) {
        return null;
    }

    private int convert(String v) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Comment> findComment(CommentQuery query) {
        return commentMapper.find(query);
    }

    public List<KDA> findKDA(KDAQuery query) {
        return kdaMapper.find(query);
    }

    public void startTiming(GameRound game) {
        //重置竞猜用户
        guessUsers = new HashSet<>();

        long current = System.currentTimeMillis();
        game.setCreateTime(current);
        gameRoundMapper.updateTime(game);
    }

    public Comment findById(Integer id) {
       return commentMapper.findById(id);
    }

}
