package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;

public class PropertyMenu extends Group
{
    private FadelandEditor editor;

    public TileMap map;

    private Image background;

    private MapPropertyPanel mapPropertyPanel;
    private Stack propertyPanelStack; // Used to swap out tile and sprite property panels
    private TilePropertyPanel tilePropertyPanel;
    private PropertyPanel propertyPanel; // Custom properties
    private SpritePropertyPanel spritePropertyPanel;
    private PropertyToolPane toolPane;

    public static int toolHeight = 35;

    private Stack stack;
    public Table propertyTable; // Holds all the properties

    public PropertyMenu(Skin skin, FadelandEditor fadelandEditor, TileMap map)
    {
        this.editor = fadelandEditor;
        this.map = map;

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.mapPropertyPanel = new MapPropertyPanel(skin, this, editor);
        this.tilePropertyPanel = new TilePropertyPanel(skin, this, editor);
        this.spritePropertyPanel = new SpritePropertyPanel(skin, this, editor);
        this.propertyPanel = new PropertyPanel(skin, this, editor, map);
        this.propertyPanelStack = new Stack();
        this.propertyPanelStack.add(this.tilePropertyPanel);
        this.propertyPanelStack.add(this.spritePropertyPanel);
        this.spritePropertyPanel.setVisible(false);
        this.toolPane = new PropertyToolPane(editor, this, skin);

        this.propertyTable = new Table();
        this.propertyTable.left().bottom();
        this.propertyTable.add(this.mapPropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.propertyPanelStack).padBottom(5).row();
        this.propertyTable.add(this.propertyPanel);

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
        this.spritePropertyPanel.setSize(width, toolHeight);
        float propertyPanelStackHeight = 0;
        if(this.tilePropertyPanel.isVisible())
            propertyPanelStackHeight = this.tilePropertyPanel.getHeight();
        else if(this.spritePropertyPanel.isVisible())
            propertyPanelStackHeight = this.spritePropertyPanel.getHeight();
        else
        {
            this.tilePropertyPanel.setSize(width, 0);
            this.spritePropertyPanel.setSize(width, 0);
        }
        this.propertyPanel.setSize(width, height - toolHeight - this.mapPropertyPanel.getHeight() - 5 - 5 - propertyPanelStackHeight);
        this.propertyPanel.setPosition(0, toolHeight);
        this.propertyTable.invalidateHierarchy();
        this.toolPane.setSize(width, toolHeight);

        this.propertyPanelStack.setSize(width, this.propertyPanelStack.getMinHeight());
        this.propertyPanelStack.invalidateHierarchy();

        this.stack.setSize(width, height - toolHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newProperty()
    {
        if(map.tileMenu.selectedTiles.size > 0)
        {
            this.propertyPanel.newProperty();

            rebuild();
        }
    }

    public void removeProperty(String propertyName)
    {
        this.propertyPanel.removeProperty(propertyName);
        rebuild();
        this.propertyTable.invalidateHierarchy();
    }

    public void removeProperty(PropertyField propertyField)
    {
        this.propertyPanel.removeProperty(propertyField);
        rebuild();
        this.propertyTable.invalidateHierarchy();
    }

    /** Upon selecting a new tile tool, rebuild property menu to only show the properties of that tile.
     * If multiple tiles are selected, only show the common properties. A common property has the same property and value. */
    public void rebuild()
    {
        this.propertyPanel.rebuild();
        this.propertyTable.invalidateHierarchy();
    }
}