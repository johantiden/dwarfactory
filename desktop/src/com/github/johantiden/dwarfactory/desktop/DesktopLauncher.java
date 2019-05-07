package com.github.johantiden.dwarfactory.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.johantiden.dwarfactory.Dwarfactory;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 2880;
        config.height = 1620;

        config.title = "Dwarfactory";
//        config.fullscreen = true;
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;
        LwjglApplication lwjglApplication = new LwjglApplication(new Dwarfactory(), config);

        Gdx.app.log("DesktopLauncher", "Started");
    }

}
