import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonGrid {
	
	private JFrame frame=new JFrame();
	private JButton[][] grid;
	
	//private JLabel label;
	
	private int[] coord;
	
	public ButtonGrid(int width, int length){ //constructor
        frame.setLayout(new GridLayout(width,length)); //set layout
        grid=new JButton[width][length]; //allocate the size of grid
        for(int y=0; y<length; y++){
                for(int x=0; x<width; x++){
                        grid[x][y]=new JButton(""); //creates new button   
                        grid[x][y].addActionListener(listener);
                        frame.add(grid[x][y]); //adds button to grid
                }
        }
        
        //label = new JLabel("Button pressed: NONE");
        //frame.add(label, BorderLayout.SOUTH);
        
        coord = null;
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); //sets appropriate size for frame
        frame.setVisible(true); //makes frame visible
	}
	
	public void setText(int row, int col, String text){
		grid[row][col].setText(text);
	}
	
	public void setText(int[] square, String text){
		setText(square[0], square[1], text);
	}
	
	public void waitForPress(){
		coord = null;
		while (coord == null){
			try {
			       Thread.sleep(200);
			} catch(InterruptedException e) {
				
			}
		}
	}
	
	public int[] getPressed(){
		waitForPress();
		return coord;
	}
	
	public ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    if (e.getSource().equals(grid[i][j])) {
                        //label.setText("Button pressed: " + i + ", " + j);
                        System.out.println(i + "***" + j);
                        coord = new int[] {i, j};
                    }
                }
            }
        }
    };

}
