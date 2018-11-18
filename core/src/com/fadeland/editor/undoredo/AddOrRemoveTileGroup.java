package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.TileGroup;
import com.fadeland.editor.map.TileMap;

public class AddOrRemoveTileGroup implements Action
{
    public TileMap map;
    public Array<TileGroup> tileGroups;
    public TileGroup tileGroup;
    public boolean add;

    public AddOrRemoveTileGroup(TileMap map, Array<TileGroup> tileGroups, TileGroup tileGroup, boolean add)
    {
        this.map = map;
        this.tileGroups = tileGroups;
        this.tileGroup = tileGroup;
        this.add = add;
    }

    @Override
    public void undo()
    {
        if(add)
            remove();
        else
            add();
    }

    @Override
    public void redo()
    {
        if(add)
            add();
        else
            remove();
    }

    private void add()
    {
        this.tileGroups.add(this.tileGroup);
        map.findAllTilesToBeGrouped();
    }

    private void remove()
    {
        this.tileGroups.removeValue(this.tileGroup, true);
        map.findAllTilesToBeGrouped();
    }
}
