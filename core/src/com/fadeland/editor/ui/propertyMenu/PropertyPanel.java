package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.tileMenu.TileMenuTools;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class PropertyPanel extends Group
{
    public static int textFieldHeight = 35;

    private FadelandEditor editor;
    private TileMap map;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    private ScrollPane scrollPane;
    public Table table; // Holds all the text fields

    private Skin skin;

    public PropertyPanel(Skin skin, PropertyMenu menu, FadelandEditor fadelandEditor, TileMap map)
    {
        this.skin = skin;
        this.menu = menu;
        this.editor = fadelandEditor;
        this.map = map;

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.scrollPane = new ScrollPane(this.table, skin);

        this.stack.add(this.background);

        this.addActor(this.scrollPane);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
        {
            this.table.getChildren().get(i).setSize(width, textFieldHeight);
            this.table.getCell(this.table.getChildren().get(i)).size(width, textFieldHeight);
        }

        this.table.invalidateHierarchy();

        this.scrollPane.setSize(width, height);
        this.scrollPane.invalidateHierarchy();

        this.background.setBounds(0, 0, width, height);
        this.stack.setSize(width, height);
        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newProperty()
    {
        if(map.selectedObjects.size > 0)
        {
            for (int i = 0; i < map.selectedObjects.size; i++)
                this.map.selectedObjects.get(i).properties.add(new PropertyField("Property", "Value", this.skin, menu, true));
        }
        else
        {
            for (int i = 0; i < map.tileMenu.selectedTiles.size; i++)
                this.map.tileMenu.selectedTiles.get(i).properties.add(new PropertyField("Property", "Value", this.skin, menu, true));
        }
    }

    /** Remove all properties with the property value of the string.
     * Return true if something was removed to allow for recursive removing all the properties.
     * External use always returns false. */
    public boolean removeProperty(String propertyName)
    {
        for(int i = 0; i < map.tileMenu.selectedTiles.size; i ++)
        {
            PropertyField propertyField = null;
            Array<PropertyField> properties = map.tileMenu.selectedTiles.get(i).properties;
            for (int k = 0; k < properties.size; k++)
            {
                propertyField = properties.get(k);
                if (propertyField.getProperty().equals(propertyName))
                    break;

            }
            if (propertyField != null)
            {
                properties.removeValue(propertyField, false);
                return removeProperty(propertyName);
            }
        }
        return false;
    }

    public void removeProperty(PropertyField propertyField)
    {
        for(int i = 0; i < map.tileMenu.selectedTiles.size; i ++)
        {
            Array<PropertyField> properties = map.tileMenu.selectedTiles.get(i).properties;
            properties.removeValue(propertyField, false);
        }
    }

    /** Rebuilds the table to remove gaps when removing properties. */
    public void rebuild()
    {
        this.table.clearChildren();

        if(map.tileMenu.selectedTiles.size == 1)
        {
            Array<PropertyField> properties = map.tileMenu.selectedTiles.first().properties;
            for (int i = 0; i < properties.size; i++)
                this.table.add(properties.get(i)).padBottom(1).row();
        }
        else if(map.tileMenu.selectedTiles.size > 1) // Only add common properties
        {
            TileTool firstTool = map.tileMenu.selectedTiles.first();
            for(int i = 0; i < firstTool.properties.size; i ++)
            {
                boolean commonProperty = true;
                for(int k = 1; k < map.tileMenu.selectedTiles.size; k ++)
                {
                    if(!map.tileMenu.selectedTiles.get(k).properties.contains(firstTool.properties.get(i), false))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(firstTool.properties.get(i)).padBottom(1).row();
            }
        }
        else if(map.selectedObjects.size == 1)
        {
            Array<PropertyField> properties = map.selectedObjects.first().properties;
            for (int i = 0; i < properties.size; i++)
                this.table.add(properties.get(i)).padBottom(1).row();
        }
        else if(map.selectedObjects.size > 1) // Only add common properties
        {
            MapObject mapObject = map.selectedObjects.first();
            for(int i = 0; i < mapObject.properties.size; i ++)
            {
                boolean commonProperty = true;
                for(int k = 1; k < map.selectedObjects.size; k ++)
                {
                    if(!map.selectedObjects.get(k).properties.contains(mapObject.properties.get(i), false))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(mapObject.properties.get(i)).padBottom(1).row();
            }
        }
        setSize(getWidth(), getHeight()); // Resize to fit the fields
    }
}