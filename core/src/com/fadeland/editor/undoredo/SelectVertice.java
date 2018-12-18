package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.TileMap;

public class SelectVertice extends PerformableAction
{
    public MapObject mapObject;
    public int oldIndex, newIndex;

    public SelectVertice(TileMap map, MapObject mapObject, int oldIndex, int newIndex)
    {
        super(map);
        this.mapObject = mapObject;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    @Override
    public void undo()
    {
        super.undo();
        this.mapObject.indexOfSelectedVertice = oldIndex;
        this.mapObject.setPosition(mapObject.polygon.getX(), mapObject.polygon.getY()); // Move the movebox to where the selected vertice is
    }

    @Override
    public void redo()
    {
        super.redo();
        this.mapObject.indexOfSelectedVertice = newIndex;
        this.mapObject.setPosition(mapObject.polygon.getX(), mapObject.polygon.getY()); // Move the movebox to where the selected vertice is
    }
}
