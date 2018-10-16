package com.fadeland.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fadeland.editor.ui.FileMenu;

public class FadelandEditor extends Game
{
	private static GameAssets gameAssets;
	private SpriteBatch batch;
	private Stage stage;
	private FileMenu fileMenu;

	@Override
	public void create ()
	{
		gameAssets = GameAssets.get();
		this.batch = new SpriteBatch();
		this.stage = new Stage(new ScreenViewport());

		this.fileMenu = new FileMenu(GameAssets.getUISkin());
		this.fileMenu.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.fileMenu.setPosition(Gdx.graphics.getWidth() / 2 - this.fileMenu.getWidth() / 2, Gdx.graphics.getHeight() / 2 - this.fileMenu.getHeight() / 2);
		this.fileMenu.setVisible(true);
		this.stage.addActor(this.fileMenu);

		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.batch.begin();
		this.batch.end();

		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		this.stage.getViewport().update(width, height, true);
		this.fileMenu.setSize(stage.getWidth(), stage.getHeight());
	}

	@Override
	public void dispose ()
	{
		this.batch.dispose();
	}
}
