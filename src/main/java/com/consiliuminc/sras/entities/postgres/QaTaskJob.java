package com.consiliuminc.sras.entities.postgres;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "qa_task_job")
//@TypeDefs({
//        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
//})
public class QaTaskJob {
    @Id
    private Integer id;
    private Integer state;
    @Column(columnDefinition = "nvarchar(18)")
    private String show_state;
    private Integer qa_id;
    @Column(columnDefinition = "nvarchar(50)")
    private String qa_name;
    private Integer machine_score;
    private Integer human_score;
    private Integer hit_term_num;
    private LocalDateTime send_date;
    private LocalDateTime finish_date;
    private Date listen_start_time;
    private Date listen_end_time;
    private Integer listen_time;
    @Column(columnDefinition = "nvarchar(12)")
    private String is_qa;
    @Column(columnDefinition = "nvarchar(12)")
    private String is_appeal;
    private Integer appeal_count;
    @Column(columnDefinition = "nvarchar(12)")
    private String is_human_end;
    private Integer qatemplate_id;
    @Column(columnDefinition = "nvarchar(50)")
    private String agent_id;
    @Transient
//    @Type(type = "jsonb")
//    @Column(columnDefinition = "jsonb")
    private String task;
    private LocalDateTime task_date;
    @Column(columnDefinition = "nvarchar(20)")
    private String send_type;
    @Column(columnDefinition = "nvarchar(12)")
    private String is_back;
    @Column(columnDefinition = "nvarchar(50)")
    private String send_people;
    @Column(columnDefinition = "nvarchar(1)")
    private String is_case_label;
    @Column(columnDefinition = "nvarchar(max)")
    private String case_content;
    @Column(columnDefinition = "nvarchar(50)")
    private String end_people;
    @Column(columnDefinition = "nvarchar(max)")
    private String machine_hits;
    @Column(columnDefinition = "nvarchar(max)")
    private String human_hits;
    private LocalDateTime insert_time;

    public String getHuman_hits() {
        return human_hits;
    }

    public void setHuman_hits(String human_hits) {
        this.human_hits = human_hits;
    }

    public String getMachine_hits() {
        return machine_hits;
    }

    public void setMachine_hits(String machine_hits) {
        this.machine_hits = machine_hits;
    }

    public String getEnd_people() {
        return end_people;
    }

    public void setEnd_people(String end_people) {
        this.end_people = end_people;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getShow_state() {
        return show_state;
    }

    public void setShow_state(String show_state) {
        this.show_state = show_state;
    }

    public Integer getQa_id() {
        return qa_id;
    }

    public void setQa_id(Integer qa_id) {
        this.qa_id = qa_id;
    }

    public String getQa_name() {
        return qa_name;
    }

    public void setQa_name(String qa_name) {
        this.qa_name = qa_name;
    }

    public Integer getMachine_score() {
        return machine_score;
    }

    public void setMachine_score(Integer machine_score) {
        this.machine_score = machine_score;
    }

    public Integer getHuman_score() {
        return human_score;
    }

    public void setHuman_score(Integer human_score) {
        this.human_score = human_score;
    }

    public Integer getHit_term_num() {
        return hit_term_num;
    }

    public void setHit_term_num(Integer hit_term_num) {
        this.hit_term_num = hit_term_num;
    }

    public LocalDateTime getSend_date() {
        return send_date;
    }

    public void setSend_date(LocalDateTime send_date) {
        this.send_date = send_date;
    }

    public LocalDateTime getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(LocalDateTime finish_date) {
        this.finish_date = finish_date;
    }

    public Date getListen_start_time() {
        return listen_start_time;
    }

    public void setListen_start_time(Date listen_start_time) {
        this.listen_start_time = listen_start_time;
    }

    public Date getListen_end_time() {
        return listen_end_time;
    }

    public void setListen_end_time(Date listen_end_time) {
        this.listen_end_time = listen_end_time;
    }

    public Integer getListen_time() {
        return listen_time;
    }

    public void setListen_time(Integer listen_time) {
        this.listen_time = listen_time;
    }

    public String getIs_qa() {
        return is_qa;
    }

    public void setIs_qa(String is_qa) {
        this.is_qa = is_qa;
    }

    public String getIs_appeal() {
        return is_appeal;
    }

    public void setIs_appeal(String is_appeal) {
        this.is_appeal = is_appeal;
    }

    public Integer getAppeal_count() {
        return appeal_count;
    }

    public void setAppeal_count(Integer appeal_count) {
        this.appeal_count = appeal_count;
    }

    public String getIs_human_end() {
        return is_human_end;
    }

    public void setIs_human_end(String is_human_end) {
        this.is_human_end = is_human_end;
    }

    public Integer getQatemplate_id() {
        return qatemplate_id;
    }

    public void setQatemplate_id(Integer qatemplate_id) {
        this.qatemplate_id = qatemplate_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDateTime getTask_date() {
        return task_date;
    }

    public void setTask_date(LocalDateTime task_date) {
        this.task_date = task_date;
    }

    public String getSend_type() {
        return send_type;
    }

    public void setSend_type(String send_type) {
        this.send_type = send_type;
    }

    public String getIs_back() {
        return is_back;
    }

    public void setIs_back(String is_back) {
        this.is_back = is_back;
    }

    public String getSend_people() {
        return send_people;
    }

    public void setSend_people(String send_people) {
        this.send_people = send_people;
    }


    public LocalDateTime getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(LocalDateTime insert_time) {
        this.insert_time = insert_time;
    }

    public String getIs_case_label() {
        return is_case_label;
    }

    public void setIs_case_label(String is_case_label) {
        this.is_case_label = is_case_label;
    }

    public String getCase_content() {
        return case_content;
    }

    public void setCase_content(String case_content) {
        this.case_content = case_content;
    }
}

