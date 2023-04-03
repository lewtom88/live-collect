package com.wy.service;

import com.wy.mapper.UserMapper;
import com.wy.model.query.UserQuery;
import com.wy.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class UserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 确保不用频繁更新
     */
    private Set<String> updatedUsers = new HashSet<>();

    /**
     * 更新用户亲密度
     * @param list
     */
    public void updateIntimacy(List<User> list) {
        List<User> updates = new ArrayList<>();
        for (User user : list) {
            if (!updates.contains(user.getPrincipalId())) {
                updates.add(user);
                updatedUsers.add(user.getPrincipalId());
            }
        }
        if (!updates.isEmpty()) {
            userMapper.batchSaveOrUpdateIntimacy(list);
        }
    }

    public List<User> queryUsers(UserQuery query) {
        List<User> list = userMapper.findUsers(query);
        return list;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void resetUpdateUsers() {
        updatedUsers = new HashSet<>();
    }
}
