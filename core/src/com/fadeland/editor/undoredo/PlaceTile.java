package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.TileLayer;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class PlaceTile extends PerformableAction
{
    public Array<TileTool> oldTileTools;
    public Array<TileTool> newTileTools;

    public TileLayer layer;

    /** Place many tiles*/
    public PlaceTile(TileMap map, TileLayer layer)
    {
        super(map);
        this.layer = layer;
        oldTileTools = new Array<>();
        for(int i = 0; i < layer.tiles.size; i ++)
            oldTileTools.add(layer.tiles.get(i).tool);
        newTileTools = new Array<>();
    }

    /** Update the newTileTools to the map tiles after placing the tiles. */
    public void addNewTiles()
    {
        for(int i = 0; i < layer.tiles.size; i ++)
            newTileTools.add(layer.tiles.get(i).tool);
    }
    @Override
    public void undo()
    {
        super.undo();
        for(int i = 0; i < this.layer.tiles.size; i ++)
            this.layer.tiles.get(i).setTool(oldTileTools.get(i));
        this.map.findAllTilesToBeGrouped();
    }

    @Override
    public void redo()
    {
        super.redo();
        for(int i = 0; i < this.layer.tiles.size; i ++)
            this.layer.tiles.get(i).setTool(newTileTools.get(i));
        this.map.findAllTilesToBeGrouped();
    }

    public boolean changed()
    {
        for (int i = 0; i < layer.tiles.size; i++)
        {
            if (oldTileTools.get(i) != newTileTools.get(i))
                return true;
        }
        return false;
    }
}
