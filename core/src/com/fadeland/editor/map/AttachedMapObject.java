package com.fadeland.editor.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;

public class AttachedMapObject extends MapObject
{
    public Vector2 positionOffset; // Position of this object should be relative to its attached parent. Add this to parents position to get the position.
    public Vector2 oldPositionOffset; // doesn't update. Used to determine where the body should be placed.
    public AttachedMapObject(TileMap map, Tile attachedTile, FloatArray vertices, float xOffset, float yOffset, float width, float height, float x, float y)
    {
        super(map, vertices, x, y);
        this.attachedTile = attachedTile;
        polygon.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
        this.positionOffset = new Vector2(xOffset, yOffset);
    }

    public AttachedMapObject(TileMap map, Tile attachedTile, float xOffset, float yOffset, float width, float height, float x, float y)
    {
        super(map, x, y);
        this.attachedTile = attachedTile;
        this.positionOffset = new Vector2(xOffset, yOffset);
    }
}
