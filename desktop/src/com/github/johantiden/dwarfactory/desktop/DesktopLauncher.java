package com.github.johantiden.dwarfactory.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.johantiden.dwarfactory.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 2880;
        config.height = 1620;

        config.title = "MyGdxGame";
        config.fullscreen = true;
        config.foregroundFPS = 30;
        LwjglApplication lwjglApplication = new LwjglApplication(new MyGdxGame(), config);

        Gdx.app.log("DesktopLauncher", "Started");


    }

}
