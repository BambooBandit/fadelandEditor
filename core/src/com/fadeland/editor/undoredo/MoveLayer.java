package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.ui.layerMenu.LayerField;

public class MoveLayer implements Action
{
    public Array<LayerField> oldLayers;
    public Array<LayerField> newLayers;
    public Array<LayerField> layers;

    public MoveLayer(Array<LayerField> layers)
    {
        this.oldLayers = new Array(layers);
        this.layers = layers;
    }

    public void addNewLayers()
    {
        this.newLayers = new Array(layers);
    }

    @Override
    public void undo()
    {
        this.layers.clear();
        this.layers.addAll(oldLayers);
        this.layers.first().mapLayer.map.layerMenu.rearrangeLayers();
        this.layers.first().mapLayer.map.layerMenu.rebuild();
    }

    @Override
    public void redo()
    {
        this.layers.clear();
        this.layers.addAll(newLayers);
        this.layers.first().mapLayer.map.layerMenu.rearrangeLayers();
        this.layers.first().mapLayer.map.layerMenu.rebuild();
    }
}