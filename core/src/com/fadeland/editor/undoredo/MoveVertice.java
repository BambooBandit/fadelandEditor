package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.TileMap;

public class MoveVertice extends PerformableAction
{
    public MapObject mapObject;
    public float oldX, oldY, newX, newY;

    public MoveVertice(TileMap map, MapObject mapObject, float oldX, float oldY)
    {
        super(map);
        this.mapObject = mapObject;
        this.oldX = oldX;
        this.oldY = oldY;
    }

    public void addNewVertices(float newX, float newY)
    {
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void undo()
    {
        super.undo();
        this.mapObject.moveVertice(oldX, oldY);
    }

    @Override
    public void redo()
    {
        super.redo();
        this.mapObject.moveVertice(newX, newY);
    }
}
