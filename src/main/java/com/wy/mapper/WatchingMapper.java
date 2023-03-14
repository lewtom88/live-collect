package com.wy.mapper;

import com.wy.model.vo.Watching;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WatchingMapper {

    @Insert("insert into t_live_watching (watching,likes,create_time) values (#{watching},#{likes},#{createTime})")
    void insert(Watching watching);
}
