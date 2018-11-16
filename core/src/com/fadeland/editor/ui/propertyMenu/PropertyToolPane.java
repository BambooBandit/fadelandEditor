package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

import static com.fadeland.editor.ui.tileMenu.TileMenu.toolHeight;

public class PropertyToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;

    private PropertyTool newProperty;

    public PropertyMenu menu;

    public PropertyToolPane(FadelandEditor editor, PropertyMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newProperty = new PropertyTool(PropertyTools.NEW, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.newProperty).padRight(1);

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
        this.toolTable.getCell(this.newProperty).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }
}
