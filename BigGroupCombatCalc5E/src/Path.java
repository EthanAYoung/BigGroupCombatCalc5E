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

}
