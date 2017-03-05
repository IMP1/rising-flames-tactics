package run;

import scn.Title;
import jog.Filesystem;
import jog.Window.WindowMode;

public class Main extends Game {
	
	public static final boolean DEBUGGING = true;
	
	public static final String TITLE = "Rising Flames";
	public static final int WIDTH = 960;
	public static final int HEIGHT = 640;
	public static final int MIN_FPS = 15;

	public Main() {
		super(new Title(), TITLE, WIDTH, HEIGHT, MIN_FPS, WindowMode.WINDOWED);
	}
	
	@Override
	protected void initialise(int width, int height, String title, WindowMode windowMode) {
		super.initialise(width, height, title, windowMode);
		Filesystem.addLocation("dat");
		Data.load();
	}

	public static void main(String... args) {
		new Main();
	}
	
}
