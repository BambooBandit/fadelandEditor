package com.fadeland.editor.map.mapdata;

public class TileSheetData extends SheetData
{
    public int width, height;
    public TileSheetData(){}
    public TileSheetData(String name, int width, int height)
    {
        super(name);
        this.width = width;
        this.height = height;
    }
}
