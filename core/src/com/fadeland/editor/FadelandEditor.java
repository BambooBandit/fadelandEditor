package com.fadeland.editor;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.AreYouSureDialog;
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

	public static boolean fileChooserOpen = false;

	public static Preferences prefs;

	public int randomSpriteIndex;

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

		prefs = Gdx.app.getPreferences("Editor preferences");
	}

	@Override
	public void render ()
	{
		try{
			fileMenu.toolPane.fps.setText(Gdx.graphics.getFramesPerSecond());
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

			stage.act();
			stage.draw();
		} catch(Exception e){
			crashRecovery();
		}
	}

	public void crashRecovery()
	{
		if(maps.size == 0)
			Gdx.app.exit();
		for(int i = 0; i < maps.size; i ++)
		{
			final int finalI = i;
			new AreYouSureDialog("Editor crashed. Save before closing " + maps.get(finalI).name + "?", maps.get(finalI).editor.stage, "", GameAssets.getUISkin(), false)
			{
				@Override
				public void yes()
				{
					boolean closeApplicationAfterSaving = (maps.size == 1);
					fileMenu.saveAs(maps.get(finalI), true, closeApplicationAfterSaving);
				}

				@Override
				public void no()
				{
					fileMenu.mapTabPane.removeMap(maps.get(finalI));
					if (maps.size == 0)
						Gdx.app.exit();
				}
			};
		}
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

	public Array<TileTool> getSpriteTools()
	{
		TileMenu tileMenu;
		if(getScreen() != null)
			tileMenu = ((TileMap) getScreen()).tileMenu;
		else
			return null;

		if(tileMenu.selectedTiles.size > 0)
			if(tileMenu.selectedTiles.first().tool != TileMenuTools.SPRITE)
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
		if(fileMenu.toolPane.random.selected && randomSpriteIndex < tileMenu.selectedTiles.size)
			return tileMenu.selectedTiles.get(randomSpriteIndex);
		return tileMenu.selectedTiles.first();
	}

	public void shuffleRandomSpriteTool()
	{
		TileMenu tileMenu;
		if(getScreen() != null)
			tileMenu = ((TileMap) getScreen()).tileMenu;
		else
			return;

		if(getSpriteTools() == null)
			return;

		// Randomly pick a sprite from the selected sprites based on weighted probabilities
		float totalSum = 0;
		float partialSum = 0;
		for(int i = 0; i < getSpriteTools().size; i ++)
			totalSum += Float.parseFloat(getSpriteTools().get(i).getPropertyField("Probability").value.getText());
		float random = Utils.randomFloat(0, totalSum);
		for(int i = 0; i < getSpriteTools().size; i ++)
		{
			partialSum += Float.parseFloat(getSpriteTools().get(i).getPropertyField("Probability").value.getText());
			if(partialSum >= random)
			{
				randomSpriteIndex = i;
				break;
			}
		}
		fileMenu.toolPane.minMaxDialog.generateRandomValues();
		if(getSpriteTool() != null)
		{
			TileTool spriteTool = getSpriteTool();
			Vector3 coords = Utils.unproject(((TileMap)getScreen()).camera, Gdx.input.getX(), Gdx.input.getY());
			for (int i = 0; i < getSpriteTool().previewSprites.size; i++)
			{
				float randomScale = fileMenu.toolPane.minMaxDialog.randomSizeValue;
				spriteTool.previewSprites.get(i).setScale(randomScale, randomScale);
				spriteTool.previewSprites.get(i).setPosition(coords.x - spriteTool.previewSprites.get(i).getWidth() / 2, coords.y - spriteTool.previewSprites.get(i).getHeight() / 2);
			}
		}
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
