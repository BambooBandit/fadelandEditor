package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.EditorPolygon;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class MapSprite extends Tile
{
    public float rotation, scale;
    public EditorPolygon polygon;
    public RotationBox rotationBox;
    public MoveBox moveBox;
    public ScaleBox scaleBox;
    public boolean selected;
    public Array<PropertyField> lockedProperties; // properties such as rotation. They belong to all sprites
    public float z;
    public int id;

    float[] verts;

    public MapSprite(TileMap map, TileTool tool, float x, float y)
    {
        super(map, tool, x, y);
        this.lockedProperties = new Array<>();
        this.sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) tool.textureRegion);
        x -= this.sprite.getWidth() / 2;
        y -= this.sprite.getHeight() / 2;
        this.position.set(x, y);
        this.sprite.setPosition(x, y);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.tool = tool;
        float[] vertices = {0, 0, this.width, 0, this.width, this.height, 0, this.height};
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        this.polygon.setOrigin(this.width / 2, this.height / 2);
        this.rotationBox = new RotationBox(this, map);
        this.rotationBox.setPosition(x + this.width, y + this.height);
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x + this.width, y + this.height - 25);
        this.scaleBox = new ScaleBox(this, map);
        this.scaleBox.setPosition(x + this.width, y + this.height - 50);
        this.verts = new float[20];
        this.scale = 1;
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
        this.scaleBox.setPosition(x + this.width, y + this.height - 50);

        for(int i = 0; i < drawableAttachedMapObjects.size; i++)
        {
            drawableAttachedMapObjects.get(i).setPosition(x + drawableAttachedMapObjects.get(i).parentAttached.positionOffset.x, y + drawableAttachedMapObjects.get(i).parentAttached.positionOffset.y);
        }
    }

    public void draw()
    {
        float lowestYOffset = -1;
        float tinyHeight = -1;
        if(sprite instanceof TextureAtlas.AtlasSprite)
            lowestYOffset = ((TextureAtlas.AtlasSprite) sprite).getAtlasRegion().offsetY;
        if(tool.topSprites != null)
            for (int i = 0; i < tool.topSprites.size; i++)
                if(tool.topSprites.get(i).getAtlasRegion().offsetY < lowestYOffset)
                    lowestYOffset = tool.topSprites.get(i).getAtlasRegion().offsetY;
        if(sprite instanceof TextureAtlas.AtlasSprite)
            tinyHeight = ((TextureAtlas.AtlasSprite) sprite).getAtlasRegion().offsetY - lowestYOffset;


        float u = sprite.getU();
        float v = sprite.getV();
        float u2 = sprite.getU2();
        float v2 = sprite.getV2();
        float xCenterScreen = Gdx.graphics.getWidth() / 2;
        float xCenterSprite = Utils.project(map.camera,sprite.getX() + sprite.getWidth() / 2, sprite.getY()).x;
        float yCenterScreen = Gdx.graphics.getHeight() / 2;
        float ySprite = Utils.project(map.camera,sprite.getX(), sprite.getY()).y;
        float xSkewAmount = ((xCenterSprite - xCenterScreen) / 3) * z;
        float ySkewAmount = ((ySprite - yCenterScreen) / 5) * z;
        float xOffset = xSkewAmount * (tinyHeight / sprite.getHeight());
        float yOffset = ySkewAmount * (tinyHeight / sprite.getHeight());
        float heightDifferencePercentage = sprite.getRegionHeight() / sprite.getHeight();
        xSkewAmount *= heightDifferencePercentage;
        ySkewAmount *= heightDifferencePercentage;

        if(!map.editor.fileMenu.toolPane.parallax.selected)
        {
            xSkewAmount = 0;
            ySkewAmount = 0;
            xOffset = 0;
            yOffset = 0;
        }
        float[] vertices = sprite.getVertices();

        verts[0] = vertices[SpriteBatch.X2] + xSkewAmount + xOffset;
        verts[1] = vertices[SpriteBatch.Y2] + ySkewAmount + yOffset;
        float colorFloatBits = sprite.getColor().toFloatBits();
        verts[2] = colorFloatBits;
        verts[3] = u;
        verts[4] = v;

        verts[5] = vertices[SpriteBatch.X3] + xSkewAmount + xOffset;
        verts[6] = vertices[SpriteBatch.Y3] + ySkewAmount + yOffset ;
        verts[7] = colorFloatBits;
        verts[8] = u2;
        verts[9] = v;

        verts[10] = vertices[SpriteBatch.X4] + xOffset;
        verts[11] = vertices[SpriteBatch.Y4] + yOffset;
        verts[12] = colorFloatBits;
        verts[13] = u2;
        verts[14] = v2;

        verts[15] = vertices[SpriteBatch.X1] + xOffset;
        verts[16] = vertices[SpriteBatch.Y1] + yOffset;
        verts[17] = colorFloatBits;
        verts[18] = u;
        verts[19] = v2;

        map.editor.batch.draw(sprite.getTexture(), verts, 0, verts.length);
//        sprite.draw(map.editor.batch);

        if(map.editor.fileMenu.toolPane.top.selected)
        {
            if (tool.topSprites != null)
            {
                for (int i = 0; i < tool.topSprites.size; i++)
                {
                    tool.topSprites.get(i).setPosition(sprite.getX(), sprite.getY());
                    tool.topSprites.get(i).setRotation(sprite.getRotation());

                    tinyHeight = tool.topSprites.get(i).getAtlasRegion().offsetY - lowestYOffset;

                    u = tool.topSprites.get(i).getU();
                    v = tool.topSprites.get(i).getV();
                    u2 = tool.topSprites.get(i).getU2();
                    v2 = tool.topSprites.get(i).getV2();
                    xCenterScreen = Gdx.graphics.getWidth() / 2;
                    xCenterSprite = Utils.project(map.camera, tool.topSprites.get(i).getX() + tool.topSprites.get(i).getWidth() / 2, tool.topSprites.get(i).getY()).x;
                    yCenterScreen = Gdx.graphics.getHeight() / 2;
                    ySprite = Utils.project(map.camera, tool.topSprites.get(i).getX(), tool.topSprites.get(i).getY()).y;
                    xSkewAmount = ((xCenterSprite - xCenterScreen) / 3) * z;
                    ySkewAmount = ((ySprite - yCenterScreen) / 5) * z;
                    xOffset = xSkewAmount * (tinyHeight / tool.topSprites.get(i).getHeight());
                    yOffset = ySkewAmount * (tinyHeight / tool.topSprites.get(i).getHeight());

                    heightDifferencePercentage = tool.topSprites.get(i).getRegionHeight() / tool.topSprites.get(i).getHeight();

                    xSkewAmount *= heightDifferencePercentage;
                    ySkewAmount *= heightDifferencePercentage;

                    if (!map.editor.fileMenu.toolPane.parallax.selected)
                    {
                        xSkewAmount = 0;
                        ySkewAmount = 0;
                        xOffset = 0;
                        yOffset = 0;
                    }
                    vertices = tool.topSprites.get(i).getVertices();

                    verts[0] = vertices[SpriteBatch.X2] + xSkewAmount + xOffset;
                    verts[1] = vertices[SpriteBatch.Y2] + ySkewAmount + yOffset;
                    verts[2] = Color.toFloatBits(255, 255, 255, 255);
                    verts[3] = u;
                    verts[4] = v;

                    verts[5] = vertices[SpriteBatch.X3] + xSkewAmount + xOffset;
                    verts[6] = vertices[SpriteBatch.Y3] + ySkewAmount + yOffset;
                    verts[7] = Color.toFloatBits(255, 255, 255, 255);
                    verts[8] = u2;
                    verts[9] = v;

                    verts[10] = vertices[SpriteBatch.X4] + xOffset;
                    verts[11] = vertices[SpriteBatch.Y4] + yOffset;
                    verts[12] = Color.toFloatBits(255, 255, 255, 255);
                    verts[13] = u2;
                    verts[14] = v2;

                    verts[15] = vertices[SpriteBatch.X1] + xOffset;
                    verts[16] = vertices[SpriteBatch.Y1] + yOffset;
                    verts[17] = Color.toFloatBits(255, 255, 255, 255);
                    verts[18] = u;
                    verts[19] = v2;

                    map.editor.batch.draw(tool.topSprites.get(i).getTexture(), verts, 0, verts.length);
//                tool.topSprites.get(i).draw(map.editor.batch);
                }
            }
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
    public void drawScaleBox()
    {
        if(selected)
            scaleBox.sprite.draw(map.editor.batch);
    }

    public void drawOutline()
    {
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());
    }

    public void setID(int id)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            if(lockedProperties.get(i).getProperty().equals("ID"))
            {
                lockedProperties.get(i).value.setText(Integer.toString(id));
                break;
            }
        }
        this.id = id;
    }

    public void setZ(float z)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            if(lockedProperties.get(i).getProperty().equals("Z"))
            {
                lockedProperties.get(i).value.setText(Float.toString(z));
                break;
            }
        }
        this.z = z;
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.sprite.setColor(r, g, b, a);
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            if(lockedProperties.get(i).rgba)
            {
                PropertyField colorProperty = lockedProperties.get(i);
                colorProperty.rValue.setText(Float.toString(r));
                colorProperty.gValue.setText(Float.toString(g));
                colorProperty.bValue.setText(Float.toString(b));
                colorProperty.aValue.setText(Float.toString(a));
            }
        }
    }

    public void setRotation(float degree)
    {
        this.rotation = degree;
        this.sprite.setRotation(degree);
        this.polygon.setRotation(degree);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i++)
                this.tool.topSprites.get(i).setRotation(degree);
        }
        for(int i = 0; i < drawableAttachedMapObjects.size; i ++)
        {
            Body body = drawableAttachedMapObjects.get(i).body;
            Array<Body> bodies = drawableAttachedMapObjects.get(i).bodies;
            if(body != null)
                drawableAttachedMapObjects.get(i).body.setTransform(body.getPosition(), (float) Math.toRadians(this.rotation));
            if(bodies != null)
            {
                for (int k = 0; k < bodies.size; k++)
                    drawableAttachedMapObjects.get(i).bodies.get(k).setTransform(bodies.get(k).getPosition(), (float) Math.toRadians(this.rotation));
            }
            if(drawableAttachedMapObjects.get(i).polygon != null)
                drawableAttachedMapObjects.get(i).polygon.rotate((float) Math.toRadians(degree));
            else if(drawableAttachedMapObjects.get(i).isPoint)
            {
                float centerX = position.x + width / 2;
                float centerY = position.y + height / 2;
                float angle = (float) Math.toRadians(sprite.getRotation()); // Convert to radians

                float rotatedX = (float) (Math.cos(angle) * (position.x - centerX) - Math.sin(angle) * (position.y - centerY) + centerX);

                float rotatedY = (float) (Math.sin(angle) * (position.x - centerX) + Math.cos(angle) * (position.y - centerY) + centerY);
                float scaledX = rotatedX + (centerX - rotatedX) * (1 - sprite.getScaleX());
                float scaledY = rotatedY + (centerY - rotatedY) * (1 - sprite.getScaleY());
                super.setPosition(scaledX, scaledY);
            }
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
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i ++)
                this.tool.topSprites.get(i).rotate(degree);
        }
        for(int i = 0; i < drawableAttachedMapObjects.size; i ++)
        {
            Body body = drawableAttachedMapObjects.get(i).body;
            Array<Body> bodies = drawableAttachedMapObjects.get(i).bodies;
            if(body != null)
                drawableAttachedMapObjects.get(i).body.setTransform(body.getPosition(), (float) Math.toRadians(this.rotation));
            if(bodies != null)
            {
                for (int k = 0; k < bodies.size; k++)
                    drawableAttachedMapObjects.get(i).bodies.get(k).setTransform(bodies.get(k).getPosition(), (float) Math.toRadians(this.rotation));
            }
            if(drawableAttachedMapObjects.get(i).polygon != null)
                drawableAttachedMapObjects.get(i).polygon.rotate((float) Math.toRadians(degree));
            else
            {
                AttachedMapObject attachedMapPoint = drawableAttachedMapObjects.get(i);
                float centerX = attachedMapPoint.attachedTile.position.x + drawableAttachedMapObjects.get(i).attachedTile.width / 2;
                float centerY = attachedMapPoint.attachedTile.position.y + drawableAttachedMapObjects.get(i).attachedTile.height / 2;
                float angle = (float) Math.toRadians(degree); // Convert to radians

                float rotatedX = (float) (Math.cos(angle) * (attachedMapPoint.position.x - centerX) - Math.sin(angle) * (attachedMapPoint.position.y - centerY) + centerX);

                float rotatedY = (float) (Math.sin(angle) * (attachedMapPoint.position.x - centerX) + Math.cos(angle) * (attachedMapPoint.position.y - centerY) + centerY);
                float scaledX = rotatedX + (centerX - rotatedX) * (1 - sprite.getScaleX());
                float scaledY = rotatedY + (centerY - rotatedY) * (1 - sprite.getScaleY());
                attachedMapPoint.setPosition(scaledX, scaledY);
            }
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

    public void setScale(float scale)
    {
        // TODO do undo
        if(scale > 1 || scale <= 0)
            return;
        this.scale = scale;
        this.sprite.setScale(scale);
        this.polygon.setScale(scale, scale);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i ++)
                this.tool.topSprites.get(i).setScale(scale);
        }
        for(int i = 0; i < drawableAttachedMapObjects.size; i ++)
        {
            if(drawableAttachedMapObjects.get(i).polygon != null)
                drawableAttachedMapObjects.get(i).polygon.setScale(scale, scale);
            drawableAttachedMapObjects.get(i).updateLightsAndBodies();
        }

        for(int i = 0; i < lockedProperties.size; i ++)
        {
            if(lockedProperties.get(i).getProperty().equals("Scale"))
            {
                lockedProperties.get(i).value.setText(Float.toString(scale));
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
