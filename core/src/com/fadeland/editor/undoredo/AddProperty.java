package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class AddProperty implements Action
{
    public TileMap tileMap;
    public Array<MapObject> selectedMapObjects;
    public Array<TileTool> selectedTiles;
    public PropertyField propertyField;

    public AddProperty(TileMap tileMap, Array<MapObject> selectedMapObjects, Array<TileTool> selectedTiles)
    {
        this.tileMap = tileMap;
        this.selectedMapObjects = selectedMapObjects;
        this.selectedTiles = selectedTiles;
    }

    public void addProperty(PropertyField propertyField)
    {
        this.propertyField = propertyField;
    }

    @Override
    public void undo()
    {
        if(selectedMapObjects.size > 0)
        {
            for(int i = 0; i < selectedMapObjects.size; i ++)
                selectedMapObjects.get(i).properties.removeValue(propertyField, false);
        }
        else
        {
            for(int i = 0; i < selectedTiles.size; i ++)
                selectedTiles.get(i).properties.removeValue(propertyField, false);
        }
        tileMap.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        if(selectedMapObjects.size > 0)
        {
            for(int i = 0; i < selectedMapObjects.size; i ++)
                selectedMapObjects.get(i).properties.add(propertyField);
        }
        else
        {
            for(int i = 0; i < selectedTiles.size; i ++)
                selectedTiles.get(i).properties.add(propertyField);
        }
        tileMap.propertyMenu.rebuild();
    }
}
