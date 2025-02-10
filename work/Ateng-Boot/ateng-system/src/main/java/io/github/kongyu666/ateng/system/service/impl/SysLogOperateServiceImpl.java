package io.github.kongyu666.ateng.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.github.kongyu666.ateng.system.entity.SysLogOperate;
import io.github.kongyu666.ateng.system.mapper.SysLogOperateMapper;
import io.github.kongyu666.ateng.system.service.SysLogOperateService;
import io.github.kongyu666.common.log.event.LogOperateEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志记录 服务层实现。
 *
 * @author 孔余
 * @since 1.0.0
 */
@Service
public class SysLogOperateServiceImpl extends ServiceImpl<SysLogOperateMapper, SysLogOperate> implements SysLogOperateService {

    /**
     * 操作日志记录
     *
     * @param logOperateEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordOper(LogOperateEvent logOperateEvent) {
        SysLogOperate sysLogOperate = new SysLogOperate();
        BeanUtils.copyProperties(logOperateEvent, sysLogOperate);
        logAdd(sysLogOperate);
    }

    @Override
    public void logAdd(SysLogOperate entity) {
        this.save(entity);
    }
}
