package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.ui.propertyMenu.PropertyField;

public class ColorPropertyData extends PropertyData
{
    public float r, g, b, a;
    public ColorPropertyData(){}
    public ColorPropertyData(PropertyField propertyField)
    {
        this.r = propertyField.getR();
        this.g = propertyField.getG();
        this.b = propertyField.getB();
        this.a = propertyField.getA();
    }
}
