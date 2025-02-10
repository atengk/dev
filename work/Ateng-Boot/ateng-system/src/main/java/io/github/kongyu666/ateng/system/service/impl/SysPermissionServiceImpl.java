package io.github.kongyu666.ateng.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.github.kongyu666.ateng.system.entity.SysPermission;
import io.github.kongyu666.ateng.system.mapper.SysPermissionMapper;
import io.github.kongyu666.ateng.system.service.SysPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 存储系统中的权限信息 服务层实现。
 *
 * @author 孔余
 * @since 1.0.0
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Override
    public void addRole(SysPermission entity) {
        this.save(entity);
    }

    @Override
    public List<SysPermission> listPermission() {
        return this.list();
    }

    @Override
    public SysPermission getPermission(Integer id) {
        return this.getById(id);
    }
}
