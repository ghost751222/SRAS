package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.Group;
import com.consiliuminc.sras.entities.sqlserver.User;
import com.consiliuminc.sras.entities.sqlserver.UserGroup;
import com.consiliuminc.sras.repository.sqlserver.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final static String defaultPassword = "123456";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserGroupService userGroupService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserGroupService userGroupService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGroupService = userGroupService;
    }

    public List<User> findAll() {
        List<User> users = this.userRepository.findAll();
        setUserGroupProperty(users);
        return users;
    }


    public User save(User user) {
        user.setAccount(user.getAccount().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public void delete(User user) {
        this.userRepository.delete(user);
    }


    @Transactional("sqlServerTransactionManager")
    public void saveUserAndUserGroup(User user) {
        user.setAccount(user.getAccount().toLowerCase());
        List<Integer> userGroups = user.getUserGroups();
        if (Strings.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }
        user = this.userRepository.save(user);
        User finalUser = user;
        this.userGroupService.deleteByUserId(user.getId());
        userGroups.forEach(u -> {
            UserGroup userGroup = new UserGroup();
            Group group = new Group();
            group.setId(u);
            userGroup.setUser(finalUser);
            userGroup.setGroup(group);
            userGroupService.save(userGroup);
        });

    }

    @Transactional("sqlServerTransactionManager")
    public void deleteUserAndUserGroup(User user) {
        this.userGroupService.deleteByUserId(user.getId());
        this.userRepository.delete(user);
    }

    public List<User> findByAccount(String account) {
        List<User> users = userRepository.findByAccount(account);
        setUserGroupProperty(users);
        return users;
    }


    public boolean isExist(User user) {
        List<User> users = this.userRepository.findByAccount(user.getAccount());
        if (users.size() == 0)
            return false;
        else
            return true;

    }


    private void setUserGroupProperty(List<User> users) {
        users.forEach(user -> {
            List<UserGroup> userGroups = userGroupService.findByUserId(user.getId());
            user.setUserGroups(new ArrayList<>());
            userGroups.forEach(userGroup -> {
                user.getUserGroups().add(userGroup.getUserGroupComposite().getGroupid());
            });
        });
    }
}
