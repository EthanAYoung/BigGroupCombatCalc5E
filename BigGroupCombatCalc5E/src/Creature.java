import java.util.ArrayList;
import java.util.Random;

public class Creature {
	
	private int hp;
	private int ac;
	private int speed;
	private int strength;
	private int dexterity;
	private int constitution;
	private int intelligence;
	private int wisdom;
	private int charisma;
	private int pasPerception;
	
	private int movementLeft;
	
	private boolean sneaking;
	private int stealthScore;
	
	private int damageDie;
	private int range;
	private Creature opponent;
	private boolean prone;
	
	private boolean down;
	private int deathSavesPos;
	private int deathSavesNeg;
	private boolean stable;
	private boolean dead;
	
	private int rowPos;
	private int colPos;
	private int faction;
	private Path myPath;
	
	private dataPointer data;
	
	public Creature(){
		
	}
	
	public Creature(int h, int a, int sp, int str, int dex, int con, int i, int wis, int cha){
		
	}
	
	public void takeTurn(){
		if (dead || stable){
			return;
		}
		if (isDown()){
			makeDeathSavingThrow();
			if (hp > 0){
				stayDown();
			}			
		}
		if (isProne()){
			standUp();
		}
		if (opponent != null && !opponent.isDown()){
			makeAttack();
		}
		else {
			if (findOpponent() && moveToOpponent()){
				makeAttack();
			}
		}
	}

	private void makeAttack(){
		
	}

	private boolean findOpponent(){
		int rowU = rowPos - 1;
		int rowD = rowPos + 1;
		int colL = colPos - 1;
		int colR = colPos + 1;
		boolean contU = rowU >= 0;
		boolean contD = rowD < data.getGrid().length;
		boolean contL = colL >= 0;
		boolean contR = colR < data.getGrid()[0].length;
		
		while (contU || contD || contL || contR){
			if (contU){
				if (checkRow(rowU, colL, colR)){
					return true;
				}
			}
			if (contD){
				if (checkRow(rowD, colL, colR)){
					return true;
				}
			}
			if (contL){
				if (checkCol(colL, rowU, rowD)){
					return true;
				}
			}
			if (contR){
				if (checkCol(colR, rowU, rowD)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean checkRow(int row, int colL, int colR) {
		int mid = (colR - colL) / 2 + colL;
		Creature curr = data.getGrid()[row][mid].occupant;
		if (curr != null && faction != curr.getFaction()){
			opponent = curr;
			return true;
		}
		for (int col = mid - 1; col >= colL; col--){
			curr = data.getGrid()[row][col].occupant;
			if(curr != null && faction != curr.getFaction()){
				opponent = curr;
				return true;
			}
		}
		for (int col = mid + 1; col <= colR; col++){
			curr = data.getGrid()[row][col].occupant;
			if(curr != null && faction != curr.getFaction()){
				opponent = curr;
				return true;
			}
		}
		
		return false;
		
	}
	
	//optimize the corners
	private boolean checkCol(int col, int rowU, int rowD) {
		int mid = (rowD - rowU) / 2 + rowU;
		Creature curr = data.getGrid()[mid][col].occupant;
		if (curr != null && faction != curr.getFaction()){
			opponent = curr;
			return true;
		}
		for (int row = mid - 1; row >= rowU; row--){
			curr = data.getGrid()[row][col].occupant;
			if(curr != null && faction != curr.getFaction()){
				opponent = curr;
				return true;
			}
		}
		for (int row = mid + 1; row <= rowD; row++){
			curr = data.getGrid()[row][col].occupant;
			if(curr != null && faction != curr.getFaction()){
				opponent = curr;
				return true;
			}
		}
		
		return false;
		
	}

	public int getFaction() {
		return faction;
	}

	private boolean moveToOpponent() {
		int squareRange = range/5;
		if(Math.abs(rowPos - opponent.getRowPos()) < squareRange && Math.abs(colPos - opponent.getColPos()) < squareRange){
			return true;
		}
		int[] target = findOpenSpaceInRange();
		myPath = getPathTo(rowPos, colPos, target[0], target[1], new Path());
		walkPath();
		return false;
	}
	
	private int[] findOpenSpaceInRange(){
		int squareRange = range/5;
		ArrayList<int[]> openSpaces = new ArrayList<int[]>();
		int left = putOnGridLower(opponent.getColPos() - squareRange);
		int right = putOnGridHigher(opponent.getColPos() + squareRange, data.getGrid()[0].length);
		int up = putOnGridLower(opponent.getRowPos() - squareRange);
		int down = putOnGridHigher(opponent.getRowPos() + squareRange, data.getGrid().length);
		
		for(int col = left; col <= right; col++){
			if(data.getGrid()[up][col].occupant == null){
				openSpaces.add(new int[]{up, col});
			}
		}
		for(int col = left; col <= right; col++){
			if(data.getGrid()[down][col].occupant == null){
				openSpaces.add(new int[]{down, col});
			}
		}
		
		for(int row = up + 1; row < down; row++){
			if(data.getGrid()[row][left].occupant == null){
				openSpaces.add(new int[]{row, left});
			}
		}
		for(int row = up + 1; row < down; row++){
			if(data.getGrid()[row][right].occupant == null){
				openSpaces.add(new int[]{row, right});
			}
		}
		
		if (openSpaces.isEmpty()){
			return null;
		}
		
		int least = Math.abs(rowPos - openSpaces.get(0)[0]) + Math.abs(colPos - openSpaces.get(0)[1]);
		int tmpLeast;
		int ind = 0;
		for (int i = 1; i < openSpaces.size(); i++){
			tmpLeast = Math.abs(rowPos - openSpaces.get(i)[0]) + Math.abs(colPos - openSpaces.get(i)[1]);
			if (tmpLeast < least){
				least = tmpLeast;
				ind = i;
			}
		}
		
		return openSpaces.get(ind);
	}
	
	private int putOnGridLower(int i){
		if (i < 0){
			return 0;
		}
		return i;
	}
	
	private int putOnGridHigher(int i, int boundary){
		if (i >= boundary){
			return boundary - 1;
		}
		return i;
	}
	
	private Path getPathTo(int myRow, int myCol, int endRow, int endCol, Path path){
		if (myRow == endRow && myCol == endCol){
			return path;
		}
		
		ArrayList<Integer> rowPosibs = new ArrayList<Integer>();
		ArrayList<Integer> colPosibs = new ArrayList<Integer>();
		if (myRow < endRow){
			rowPosibs.add(myRow + 1);
			rowPosibs.add(myRow);
		}
		if (myRow > endRow){
			rowPosibs.add(myRow - 1);
			rowPosibs.add(myRow);
		}
		if (myRow == endRow){
			rowPosibs.add(myRow);
		}
		
		if (myCol < endCol){
			colPosibs.add(myCol + 1);
			colPosibs.add(myCol);
		}
		if (myCol > endCol){
			colPosibs.add(myCol - 1);
			colPosibs.add(myCol);
		}
		if (myCol == endCol){
			colPosibs.add(myCol);
		}
		
		ArrayList<Path> paths = new ArrayList<Path>();
		Path temp;
		int currRow;
		int currCol;
		for (int row = 0; row < rowPosibs.size(); row++){
			currRow = rowPosibs.get(row);
			for (int col = 0; col < colPosibs.size(); col++){
				currCol = colPosibs.get(col);
				if (currRow == 0 && currCol == 0){
					continue;
				}
				temp = path.copy();
				temp.addStep(new int[]{currRow, currCol});
				if (data.getGrid()[currRow][currCol].occupant == null){
					temp.increaseLength(data.getGrid()[currRow][currCol].getMoveReq());
				}
				else {
					temp.increaseLength(data.getGrid()[currRow][currCol].getMoveReq() + 5);
				}
				paths.add(getPathTo(currRow, currCol, endRow, endCol, temp));
			}
		}
		
		int least = paths.get(0).getLength();
		int shortestPathInd = 0;
		int tmpLeast;
		for (int i = 1; i < paths.size(); i++){
			tmpLeast = paths.get(i).getLength();
			if (tmpLeast < least){
				shortestPathInd = i;
				least = tmpLeast;
			}
		}
		
		return paths.get(shortestPathInd);
	}
	
	private void walkPath(){
		int currRow;
		int currCol;
		Terrain curr;
		for (int i = 0; i < myPath.getSteps().size(); i++){
			currRow = myPath.getSteps().get(i)[0];
			currCol = myPath.getSteps().get(i)[1];
			curr = data.getGrid()[currRow][currCol];
			if (curr.occupant == null && movementLeft >= curr.getMoveReq()){
				rowPos = currRow;
				colPos = currCol;
				curr.occupant = this;
			}
			if (curr.occupant != null)
		}
	}

	private int getRowPos() {
		return rowPos;
	}

	private int getColPos() {
		return colPos;
	}

	private int rollD20(){
		Random rand = new Random();
		return rand.nextInt(20) + 1;
	}
	
	public boolean isDown(){
		return down;
	}

	private void makeDeathSavingThrow(){
		int roll = rollD20();
		if (roll == 20){
			deathSavesPos = 0;
			deathSavesNeg = 0;
			hp = 1;
		}
		else if (roll == 1){
			deathSavesNeg += 2;
		}
		else if (roll > 10){
			deathSavesPos++;
		}
		else{
			deathSavesNeg++;
		}
		if (deathSavesPos == 3){
			stable = true;
			deathSavesPos = 0;
			deathSavesNeg = 0;
		}
		if (deathSavesNeg == 3){
			dead = true;
			deathSavesPos = 0;
			deathSavesNeg = 0;
		}
		
	}
	
	public void takeDamage(int damage){
		hp -= damage;
		if (hp < 0){
			dropProne();
			goDown();
		}
	}
	
	private boolean standUp(){
		int temp;
		temp = movementLeft - speed / 2;
		if (temp < 0){
			return false;
		}
		else {
			movementLeft = temp;
			return true;
		}
	}
	
	public void dropProne(){
		prone = true;
	}
	
	public boolean isProne(){
		return prone;
	}
	
	private void goDown(){
		down = true;
	}
	
	private void stayDown(){
		down = true;
	}
	
	private void riseUp(){
		standUp();
		down = false;
	}
	

}
