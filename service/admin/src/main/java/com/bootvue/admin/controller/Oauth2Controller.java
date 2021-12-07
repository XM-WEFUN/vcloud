package com.bootvue.admin.controller;

import com.bootvue.admin.dto.Id;
import com.bootvue.admin.service.Oauth2Item;
import com.bootvue.admin.service.Oauth2QueryIn;
import com.bootvue.admin.service.Oauth2Service;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.result.PageOut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Oauth2管理")
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(hasRole = "admin")
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    @PostMapping("/list")
    @ApiOperation("oauth2 client列表")
    public PageOut<List<Oauth2Item>> list(@RequestBody Oauth2QueryIn param) {
        return oauth2Service.list(param);
    }

    @PostMapping("/add")
    @ApiOperation("新增oauth2 client")
    public void add(@RequestBody Oauth2Item param) {
        oauth2Service.addOrUpdate(param);
    }

    @PostMapping("/update")
    @ApiOperation("更新oauth2 client")
    public void update(@RequestBody Oauth2Item param) {
        oauth2Service.addOrUpdate(param);
    }

    @PostMapping("/delete")
    @ApiOperation("删除oauth2 client")
    public void delete(@RequestBody Id param) {
        oauth2Service.delete(param.getId());
    }
}
