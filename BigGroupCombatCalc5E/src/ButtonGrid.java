import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.GridLayout;

public class ButtonGrid {
	
	JFrame frame=new JFrame();
	JButton[][] grid;
	
	public ButtonGrid(int width, int length){ //constructor
        frame.setLayout(new GridLayout(width,length)); //set layout
        grid=new JButton[width][length]; //allocate the size of grid
        for(int y=0; y<length; y++){
                for(int x=0; x<width; x++){
                        grid[x][y]=new JButton(""); //creates new button     
                        frame.add(grid[x][y]); //adds button to grid
                }
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); //sets appropriate size for frame
        frame.setVisible(true); //makes frame visible
	}
	
	public void setText(int row, int col, String text){
		grid[row][col].setText(text);
	}

}
