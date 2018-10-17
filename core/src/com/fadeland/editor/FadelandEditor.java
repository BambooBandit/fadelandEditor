package com.fadeland.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.FileMenu;

public class FadelandEditor extends Game
{
	public static final int buttonHeight = 35;
	public static final int tabHeight = 25;
	public static final int toolHeight = 35;
	private static GameAssets gameAssets;
	private SpriteBatch batch;
	private Stage stage;
	private FileMenu fileMenu;

	public TileMap activeMap; // Map currently being edited
	public Array<TileMap> maps; // All maps open in the program.

	@Override
	public void create ()
	{
		gameAssets = GameAssets.get();

		this.maps = new Array<>();

		this.batch = new SpriteBatch();
		this.stage = new Stage(new ScreenViewport());

		this.fileMenu = new FileMenu(GameAssets.getUISkin(), this);
		this.fileMenu.setSize(Gdx.graphics.getWidth(), buttonHeight, tabHeight, toolHeight);
		this.fileMenu.setPosition(0, Gdx.graphics.getHeight() - this.fileMenu.getHeight()); // Move to the top

		this.fileMenu.setVisible(true);
		this.stage.addActor(this.fileMenu);

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
