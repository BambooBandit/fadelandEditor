package com.fadeland.editor.map;

import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

import java.util.ArrayList;

public class TileMapData
{
    public String name;
    public int tileSize;
    public float brightness;
    public ArrayList<ToolData> tileTools;
    public ArrayList<ToolData> spriteTools;
    public ArrayList<LayerData> layers;
    public ArrayList<TileGroupData> tileGroups;

    public TileMapData(){}
    public TileMapData(TileMap tileMap)
    {
        this.name = tileMap.name;
        this.tileSize = tileMap.tileSize;
        this.brightness = Float.parseFloat(tileMap.propertyMenu.mapPropertyPanel.mapBrightnessProperty.value.getText());
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
        this.tileGroups = new ArrayList<>();
        for(int i = 0; i < tileMap.tileGroups.size; i ++)
            this.tileGroups.add(new TileGroupData(tileMap.tileGroups.get(i)));
    }
}

class TileGroupData
{
    public ArrayList<Integer> boundGroupIds;
    public ArrayList<String> types; // 2D array of tile types that have a group bound to it
    public int width; // Determines how long the rows of the boundGroup array is.
    public int height; // Determines how long the columns of the boundGroup array is.
    public TileGroupData(){}
    public TileGroupData(TileGroup tileGroup)
    {
        this.boundGroupIds = new ArrayList<>(tileGroup.boundGroup.size);
        for(int i = 0; i < tileGroup.boundGroup.size; i++)
        {
            if(tileGroup.boundGroup.get(i) == null)
                this.boundGroupIds.add(null);
            else
                this.boundGroupIds.add(tileGroup.boundGroup.get(i).id);
        }
        this.types = new ArrayList<>(tileGroup.types.size);
        for(int i = 0; i < tileGroup.types.size; i++)
            this.types.add(tileGroup.types.get(i));
        this.width = tileGroup.width;
        this.height = tileGroup.height;
    }
}

abstract class LayerData
{
    public String name;
    public int width, height;
    public float x, y, z;
    public LayerData(){}
    public LayerData(Layer layer)
    {
        this.name = layer.layerField.layerName.getText();
        this.width = layer.width;
        this.height = layer.height;
        this.x = layer.x;
        this.y = layer.y;
        this.z = layer.z;
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
    public ArrayList<PropertyData> lockedPropertyData;
    public ArrayList<MapObjectData> attachedObjects;
    public ToolData(){}
    public ToolData(TileTool tileTool)
    {
        this.id = tileTool.id;
        this.type = tileTool.tool.type;

        this.propertyData = new ArrayList<>();
        for(int i = 0; i < tileTool.properties.size; i ++)
            propertyData.add(new PropertyData(tileTool.properties.get(i)));

        this.lockedPropertyData = new ArrayList<>();
        for(int i = 0; i < tileTool.lockedProperties.size; i ++)
            lockedPropertyData.add(new PropertyData(tileTool.lockedProperties.get(i)));

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
    public float x, y;
    public int id;
    public boolean blockedByObject;
    public MapTileData(){}
    public MapTileData(Tile tile)
    {
        this.x = tile.position.x;
        this.y = tile.position.y;
        this.blockedByObject = tile.hasBlockedObjectOnTop;
        if(tile.tool != null)
            this.id = tile.tool.id;
        else
            this.id = -1;
    }
}

class MapSpriteData
{
    public float x, y, z;
    int id;
    public float width, height, rotation, scale;
    public MapSpriteData(){}
    public MapSpriteData(MapSprite mapSprite)
    {
        this.x = mapSprite.position.x;
        this.y = mapSprite.position.y;
        this.z = mapSprite.z;
        this.id = mapSprite.tool.id;
        this.width = mapSprite.sprite.getWidth();
        this.height = mapSprite.sprite.getHeight();
        this.rotation = mapSprite.sprite.getRotation();
        this.scale = mapSprite.sprite.getScaleX();
    }
}

abstract class MapObjectData
{
    public float x, y;
    public float xOffset, yOffset, width, height;
    public ArrayList<PropertyData> propertyData;
    public MapObjectData(){}
    public MapObjectData(MapObject mapObject)
    {
        this.x = mapObject.position.x;
        this.y = mapObject.position.y;
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
