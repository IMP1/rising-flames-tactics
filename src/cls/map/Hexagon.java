package cls.map;

import cls.map.Map.Terrain;

public class Hexagon {

	public final static double ROOT_3 = Math.sqrt(3);
	
	public final static double SIZE = 24;
	
	protected final static double HEIGHT = SIZE * 2;
	protected final static double VERT = 3 * HEIGHT / 4;
	
	protected final static double WIDTH = HEIGHT * ROOT_3 / 2;
	protected final static double HORZ = WIDTH;
	
	private final static int[][] NEIGHBOURS = { 
		{1, -1}, { 1, 0}, {0,  1},
		{-1, 1}, {-1, 0}, {0, -1},
	};
	
	public static Hexagon getNeighbour(Hexagon[][] map, Hexagon hex, int direction) {
		int[] d = NEIGHBOURS[direction];
		return map[hex.j + d[1]][hex.i + d[0]];
	}
	
	public static int[] getAxial(int x, int y) {
		double ai = ((1.0/3.0) * ROOT_3 * x - (1.0/3.0) * y) / SIZE;
		double aj = (2.0/3.0) * y / SIZE;
		double ak = -(ai + aj);
		
		int ri = (int)Math.round(ai);
		int rj = (int)Math.round(aj);
		int rk = (int)Math.round(ak);
		
		double di = Math.abs(ri - ai);
		double dj = Math.abs(rj - aj);
		double dk = Math.abs(rk - ak);
		
		if (di > dj && di > dk) {
			ri = -(rj + rk);
		} else if (dj > dk) {
			rj = -(ri + rk);
		} else {
			rk = -(ri + rj);
		}
		return new int[] {ri, rj};
	}
	
	public static int[] getAxial(int[] coordinates) {
		return getAxial(coordinates[0], coordinates[1]);
	}
	
	public static int[] getPixel(int i, int j) {
		double x = HORZ * (i + j / 2.0);
		double y = VERT * j;
		
		int rx = (int)x;
		int ry = (int)y;
		return new int[] {rx, ry};
	}
	
	private int i, j;
	private Map.Terrain terrainType;
	
	public Hexagon(int i, int j, Map.Terrain terrainType) {
		this.i = i;
		this.j = j;
		this.terrainType = terrainType;
	}
	
	public int getPixelX() {
		return Hexagon.getPixel(i, j)[0];
	}
	
	public int getPixelY() {
		return Hexagon.getPixel(i, j)[1];
	}
	
	public int distance(Hexagon hex) {
		int k = -(i + j);
		int hex_k = -(hex.i + hex.j);
		return (Math.abs(i - hex.i) + Math.abs(j - hex.j) + Math.abs(k - hex_k)) / 2;
	}
	
	public void draw(int offsetX, int offsetY) {
		if (terrainType == Terrain.BLANK) return;
		if (terrainType == Terrain.RIVER) {
			jog.Graphics.setColour(0, 64, 255);
		}
		if (terrainType == Terrain.PLAIN) {
			jog.Graphics.setColour(128, 128, 48);
		}
		if (terrainType == Terrain.GRASS) {
			jog.Graphics.setColour(64, 128, 48);
		}
		if (terrainType == Terrain.BRIDGE) {
			jog.Graphics.setColour(64, 32, 0);
		}
		if (terrainType == Terrain.FOREST) {
			jog.Graphics.setColour(0, 64, 0);
		}
		if (terrainType == Terrain.PEAK) {
			jog.Graphics.setColour(48, 16, 0);
		}
		if (terrainType == Terrain.FORT) {
			jog.Graphics.setColour(64, 64, 64);
		}
		jog.Graphics.circle(true, getPixelX() + offsetX, getPixelY() + offsetY, SIZE);
		jog.Graphics.setColour(255, 255, 255);
		jog.Graphics.circle(false, getPixelX() + offsetX, getPixelY() + offsetY, SIZE);
	}
	
	public Terrain getTerrainType() {
		return terrainType;
	}

}
