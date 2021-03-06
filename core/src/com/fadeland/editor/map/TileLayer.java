package com.fadeland.editor.map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerField;
import com.fadeland.editor.ui.layerMenu.LayerTypes;

import static com.fadeland.editor.map.TileMap.tileSize;

public class TileLayer extends Layer
{
    public Array<PossibleTileGroup> possibleTileGroups;

    public TileLayer(FadelandEditor editor, TileMap map, LayerTypes type, LayerField layerField)
    {
        super(editor, map, type, layerField);

        this.possibleTileGroups = new Array<>();

        for(int y = 0; y < height; y ++)
        {
            for(int x = 0; x < width; x ++)
                this.tiles.add(new Tile(map, this, x * 64, y * 64));
        }
    }

    @Override
    public void draw()
    {
        setCameraZoomToThisLayer();

        for(int i = 0; i < tiles.size; i ++)
            this.tiles.get(i).draw();
        for(int i = 0; i < tiles.size; i ++)
        {
            if(!(tiles.get(i) instanceof MapSprite))
                this.tiles.get(i).drawTopSprites();
        }
        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && editor.getFileTool() != null && editor.getTileTools() != null && (editor.getFileTool().tool == Tools.BRUSH || editor.getFileTool().tool == Tools.BIND))
        {
            for (int i = 0; i < editor.getTileTools().size; i ++)
            {
                editor.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                editor.getTileTools().get(i).previewSprites.get(0).setAlpha(.25f);
                editor.getTileTools().get(i).previewSprites.get(0).draw(editor.batch);
                editor.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        }

        setCameraZoomToSelectedLayer();
    }

    @Override
    public void resize(int width, int height, boolean down, boolean right)
    {
        int oldWidth = this.width;
        int oldHeight = this.height;
        this.width = width;
        this.height = height;
        if(width > oldWidth) // grow horizontal
        {
            if(right) // grow right
            {
                int index = oldWidth;
                int widthIncrease = width - oldWidth;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthIncrease; k ++)
                        this.tiles.insert(index, new Tile(map, this, 0, 0));
                    index += widthIncrease + oldWidth;
                }
            }
            else // grow left
            {
                int index = 0;
                int widthIncrease = width - oldWidth;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthIncrease; k ++)
                        this.tiles.insert(index, new Tile(map, this, 0, 0));
                    index += widthIncrease + oldWidth;
                }
            }
        }
        else // shrink horizontal
        {

            if(right) // shrink right
            {
                int widthShrink = oldWidth - width;
                int index = oldWidth - widthShrink;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthShrink; k ++)
                        this.tiles.removeIndex(index);
                    index += oldWidth - widthShrink;
                }
            }
            else // shrink left
            {
                int widthShrink = oldWidth - width;
                int index = 0;
                for(int i = 0; i < oldHeight; i ++)
                {
                    for(int k = 0; k < widthShrink; k ++)
                        this.tiles.removeIndex(index);
                    index += oldWidth - widthShrink;
                }
            }
        }

        if(height > oldHeight) // grow vertical
        {
            if(down) // grow down
            {
                int heightIncrease = height - oldHeight;
                for(int i = 0; i < this.width * heightIncrease; i ++)
                    this.tiles.insert(0, new Tile(map, this, 0, 0));
            }
            else // grow up
            {
                int heightIncrease = height - oldHeight;
                for(int i = 0; i < this.width * heightIncrease; i ++)
                    this.tiles.add(new Tile(map, this, 0, 0));
            }
        }
        else // shrink vertical
        {
            if(down) // shrink down
            {
                int heightShrink = oldHeight - height;
                for(int i = 0; i < this.width * heightShrink; i ++)
                    this.tiles.removeIndex(0);
            }
            else // shrink up
            {
                int heightShrink = oldHeight - height;
                for(int i = 0; i < this.width * heightShrink; i ++)
                    this.tiles.removeIndex(this.tiles.size - 1);
            }
        }
        repositionTiles();
        super.resize();
    }

    private void repositionTiles()
    {
        int index = 0;
        for(int y = 0; y < height; y ++)
        {
            for(int x = 0; x < width; x ++)
            {
                this.tiles.get(index).setPosition(x * 64, y * 64);
                index ++;
            }
        }
    }

    public void drawPossibleTileGroups()
    {
        setCameraZoomToThisLayer();

        for(int i = 0; i < possibleTileGroups.size; i ++)
            possibleTileGroups.get(i).draw();

        setCameraZoomToSelectedLayer();
    }

    public void drawBlocked()
    {
        setCameraZoomToThisLayer();

        float r = .85f;
        float g = .25f;
        float b = .25f;
        for(int i = 0; i < tiles.size; i ++)
        {
            Tile tile = tiles.get(i);
            if(tile.hasBlockedObjectOnTop || (tile.tool != null && tile.tool.getPropertyField("blocked") != null))
            {
                this.editor.shapeRenderer.setColor(r, g, b, 1);
                this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                editor.shapeRenderer.rect(tile.position.x, tile.position.y, tileSize, tileSize);
                this.editor.shapeRenderer.setColor(r, g, b,.075f);
                this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                editor.shapeRenderer.rect(tile.position.x, tile.position.y, tileSize, tileSize);
            }
        }

        setCameraZoomToSelectedLayer();
    }

    // All the below methods are for grouped tiles

    public void findAllTilesToBeGrouped()
    {
        this.possibleTileGroups.clear();

        for(int i = 0; i < tiles.size; i ++)
        {
            for(int k = 0; k < map.tileGroups.size; k ++)
            {
                if(doesThisPartOfTheMapMatchWithThisGroup(i, map.tileGroups.get(k)))
                    addToPossibleTileGroups(tiles.get(i), map.tileGroups.get(k));
            }
        }
    }

    private boolean doesThisPartOfTheMapMatchWithThisGroup(int index, TileGroup group)
    {
        int groupWidth = group.width;
        int right = 0;
        int down = group.height - 1;
        for(int i = 0; i < group.types.size; i ++)
        {
            Tile tile = getTileFrom(index, right, down);
            if(tile == null)
                return false;
            if(tile.tool == null || group.types.get(i) == null){}
            else if(tile.tool != null && group.types.get(i) != null && group.types.get(i).equals(tile.tool.getPropertyField("Type").value.getText())){}
            else
                return false;
            right ++;
            if(right >= groupWidth)
            {
                right = 0;
                down --;
            }
        }
        return true;
    }

    private Tile getTileFrom(int index, int amountRight, int amountDown)
    {
        int fromIndex = index;
        int rowOld = (int) Math.floor(fromIndex / width);
        fromIndex += amountRight;
        int rowNew = (int) Math.floor(fromIndex / width);
        if(rowNew != rowOld)
            return null;
        fromIndex += amountDown * width;
        if(fromIndex >= tiles.size)
            return null;
        return tiles.get(fromIndex);
    }

    private void addToPossibleTileGroups(Tile tile, TileGroup group)
    {
        for(int i = 0; i < possibleTileGroups.size; i ++)
        {
            if(possibleTileGroups.get(i).position.x == tile.position.x && possibleTileGroups.get(i).position.y == tile.position.y)
            {
                possibleTileGroups.get(i).tileGroups.add(group);
                return;
            }
        }
        PossibleTileGroup possibleTileGroup = new PossibleTileGroup(editor, this, tile.position);
        possibleTileGroups.add(possibleTileGroup);
        possibleTileGroups.get(possibleTileGroups.size - 1).tileGroups.add(group);
    }
}
