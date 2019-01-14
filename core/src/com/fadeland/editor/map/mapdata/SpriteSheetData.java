package com.fadeland.editor.map.mapdata;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class SpriteSheetData extends SheetData
{
    public ArrayList<String> spriteNames;
    public SpriteSheetData(){}
    public SpriteSheetData(String name, Array<TextureAtlas.AtlasRegion> spriteNames)
    {
        super(name);
        this.spriteNames = new ArrayList<>(spriteNames.size);
        for(int i = 0; i < spriteNames.size; i ++)
            this.spriteNames.add(spriteNames.get(i).name);
    }
}
