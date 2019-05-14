import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Runner {

	private static Terrain[][] grid;
	private static int numOfFacT;
	private static int numOfFacF;
	private static Creature man1;
	private static Creature man2;
	
	public static void main(String[] args) {
		grid = populateGridTest2();
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
			System.out.println("**********BEGIN************");
			System.out.println("Jorn");
			man1.runDiagnostic();
			System.out.println("**********************");
			System.out.println("Bjorn");
			man2.runDiagnostic();
			System.out.println("**********END************");
		}
		
		getCasualtyReport();
		System.out.println("**********************");
		System.out.println("Jorn");
		man1.runDiagnostic();
		System.out.println("**********************");
		System.out.println("Bjorn");
		man2.runDiagnostic();

	}

	private static Terrain[][] populateGridTest() {
		grid = new Terrain[20][20];
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				grid[row][col] = new Terrain();
			}
		}
			
		Spearman Jorn = new Spearman();
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
		
		man1 = Jorn;
		man2 = Bjorn;
		
		return grid;
	}
	
	private static Terrain[][] populateGridTest2() {
		grid = new Terrain[20][20];
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				grid[row][col] = new Terrain();
			}
		}
		
		Infantry inf;
		Spearman spe;
		for (int col = 0; col < 20; col++){
			inf = new Infantry();
			setUpCreature(inf, 17, col, true);
			spe = new Spearman();
			setUpCreature(spe, 18, col, true);
			
			inf = new Infantry();
			setUpCreature(inf, 1, col, false);
			inf = new Infantry();
			setUpCreature(inf, 2, col, false);
		}
		

		Spearman Jorn = new Spearman();
		setUpCreature(Jorn, 19, 10, true);
		Infantry Bjorn = new Infantry();
		setUpCreature(Bjorn, 0, 10, false);
		
		man1 = Jorn;
		man2 = Bjorn;
		
		return grid;
	}
	
	private static void setUpCreature(Creature c, int row, int col, boolean fac){
		grid[row][col].occupant = c;
		c.setFaction(fac);
		modFacNum(fac, 1);
		c.setBoard(grid);
		c.setPos(row, col);
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
					if (grid[row][col].getBodies().get(i).isDead()){
						if (grid[row][col].getBodies().get(i).getFaction()){
							deadFacT++;
						}
						else {
							deadFacF++;
						}
					}
					else {
						if (grid[row][col].getBodies().get(i).getFaction()){
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
