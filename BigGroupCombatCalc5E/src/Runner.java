
public class Runner {

	private Terrain[][] grid;
	
	public void main(String[] args) {
		grid = populateGridTest();
		for (int row = 0; row < grid.length; row++){
			for (int col = 0; col < grid[0].length; col++){
				if (grid[row][col].occupant != null){
					grid[row][col].occupant.rollInitiative();
				}
			}
		}

	}

	private Terrain[][] populateGridTest() {
		grid = new Terrain[20][20];
		Infantry Jorn = new Infantry();
		grid[19][10].occupant = Jorn;
		Infantry Bjorn = new Infantry();
		grid[0][10].occupant = Bjorn;
		return grid;
	}
	
	public Terrain[][] getGrid(){
		return grid;
	}

}
