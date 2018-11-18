package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.MapObject;

public class MoveVertice implements Action
{
    public MapObject mapObject;
    public float oldX, oldY, newX, newY;

    public MoveVertice(MapObject mapObject, float oldX, float oldY)
    {
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
        this.mapObject.moveVertice(oldX, oldY);
    }

    @Override
    public void redo()
    {
        this.mapObject.moveVertice(newX, newY);
    }
}
