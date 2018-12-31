package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.AttachedMapObject;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

import java.util.ArrayList;

public class ToolData
{
    public int id;
    public String type;
    public ArrayList<PropertyData> propertyData;
    public ArrayList<PropertyData> lockedPropertyData;
    public ArrayList<MapObjectData> attachedObjects;
    public ToolData(){}
    public ToolData(TileTool tileTool)
    {
        this.id = tileTool.id;
        this.type = tileTool.tool.type;

        this.propertyData = new ArrayList<>();
        for(int i = 0; i < tileTool.properties.size; i ++)
        {
            PropertyField properties = tileTool.properties.get(i);
            if(properties.rgba)
                propertyData.add(new ColorPropertyData(properties));
            else if(properties.rgbaDistanceRayAmount)
                propertyData.add(new LightPropertyData(properties));
            else
                propertyData.add(new NonColorPropertyData(properties));
        }

        this.lockedPropertyData = new ArrayList<>();
        for(int i = 0; i < tileTool.lockedProperties.size; i ++)
        {
            PropertyField properties = tileTool.lockedProperties.get(i);
            if(properties.rgba)
                lockedPropertyData.add(new ColorPropertyData(properties));
            else if(properties.rgbaDistanceRayAmount)
                lockedPropertyData.add(new LightPropertyData(properties));
            else
                lockedPropertyData.add(new NonColorPropertyData(properties));
        }

        this.attachedObjects = new ArrayList<>();
        for(int i = 0; i < tileTool.mapObjects.size; i++)
        {
            AttachedMapObject attachedMapObject = tileTool.mapObjects.get(i);
            MapObjectData mapObjectData;
            if(attachedMapObject.isPoint)
                mapObjectData = new MapPointData(attachedMapObject);
            else
                mapObjectData = new MapPolygonData(attachedMapObject);
            this.attachedObjects.add(mapObjectData);
        }
    }
}
