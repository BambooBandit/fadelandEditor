package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class PropertyPanel extends Group
{
    public static int textFieldHeight = 35;

    private FadelandEditor editor;

    private Image background;
    private Stack stack;
    private ScrollPane scrollPane;
    public Table table; // Holds all the text fields

    private Skin skin;

    public PropertyPanel(Skin skin, FadelandEditor fadelandEditor)
    {
        this.skin = skin;
        this.editor = fadelandEditor;

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
        PropertyField property = new PropertyField("Property", "Value", this.skin);
        this.table.add(property).padBottom(1).row();

        setSize(getWidth(), getHeight()); // Resize to fit the new field
    }
}