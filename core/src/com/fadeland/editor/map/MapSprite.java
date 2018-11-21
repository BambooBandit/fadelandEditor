package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class MapSprite extends Tile
{
    public float rotation;
    public Polygon polygon;
    public RotationBox rotationBox;
    public MoveBox moveBox;
    public boolean selected;
    public Array<PropertyField> lockedProperties; // properties such as rotation. They belong to all sprites

    public MapSprite(TileMap map, TileTool tool, float x, float y)
    {
        super(map, tool, x, y);
        this.lockedProperties = new Array<>();
        this.sprite = new Sprite(tool.textureRegion);
        this.sprite.setPosition(x, y);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.tool = tool;
        float[] vertices = {0, 0, this.width, 0, this.width, this.height, 0, this.height};
        this.polygon = new Polygon(vertices);
        this.polygon.setPosition(x, y);
        this.polygon.setOrigin(this.width / 2, this.height / 2);
        this.rotationBox = new RotationBox(this, map);
        this.rotationBox.setPosition(x + this.width, y + this.height);
        this.moveBox = new MoveBox(this, map);
        this.moveBox.setPosition(x + this.width, y + this.height - 25);
    }

    @Override
    public void setTool(TileTool tool) { }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.polygon.setPosition(x, y);
        this.rotationBox.setPosition(x + this.width, y + this.height);
        this.moveBox.setPosition(x + this.width, y + this.height - 25);
    }

    public void draw()
    {
        sprite.draw(map.editor.batch);
        if(tool.topSprite != null)
        {
            tool.topSprite.setPosition(sprite.getX(), sprite.getY());
            tool.topSprite.setRotation(sprite.getRotation());
            tool.topSprite.draw(map.editor.batch);
        }
    }

    public void drawRotationBox()
    {
        if(selected)
            rotationBox.sprite.draw(map.editor.batch);
    }
    public void drawMoveBox()
    {
        if(selected)
            moveBox.sprite.draw(map.editor.batch);
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
        if(this.tool.topSprite != null)
            this.tool.topSprite.setRotation(degree);
        for(int i = 0; i < tool.mapObjects.size; i ++)
            tool.mapObjects.get(i).polygon.setRotation(degree);

        for(int i = 0; i < lockedProperties.size; i ++)
        {
            if(lockedProperties.get(i).getProperty().equals("Rotation"))
            {
                lockedProperties.get(i).value.setText(Float.toString(this.rotation));
                break;
            }
        }
    }

    public void rotate(float degree)
    {
        this.rotation += degree;
        Utils.tilePositionCopy.set(position);
        Vector2 endPos = Utils.tilePositionCopy.sub(Utils.centerOrigin).rotate(degree).add(Utils.centerOrigin); // TODO don't assume this was set in case rotate is used somewhere else
        setPosition(endPos.x, endPos.y);
        this.sprite.rotate(degree);
        this.polygon.rotate(degree);
        if(this.tool.topSprite != null)
            this.tool.topSprite.rotate(degree);
        for(int i = 0; i < tool.mapObjects.size; i ++)
            tool.mapObjects.get(i).polygon.rotate(degree);

        for(int i = 0; i < lockedProperties.size; i ++)
        {
            if(lockedProperties.get(i).getProperty().equals("Rotation"))
            {
                lockedProperties.get(i).value.setText(Float.toString(this.rotation));
                break;
            }
        }
    }

    public void select()
    {
        this.selected = true;
    }
    public void unselect()
    {
        this.selected = false;
    }

    public void setOrigin(float x, float y, boolean center)
    {
        if(center)
        {
            this.sprite.setOrigin(x + width / 2, y + height / 2);
            this.polygon.setOrigin(x + width / 2, y + height / 2);
        }
        else
        {
            this.sprite.setOrigin(x, y);
            this.polygon.setOrigin(x, y);
        }
    }
}
