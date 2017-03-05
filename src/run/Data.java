package run;

import java.util.HashMap;

import cls.Weapon;
import cls.game.Actor;

public final class Data {
	private Data() {}
	
	public static class MissingDataException extends RuntimeException {
		private static final long serialVersionUID = -7046121332447383903L;
		public MissingDataException(String message) {
			super(message);
		}
	}

	public static Actor[] actors = new Actor[0];

	public static Actor getActor(String name) {
		for (Actor a : actors) {
			if (a.getName().equals(name)) return a;
		}
		return null;
	}
	
	public static void load() {
		loadActors();
	}
	
	private static void loadActors() {
		java.util.ArrayList<Actor> dataActors = new java.util.ArrayList<Actor>();
		// Enumerate through dat/actors
		// Create actors, populate table
		for (String filename : jog.Filesystem.enumerate("dat/actors")) {
			Actor a = readActor("dat/actors/" + filename);
			dataActors.add(a);
		}
		actors = dataActors.toArray(actors);
	}
	
	private static Actor readActor(String filename) {
		String[] actorData = jog.Filesystem.readFile(filename).split("\n");
		String name = actorData[0];
		Actor.Class actorClass = Actor.Class.valueOf(actorData[1]);
		String[] baseStatData = actorData[2].split(" ");
		String[] growthStatData = actorData[3].split(" ");
		String[] weaponStatData = actorData[4].split(" ");
		int[] baseStats = new int[baseStatData.length];
		for (int i = 0; i < baseStatData.length; i ++) {
			baseStats[i] = Integer.valueOf(baseStatData[i]);
		}
		double[] growthStats = new double[growthStatData.length];
		for (int i = 0; i < growthStatData.length; i ++) {
			growthStats[i] = Double.valueOf(growthStatData[i]);
		}
		HashMap<Weapon.Category, Weapon.Rank> weaponStats = new HashMap<Weapon.Category, Weapon.Rank>();
		for (int i = 0; i < weaponStatData.length; i ++) {
			String[] w = weaponStatData[i].split(":"); 
			weaponStats.put(Weapon.Category.valueOf(w[0]), Weapon.Rank.valueOf(w[1]));
		}
		return new Actor(name, actorClass, baseStats, growthStats, weaponStats);
	}

}
