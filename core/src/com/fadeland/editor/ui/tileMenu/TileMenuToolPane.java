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

import static com.fadeland.editor.ui.tileMenu.TileMenu.toolHeight;

public class TileMenuToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;

    private TileMenuTool lines;

    public TileMenu menu;

    public TileMenuToolPane(FadelandEditor editor, TileMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.lines = new TileMenuTool(TileMenuTools.LINES, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.lines).padRight(1);

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
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    /** Tool was clicked on. If it's a tile, see if CONTROL was being held down to handle selecting or removing multiple tiles. */
    public void selectTool(TileMenuTool selectedTool)
    {
        if(selectedTool.tool == TileMenuTools.LINES)
        {
            if (selectedTool.isSelected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
        else if(selectedTool.tool == TileMenuTools.TILE)
        {
            for(int i = 0; i < this.menu.tileTable.getChildren().size; i ++)
            {
                TileMenuTool tool = (TileMenuTool) this.menu.tileTable.getChildren().get(i);
                if(tool == selectedTool)
                {
                    if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                    {
                        if(tool.isSelected)
                        {
                            this.menu.selectedTiles.removeValue(tool, false);
                            tool.unselect();
                        }
                        else
                        {
                            this.menu.selectedTiles.add(tool);
                            tool.select();
                        }
                    }
                    else
                    {
                        this.menu.selectedTiles.clear();
                        this.menu.selectedTiles.add(tool);
                        tool.select();
                    }
                }
                else if(tool.tool == TileMenuTools.TILE)
                {
                    if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                    {
                        this.menu.selectedTiles.removeValue(tool, false);
                        tool.unselect();
                    }
                }
            }
        }
    }
}
