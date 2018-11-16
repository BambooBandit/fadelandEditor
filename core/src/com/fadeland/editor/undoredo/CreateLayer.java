package com.fadeland.editor.undoredo;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.ui.layerMenu.LayerMenu;
import com.fadeland.editor.ui.tileMenu.TileTool;

import java.awt.*;

public class CreateLayer implements Action
{
    public Layer layer;

    public CreateLayer(Layer layer)
    {
        this.layer = layer;
    }

    @Override
    public void undo()
    {
        Array<EventListener> listeners = layer.layerField.remove.getListeners();
        for(int i = 0; i < listeners.size; i++)
        {
            if(listeners.get(i) instanceof ClickListener){
                ((ClickListener)listeners.get(i)).clicked(null, 0, 0);
            }
        }
    }

    @Override
    public void redo()
    {
        this.layer.map.layerMenu.addLayer(this.layer);
    }
}