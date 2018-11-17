package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.TileMap;

public class SelectObject implements Action
{
    public TileMap map;
    public Array<MapObject> selectedObjects;
    public Array<MapObject> selectedOld;
    public Array<MapObject> selectedNew;

    public SelectObject(TileMap map, Array<MapObject> selectedObjects)
    {
        this.map = map;
        this.selectedObjects = selectedObjects;
        this.selectedOld = new Array<>(selectedObjects);
    }

    public void addSelected()
    {
        this.selectedNew = new Array<>(selectedObjects);
    }

    @Override
    public void undo()
    {
        this.selectedObjects.clear();
        this.selectedObjects.addAll(this.selectedOld);
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        this.selectedObjects.clear();
        this.selectedObjects.addAll(this.selectedNew);
        this.map.propertyMenu.rebuild();
    }
}
