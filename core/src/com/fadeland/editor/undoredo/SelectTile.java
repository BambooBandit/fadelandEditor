package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class SelectTile extends PerformableAction
{
    public Tile oldSelectedTile;
    public Tile newSelectedTile;
    
    public SelectTile(TileMap map, Tile oldTile, Tile newTile)
    {
        super(map);
        this.oldSelectedTile = oldTile;
        this.newSelectedTile = newTile;
    }
    
    @Override
    public void undo()
    {
        super.undo();
        map.selectedTile = oldSelectedTile;
    }

    @Override
    public void redo()
    {
        super.redo();
        map.selectedTile = newSelectedTile;
    }
}
