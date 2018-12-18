package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class PlaceTile extends PerformableAction
{
    public Tile tile;
    public TileTool oldTileTool;
    public TileTool newTileTool;

    public Array<Tile> tiles;
    public Array<TileTool> oldTileTools;
    public Array<TileTool> newTileTools;

    public PlaceTile(TileMap map, Tile tile, TileTool oldTileTool, TileTool newTileTool)
    {
        super(map);
        this.tile = tile;
        this.oldTileTool = oldTileTool;
        this.newTileTool = newTileTool;
    }

    /** Place many tiles*/
    public PlaceTile(TileMap map)
    {
        super(map);
        tiles = new Array<>();
        oldTileTools = new Array<>();
        newTileTools = new Array<>();
    }

    /** Add to the many tiles to undo. */
    public void addTile(Tile tile, TileTool oldTileTool, TileTool newTileTool)
    {
        // Convert to many tiles
        if(tiles == null)
        {
            tiles = new Array<>();
            oldTileTools = new Array<>();
            newTileTools = new Array<>();
            tiles.add(this.tile);
            oldTileTools.add(this.oldTileTool);
            newTileTools.add(this.newTileTool);
            this.tile = null;
            this.oldTileTool = null;
            this.newTileTool = null;
        }
        tiles.add(tile);
        oldTileTools.add(oldTileTool);
        newTileTools.add(newTileTool);
    }

    @Override
    public void undo()
    {
        super.undo();
        if(tile == null)
            for(int i = 0; i < this.tiles.size; i ++)
                this.tiles.get(i).setTool(oldTileTools.get(i));
        else
            this.tile.setTool(oldTileTool);
        this.map.findAllTilesToBeGrouped();
    }

    @Override
    public void redo()
    {
        super.redo();
        if(tile == null)
            for(int i = 0; i < this.tiles.size; i ++)
                this.tiles.get(i).setTool(newTileTools.get(i));
        else
            this.tile.setTool(newTileTool);
        this.map.findAllTilesToBeGrouped();
    }

    public boolean changed()
    {
        if(tile != null)
        {
            if (oldTileTool != newTileTool)
                return true;
        }
        else
        {
            for (int i = 0; i < tiles.size; i++)
            {
                if (oldTileTools.get(i) != newTileTools.get(i))
                    return true;
            }
        }
        return false;
    }
}
