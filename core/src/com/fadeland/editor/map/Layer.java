package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;
import com.fadeland.editor.ui.layerMenu.LayerTypes;

public abstract class Layer
{
    public Array<Tile> tiles;

    protected FadelandEditor editor;
    public TileMap map;
    public LayerField layerField;
    public LayerTypes type;

    public Layer(FadelandEditor editor, TileMap map, LayerTypes type, LayerField layerField)
    {
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
                    mapObject.setPosition(this.tiles.get(i).position.x + mapObject.positionOffset.x, this.tiles.get(i).position.y + mapObject.positionOffset.y);
                    mapObject.draw();
                }
            }
        }
    }
}
