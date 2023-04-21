package com.wy.mapper;

import com.wy.model.vo.Watch;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WatchMapper {

    @Insert("insert into t_live_watch (watch,likes,create_time) values (#{watch},#{likes},#{createTime})")
    void insert(Watch watch);

    @Select("select * from t_live_watch where 1 = 1 " +
            "<if test='startTime != null'>and create_time>= #{startTime}</if>")
    List<Watch> find(Long startTime);

    @Select("select * from t_live_watch where create_time >= #{start} limit 1")
    Watch findRecent(long start);
}
