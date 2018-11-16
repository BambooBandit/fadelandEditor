package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.Tile;

public class BringSpriteUpOrDown implements Action
{
    public Array<Tile> tiles;
    public Array<Tile> tilesOld;
    public Array<Tile> tilesNew;

    public BringSpriteUpOrDown(Array<Tile> tiles)
    {
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
        this.tiles.clear();
        this.tiles.addAll(this.tilesOld);
    }

    @Override
    public void redo()
    {
        this.tiles.clear();
        this.tiles.addAll(this.tilesNew);
    }
}
