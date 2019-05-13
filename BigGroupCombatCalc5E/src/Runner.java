import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Runner {

	private static Terrain[][] grid;
	private static int numOfFacT;
	private static int numOfFacF;
	
	public static void main(String[] args) {
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
		
		getCasualtyReport();

	}

	private static Terrain[][] populateGridTest() {
		grid = new Terrain[20][20];
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				grid[row][col] = new Terrain();
			}
		}
			
		Infantry Jorn = new Infantry();
		grid[19][10].occupant = Jorn;
		Jorn.setFaction(true);
		modFacNum(true, 1);
		Jorn.setBoard(grid);
		Jorn.setPos(19, 10);
		Infantry Bjorn = new Infantry();
		grid[0][10].occupant = Bjorn;
		Bjorn.setFaction(false);
		modFacNum(false, 1);
		Bjorn.setBoard(grid);
		Bjorn.setPos(0, 10);
		return grid;
	}
	
	public Terrain[][] getGrid(){
		return grid;
	}
	
	private static void modFacNum(Boolean fac, int mod){
		if (fac){
			numOfFacT += mod;
		}
		else {
			numOfFacF += mod;
		}
	}
	
	private static Queue<Creature> getIntOrder(){
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
		sorter = null;
		
		return order;
		
	}
	
	private static void getCasualtyReport(){
		String winner;
		if(numOfFacT > numOfFacF){
			winner = "FacT";
		}
		else {
			winner = "FacF";
		}
		System.out.println("The winner is " + winner);
		
		int deadFacT = 0;
		int stableFacT = 0;
		int deadFacF = 0;
		int stableFacF = 0;
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				for (int i = 0; i < grid[row][col].getBodies().size(); i++){
					if(grid[row][col].getBodies().get(i).isDead()){
						if(grid[row][col].getBodies().get(i).getFaction()){
							deadFacT++;
						}
						else {
							deadFacF++;
						}
					}
					else {
						if(grid[row][col].getBodies().get(i).getFaction()){
							stableFacT++;
						}
						else {
							stableFacF++;
						}
					}
				}
			}
		}
		
		System.out.println("FacT - Dead: " + deadFacT + " Stable: " +stableFacT);
		System.out.println("FacF - Dead: " + deadFacF + " Stable: " +stableFacF);
	}

}
