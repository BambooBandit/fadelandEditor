package com.fadeland.editor.map;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.EditorPolygon;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class MapObject extends Tile
{
//    protected float rotation;
    public EditorPolygon polygon;
    public static float[] pointShape = new float[10];
//    public RotationBox rotationBox;
    public MoveBox moveBox;
    private boolean selected;
    public Array<PropertyField> properties; // properties such as probability and rotation. They belong to all tiles and sprites

    public int indexOfSelectedVertice = -1; // x index. y is + 1
    public int indexOfHoveredVertice = -1; // x index. y is + 1

    public Tile attachedTile = null;

    public Body body = null;
    public Array<Body> bodies;
    public PointLight pointLight = null;
    public Array<PointLight> pointLights;

    public float[] vertices;

    public boolean isPoint;

    // Polygon
    public MapObject(TileMap map, float[] vertices, float x, float y)
    {
        super(map, x, y);
        this.vertices = vertices;
        this.properties = new Array<>();
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        this.position.set(x, y);
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
        this.isPoint = false;
    }

    // Point
    public MapObject(TileMap map, float x, float y)
    {
        super(map, x, y);
        this.properties = new Array<>();
        this.position.set(x, y);
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
        this.isPoint = true;
    }

    @Override
    public void setTool(TileTool tool) { }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        if(isPoint && this.attachedTile != null)
        {
            float centerX = attachedTile.position.x + attachedTile.width / 2;
            float centerY = attachedTile.position.y + attachedTile.height / 2;
            float angle = (float) Math.toRadians(this.attachedTile.sprite.getRotation()); // Convert to radians

            float rotatedX = (float) (Math.cos(angle) * (position.x - centerX) - Math.sin(angle) * (position.y - centerY) + centerX);

            float rotatedY = (float) (Math.sin(angle) * (position.x - centerX) + Math.cos(angle) * (position.y - centerY) + centerY);
            x = rotatedX;
            y = rotatedY;
            float scaledX = rotatedX + (centerX - rotatedX) * (1 - attachedTile.sprite.getScaleX());
            float scaledY = rotatedY + (centerY - rotatedY) * (1 - attachedTile.sprite.getScaleY());
            super.setPosition(scaledX, scaledY);
        }
        if(!isPoint)
        {
            this.polygon.setPosition(x, y);
            float rotation = 0;
            float width = 0, height = 0;
            if(this.attachedTile != null && this.attachedTile instanceof MapSprite)
            {
                MapSprite mapSprite = (MapSprite) this.attachedTile;
                width = mapSprite.width;
                height = mapSprite.height;
                rotation = (float) Math.toRadians(mapSprite.rotation);
                polygon.setRotation(mapSprite.rotation);
            }
            if (this.body != null)
            {
                this.body.setTransform(this.position.x + (width / 2), this.position.y + (height / 2), rotation);
                map.searchForBlockedTiles();
            }
            else if(this.bodies != null && this.bodies.size > 0)
            {
                int bodyIndex = 0;
                AttachedMapObject attachedMapObject = (AttachedMapObject) this;
                boolean searchForBlocked = false;
                for (int i = 0; i < map.layers.size; i++)
                {
                    for(int k = 0; k < map.layers.get(i).tiles.size; k ++)
                    {
                        if(this.attachedTile.tool == map.layers.get(i).tiles.get(k).tool)
                        {
                            Body body = bodies.get(bodyIndex);
                            float rotation2 = rotation;
                            if(body.getUserData() instanceof MapSprite)
                            {
                                MapSprite mapSprite = (MapSprite) body.getUserData();
                                rotation2 = (float) Math.toRadians(mapSprite.rotation);
                            }
                            Utils.positionDifference.set(attachedMapObject.positionOffset);
                            Utils.positionDifference.sub(attachedMapObject.oldPositionOffset);
                            Vector2 bodyPosition = bodies.get(bodyIndex).getPosition();
                            boolean changed = !(bodyPosition.x == Utils.positionDifference.x + map.layers.get(i).tiles.get(k).position.x + map.layers.get(i).tiles.get(k).width / 2 && bodyPosition.y == Utils.positionDifference.y + map.layers.get(i).tiles.get(k).position.y + map.layers.get(i).tiles.get(k).height / 2 && bodies.get(bodyIndex).getAngle() == rotation2);
                            if(changed)
                            {
                                bodies.get(bodyIndex).setTransform(Utils.positionDifference.x + map.layers.get(i).tiles.get(k).position.x + map.layers.get(i).tiles.get(k).width / 2, Utils.positionDifference.y + map.layers.get(i).tiles.get(k).position.y + map.layers.get(i).tiles.get(k).height / 2, rotation2);
                                searchForBlocked = true;
                            }
                            bodyIndex ++;
                        }
                    }
                }
                if(searchForBlocked)
                    map.searchForBlockedTiles();
            }
        }
        else if(this.pointLight != null)
            this.pointLight.setPosition(this.position);
        else if(this.pointLights != null)
        {
            int lightIndex = 0;
            AttachedMapObject attachedMapObject = (AttachedMapObject) this;
            for (int i = 0; i < map.layers.size; i++)
            {
                for(int k = 0; k < map.layers.get(i).tiles.size; k ++)
                {
                    if(this.attachedTile.tool == map.layers.get(i).tiles.get(k).tool)
                    {
                        pointLights.get(lightIndex).setPosition(attachedMapObject.positionOffset.x + map.layers.get(i).tiles.get(k).position.x, attachedMapObject.positionOffset.y + map.layers.get(i).tiles.get(k).position.y);
                        lightIndex ++;
                    }
                }
            }
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
        else if(this.bodies != null && this.bodies.size > 0)
        {
            removeBody();
            createBody();
        }
        this.polygon.setVertices(vertices);
        setPosition(polygon.getX(), polygon.getY());
        map.searchForBlockedTiles();
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

    @Override
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

    public PropertyField getLightPropertyField()
    {
        for(int i = 0; i < this.properties.size; i ++)
            if(this.properties.get(i).rgbaDistanceRayAmount)
                return this.properties.get(i);
        return null;
    }

    public PropertyField getPropertyField(String propertyName)
    {
        for(int i = 0; i < this.properties.size; i ++)
        {
            if(this.properties.get(i).getProperty() != null && this.properties.get(i).getProperty().equals(propertyName))
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
        else if(this.bodies != null)
        {
            for(int i = 0; i < this.bodies.size; i ++)
                this.map.world.destroyBody(this.bodies.get(i));
            this.bodies.clear();
        }
        map.searchForBlockedTiles();
    }

    public void createBody()
    {
        if(this.attachedTile != null)
        {
            removeBody();
            for (int i = 0; i < map.layers.size; i++)
            {
                for(int k = 0; k < map.layers.get(i).tiles.size; k ++)
                {
                    if(this.attachedTile.tool == map.layers.get(i).tiles.get(k).tool)
                        createBody(map.layers.get(i).tiles.get(k));
                }
            }
        }
        else if(this.body == null && !(this instanceof AttachedMapObject))
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
            map.searchForBlockedTiles();
        }
    }

    public void createBody(Tile tile)
    {
        AttachedMapObject attachedMapObject = (AttachedMapObject) this;
        if(bodies == null)
            bodies = new Array<>();

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(this.position);
        PolygonShape shape = new PolygonShape();
        float[] vertices = this.polygon.getScaledVertices().clone();
        if(attachedMapObject.oldPositionOffset == null)
            attachedMapObject.oldPositionOffset = new Vector2();
        attachedMapObject.oldPositionOffset.set(attachedMapObject.positionOffset);
        for(int i = 0; i < vertices.length - 1; i += 2)
        {
            vertices[i] -= tile.width / 2;
            vertices[i] += attachedMapObject.positionOffset.x;
            vertices[i + 1] -= tile.height / 2;
            vertices[i + 1] += attachedMapObject.positionOffset.y;
        }
        shape.set(vertices);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = PhysicsBits.WORLD_PHYSICS;
        fixtureDef.filter.maskBits = PhysicsBits.LIGHT_PHYSICS;
        Body body = this.map.world.createBody(bodyDef).createFixture(fixtureDef).getBody();
        body.setTransform(tile.position.x + tile.width / 2, tile.position.y + tile.height / 2, 0);
        shape.dispose();

        body.setUserData(tile);
        bodies.add(body);
        map.searchForBlockedTiles();
    }

    public void createLight()
    {
        if(this.attachedTile != null)
        {
            removeLight();
            for (int i = 0; i < map.layers.size; i++)
            {
                for(int k = 0; k < map.layers.get(i).tiles.size; k ++)
                {
                    if(this.attachedTile.tool == map.layers.get(i).tiles.get(k).tool)
                        createLight(map.layers.get(i).tiles.get(k));
                }
            }
        }
        else if(this.pointLight == null)
        {
            PropertyField propertyField = getLightPropertyField();
            Color color = new Color(Float.parseFloat(propertyField.rValue.getText()), Float.parseFloat(propertyField.gValue.getText()), Float.parseFloat(propertyField.bValue.getText()), Float.parseFloat(propertyField.aValue.getText()));
            this.pointLight = new PointLight(map.rayHandler, Integer.parseInt(propertyField.rayAmountValue.getText()), color, Float.parseFloat(propertyField.distanceValue.getText()), this.position.x, this.position.y);
        }
    }

    public void createLight(Tile tile)
    {
        AttachedMapObject attachedMapObject = (AttachedMapObject) this;
        if(pointLights == null)
            pointLights = new Array<>();
        PropertyField propertyField = getLightPropertyField();
        Color color = new Color(Float.parseFloat(propertyField.rValue.getText()), Float.parseFloat(propertyField.gValue.getText()), Float.parseFloat(propertyField.bValue.getText()), Float.parseFloat(propertyField.aValue.getText()));
        pointLights.add(new PointLight(map.rayHandler, Integer.parseInt(propertyField.rayAmountValue.getText()), color, Float.parseFloat(propertyField.distanceValue.getText()), attachedMapObject.positionOffset.x + tile.position.x, attachedMapObject.positionOffset.y + tile.position.y));
    }

    public void removeLight()
    {
        if(this.pointLight != null)
        {
            this.pointLight.remove();
            this.pointLight = null;
        }
        else if(this.pointLights != null)
        {
            for (int i = 0; i < this.pointLights.size; i++)
                this.pointLights.get(i).remove();
            this.pointLights.clear();
        }
    }

    public void updateLightsAndBodies()
    {
        if(this.body != null || (this.bodies != null && this.bodies.size > 0))
        {
            removeBody();
            createBody();
        }

        if(this.pointLight != null || (this.pointLight != null && this.pointLights.size > 0))
        {
            removeLight();
            createLight();
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
