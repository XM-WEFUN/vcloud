package com.bootvue.admin.controller;

import cn.hutool.core.lang.tree.Tree;
import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.DeptItem;
import com.bootvue.admin.dto.Id;
import com.bootvue.admin.service.DeptService;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.R;
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
import java.util.Set;

@Api(tags = "部门管理")
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(hasRole = "admin")
public class DeptController {

    private final DeptService deptService;


    @PostMapping("/list")
    @ApiOperation("部门列表")
    public List<Tree<String>> list(@RequestBody Id param, AppUser user) {
        return deptService.list(param.getId(), user);
    }

    @PostMapping("/add")
    @ApiOperation("新增部门")
    public void add(@Valid @RequestBody DeptItem param, AppUser user, BindingResult result) {
        R.handleErr(result);
        deptService.addOrUpdate(param, user);
    }

    @PostMapping("/update")
    @ApiOperation("更新部门信息")
    public void update(@Valid @RequestBody DeptItem param, AppUser user, BindingResult result) {
        R.handleErr(result);
        deptService.addOrUpdate(param, user);
    }

    @PostMapping("/delete")
    @ApiOperation("删除部门")
    public void delete(@RequestBody Id param, AppUser user) {
        deptService.delete(param.getId(), user);
    }

    @PostMapping("/assign_user")
    @ApiOperation("分配用户")
    public void assignUser(@Valid @RequestBody AssignIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        deptService.assignUser(param, user);
    }

    @PostMapping("/list_by_user")
    @ApiOperation("某个用户拥有的 dept_id集合")
    public Set<String> listByUser(@RequestBody Id param, AppUser user) {
        return deptService.listByUser(param.getId(), user);
    }

}
