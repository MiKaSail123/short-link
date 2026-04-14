package com.threadfeng.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threadfeng.shortlink.admin.common.biz.user.UserContext;
import com.threadfeng.shortlink.admin.dao.entity.GroupDO;
import com.threadfeng.shortlink.admin.dao.mapper.GroupMapper;
import com.threadfeng.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.threadfeng.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.threadfeng.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.threadfeng.shortlink.admin.service.GroupService;
import com.threadfeng.shortlink.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper,GroupDO> implements GroupService{

    @Override
    public void saveGroup(String groupName) {
        String gid;
      do {
          gid=RandomGenerator.generateRandom();
      }while (!hasGid(gid));
            GroupDO groupDO = GroupDO.builder()
                    .gid(RandomGenerator.generateRandom())
                    .sortOrder(0)
                    .name(groupName)
                    .username(UserContext.getUsername())
                    .build();
            baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupSaveReqDTO> listGroup() {
        // TODO 获取用户名
        LambdaQueryWrapper<GroupDO> queryWrapper =Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag,0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(List.of(GroupDO::getSortOrder, GroupDO::getUpdateTime));
        List<GroupDO> groupDOList =baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList,ShortLinkGroupSaveReqDTO.class);
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper =Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .eq(GroupDO::getGid,requestParam.getGid())
                .eq(GroupDO::getDelFlag,0);
        GroupDO groupDO=new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
      LambdaUpdateWrapper<GroupDO> updateWrapper=Wrappers.lambdaUpdate(GroupDO.class)
              .eq(GroupDO::getGid,gid)
              .eq(GroupDO::getUsername,UserContext.getUsername())
              .eq(GroupDO::getDelFlag,0);
      GroupDO groupDO=new GroupDO();
      groupDO.setDelFlag(1);
      baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each ->{
            GroupDO groupDO=GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO>updateWrapper=Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername,UserContext.getUsername())
                    .eq(GroupDO::getGid,each.getGid())
                    .eq(GroupDO::getDelFlag,0);
            baseMapper.update(groupDO,updateWrapper);
                }
        );
    }

    private boolean hasGid(String gid){

        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid,gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername,UserContext.getUsername());
        GroupDO hasGroupFlag=baseMapper.selectOne(queryWrapper);
        return  hasGroupFlag==null;
    }
}
