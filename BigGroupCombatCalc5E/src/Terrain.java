import java.util.ArrayList;

public class Terrain {
	
	public Creature occupant;
	private int moveReq;
	private ArrayList<Creature> bodies;
	
	public Terrain(){
		bodies = new ArrayList<Creature>();
		moveReq = 5;
	}
	
	public void addBody(Creature body){
		bodies.add(body);
		moveReq += 5;
	}
	
	public ArrayList<Creature> getBodies(){
		return bodies;
	}
	
	public int getMoveReq(){
		return moveReq;
	}

}
