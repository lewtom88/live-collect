package com.wy;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.model.Result;
import com.wy.model.query.CommentQuery;
import com.wy.model.vo.Comment;
import com.wy.model.vo.User;
import com.wy.service.LiveService;
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
public class CommentController {

    Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private LiveService liveService;

    @RequestMapping("/query_comment")
    public Result<PageInfo<User>> queryComment(@RequestBody CommentQuery query) {
        PageHelper.startPage(query.getCurrent(), query.getPageSize());
        List<Comment> list = liveService.findComment(query);
        PageInfo<User> pageInfo = new PageInfo(list);

        return new Result<>(pageInfo);
    }

}
