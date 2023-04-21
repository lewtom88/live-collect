package com.wy.service;

import com.wy.mapper.WatchMapper;
import com.wy.model.vo.Watch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WatchService {

    @Autowired
    private WatchMapper watchMapper;

    public void collect(Watch watch) {
        Long current = System.currentTimeMillis();
        watch.setCreateTime(current);
        watchMapper.insert(watch);
    }
}
