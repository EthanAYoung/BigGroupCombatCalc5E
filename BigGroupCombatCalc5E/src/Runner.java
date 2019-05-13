import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Runner {

	private Terrain[][] grid;
	private int numOfFacT;
	private int numOfFacF;
	
	public void main(String[] args) {
		grid = populateGridTest();
		Queue<Creature> order = getIntOrder();
		
		Creature curr;
		while(numOfFacT > 0 && numOfFacF > 0){
			curr = order.remove();
			curr.takeTurn();
			if (curr.isDefeated()){
				modFacNum(curr.getFaction(), -1);
			}
			else {
				order.add(curr);
			}
		}

	}

	private Terrain[][] populateGridTest() {
		grid = new Terrain[20][20];
		Infantry Jorn = new Infantry();
		grid[19][10].occupant = Jorn;
		Jorn.setFaction(true);
		modFacNum(true, 1);
		Infantry Bjorn = new Infantry();
		grid[0][10].occupant = Bjorn;
		Bjorn.setFaction(false);
		modFacNum(false, 1);
		return grid;
	}
	
	public Terrain[][] getGrid(){
		return grid;
	}
	
	private void modFacNum(Boolean fac, int mod){
		if (fac){
			numOfFacT += mod;
		}
		else {
			numOfFacF += mod;
		}
	}
	
	private Queue<Creature> getIntOrder(){
		PriorityQueue<Creature> sorter = new PriorityQueue<Creature>();
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				if (grid[row][col].occupant != null){
					grid[row][col].occupant.rollInitiative();
					sorter.add(grid[row][col].occupant);
				}
			}
		}
		
		Queue<Creature> order = new LinkedList<>();
		while (!sorter.isEmpty()){
			order.add(sorter.remove());
		}
		
		return order;
		
	}

}
