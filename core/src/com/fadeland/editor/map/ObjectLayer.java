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
        setCameraZoomToThisLayer();

        for(int i = 0; i < this.tiles.size; i ++)
            this.tiles.get(i).draw();

        setCameraZoomToSelectedLayer();

//        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH && editor.getSpriteTool() != null)
//        {
//            editor.getSpriteTool().previewSprites.setAlpha(.25f);
//            editor.getSpriteTool().previewSprites.draw(editor.batch);
//        }
    }

}
