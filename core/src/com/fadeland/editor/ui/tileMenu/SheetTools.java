package com.fadeland.editor.ui.tileMenu;

public enum SheetTools
{
    MAP("map"), TILES("tiles"), FLATMAP("flatMap"), DESERTTILES("desertTiles");

    public String name;
    SheetTools(String name)
    {
        this.name = name;
    }
}
