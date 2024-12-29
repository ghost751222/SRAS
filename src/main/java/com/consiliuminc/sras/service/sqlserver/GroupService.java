package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.Function;
import com.consiliuminc.sras.entities.sqlserver.Group;
import com.consiliuminc.sras.entities.sqlserver.GroupFunction;
import com.consiliuminc.sras.repository.sqlserver.GroupFunctionRepository;
import com.consiliuminc.sras.repository.sqlserver.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    private GroupRepository groupRepository;
    private GroupFunctionRepository groupFunctionRepository;
    private UserGroupService userGroupService;
    private FunctionService functionService;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupFunctionRepository groupFunctionRepository, UserGroupService userGroupService, FunctionService functionService) {
        this.groupRepository = groupRepository;
        this.groupFunctionRepository = groupFunctionRepository;
        this.userGroupService = userGroupService;
        this.functionService = functionService;
    }


    public List<Group> findByName(String groupName) {
        return this.groupRepository.findByName(groupName);
    }

    public List<Group> findAll() {
        List<Group> groups = groupRepository.findAll();
        List<Function> functions = functionService.findAll();

        groups.forEach(group -> {
            List<GroupFunction> groupFunctions = groupFunctionRepository.findByGroupId(group.getId());
            group.setFunctions(new ArrayList<>());
            groupFunctions.forEach(groupFunction -> {
                group.getFunctions().add(groupFunction.getGroupFunctionComposite().getProgram_name());
            });
        });
        return groups;
    }


    public Group save(Group group) {
        return this.groupRepository.save(group);
    }

    public void delete(Group group) {
        this.groupRepository.delete(group);
    }


    @Transactional("sqlServerTransactionManager")
    public void saveGroupAndGroupFunction(Group group) {
        List<String> groupFunctions = group.getFunctions();
        this.groupFunctionRepository.deleteByGroupId(group.getId());
        group = this.groupRepository.save(group);
        final Integer groupId = group.getId();
        Group finalGroup = group;
        groupFunctions.forEach(functionName -> {
            GroupFunction groupFunction = new GroupFunction();
            Function function = new Function();
            function.setProgram_name(functionName);
            groupFunction.setGroup(finalGroup);
            groupFunction.setFunction(function);
            //groupFunction.setGroupFunctionId(new GroupFunctionId(groupId,functionName));
            groupFunctionRepository.save(groupFunction);
        });
    }


    @Transactional("sqlServerTransactionManager")
    public void deleteGroupAndGroupFunction(Group group) {

        this.groupFunctionRepository.deleteByGroupId(group.getId());
        this.userGroupService.deleteByGroupId(group.getId());
        this.groupRepository.delete(group);
    }

}
