package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;
import com.fadeland.editor.ui.layerMenu.LayerTypes;

import static com.fadeland.editor.map.TileMap.tileSize;

public abstract class Layer
{
    public Array<Tile> tiles;

    protected int width, height;

    protected FadelandEditor editor;
    public TileMap map;
    public LayerField layerField;
    public LayerTypes type;

    public Layer(FadelandEditor editor, TileMap map, LayerTypes type, LayerField layerField)
    {
        this.width = map.mapWidth;
        this.height = map.mapHeight;
        this.tiles = new Array<>();
        this.editor = editor;
        this.map = map;
        this.type = type;
        this.layerField = layerField;
    }

    public abstract void draw();

    public void drawAttachedMapObjects()
    {
        for(int i = 0; i < this.tiles.size; i ++)
        {
            if(this.tiles.get(i).tool != null)
            {
                for (int k = 0; k < this.tiles.get(i).tool.mapObjects.size; k++)
                {
                    AttachedMapObject mapObject = this.tiles.get(i).tool.mapObjects.get(k);
                    mapObject.attachedTile = this.tiles.get(i);
                    mapObject.setPosition(this.tiles.get(i).position.x + mapObject.positionOffset.x, this.tiles.get(i).position.y + mapObject.positionOffset.y);
                    mapObject.draw();
                }
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
            if(!right) // grow left
            {
                float widthIncrease = (width - oldWidth) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x + widthIncrease, tiles.get(i).position.y);
            }
        }
        else // shrink horizontal
        {
            if(!right) // shrink left
            {
                float widthShrink = (oldWidth - width) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x - widthShrink, tiles.get(i).position.y);
            }
        }

        if(height > oldHeight) // grow vertical
        {
            if(down) // grow down
            {
                float heightIncrease = (height - oldHeight) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x, tiles.get(i).position.y + heightIncrease);
            }
        }
        else // shrink vertical
        {
            if(down) // shrink down
            {
                float heightShrink = (oldHeight - height) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x, tiles.get(i).position.y - heightShrink);
            }
        }
    }
}
