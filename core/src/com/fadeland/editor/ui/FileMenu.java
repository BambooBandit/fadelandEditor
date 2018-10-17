package com.fadeland.editor.ui;

import com.badlogic.gdx.Gdx;
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
    private TextButton undoButton;
    private TextButton redoButton;

    public FileMenu(Skin skin)
    {
        this.newButton = new TextButton("New", skin);
        this.openButton = new TextButton("Open", skin);
        this.saveButton = new TextButton("Save", skin);
        this.saveAsButton = new TextButton("Save As", skin);
        this.undoButton = new TextButton("Undo", skin);
        this.redoButton = new TextButton("Redo", skin);

        // Buttons text color
        this.newButton.getLabel().setColor(Color.BLACK);
        this.openButton.getLabel().setColor(Color.BLACK);
        this.saveButton.getLabel().setColor(Color.BLACK);
        this.saveAsButton.getLabel().setColor(Color.BLACK);
        this.undoButton.getLabel().setColor(Color.BLACK);
        this.redoButton.getLabel().setColor(Color.BLACK);

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
        this.undoButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.redoButton.addListener(new ClickListener()
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
        this.table.add(this.undoButton);
        this.table.add(this.redoButton);

        this.addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        int buttonAmount = table.getCells().size;
        float buttonWidth = width / buttonAmount;
        this.table.getCell(this.newButton).size(buttonWidth, height);
        this.table.getCell(this.openButton).size(buttonWidth, height);
        this.table.getCell(this.saveButton).size(buttonWidth, height);
        this.table.getCell(this.saveAsButton).size(buttonWidth, height);
        this.table.getCell(this.undoButton).size(buttonWidth, height);
        this.table.getCell(this.redoButton).size(buttonWidth, height);

        this.table.invalidateHierarchy();

        super.setSize(this.table.getMinWidth(), this.table.getMinHeight());

    }

    @Override
    public void setPosition (float x, float y)
    {
        super.setPosition(x + getWidth() / 2, y + getHeight() / 2);
    }
}
