package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileLayer;
import com.fadeland.editor.map.TileMap;

public class ResizeMap implements Action
{
    public TileMap map;
    int oldWidth, oldHeight, newWidth, newHeight;
    public Array<Array<Tile>> oldTiles;
    public Array<Array<Tile>> newTiles;

    public ResizeMap(TileMap map, int oldWidth, int oldHeight)
    {
        this.map = map;

        this.oldTiles = new Array<>();
        this.newTiles = new Array<>();

        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;

        for(int i = 0; i < map.layers.size; i ++)
        {
            if (map.layers.get(i) instanceof TileLayer)
                this.oldTiles.add(new Array(map.layers.get(i).tiles));
            else
                this.newTiles.add(null);
        }
    }

    public void addNew(int newWidth, int newHeight)
    {
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        
        for(int i = 0; i < map.layers.size; i ++)
        {
            if (map.layers.get(i) instanceof TileLayer)
                this.newTiles.add(new Array(map.layers.get(i).tiles));
            else
                this.newTiles.add(null);
        }
    }

    @Override
    public void undo()
    {
        map.mapWidth = this.oldWidth;
        map.mapHeight = this.oldHeight;
        for(int i = 0; i < map.layers.size; i ++)
        {
            if (map.layers.get(i) instanceof TileLayer)
            {
                map.layers.get(i).tiles.clear();
                map.layers.get(i).tiles.addAll(oldTiles.get(i));
            }
        }
    }

    @Override
    public void redo()
    {
        map.mapWidth = this.newWidth;
        map.mapHeight = this.newHeight;
        for(int i = 0; i < map.layers.size; i ++)
        {
            if (map.layers.get(i) instanceof TileLayer)
            {
                map.layers.get(i).tiles.clear();
                map.layers.get(i).tiles.addAll(newTiles.get(i));
            }
        }
    }
}
