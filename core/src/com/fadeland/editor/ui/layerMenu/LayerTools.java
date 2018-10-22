package com.fadeland.editor.ui.layerMenu;

public enum LayerTools
{
    NEWTILE("newTileLayer", LayerTypes.TILE), NEWOBJECT("newObjectLayer", LayerTypes.OBJECT);

    public String name;
    public LayerTypes type;
    LayerTools(String name, LayerTypes type)
    {
        this.name = name;
        this.type = type;
    }
}
