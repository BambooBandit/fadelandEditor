package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class ObjectPropertyPanel extends Group
{
    public static int textFieldHeight = 35;

    private FadelandEditor editor;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    private Label probablityProperty;
    private TextField probablityValue;

    public ObjectPropertyPanel(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.probablityProperty = new Label("Rotation", skin);
        this.probablityValue = new TextField("0", skin);

        this.table.add(this.probablityProperty);
        this.table.add(this.probablityValue);

        this.stack.add(this.background);
        this.stack.add(this.table);

        this.addActor(this.stack);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
            this.table.getCell(this.table.getChildren().get(i)).size(width / 2, textFieldHeight);

        float newHeight = textFieldHeight * this.table.getChildren().size / 2;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, newHeight);
    }
}