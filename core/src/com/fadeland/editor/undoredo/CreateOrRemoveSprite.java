package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.propertyMenu.PropertyToolPane;

public class CreateOrRemoveSprite extends PerformableAction
{
    public Array<Tile> mapSprites;
    public Array<Tile> oldSprites;
    public Array<Tile> newSprites;
    public Array<MapSprite> selection;
    public Array<MapSprite> oldSelection;
    public Array<MapSprite> newSelection;

    public CreateOrRemoveSprite(TileMap map, Array<Tile> mapSprites, Array<MapSprite> selection)
    {
        super(map);
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
        super.undo();
        this.mapSprites.clear();
        this.mapSprites.addAll(oldSprites);
        if(this.selection != null)
        {
            this.selection.clear();
            this.selection.addAll(oldSelection);
        }
        this.map.propertyMenu.rebuild();
        PropertyToolPane.updateLightsAndBlocked(map);
    }

    @Override
    public void redo()
    {
        super.redo();
        this.mapSprites.clear();
        this.mapSprites.addAll(newSprites);
        if(this.selection != null)
        {
            this.selection.clear();
            this.selection.addAll(newSelection);
        }
        this.map.propertyMenu.rebuild();
        PropertyToolPane.updateLightsAndBlocked(map);
    }
}
