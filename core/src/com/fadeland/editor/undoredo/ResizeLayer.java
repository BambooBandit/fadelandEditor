package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileLayer;
import com.fadeland.editor.map.TileMap;

public class ResizeLayer implements Action
{
    public TileMap map;
    public Layer layer;
    int oldWidth, oldHeight, newWidth, newHeight;
    public Array<Tile> oldTiles;
    public Array<Tile> newTiles;

    public ResizeLayer(TileMap map, Layer layer, int oldWidth, int oldHeight)
    {
        this.map = map;
        this.layer = layer;

        this.oldTiles = new Array<>();
        this.newTiles = new Array<>();

        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;

        if (layer instanceof TileLayer)
            this.oldTiles.addAll(layer.tiles);
    }

    public void addNew(int newWidth, int newHeight)
    {
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        
        if (layer instanceof TileLayer)
            this.newTiles.addAll(layer.tiles);
    }

    @Override
    public void undo()
    {
        layer.width = this.oldWidth;
        layer.height = this.oldHeight;
        if (layer instanceof TileLayer)
        {
            layer.tiles.clear();
            layer.tiles.addAll(oldTiles);
        }
    }

    @Override
    public void redo()
    {
        layer.width = this.newWidth;
        layer.height = this.newHeight;
        if (layer instanceof TileLayer)
        {
            layer.tiles.clear();
            layer.tiles.addAll(newTiles);
        }
    }
}
