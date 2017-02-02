package com.quadtree.simulation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.quadtree.simulation.MyMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 1280;
        config.height = 720;
        config.title = "QuadTree Simulation - LibGDX";
		new LwjglApplication(new MyMain(), config);
	}
}
