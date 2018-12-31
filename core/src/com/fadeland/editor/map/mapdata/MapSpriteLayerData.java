package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.MapSprite;

import java.util.ArrayList;

public class MapSpriteLayerData extends LayerData
{
    public ArrayList<MapSpriteData> tiles;
    public MapSpriteLayerData(){}
    public MapSpriteLayerData(Layer layer)
    {
        super(layer);
        this.tiles = new ArrayList<>();
        for(int i = 0; i < layer.tiles.size; i ++)
            tiles.add(new MapSpriteData((MapSprite) layer.tiles.get(i)));
    }
}
