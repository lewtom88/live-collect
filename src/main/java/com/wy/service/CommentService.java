package com.wy.service;

import com.wy.mapper.CommentMapper;
import com.wy.mapper.GameRoundMapper;
import com.wy.mapper.KDAMapper;
import com.wy.mapper.UserMapper;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Component
public class CommentService {

    Logger logger = LoggerFactory.getLogger(CommentService.class);

    private AtomicInteger round;

    private Long gameTime;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private KDAMapper kdaMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GameRoundMapper gameRoundMapper;

    private Set<String> guessUsers = new HashSet<>();

    private Pattern pattern = Pattern.compile("[射手|大射|法师|大法|战士|边路|坦边|坦克|打野|野核|辅助|游走] *[赢|输]* \\d+[ |-]+\\d+[ |-]+\\d+");

    private static Map<String, String> playerMapping = new HashMap<>();

    static {
        playerMapping.put("战", "战/坦");
        playerMapping.put("战边", "战/坦");
        playerMapping.put("边路", "战/坦");
        playerMapping.put("肉", "战/坦");
        playerMapping.put("坦克", "战/坦");
        playerMapping.put("坦边", "战/坦");
        playerMapping.put("战士", "战/坦");

        playerMapping.put("法", "法师");
        playerMapping.put("大法", "法师");

        playerMapping.put("野", "打野");
        playerMapping.put("野核", "打野");

        playerMapping.put("辅", "辅助");
        playerMapping.put("游走", "辅助");

        playerMapping.put("射", "射手");
        playerMapping.put("大射", "射手");
    }

    /**
     * 开启下一场比赛
     */
    public int startNewGame() {
        if (round == null) {
            gameTime = System.currentTimeMillis();
            GameRound gameRound = gameRoundMapper.findLatest();
            if (gameRound == null || gameTime - gameRound.getCreateTime() > 1000 * 60 * 60) {
                round = new AtomicInteger(1);
            } else {
                Integer r = gameRound.getGameRound();
                round = new AtomicInteger(r + 1);
            }
        } else {
            round.incrementAndGet();
            gameTime = System.currentTimeMillis();
        }

        GameRound newGame = new GameRound();
        newGame.setGameRound(round.get());
        newGame.setCreateTime(gameTime);
        gameRoundMapper.insert(newGame);

        logger.info("{} attended in this match.", guessUsers.size());
        guessUsers = new HashSet<>();
        logger.info("Start new game...round: {}", round.get());

        return round.get();
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
                    kda.setValid(0);
                    logger.warn("The user has guessed at this round: {0}", kda.getName());
                } else {
                    kda.setValid(1);
                    guessUsers.add(kda.getPrincipalId());
                }
                parseGuessing1(kda);
                if (round != null) {
                    kda.setGameRound(round.get());
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
        return pattern.matcher(key).find();
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
        CommentService cs = new CommentService();
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
        CommentService cs = new CommentService();
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
     * kda 射手赢 3 2 1
     * kda 射手 3 2 1
     * kda 射手 赢 3 2 1
     * KDA 射手赢 3-2-1
     * kda 3-2-1 射手赢
     * @param kda
     */
    private void parseGuessing1(KDA kda) {
        String c = kda.getComment().trim();
        String[] ca = c.split("[ |-]+");
        if (ca.length < 4) {
            logger.error("Failed to parse the guessing. {}", c);
            return;
        }
        if (ca.length == 5) {
            kda.setPlayer(ca[0]);
            kda.setPlayerRole(convertPlayer(kda.getPlayer()));
            kda.setGameResult(ca[1]);
            kda.setKill(Integer.parseInt(ca[2]));
            kda.setDeath(Integer.parseInt(ca[3]));
            kda.setAssist(Integer.parseInt(ca[4]));
        } else if (ca.length == 4) {
            if (ca[0].length() <= 2) {
                kda.setPlayer(ca[0]);
                kda.setPlayerRole(convertPlayer(kda.getPlayer()));
                kda.setGameResult("赢");
            } else {
                kda.setPlayer(ca[0].substring(0, 2));
                kda.setPlayerRole(convertPlayer(kda.getPlayer()));
                kda.setGameResult(ca[0].substring(2));
            }
            kda.setKill(Integer.parseInt(ca[1]));
            kda.setDeath(Integer.parseInt(ca[2]));
            kda.setAssist(Integer.parseInt(ca[3]));
        } else {
            logger.error("Failed to parse the comment: " + c);
        }
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

    /**
     * 给JS前端抓的数据用
     * @param data
     */
    public void insertComment(List<List<String>> data) {
        /*long current = System.currentTimeMillis();
        List<Comment> list = new ArrayList();
        List<KDA> kdaList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Comment ksComment = new Comment();
            List<String> comment = data.get(i);
            ksComment.setName(comment.get(0));
            ksComment.setComment(comment.get(1));
            ksComment.setCreateTime(current);
            //ksComment.setType(CommentType.getValue(comment.get(1), comment.get(0)));
            list.add(ksComment);

            if (ksComment.getType() == CommentType.GUESS.getValue()) {
                KDA kda = new KDA();
                kda.setName(ksComment.getName());
                kda.setComment(ksComment.getComment());
                if (guessUsers.contains(ksComment.getName())) {
                    kda.setInvalid(true);
                    logger.warn("The user has guessed at this round: {0}", ksComment);
                }
                String[] kdaArray = ksComment.getComment().split(" ");
                try {
                    if (kdaArray.length > 1) {
                        kda.setKill(convert(kdaArray[1]));
                    }
                    if (kdaArray.length > 2) {
                        kda.setDeath(convert(kdaArray[2]));
                    }
                    if (kdaArray.length > 3) {
                        kda.setAssist(convert(kdaArray[3]));
                    }
                    if (kdaArray.length > 4) {
                        String s = kdaArray[4];
                        if (s.contains("输")) {
                            kda.setPlayer(s.replaceAll("输", ""));
                            kda.setPlayerRole(convertPlayer(kda.getPlayer()));
                            kda.setGameResult("输");
                        } else if (s.contains("赢")) {
                            kda.setPlayer(s.replaceAll("赢", ""));
                            kda.setPlayerRole(convertPlayer(kda.getPlayer()));
                            kda.setGameResult("赢");
                        }
                    }
                    if (kdaArray.length > 5) {
                        if (kda.getGameResult() == null) {
                            kda.setGameResult(kdaArray[5]);
                        } else {
                            kda.setWechat(kdaArray[5]);
                        }
                    }
                    if (kdaArray.length > 6) {
                        kda.setWechat(kdaArray[6]);
                    }
                } catch (Exception e) {
                    logger.error("Failed to parse the game guess.", e);
                }
                kda.setGameRound(round.get());
                kda.setCreateTime(current);

                kdaList.add(kda);
            } else if (ksComment.getType() == CommentType.START_TAG.getValue()) {
                round.incrementAndGet();
                guessUsers = new HashSet<>();
                logger.warn("The game round has been changed to " + round.get());
            }
        }

        if (!list.isEmpty()) {
            commentMapper.batchInsert(list);
        }
        if (!kdaList.isEmpty()) {
            kdaMapper.batchInsert(kdaList);
        }*/
    }

    private String convertPlayer(String player) {
        return playerMapping.get(player);
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

}
