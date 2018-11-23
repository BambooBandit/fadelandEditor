package com.fadeland.editor.map;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class MapObject extends Tile
{
//    protected float rotation;
    public Polygon polygon;
    public static float[] pointShape = new float[10];
//    public RotationBox rotationBox;
    public MoveBox moveBox;
    private boolean selected;
    public Array<PropertyField> properties; // properties such as probability and rotation. They belong to all tiles and sprites

    public int indexOfSelectedVertice = -1; // x index. y is + 1
    public int indexOfHoveredVertice = -1; // x index. y is + 1

    public Body body = null;

    public FloatArray vertices;

    public boolean isPoint;

    // Polygon
    public MapObject(TileMap map, FloatArray vertices, float x, float y)
    {
        super(map, x, y);
        this.vertices = vertices;
        this.properties = new Array<>();
        this.polygon = new Polygon(vertices.toArray());
        this.polygon.setPosition(x, y);
        this.position.set(x, y);
        this.moveBox = new MoveBox(this, map);
        this.moveBox.setPosition(x, y);
        this.isPoint = false;
    }

    // Point
    public MapObject(TileMap map, float x, float y)
    {
        super(map, x, y);
        this.properties = new Array<>();
        this.position.set(x, y);
        this.moveBox = new MoveBox(this, map);
        this.moveBox.setPosition(x, y);
        this.isPoint = true;
    }

    @Override
    public void setTool(TileTool tool) { }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        if(!isPoint)
        {
            this.polygon.setPosition(x, y);
            if (this.body != null)
                this.body.setTransform(this.position, 0);
        }
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
        if(this.body != null)
        {
            removeBody();
            createBody();
        }
        this.polygon.setVertices(vertices);
        setPosition(polygon.getX(), polygon.getY());
    }

    public float getVerticeX()
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice] + this.polygon.getX();
    }

    public float getVerticeY()
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice + 1] + this.polygon.getY();
    }

    public void draw()
    {
        if(isPoint)
        {
            pointShape[0] = position.x + 0;
            pointShape[1] = position.y + 0;
            pointShape[2] = position.x - 4;
            pointShape[3] = position.y + 8;
            pointShape[4] = position.x - 1;
            pointShape[5] = position.y + 11;
            pointShape[6] = position.x + 1;
            pointShape[7] = position.y + 11;
            pointShape[8] = position.x + 4;
            pointShape[9] = position.y + 8;
            map.editor.shapeRenderer.polygon(pointShape);
        }
        else
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

    public PropertyField getPropertyField(String propertyName)
    {
        for(int i = 0; i < this.properties.size; i ++)
        {
            if(this.properties.get(i).getProperty().equals(propertyName))
                return this.properties.get(i);
        }
        return null;
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

    public void removeBody()
    {
        if(this.body != null)
        {
            this.map.world.destroyBody(this.body);
            this.body = null;
        }
    }

    public void createBody()
    {
        if(this.body == null)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(this.position);
            PolygonShape shape = new PolygonShape();
            float[] vertices = this.polygon.getVertices();
            shape.set(vertices);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0;
            fixtureDef.filter.categoryBits = PhysicsBits.WORLD_PHYSICS;
            fixtureDef.filter.maskBits = PhysicsBits.LIGHT_PHYSICS;
            this.body = this.map.world.createBody(bodyDef).createFixture(fixtureDef).getBody();
            this.body.setTransform(this.position, 0);
            shape.dispose();
        }
    }

    public boolean isHoveredOver(float x, float y)
    {
        if(isPoint)
        {
            double distance = Math.sqrt(Math.pow((x - position.x), 2) + Math.pow((y - position.y), 2));
            return distance <= 15;
        }
        else
            return this.polygon.contains(x, y);
    }
}
