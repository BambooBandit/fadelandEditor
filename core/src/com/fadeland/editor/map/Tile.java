package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.map.TileMap.tileSize;

public class Tile
{
    protected TileMap map;
    protected Sprite sprite;
    protected float width, height;
    public Vector2 position;
    public TileTool tool;
    public boolean hasBeenPainted = false;

    // For Tiles, and objects
    public Tile(TileMap map, float x, float y)
    {
        this.map = map;
        this.position = new Vector2(x, y);
        this.width = tileSize;
        this.height = tileSize;
    }

    // For MapSprites
    public Tile(TileMap map, TileTool tool, float x, float y)
    {
        this.map = map;
        this.sprite = new Sprite(tool.textureRegion);
        this.position = new Vector2(x, y);
        this.width = tileSize;
        this.height = tileSize;
        this.tool = tool;
    }

    public void setTool(TileTool tool)
    {
        TileTool oldTool = this.tool;
        if(tool == null)
            this.sprite = null;
        else
            this.sprite = new Sprite(tool.textureRegion);
        this.tool = tool;
        if(oldTool != null)
        {
            for(int i = 0; i < oldTool.mapObjects.size; i ++)
                oldTool.mapObjects.get(i).updateLightsAndBodies();
        }
        if(this.tool != null)
        {
            for(int i = 0; i < this.tool.mapObjects.size; i ++)
                this.tool.mapObjects.get(i).updateLightsAndBodies();
        }
    }

    public void draw()
    {
        if(this.sprite != null)
            map.editor.batch.draw(sprite, position.x, position.y, width, height);
    }

    public void drawTopSprite()
    {
        if(this.sprite != null)
        {
            if(this.tool.topSprite != null)
                map.editor.batch.draw(this.tool.topSprite, position.x, position.y);
        }
    }

    public void setPosition(float x, float y)
    {
        this.position.set(x, y);
        if(this.sprite != null)
            this.sprite.setPosition(x, y);
    }

    public void addMapObject(AttachedMapObject mapObject)
    {
        this.tool.mapObjects.add(mapObject);
    }
}
