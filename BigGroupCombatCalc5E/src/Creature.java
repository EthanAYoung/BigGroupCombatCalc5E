import java.util.ArrayList;
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
	
	protected int movementLeft;
	protected boolean hasReaction;
	protected int attacksLeft;
	
	protected boolean sneaking;
	protected int stealthScore;
	
	protected ArrayList<String> actions;
	protected ArrayList<String> bonusActions;

	protected int initiative;
	protected int numOfAttacks;
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
	protected Terrain[][] board;
	protected boolean allEnemiesDown;
	
	protected int enemiesDowned;
	protected String name;
	protected String abbreviation;
	
	protected boolean player;
	
	protected ArrayList<PassiveEffect> passiveEffects;
	protected PassiveEffect concentrationTarget;
	
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
		
		enemiesDowned = 0;
		name = "Creature";
		abbreviation = "CR";
		
		player = false;
	}
	
	public Creature(int h, int a, int sp, int str, int dex, int con, int i, int wis, int cha){
		
	}
	
	public void takeTurn(){
		if (startTurn()) {
			if (isProne()){
				standUp();
			}
			if (opponent != null && !opponent.isDown()){
				if (isInRangeOf(opponent)){
					makeAttack();
				}
				else if (moveToAttack(opponent)){
					makeAttack();
				}
			}
			else {
				if (findOpponent() && moveToAttack(opponent)){
					makeAttack();
				}
			}
		}
	}
	
	public boolean startTurn(){
		if (isDefeated()){
			return false;
		}
		hasReaction = true;
		movementLeft = speed;
		attacksLeft = numOfAttacks;
		if (isDown()){
			makeDeathSavingThrow();
			if (hp > 0){
				stayDown();
			}
			return false;			
		}
		return true;
	}

	public void makeAttack(){
		int attackRoll = rollDie(20) + strength + proficiency;
		int damageRoll = rollDie(damageDie) + strength;
		opponent.getAttacked(attackRoll, damageRoll, this);
		if (opponent.isDown()){
			enemiesDowned++;
		}
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
	
	public ArrayList<Creature> getEnemiesInRange(){
		int squareRange = range / 5;
		ArrayList<Creature> enemiesInRange = new ArrayList<Creature>();
		int left = putOnGridLower(colPos - squareRange);
		int right = putOnGridHigher(colPos + squareRange, board[0].length);
		int up = putOnGridLower(rowPos - squareRange);
		int down = putOnGridHigher(rowPos + squareRange, board.length);
		
		for (int row = up; row <= down; row++){
			for (int col = left; col <= right; col++){
				if (canFightOther(board[row][col].occupant)){
					enemiesInRange.add(board[row][col].occupant);
				}
			}
		}
		return enemiesInRange;
	}
	
	public boolean moveTo(int row, int col){
		if (walkPath(getPathTo(rowPos, colPos, row, col, 0))){
			return true;
		}
		return false;
	}

	private boolean moveToAttack(Creature other) {
		if(isInRangeOf(other)){
			return true;
		}
		int[] target = findOpenSpaceInRangeOf(other);
		if (target == null){
			return false;
		}
		if (moveTo(target[0], target[1])){
			return true;
		}
		return false;
	}
	
	public boolean isInRangeOf(Creature other) {
		int squareRange = range/5;
		if(Math.abs(rowPos - other.getRowPos()) <= squareRange && Math.abs(colPos - other.getColPos()) <= squareRange){
			return true;
		}
		return false;
	}
	
	private int[] findOpenSpaceInRangeOf(Creature other){
		int squareRange = range / 5;
		ArrayList<int[]> openSpaces = new ArrayList<int[]>();
		int left = putOnGridLower(other.getColPos() - squareRange);
		int right = putOnGridHigher(other.getColPos() + squareRange, board[0].length);
		int up = putOnGridLower(other.getRowPos() - squareRange);
		int down = putOnGridHigher(other.getRowPos() + squareRange, board.length);
		
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
		
		for(int row = up + 1; row < down; row++){ //doesn't check the corner twice
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
	
	private boolean walkPath(Path myPath){
		myPath.setStart(1);
		int currRow;
		int currCol;
		Terrain curr;
		int moveReqPile = 0;
		int lastSuccInd = -1;
		for (int i = 0; i < myPath.getSteps().size(); i++){
			//System.out.println("Movement Left = " + movementLeft);
			currRow = myPath.getSteps().get(i)[0];
			currCol = myPath.getSteps().get(i)[1];
			curr = board[currRow][currCol];
			//System.out.println("curr.getMoveReq(): " + curr.getMoveReq());
			//System.out.println("moveReqPile: " + moveReqPile);
			if (curr.occupant == null){
				if (movementLeft >= curr.getMoveReq() + moveReqPile){
					movementLeft -= (curr.getMoveReq() + moveReqPile);
					board[rowPos][colPos].occupant = null;
					rowPos = currRow;
					colPos = currCol;
					curr.occupant = this;
					lastSuccInd = i;
					moveReqPile = 0;
					//System.out.println("Moved: " + movementLeft);
				}
				else {
					//System.out.println("Stopped");
					myPath.setStart(lastSuccInd + 1);
					return false;
				}
			}
			else {
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
			down = false;
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

	private void getAttacked(int attackRoll, int damageRoll, Creature attacker) {
		opponent = attacker;
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
	
	public boolean standUp(){
		int temp;
		prone = false;
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
		stable = true;
		removeFromPlay();
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

	public String getName() {
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public String getAbbreviation() {
		return abbreviation;
	}
	
	public void setAbbreviation(String abb) {
		abbreviation = abb;
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
		System.out.println("Prone? " + prone);
		System.out.println("Down? " + down);
		System.out.println("Dead? " + dead);
		System.out.println("Stable? " + stable);
		System.out.println("Position = " + rowPos + ", " + colPos);
		System.out.println("Enemies Felled = " + enemiesDowned);
	}
	
	public void setAsPlayerChar(){
		player = true;
	}
	
	public boolean isPlayerChar(){
		return player;
	}
	
	public int[] getLocation(){
		return new int[]{rowPos, colPos};
	}

	@Override
	public int compareTo(Creature arg0) {
		// TODO Auto-generated method stub
		return initiative - arg0.initiative;
	}
	

}
