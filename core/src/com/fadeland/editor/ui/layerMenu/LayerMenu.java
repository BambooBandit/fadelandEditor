package com.fadeland.editor.ui.layerMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class LayerMenu extends Group
{
    private FadelandEditor editor;

    private Image background;

    private LayerToolPane toolPane;

    public static int toolHeight = 35;

    private Stack stack;
    public Table tileTable; // Holds all the tiles

    public LayerMenu(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new LayerToolPane(editor, this, skin);

        this.tileTable = new Table();
        this.tileTable.left().top();

        this.stack.add(this.background);
//        this.stack.add(this.tilePropertyPanel);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.stack.setSize(width, height - toolHeight);
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.toolPane.setSize(width, toolHeight);

        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newLayer()
    {
    }
}