package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class Tile
{
    protected TileMap map;
    protected Layer layer;
    protected Sprite sprite;
    protected float width, height;
    public Vector2 position;
    public TileTool tool;
    public boolean hasBeenPainted = false;

    public Tile(TileMap map, TileLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.position = new Vector2(x, y);
        this.width = tileSize;
        this.height = tileSize;
    }

    public Tile(TileMap map, SpriteLayer layer, TileTool tool, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.sprite = new Sprite(tool.textureRegion);
        this.position = new Vector2(x, y);
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
            map.editor.batch.draw(sprite, position.x, position.y, width, height);
    }

    public void setPosition(float x, float y)
    {
        this.position.set(x, y);
        if(this.sprite != null)
            this.sprite.setPosition(x, y);
    }
}
