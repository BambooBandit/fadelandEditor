package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;

import java.util.Iterator;

import static com.fadeland.editor.map.TileMap.tileSize;

public class TileMenu extends Group
{
    public static int tileSheetWidth;
    public static int tileSheetHeight;
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
        Iterator<EventListener> iterator = tileScrollPane.getListeners().iterator();
        while (iterator.hasNext())
        {
            EventListener listener = iterator.next();
            if (listener instanceof InputListener)
                iterator.remove();
        }
        this.tileScrollPane.addListener(new InputListener()
        {
            public boolean scrolled (InputEvent event, float x, float y, int amount)
            {
                Table table = (Table) tileScrollPane.getWidget();
                for(int i = 0; i < table.getCells().size; i ++)
                {
                    table.getCells().get(i).size(table.getCells().get(i).getMinWidth() - amount * 5, table.getCells().get(i).getMinHeight() - amount * 5);
                    TileTool tileTool = (TileTool) table.getCells().get(i).getActor();
                    tileTool.image.setSize(table.getCells().get(i).getMinWidth() - amount * 5, table.getCells().get(i).getMinHeight() - amount * 5);
                    table.invalidateHierarchy();
                }
                return true;
            }
        });

        this.spriteTable = new Table();
        this.spriteTable.left().top();
        this.spriteScrollPane = new ScrollPane(this.spriteTable, GameAssets.getUISkin());
        iterator = spriteScrollPane.getListeners().iterator();
        while (iterator.hasNext())
        {
            EventListener listener = iterator.next();
            if (listener instanceof InputListener)
                iterator.remove();
        }
        this.spriteScrollPane.addListener(new InputListener()
        {
            public boolean scrolled (InputEvent event, float x, float y, int amount)
            {
                Table table = (Table) spriteScrollPane.getWidget();
                for(int i = 0; i < table.getCells().size; i ++)
                {
                    if(table.getCells().get(i).getActor() instanceof TileTool)
                    {
                        table.getCells().get(i).size(table.getCells().get(i).getMinWidth() - (table.getCells().get(i).getMinWidth() / (amount * 3f)), table.getCells().get(i).getMinHeight() - (table.getCells().get(i).getMinHeight() / (amount * 3f)));
                        TileTool tileTool = (TileTool) table.getCells().get(i).getActor();
                        tileTool.image.setSize(table.getCells().get(i).getMinWidth(), table.getCells().get(i).getMinHeight());
                        table.invalidateHierarchy();
                    }
                }
                return true;
            }
        });

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new TileMenuToolPane(editor, this, map, skin);

        // Add all the tiles to the tileTable as Images
        Texture tileSheet = new Texture(tileSheetName);
        tileSheetWidth = tileSheet.getWidth();
        tileSheetHeight = tileSheet.getHeight();
        tileTable.padLeft(1);
        tileTable.padTop(1);
        int id = 0;
        for(int y = 0; y < tileSheet.getHeight(); y += tileSize)
        {
            for(int x = 0; x < tileSheet.getWidth(); x += tileSize)
            {
                TextureRegion tileRegion = new TextureRegion(tileSheet, x, y, tileSize, tileSize);

                TileTool tile = new TileTool(TileMenuTools.TILE, SheetTools.MAP, new Image(tileRegion), tileRegion, "", id, tileSheet.getWidth() - x - tileSize, y, toolPane, skin);
                id ++;
                tileTable.add(tile);
            }
            tileTable.row();
        }

        id = createSpriteSheet(SheetTools.MAP, skin, 0);
        id = createSpriteSheet(SheetTools.FLATMAP, skin, id);

        this.stack.add(this.background);
        this.stack.add(this.tileScrollPane);
        this.stack.add(this.spriteScrollPane);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    private int createSpriteSheet(SheetTools sheetTool, Skin skin, int id)
    {
        // Add all the sprites to the spriteTable as Images
        spriteTable.padLeft(1);
        spriteTable.padTop(1);
        spriteTable.add(new Label(sheetTool.name, skin));
        spriteTable.row();
        for(int i = 0; i < GameAssets.getGameAtlas(sheetTool.name).getRegions().size; i ++)
        {
            TextureAtlas.AtlasRegion spriteRegion = GameAssets.getGameAtlas(sheetTool.name).getRegions().get(i);

            TileTool sprite = new TileTool(TileMenuTools.SPRITE, sheetTool, new Image(spriteRegion), spriteRegion, spriteRegion.name, id, 0, 0, toolPane, skin);
            float newWidth = sprite.image.getWidth() / 5;
            float newHeight = sprite.image.getHeight() / 5;
            sprite.image.setSize(newWidth, newHeight);
            sprite.setSize(newWidth, newHeight);
            id ++;
            spriteTable.add(sprite);
            if((i + 1) % 5 == 0)
                spriteTable.row();
        }
        spriteTable.row();
        spriteTable.padBottom(200).row();
        return id;
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

    public TileTool getTileTool(TileMenuTools tileMenuTools, int id)
    {
        if(tileMenuTools == TileMenuTools.TILE)
        {
            for(int i = 0; i < tileTable.getChildren().size; i ++)
            {
                TileTool tileTool = (TileTool) tileTable.getChildren().get(i);
                if(tileTool.id == id)
                    return tileTool;
            }
        }
        else if(tileMenuTools == TileMenuTools.SPRITE)
        {
            for(int i = 0; i < spriteTable.getChildren().size; i ++)
            {
                if(spriteTable.getChildren().get(i) instanceof TileTool)
                {
                    TileTool tileTool = (TileTool) spriteTable.getChildren().get(i);
                    if (tileTool.id == id)
                        return tileTool;
                }
            }
        }
        return null;
    }

    public TileTool getSpriteTool(TileMenuTools tileMenuTools, String name)
    {
        if(tileMenuTools == TileMenuTools.SPRITE)
        {
            for(int i = 0; i < spriteTable.getChildren().size; i ++)
            {
                if(spriteTable.getChildren().get(i) instanceof TileTool)
                {
                    TileTool tileTool = (TileTool) spriteTable.getChildren().get(i);
                    if (tileTool.name.equals(name))
                        return tileTool;
                }
            }
        }
        return null;
    }

    public TileTool getTileTool(String type, int id, String name)
    {
        TileMenuTools tileMenuTools = null;
        if(type.equals("tile"))
            tileMenuTools = TileMenuTools.TILE;
        else if(type.equals("sprite"))
            tileMenuTools = TileMenuTools.SPRITE;

        if(tileMenuTools == TileMenuTools.TILE)
        {
            for(int i = 0; i < tileTable.getChildren().size; i ++)
            {
                TileTool tileTool = (TileTool) tileTable.getChildren().get(i);
                if(tileTool.id == id)
                    return tileTool;
            }
        }
        else if(tileMenuTools == TileMenuTools.SPRITE)
        {
            for(int i = 0; i < spriteTable.getChildren().size; i ++)
            {
                if(spriteTable.getChildren().get(i) instanceof TileTool)
                {
                    TileTool tileTool = (TileTool) spriteTable.getChildren().get(i);
                    if (tileTool.name.equals(name))
                        return tileTool;
                }
            }
        }
        return null;
    }
}
