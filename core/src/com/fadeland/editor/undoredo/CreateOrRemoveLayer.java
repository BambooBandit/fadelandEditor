package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.TileMap;

public class CreateOrRemoveLayer extends PerformableAction
{
    public Layer layer;
    public boolean create;

    public CreateOrRemoveLayer(TileMap map, Layer layer, boolean create)
    {
        super(map);
        this.layer = layer;
        this.create = create;
    }

    @Override
    public void undo()
    {
        super.undo();
        if(create)
            remove();
        else
            create();
    }

    @Override
    public void redo()
    {
        super.redo();
        if(create)
            create();
        else
            remove();
    }

    private void create()
    {
        this.layer.map.layerMenu.addLayer(this.layer);
        if(this.layer.layerField.isSelected)
            this.layer.map.selectedLayer = this.layer;
    }

    private void remove()
    {
        this.layer.map.layerMenu.removeLayer(layer.layerField);
    }
}