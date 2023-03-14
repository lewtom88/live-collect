package com.wy.service;

import com.wy.mapper.WatchingMapper;
import com.wy.model.vo.Watching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WatchingService {

    @Autowired
    WatchingMapper watchingMapper;

    public void insert(Watching watching) {
        watching.setCreateTime(System.currentTimeMillis());
        watchingMapper.insert(watching);
    }
}
