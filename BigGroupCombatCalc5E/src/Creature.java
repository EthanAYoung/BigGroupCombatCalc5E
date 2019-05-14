import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Creature implements Comparable<Creature>{
	
	protected int hp;
	protected int ac;
	protected int speed;
	protected int strength;
	protected int dexterity;
	protected int constitution;
	protected int intelligence;
	protected int wisdom;
	protected int charisma;
	protected int pasPerception;
	protected int proficiency;
	protected int tempHp;
	protected int hpMax;
	protected int initiative;
	
	protected int movementLeft;
	protected boolean hasReaction;
	
	protected boolean sneaking;
	protected int stealthScore;
	
	protected int damageDie;
	protected int range;
	protected Creature opponent;
	protected boolean prone;
	
	protected boolean down;
	protected int deathSavesPos;
	protected int deathSavesNeg;
	protected boolean stable;
	protected boolean dead;
	
	protected int rowPos;
	protected int colPos;
	protected boolean faction;
	protected Path myPath;
	protected Terrain[][] board;
	protected boolean allEnemiesDown;
	
	public Creature(){
		sneaking = false;
		
		opponent = null;
		prone = false;
		
		down = false;
		deathSavesPos = 0;
		deathSavesNeg = 0;
		stable = false;
		dead = false;
		allEnemiesDown = false;
		
	}
	
	public Creature(int h, int a, int sp, int str, int dex, int con, int i, int wis, int cha){
		
	}
	
	public void takeTurn(){
		if (isDefeated()){
			return;
		}
		hasReaction = true;
		movementLeft = speed;
		if (isDown()){
			makeDeathSavingThrow();
			if (hp > 0){
				stayDown();
			}
			return;			
		}
		if (isProne()){
			standUp();
		}
		if (opponent != null && !opponent.isDown()){
			if (opponentIsInRange()){
				makeAttack();
			}
			else if (moveToOpponent()){
				makeAttack();
			}
		}
		else {
			if (findOpponent() && moveToOpponent()){
				makeAttack();
			}
		}
	}

	private void makeAttack(){
		int attackRoll = rollDie(20) + strength + proficiency;
		int damageRoll = rollDie(damageDie) + strength;
		opponent.getAttacked(attackRoll, damageRoll);
	}

	private boolean findOpponent(){
		if (allEnemiesDown){
			return false;
		}
		
		int rowU = rowPos - 1;
		int rowD = rowPos + 1;
		int colL = colPos - 1;
		int colR = colPos + 1;
		boolean contU = rowU >= 0;
		boolean contD = rowD < board.length;
		boolean contL = colL >= 0;
		boolean contR = colR < board[0].length;
		
		while (contU || contD || contL || contR){
			if (checkDir(contU, contL, contR, rowU, colL, colR, board[0].length, true)){
				return true;
			}
			if (checkDir(contD, contL, contR, rowD, colL, colR, board[0].length, true)){
				return true;
			}
			if (checkDir(contL, contU, contD, colL, rowU, rowD, board.length, false)){
				return true;
			}
			if (checkDir(contR, contU, contD, colR, rowU, rowD, board.length, false)){
				return true;
			}
			
			rowU--;
			rowD++;
			colL--;
			colR++;
			contU = rowU >= 0;
			contD = rowD < board.length;
			contL = colL >= 0;
			contR = colR < board[0].length;
		}
		
		allEnemiesDown = true;
		return false;
	}
	
	private boolean checkDir(Boolean cont, Boolean lowCont, Boolean highCont, int base, int low, int high, int boundary, boolean isRow){
		if (cont){
			if (!lowCont){
				low = 0;
			}
			if (!highCont){
				high = boundary - 1;
				//System.out.println(boundary);
			}
			if (isRow){
				if (checkRow(base, low, high)){
					return true;
				}
			}
			else {
				if (checkCol(base, low, high)){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkRow(int row, int colL, int colR) {
		int mid = colPos;
		Creature curr = board[row][mid].occupant;
		if (canFightOther(curr)){
			opponent = curr;
			return true;
		}
		for (int col = mid - 1; col >= colL; col--){
			curr = board[row][col].occupant;
			if (canFightOther(curr)){
				opponent = curr;
				return true;
			}
		}
		for (int col = mid + 1; col <= colR; col++){
			//System.out.println("Row = " + row + " Col = " + col);
			curr = board[row][col].occupant;
			if (canFightOther(curr)){
				opponent = curr;
				return true;
			}
		}
		
		return false;
		
	}
	
	//optimize the corners
	private boolean checkCol(int col, int rowU, int rowD) {
		int mid = rowPos;
		Creature curr = board[mid][col].occupant;
		if (canFightOther(curr)){
			opponent = curr;
			return true;
		}
		for (int row = mid - 1; row >= rowU; row--){
			curr = board[row][col].occupant;
			if (canFightOther(curr)){
				opponent = curr;
				return true;
			}
		}
		for (int row = mid + 1; row <= rowD; row++){
			//System.out.println("Row = " + row + " Col = " + col);
			curr = board[row][col].occupant;
			if (canFightOther(curr)){
				opponent = curr;
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean canFightOther(Creature other){
		if (other != null && faction != other.getFaction() && !other.isDown()){
			return true;
		}
		return false;
	}

	public boolean getFaction() {
		return faction;
	}

	private boolean moveToOpponent() {
		if(opponentIsInRange()){
			return true;
		}
		int[] target = findOpenSpaceInRange();
		myPath = getPathTo(rowPos, colPos, target[0], target[1], 0);
		if (walkPath()){
			return true;
		}
		return false;
	}
	
	private boolean opponentIsInRange() {
		int squareRange = range/5;
		if(Math.abs(rowPos - opponent.getRowPos()) <= squareRange && Math.abs(colPos - opponent.getColPos()) <= squareRange){
			return true;
		}
		return false;
	}
	
	private int[] findOpenSpaceInRange(){
		int squareRange = range/5;
		ArrayList<int[]> openSpaces = new ArrayList<int[]>();
		int left = putOnGridLower(opponent.getColPos() - squareRange);
		int right = putOnGridHigher(opponent.getColPos() + squareRange, board[0].length);
		int up = putOnGridLower(opponent.getRowPos() - squareRange);
		int down = putOnGridHigher(opponent.getRowPos() + squareRange, board.length);
		
		for(int col = left; col <= right; col++){
			if(board[up][col].occupant == null){
				openSpaces.add(new int[]{up, col});
			}
		}
		for(int col = left; col <= right; col++){
			if(board[down][col].occupant == null){
				openSpaces.add(new int[]{down, col});
			}
		}
		
		for(int row = up + 1; row < down; row++){
			if(board[row][left].occupant == null){
				openSpaces.add(new int[]{row, left});
			}
		}
		for(int row = up + 1; row < down; row++){
			if(board[row][right].occupant == null){
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
	
	private Path getPathTo(int myRow, int myCol, int endRow, int endCol, int totalLength){
		if (myRow == endRow && myCol == endCol){
			Path ret = new Path();
			ret.addHead(myRow, myCol, board[myRow][myCol].getMoveReq());
			return ret;
		}
		/*if (totalLength > movementLeft){
			Path ret = new Path();
			ret.addHead(myRow, myCol, board[myRow][myCol].getMoveReq());
			return ret;
		}*/
		
		int rowLow = myRow;
		int rowHigh = myRow;
		int colLow = myCol;
		int colHigh = myCol;
		if (myRow < endRow){
			rowHigh = myRow + 1;
		}
		if (myRow > endRow){
			rowLow = myRow - 1;
		}
		
		if (myCol < endCol){
			colHigh = myCol + 1;
		}
		if (myCol > endCol){
			colLow = myCol - 1;
		}
		
		//System.out.println("myRow = " + myRow + " myCol = " + myCol);
		//System.out.println("endRow = " + endRow + " endCol = " + endCol);
		
		ArrayList<Path> paths = new ArrayList<Path>();
		for (int currRow = rowLow; currRow <= rowHigh; currRow++){
			//System.out.println(currRow);
			//System.out.println("colLow = " + colLow + " colHigh = " + colHigh);
			for (int currCol = colLow; currCol <= colHigh; currCol++){
				//System.out.println("Row = " + currRow + " Col = " + currCol);
				if (currRow != myRow || currCol != myCol){
					//System.out.println("inside");
					paths.add(getPathTo(currRow, currCol, endRow, endCol, totalLength + board[currRow][currCol].getMoveReq()));
				}
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
		
		Path ret = paths.get(shortestPathInd);
		ret.addHead(myRow, myCol, board[myRow][myCol].getMoveReq());
		return ret;
	}
	
	private boolean walkPath(){
		myPath.setStart(1);
		int currRow;
		int currCol;
		Terrain curr;
		int moveReqPile = 0;
		int lastSuccInd = -1;
		for (int i = 0; i < myPath.getSteps().size(); i++){
			currRow = myPath.getSteps().get(i)[0];
			currCol = myPath.getSteps().get(i)[1];
			curr = board[currRow][currCol];
			if (curr.occupant == null){
				if (movementLeft >= curr.getMoveReq() + moveReqPile){
					board[rowPos][colPos].occupant = null;
					rowPos = currRow;
					colPos = currCol;
					curr.occupant = this;
					lastSuccInd = i;
					movementLeft -= (curr.getMoveReq() + moveReqPile);
					moveReqPile = 0;
				}
				else {
					myPath.setStart(lastSuccInd + 1);
					return false;
				}
			}
			if (curr.occupant != null){
				moveReqPile += curr.getMoveReq();
			}
		}
		return true;
	}

	private int getRowPos() {
		return rowPos;
	}

	private int getColPos() {
		return colPos;
	}

	private int rollDie(int die){
		Random rand = new Random();
		return rand.nextInt(die) + 1;
	}
	
	public boolean isDown(){
		return down;
	}

	private void makeDeathSavingThrow(){
		int roll = rollDie(20);
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
			removeFromPlay();
		}
		if (deathSavesNeg == 3){
			dead = true;
			deathSavesPos = 0;
			deathSavesNeg = 0;
			removeFromPlay();
		}
		
	}

	private void getAttacked(int attackRoll, int damageRoll) {
		if (attackRoll > ac){
			takeDamage(damageRoll);
		}
	}
	
	private void takeDamage(int damage){
		hp -= damage;
		if (hp <= 0){
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
		stable = true;
		removeFromPlay();
	}
	
	private void riseUp(){
		standUp();
		down = false;
	}
	
	public boolean isDefeated(){
		if (dead || stable){
			return true;
		}
		return false;
	}
	
	public boolean isDead(){
		return dead;
	}
	
	private void removeFromPlay(){
		board[rowPos][colPos].occupant = null;
		board[rowPos][colPos].addBody(this);
	}
	
	public void setFaction(boolean fac){
		faction = fac;
	}
	
	public int rollInitiative(){
		initiative = rollDie(20) + dexterity;
		return initiative;
	}
	
	public void setBoard(Terrain[][] g){
		board = g;
	}
	
	public void setPos(int r, int c){
		rowPos = r;
		colPos = c;
	}
	
	public void runDiagnostic(){
		System.out.println("Hp = " + hp);
		System.out.println("Down? " + down);
		System.out.println("Dead? " + dead);
		System.out.println("Stable? " + stable);
		System.out.println("Position = " + rowPos + ", " + colPos);
	}

	@Override
	public int compareTo(Creature arg0) {
		// TODO Auto-generated method stub
		return initiative - arg0.initiative;
	}
	

}
