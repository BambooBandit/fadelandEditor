package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.map.TileMap.tileSize;

public class Tile
{
    protected TileMap map;
    public Sprite sprite;
    protected float width, height;
    public Vector2 position;
    public TileTool tool;
    public boolean hasBeenPainted = false;
    public boolean hasBlockedObjectOnTop = false;
    public Layer layer;
    public Array<AttachedMapObject> drawableAttachedMapObjects;

    // For Tiles, and objects
    public Tile(TileMap map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.position = new Vector2(x, y);
        this.width = tileSize;
        this.height = tileSize;

        this.drawableAttachedMapObjects = new Array<>();
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

        this.drawableAttachedMapObjects = new Array<>();

        if(this.tool != null)
        {
            for (int i = 0; i < this.tool.mapObjects.size; i++)
                this.drawableAttachedMapObjects.add(new AttachedMapObject(this.tool.mapObjects.get(i), this));
        }
    }

    public void setTool(TileTool tool)
    {
        this.drawableAttachedMapObjects.clear();
        TileTool oldTool = this.tool;
        if(tool == null)
            this.sprite = null;
        else
            this.sprite = new Sprite(tool.textureRegion);
        this.tool = tool;
        if(this.tool != null)
        {
            for (int i = 0; i < this.tool.mapObjects.size; i++)
                this.drawableAttachedMapObjects.add(new AttachedMapObject(this.tool.mapObjects.get(i), this));
        }
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

    public void drawTopSprites()
    {
        if(this.sprite != null)
        {
            if(this.tool.topSprites != null)
            {
                for(int i = 0; i < this.tool.topSprites.size; i ++)
                    map.editor.batch.draw(this.tool.topSprites.get(i), position.x, position.y);
            }
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
//        this.drawableAttachedMapObjects.add(new AttachedMapObject(mapObject, this));
        this.tool.mapObjects.add(mapObject);
        map.addDrawableAttachedMapObjects(tool);
    }
}
