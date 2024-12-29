

package com.consiliuminc.sras.entities.postgres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qa_interface_dim")
public class QaInterfaceDim implements java.io.Serializable {

    @Id
    private Integer id;
    @Column(columnDefinition = "nvarchar(56)")
    private String type;
    @Column(columnDefinition = "nvarchar(56)")
    private String field_name;
    @Column(columnDefinition = "nvarchar(56)")
    private String field_type;
    @Column(columnDefinition = "nvarchar(56)")
    private String old_field;
    @Column(columnDefinition = "nvarchar(56)")
    private String display_name;
    @Column(columnDefinition = "nvarchar(56)")
    private String display_format;
    @Column(columnDefinition = "nvarchar(56)")
    private String search_format;
    private Integer can_as_get;
    private Integer can_filter;
    private Integer can_table_show;
    private Integer can_order_show;
    private Integer can_export;
    @Column(columnDefinition = "nvarchar(56)")
    private String classify;
    @Column(name = "\"order\"")
    private Integer order;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getOld_field() {
        return old_field;
    }

    public void setOld_field(String old_field) {
        this.old_field = old_field;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_format() {
        return display_format;
    }

    public void setDisplay_format(String display_format) {
        this.display_format = display_format;
    }

    public String getSearch_format() {
        return search_format;
    }

    public void setSearch_format(String search_format) {
        this.search_format = search_format;
    }

    public Integer getCan_as_get() {
        return can_as_get;
    }

    public void setCan_as_get(Integer can_as_get) {
        this.can_as_get = can_as_get;
    }

    public Integer getCan_filter() {
        return can_filter;
    }

    public void setCan_filter(Integer can_filter) {
        this.can_filter = can_filter;
    }

    public Integer getCan_table_show() {
        return can_table_show;
    }

    public void setCan_table_show(Integer can_table_show) {
        this.can_table_show = can_table_show;
    }

    public Integer getCan_order_show() {
        return can_order_show;
    }

    public void setCan_order_show(Integer can_order_show) {
        this.can_order_show = can_order_show;
    }

    public Integer getCan_export() {
        return can_export;
    }

    public void setCan_export(Integer can_export) {
        this.can_export = can_export;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


}
