package cls.game;

import java.util.HashMap;

import cls.Weapon;

public class Actor {
	
	public static enum MovementType {
		FOOT,
		HEAVY,
		ARMOUR,
		MAGE,
		HORSE,
		FLYING,
		BANDIT,
		PIRATE,
	}
	
	public static enum Class {
		LORD         (MovementType.FOOT,   1.0),
		THIEF        (MovementType.FOOT,   1.0),
		FIGHTER      (MovementType.HEAVY,  1.0),
		RANGER       (MovementType.HORSE,  1.0),
		CAVALIER     (MovementType.HORSE,  1.0),
		KNIGHT       (MovementType.ARMOUR, 1.0),
		SHAMAN       (MovementType.MAGE,   1.0),
		SAGE         (MovementType.MAGE,   1.0),
		ARCHER       (MovementType.FOOT,   1.0),
		WYVERN_RIDER (MovementType.FLYING, 1.0),
		CORSAIR      (MovementType.PIRATE, 1.0),
		;
		public final String name;
		public final MovementType moveType;
		public final double strength; // Used to determine experience gains.
		private Class(MovementType moveType, double strength) {
			this.moveType = moveType;
			this.strength = strength;
			this.name = getName();
		};
		private String getName() {
			String[] words = this.toString().split("_");
			String name = "";
			for (String word : words) {
				name += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
			}
			return name;
		}
	}
	
	/* Base Attributes */
	private int maxHealth;      // Starting health at the beginning of a battle.
	private int moveDistance;   // How far away (in tiles) is reachable in one turn. 
	private int visionDistance; // How far away (in tiles) is visible at any point.
	/* Base Stats */
	private int baseStrength;   // Effectiveness of physical attacks.
	private int baseDefence;    // Resistance to physical attacks.
	private int baseMagic;      // Effectiveness of magical attacks.
	private int baseResistance; // Resistance to magical attacks.
	private int baseSpeed;      // Speed determines who attacks first (and double attacks).
	private int baseSkill;      // Skill determines critical-hit likelihoods.
	/* Weapon Stats */
	private HashMap<Weapon.Category, Weapon.Rank> weaponProficienies; // Proficiency (damage multiplier) with Weapon Categories.
	/* Growth Stats */
	private double growthHealth;     // Probability that health will increase on level-up.
	private double growthStrength;   // Probability that strength will increase on level-up.
	private double growthDefence;    // Probability that defence will increase on level-up.
	private double growthMagic;      // Probability that magic will increase on level-up.
	private double growthResistance; // Probability that resistance will increase on level-up.
	private double growthSpeed;      // Probability that speed will increase on level-up.
	private double growthSkill;      // Probability that skill will increase on level-up.
	private double growthMove;       // Probability that move will increase on level-up.
	private double growthVision;     // Probability that vision will increase on level-up.

	private Weapon equippedWeapon;
	private Actor.Class actorClass;
	private String name;
	private int level;
	protected double experience;
	protected int levelsToGain;
	
	private void initialiseStats() {
		initialiseBaseAttributes();
		initialiseBaseStats();
		initialiseGrowthStats();
		initialiseWeaponProficiencies();
	}
	
	private void initialiseBaseAttributes() {
		name           = "";
		maxHealth      = 10;
		moveDistance   = 3;
		visionDistance = 4;
		level          = 1;
		experience     = 0;
	}
	
	private void initialiseBaseStats() {
		baseStrength   = 1;
		baseDefence    = 1;
		baseMagic      = 1;
		baseResistance = 1;
		baseSpeed      = 1;
		baseSkill      = 1;
	}
	
	private void initialiseGrowthStats() {
		growthHealth     = 1.0;
		growthStrength   = 1.0;
		growthDefence    = 1.0;
		growthMagic      = 1.0;
		growthResistance = 1.0;
		growthSpeed      = 1.0;
		growthSkill      = 1.0;
		growthMove       = 1.0;
		growthVision     = 1.0;
	}
	
	private void initialiseWeaponProficiencies() {
		weaponProficienies = new HashMap<Weapon.Category, Weapon.Rank>();
	}
	
	public Actor() {
		initialiseStats();
	}
	
	public Actor(String name, Actor.Class actorClass, int[] baseStats, double[] growthStats, HashMap<Weapon.Category, Weapon.Rank> weaponProfs) {
		this.name = name;
		this.actorClass = actorClass;
		// Stats
		maxHealth      = baseStats[0];
		moveDistance   = baseStats[1];
		visionDistance = baseStats[2];
		baseStrength   = baseStats[3];
		baseDefence    = baseStats[4];
		baseMagic      = baseStats[5];
		baseResistance = baseStats[6];
		baseSpeed      = baseStats[7];
		baseSkill      = baseStats[8];
		// Growth Stats
		growthHealth     = growthStats[0];
		growthStrength   = growthStats[1];
		growthDefence    = growthStats[2];
		growthMagic      = growthStats[3];
		growthResistance = growthStats[4];
		growthSpeed      = growthStats[5];
		growthSkill      = growthStats[6];
		growthMove       = growthStats[7];
		growthVision     = growthStats[8];
		// Weapon Proficiencies
		weaponProficienies = new HashMap<Weapon.Category, Weapon.Rank>();
		weaponProficienies.putAll(weaponProfs);
	}
	
	public double getExperienceGain(Actor enemy) {
		double relativeStrength = enemy.actorClass.strength / this.actorClass.strength;
		return relativeStrength * 10;
	}
	
	private double getWeaponWeight() {
		if (equippedWeapon == null) {
			return 0;
		} else {
			return equippedWeapon.getWeight();
		}
	}
	
	private double getWeaponAccuracy() {
		if (equippedWeapon == null) {
			return 0;
		} else {
			return equippedWeapon.getAccuracy();
		}
	}
	
	public boolean canUseWeapon(Weapon w) {
		if (weaponProficienies.containsKey(w.getCategory())) {
			return getWeaponProficiency(w).compareTo(w.getRequiredRank()) >= 0;
		} else {
			return false;
		}
	}
	
	public Weapon.Rank getWeaponProficiency(Weapon w) {
		return weaponProficienies.get(w.getCategory());
	}
	
	public int getWeaponBonusAccuracy() {
		if (equippedWeapon == null) {
			return 0;
		} else {
			return equippedWeapon.getBonusAccuracy(this);
		}
	}
	
	public int getWeaponBonusMight() {
		if (equippedWeapon == null) {
			return 0;
		} else {
			return equippedWeapon.getBonusMight(this);
		}
	}
	
	public Actor.Class getClassType() {
		return actorClass;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getMoveDistance() {
		return moveDistance;
	}
	
	public int getVisionDistance() {
		return visionDistance;
	}
	
	public int getDefence() {
		return baseDefence;
	}
	
	public int getResistance() {
		return baseResistance;
	}
	
	public int getSpeed() {
		return baseSpeed;
	}
	
	public int getSkill() {
		return baseSkill;
	}
	
	public int getStrength() {
		return baseStrength;
	}
	
	public int getMagic() {
		return baseMagic;
	}
	
	public double getAttackSpeed() {
		int strength;
		if (equippedWeapon.isMagical()) {
			strength = getMagic();
		} else {
			strength = getStrength();
		}
		return Math.max(getSpeed(), getSpeed() - (getWeaponWeight() - strength));
	}
	
	public double getHitRate() {
		return getWeaponAccuracy() + getSkill() * 2 + getWeaponBonusAccuracy() / 2;
	}
	
	public double getEvasionRate() {
		return getAttackSpeed() * 2;
	}
	
	public String getName() {
		return name;
	}
	
	public void levelUp() {
		if (Math.random() < growthHealth)     maxHealth ++;
		if (Math.random() < growthStrength)   baseStrength ++;
		if (Math.random() < growthDefence)    baseDefence ++;
		if (Math.random() < growthMagic)      baseMagic ++;
		if (Math.random() < growthResistance) baseResistance ++;
		if (Math.random() < growthSpeed)      baseSpeed ++;
		if (Math.random() < growthSkill)      baseSkill ++;
		if (Math.random() < growthMove)       moveDistance ++;
		if (Math.random() < growthVision)     visionDistance ++;
		levelsToGain --;
	}
	
}
