package com.fadeland.editor.map;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class AttachedMapObject extends MapObject
{
    public Vector2 positionOffset; // Position of this object should be relative to its attached parent. Add this to parents position to get the position.
    public AttachedMapObject(TileMap map, FloatArray vertices, float xOffset, float yOffset, float width, float height, float x, float y)
    {
        super(map, vertices, x, y);
        polygon.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
        this.positionOffset = new Vector2(xOffset, yOffset);
    }
}
