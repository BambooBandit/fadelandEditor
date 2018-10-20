package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PropertyField extends Group
{
    private Label property; // Null if removeable is true
    private TextField propertyTextField; // Null if removeable is false
    private TextField value;
    private TextButton remove; // Null if removeable is false
    private Table table;
    private boolean removeable;

    private PropertyMenu menu;

    public PropertyField(String property, String value, Skin skin, final PropertyMenu menu, boolean removeable)
    {
        this.menu = menu;

        this.removeable = removeable;

        if(removeable)
            this.propertyTextField = new TextField(property, skin);
        else
            this.property = new Label(property, skin);
        this.value = new TextField(value, skin);

        this.table = new Table();
        this.table.bottom().left();
        if(removeable)
            this.table.add(this.propertyTextField);
        else
            this.table.add(this.property);
        this.table.add(this.value);

        if(removeable)
        {
            this.remove = new TextButton("X", skin);
            final PropertyField removeableField = this;
            this.remove.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    menu.removeProperty(removeableField);
                }
            });

            this.table.add(this.remove);
        }

        addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.value.setSize(width / 2, height);
        if(this.removeable)
        {
            this.propertyTextField.setSize(width / 2, height);
            this.table.getCell(this.propertyTextField).size(width / 2, height);
            this.table.getCell(this.value).size((width / 2) - height, height);
        }
        else
        {
            this.property.setSize(width / 2, height);
            this.table.getCell(this.property).size(width / 2, height);
            this.table.getCell(this.value).size(width / 2, height);
        }
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public String getProperty()
    {
        if(this.removeable)
            return this.propertyTextField.getText();
        else
            return this.property.getName();
    }
    public String getValue()
    {
        return this.value.getText();
    }
}
