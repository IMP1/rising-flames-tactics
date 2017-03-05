package scn;

import java.awt.Color;
import java.awt.event.KeyEvent;

import cls.game.Unit;
import cls.map.Hexagon;
import run.Data;
import run.Vocab;

public class Battle extends Scene {

	public enum Result {
		SUCCESS,
		FAILURE,
	}
	
	private final int MAP_OFFSET_X = jog.Window.getWidth() / 2;
	private final int MAP_OFFSET_Y = jog.Window.getHeight() / 2;
	
	private boolean battleEnded;
	private Result result;
	
	private String name;
	private cls.map.Map map;
	private java.util.ArrayList<cls.game.Unit> playerUnits;
	private java.util.ArrayList<cls.game.Unit> enemyUnits;
	private java.util.ArrayList<cls.game.Unit> neutralUnits;
	private cls.game.Unit selectedUnit;
	private String levelFilename;
	
	public Battle(String level) {
		levelFilename = level;
	}
	
	public void start() {
		loadLevel(levelFilename);
		result = null;
		playerUnits.get(0).damage(1);
	}
	
	private void loadLevel(String level) {
		playerUnits = new java.util.ArrayList<cls.game.Unit>();
		enemyUnits = new java.util.ArrayList<cls.game.Unit>();
		neutralUnits = new java.util.ArrayList<cls.game.Unit>();
		String[] levelData = jog.Filesystem.readFile(level).split("\n");
		name = levelData[0];
		loadMap(levelData[1]);
		
		int i;
		for (i = 2; i < levelData.length && !levelData[i].isEmpty(); i ++) {
			addPlayerUnit(levelData[i]);
		}
		i += 1; // Skip the blank line
		for (; i < levelData.length && !levelData[i].isEmpty(); i ++) {
			
		}
	}
	
	private void loadMap(String mapFile) {
		map = new cls.map.Map(mapFile);
	}
	
	private void addPlayerUnit(String line) {
		String[] unitData = line.split(" "); 
		String unitName = unitData[0];
		int unitStartX = Integer.parseInt(unitData[1]);
		int unitStartY = Integer.parseInt(unitData[2]);
		cls.game.Actor a = Data.getActor(unitName);
		if (a == null) {
			throw new Data.MissingDataException("There is no actor with the name '" + unitName + "'.");
		} else {
			playerUnits.add(new Unit(Data.getActor(unitName), unitStartX, unitStartY));
		}
	}

	@Override
	public void update(double dt) {
		if (battleEnded) {
			SceneManager.returnScene();
		}
	}

	@Override
	public void draw() {
		drawMap();
		drawUnits();
		drawGUI();
	}
	
	private int[] mouseCoordinates() {
		return mouseCoordinates(jog.Input.getMouseX(), jog.Input.getMouseY());
	}
	
	private int[] mouseCoordinates(int mx, int my) {
		return new int[] { mx - MAP_OFFSET_X, my - MAP_OFFSET_Y };
	}
	
	private void drawMap() {
		map.draw(MAP_OFFSET_X, MAP_OFFSET_Y);
	}
	
	private void drawUnits() {
		for (Unit u : playerUnits) {
			u.draw(MAP_OFFSET_X, MAP_OFFSET_Y);
		}
		for (Unit u : enemyUnits) {
			u.draw(MAP_OFFSET_X, MAP_OFFSET_Y);
		}
		for (Unit u : neutralUnits) {
			u.draw(MAP_OFFSET_X, MAP_OFFSET_Y);
		}
	}
	
	private void drawGUI() {
		jog.Graphics.print(name, 0, 0);
		int[] m = mouseCoordinates();
		int[] a = Hexagon.getAxial(m);
		drawGUITile(a[0], a[1]);
		drawGUIUnit(a[0], a[1]);
	}
	
	private void drawGUITile(int i, int j) {
		Hexagon t = map.tileAt(i, j);
		if (t == null || t.getTerrainType() == cls.map.Map.Terrain.BLANK) return;
		int w = 96;
		int h = 96;
		int x = 8;
		int y = jog.Window.getHeight() - h - 8; 
		jog.Graphics.rectangle(false, x, y, w, h);
		jog.Graphics.printCentred(t.getTerrainType().name, x + w/2, y + 4);
		jog.Graphics.print(Vocab.Terrain.DEF_S, x + 8, y + 16);
		jog.Graphics.print(String.valueOf(t.getTerrainType().defence), x + 8, y + 32);
		jog.Graphics.print(Vocab.Terrain.AVO_S, x + 32 + 8, y + 16);
		jog.Graphics.print(String.valueOf(t.getTerrainType().avoid), x + 32 + 8, y + 32);
		jog.Graphics.print(Vocab.Terrain.HEA_S, x + 64 + 8, y + 16);
		jog.Graphics.print(String.valueOf(t.getTerrainType().heal), x + 64 + 8, y + 32);
	}
	
	private void drawGUIUnit(int i, int j) {
		drawGUIUnitHover(i, j);
		drawGUIUnitSelected();
	}
	
	private void drawGUIUnitHover(int i, int j) {
		Unit u = unitAt(i, j);
		if (u == null) return;
		int w = 256;
		int h = 96;
		int x = jog.Window.getWidth() - w - 8;
		int y = jog.Window.getHeight() - h - 8;
		jog.Graphics.rectangle(false, x, y, w, h);
		u.drawFace(x + w - 64, y + 96);
		jog.Graphics.printCentred(u.getActor().getName(), x + w / 2, y + 4);
		drawHealth(x + 8, y + h - 24, 108, 16, u);
	}
	
	private void drawGUIUnitSelected() {
		if (selectedUnit == null) return;
		
	}
	
	private void drawHealth(int x, int y, int w, int h, Unit u) {
		Color empty = new Color(64, 0, 0);
		Color filled = new Color(192, 0, 0);
		drawBar(x, y, w, h, u.getHealth(), u.getActor().getMaxHealth(), empty, filled);
	}
	
	private void drawBar(int x, int y, int w, int h, int value, int max, Color emptyColour, Color filledColour) {
		int fullness = (w * value / max);
		jog.Graphics.setColour(emptyColour);
		jog.Graphics.rectangle(true, x, y, w, h);
		jog.Graphics.setColour(filledColour);
		jog.Graphics.rectangle(true, x+1, y+1, fullness - 2, h - 2);
		jog.Graphics.setColour(Color.WHITE);
		String msg = String.valueOf(value);
		if (value != max) {
			msg += " / " + String.valueOf(max);
		}
		jog.Graphics.print(msg, x + fullness, y);
	}
	
	public Unit unitAt(int i, int j) {
		for (Unit u : playerUnits) {
			if (u.isAt(i, j)) return u;
		}
		for (Unit u : enemyUnits) {
			if (u.isAt(i, j)) return u;
		}
		for (Unit u : neutralUnits) {
			if (u.isAt(i, j)) return u;
		}
		return null;
	}
	
	@Override
	public void keyReleased(int key) {
		if (key == KeyEvent.VK_END) {
			System.out.println("There is no cow level");
			battleEnded = true;
		}
	}


}
