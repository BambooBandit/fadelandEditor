package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class MapObject extends Tile
{
//    protected float rotation;
    public Polygon polygon;
//    public RotationBox rotationBox;
    public MoveBox moveBox;
    private boolean selected;
    public Array<PropertyField> properties; // properties such as probability and rotation. They belong to all tiles and sprites

    public int indexOfSelectedVertice = -1; // x index. y is + 1
    public int indexOfHoveredVertice = -1; // x index. y is + 1

    public MapObject(TileMap map, ObjectLayer layer, FloatArray vertices, float x, float y)
    {
        super(map, layer, vertices, x, y);
        this.properties = new Array<>();
        this.polygon = new Polygon(vertices.toArray());
        this.polygon.setPosition(x, y);
        this.position.set(x, y);
        this.moveBox = new MoveBox(this, map);
        this.moveBox.setPosition(x, y);
    }

    @Override
    public void setTool(TileTool tool) { }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.polygon.setPosition(x, y);
        if(indexOfSelectedVertice != -1)
            this.moveBox.setPosition(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1]);
        else
            this.moveBox.setPosition(x, y);
    }

    public void moveVertice(float x, float y)
    {
        float[] vertices = this.polygon.getVertices();
        vertices[indexOfSelectedVertice] = x - this.polygon.getX();
        vertices[indexOfSelectedVertice + 1] = y - this.polygon.getY();
        this.polygon.setVertices(vertices);
        setPosition(polygon.getX(), polygon.getY());
    }

    public void draw()
    {
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());
    }

    public void drawSelectedVertices()
    {
        if(indexOfSelectedVertice != -1)
            map.editor.shapeRenderer.circle(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1], 5);
    }

    public void drawHoveredVertices()
    {
        if(indexOfHoveredVertice != -1)
            map.editor.shapeRenderer.circle(polygon.getTransformedVertices()[indexOfHoveredVertice], polygon.getTransformedVertices()[indexOfHoveredVertice + 1], 5);
    }

    public void drawMoveBox()
    {
        if(selected)
            moveBox.sprite.draw(map.editor.batch);
    }

//    public void setRotation(float degree)
//    {
//        this.rotation = degree;
//        this.sprite.setRotation(degree);
//        this.polygon.setRotation(degree);
//
//        for(int i = 0; i < lockedProperties.size; i ++)
//        {
//            if(lockedProperties.get(i).getProperty().equals("Rotation"))
//            {
//                lockedProperties.get(i).value.setText(Float.toString(this.rotation));
//                break;
//            }
//        }
//    }

//    public void rotate(float degree)
//    {
//        this.rotation += degree;
//        Utils.tilePositionCopy.set(position);
//        Vector2 endPos = Utils.tilePositionCopy.sub(Utils.centerOrigin).rotate(degree).add(Utils.centerOrigin); // TODO don't assume this was set in case rotate is used somewhere else
//        setPosition(endPos.x, endPos.y);
//        this.sprite.rotate(degree);
//        this.polygon.rotate(degree);
//
//        for(int i = 0; i < lockedProperties.size; i ++)
//        {
//            if(lockedProperties.get(i).getProperty().equals("Rotation"))
//            {
//                lockedProperties.get(i).value.setText(Float.toString(this.rotation));
//                break;
//            }
//        }
//    }

    public void select()
    {
        this.selected = true;
    }
    public void unselect()
    {
        this.selected = false;
    }
}
