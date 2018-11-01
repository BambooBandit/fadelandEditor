package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class MapSprite extends Tile
{
    protected float rotation;
    public Polygon polygon;

    public MapSprite(TileMap map, SpriteLayer layer, TileTool tool, float x, float y)
    {
        super(map, layer, tool, x, y);
        this.sprite = new Sprite(tool.textureRegion);
        this.sprite.setPosition(x, y);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.tool = tool;
        float[] vertices = {0, 0, this.width, 0, this.width, this.height, 0, this.height};
        this.polygon = new Polygon(vertices);
        this.polygon.setPosition(x, y);
    }

    @Override
    public void setTool(TileTool tool) { }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.polygon.setPosition(x, y);
    }

    public void draw()
    {
        map.editor.batch.draw(sprite, x, y, width, height);
    }

    public void drawOutline()
    {
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());
    }

    public void setRotation(float degree)
    {
        this.rotation = degree;
        this.sprite.setRotation(degree);
        this.polygon.setRotation(degree);
    }
}
