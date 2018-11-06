package com.fadeland.editor.ui.layerMenu;

public enum LayerTypes
{
    TILE("tileLayer"), SPRITE("spriteLayer"), OBJECT("objectLayer");

    public String name;

    LayerTypes(String name)
    {
        this.name = name;
    }
}
