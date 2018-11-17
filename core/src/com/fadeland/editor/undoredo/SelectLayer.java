package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.TileMap;

public class SelectLayer implements Action
{
    public TileMap map;
    public Layer oldSelectedLayer;
    public Layer newSelectedLayer;
    
    public SelectLayer(TileMap map, Layer oldLayer, Layer newLayer)
    {
        this.map = map;
        this.oldSelectedLayer = oldLayer;
        this.newSelectedLayer = newLayer;
    }
    
    @Override
    public void undo()
    {
        map.layerMenu.unselectAll();
        if(oldSelectedLayer != null)
            oldSelectedLayer.layerField.select();
        map.selectedLayer = oldSelectedLayer;
    }

    @Override
    public void redo()
    {
        map.layerMenu.unselectAll();
        if(newSelectedLayer != null)
            newSelectedLayer.layerField.select();
        map.selectedLayer = newSelectedLayer;
    }
}
