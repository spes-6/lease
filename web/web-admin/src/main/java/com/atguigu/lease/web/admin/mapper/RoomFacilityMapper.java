package com.atguigu.lease.web.admin.mapper;

import com.atguigu.lease.model.entity.RoomFacility;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author liubo
* @description 针对表【room_facility(房间&配套关联表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.atguigu.lease.model.RoomFacility
*/
@Mapper
public interface RoomFacilityMapper extends BaseMapper<RoomFacility> {

}




