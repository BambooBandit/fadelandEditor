package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerField;

public class SpriteLayer extends Layer
{

    public SpriteLayer(FadelandEditor editor, TileMap map, LayerField layerField)
    {
        super(editor, map, layerField);
    }

    @Override
    public void draw()
    {
        for(int i = 0; i < this.tiles.size; i ++)
            this.tiles.get(i).draw();

        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH && editor.getSpriteTool() != null)
        {
            editor.getSpriteTool().previewSprite.setAlpha(.25f);
            editor.getSpriteTool().previewSprite.draw(editor.batch);
        }
    }
}
