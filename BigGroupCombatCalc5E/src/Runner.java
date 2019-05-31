import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Runner {

	private static Terrain[][] grid;
	private static int numOfFacT;
	private static int numOfFacF;
	private static Creature man1;
	private static Creature man2;
	private static Queue<Creature> order;
	private static ButtonGrid buttonGrid;
	
	public static void main(String[] args) {
		grid = populateGridTest2();
		order = getIntOrder();
		
		playerPlay();
		//autoPlay();
		
		getCasualtyReport();
		System.out.println("**********************");
		System.out.println("Jorn");
		man1.runDiagnostic();
		System.out.println("**********************");
		System.out.println("Bjorn");
		man2.runDiagnostic();
	}
	
	private static void autoPlay(){
		Creature curr;
		int[] beforeLoc;
		int[] afterLoc;
		while(numOfFacT > 0 && numOfFacF > 0){
			curr = order.remove();
			
			beforeLoc = curr.getLocation();	
			curr.takeTurn();
			afterLoc = curr.getLocation();
			
			if (curr.isDefeated()){
				modFacNum(curr.getFaction(), -1);
				buttonGrid.setText(afterLoc[0], afterLoc[1], "");
			}
			else {
				order.add(curr);
				updateCreatureText(curr);
			}
			
			if (grid[beforeLoc[0]][beforeLoc[1]].occupant == null){
				buttonGrid.setText(beforeLoc[0], beforeLoc[1], "");
			}
			
			System.out.println("**********BEGIN************");
			System.out.println("Jorn");
			man1.runDiagnostic();
			System.out.println("**********************");
			System.out.println("Bjorn");
			man2.runDiagnostic();
			System.out.println("**********END************");
		}
	}
	
	private static void playerPlay(){
		man1.setAsPlayerChar();
		Creature curr;
		int[] beforeLoc;
		int[] afterLoc;
		while(numOfFacT > 0 && numOfFacF > 0){
			curr = order.remove();
			//System.out.println("Before: " + beforeLoc[0] + ", " + beforeLoc[1]);
			
			if (curr.isDown()){
				updateCreatureText(curr);
			}
			
			if (curr.isPlayerChar()){
				beforeLoc = takePlayerTurn(curr);
			}
			else {
				beforeLoc = curr.getLocation();
				curr.takeTurn();
			}
			afterLoc = curr.getLocation();
			//System.out.println("After: " + afterLoc[0] + ", " + afterLoc[1]);
			
			if (curr.isDefeated()){
				removeCreature(curr);
			}
			else {
				order.add(curr);
				updateCreatureText(curr);
			}
			
			if (grid[beforeLoc[0]][beforeLoc[1]].occupant == null){
				buttonGrid.setText(beforeLoc, "");
			}
			
			updateDownStatus(curr.opponent);
			
			/*
			System.out.println("**********BEGIN************");
			System.out.println("Jorn");
			man1.runDiagnostic();
			System.out.println("**********************");
			System.out.println("Bjorn");
			man2.runDiagnostic();
			System.out.println("**********END************");
			*/
		}
	}

	private static int[] takePlayerTurn(Creature player){
		int[] beforeLoc = player.getLocation();
		if (player.startTurn()) {
			Scanner in = new Scanner(System.in);
			String input;
			boolean repeat;
			int commaInd;
			int row;
			int col;
			
			if (player.isProne()){
				System.out.println("You are prone. Would you like to stand up? (yes/no)");
				input = in.nextLine();
				if (input.toLowerCase().startsWith("y")){
					player.standUp();
				}
			}
			
			System.out.println("Your location is: " + player.getLocation()[0] + ", " + player.getLocation()[1]);
			
			int[] coord;
			
			do {
				System.out.println("Where do you want to go? Enter square coordinates in the form of row,col");
				/*input = in.nextLine();
				commaInd = input.indexOf(',');
				row = Integer.parseInt(input.substring(0, commaInd));
				col = Integer.parseInt(input.substring(commaInd+1));
				System.out.println(row);
				System.out.println(col);*/
				
				coord = buttonGrid.getPressed();
				row = coord[0];
				col = coord[1];
				
				if (grid[row][col].occupant != null && !isSameLocAs(row, col, player.getLocation())){
					repeat = true;
					System.out.println("Please enter a valid empty square");
				}
				else {
					repeat = false;
				}
				
			} while (repeat);
			
			System.out.println(player.moveTo(row, col));
			
			updateCreatureText(player);
			
			if (grid[beforeLoc[0]][beforeLoc[1]].occupant == null){
				buttonGrid.setText(beforeLoc, "");
			}
			
			
			/*ArrayList<Creature> enemies = player.getEnemiesInRange();
			if (!enemies.isEmpty()){
				System.out.println("Enemy Combatants you can attack:");
				for (int i = 0; i < enemies.size(); i++){
					System.out.println(i + " : " + enemies.get(i).getName() + " (" + enemies.get(i).getLocation()[0] + "," + enemies.get(i).getLocation()[1] + ")");
				}
				System.out.println("Enter the enemy combatant's number: ");
				input = in.nextLine();
				player.opponent = enemies.get(Integer.parseInt(input));
				player.makeAttack();
			}*/
			
			ArrayList<Creature> enemies = player.getEnemiesInRange();
			for (int i = 0; i < enemies.size(); i ++){
				System.out.println(Arrays.toString(enemies.get(i).getLocation()));
			}
			
			if (!player.getEnemiesInRange().isEmpty()){
				do {
					System.out.println("Click on an enemy in range to attack");
					
					coord = buttonGrid.getPressed();
					row = coord[0];
					col = coord[1];
					
					if (grid[row][col].occupant != null)
						System.out.println(grid[row][col].occupant.name);
					else
						System.out.println("null");
					
					if (player.canFightOther(grid[row][col].occupant) && player.isInRangeOf(grid[row][col].occupant)){
						repeat = false;
						player.opponent = grid[row][col].occupant;
						player.makeAttack();
						updateDownStatus(player.opponent);
					}
					else {
						repeat = true;
						System.out.println("Please enter a valid empty square");
					}
					
				} while (repeat);
			}
		}
		
		return beforeLoc;
	}
	
	private static boolean isSameLocAs(int row, int col, int[] loc){
		return row == loc[0] && col == loc[1];
	}

	private static ArrayList<Creature> getAllEnemies(boolean faction) {
		ArrayList<Creature> enemies = new ArrayList<Creature>();
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				if (grid[row][col].occupant != null && grid[row][col].occupant.getFaction() != faction){
					enemies.add(grid[row][col].occupant);
				}
			}
		}
		return enemies;
	}
	
	private static void updateCreatureText(Creature creature){
		String fac;
		if (creature.getFaction()){
			fac = "T";
		}
		else {
			fac = "F";
		}
		if(creature.isDown()){
			buttonGrid.setText(creature.getLocation(), creature.getAbbreviation() + "\n(" + fac + ")\nDown");
		}
		else {
			buttonGrid.setText(creature.getLocation(), creature.getAbbreviation() + "\n (" + fac + ")");
		}
	}
	
	private static void updateDownStatus(Creature creature) {
		if (creature != null && creature.isDown()){
			updateCreatureText(creature);
		}
		
	}
	
	private static void removeCreature(Creature creature){
		buttonGrid.setText(creature.getLocation(), "");
		modFacNum(creature.getFaction(), -1);
	}

	private static Terrain[][] populateGridTest() {
		grid = new Terrain[20][20];
		buttonGrid = new ButtonGrid(grid.length, grid[0].length);
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				grid[row][col] = new Terrain();
			}
		}
			
		Spearman Jorn = new Spearman();
		setUpCreature(Jorn, 19, 10, true);
		Infantry Bjorn = new Infantry();
		setUpCreature(Bjorn, 0, 10, false);
		
		man1 = Jorn;
		man2 = Bjorn;
		
		return grid;
	}
	
	private static Terrain[][] populateGridTest2() {
		grid = new Terrain[20][20];
		buttonGrid = new ButtonGrid(grid.length, grid[0].length);
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
		Jorn.setName("Jorn");
		Jorn.setAbbreviation("Jorn");
		setUpCreature(Jorn, 19, 10, true);
		Infantry Bjorn = new Infantry();
		Bjorn.setName("Bjorn");
		Bjorn.setAbbreviation("Bjorn");
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
		updateCreatureText(c);
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
