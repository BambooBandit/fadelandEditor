package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class CreateOrRemoveObject implements Action
{
    public TileMap map;
    public Array<Tile> mapObjects;
    public Array<Tile> oldObjects;
    public Array<Tile> newObjects;
    public Array<MapObject> selection;
    public Array<MapObject> oldSelection;
    public Array<MapObject> newSelection;

    public CreateOrRemoveObject(TileMap map, Array<Tile> mapObjects, Array<MapObject> selection)
    {
        this.map = map;
        this.mapObjects = mapObjects;
        this.selection = selection;
        this.oldObjects = new Array<>(mapObjects);
        this.oldSelection = new Array<>(selection);
    }

    public void addObjects()
    {
        this.newObjects = new Array<>(mapObjects);
        this.newSelection = new Array<>(selection);
    }

    @Override
    public void undo()
    {
        this.mapObjects.clear();
        this.mapObjects.addAll(oldObjects);
        this.selection.clear();
        this.selection.addAll(oldSelection);
        map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        this.mapObjects.clear();
        this.mapObjects.addAll(newObjects);
        this.selection.clear();
        this.selection.addAll(newSelection);
        map.propertyMenu.rebuild();
    }
}
