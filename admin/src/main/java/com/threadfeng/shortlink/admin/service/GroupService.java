package com.threadfeng.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threadfeng.shortlink.admin.dao.entity.GroupDO;
import com.threadfeng.shortlink.admin.dao.entity.UserDO;
import com.threadfeng.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.threadfeng.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.threadfeng.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
     void saveGroup(String groupName);

    List<ShortLinkGroupSaveReqDTO> listGroup();

    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);

    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
