package com.wy;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.model.Result;
import com.wy.model.query.UserQuery;
import com.wy.model.vo.User;
import com.wy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/query_fans")
    public Result<PageInfo<User>> queryFans(@RequestBody UserQuery query) {
        String key = query.getKeyword();
        if ("QQ".equalsIgnoreCase(key) || "微信".equals(key)) {
            query.setContactType(key);
            query.setKeyword(null);
        }

        PageHelper.startPage(query.getCurrent(), query.getPageSize());
        List<User> list = userService.queryUsers(query);
        PageInfo<User> pageInfo = new PageInfo(list);
        return new Result<>(pageInfo);
    }

}
