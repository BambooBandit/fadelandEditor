package com.fadeland.editor.map;

import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileMenu;
import com.fadeland.editor.ui.tileMenu.TileTool;

import java.util.ArrayList;

public class TileMapData
{
    public String name;
    public int mapWidth;
    public int mapHeight;
    public int tileSize;
    public ArrayList<ToolData> tileTools;
    public ArrayList<ToolData> spriteTools;
    public ArrayList<LayerData> layers;

    public TileMapData(){}
    public TileMapData(TileMap tileMap)
    {
        this.name = tileMap.name;
        this.mapWidth = tileMap.mapWidth;
        this.mapHeight = tileMap.mapHeight;
        this.tileSize = TileMenu.tileSize;
        this.tileTools = new ArrayList<>();
        for(int i = 0; i < tileMap.tileMenu.tileTable.getChildren().size; i ++)
            this.tileTools.add(new ToolData((TileTool) tileMap.tileMenu.tileTable.getChildren().get(i)));
        this.spriteTools = new ArrayList<>();
        for(int i = 0; i < tileMap.tileMenu.spriteTable.getChildren().size; i ++)
            this.spriteTools.add(new ToolData((TileTool) tileMap.tileMenu.spriteTable.getChildren().get(i)));
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
    }
}

abstract class LayerData
{
    public String name;
    public LayerData(){}
    public LayerData(Layer layer)
    {
        this.name = layer.layerField.layerName.getText();
    }
}

class TileLayerData extends LayerData
{
    public ArrayList<MapTileData> tiles;
    public TileLayerData(){}
    public TileLayerData(Layer layer)
    {
        super(layer);
        this.tiles = new ArrayList<>();
        for(int i = 0; i < layer.tiles.size; i ++)
            tiles.add(new MapTileData(layer.tiles.get(i)));
    }
}

class MapSpriteLayerData extends LayerData
{
    public ArrayList<MapSpriteData> tiles;
    public MapSpriteLayerData(){}
    public MapSpriteLayerData(Layer layer)
    {
        super(layer);
        this.tiles = new ArrayList<>();
        for(int i = 0; i < layer.tiles.size; i ++)
            tiles.add(new MapSpriteData((MapSprite) layer.tiles.get(i)));
    }
}

class MapObjectLayerData extends LayerData
{
    public ArrayList<MapObjectData> tiles;
    public MapObjectLayerData(){}
    public MapObjectLayerData(Layer layer)
    {
        super(layer);
        this.tiles = new ArrayList<>();
        for(int i = 0; i < layer.tiles.size; i ++)
        {
            if(((MapObject)layer.tiles.get(i)).isPoint)
                tiles.add(new MapPointData((MapObject) layer.tiles.get(i)));
            else
                tiles.add(new MapPolygonData((MapObject) layer.tiles.get(i)));
        }
    }
}

class PropertyData
{
    public String property;
    public String value;
    public PropertyData(){}
    public PropertyData(PropertyField propertyField)
    {
        this.property = propertyField.getProperty();
        this.value = propertyField.getValue();
    }
}

class ToolData
{
    public int id;
    public String type;
    public ArrayList<PropertyData> propertyData;
    public ArrayList<MapObjectData> attachedObjects;
    public ToolData(){}
    public ToolData(TileTool tileTool)
    {
        this.id = tileTool.id;
        this.type = tileTool.tool.type;

        this.propertyData = new ArrayList<>();
        for(int i = 0; i < tileTool.properties.size; i ++)
            propertyData.add(new PropertyData(tileTool.properties.get(i)));

        this.attachedObjects = new ArrayList<>();
        for(int i = 0; i < tileTool.mapObjects.size; i++)
        {
            AttachedMapObject attachedMapObject = tileTool.mapObjects.get(i);
            MapObjectData mapObjectData;
            if(attachedMapObject.isPoint)
                mapObjectData = new MapPointData(attachedMapObject);
            else
                mapObjectData = new MapPolygonData(attachedMapObject);
            this.attachedObjects.add(mapObjectData);
        }
    }
}

class MapTileData
{
    public int x, y, id;
    public MapTileData(){}
    public MapTileData(Tile tile)
    {
        this.x = (int) tile.position.x;
        this.y = (int) tile.position.y;
        if(tile.tool != null)
            this.id = tile.tool.id;
        else
            this.id = -1;
    }
}

class MapSpriteData
{
    public int x, y, id;
    public float width, height, rotation;
    public MapSpriteData(){}
    public MapSpriteData(MapSprite mapSprite)
    {
        this.x = (int) mapSprite.position.x;
        this.y = (int) mapSprite.position.y;
        this.id = mapSprite.tool.id;
        this.width = mapSprite.sprite.getWidth();
        this.height = mapSprite.sprite.getHeight();
        this.rotation = mapSprite.sprite.getRotation();
    }
}

abstract class MapObjectData
{
    public int x, y;
    public float xOffset, yOffset, width, height;
    public ArrayList<PropertyData> propertyData;
    public MapObjectData(){}
    public MapObjectData(MapObject mapObject)
    {
        this.x = (int) mapObject.position.x;
        this.y = (int) mapObject.position.y;
        if(mapObject instanceof AttachedMapObject)
        {
            AttachedMapObject attachedMapObject = (AttachedMapObject) mapObject;
            this.xOffset = attachedMapObject.positionOffset.x;
            this.yOffset = attachedMapObject.positionOffset.y;
            this.width = attachedMapObject.width;
            this.height = attachedMapObject.height;
        }
        this.propertyData = new ArrayList<>();
        for(int i = 0; i < mapObject.properties.size; i ++)
            propertyData.add(new PropertyData(mapObject.properties.get(i)));
    }
}
class MapPolygonData extends MapObjectData
{
    public float[] vertices;
    public MapPolygonData(){}
    public MapPolygonData(MapObject mapObject)
    {
        super(mapObject);
        this.vertices = mapObject.polygon.getVertices();
    }
}

class MapPointData extends MapObjectData
{
    public MapPointData(){}
    public MapPointData(MapObject mapObject)
    {
        super(mapObject);
    }
}
