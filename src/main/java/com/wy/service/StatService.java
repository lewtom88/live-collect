package com.wy.service;

import com.wy.mapper.*;
import com.wy.model.vo.Comment;
import com.wy.model.vo.StatHour;
import com.wy.model.vo.TopComment;
import com.wy.model.vo.Watch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class StatService {

    Logger logger = LoggerFactory.getLogger(StatService.class);

    @Autowired
    StatHourMapper statHourMapper;
    @Autowired
    WatchMapper watchMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    KDAMapper kdaMapper;
    @Autowired
    GameRoundMapper gameRoundMapper;

    private final long HOUR = 1000 * 60 * 60;

    private final long DAY = 24 * HOUR;

    private Long peakTime;

    public StatService() {
        peakTime = parseCurrentDay(System.currentTimeMillis());
        //每晚22点
        peakTime += HOUR * 22;
    }

   // @Scheduled(cron = "0 3 */1 * * ?")
    public void stat() {
        long current = System.currentTimeMillis();
        long currentHour = parseCurrentHour(current);
        long endHour = currentHour + HOUR;

        int c = commentMapper.statCount(currentHour, endHour, null);
        if (c == 0) {
            logger.warn("No need to stat.");
            return;
        }

        StatHour statHour = new StatHour();
        int commentCount = commentMapper.statCount(currentHour, endHour, Comment.TYPE_COMMENT);
        int giftCount = commentMapper.statCount(currentHour, endHour, Comment.TYPE_GIFT);
        int likeCount = commentMapper.statCount(currentHour, endHour, Comment.TYPE_LIKE);
        int kdaCount = commentMapper.statCount(currentHour, endHour, Comment.TYPE_GUESSING);
        int contactUpdateCount = commentMapper.statCount(currentHour, endHour, Comment.TYPE_CONTACT_UPDATE);
        statHour.setCommentCount(commentCount);
        statHour.setGiftCount(giftCount);
        statHour.setLikeCount(likeCount);
        statHour.setKdaCount(kdaCount);
        statHour.setContactUpdateCount(contactUpdateCount);

        int gameCount = gameRoundMapper.statCount(currentHour, endHour);
        int bonus = gameRoundMapper.statBonus(currentHour, endHour);
        statHour.setGameCount(gameCount);
        statHour.setBonusAmount(bonus);

        Watch watch = watchMapper.findRecent(currentHour);
        String value = watch.getWatch();
        int watchCount = parseWatchValue(value);
        statHour.setWatchCount(watchCount);

        statHour.setCreateTime(currentHour);
        statHourMapper.insert(statHour);
    }

    private int parseWatchValue(String value) {
        if (value == null) {
            return 0;
        }

        int c = 0;
        int unit = 1;
        if (value.endsWith("万")) {
            c = Integer.parseInt(value.substring(0, value.length() - 1));
            unit = 10000;
        } else if (value.endsWith("千")) {
            c = Integer.parseInt(value.substring(0, value.length() - 1));
            unit = 1000;
        } else {
            c = Integer.parseInt(value);
        }
        return c * unit;
    }

    long parseCurrentHour(long time) {
        long v = time / HOUR;
        return v * HOUR;
    }

    long parseCurrentDay(long time) {
        long v = time / DAY;
        return v * DAY;
    }

    public static void main(String[] args) {
        Date date = new Date();
        long v = date.getTime() / 1000 / 60 / 60;
        System.out.println(new Date(v * 1000 * 60 * 60));
    }

    public int statCC() {
        return statHourMapper.statComment();
    }

    /**
     * 今天是从中午12点到晚上2点
     * @return
     */
    public int statTodayCC() {
        long[] timeRange = getTimeRange();
        return commentMapper.statCount(timeRange[0], timeRange[1], null);
    }

    public int statG() {
        return statHourMapper.statGame();
    }

    public int statTodayG() {
        long[] timeRange = getTimeRange();
        return gameRoundMapper.statCount(timeRange[0], timeRange[1]);
    }

    private long[] getTimeRange() {
        long current = System.currentTimeMillis();
        long today = parseCurrentDay(current);
        long start = today + 12 * HOUR;
        long end = start + 14 * HOUR;
        return new long[] {start, end};
    }

    public int statB() {
        return statHourMapper.statBonus();
    }

    public int statTodayB() {
        long[] timeRange = getTimeRange();
        return gameRoundMapper.statBonus(timeRange[0], timeRange[1]);
    }

    public int statKDA() {
        return statHourMapper.statKDA();
    }

    public int statTodayKDA() {
        long[] timeRange = getTimeRange();
        return kdaMapper.statCount(timeRange[0], timeRange[1]);
    }

    public StatHour findLast() {
        return statHourMapper.findLast();
    }

    public List<StatHour> findAll() {
        return statHourMapper.findAll();
    }

    public boolean isPeakTime(long time) {
        return (peakTime - time) % DAY == 0;
    }

    public int statGF() {
        return statHourMapper.statGift();
    }

    public int statTodayGF() {
        long[] timeRange = getTimeRange();
        return commentMapper.statCount(timeRange[0], timeRange[1], Comment.TYPE_GIFT);
    }

    public List<TopComment> statAC() {
        long[] timeRange = getTimeRange();
        return commentMapper.top10ByName(timeRange[0], timeRange[1]);
    }
}
