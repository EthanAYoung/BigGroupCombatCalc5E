
public class dataPointer {
	
	private Runner runner;
	
	public dataPointer(Runner r){
		runner = r;
	}
	
	public Terrain[][] getGrid(){
		return runner.getGrid();
	}
	
}
