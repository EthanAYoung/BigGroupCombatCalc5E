import java.util.ArrayList;

public class Path {
	
	private ArrayList<int[]> steps;
	private int length;
	
	public Path(){
		steps = new ArrayList<int[]>();
		length = 0;
	}
	
	public void addStep(int[] step){
		steps.add(step);
	}
	
	public ArrayList<int[]> getSteps(){
		return steps;
	}
	
	public void addHead(int row, int col, int moveReq){
		steps.add(0, new int[]{row, col});
	}
	
	public int getLength(){
		return length;
	}
	
	public void increaseLength(int i){
		length += i;
	}
	
	public Path copy(){
		Path copy = new Path();
		for (int i = 0; i < steps.size(); i++){
			copy.addStep(steps.get(i));
		}
		return copy;
	}

	public void setStart(int i) {
		steps = (ArrayList<int[]>) steps.subList(i, steps.size());
	}

}
