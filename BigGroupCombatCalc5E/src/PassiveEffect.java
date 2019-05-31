
public class PassiveEffect {
	
	int duration;
	
	public PassiveEffect() {
		
	}
	
	public void activate(Creature creature) {
		duration--;
		applyEffects(creature);
	}
	
	private void applyEffects(Creature creature) {
		
	}

}
