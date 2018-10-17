package com.fadeland.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.FileMenu.FileMenu;
import com.fadeland.editor.ui.TileMenu.TileMenu;

public class FadelandEditor extends Game
{
	public static final int buttonHeight = 35;
	public static final int tabHeight = 25;
	public static final int toolHeight = 35;
	private static GameAssets gameAssets;
	private SpriteBatch batch;
	private Stage stage;
	private FileMenu fileMenu;
	private TileMenu tileMenu;

	public TileMap activeMap; // Map currently being edited
	public Array<TileMap> maps; // All maps open in the program.

	@Override
	public void create ()
	{
		gameAssets = GameAssets.get();

		this.maps = new Array<>();

		this.batch = new SpriteBatch();
		this.stage = new Stage(new ScreenViewport());

		// FileMenu
		this.fileMenu = new FileMenu(GameAssets.getUISkin(), this);
		this.fileMenu.setVisible(true);
		this.stage.addActor(this.fileMenu);

		// TileMenu
		this.tileMenu = new TileMenu(GameAssets.getUISkin(), this);
		this.tileMenu.setVisible(true);
		this.stage.addActor(this.tileMenu);

		Gdx.input.setInputProcessor(this.stage);
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

		this.tileMenu.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 - this.fileMenu.getHeight());
		this.tileMenu.setPosition(Gdx.graphics.getWidth() - this.tileMenu.getWidth(), 0);
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
}
