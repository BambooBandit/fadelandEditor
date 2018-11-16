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

    public static Array<Layer> copyLayers(Array<Layer> toBeCopied)
    {
        Array<Layer> layers = new Array<>();
        for(int i = 0; i < toBeCopied.size; i ++)
        {
            Layer newLayer = null;
            FadelandEditor editor = toBeCopied.first().editor;
            TileMap map = toBeCopied.first().map;
            LayerField layerField = toBeCopied.first().layerField;
            if(toBeCopied.first() instanceof TileLayer)
                newLayer = new TileLayer(editor, map, layerField.mapLayer.type, layerField);
            else if(toBeCopied.first() instanceof SpriteLayer)
                newLayer = new SpriteLayer(editor, map, layerField.mapLayer.type, layerField);
            else if(toBeCopied.first() instanceof ObjectLayer)
                newLayer = new ObjectLayer(editor, map, layerField.mapLayer.type, layerField);

            newLayer.tiles = Tile.copyTiles(toBeCopied.get(i).tiles);
            System.out.println(newLayer.tiles.size);
            layers.add(newLayer);
        }
        return layers;
    }
}
