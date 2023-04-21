package com.wy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wy.model.Result;
import com.wy.model.vo.StatData;
import com.wy.model.vo.StatHour;
import com.wy.model.vo.TopComment;
import com.wy.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.*;

@RestController
@CrossOrigin
public class StatController {

    @Autowired
    StatService statService;

    private NumberFormat numberFormat;

    private DateFormat dateFormat;

    public StatController() {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @RequestMapping("/query_stat_data")
    public Result<StatData> queryStatData() {
        StatData statData = new StatData();
        int cc = statService.statCC();
        int todayCC = statService.statTodayCC();
        String comment = format(todayCC, cc);
        statData.setComment(comment);

        int gc = statService.statG();
        int todayGC = statService.statTodayG();
        String gameCount = format(todayGC, gc);
        statData.setGame(gameCount);

        int b = statService.statB();
        int todayB = statService.statTodayB();
        String bonus = format(todayB, b);
        statData.setBonus(bonus);

        int kda = statService.statKDA();
        int todayKDA = statService.statTodayKDA();
        String kdaStat = format(todayKDA, kda);
        statData.setKda(kdaStat);

        StatHour last = statService.findLast();
        String watchLike = format(last.getWatchCount(), last.getLikeCount());
        statData.setWatchLike(watchLike);

        JSONArray watchHistory = new JSONArray();
        List<StatHour> list = statService.findAll();
        for (StatHour statHour : list) {
            if (statService.isPeakTime(statHour.getCreateTime())) {
                JSONObject watch = new JSONObject();
                watch.put("Date", dateFormat.format(statHour.getCreateTime()));
                watch.put("watches", statHour.getWatchCount());
                watchHistory.add(watch);
            }
        }
        statData.setWatchLike(watchHistory.toJSONString());

        int gift = statService.statGF();
        int todayGift = statService.statTodayGF();
        String giftCount = format(todayGift, gift);
        statData.setGift(giftCount);

        JSONArray giftHistory = new JSONArray();
        LinkedHashMap<String, Integer> giftByDay = new LinkedHashMap<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (giftHistory.size() <= 10) {
                StatHour statHour = list.get(i);
                String date = dateFormat.format(statHour.getCreateTime());
                int c = giftByDay.getOrDefault(date, 0);
                c += statHour.getGiftCount();
                giftByDay.put(date, c);
            }
        }
        for (Map.Entry<String, Integer> entry : giftByDay.entrySet()) {
            JSONObject giftC = new JSONObject();
            giftC.put(entry.getKey(), entry.getValue());
        }
        Collections.reverse(giftHistory);
        statData.setGiftHistory(giftHistory.toJSONString());

        List<TopComment> ccs = statService.statAC();
        JSONArray tcs = new JSONArray();
        for (TopComment tc : ccs) {
            JSONObject t = new JSONObject();
            t.put("type", tc.getName());
            t.put("count", tc.getCount());
            tcs.add(t);
        }
        statData.setTopComment(tcs.toJSONString());

        return new Result<>(statData);
    }

    private String format(int today, int all) {
        return numberFormat.format(today) + "/" + numberFormat.format(all);
    }
}
