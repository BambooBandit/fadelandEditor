package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;

public class TileMenu extends Group
{
    public static int tileSize = 64;
    public static int tilePadding = 2; // Bleeding area in pixels
    public static int toolHeight = 35;
    public static String tileSheetName = "tiles.png";
    public static String spriteSheetName = "sprites.png";

    private FadelandEditor editor;
    private TileMap map;

    public ScrollPane tileScrollPane;
    public ScrollPane spriteScrollPane;
    private Stack stack;
    private Image background;
    public TileMenuToolPane toolPane;
    public Table tileTable; // Holds all the tiles
    public Table spriteTable; // Holds all the sprites

    public Array<TileTool> selectedTiles;

    public TileMenu(Skin skin, FadelandEditor fadelandEditor, TileMap map)
    {
        this.editor = fadelandEditor;
        this.map = map;

        this.selectedTiles = new Array<>();

        this.tileTable = new Table();
        this.tileTable.left().top();
        this.tileScrollPane = new ScrollPane(this.tileTable, GameAssets.getUISkin());

        this.spriteTable = new Table();
        this.spriteTable.left().top();
        this.spriteScrollPane = new ScrollPane(this.spriteTable, GameAssets.getUISkin());

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new TileMenuToolPane(editor, this, map, skin);

        // Add all the tiles to the tileTable as Images
        Texture tileSheet = new Texture(tileSheetName);
        tileTable.padLeft(1);
        tileTable.padTop(1);
        for(int y = 0; y < tileSheet.getHeight(); y += tileSize)
        {
            for(int x = 0; x < tileSheet.getWidth(); x += tileSize)
            {
                TextureRegion tileRegion = new TextureRegion(tileSheet, x, y, tileSize, tileSize);

                TileTool tile = new TileTool(TileMenuTools.TILE, new Image(tileRegion), tileRegion, x + y, tileSheet.getWidth() - x, y, toolPane, skin);
                tileTable.add(tile);
            }
            tileTable.row();
        }

        // Add all the sprites to the spriteTable as Images
        Texture spriteSheet = new Texture(spriteSheetName);
        spriteTable.padLeft(1);
        spriteTable.padTop(1);
        for(int y = 0; y < spriteSheet.getHeight(); y += tileSize)
        {
            for(int x = 0; x < spriteSheet.getWidth(); x += tileSize)
            {
                TextureRegion spriteRegion = new TextureRegion(spriteSheet, x, y, tileSize, tileSize);

                TileTool sprite = new TileTool(TileMenuTools.SPRITE, new Image(spriteRegion), spriteRegion,x + y, spriteSheet.getWidth() - x, y, toolPane, skin);
                spriteTable.add(sprite);
            }
            spriteTable.row();
        }

        this.stack.add(this.background);
        this.stack.add(this.tileScrollPane);
        this.stack.add(this.spriteScrollPane);
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
}
