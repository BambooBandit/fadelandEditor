package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;

public class TilePropertyPanel extends Group
{
    public static int textFieldHeight = 32;

    private FadelandEditor editor;

    public Table table; // Holds all the text fields

    private Label probablityProperty;
    private TextField probablityValue;

    public TilePropertyPanel(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.table = new Table();
        this.table.left().top();

        this.probablityProperty = new Label("Probablity", skin);
        this.probablityValue = new TextField("1.0", skin);

        this.table.add(this.probablityProperty);
        this.table.add(this.probablityValue);

        this.addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
            this.table.getCell(this.table.getChildren().get(i)).size(width / 2, textFieldHeight);

        this.table.invalidateHierarchy();

        super.setSize(width, textFieldHeight * this.table.getChildren().size / 2);
    }
}