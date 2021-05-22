package com.bootvue.admin.controller.action;

import com.bootvue.admin.dto.ActionItem;
import com.bootvue.admin.dto.RoleActionIn;
import com.bootvue.admin.dto.RoleIn;
import com.bootvue.admin.service.ActionService;
import com.bootvue.core.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/action")
@Api(tags = "权限菜单相关接口")
public class ActionController {
    private final ActionService actionService;

    @PostMapping("/list")
    @ApiOperation("获取当前角色权限菜单详情")
    public List<ActionItem> actionList(@RequestBody RoleIn role) {
        return actionService.actionList(role);
    }

    @PostMapping("/update")
    @ApiOperation("更新某个角色的菜单权限")
    public void updateRoleActions(@RequestBody @Valid RoleActionIn param, BindingResult result) {
        R.handleErr(result);
        actionService.updateRoleService(param);
    }
}
