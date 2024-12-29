package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaUserManageUser;
import com.consiliuminc.sras.repository.postgres.QaUserManageUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QaUserManageUserService {

    private QaUserManageUserRepository qaUserManageUserRepository;


    @Autowired
    public QaUserManageUserService(QaUserManageUserRepository qaUserManageUserRepository) {
        this.qaUserManageUserRepository = qaUserManageUserRepository;
    }

    public List<QaUserManageUser> findAll() {
        return qaUserManageUserRepository.findAll();
    }

    public List<QaUserManageUser> findByName(String name) {
        return qaUserManageUserRepository.findByName(name);
    }


    public List<QaUserManageUser> findByUserName(String findByUserName) {
        return qaUserManageUserRepository.findByUsername(findByUserName);
    }

    public void delete(QaUserManageUser qaUserManageUser) {
        qaUserManageUserRepository.delete(qaUserManageUser);
    }

   // @Transactional(transactionManager = "postgresTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public QaUserManageUser save(QaUserManageUser qaUserManageUser) {
        return qaUserManageUserRepository.save(qaUserManageUser);
    }
}
