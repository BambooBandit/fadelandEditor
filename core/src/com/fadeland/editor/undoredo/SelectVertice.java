package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.MapObject;

public class SelectVertice implements Action
{
    public MapObject mapObject;
    public int oldIndex, newIndex;

    public SelectVertice(MapObject mapObject, int oldIndex, int newIndex)
    {
        this.mapObject = mapObject;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    @Override
    public void undo()
    {
        this.mapObject.indexOfSelectedVertice = oldIndex;
        this.mapObject.setPosition(mapObject.polygon.getX(), mapObject.polygon.getY()); // Move the movebox to where the selected vertice is
    }

    @Override
    public void redo()
    {
        this.mapObject.indexOfSelectedVertice = newIndex;
        this.mapObject.setPosition(mapObject.polygon.getX(), mapObject.polygon.getY()); // Move the movebox to where the selected vertice is
    }
}
