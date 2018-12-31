package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.Tile;

public class MapTileData
{
    public float x, y;
    public int id;
    public boolean blockedByObject;
    public MapTileData(){}
    public MapTileData(Tile tile)
    {
        this.x = tile.position.x;
        this.y = tile.position.y;
        this.blockedByObject = tile.hasBlockedObjectOnTop;
        if(tile.tool != null)
            this.id = tile.tool.id;
        else
            this.id = -1;
    }
}
