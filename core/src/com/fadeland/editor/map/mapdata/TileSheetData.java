package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.SheetTools;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class TileSheetData extends SheetData
{
    public int width, height;
    public TileSheetData(){}
    public TileSheetData(TileMap tileMap, SheetTools sheetTool, int width, int height)
    {
        super(tileMap, sheetTool.name);
        this.width = width;
        this.height = height;
        for(int i = 0; i < tileMap.tileMenu.tileTable.getChildren().size; i ++)
        {
            if(tileMap.tileMenu.tileTable.getChildren().get(i) instanceof TileTool)
            {
                TileTool tileTool = ((TileTool) tileMap.tileMenu.tileTable.getChildren().get(i));
                if (tileTool.sheetTool == sheetTool)
                    this.tools.add(new ToolData(tileTool));
            }
        }
    }
}
