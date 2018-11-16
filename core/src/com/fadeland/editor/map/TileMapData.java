package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;

public class TileMapData
{
    public String name;
    public Array<Layer> layers;
    public Layer selectedLayer;
    public Tile selectedTile;
    public Array<MapSprite> selectedSprites;
    public Array<MapObject> selectedObjects;
    public Array<TileGroup> tileGroups;
    public int mapWidth;
    public int mapHeight;
//    public TileMenu tileMenu;
//    public PropertyMenu propertyMenu;

    public TileMapData()
    {
        this.layers = new Array<>();
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();
        this.tileGroups = new Array<>();
    }

    public void setData(TileMap tileMap)
    {
        this.name = tileMap.name;
//        this.layers = tileMap.layers;
        this.layers.clear();
        this.layers.addAll(tileMap.layers);
        this.selectedLayer = tileMap.selectedLayer;
//        this.tileMenu = tileMap.tileMenu;
//        this.propertyMenu = tileMap.propertyMenu;
        this.selectedTile = tileMap.selectedTile;
        this.selectedSprites.clear();
        this.selectedSprites.addAll(tileMap.selectedSprites);
        this.selectedObjects.clear();
        this.selectedObjects.addAll(tileMap.selectedObjects);
        this.tileGroups.clear();
        this.tileGroups.addAll(tileMap.tileGroups);
        this.mapWidth = tileMap.mapWidth;
        this.mapHeight = tileMap.mapHeight;
    }

    public void setMapToOtherMap(TileMap tileMap)
    {
        tileMap.name = this.name;
//        tileMap.layers = this.layers;
        tileMap.layers.clear();
        tileMap.layers.addAll(this.layers);
        tileMap.selectedLayer = this.selectedLayer;
//        tileMap.tileMenu = this.tileMenu;
//        tileMap.propertyMenu = this.propertyMenu;
        tileMap.selectedTile = this.selectedTile;
        tileMap.selectedSprites.clear();
        tileMap.selectedSprites.addAll(this.selectedSprites);
        tileMap.selectedObjects.clear();
        tileMap.selectedObjects.addAll(this.selectedObjects);
        tileMap.tileGroups.clear();
        tileMap.tileGroups.addAll(this.tileGroups);
        tileMap.mapWidth = this.mapWidth;
        tileMap.mapHeight = this.mapHeight;
        tileMap.findAllTilesToBeGrouped();
    }
}
