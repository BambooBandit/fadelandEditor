package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.AttachedMapObject;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class CreateOrRemoveAttachedObject implements Action
{
    public TileMap map;
    public Array<Tile> mapParents;
    public Array<Array<AttachedMapObject>> oldParents;
    public Array<Array<AttachedMapObject>> newParents;
    public Array<MapObject> selection;
    public Array<MapObject> oldSelection;
    public Array<MapObject> newSelection;

    public CreateOrRemoveAttachedObject(TileMap map, Array<Tile> mapParents, Array<MapObject> selection)
    {
        this.map = map;
        this.mapParents = mapParents;
        this.selection = selection;
        if(selection != null)
            this.oldSelection = new Array<>(selection);

        this.oldParents = new Array<>(mapParents.size);
        for(int i = 0; i < mapParents.size; i ++)
            oldParents.add(new Array<>(mapParents.get(i).tool.mapObjects));
    }

    public void addAttachedObjects()
    {
        if(this.selection != null)
            this.newSelection = new Array<>(selection);

        this.newParents = new Array<>(mapParents.size);
        for(int i = 0; i < mapParents.size; i ++)
            newParents.add(new Array<>(mapParents.get(i).tool.mapObjects));
    }

    @Override
    public void undo()
    {
        for(int i = 0; i < mapParents.size; i ++)
        {
            mapParents.get(i).tool.mapObjects.clear();
            mapParents.get(i).tool.mapObjects.addAll(oldParents.get(i));
        }
        if(this.selection != null)
        {
            this.selection.clear();
            this.selection.addAll(oldSelection);
        }
        map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        for(int i = 0; i < mapParents.size; i ++)
        {
            mapParents.get(i).tool.mapObjects.clear();
            mapParents.get(i).tool.mapObjects.addAll(newParents.get(i));
        }
        if(this.selection != null)
        {
            this.selection.clear();
            this.selection.addAll(newSelection);
        }
        map.propertyMenu.rebuild();
    }
}
