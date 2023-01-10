package com.mulan.fengwo_backend.service;

import com.mulan.fengwo_backend.model.domain.User;

import java.util.List;

public interface UserService {
    User searchUserById(Long id);

    List<User> searchUsersByTags(List<String> tagNameList);

    User getSafetyUser(User user);
}
