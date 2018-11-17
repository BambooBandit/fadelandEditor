package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class CreateOrRemoveSprite implements Action
{
    public TileMap map;
    public Array<Tile> mapSprites;
    public Array<Tile> oldSprites;
    public Array<Tile> newSprites;
    public Array<MapSprite> selection;
    public Array<MapSprite> oldSelection;
    public Array<MapSprite> newSelection;

    public CreateOrRemoveSprite(TileMap map, Array<Tile> mapSprites, Array<MapSprite> selection)
    {
        this.map = map;
        this.mapSprites = mapSprites;
        this.selection = selection;
        this.oldSprites = new Array<>(mapSprites);
        if(selection != null)
            this.oldSelection = new Array<>(selection);
    }

    public void addSprites()
    {
        this.newSprites = new Array<>(mapSprites);
        if(this.selection != null)
            this.newSelection = new Array<>(selection);
    }

    @Override
    public void undo()
    {
        this.mapSprites.clear();
        this.mapSprites.addAll(oldSprites);
        if(this.selection != null)
        {
            this.selection.clear();
            this.selection.addAll(oldSelection);
        }
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        this.mapSprites.clear();
        this.mapSprites.addAll(newSprites);
        if(this.selection != null)
        {
            this.selection.clear();
            this.selection.addAll(newSelection);
        }
        this.map.propertyMenu.rebuild();
    }
}
