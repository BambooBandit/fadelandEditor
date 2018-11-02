package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class RotationBox
{
    public Sprite sprite;
    private Rectangle rectangle;
    private TileMap map;
    private MapSprite mapSprite;
    public RotationBox(MapSprite mapSprite, TileMap Map)
    {
        int width = 25;
        int height = 25;
        this.mapSprite = mapSprite;
        this.map = map;
        this.sprite = new Sprite(new Texture("ui/rotate.png")); // TODO pack this
        this.sprite.setSize(width, height);
        this.rectangle = new Rectangle(0, 0, width, height);
    }

    public void setPosition(float x, float y)
    {
        this.sprite.setPosition(x, y);
        this.rectangle.setPosition(x, y);
    }

    public boolean contains(float x, float y)
    {
        return this.rectangle.contains(x, y);
    }
}
