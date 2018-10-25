package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class MapSprite extends Tile
{
    protected float rotation;

    public MapSprite(TileMap map, SpriteLayer layer, TileTool tool, float x, float y)
    {
        super(map, layer, tool, x, y);
        this.sprite = new Sprite(tool.textureRegion);
        this.sprite.setPosition(x, y);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.tool = tool;
    }

    @Override
    public void setTool(TileTool tool) { }

    public void draw()
    {
        if(this.sprite != null)
            sprite.draw(map.editor.batch);
            map.editor.batch.draw(sprite, x, y, width, height);
    }
}
