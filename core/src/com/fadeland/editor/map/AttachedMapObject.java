package com.fadeland.editor.map;

import com.badlogic.gdx.math.Vector2;

public class AttachedMapObject extends MapObject
{
    public Vector2 positionOffset; // Position of this object should be relative to its attached parent. Add this to parents position to get the position.
    public Vector2 oldPositionOffset; // doesn't update. Used to determine where the body should be placed.
    public float width, height;
    public AttachedMapObject parentAttached;
    public static int idCreator;
    public int id;
    public AttachedMapObject(TileMap map, Layer layer, Tile attachedTile, float[] vertices, float xOffset, float yOffset, float width, float height, float x, float y)
    {
        super(map, layer, vertices, x, y);
        this.attachedTile = attachedTile;
        this.width = width;
        this.height = height;
        polygon.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
        this.positionOffset = new Vector2(xOffset, yOffset);
        this.id = idCreator;
        idCreator ++;
    }

    public AttachedMapObject(TileMap map, Layer layer, Tile attachedTile, float xOffset, float yOffset, float x, float y)
    {
        super(map, layer, x, y);
        this.attachedTile = attachedTile;
        this.positionOffset = new Vector2(xOffset, yOffset);
        this.id = idCreator;
        idCreator ++;
    }

    public AttachedMapObject(AttachedMapObject attachedMapObject, Tile tile)
    {
        super(attachedMapObject);
        this.attachedTile = tile;
        this.id = attachedMapObject.id;
        this.parentAttached = attachedMapObject;
        this.properties = attachedMapObject.properties;
        this.positionOffset = attachedMapObject.positionOffset;
        this.oldPositionOffset = attachedMapObject.oldPositionOffset;
    }
}
