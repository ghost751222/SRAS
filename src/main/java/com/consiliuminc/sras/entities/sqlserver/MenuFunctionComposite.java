package com.consiliuminc.sras.entities.sqlserver;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MenuFunctionComposite implements Serializable {

    private String menu_name;

    private String program_name;

    public MenuFunctionComposite() {
    }

    public MenuFunctionComposite(String menu_name, String program_name) {
        this.menu_name = menu_name;
        this.program_name = program_name;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getFunction_name() {
        return program_name;
    }

    public void setFunction_name(String program_name) {
        this.program_name = program_name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
