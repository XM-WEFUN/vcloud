package com.bootvue.admin.service;

import com.bootvue.common.result.PageOut;

import java.util.List;

public interface Oauth2Service {
    PageOut<List<Oauth2Item>> list(Oauth2QueryIn param);

    void delete(Long id);

    void addOrUpdate(Oauth2Item param);
}
