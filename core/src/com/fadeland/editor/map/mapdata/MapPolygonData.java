package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.MapObject;

public class MapPolygonData extends MapObjectData
{
    public float[] vertices;
    public MapPolygonData(){}
    public MapPolygonData(MapObject mapObject)
    {
        super(mapObject);
        this.vertices = mapObject.polygon.getVertices();
    }
}
