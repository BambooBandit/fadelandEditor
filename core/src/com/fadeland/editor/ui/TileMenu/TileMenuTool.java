package com.fadeland.editor.ui.TileMenu;

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
    private Image image;

    private TileMenuToolPane tileMenuToolPane;

    public boolean isSelected;

    private TileMenuTools tool;

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

    public void select()
    {
        this.background.setColor(Color.GREEN);
        this.isSelected = true;

        if(this.tool == TileMenuTools.LINES)
            tileMenuToolPane.menu.tileTable.setDebug(true);
    }

    public void unselect()
    {
        this.background.setColor(Color.WHITE);
        this.isSelected = false;

        if(this.tool == TileMenuTools.LINES)
            tileMenuToolPane.menu.tileTable.setDebug(false);
    }
}