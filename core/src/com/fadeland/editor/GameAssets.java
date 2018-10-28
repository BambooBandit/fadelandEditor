package com.fadeland.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

/**Singleton that holds all different kinds
 * of textures, colors, fonts and sounds.*/
public class GameAssets
{
    private static GameAssets gameAssets = null;
    private static AssetManager assets;
    private static BitmapFont smallFont;
    private static BitmapFont font;
    private static BitmapFont headerFont;
    private static GlyphLayout glyph;
    private static TextureAtlas gameAtlas;
    private static TextureAtlas uiAtlas;
    private static Skin uiSkin;

    private static ObjectMap<String, TextureAtlas.AtlasRegion> cachedGameAtlas;

    private GameAssets() { }

    /**Gets the static instance if the GameAssets class.
     * It holds all different kinds of textures, colors,
     * fonts and sounds.
     * @return gameAssets, instantiates it if null*/
    public static GameAssets get()
    {
        if(gameAssets == null)
        {
            GameAssets.gameAssets = new GameAssets();
            assets = new AssetManager();
            uiSkin = new Skin();
            initFonts();
            loadAssets();
            GameAssets.gameAssets.setGameAtlas((TextureAtlas) GameAssets.gameAssets.getAssets().get("atlas.atlas"));
        }
        return gameAssets;
    }


    public static <T> T getAsset(String string, Class<T> c) { return get().assets.get(string, c); }

    /**Fills the AssetManager object with
     * texture atlases for UI and game art.*/
    public static void loadAssets()
    {
        assets.load("ui/ui.atlas", TextureAtlas.class);
        assets.finishLoading();
        uiAtlas = assets.get("ui/ui.atlas");
        uiSkin.addRegions(uiAtlas);
        uiSkin.add("default-font", font);
        uiSkin.add("header-font", headerFont);
        uiSkin.add("small-font", smallFont);
        uiSkin.load(Gdx.files.internal("ui/ui.json"));
        assets.load("atlas.atlas", TextureAtlas.class);
        assets.finishLoading();
    }

    /**Initializes fonts upon game start up.
     * The fonts will be sized according to window size.*/
    private static void initFonts()
    {
        int size = 15;

        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/sitka.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = size;
        params.color = Color.WHITE;
        font = generator.generateFont(params);

        params.size = (int) (size * 2.5);
        headerFont = generator.generateFont(params);

        params.size = (int) (size / 1.5f);
        smallFont = generator.generateFont(params);

        glyph = new GlyphLayout();
    }

    public static GameAssets getGameAssets()
    {
        return get().gameAssets;
    }

    public static AssetManager getAssets()
    {
        return get().assets;
    }

    public static BitmapFont getFont() { return get().font; }

    public static BitmapFont getHeaderFont() { return get().headerFont; }

    public static BitmapFont getSmallFont() { return get().smallFont; }

    public static GlyphLayout getGlyph()
    {
        return get().glyph;
    }

    public static TextureAtlas getGameAtlas()
    {
        return get().gameAtlas;
    }

    /**Should only be used by the Loader
     * @param atlas - Setter*/
    public static void setGameAtlas(TextureAtlas atlas)
    {
        get().gameAtlas = atlas;
        cachedGameAtlas = new ObjectMap<>(atlas.getRegions().size);
        for(int i = 0; i < atlas.getRegions().size; i++)
            cachedGameAtlas.put(atlas.getRegions().get(i).name, atlas.getRegions().get(i));
    }

    public static TextureRegion getTextureRegion(String path) { return cachedGameAtlas.get(path); }

    public static boolean hasTextureRegion(String path) { return cachedGameAtlas.containsKey(path); }

    public static Sprite createSprite(String path) { return new Sprite(cachedGameAtlas.get(path)); }

    public static TextureAtlas.AtlasSprite createAtlasSprite(String path) { return new TextureAtlas.AtlasSprite(cachedGameAtlas.get(path)); }

    public static TextureAtlas getUIAtlas()
    {
        return get().uiAtlas;
    }

    public static Skin getUISkin()
    {
        return get().uiSkin;
    }
}
