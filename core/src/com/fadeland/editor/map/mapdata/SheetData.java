package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.TileMap;

import java.util.ArrayList;

public abstract class SheetData
{
    public String name;
    public ArrayList<ToolData> tools;

    public SheetData(){}
    public SheetData(TileMap tileMap, String name)
    {
        this.name = name;
        this.tools = new ArrayList<>();
    }
}
