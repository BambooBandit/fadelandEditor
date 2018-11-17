package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class SelectTileTool implements Action
{
    public TileMap map;
    public Array<TileTool> selectedTiles;
    public Array<TileTool> oldSelection;
    public Array<TileTool> newSelection;

    public SelectTileTool(TileMap map, Array<TileTool> selectedTiles)
    {
        this.map = map;
        this.selectedTiles = selectedTiles;
        this.oldSelection = new Array<>(selectedTiles);
    }

    public void addSelectedTiles()
    {
        this.newSelection = new Array<>(selectedTiles);
    }

    @Override
    public void undo()
    {
        for(int i = 0; i < selectedTiles.size; i ++)
            selectedTiles.get(i).unselect();
        selectedTiles.clear();
        selectedTiles.addAll(oldSelection);
        for(int i = 0; i < selectedTiles.size; i ++)
            selectedTiles.get(i).select();
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        for(int i = 0; i < selectedTiles.size; i ++)
            selectedTiles.get(i).unselect();
        selectedTiles.clear();
        selectedTiles.addAll(newSelection);
        for(int i = 0; i < selectedTiles.size; i ++)
            selectedTiles.get(i).select();
        this.map.propertyMenu.rebuild();
    }
}
