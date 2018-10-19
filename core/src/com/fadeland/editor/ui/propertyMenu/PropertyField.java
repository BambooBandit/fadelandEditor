package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class PropertyField extends Group
{
    private Label property;
    private TextField value;
    private Table table;

    public PropertyField(String property, String value, Skin skin)
    {
        this.property = new Label(property, skin);
        this.value = new TextField(value, skin);

        this.table = new Table();
        this.table.bottom().left();
        this.table.add(this.property);
        this.table.add(this.value);

        addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.property.setSize(width / 2, height);
        this.value.setSize(width / 2, height);
        this.table.getCell(this.property).size(width / 2, height);
        this.table.getCell(this.value).size(width / 2, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }
}
