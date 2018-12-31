package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.TileGroup;

import java.util.ArrayList;

public class TileGroupData
{
    public ArrayList<Integer> boundGroupIds;
    public ArrayList<String> types; // 2D array of tile types that have a group bound to it
    public int width; // Determines how long the rows of the boundGroup array is.
    public int height; // Determines how long the columns of the boundGroup array is.
    public TileGroupData(){}
    public TileGroupData(TileGroup tileGroup)
    {
        this.boundGroupIds = new ArrayList<>(tileGroup.boundGroup.size);
        for(int i = 0; i < tileGroup.boundGroup.size; i++)
        {
            if(tileGroup.boundGroup.get(i) == null)
                this.boundGroupIds.add(null);
            else
                this.boundGroupIds.add(tileGroup.boundGroup.get(i).id);
        }
        this.types = new ArrayList<>(tileGroup.types.size);
        for(int i = 0; i < tileGroup.types.size; i++)
            this.types.add(tileGroup.types.get(i));
        this.width = tileGroup.width;
        this.height = tileGroup.height;
    }
}
