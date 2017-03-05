package cls;

import cls.game.Actor;

public class Weapon {
	
	public enum Rank {
		E, D, C, B, A, S;
	}
	
	public enum Category {
		SWORD,
		LANCE,
		AXE,
		BOW,
		DARK,
		LIGHT,
		ELEMENTAL,
		STAFF;
		public boolean isPhysical() {
			return this == SWORD ||
				   this == LANCE || 
				   this == AXE   || 
				   this == BOW;
		}
		public boolean isMagical() {
			return this == DARK      ||
				   this == LIGHT     ||
				   this == ELEMENTAL ||
				   this == STAFF;
		}
	}
	
	private Category type;
	private Rank requiredRank;

	public Weapon() {
		
	}
	
	public int getBonusMight(Actor wielder) {
		switch(type) {
			case SWORD:
				if (wielder.getWeaponProficiency(this) == Rank.C) return 1;
				if (wielder.getWeaponProficiency(this) == Rank.B) return 2;
				if (wielder.getWeaponProficiency(this) == Rank.A) return 3;
				break;
			case LANCE: case BOW: case DARK: case LIGHT: case ELEMENTAL:
				if (wielder.getWeaponProficiency(this) == Rank.C) return 1;
				if (wielder.getWeaponProficiency(this) == Rank.B) return 1;
				if (wielder.getWeaponProficiency(this) == Rank.A) return 2;
				break;
			default:
				break;
		}
		return 0;
	}
	
	public int getBonusAccuracy(Actor wielder) {
		switch(type) {
			case AXE:
				if (wielder.getWeaponProficiency(this) == Rank.C) return 5;
				if (wielder.getWeaponProficiency(this) == Rank.B) return 10;
				if (wielder.getWeaponProficiency(this) == Rank.A) return 15;
				break;
			case LANCE: case BOW: case DARK: case LIGHT: case ELEMENTAL:
				if (wielder.getWeaponProficiency(this) == Rank.B) return 5;
				if (wielder.getWeaponProficiency(this) == Rank.A) return 5;
				break;
			default:
				break;
		}
		return 0;
	}
	
	public int getWeight() {
		return 0;
	}
	
	public double getAccuracy() {
		return 1.0;
	}
	
	public Rank getRequiredRank() {
		return requiredRank;
	}
	
	public Category getCategory() {
		return type;
	}
	
	public boolean isPhysical() {
		return type.isPhysical();
	}
	
	public boolean isMagical() {
		return type.isMagical();
	}

}
