package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
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
    public float z;

    float[] verts;

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
        this.verts = new float[20];
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
        float u = sprite.getU();
        float v = sprite.getV();
        float u2 = sprite.getU2();
        float v2 = sprite.getV2();
        float centerScreen = Gdx.graphics.getWidth() / 2;
        float centerSprite = Utils.project(map.camera,sprite.getX() + sprite.getHeight() / 2, sprite.getY()).x;
        float skewAmount = (centerSprite - centerScreen) / 2;
        float[] vertices = sprite.getVertices();

        verts[0] = vertices[SpriteBatch.X2] + skewAmount;
        verts[1] = vertices[SpriteBatch.Y2];
        verts[2] = Color.toFloatBits(255, 255, 255, 255);
        verts[3] = u;
        verts[4] = v;

        verts[5] = vertices[SpriteBatch.X3] + skewAmount;
        verts[6] = vertices[SpriteBatch.Y3];
        verts[7] = Color.toFloatBits(255, 255, 255, 255);
        verts[8] = u2;
        verts[9] = v;

        verts[10] = vertices[SpriteBatch.X4];
        verts[11] = vertices[SpriteBatch.Y4];
        verts[12] = Color.toFloatBits(255, 255, 255, 255);
        verts[13] = u2;
        verts[14] = v2;

        verts[15] = vertices[SpriteBatch.X1];
        verts[16] = vertices[SpriteBatch.Y1];
        verts[17] = Color.toFloatBits(255, 255, 255, 255);
        verts[18] = u;
        verts[19] = v2;

        map.editor.batch.draw(sprite.getTexture(), verts, 0, verts.length);

        if(tool.topSprite != null)
        {
            tool.topSprite.setPosition(sprite.getX(), sprite.getY());
            tool.topSprite.setRotation(sprite.getRotation());

            map.editor.batch.draw(tool.topSprite.getTexture(), verts, 0, verts.length);
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

    public void setZ(float z)
    {
        this.z = z;
    }

    public void setRotation(float degree)
    {
        this.rotation = degree;
        this.sprite.setRotation(degree);
        this.polygon.setRotation(degree);
        if(this.tool.topSprite != null)
            this.tool.topSprite.setRotation(degree);
        for(int i = 0; i < tool.mapObjects.size; i ++)
        {
            Body body = tool.mapObjects.get(i).body;
            Array<Body> bodies = tool.mapObjects.get(i).bodies;
            if(body != null)
                tool.mapObjects.get(i).body.setTransform(body.getPosition(), this.rotation);
            if(bodies != null)
            {
                for (int k = 0; k < bodies.size; k++)
                    tool.mapObjects.get(i).bodies.get(k).setTransform(bodies.get(k).getPosition(), this.rotation);
            }
            tool.mapObjects.get(i).polygon.rotate(degree);
        }

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
        {
            Body body = tool.mapObjects.get(i).body;
            Array<Body> bodies = tool.mapObjects.get(i).bodies;
            if(body != null)
                tool.mapObjects.get(i).body.setTransform(body.getPosition(), this.rotation);
            if(bodies != null)
            {
                for (int k = 0; k < bodies.size; k++)
                    tool.mapObjects.get(i).bodies.get(k).setTransform(bodies.get(k).getPosition(), this.rotation);
            }
            tool.mapObjects.get(i).polygon.rotate(degree);
        }

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
}
