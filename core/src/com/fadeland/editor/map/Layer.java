package com.fadeland.editor.map;

import com.fadeland.editor.FadelandEditor;

public abstract class Layer
{
    protected FadelandEditor editor;
    protected TileMap map;

    public Layer(FadelandEditor editor, TileMap map)
    {
        this.editor = editor;
        this.map = map;
    }

    public abstract void draw();

}
