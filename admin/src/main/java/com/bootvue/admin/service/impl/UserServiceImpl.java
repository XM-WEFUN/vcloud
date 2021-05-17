package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.UserIn;
import com.bootvue.admin.dto.UserOut;
import com.bootvue.admin.service.UserService;
import com.bootvue.core.ddo.user.UserDo;
import com.bootvue.core.entity.User;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.service.UserMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserMapperService userMapperService;
    private final HttpServletRequest request;

    @Override
    public PageOut<List<UserOut>> userList(UserIn param) {
        Page<User> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<UserDo> users = userMapperService.listUsers(page, Long.valueOf(request.getHeader("tenant_id")), param.getUsername());
        PageOut<List<UserOut>> out = new PageOut<>();

        out.setTotal(users.getTotal());

        out.setRows(users.getRecords().stream().map(e -> new UserOut(e.getId(), e.getUsername(),
                e.getRole(), e.getStatus(), e.getCreateTime())).collect(Collectors.toList()));
        return out;
    }
}
