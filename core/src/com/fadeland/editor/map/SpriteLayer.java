package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;

public class SpriteLayer extends Layer
{

    public Array<Tile> sprites;

    public SpriteLayer(FadelandEditor editor, TileMap map, LayerField layerField)
    {
        super(editor, map, layerField);

        this.sprites = new Array<>();
    }

    @Override
    public void draw()
    {
        for(int i = 0; i < sprites.size; i ++)
            this.sprites.get(i).draw();
    }

}
