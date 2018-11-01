package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;

public abstract class Layer
{
    public Array<Tile> tiles;

    protected FadelandEditor editor;
    protected TileMap map;
    protected LayerField layerField;

    public Layer(FadelandEditor editor, TileMap map, LayerField layerField)
    {
        this.tiles = new Array<>();
        this.editor = editor;
        this.map = map;
        this.layerField = layerField;
    }

    public abstract void draw();

}
