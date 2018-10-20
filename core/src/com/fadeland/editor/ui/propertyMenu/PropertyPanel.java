package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class PropertyPanel extends Group
{
    public static int textFieldHeight = 35;

    private FadelandEditor editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    private ScrollPane scrollPane;
    public Table table; // Holds all the text fields
    public Array<PropertyField> properties;

    private Skin skin;

    public PropertyPanel(Skin skin, PropertyMenu menu, FadelandEditor fadelandEditor)
    {
        this.skin = skin;
        this.menu = menu;
        this.editor = fadelandEditor;

        this.properties = new Array<>();

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.scrollPane = new ScrollPane(this.table, skin);

        this.stack.add(this.background);
        this.stack.add(this.scrollPane);

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
        PropertyField property = new PropertyField("Property", "Value", this.skin, menu, true);
        this.table.add(property).padBottom(1).row();
        this.properties.add(property);

        setSize(getWidth(), getHeight()); // Resize to fit the new field
    }

    /** Remove all properties with the property value of the string.
     * Return true if something was removed to allow for recursive removing all the properties.
     * External use always returns false. */
    public boolean removeProperty(String propertyName)
    {
        PropertyField propertyField = null;
        for(int i = 0; i < this.properties.size; i ++)
        {
            propertyField = this.properties.get(i);
            if(propertyField.getProperty().equals(propertyName))
                break;

        }
        if(propertyField != null)
        {
            this.table.removeActor(propertyField, false);
            this.properties.removeValue(propertyField, false);
            rebuild();
            return removeProperty(propertyName);
        }
        return false;
    }

    public void removeProperty(PropertyField propertyField)
    {
        this.table.removeActor(propertyField, false);
        this.properties.removeValue(propertyField, false);
        rebuild();
    }

    /** Rebuilds the table to remove gaps when removing properties. */
    private void rebuild()
    {
        this.table.clearChildren();
        for(int i = 0; i < this.properties.size; i ++)
            this.table.add(this.properties.get(i)).padBottom(1).row();
        setSize(getWidth(), getHeight()); // Resize to fit the fields
    }
}