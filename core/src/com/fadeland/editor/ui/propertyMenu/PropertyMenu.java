package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class PropertyMenu extends Group
{
    private FadelandEditor editor;

    private Image background;

    private MapPropertyPanel mapPropertyPanel;
    private Stack propertyPanelStack; // Used to swap out tile and object property panels
    private TilePropertyPanel tilePropertyPanel;
    private PropertyPanel propertyPanel; // Custom properties
    private ObjectPropertyPanel objectPropertyPanel;
    private PropertyToolPane toolPane;

    public static int toolHeight = 35;

    private Stack stack;
    public Table propertyTable; // Holds all the properties

    public PropertyMenu(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.mapPropertyPanel = new MapPropertyPanel(skin, editor);
        this.tilePropertyPanel = new TilePropertyPanel(skin, editor);
        this.objectPropertyPanel = new ObjectPropertyPanel(skin, editor);
        this.propertyPanel = new PropertyPanel(skin, editor);
        this.propertyPanelStack = new Stack();
        this.propertyPanelStack.add(this.tilePropertyPanel);
        this.propertyPanelStack.add(this.objectPropertyPanel);
        this.toolPane = new PropertyToolPane(editor, this, skin);

        this.propertyTable = new Table();
        this.propertyTable.left().top();
        this.propertyTable.add(this.mapPropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.propertyPanelStack).padBottom(5).row();
        this.propertyTable.add(this.propertyPanel).padBottom(5).row();

        this.stack.add(this.background);
        this.stack.add(this.propertyTable);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.mapPropertyPanel.setSize(width, toolHeight);
        this.tilePropertyPanel.setSize(width, toolHeight);
        this.objectPropertyPanel.setSize(width, toolHeight);
        this.propertyPanel.setSize(width, toolHeight);
        this.propertyTable.invalidateHierarchy();
        this.toolPane.setSize(width, toolHeight);

        this.propertyPanelStack.setSize(width, this.propertyPanelStack.getMinHeight());
        this.propertyPanelStack.invalidateHierarchy();

        System.out.println(this.propertyPanel.getHeight());

        this.stack.setSize(width, height - toolHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newProperty()
    {
    }
}