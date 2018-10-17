package com.fadeland.editor.ui.TileMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class TileMenu extends Group
{
    public static int tileSize = 64;
    public static int tilePadding = 2; // Bleeding area in pixels
    public static int toolHeight = 35;
    public static String tileSheetName = "tiles.png";

    private FadelandEditor editor;

    private ScrollPane scrollPane;
    private Stack stack;
    private Image background;
    private TileMenuToolPane toolPane;
    private Table menuTable; // Holds the menu
    public Table tileTable; // Holds all the tiles

    public TileMenu(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new TileMenuToolPane(editor, this, skin);

        this.menuTable = new Table();
        this.tileTable = new Table();
        this.tileTable.left().top();
        this.scrollPane = new ScrollPane(this.tileTable, GameAssets.getUISkin());


        // Add all the tiles to the tileTable as Images
        Texture tileSheet = new Texture(tileSheetName);
        tileTable.padLeft(1);
        tileTable.padTop(1);
        for(int y = 0; y < tileSheet.getHeight(); y += tileSize)
        {
            for(int x = 0; x < tileSheet.getWidth(); x += tileSize)
            {
                TextureRegion tileRegion = new TextureRegion(tileSheet, x, y, tileSize, tileSize);
                Image tile = new Image(tileRegion);
                tile.setSize(tileSize, tileSize);
                tileTable.add(tile);
            }
            tileTable.row();
        }

        this.stack.add(this.background);
        this.stack.add(this.scrollPane);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.stack.setSize(width, height - toolHeight);
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.toolPane.setSize(width, toolHeight);


        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    @Override
    public void setPosition (float x, float y)
    {
        super.setPosition(x, y);
    }
}
