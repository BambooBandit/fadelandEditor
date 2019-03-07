package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.SheetTools;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class SpriteSheetData extends SheetData
{
    public SpriteSheetData(){}
    public SpriteSheetData(TileMap tileMap, SheetTools sheetTool)
    {
        super(tileMap, sheetTool.name);
        for(int i = 0; i < tileMap.tileMenu.spriteTable.getChildren().size; i ++)
        {
            if(tileMap.tileMenu.spriteTable.getChildren().get(i) instanceof TileTool)
            {
                TileTool tileTool = ((TileTool) tileMap.tileMenu.spriteTable.getChildren().get(i));
                if(tileTool.sheetTool == sheetTool)
                    this.tools.add(new ToolData(tileTool));
            }
        }
    }
}
