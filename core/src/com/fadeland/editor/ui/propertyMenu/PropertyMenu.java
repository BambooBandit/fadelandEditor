package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileMenuTools;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class PropertyMenu extends Group
{
    private FadelandEditor editor;

    public TileMap map;

    private Image background;

    public MapPropertyPanel mapPropertyPanel;
    public LayerPropertyPanel layerPropertyPanel;
    public RemoveablePropertyPanel tilePropertyPanel;
    public RemoveablePropertyPanel spritePropertyPanel;
    private PropertyPanel propertyPanel; // Custom properties
    private PropertyToolPane toolPane;

    public static int toolHeight = 35;

    private Stack stack;
    public Table propertyTable; // Holds all the properties

    private Skin skin;

    public PropertyMenu(Skin skin, FadelandEditor fadelandEditor, TileMap map)
    {
        this.editor = fadelandEditor;
        this.map = map;
        this.skin = skin;

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.mapPropertyPanel = new MapPropertyPanel(skin, this, editor);
        this.mapPropertyPanel.removeablePropertyPanel.setVisible(false);
        this.layerPropertyPanel = new LayerPropertyPanel(skin, this, editor);
        this.layerPropertyPanel.setVisible(false);
        this.tilePropertyPanel = new RemoveablePropertyPanel(skin, this, editor);
        this.spritePropertyPanel = new RemoveablePropertyPanel(skin, this, editor);
        this.spritePropertyPanel.setVisible(false);
        this.propertyPanel = new PropertyPanel(skin, this, editor, map);
        this.toolPane = new PropertyToolPane(editor, this, skin);

        this.propertyTable = new Table();
        this.propertyTable.left().bottom();
        this.propertyTable.add(this.mapPropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.layerPropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.spritePropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.tilePropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.mapPropertyPanel.removeablePropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.propertyPanel);

        setTileProperties();
        setSpriteProperties();

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
        this.layerPropertyPanel.setSize(width, toolHeight);
        this.spritePropertyPanel.setSize(width, toolHeight);
        this.mapPropertyPanel.removeablePropertyPanel.setSize(width, toolHeight);
        float propertyPanelStackHeight = mapPropertyPanel.getHeight();

        if(this.layerPropertyPanel.isVisible())
            propertyPanelStackHeight += this.layerPropertyPanel.getHeight();
        else
            this.layerPropertyPanel.setSize(width, 0);

        if(this.tilePropertyPanel.isVisible())
            propertyPanelStackHeight += this.tilePropertyPanel.getHeight();
        else
            this.tilePropertyPanel.setSize(width, 0);

        if(this.spritePropertyPanel.isVisible())
            propertyPanelStackHeight += this.spritePropertyPanel.getHeight();
        else
            this.spritePropertyPanel.setSize(width, 0);

        if(this.mapPropertyPanel.removeablePropertyPanel.isVisible())
            propertyPanelStackHeight += this.mapPropertyPanel.removeablePropertyPanel.getHeight();
        else
            this.mapPropertyPanel.removeablePropertyPanel.setSize(width, 0);

        this.propertyPanel.setSize(width, height - toolHeight - 5 - 5 - 5 - propertyPanelStackHeight);
        this.propertyPanel.setPosition(0, toolHeight);
        this.propertyTable.invalidateHierarchy();
        this.toolPane.setSize(width, toolHeight);

//        this.propertyPanelStack.setSize(width, this.propertyPanelStack.getMinHeight());
//        this.propertyPanelStack.invalidateHierarchy();

        this.stack.setSize(width, height - toolHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newProperty(boolean light)
    {
        this.propertyPanel.newProperty(light);
        rebuild();
    }

    public void newProperty(String property, String value)
    {
        this.propertyPanel.newProperty(property, value);
        rebuild();
    }

    public void newProperty(float r, float g, float b, float a)
    {
        this.propertyPanel.newProperty(r, g, b, a);
        rebuild();
    }

    public void newProperty(float r, float g, float b, float a, float distance, int rayAmount)
    {
        this.propertyPanel.newProperty(r, g, b, a, distance, rayAmount);
        rebuild();
    }

    private void setTileProperties()
    {
        for(int i = 0; i < map.tileMenu.tileTable.getChildren().size; i ++)
        {
            if(map.tileMenu.tileTable.getChildren().get(i) instanceof TileTool)
            {
                PropertyField probability = new PropertyField("Probability", "1.0", skin, this, false);
                probability.value.setTextFieldFilter(new TextField.TextFieldFilter()
                {
                    @Override
                    public boolean acceptChar(TextField textField, char c)
                    {
                        return c == '.' || Character.isDigit(c);
                    }
                });

                ((TileTool) map.tileMenu.tileTable.getChildren().get(i)).lockedProperties.add(probability);
                PropertyField type = new PropertyField("Type", "", skin, this, false);
                ((TileTool) map.tileMenu.tileTable.getChildren().get(i)).lockedProperties.add(type);

                PropertyField dustType = new PropertyField("Dust Type", "", skin, this, false);
                ((TileTool) map.tileMenu.tileTable.getChildren().get(i)).lockedProperties.add(dustType);

                PropertyField dustColor = new PropertyField(skin, this, false, 1, 1, 1, 1);
                ((TileTool) map.tileMenu.tileTable.getChildren().get(i)).lockedProperties.add(dustColor);
            }
        }
    }
    private void setSpriteProperties()
    {
        for(int i = 0; i < map.tileMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.tileMenu.spriteTable.getChildren().get(i) instanceof TileTool)
            {
                PropertyField probability = new PropertyField("Probability", "1.0", skin, this, false);
                probability.value.setTextFieldFilter(new TextField.TextFieldFilter()
                {
                    @Override
                    public boolean acceptChar(TextField textField, char c)
                    {
                        return c == '.' || Character.isDigit(c);
                    }
                });

                ((TileTool) map.tileMenu.spriteTable.getChildren().get(i)).lockedProperties.add(probability);
                PropertyField type = new PropertyField("Type", "", skin, this, false);
                ((TileTool) map.tileMenu.spriteTable.getChildren().get(i)).lockedProperties.add(type);
                PropertyField z = new PropertyField("spawnZ", "", skin, this, false);
                ((TileTool) map.tileMenu.spriteTable.getChildren().get(i)).lockedProperties.add(z);
            }
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
        this.tilePropertyPanel.table.clearChildren();
        this.spritePropertyPanel.table.clearChildren();
        this.mapPropertyPanel.removeablePropertyPanel.table.clearChildren();
        if(map.selectedLayer != null)
        {
            this.layerPropertyPanel.setVisible(true);
            this.layerPropertyPanel.layerWidthProperty.value.setText(Integer.toString(map.selectedLayer.width));
            this.layerPropertyPanel.layerHeightProperty.value.setText(Integer.toString(map.selectedLayer.height));
            this.layerPropertyPanel.layerZProperty.value.setText(Float.toString(map.selectedLayer.z));
        }
        else
            this.layerPropertyPanel.setVisible(false);
        if(map.tileMenu.selectedTiles.size == 1)
        {
            if(map.tileMenu.selectedTiles.first().tool == TileMenuTools.TILE)
            {
                Array<PropertyField> tileProperties = map.tileMenu.selectedTiles.first().lockedProperties;
                for (int i = 0; i < tileProperties.size; i++)
                    this.tilePropertyPanel.table.add(tileProperties.get(i)).padBottom(1).row();
                this.tilePropertyPanel.setVisible(true);
            }
            else if(map.tileMenu.selectedTiles.first().tool == TileMenuTools.SPRITE)
            {
                Array<PropertyField> spriteProperties = map.tileMenu.selectedTiles.first().lockedProperties;
                for (int i = 0; i < spriteProperties.size; i++)
                    this.spritePropertyPanel.table.add(spriteProperties.get(i)).padBottom(1).row();
                this.spritePropertyPanel.setVisible(true);
            }
        }
        if(map.selectedSprites.size > 0)
        {
            Array<PropertyField> spriteProperties = map.selectedSprites.first().lockedProperties;
            for (int i = 0; i < spriteProperties.size; i++)
                this.spritePropertyPanel.table.add(spriteProperties.get(i)).padBottom(1).row();
            this.spritePropertyPanel.setVisible(true);
        }
        if(map.selectedSprites.size == 0 && map.tileMenu.selectedTiles.size == 0)
        {
            for(int i = 0; i < mapPropertyPanel.properties.size; i ++)
            {
                this.mapPropertyPanel.removeablePropertyPanel.table.add(mapPropertyPanel.properties.get(i)).padBottom(1).row();
            }
            this.mapPropertyPanel.removeablePropertyPanel.setVisible(true);
        }
        if(this.layerPropertyPanel.isVisible())
            this.layerPropertyPanel.setSize(getWidth(), toolHeight);
        else
            this.layerPropertyPanel.setSize(getWidth(), 0);
        if(this.tilePropertyPanel.isVisible())
            this.tilePropertyPanel.setSize(getWidth(), toolHeight);
        else
            this.tilePropertyPanel.setSize(getWidth(), 0);
        if(this.spritePropertyPanel.isVisible())
            this.spritePropertyPanel.setSize(getWidth(), toolHeight);
        else
            this.spritePropertyPanel.setSize(getWidth(), 0);
        if(this.mapPropertyPanel.removeablePropertyPanel.isVisible())
            this.mapPropertyPanel.removeablePropertyPanel.setSize(getWidth(), toolHeight);
        else
            this.mapPropertyPanel.removeablePropertyPanel.setSize(getWidth(), 0);

        this.propertyPanel.rebuild();
        this.propertyTable.invalidateHierarchy();

        setSize(getWidth(), getHeight()); // refits everything
    }
}