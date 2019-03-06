package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.undoredo.SelectTileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.toolHeight;

public class TileMenuToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;
    private TileMap map;

    private TileMenuTool tiles;
    private TileMenuTool sprites;
    private TileMenuTool lines;

    public TileMenu menu;

    public TileMenuToolPane(FadelandEditor editor, TileMenu menu, TileMap map, Skin skin)
    {
        this.menu = menu;
        this.map = map;
        this.toolTable = new Table();
        this.tiles = new TileMenuTool(TileMenuTools.TILESELECT, this, skin);
        this.sprites = new TileMenuTool(TileMenuTools.SPRITESELECT, this, skin);
        this.lines = new TileMenuTool(TileMenuTools.LINES, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.tiles).padRight(1);
        this.toolTable.add(this.sprites).padRight(1);
        this.toolTable.add(this.lines).padRight(1);

        this.sprites.unselect();
        this.tiles.select();
        selectMultipleTiles();

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.toolTable.getCell(this.tiles).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.sprites).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    /** Tool was clicked on. If it's a tile, see if CONTROL was being held down to handle selecting or removing multiple tiles. */
    public void selectTool(TileMenuTool selectedTool)
    {
        SelectTileTool selectTileTool = new SelectTileTool(map, this.menu.selectedTiles);
        if(selectedTool.tool == TileMenuTools.LINES)
        {
            if (selectedTool.isSelected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
        else if(selectedTool.tool == TileMenuTools.TILESELECT)
        {
            this.sprites.unselect();
            this.tiles.select();
            selectMultipleTiles();
        }
        else if(selectedTool.tool == TileMenuTools.SPRITESELECT)
        {
            this.tiles.unselect();
            this.sprites.select();
            selectMultipleTiles();
        }
        else if(selectedTool.tool == TileMenuTools.TILE)
        {
            for(int i = 0; i < this.menu.tileTable.getChildren().size; i ++)
            {
                TileTool tool = (TileTool) this.menu.tileTable.getChildren().get(i);
                if(tool == selectedTool)
                {
                    if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                    {
                        if(tool.isSelected)
                        {
                            this.menu.selectedTiles.removeValue(tool, false);
                            this.map.propertyMenu.rebuild();
                            tool.unselect();
                        }
                        else
                        {
                            this.menu.selectedTiles.add(tool);
                            this.map.propertyMenu.rebuild();
                            tool.select();
                        }
                    }
                    else
                    {
                        this.menu.selectedTiles.clear();
                        this.menu.selectedTiles.add(tool);
                        this.map.propertyMenu.rebuild();
                        tool.select();
                    }
                }
                else if(tool.tool == TileMenuTools.TILE)
                {
                    if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                    {
                        this.menu.selectedTiles.removeValue(tool, false);
                        this.map.propertyMenu.rebuild();
                        tool.unselect();
                    }
                }
            }
        }
        else if(selectedTool.tool == TileMenuTools.SPRITE)
        {
            for(int i = 0; i < this.menu.spriteTable.getChildren().size; i ++)
            {
                if(this.menu.spriteTable.getChildren().get(i) instanceof TileTool)
                {
                    TileTool tool = (TileTool) this.menu.spriteTable.getChildren().get(i);
                    if (tool == selectedTool)
                    {
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                        {
                            if (tool.isSelected)
                            {
                                this.menu.selectedTiles.removeValue(tool, false);
                                this.map.propertyMenu.rebuild();
                                tool.unselect();
                            } else
                            {
                                this.menu.selectedTiles.add(tool);
                                this.map.propertyMenu.rebuild();
                                tool.select();
                            }
                        } else
                        {
                            this.menu.selectedTiles.clear();
                            this.menu.selectedTiles.add(tool);
                            this.map.propertyMenu.rebuild();
                            tool.select();
                        }
                    } else if (tool.tool == TileMenuTools.SPRITE)
                    {
                        if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                        {
                            this.menu.selectedTiles.removeValue(tool, false);
                            this.map.propertyMenu.rebuild();
                            tool.unselect();
                        }
                    }
                }
            }
        }
        this.menu.selectedTiles.sort();
        selectTileTool.addSelectedTiles();
        map.performAction(selectTileTool);
    }

    /** Used to select all the selected tiles/sprites when switching from tiles to sprites panels*/
    private void selectMultipleTiles()
    {
        this.menu.selectedTiles.clear();
        if(this.tiles.isSelected)
        {
            for(int i = 0; i < this.menu.tileTable.getChildren().size; i ++)
            {
                if(((TileTool)this.menu.tileTable.getChildren().get(i)).isSelected)
                    this.menu.selectedTiles.add((TileTool) this.menu.tileTable.getChildren().get(i));
            }
        }
        else if(this.sprites.isSelected)
        {
            for(int i = 0; i < this.menu.spriteTable.getChildren().size; i ++)
            {
                if(this.menu.spriteTable.getChildren().get(i) instanceof TileTool)
                {
                    if (((TileTool) this.menu.spriteTable.getChildren().get(i)).isSelected)
                        this.menu.selectedTiles.add((TileTool) this.menu.spriteTable.getChildren().get(i));
                }
            }
        }
        if(this.map.propertyMenu != null)
            this.map.propertyMenu.rebuild();
    }

    /** Draws preview sprites to show how the tiles/sprites would look like if placed. */
    private void buildPreviewSprites()
    {

    }
}
