package com.bootvue.admin.controller.action;

import com.bootvue.admin.controller.action.dto.ActionIn;
import com.bootvue.admin.controller.action.dto.ActionOut;
import com.bootvue.admin.controller.action.dto.ActionQueryIn;
import com.bootvue.admin.dto.ActionItem;
import com.bootvue.admin.dto.RoleActionIn;
import com.bootvue.admin.dto.RoleIn;
import com.bootvue.admin.service.ActionService;
import com.bootvue.core.result.PageOut;
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
    @ApiOperation("获取角色权限菜单详情")
    public List<ActionItem> actionList(@RequestBody RoleIn role) {
        return actionService.actionList(role);
    }

    @PostMapping("/update")
    @ApiOperation("更新某个角色的菜单权限")
    public void updateRoleActions(@RequestBody @Valid RoleActionIn param, BindingResult result) {
        R.handleErr(result);
        actionService.updateRoleService(param);
    }


    @PostMapping("/list_item")
    public PageOut<List<ActionOut>> getActionList(@RequestBody ActionQueryIn param) {
        return actionService.getActionList(param);
    }

    @PostMapping("/add")
    public void addAction(@RequestBody ActionIn param) {
        actionService.addOrUpdateAction(param);
    }

    @PostMapping("/update_item")
    public void updateAction(@RequestBody ActionIn param) {
        actionService.addOrUpdateAction(param);
    }

    @PostMapping("/delete")
    public void deleteAction(@RequestBody ActionIn param) {
        actionService.deleteAction(param);
    }
}
