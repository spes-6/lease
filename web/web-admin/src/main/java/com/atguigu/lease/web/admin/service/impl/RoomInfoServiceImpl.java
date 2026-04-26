package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private RoomAttrValueService roomAttrValueService;
    @Autowired
    private RoomFacilityService roomFacilityService;
    @Autowired
    private RoomLabelService roomLabelService;
    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;
    @Autowired
    private RoomLeaseTermService roomLeaseTermService;
    @Override
    public void saveOrUpdateRoomSubmitVo(RoomSubmitVo roomSubmitVo) {
        super.saveOrUpdate(roomSubmitVo);
        if(roomSubmitVo.getId()!=null){
            //删除
            //1.删除原有graphInfoList
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphQueryWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);

            LambdaQueryWrapper<RoomAttrValue> attrQueryWrapper = new LambdaQueryWrapper<>();
            attrQueryWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(attrQueryWrapper);

            LambdaQueryWrapper<RoomFacility> roomFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomFacilityLambdaQueryWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityLambdaQueryWrapper);

            LambdaQueryWrapper<RoomLabel> roomLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomLabelLambdaQueryWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(roomLabelLambdaQueryWrapper);

            LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomPaymentTypeLambdaQueryWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(roomPaymentTypeLambdaQueryWrapper);

            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermLambdaQueryWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            roomLeaseTermService.remove(roomLeaseTermLambdaQueryWrapper);
        }

        //批量添加
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }
        //2.保存新的roomAttrValueList
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if (!CollectionUtils.isEmpty(attrValueIds)) {
            List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue.builder().roomId(roomSubmitVo.getId()).attrValueId(attrValueId).build();
                roomAttrValueList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueList);
        }
        //3.保存新的facilityInfoList
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            List<RoomFacility> roomFacilityList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                RoomFacility roomFacility = RoomFacility.builder().roomId(roomSubmitVo.getId()).facilityId(facilityInfoId).build();
                roomFacilityList.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilityList);
        }
        //4.保存新的labelInfoList
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelInfoIds)) {
            ArrayList<RoomLabel> roomLabelList = new ArrayList<>();
            for (Long labelInfoId : labelInfoIds) {
                RoomLabel roomLabel = RoomLabel.builder().roomId(roomSubmitVo.getId()).labelId(labelInfoId).build();
                roomLabelList.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabelList);
        }
        //5.保存新的paymentTypeList
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if (!CollectionUtils.isEmpty(paymentTypeIds)) {
            ArrayList<RoomPaymentType> roomPaymentTypeList = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = RoomPaymentType.builder().roomId(roomSubmitVo.getId()).paymentTypeId(paymentTypeId).build();
                roomPaymentTypeList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypeList);
        }
        //6.保存新的leaseTermList
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if (!CollectionUtils.isEmpty(leaseTermIds)) {
            ArrayList<RoomLeaseTerm> roomLeaseTerms = new ArrayList<>();
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder().roomId(roomSubmitVo.getId()).leaseTermId(leaseTermId).build();
                roomLeaseTerms.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTerms);
        }

    }

    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
       return roomInfoMapper.pageRoomItemByQuery(page,queryVo);


    }

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private PaymentTypeMapper paymentTypeMapper;
    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Override
    public RoomDetailVo getDetailById(Long id) {
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        if (roomInfo == null) {
            return null;
        }

        RoomDetailVo roomDetailVo = new RoomDetailVo();
        roomDetailVo.setId(roomInfo.getId());
        roomDetailVo.setRoomNumber(roomInfo.getRoomNumber());
        roomDetailVo.setRent(roomInfo.getRent());
        roomDetailVo.setApartmentId(roomInfo.getApartmentId());
        roomDetailVo.setIsRelease(roomInfo.getIsRelease());
        roomDetailVo.setCreateTime(roomInfo.getCreateTime());
        roomDetailVo.setUpdateTime(roomInfo.getUpdateTime());
        roomDetailVo.setIsDeleted(roomInfo.getIsDeleted());

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());
        roomDetailVo.setApartmentInfo(apartmentInfo);

        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemAndId(ItemType.ROOM, id);
        roomDetailVo.setGraphVoList(graphVoList);

        LambdaQueryWrapper<RoomAttrValue> attrValueWrapper = new LambdaQueryWrapper<>();
        attrValueWrapper.eq(RoomAttrValue::getRoomId, id);
        List<RoomAttrValue> roomAttrValues = roomAttrValueService.list(attrValueWrapper);
        
        List<AttrValueVo> attrValueVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomAttrValues)) {
            for (RoomAttrValue roomAttrValue : roomAttrValues) {
                AttrValue attrValue = attrValueMapper.selectById(roomAttrValue.getAttrValueId());
                if (attrValue != null) {
                    AttrValueVo attrValueVo = new AttrValueVo();
                    attrValueVo.setId(attrValue.getId());
                    attrValueVo.setName(attrValue.getName());
                    attrValueVo.setAttrKeyId(attrValue.getAttrKeyId());
                    attrValueVoList.add(attrValueVo);
                }
            }
        }
        roomDetailVo.setAttrValueVoList(attrValueVoList);

        LambdaQueryWrapper<RoomFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(RoomFacility::getRoomId, id);
        List<RoomFacility> roomFacilities = roomFacilityService.list(facilityWrapper);
        
        List<FacilityInfo> facilityInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomFacilities)) {
            for (RoomFacility roomFacility : roomFacilities) {
                FacilityInfo facilityInfo = facilityInfoMapper.selectById(roomFacility.getFacilityId());
                if (facilityInfo != null) {
                    facilityInfoList.add(facilityInfo);
                }
            }
        }
        roomDetailVo.setFacilityInfoList(facilityInfoList);

        LambdaQueryWrapper<RoomLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(RoomLabel::getRoomId, id);
        List<RoomLabel> roomLabels = roomLabelService.list(labelWrapper);
        
        List<LabelInfo> labelInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomLabels)) {
            for (RoomLabel roomLabel : roomLabels) {
                LabelInfo labelInfo = labelInfoMapper.selectById(roomLabel.getLabelId());
                if (labelInfo != null) {
                    labelInfoList.add(labelInfo);
                }
            }
        }
        roomDetailVo.setLabelInfoList(labelInfoList);

        LambdaQueryWrapper<RoomPaymentType> paymentTypeWrapper = new LambdaQueryWrapper<>();
        paymentTypeWrapper.eq(RoomPaymentType::getRoomId, id);
        List<RoomPaymentType> roomPaymentTypes = roomPaymentTypeService.list(paymentTypeWrapper);
        
        List<PaymentType> paymentTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomPaymentTypes)) {
            for (RoomPaymentType roomPaymentType : roomPaymentTypes) {
                PaymentType paymentType = paymentTypeMapper.selectById(roomPaymentType.getPaymentTypeId());
                if (paymentType != null) {
                    paymentTypeList.add(paymentType);
                }
            }
        }
        roomDetailVo.setPaymentTypeList(paymentTypeList);

        LambdaQueryWrapper<RoomLeaseTerm> leaseTermWrapper = new LambdaQueryWrapper<>();
        leaseTermWrapper.eq(RoomLeaseTerm::getRoomId, id);
        List<RoomLeaseTerm> roomLeaseTerms = roomLeaseTermService.list(leaseTermWrapper);
        
        List<LeaseTerm> leaseTermList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomLeaseTerms)) {
            for (RoomLeaseTerm roomLeaseTerm : roomLeaseTerms) {
                LeaseTerm leaseTerm = leaseTermMapper.selectById(roomLeaseTerm.getLeaseTermId());
                if (leaseTerm != null) {
                    leaseTermList.add(leaseTerm);
                }
            }
        }
        roomDetailVo.setLeaseTermList(leaseTermList);

        return roomDetailVo;
    }
}




