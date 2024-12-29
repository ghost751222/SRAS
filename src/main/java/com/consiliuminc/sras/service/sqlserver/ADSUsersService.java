package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.ADSUsers;
import com.consiliuminc.sras.entities.sqlserver.Function;
import com.consiliuminc.sras.repository.sqlserver.ADSUsersRepository;
import com.consiliuminc.sras.repository.sqlserver.FunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ADSUsersService {

    private ADSUsersRepository adsUsersRepository;


    @Autowired
    public ADSUsersService(ADSUsersRepository adsUsersRepository) {
        this.adsUsersRepository = adsUsersRepository;
    }


    public List<ADSUsers> findAll() {
        return this.adsUsersRepository.findAll();
    }



    public List<Map<String,String>> findDistinctDeskCodeTeamCodeByDeskCode(String deskCode) {
        List<Map<String,String>> result= new ArrayList<>();
        List<String> deskCodes =this.adsUsersRepository.findDistinctDeskCodeTeamCodeByDeskCode(deskCode);
        for (String _deskCode: deskCodes) {
            Map<String,String> map = new HashMap<>();
            String[] detailResult = _deskCode.split(",");
            map.put("deskCode",detailResult[0]);
            map.put("teamCode",detailResult[1]);
            result.add(map);
        }

        return result;
    }

    public List<Map<String,String>> findDistinctGroupCodeTeamCodeByGroupCode(String groupCode) {
        List<Map<String,String>> result= new ArrayList<>();
        List<String> groupCodes =this.adsUsersRepository.findDistinctGroupCodeTeamCodeByGroupCode(groupCode);
        for (String _groupCode: groupCodes) {
            Map<String,String> map = new HashMap<>();
            String[] detailResult = _groupCode.split(",");
            map.put("groupCode",detailResult[0]);
            map.put("teamCode",detailResult[1]);
            result.add(map);
        }

        return result;
    }


}
