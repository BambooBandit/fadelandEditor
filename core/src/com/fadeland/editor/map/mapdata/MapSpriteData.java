package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.MapSprite;

public class MapSpriteData
{
    public float x, y, z;
    public int id;
    public int spriteID; // Manually definable ID
    public String name;
    public float width, height, rotation, scale;
    public MapSpriteData(){}
    public MapSpriteData(MapSprite mapSprite)
    {
        this.x = mapSprite.position.x;
        this.y = mapSprite.position.y;
        this.z = mapSprite.z;
        this.id = mapSprite.tool.id;
        this.spriteID = mapSprite.id;
        this.name = mapSprite.tool.name;
        this.width = mapSprite.sprite.getWidth();
        this.height = mapSprite.sprite.getHeight();
        this.rotation = mapSprite.sprite.getRotation();
        this.scale = mapSprite.sprite.getScaleX();
    }
}
