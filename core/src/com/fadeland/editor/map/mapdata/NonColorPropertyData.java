package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.ui.propertyMenu.PropertyField;

public class NonColorPropertyData extends PropertyData
{
    public String property;
    public String value;
    public NonColorPropertyData(){}
    public NonColorPropertyData(PropertyField propertyField)
    {
        this.property = propertyField.getProperty();
        this.value = propertyField.getValue();
    }
}
