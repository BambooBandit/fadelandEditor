package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class MoveBox
{
    public Sprite sprite;
    private Rectangle rectangle;
    public MoveBox()
    {
        int width = 25;
        int height = 25;
        this.sprite = new Sprite(new Texture("ui/move.png")); // TODO pack this
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
