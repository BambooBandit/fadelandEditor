package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class Tile
{
    protected TileMap map;
    protected Layer layer;
    protected Sprite sprite;
    protected float x, y, width, height;
    public TileTool tool;

    public Tile(TileMap map, TileLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.width = tileSize;
        this.height = tileSize;
    }

    public Tile(TileMap map, SpriteLayer layer, TileTool tool, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.sprite = new Sprite(tool.textureRegion);
        this.x = x;
        this.y = y;
        this.width = tileSize;
        this.height = tileSize;
        this.tool = tool;
    }

    public void setTool(TileTool tool)
    {
        if(tool == null)
            this.sprite = null;
        else
            this.sprite = new Sprite(tool.textureRegion);
        this.tool = tool;
    }

    public void draw()
    {
        if(this.sprite != null)
            map.editor.batch.draw(sprite, x, y, width, height);
    }

    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}
