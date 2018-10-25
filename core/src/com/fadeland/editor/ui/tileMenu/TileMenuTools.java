package com.fadeland.editor.ui.tileMenu;

public enum TileMenuTools
{
    TILE(), SPRITE(), TILESELECT("tileLayer"), SPRITESELECT("spriteLayer"), LINES("lines");

    public String name;
    TileMenuTools(String name)
    {
        this.name = name;
    }
    TileMenuTools(){}
}
