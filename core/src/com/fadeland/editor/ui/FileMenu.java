package com.fadeland.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FileMenu extends Group
{
    private Table table;

    private TextButton newButton;
    private TextButton openButton;
    private TextButton saveButton;
    private TextButton saveAsButton;

    public FileMenu(Skin skin)
    {
        this.newButton = new TextButton("New", skin);
        this.openButton = new TextButton("Open", skin);
        this.saveButton = new TextButton("Save", skin);
        this.saveAsButton = new TextButton("Save As", skin);

        // Buttons text color
        this.newButton.getLabel().setColor(Color.BLACK);
        this.openButton.getLabel().setColor(Color.BLACK);
        this.saveButton.getLabel().setColor(Color.BLACK);
        this.saveAsButton.getLabel().setColor(Color.BLACK);

        // Add listeners
        this.newButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.openButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.saveButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.saveAsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });

        // Add header and buttons to the table
        this.table = new Table();
        this.table.add(this.newButton);
        this.table.add(this.openButton);
        this.table.add(this.saveButton);
        this.table.add(this.saveAsButton);

        this.addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        super.setSize(width, height);

        this.table.getCell(this.newButton).size(width / 4.5f, height / 12);
        this.table.getCell(this.openButton).size(width / 4.5f, height / 12);
        this.table.getCell(this.saveButton).size(width / 4.5f, height / 12);
        this.table.getCell(this.saveAsButton).size(width / 4.5f, height / 12);

        this.table.invalidateHierarchy();

        this.table.setPosition(getX() + (getWidth() / 2), getY() + (getHeight() / 2));

    }

    @Override
    public void setPosition (float x, float y)
    {
        super.setPosition(x, y);
        this.table.setPosition(x + (getWidth() / 2), y + (getHeight() / 2));
    }
}
