package com.fadeland.editor.map;

import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;

public abstract class Layer
{
    protected FadelandEditor editor;
    protected TileMap map;
    protected LayerField layerField;

    public Layer(FadelandEditor editor, TileMap map, LayerField layerField)
    {
        this.editor = editor;
        this.map = map;
        this.layerField = layerField;
    }

    public abstract void draw();

}
