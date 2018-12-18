package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class BringSpriteUpOrDown extends PerformableAction
{
    public Array<Tile> tiles;
    public Array<Tile> tilesOld;
    public Array<Tile> tilesNew;

    public BringSpriteUpOrDown(TileMap map, Array<Tile> tiles)
    {
        super(map);
        this.tiles = tiles;
        this.tilesOld = new Array<>(tiles);
    }

    public void addNew()
    {
        this.tilesNew = new Array<>(tiles);
    }

    @Override
    public void undo()
    {
        super.undo();
        this.tiles.clear();
        this.tiles.addAll(this.tilesOld);
    }

    @Override
    public void redo()
    {
        super.redo();
        this.tiles.clear();
        this.tiles.addAll(this.tilesNew);
    }
}
