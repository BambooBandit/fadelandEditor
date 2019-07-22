package com.fadeland.editor.map.mapdata;

import com.fadeland.editor.map.ObjectLayer;
import com.fadeland.editor.map.SpriteLayer;
import com.fadeland.editor.map.TileLayer;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.SheetTools;

import java.util.ArrayList;

public class TileMapData
{
    public String name;
    public int tileSize;
    public int tilePadSize;
    public ArrayList<SheetData> sheets;
    public ArrayList<LayerData> layers;
    public ArrayList<TileGroupData> tileGroups;
    public ArrayList<PropertyData> mapLockedProperties;
    public ArrayList<PropertyData> mapProperties;

    public TileMapData(){}
    public TileMapData(TileMap tileMap, boolean settingFLMDefaults)
    {
        this.name = tileMap.name;
        this.tileSize = tileMap.tileSize;
        this.tilePadSize = tileMap.tilePadSize;
        this.mapLockedProperties = new ArrayList<>();
        this.mapProperties = new ArrayList<>();
        for(int i = 0; i < tileMap.propertyMenu.mapPropertyPanel.lockedProperties.size; i ++)
        {
            PropertyField property = tileMap.propertyMenu.mapPropertyPanel.lockedProperties.get(i);
            if(property.rgba)
                mapLockedProperties.add(new ColorPropertyData(property));
            else if(property.rgbaDistanceRayAmount)
                mapLockedProperties.add(new LightPropertyData(property));
            else
                mapLockedProperties.add(new NonColorPropertyData(property));
        }
        for(int i = 0; i < tileMap.propertyMenu.mapPropertyPanel.properties.size; i ++)
        {
            PropertyField property = tileMap.propertyMenu.mapPropertyPanel.properties.get(i);
            if(property.rgba)
                mapProperties.add(new ColorPropertyData(property));
            else if(property.rgbaDistanceRayAmount)
                mapProperties.add(new LightPropertyData(property));
            else
                mapProperties.add(new NonColorPropertyData(property));
        }
        this.sheets = new ArrayList<>(4);

        boolean map = false;
        boolean tiles = false;
        boolean flatMap = false;
        boolean canyonMap = false;
        boolean canyonBackdrop = false;
        boolean desertTiles = false;
        boolean canyonTiles = false;
        boolean mesaMap = false;

        this.layers = new ArrayList<>();
        for(int i = 0; i < tileMap.layers.size; i ++)
        {
            if(tileMap.layers.get(i) instanceof TileLayer)
                this.layers.add(new TileLayerData(tileMap.layers.get(i)));
            else if(tileMap.layers.get(i) instanceof SpriteLayer)
                this.layers.add(new MapSpriteLayerData(tileMap.layers.get(i)));
            else if(tileMap.layers.get(i) instanceof ObjectLayer)
                this.layers.add(new MapObjectLayerData(tileMap.layers.get(i)));

            for(int k = 0; k < tileMap.layers.get(i).tiles.size; k++)
            {
                if(tileMap.layers.get(i).tiles.get(k).tool == null)
                    continue;
                if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.MAP)
                    map = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.TILES)
                    tiles = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.FLATMAP)
                    flatMap = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.CANYONMAP)
                    canyonMap = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.CANYONBACKDROP)
                    canyonBackdrop = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.DESERTTILES)
                    desertTiles = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.CANYONTILES)
                    canyonTiles = true;
                else if(tileMap.layers.get(i).tiles.get(k).tool.sheetTool == SheetTools.MESAMAP)
                    mesaMap = true;
            }
        }
        if(map)
            this.sheets.add(new SpriteSheetData(tileMap, SheetTools.MAP));
        if(tiles)
            this.sheets.add(new TileSheetData(tileMap, SheetTools.TILES, SheetTools.TILES.tileSheetWidth, SheetTools.TILES.tileSheetHeight));
        if(flatMap)
            this.sheets.add(new SpriteSheetData(tileMap, SheetTools.FLATMAP));
        if(canyonMap)
            this.sheets.add(new SpriteSheetData(tileMap, SheetTools.CANYONMAP));
        if(canyonBackdrop)
            this.sheets.add(new SpriteSheetData(tileMap, SheetTools.CANYONBACKDROP));
        if(desertTiles)
            this.sheets.add(new TileSheetData(tileMap, SheetTools.DESERTTILES, SheetTools.DESERTTILES.tileSheetWidth, SheetTools.DESERTTILES.tileSheetHeight));
        if(canyonTiles)
            this.sheets.add(new TileSheetData(tileMap, SheetTools.CANYONTILES, SheetTools.CANYONTILES.tileSheetWidth, SheetTools.CANYONTILES.tileSheetHeight));
        if(mesaMap)
            this.sheets.add(new SpriteSheetData(tileMap, SheetTools.MESAMAP));

        this.tileGroups = new ArrayList<>();
        for(int i = 0; i < tileMap.tileGroups.size; i ++)
            this.tileGroups.add(new TileGroupData(tileMap.tileGroups.get(i)));


        // Remove all the map data such as layers and tiles since they are not default information
        if(settingFLMDefaults)
        {
            this.name = "defaultFLM.flm";
            this.layers.clear();
        }
    }
}

