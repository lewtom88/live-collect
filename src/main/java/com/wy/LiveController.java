package com.wy;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.model.Result;
import com.wy.model.vo.Comment;
import com.wy.model.vo.KDA;
import com.wy.model.vo.User;
import com.wy.model.vo.Watching;
import com.wy.model.query.KDAQuery;
import com.wy.service.CommentService;
import com.wy.service.UserService;
import com.wy.service.WatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class LiveController {

    Logger logger = LoggerFactory.getLogger(LiveController.class);

    //收集时间
    long collectTime = System.currentTimeMillis();

    //收集时间间隔
    long collectInterval = 1000 * 60;

    @Autowired
    private CommentService commentService;

    @Autowired
    private WatchingService watchingService;

    @Autowired
    private UserService userService;

    @RequestMapping("/start_game")
    public Result<Integer> startGame() {
        Integer r = commentService.startNewGame();
        return new Result<>(r);
    }

    @RequestMapping("/insert_comment")
    public String insertComment(@RequestBody Map<String, List<List<String>>> body) {
        List<List<String>> data =  body.get("data");
        commentService.insertComment(data);
        logger.warn("insert data : " + data);
        return "Success!";
    }

    @RequestMapping("query_kda")
    public Result<PageInfo<KDA>> queryKDA(@RequestBody KDAQuery query) {
        Object valid = query.getValid();
        if (valid != null) {
            if (valid instanceof List) {
                List validList = (List) valid;
                if (validList.isEmpty() || validList.size() >= 2) {
                    query.setValid(null);
                } else {
                    query.setValid(validList.get(0));
                }
            }
        }
        PageHelper.startPage(query.getCurrent(), query.getPageSize());
        List<KDA> list = commentService.findKDA(query);
        PageInfo<KDA> pageInfo = new PageInfo(list);

        return new Result<>(pageInfo);
    }

    @RequestMapping("socket_collect")
    public String socketCollect(@RequestBody Map<String, Object> body) {
        collectWatching(body);
        collectComments(body);
        collectUsers(body);
        return "success";
    }

    private void collectUsers(Map<String, Object> body) {
        List<User> list = new ArrayList<>();
        List<Map<String, Object>> commentFeeds = (List<Map<String, Object>>) body.get("commentFeeds");
        if (commentFeeds != null) {
            long current = System.currentTimeMillis();
            for (Map<String, Object> commentFeed : commentFeeds) {
                if (commentFeed.containsKey("senderState")) {
                    User user = new User();
                    Map<String, String> userFeed = (Map<String, String>) commentFeed.get("user");
                    user.setPrincipalId(userFeed.get("principalId"));
                    user.setName(userFeed.get("userName"));

                    Map<String, Object> senderState = (Map<String, Object>) commentFeed.get("senderState");
                    if (senderState != null) {
                        user.setFansGroupIntimacyLevel((Integer) senderState.get("fansGroupIntimacyLevel"));
                        if (senderState.containsKey("liveFansGroupState")) {
                            Integer inLevel = ((Map<String, Integer>) senderState.get("liveFansGroupState")).get("intimacyLevel");
                            user.setIntimacyLevel(inLevel);
                        }
                        user.setWealthGrade((Integer) senderState.get("wealthGrade"));
                        user.setBadgeKey((String) senderState.get("badgeKey"));
                    }

                    user.setCreateTime(current);
                    list.add(user);
                }
            }
        }

        if (!list.isEmpty()) {
            userService.updateIntimacy(list);
        }
    }

    private void collectComments(Map<String, Object> body) {
        List<Comment> list = new ArrayList<>();
        List<Map<String, Object>> commentFeeds = (List<Map<String, Object>>) body.get("commentFeeds");
        if (commentFeeds != null) {
            for (Map<String, Object> commentFeed : commentFeeds) {
                Comment c = new Comment();
                String content = (String) commentFeed.get("content");
                parseUser(commentFeed, c);
                c.setComment(content);
                c.setType(Comment.TYPE_COMMENT);
                list.add(c);
            }
        }

        List<Map<String, Object>> giftFeeds = (List<Map<String, Object>>) body.get("giftFeeds");
        if (giftFeeds != null) {
            for (Map<String, Object> giftFeed : giftFeeds) {
                Comment c = new Comment();
                parseUser(giftFeed, c);
                c.setGiftId((Integer) giftFeed.get("giftId"));
                c.setRank((Integer) giftFeed.get("rank"));
                c.setComboCount((Integer) giftFeed.get("comboCount"));
                c.setType(Comment.TYPE_GIFT);
                list.add(c);
            }
        }


        List<Map<String, Object>> likeFeeds = (List<Map<String, Object>>) body.get("likeFeeds");
        if (likeFeeds != null) {
            for (Map<String, Object> likeFeed : likeFeeds) {
                Comment c = new Comment();
                parseUser(likeFeed, c);
                c.setType(Comment.TYPE_LIKE);
                list.add(c);
            }
        }

        commentService.create(list);
    }

    private void parseUser(Map<String, Object> commentFeed, Comment c) {
        Map<String, String> user = (Map<String, String>) commentFeed.get("user");
        String principalId = user.get("principalId");
        String userName = user.get("userName");
        c.setName(userName);
        c.setPrincipalId(principalId);
    }

    private void collectWatching(Map<String, Object> body) {
        long current = System.currentTimeMillis();
        if ((current - collectTime) < collectInterval) {
            return;
        }
        collectTime = current;

        String watchingCount = (String) body.get("displayWatchingCount");
        String likeCount = (String) body.get("displayLikeCount");
        int likes = Integer.parseInt(likeCount);
        int watches = 0;
        if ("1万".equals(watchingCount)) {
            watches = 10000;
        }
        Watching watching = new Watching();
        watching.setLikes(likes);
        watching.setWatching(watches);
        watchingService.insert(watching);

        logger.info("Insert watching: {} - {} and likes: {}", watchingCount, watches, likes);
    }

    @RequestMapping("/test")
    public String test() {
        logger.warn("test success.");
        return "Yes, you are right.";
    }
}
