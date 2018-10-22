package com.fadeland.editor.ui.layerMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

import static com.fadeland.editor.ui.tileMenu.TileMenu.toolHeight;

public class LayerToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;

    private LayerTool newTileLayer;
    private LayerTool newObjectLayer;

    public LayerMenu menu;

    public LayerToolPane(FadelandEditor editor, LayerMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newTileLayer = new LayerTool(LayerTools.NEWTILE, this, skin);
        this.newObjectLayer = new LayerTool(LayerTools.NEWOBJECT, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.newTileLayer).padRight(1);
        this.toolTable.add(this.newObjectLayer).padRight(1);

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
        this.toolTable.getCell(this.newTileLayer).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.newObjectLayer).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }
}
