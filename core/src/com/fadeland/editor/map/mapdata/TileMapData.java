package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.ObjectLayer;
import com.fadeland.editor.map.SpriteLayer;
import com.fadeland.editor.map.TileLayer;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.SheetTools;
import com.fadeland.editor.ui.tileMenu.TileMenu;

import java.util.ArrayList;

public class TileMapData
{
    public String name;
    public int tileSize;
    public float r, g, b, a;
    public ArrayList<SheetData> sheets;
    public ArrayList<LayerData> layers;
    public ArrayList<TileGroupData> tileGroups;

    public TileMapData(){}
    public TileMapData(TileMap tileMap)
    {
        this.name = tileMap.name;
        this.tileSize = tileMap.tileSize;
        this.r = Float.parseFloat(tileMap.propertyMenu.mapPropertyPanel.mapRGBAProperty.rValue.getText());
        this.g = Float.parseFloat(tileMap.propertyMenu.mapPropertyPanel.mapRGBAProperty.gValue.getText());
        this.b = Float.parseFloat(tileMap.propertyMenu.mapPropertyPanel.mapRGBAProperty.bValue.getText());
        this.a = Float.parseFloat(tileMap.propertyMenu.mapPropertyPanel.mapRGBAProperty.aValue.getText());
        this.sheets = new ArrayList<>(2);
        this.sheets.add(new TileSheetData(tileMap, "tiles.png", TileMenu.tileSheetWidth, TileMenu.tileSheetHeight));
        this.sheets.add(new SpriteSheetData(tileMap, SheetTools.MAP));
        this.layers = new ArrayList<>();
        for(int i = 0; i < tileMap.layers.size; i ++)
        {
            if(tileMap.layers.get(i) instanceof TileLayer)
                this.layers.add(new TileLayerData(tileMap.layers.get(i)));
            else if(tileMap.layers.get(i) instanceof SpriteLayer)
                this.layers.add(new MapSpriteLayerData(tileMap.layers.get(i)));
            else if(tileMap.layers.get(i) instanceof ObjectLayer)
                this.layers.add(new MapObjectLayerData(tileMap.layers.get(i)));
        }
        this.tileGroups = new ArrayList<>();
        for(int i = 0; i < tileMap.tileGroups.size; i ++)
            this.tileGroups.add(new TileGroupData(tileMap.tileGroups.get(i)));
    }
}

