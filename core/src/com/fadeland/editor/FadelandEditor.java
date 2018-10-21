package com.fadeland.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.fileMenu.FileMenu;
import com.fadeland.editor.ui.fileMenu.Tool;
import com.fadeland.editor.ui.layerMenu.LayerMenu;
import com.fadeland.editor.ui.propertyMenu.PropertyMenu;
import com.fadeland.editor.ui.tileMenu.TileMenu;
import com.fadeland.editor.ui.tileMenu.TileMenuTool;
import com.fadeland.editor.ui.tileMenu.TileTool;

public class FadelandEditor extends Game
{
	public static final int buttonHeight = 35;
	public static final int tabHeight = 25;
	public static final int toolHeight = 35;
	private static GameAssets gameAssets;
	public ShapeRenderer shapeRenderer;
	public SpriteBatch batch;
	public InputMultiplexer inputMultiplexer;
	public Stage stage;
	public FileMenu fileMenu;
	public TileMenu tileMenu;
	public PropertyMenu propertyMenu;
	public LayerMenu layerMenu;

	public TileMap activeMap; // Map currently being edited
	public Array<TileMap> maps; // All maps open in the program.

	@Override
	public void create ()
	{
		gameAssets = GameAssets.get();

		this.inputMultiplexer = new InputMultiplexer();

		this.maps = new Array<>();

		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.stage = new Stage(new ScreenViewport());

		// fileMenu
		this.fileMenu = new FileMenu(GameAssets.getUISkin(), this);
		this.fileMenu.setVisible(true);
		this.stage.addActor(this.fileMenu);

		// tileMenu
		this.tileMenu = new TileMenu(GameAssets.getUISkin(), this);
		this.tileMenu.setVisible(true);
		this.stage.addActor(this.tileMenu);

		// propertyMenu
		this.propertyMenu = new PropertyMenu(GameAssets.getUISkin(), this);
		this.propertyMenu.setVisible(true);
		this.stage.addActor(this.propertyMenu);

		// layerMenu
		this.layerMenu = new LayerMenu(GameAssets.getUISkin(), this);
		this.layerMenu.setVisible(true);
		this.stage.addActor(this.layerMenu);

		this.inputMultiplexer.addProcessor(this.stage);
		Gdx.input.setInputProcessor(this.inputMultiplexer);
	}

	@Override
	public void render ()
	{
		if(activeMap == null)
		{
			// The map clears the screen, but no map is active so manually clear the screen here
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		else // Render the active map
			super.render();

		this.batch.begin();
		this.batch.end();

		stage.act();
        stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		this.stage.getViewport().update(width, height, true);
		this.fileMenu.setSize(Gdx.graphics.getWidth(), buttonHeight, tabHeight, toolHeight);
		this.fileMenu.setPosition(0, Gdx.graphics.getHeight() - this.fileMenu.getHeight());

		this.tileMenu.setSize(Gdx.graphics.getWidth() / 4, (Gdx.graphics.getHeight() - this.fileMenu.getHeight()) / 2);
		this.tileMenu.setPosition(Gdx.graphics.getWidth() - this.tileMenu.getWidth(), 0);

		this.propertyMenu.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() - this.fileMenu.getHeight());
		this.propertyMenu.setPosition(0, 0);

		this.layerMenu.setSize(Gdx.graphics.getWidth() / 4, (Gdx.graphics.getHeight() - this.fileMenu.getHeight()) / 2);
		this.layerMenu.setPosition(Gdx.graphics.getWidth() - this.tileMenu.getWidth(), this.tileMenu.getHeight());
	}

	@Override
	public void dispose ()
	{
		this.batch.dispose();
	}

	/** Stores the map in the maps array to allow for switching between map tabs.
	 * Creates the tab for the map.*/
	public void addToMaps(TileMap map)
	{
		this.fileMenu.mapTabPane.addMap(map);
	}

    public Tool getFileTool()
    {
        return this.fileMenu.toolPane.getTool();
    }

    public TileTool getTileTool()
    {
        if(this.tileMenu.selectedTiles.size == 0)
            return null;
        return this.tileMenu.selectedTiles.first();
    }
}
