package com.fadeland.editor.ui.tileMenu;

public enum SheetTools
{
    MAP("map"), TILES("tiles"), FLATMAP("flatMap"), DESERTTILES("desertTiles"), CANYONMAP("canyonMap"), CANYONBACKDROP("canyonBackdrop"), CANYONTILES("canyonTiles");

    public String name;
    public int tileSheetWidth;
    public int tileSheetHeight;
    SheetTools(String name)
    {
        this.name = name;
    }
}
