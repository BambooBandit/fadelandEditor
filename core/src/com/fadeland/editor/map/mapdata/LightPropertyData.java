package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.ui.propertyMenu.PropertyField;

public class LightPropertyData extends ColorPropertyData
{
    public float distance;
    public int rayAmount;
    public LightPropertyData(){}
    public LightPropertyData(PropertyField propertyField)
    {
        super(propertyField);
        this.distance = propertyField.getDistance();
        this.rayAmount = propertyField.getRayAmount();
    }
}
