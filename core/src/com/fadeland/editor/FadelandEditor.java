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
import com.fadeland.editor.ui.tileMenu.TileMenu;
import com.fadeland.editor.ui.tileMenu.TileMenuTools;
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

		this.inputMultiplexer.addProcessor(this.stage);

		Gdx.input.setInputProcessor(this.inputMultiplexer);
	}

	@Override
	public void render ()
	{
		if(Gdx.input.isKeyJustPressed(Input.Keys.Z) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
			undo();
		else if(Gdx.input.isKeyJustPressed(Input.Keys.R) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
			redo();

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

		if(this.getScreen() != null)
		    this.getScreen().resize(width, height);
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

    public Array<TileTool> getTileTools()
    {
    	TileMenu tileMenu;
    	if(getScreen() != null)
    		tileMenu = ((TileMap) getScreen()).tileMenu;
    	else
    		return null;

        if(tileMenu.selectedTiles.size > 0)
	        if(tileMenu.selectedTiles.first().tool != TileMenuTools.TILE)
    	    	return  null;
        return tileMenu.selectedTiles;
    }

	public TileTool getSpriteTool()
	{
		TileMenu tileMenu;
		if(getScreen() != null)
			tileMenu = ((TileMap) getScreen()).tileMenu;
		else
			return null;

		if(tileMenu.selectedTiles.size == 0)
			return null;
		if(tileMenu.selectedTiles.first().tool != TileMenuTools.SPRITE)
			return  null;
		return tileMenu.selectedTiles.first();
	}

	public void undo()
	{
		if(getScreen() != null)
		{
			TileMap tileMap = (TileMap) getScreen();
			tileMap.undo();
		}
	}

	public void redo()
	{
		if(getScreen() != null)
		{
			TileMap tileMap = (TileMap) getScreen();
			tileMap.redo();
		}
	}
}
