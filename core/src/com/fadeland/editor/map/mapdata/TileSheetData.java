package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class TileSheetData extends SheetData
{
    public int width, height;
    public TileSheetData(){}
    public TileSheetData(TileMap tileMap, String name, int width, int height)
    {
        super(tileMap, name);
        this.width = width;
        this.height = height;
        for(int i = 0; i < tileMap.tileMenu.tileTable.getChildren().size; i ++)
            this.tools.add(new ToolData((TileTool) tileMap.tileMenu.tileTable.getChildren().get(i)));
    }
}
