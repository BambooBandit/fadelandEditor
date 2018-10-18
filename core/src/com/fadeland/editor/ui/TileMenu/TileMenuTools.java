package com.fadeland.editor.ui.TileMenu;

public enum TileMenuTools
{
    TILE(), LINES("lines");

    public String name;
    TileMenuTools(String name)
    {
        this.name = name;
    }
    TileMenuTools(){}
}
