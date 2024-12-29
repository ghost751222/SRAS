package com.consiliuminc.sras.service;

import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.util.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PropertySource({"classpath:persistence.properties"})
public class ResultSetService {

    private static final Logger logger = LogManager.getLogger(ResultSetService.class);

    @Value("${spring.second-datasource.jdbcUrl}")
    private String jdbcUrl;
    @Value("${spring.second-datasource.username}")
    private String username;
    @Value("${spring.second-datasource.password}")
    private String password;


    public List<QaTaskJob> setQaTaskJobTaskValue(List<QaTaskJob> qaTaskJobs,List<QaInterfaceDim> qaInterfaceDims){
        String sql = "select * from qa_task_job where id = %d";
        ResultSet rs = null;
        Statement stmt = null;
        String json  ="{}";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {

            stmt = conn.createStatement();
            List<QaInterfaceDim> _qaInterfaceDims = qaInterfaceDims.stream().filter(qaInterfaceDim -> qaInterfaceDim.getType().equals("task")).collect(Collectors.toList());
            for (QaTaskJob qaTaskJob: qaTaskJobs) {
                JsonNode jsonNode = JacksonUtils.toJsonNode(json);
                rs = stmt.executeQuery(String.format(sql,qaTaskJob.getId()));
                while (rs.next()){
                    for (QaInterfaceDim qaInterfaceDim: _qaInterfaceDims) {
                        try {
                            String fieldName =qaInterfaceDim.getField_name();
                            Object value = rs.getObject(fieldName);
                            ((ObjectNode) jsonNode).put(fieldName,value == null ? "None":value.toString());
                        }catch (Exception e){
                            logger.error("setQaTaskJobTaskValue setTaskValue Error ={}",e);
                        }

                    }
                }
                qaTaskJob.setTask(jsonNode.toString());
            }



        } catch (SQLException | IOException e) {
            logger.error("setQaTaskJobTaskValue Error ={}",e);
        }finally {
            return qaTaskJobs;
        }
    }
}
