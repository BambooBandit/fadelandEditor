package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.Utils;
import com.fadeland.editor.undoredo.PlaceTile;

import static com.fadeland.editor.map.TileMap.tileSize;

public class PossibleTileGroup
{
    public Array<TileGroup> tileGroups; // The groups that this possible group of tiles can be stamped into. If stamped, a TileGroup will be chosen at random
    public FadelandEditor editor;
    public TileLayer layer;

    public Vector2 position; // The upper left of this possible group

    public float r = .45f;
    public float g = .55f;
    public float b = .15f;

    public PossibleTileGroup(FadelandEditor editor, TileLayer layer, Vector2 position)
    {
        this.tileGroups = new Array<>();
        this.position = new Vector2();
        this.editor = editor;
        this.layer = layer;
        this.position = new Vector2(position);
    }

    public void draw()
    {
        int groupWidth = tileGroups.get(0).width;
        int right = 0;
        int down = tileGroups.get(0).height;

        for(int i = 0; i < tileGroups.get(0).types.size; i ++)
        {
            if(tileGroups.get(0).types.get(i) != null)
            {
                this.editor.shapeRenderer.setColor(r, g, b, 1);
                this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                editor.shapeRenderer.rect(position.x + (right * tileSize), position.y - tileSize + (down * tileSize), tileSize, tileSize);
                this.editor.shapeRenderer.setColor(r, g, b,.075f);
                this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                editor.shapeRenderer.rect(position.x + (right * tileSize), position.y - tileSize + (down * tileSize), tileSize, tileSize);
            }
            right ++;
            if(right >= groupWidth)
            {
                right = 0;
                down --;
            }
        }
    }

    public boolean clickedGroup(float x, float y)
    {
        int groupWidth = tileGroups.get(0).width;
        int right = 0;
        int down = tileGroups.get(0).height;

        for(int i = 0; i < tileGroups.get(0).boundGroup.size; i ++)
        {
            if(tileGroups.get(0).types.get(i) != null)
            {
                if(Utils.coordInRect(x, y, position.x + (right * tileSize), position.y - tileSize + (down * tileSize), tileSize, tileSize))
                    return true;
            }
            right ++;
            if(right >= groupWidth)
            {
                right = 0;
                down --;
            }
        }
        return false;
    }

    private TileGroup getRandomTileGroup()
    {
        int random = Utils.randomInt(0, tileGroups.size - 1);
        return tileGroups.get(random);
    }

    /** Stamps this tile group to the layer*/
    public void stamp()
    {
        TileGroup tileGroup = getRandomTileGroup();
        int groupWidth = tileGroup.width;
        int right = 0;
        int down = tileGroup.height;

        PlaceTile placeTile = new PlaceTile(layer.map);

        for(int i = 0; i < tileGroup.boundGroup.size; i ++)
        {
            if(tileGroup.types.get(i) != null)
            {
                Tile tile = layer.map.getTile(position.x + 1 + (right * tileSize), position.y + - tileSize + (down * tileSize));
                placeTile.addTile(tile, tile.tool, tileGroup.boundGroup.get(i));
                tile.setTool(tileGroup.boundGroup.get(i));
            }
            right ++;
            if(right >= groupWidth)
            {
                right = 0;
                down --;
            }
        }

        layer.map.performAction(placeTile);
    }
}
