package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.Layer;

public class CreateOrRemoveLayer implements Action
{
    public Layer layer;
    public boolean create;

    public CreateOrRemoveLayer(Layer layer, boolean create)
    {
        this.layer = layer;
        this.create = create;
    }

    @Override
    public void undo()
    {
        if(create)
            remove();
        else
            create();
    }

    @Override
    public void redo()
    {
        if(create)
            create();
        else
            remove();
    }

    private void create()
    {
        this.layer.map.layerMenu.addLayer(this.layer);
    }

    private void remove()
    {
        this.layer.map.layerMenu.removeLayer(layer.layerField);
    }
}