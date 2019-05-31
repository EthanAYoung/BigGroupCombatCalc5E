
public class EffectMark extends PassiveEffect{
	
	private Creature target;
	private String name;
	private int bonusDamage;
	
	public EffectMark(){
		super();
	}
	
	private void applyEffects(Creature creature) {
		if(target.isDown() && !creature.bonusActions.contains("switch " + name)) {
			creature.bonusActions.add("switch " + name);
		}
	}

}
