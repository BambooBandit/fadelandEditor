package com.fadeland.editor.map;

import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;
import com.fadeland.editor.ui.layerMenu.LayerTypes;

public class ObjectLayer extends Layer
{

    public ObjectLayer(FadelandEditor editor, TileMap map, LayerTypes type, LayerField layerField)
    {
        super(editor, map, type, layerField);
    }

    @Override
    public void draw()
    {
        this.map.camera.zoom = this.map.zoom - z;
        this.map.camera.update();
        this.editor.batch.setProjectionMatrix(map.camera.combined);

        for(int i = 0; i < this.tiles.size; i ++)
            this.tiles.get(i).draw();

//        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH && editor.getSpriteTool() != null)
//        {
//            editor.getSpriteTool().previewSprite.setAlpha(.25f);
//            editor.getSpriteTool().previewSprite.draw(editor.batch);
//        }
    }

}
