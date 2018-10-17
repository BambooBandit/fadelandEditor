package com.fadeland.editor.ui.TileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.ui.FileMenu.Tool;

import static com.fadeland.editor.ui.TileMenu.TileMenu.toolHeight;

public class TileMenuToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;

    private TileMenuTool lines;
    private TileMenuTool selectedTool;

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

    public void selectTool(TileMenuTool selectedTool)
    {
        this.selectedTool = selectedTool;
        if(selectedTool.isSelected)
            selectedTool.unselect();
        else
            selectedTool.select();
    }
}
