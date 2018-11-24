package com.fadeland.editor.ui.tileMenu;

public enum TileMenuTools
{
    TILE("tile"), SPRITE("sprite"), TILESELECT(null, "tileLayer"), SPRITESELECT(null, "spriteLayer"), LINES(null, "lines");

    public String name;
    public String type;
    TileMenuTools(String type, String name)
    {
        this.type = type;
        this.name = name;
    }
    TileMenuTools(String type)
    {
        this.type = type;
    }
}
