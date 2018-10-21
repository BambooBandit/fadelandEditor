package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class Tile
{
    private TileMap map;
    private Sprite sprite;
    private int x, y, width, height;
    public TileTool tool;

    public Tile(TileMap map, int x, int y)
    {
        this.map = map;
        this.x = x;
        this.y = y;
        this.width = tileSize;
        this.height = tileSize;
    }

    public Tile(TileMap map, TileTool tool, int x, int y)
    {
        this.map = map;
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
}
