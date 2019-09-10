package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.MapSprite;

public class MapSpriteData
{
    public float x, y, z;
    public float r, g, b, a;
    public int id;
    public int spriteID; // Manually definable ID
    public String name;
    public String sheetName;
    public float width, height, rotation, scale;
    public int layerOverrideIndex; // The index of the layer that has been overridden to always draw behind this sprite. It is also counting from 1 rather than 0 for JSON purposes.
    public MapSpriteData(){}
    public MapSpriteData(MapSprite mapSprite)
    {
        this.x = mapSprite.position.x;
        this.y = mapSprite.position.y;
        this.z = mapSprite.z;
        this.id = mapSprite.tool.id;
        this.spriteID = mapSprite.id;
        this.name = mapSprite.tool.name;
        this.sheetName = mapSprite.tool.sheetTool.name;
        this.width = mapSprite.sprite.getWidth();
        this.height = mapSprite.sprite.getHeight();
        this.rotation = mapSprite.sprite.getRotation();
        this.scale = mapSprite.sprite.getScaleX();
        this.r = mapSprite.sprite.getColor().r;
        this.g = mapSprite.sprite.getColor().g;
        this.b = mapSprite.sprite.getColor().b;
        this.a = mapSprite.sprite.getColor().a;
        if(mapSprite.layerOverride != null)
            this.layerOverrideIndex = mapSprite.layer.map.layers.indexOf(mapSprite.layerOverride, true) + 1;
        else
            this.layerOverrideIndex = 0;
    }
}
