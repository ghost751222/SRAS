package com.consiliuminc.sras.entities.sqlserver;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "sras_menu")
@Table(name = "sras_menu")
public class Menu {

    @Id
    private String menu_name;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String display_name;


    private Integer seq;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu")
    @OrderBy("seq ASC")
    Set<MenuFunction> menuFunctions;


    public Menu() {
    }

    public Menu(String menu_name, String display_name, Integer seq) {
        this.menu_name = menu_name;
        this.display_name = display_name;
        this.seq = seq;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
