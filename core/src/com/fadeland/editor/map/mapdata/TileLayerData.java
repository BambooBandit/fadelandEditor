package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.Layer;

import java.util.ArrayList;

public class TileLayerData extends LayerData
{
    public ArrayList<MapTileData> tiles;
    public TileLayerData(){}
    public TileLayerData(Layer layer)
    {
        super(layer);
        this.tiles = new ArrayList<>();
        for(int i = 0; i < layer.tiles.size; i ++)
            tiles.add(new MapTileData(layer.tiles.get(i)));
    }
}
