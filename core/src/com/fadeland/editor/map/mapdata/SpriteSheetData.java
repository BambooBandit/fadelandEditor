package com.fadeland.editor.map.mapdata;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class SpriteSheetData extends SheetData
{
    public SpriteSheetData(){}
    public SpriteSheetData(TileMap tileMap, String name, Array<TextureAtlas.AtlasRegion> spriteNames)
    {
        super(tileMap, name);
        for(int i = 0; i < tileMap.tileMenu.spriteTable.getChildren().size; i ++)
            this.tools.add(new ToolData((TileTool) tileMap.tileMenu.spriteTable.getChildren().get(i)));
    }
}
