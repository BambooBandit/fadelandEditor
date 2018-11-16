package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class Tile
{
    protected TileMap map;
    protected Sprite sprite;
    protected float width, height;
    public Vector2 position;
    public TileTool tool;
    public boolean hasBeenPainted = false;

    // For Tiles
    public Tile(TileMap map, float x, float y)
    {
        this.map = map;
        this.position = new Vector2(x, y);
        this.width = tileSize;
        this.height = tileSize;
    }

    // For MapSprites
    public Tile(TileMap map, TileTool tool, float x, float y)
    {
        this.map = map;
        this.sprite = new Sprite(tool.textureRegion);
        this.position = new Vector2(x, y);
        this.width = tileSize;
        this.height = tileSize;
        this.tool = tool;
    }

    // For MapObject
    public Tile(TileMap map, FloatArray vertices, float x, float y)
    {
        this.map = map;
        this.position = new Vector2(x, y);
    }

    public void setTool(TileTool tool)
    {
        if(tool == null)
            this.sprite = null;
        else
            this.sprite = new Sprite(tool.textureRegion);
        this.tool = tool;
    }

    public void draw()
    {
        if(this.sprite != null)
            map.editor.batch.draw(sprite, position.x, position.y, width, height);
    }

    public void setPosition(float x, float y)
    {
        this.position.set(x, y);
        if(this.sprite != null)
            this.sprite.setPosition(x, y);
    }

    public void addMapObject(AttachedMapObject mapObject)
    {
        this.tool.mapObjects.add(mapObject);
    }

    public static Array<Tile> copyTiles(Array<Tile> toBeCopied)
    {
        Array<Tile> tiles = new Array<>();
        for(int i = 0; i < toBeCopied.size; i ++)
        {
            Tile newTile = null;
            TileMap map = toBeCopied.first().map;
            if(toBeCopied.first() instanceof MapSprite)
                newTile = new MapSprite(map, toBeCopied.get(i).tool, toBeCopied.get(i).position.x, toBeCopied.get(i).position.y);
            else if(toBeCopied.first() instanceof MapObject)
                newTile = new MapObject(map, ((MapObject)toBeCopied.get(i)).vertices, toBeCopied.get(i).position.x, toBeCopied.get(i).position.y);
            else
                newTile = new Tile(map, toBeCopied.get(i).position.x, toBeCopied.get(i).position.y);

            tiles.add(newTile);
        }
        return tiles;
    }
}
