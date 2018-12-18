package com.fadeland.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.ui.AreYouSureDialog;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		final FadelandEditor fadelandEditor = new FadelandEditor();
		new LwjglApplication(fadelandEditor, config)
		{
			@Override
			public void exit()
			{
				if(fadelandEditor.maps.size == 0)
					super.exit();
				final LwjglApplication application = this;
				for(int i = 0; i < fadelandEditor.maps.size; i ++)
				{
					final int finalI = i;
					if(fadelandEditor.maps.get(finalI).changed)
					{
						new AreYouSureDialog("Save before closing " + fadelandEditor.maps.get(finalI).name + "?", fadelandEditor.maps.get(finalI).editor.stage, "", GameAssets.getUISkin())
						{
							@Override
							public void yes()
							{
								boolean closeApplicationAfterSaving = (fadelandEditor.maps.size == 1);
								fadelandEditor.fileMenu.save(fadelandEditor.maps.get(finalI), true, closeApplicationAfterSaving);
							}

							@Override
							public void no()
							{
								fadelandEditor.fileMenu.mapTabPane.removeMap(fadelandEditor.maps.get(finalI));
								if (fadelandEditor.maps.size == 0)
									application.exit();
							}
						};
					}
					else
					{
						i --;
						fadelandEditor.fileMenu.mapTabPane.removeMap(fadelandEditor.maps.get(finalI));
						if (fadelandEditor.maps.size == 0)
							application.exit();
					}
				}
			}
		};
	}
}
