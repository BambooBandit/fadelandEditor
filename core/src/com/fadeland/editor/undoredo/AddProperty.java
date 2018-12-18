package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class AddProperty extends PerformableAction
{
    public Array<MapObject> selectedMapObjects;
    public Array<TileTool> selectedTiles;
    public PropertyField propertyField;

    public AddProperty(TileMap map, Array<MapObject> selectedMapObjects, Array<TileTool> selectedTiles)
    {
        super(map);
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
        super.undo();
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
        map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        super.redo();
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
        map.propertyMenu.rebuild();
    }
}
