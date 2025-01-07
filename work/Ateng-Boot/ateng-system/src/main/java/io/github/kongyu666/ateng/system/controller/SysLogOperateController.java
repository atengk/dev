package io.github.kongyu666.ateng.system.controller;

import io.github.kongyu666.ateng.system.service.SysLogOperateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志记录 控制层。
 *
 * @author 孔余
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sysLogOperate")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SysLogOperateController {
    private final SysLogOperateService sysLogOperateService;

    @GetMapping("/test")
    public void test() {
        sysLogOperateService.logAdd(null);
    }


}
