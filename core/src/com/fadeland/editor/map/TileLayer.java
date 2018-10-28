package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerField;

public class TileLayer extends Layer
{

    public Array<Tile> tiles;

    public TileLayer(FadelandEditor editor, TileMap map, LayerField layerField)
    {
        super(editor, map, layerField);

        this.tiles = new Array<>();

        for(int y = 0; y < map.mapHeight; y ++)
        {
            for(int x = 0; x < map.mapWidth; x ++)
                this.tiles.add(new Tile(map, this, x * 64, y * 64));
        }
    }

    @Override
    public void draw()
    {
        for(int i = 0; i < tiles.size; i ++)
            this.tiles.get(i).draw();
        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH)
        {
            for (int i = 0; i < editor.getTileTools().size; i ++)
            {
                editor.getTileTools().get(i).previewSprite.setAlpha(.25f);
                editor.getTileTools().get(i).previewSprite.draw(editor.batch);
            }
        }
    }

}
