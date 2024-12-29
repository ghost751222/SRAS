package com.consiliuminc.sras.scheduled;

import com.consiliuminc.sras.componets.Pbkdf2Sha256;
import com.consiliuminc.sras.entities.postgres.QaUserManageRole;
import com.consiliuminc.sras.entities.postgres.QaUserManageUser;
import com.consiliuminc.sras.entities.postgres.QaUserManageUserGroups;
import com.consiliuminc.sras.entities.postgres.QaUserManageUserRoles;
import com.consiliuminc.sras.entities.sqlserver.ADSUsers;
import com.consiliuminc.sras.service.postgres.QaUserManageRoleService;
import com.consiliuminc.sras.service.postgres.QaUserManageUserGroupsService;
import com.consiliuminc.sras.service.postgres.QaUserManageUserRolesService;
import com.consiliuminc.sras.service.postgres.QaUserManageUserService;
import com.consiliuminc.sras.service.sqlserver.ADSUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdsUsersScheduled {

    private static final Logger logger = LoggerFactory.getLogger(AdsUsersScheduled.class);

    @Value("${pqa.default.password:123456}")
    private String defaultPassword;

    @Value("${pqa.default.user.group.id:3}")
    private int pqaDefaultUserGroupId;


    @Value("${pqa.default.user.group.workplace:3}")
    private int pqaDefaultUserGroupWorkPlace;


    @Value("${psae.qa:質檢員}")
    private String qa;


    @Value("${psae.qaleader:質檢組長}")
    private String qaLeader;


    @Value("${psae.agentleader:坐席主管}")
    private String agentLeader;

    private String encryptPassword;
    private PlatformTransactionManager platformTransactionManager;
    private ADSUsersService adsUsersService;
    private QaUserManageUserGroupsService qaUserManageUserGroupsService;
    private QaUserManageUserService qaUserManageUserService;
    private QaUserManageUserRolesService qaUserManageUserRolesService;
    private QaUserManageRoleService qaUserManageRoleService;


    @Autowired
    public AdsUsersScheduled(@Qualifier(("postgresTransactionManager")) PlatformTransactionManager platformTransactionManager, ADSUsersService adsUsersService, QaUserManageUserGroupsService qaUserManageUserGroupsService, QaUserManageUserService qaUserManageUserService, QaUserManageUserRolesService qaUserManageUserRolesService, QaUserManageRoleService qaUserManageRoleService) {
        this.platformTransactionManager = platformTransactionManager;
        this.adsUsersService = adsUsersService;
        this.qaUserManageUserService = qaUserManageUserService;
        this.qaUserManageUserRolesService = qaUserManageUserRolesService;
        this.qaUserManageUserGroupsService = qaUserManageUserGroupsService;
        this.qaUserManageRoleService = qaUserManageRoleService;
    }


    @Scheduled(cron = "${task.adsusers.cron:0 0 23 * * *}")
    //@PostConstruct
    public void executeAdsUsers() {
        String key = "SCF";
        encryptPassword = Pbkdf2Sha256.encode(defaultPassword);
        List<ADSUsers> adsUsers = adsUsersService.findAll();
        for (ADSUsers _adsUsers : adsUsers) {
            try {
                String account = _adsUsers.getUserID();
                String enable = _adsUsers.getEnable();
                String teamCode = _adsUsers.getTeamCode();
                List<QaUserManageUser> qaUserManageUsers = qaUserManageUserService.findByUserName(account);
                boolean isExist = qaUserManageUsers.size() > 0;
                List<QaUserManageRole> qaUserManageRoles = null;
                QaUserManageUser _qaUserManageUser = null;
                List<String> groupNames = new ArrayList<>();

                if ("Y".equalsIgnoreCase(enable)) {

//                    if (!teamCode.startsWith(key)) {
//                        //TL MGR DH mapping 坐席主管
//                        if (account.equals(_adsUsers.getTeamLeader())) {
//                            groupNames.add(teamCode);
//                        } else if (account.equals(_adsUsers.getDeskLeader())) {
//                            groupNames.addAll(findDistinctDeskCodeTeamCodeByDeskCode((_adsUsers.getDeskCode())));
//                        } else if (account.equals(_adsUsers.getGroupLeader())) {
//                            groupNames.addAll(findDistinctGroupCodeTeamCodeByGroupCode((_adsUsers.getGroupCode())));
//                        }
//                        qaUserManageRoles = getAgentLeaderRole();

                    if (!teamCode.startsWith(key) && account.equals(_adsUsers.getTeamLeader())) {
                        //TL  mapping 坐席主管
                        groupNames.add(teamCode);
                        qaUserManageRoles = getAgentLeaderRole();
                    } else if (!teamCode.startsWith(key) && account.equals(_adsUsers.getDeskLeader())) {
                        //MGR  mapping 坐席主管
                        groupNames.addAll(findDistinctDeskCodeTeamCodeByDeskCode((_adsUsers.getDeskCode())));
                        qaUserManageRoles = getAgentLeaderRole();
                    } else if (!teamCode.startsWith(key) && account.equals(_adsUsers.getGroupLeader())) {
                        //DH  mapping 坐席主管
                        groupNames.addAll(findDistinctGroupCodeTeamCodeByGroupCode((_adsUsers.getGroupCode())));
                        qaUserManageRoles = getAgentLeaderRole();

                    } else if (teamCode.startsWith(key) && (account.equals(_adsUsers.getTeamLeader()) || account.equals(_adsUsers.getDeskLeader()))) {

                        //SL mapping 質檢員+質檢組長
                        qaUserManageRoles = getQaLeaderRole();

                    } else if (teamCode.startsWith(key)) {

                        //SA mapping 質檢員
                        qaUserManageRoles = getQaRole();
                    }

                    if (isExist) _qaUserManageUser = qaUserManageUsers.get(0);

                    if(qaUserManageRoles == null) qaUserManageRoles =new ArrayList<>();
                    generateData(_adsUsers, _qaUserManageUser, qaUserManageRoles, groupNames);


                } else if ("N".equalsIgnoreCase(enable)) {
                    if (isExist) {
                        for (QaUserManageUser qaUserManageUser : qaUserManageUsers) {
                            qaUserManageUserRolesService.deleteByUserID(qaUserManageUser.getID());
                            qaUserManageUserGroupsService.deleteByUserID(qaUserManageUser.getID());
                            qaUserManageUserService.delete(qaUserManageUser);
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("executeAdsUsers error={} ", e.getMessage());
            }
        }

    }


    //質檢員
    private List<QaUserManageRole> getQaRole() {
        return qaUserManageRoleService.findAll().stream().filter(qaUserManageRole -> qaUserManageRole.getName().equals(qa)).collect(Collectors.toList());
    }

    //質檢員+質檢組長
    private List<QaUserManageRole> getQaLeaderRole() {
        return qaUserManageRoleService.findAll().stream().filter(qaUserManageRole -> qaUserManageRole.getName().equals(qa) || qaUserManageRole.getName().equals(qaLeader)).collect(Collectors.toList());
    }

    //坐席主管
    private List<QaUserManageRole> getAgentLeaderRole() {
        return qaUserManageRoleService.findAll().stream().filter(qaUserManageRole -> qaUserManageRole.getName().equals(agentLeader)).collect(Collectors.toList());
    }


    private List<String> findDistinctDeskCodeTeamCodeByDeskCode(String deskCode) {


        List<String> results = new ArrayList<>();
        List<Map<String, String>> deskCodes = adsUsersService.findDistinctDeskCodeTeamCodeByDeskCode(deskCode);

        deskCodes.forEach(d -> {
            results.add(d.get("teamCode"));

        });
        return results;
    }

    private List<String> findDistinctGroupCodeTeamCodeByGroupCode(String groupCode) {
        List<String> results = new ArrayList<>();
        List<Map<String, String>> groupCodes = adsUsersService.findDistinctGroupCodeTeamCodeByGroupCode(groupCode);

        groupCodes.forEach(d -> {
            results.add(d.get("teamCode"));

        });
        return results;
    }


    private void generateData(ADSUsers adsUsers, QaUserManageUser qaUserManageUser, List<QaUserManageRole> qaUserManageRoles, List<String> groupNames) {
        QaUserManageUser _qaUserManageUser = qaUserManageUser;
        QaUserManageUserRoles qaUserManageUserRoles;
        QaUserManageUserGroups qaUserManageUserGroups;
        TransactionStatus transactionStatus = null;
        List<String> roleCodes = new ArrayList<>();
        try {
            transactionStatus = getTransactionStatus();

            for (QaUserManageRole qaUserManageRole : qaUserManageRoles) {
                roleCodes.add(String.valueOf(qaUserManageRole.getCode()));
            }
            if (_qaUserManageUser == null) _qaUserManageUser = new QaUserManageUser();


            //qaUserManageUser = new QaUserManageUser();
            _qaUserManageUser.setName(adsUsers.getUserName());
            _qaUserManageUser.setUsername(adsUsers.getUserID());
            _qaUserManageUser.setPassword(encryptPassword);
            _qaUserManageUser.setRoleCode(String.join(",", roleCodes));
            _qaUserManageUser.setGroupId(pqaDefaultUserGroupId);
            _qaUserManageUser.setWorkPlace(String.valueOf(pqaDefaultUserGroupWorkPlace));
            _qaUserManageUser.setIsSuperuser(0);
            _qaUserManageUser.setGroup_name(String.join("/", groupNames));
            _qaUserManageUser = qaUserManageUserService.save(_qaUserManageUser);

            qaUserManageUserRolesService.deleteByUserID(_qaUserManageUser.getID());
            for (QaUserManageRole qaUserManageRole : qaUserManageRoles) {
                qaUserManageUserRoles = new QaUserManageUserRoles();
                qaUserManageUserRoles.setUserID(_qaUserManageUser.getID());
                qaUserManageUserRoles.setRoleID(qaUserManageRole.getId());
                qaUserManageUserRolesService.save(qaUserManageUserRoles);
            }


            qaUserManageUserGroupsService.deleteByUserID(_qaUserManageUser.getID());
            qaUserManageUserGroups = new QaUserManageUserGroups();
            qaUserManageUserGroups.setUserID(_qaUserManageUser.getID());
            qaUserManageUserGroups.setGroupID(pqaDefaultUserGroupId);
            qaUserManageUserGroupsService.save(qaUserManageUserGroups);

            platformTransactionManager.commit(transactionStatus);

        } catch (Exception e) {
            platformTransactionManager.rollback(transactionStatus);
            throw e;
        }
    }


    private TransactionStatus getTransactionStatus() {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        return platformTransactionManager.getTransaction(transactionDefinition);

    }

}
