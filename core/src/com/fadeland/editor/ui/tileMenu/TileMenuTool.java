package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.GameAssets;

import static com.fadeland.editor.FadelandEditor.toolHeight;

public class TileMenuTool extends Group
{
    private Image background;
    protected Image image;

    protected TileMenuToolPane tileMenuToolPane;

    public boolean isSelected;

    public TileMenuTools tool;
    public SheetTools sheetTool;

    /** For tile menu tools */
    public TileMenuTool(TileMenuTools tool, final TileMenuToolPane tileMenuToolPane, Skin skin)
    {
        this.tool = tool;
        this.tileMenuToolPane = tileMenuToolPane;
        this.background = new Image(GameAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas

        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);

        final TileMenuTool selectedTool = this;
        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                tileMenuToolPane.selectTool(selectedTool);
            }
        });
    }

    /** For tiles */
    public TileMenuTool(TileMenuTools tool, SheetTools sheetTool, Image image, final TileMenuToolPane tileMenuToolPane, Skin skin)
    {
        this.tool = tool;
        this.tileMenuToolPane = tileMenuToolPane;
        this.image = image;

        addActor(image);
        setSize(image.getWidth(), image.getHeight());

        final TileMenuTool selectedTool = this;
        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                tileMenuToolPane.selectTool(selectedTool);
            }
        });
    }

    public void select()
    {
        if(this.background!= null)
            this.background.setColor(Color.GREEN);
        else
            this.image.setColor(Color.GREEN);

        this.isSelected = true;

        if(this.tool == TileMenuTools.LINES)
        {
            tileMenuToolPane.menu.tileTable.setDebug(true);
            tileMenuToolPane.menu.spriteTable.setDebug(true);
        }
        else if(this.tool == TileMenuTools.TILESELECT)
            tileMenuToolPane.menu.tileScrollPane.setVisible(true);
        else if(this.tool == TileMenuTools.SPRITESELECT)
            tileMenuToolPane.menu.spriteScrollPane.setVisible(true);
    }

    public void unselect()
    {
        if(this.background!= null)
            this.background.setColor(Color.WHITE);
        else
            this.image.setColor(Color.WHITE);

        this.isSelected = false;

        if(this.tool == TileMenuTools.LINES)
        {
            tileMenuToolPane.menu.tileTable.setDebug(false);
            tileMenuToolPane.menu.spriteTable.setDebug(false);
        }
        else if(this.tool == TileMenuTools.TILESELECT)
            tileMenuToolPane.menu.tileScrollPane.setVisible(false);
        else if(this.tool == TileMenuTools.SPRITESELECT)
            tileMenuToolPane.menu.spriteScrollPane.setVisible(false);
    }
}