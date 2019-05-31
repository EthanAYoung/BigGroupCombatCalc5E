
public class Infantry extends Creature{
	
	public Infantry (){
		super();
		hp = 17;
		ac = 14;
		speed = 30;
		strength = 2;
		dexterity = 0;
		constitution = 1;
		intelligence = -1;
		wisdom = 0;
		charisma = -1;
		pasPerception = 10;
		proficiency = 2;
		hpMax = hp;
		
		damageDie = 8;
		range = 5;
		
		name = "Infantry";
		abbreviation = "IN";
	}

}
