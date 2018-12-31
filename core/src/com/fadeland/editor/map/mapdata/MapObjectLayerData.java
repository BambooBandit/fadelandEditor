package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.MapObject;

import java.util.ArrayList;

public class MapObjectLayerData extends LayerData
{
    public ArrayList<MapObjectData> tiles;
    public MapObjectLayerData(){}
    public MapObjectLayerData(Layer layer)
    {
        super(layer);
        this.tiles = new ArrayList<>();
        for(int i = 0; i < layer.tiles.size; i ++)
        {
            if(((MapObject)layer.tiles.get(i)).isPoint)
                tiles.add(new MapPointData((MapObject) layer.tiles.get(i)));
            else
                tiles.add(new MapPolygonData((MapObject) layer.tiles.get(i)));
        }
    }
}
