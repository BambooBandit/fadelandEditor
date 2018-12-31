package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.AttachedMapObject;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.ui.propertyMenu.PropertyField;

import java.util.ArrayList;

public abstract class MapObjectData
{
    public float x, y;
    public float xOffset, yOffset, width, height;
    public ArrayList<PropertyData> propertyData;
    public MapObjectData(){}
    public MapObjectData(MapObject mapObject)
    {
        this.x = mapObject.position.x;
        this.y = mapObject.position.y;
        if(mapObject instanceof AttachedMapObject)
        {
            AttachedMapObject attachedMapObject = (AttachedMapObject) mapObject;
            this.xOffset = attachedMapObject.positionOffset.x;
            this.yOffset = attachedMapObject.positionOffset.y;
            this.width = attachedMapObject.width;
            this.height = attachedMapObject.height;
        }
        this.propertyData = new ArrayList<>();
        for(int i = 0; i < mapObject.properties.size; i ++)
        {
            PropertyField properties = mapObject.properties.get(i);
            if(properties.rgba)
                propertyData.add(new ColorPropertyData(properties));
            else if(properties.rgbaDistanceRayAmount)
                propertyData.add(new LightPropertyData(properties));
            else
                propertyData.add(new NonColorPropertyData(properties));
        }
    }
}
