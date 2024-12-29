package com.consiliuminc.sras.entities.sqlserver;

import javax.persistence.*;

@Entity
@Table(name = "sras_menu_function")
public class MenuFunction {

    @EmbeddedId
    private MenuFunctionComposite menuFunctionComposite;

    @ManyToOne
    @MapsId("menu_name")
    @JoinColumn(name = "menu_name")
    @OrderBy("seq ASC")
    Menu menu;



    @ManyToOne
    @MapsId("program_name")
    @JoinColumn(name = "program_name")
    @OrderBy("seq ASC")
    Function function;


    public MenuFunction() {
        this.menuFunctionComposite = new MenuFunctionComposite(null,null);

    }

    public MenuFunctionComposite getMenuFunctionComposite() {
        return menuFunctionComposite;
    }

    public void setMenuFunctionComposite(MenuFunctionComposite menuFunctionComposite) {
        this.menuFunctionComposite = menuFunctionComposite;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        this.menuFunctionComposite.setMenu_name(this.menu.getMenu_name());
    }

    public Function getFunction() {

        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
        this.menuFunctionComposite.setFunction_name(this.function.getProgram_name());
    }
}
