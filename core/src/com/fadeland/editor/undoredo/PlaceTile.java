package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class PlaceTile implements Action
{
    public TileMap tileMap;

    public Tile tile;
    public TileTool oldTileTool;
    public TileTool newTileTool;

    public Array<Tile> tiles;
    public Array<TileTool> oldTileTools;
    public Array<TileTool> newTileTools;

    public PlaceTile(TileMap tileMap, Tile tile, TileTool oldTileTool, TileTool newTileTool)
    {
        this.tileMap = tileMap;
        this.tile = tile;
        this.oldTileTool = oldTileTool;
        this.newTileTool = newTileTool;
    }

    /** Place many tiles*/
    public PlaceTile(TileMap tileMap)
    {
        this.tileMap = tileMap;
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
        if(tile == null)
            for(int i = 0; i < this.tiles.size; i ++)
                this.tiles.get(i).setTool(oldTileTools.get(i));
        else
            this.tile.setTool(oldTileTool);
        this.tileMap.findAllTilesToBeGrouped();
    }

    @Override
    public void redo()
    {
        if(tile == null)
            for(int i = 0; i < this.tiles.size; i ++)
                this.tiles.get(i).setTool(newTileTools.get(i));
        else
            this.tile.setTool(newTileTool);
        this.tileMap.findAllTilesToBeGrouped();
    }
}
