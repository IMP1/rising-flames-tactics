package cls.map;

import java.util.HashMap;

import jog.Filesystem;

import cls.game.Actor.MovementType;

public class Map {
	
	private static class MovementCost {
		final MovementType moveType;
		final Integer cost;
		MovementCost(MovementType moveType, Integer cost) {
			this.moveType = moveType;
			this.cost = cost;
		}
	}
	private static MovementCost p(MovementType k, Integer v) {
		return new MovementCost(k, v);
	}
	
	public static enum Terrain {
		BLANK    (-1, -1, -1, -1), // This represents an absence of map.
		
		PLAIN    (0, 0, 0, 1),
		GRASS    (0, 0, 0, 1),
		FOREST   (1, 20, 0, 2, p(MovementType.HORSE, 3)),
		RIVER    (0, 0, 0, -1, p(MovementType.FOOT, 5), p(MovementType.BANDIT, 5), p(MovementType.HORSE, 5), p(MovementType.PIRATE, 2)),
		BRIDGE   (0, 0, 0, 1),
		FORT     (2, 20, 0.2, 2),
		PEAK     (2, 40, 0, -1, p(MovementType.BANDIT, 4), p(MovementType.FLYING, 1)),
		
		;
		public final String name;
		public final int defence;
		public final int avoid;
		public final double heal;
		private int defaultCost; // -1 means impassable.
		private HashMap<MovementType, Integer> moveCosts;
		private Terrain(int defence, int avoid, double heal, int defaultCost, MovementCost... terrainCosts) {
			moveCosts = new HashMap<MovementType, Integer>();
			for (MovementCost cost : terrainCosts) {
				moveCosts.put(cost.moveType, cost.cost);
			}
			this.defaultCost = defaultCost;
			this.defence = defence;
			this.avoid = avoid;
			this.heal = heal;
			name = getName();
		}
		public boolean canCross(MovementType moveType) {
			return getCost(moveType) >= 0;
		}
		public int getCost(MovementType moveType) {
			if (moveCosts.containsKey(moveType)) {
				return moveCosts.get(moveType);
			} else {
				return defaultCost;
			}
		}
		private String getName() {
			String[] words = this.toString().split("_");
			String name = "";
			for (String word : words) {
				name += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
			}
			return name;
		}
	}
	
	private final static HashMap<String, Terrain> TERRAIN_CODES = new HashMap<String, Terrain>();
	static {
		TERRAIN_CODES.put("null", Terrain.BLANK);
		TERRAIN_CODES.put("grs1", Terrain.PLAIN);
		TERRAIN_CODES.put("grs2", Terrain.GRASS);
		TERRAIN_CODES.put("rvr1", Terrain.RIVER);
		TERRAIN_CODES.put("brg1", Terrain.BRIDGE); // Bridge /
		TERRAIN_CODES.put("brg2", Terrain.BRIDGE); // Bridge \
		TERRAIN_CODES.put("brg3", Terrain.BRIDGE); // Bridge _
	}
	
	public final String title;
	private Hexagon[][] tiles;
	private int mapWidth;
	private int mapHeight;

	public Map(String filename) {
		String data = Filesystem.readFile(filename);
		title = data.substring(0, data.indexOf("\n"));
		populateTiles(data.substring(data.indexOf("\n")).trim());
	}
	
	public Map() {
		title = "Random Map";
		generateRandomMap(8, 8);
	}
	
	private void populateTiles(String data) {
		String[] rows = data.split("\\n");
		String[][] t = new String[rows.length][];
		for (int j = 0; j < rows.length; j ++) {
			String[] row = rows[j].split(" ");
			t[j] = new String[row.length];
			for (int i = 0; i < row.length; i ++) {
				t[j][i] = row[i];
			}
		}
		mapWidth = t[0].length;
		mapHeight = t.length;
		tiles = new Hexagon[mapHeight][mapWidth];
		for (int j = 0; j < rows.length; j ++) {
			tiles[j] = new Hexagon[t[j].length];
			for (int i = 0; i < tiles[j].length; i ++) {
				int x = i - mapWidth / 2;
				int y = j - (mapHeight / 2) - Math.min(0, j);
				Terrain terrain = TERRAIN_CODES.get(t[j][i]);
				tiles[j][i] = new Hexagon(x, y, terrain);
			}
		}
	}
	
	private void generateRandomMap(int width, int height) {
		mapWidth = width;
		mapHeight = height;
		tiles = new Hexagon[mapHeight][mapWidth];
		for (int j = 0; j < mapHeight; j ++) {
			tiles[j] = new Hexagon[mapWidth];
			for (int i = 0; i < tiles[j].length; i ++) {
				int x = i - mapWidth / 2;
				int y = j - (mapHeight / 2) - Math.min(0, j);
				int t = 1 + (int)(Math.random() * (Terrain.values().length - 1));
				Terrain terrain = Terrain.values()[t];
				tiles[j][i] = new Hexagon(x, y, terrain);
			}
		}
	}
	
	public Hexagon tileAt(int x, int y) {
		int i = x + mapWidth / 2;
		int j = y + (mapHeight / 2);
		if (j >= tiles.length) j /= 2;
		if (j >= 0 && j < tiles.length && i >= 0 && i < tiles[j].length) {
			return tiles[j][i];
		} else {
			return null;
		}
	}
	
	public void draw(int offsetX, int offsetY) {
		for (Hexagon[] row : tiles) {
			for (Hexagon h : row) {
				h.draw(offsetX, offsetY);
			}
		}
	}

}
