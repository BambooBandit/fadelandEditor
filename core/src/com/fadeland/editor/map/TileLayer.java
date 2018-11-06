package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerField;

public class TileLayer extends Layer
{

    private int width, height;

    public TileLayer(FadelandEditor editor, TileMap map, LayerField layerField)
    {
        super(editor, map, layerField);

        this.width = map.mapWidth;
        this.height = map.mapHeight;

        for(int y = 0; y < height; y ++)
        {
            for(int x = 0; x < width; x ++)
                this.tiles.add(new Tile(map, x * 64, y * 64));
        }
    }

    @Override
    public void draw()
    {
        for(int i = 0; i < tiles.size; i ++)
            this.tiles.get(i).draw();
        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && editor.getFileTool() != null && editor.getTileTools() != null && editor.getFileTool().tool == Tools.BRUSH)
        {
            for (int i = 0; i < editor.getTileTools().size; i ++)
            {
                editor.getTileTools().get(i).previewSprite.setAlpha(.25f);
                editor.getTileTools().get(i).previewSprite.draw(editor.batch);
            }
        }
    }

    public void resize(int width, int height, boolean down, boolean right)
    {
        int oldWidth = this.width;
        int oldHeight = this.height;
        this.width = width;
        this.height = height;
        if(width > oldWidth) // grow horizontal
        {
            if(right) // grow right
            {
                int index = oldWidth;
                int widthIncrease = width - oldWidth;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthIncrease; k ++)
                        this.tiles.insert(index, new Tile(map, 0, 0));
                    index += widthIncrease + oldWidth;
                }
            }
            else // grow left
            {
                int index = 0;
                int widthIncrease = width - oldWidth;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthIncrease; k ++)
                        this.tiles.insert(index, new Tile(map, 0, 0));
                    index += widthIncrease + oldWidth;
                }
            }
        }
        else // shrink horizontal
        {

            if(right) // shrink right
            {
                int widthShrink = oldWidth - width;
                int index = oldWidth - widthShrink;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthShrink; k ++)
                        this.tiles.removeIndex(index);
                    index += oldWidth - widthShrink;
                }
            }
            else // shrink left
            {
                int widthShrink = oldWidth - width;
                int index = 0;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthShrink; k ++)
                        this.tiles.removeIndex(index);
                    index += oldWidth - widthShrink;
                }
            }
        }

        if(height > oldHeight) // grow horizontal
        {
            if(down) // grow down
            {
                int heightIncrease = height - oldHeight;
                for(int i = 0; i < this.width * heightIncrease; i ++)
                    this.tiles.insert(0, new Tile(map, 0, 0));
            }
            else // grow up
            {
                int heightIncrease = height - oldHeight;
                for(int i = 0; i < this.width * heightIncrease; i ++)
                    this.tiles.add(new Tile(map, 0, 0));
            }
        }
        else // shrink horizontal
        {
            if(down) // shrink down
            {
                int heightShrink = oldHeight - height;
                for(int i = 0; i < this.width * heightShrink; i ++)
                    this.tiles.removeIndex(0);
            }
            else // shrink up
            {
                int heightShrink = oldHeight - height;
                for(int i = 0; i < this.width * heightShrink; i ++)
                    this.tiles.removeIndex(this.tiles.size - 1);
            }
        }
        repositionTiles();
    }

    private void repositionTiles()
    {
        int index = 0;
        for(int y = 0; y < height; y ++)
        {
            for(int x = 0; x < width; x ++)
            {
                this.tiles.get(index).setPosition(x * 64, y * 64);
                index ++;
            }
        }
    }
}
