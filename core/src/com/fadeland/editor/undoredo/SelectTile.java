package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class SelectTile implements Action
{
    public TileMap map;
    public Tile oldSelectedTile;
    public Tile newSelectedTile;
    
    public SelectTile(TileMap map, Tile oldTile, Tile newTile)
    {
        this.map = map;
        this.oldSelectedTile = oldTile;
        this.newSelectedTile = newTile;
    }
    
    @Override
    public void undo()
    {
        map.selectedTile = oldSelectedTile;
    }

    @Override
    public void redo()
    {
        map.selectedTile = newSelectedTile;
    }
}
