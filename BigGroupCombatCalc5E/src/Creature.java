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
	
	public Creature(){
		
	}
	
	public Creature(int h, int a, int sp, int str, int dex, int con, int i, int wis, int cha){
		
	}
	
	public void takeTurn(Creature[][] grid){
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
			if (findOpponent(grid) && moveToOpponent(grid)){
				makeAttack();
			}
		}
	}

	private void makeAttack(){
		
	}

	private boolean findOpponent(Creature[][] grid){
		int rowU = rowPos - 1;
		int rowD = rowPos + 1;
		int colL = colPos - 1;
		int colR = colPos + 1;
		boolean contU = rowU >= 0;
		boolean contD = rowD < grid.length;
		boolean contL = colL >= 0;
		boolean contR = colR < grid[0].length;
		
		while (contU || contD || contL || contR){
			if (contU){
				if (checkRow(rowU, colL, colR, grid)){
					return true;
				}
			}
			if (contD){
				if (checkRow(rowD, colL, colR, grid)){
					return true;
				}
			}
			if (contL){
				if (checkCol(colL, rowU, rowD, grid)){
					return true;
				}
			}
			if (contR){
				if (checkCol(colR, rowU, rowD, grid)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean checkRow(int row, int colL, int colR, Creature[][] grid) {
		int mid = (colR - colL) / 2 + colL;
		if (faction != grid[row][mid].getFaction()){
			opponent = grid[row][mid];
			return true;
		}
		for (int col = mid - 1; col >= colL; col--){
			if(faction != grid[row][col].getFaction()){
				opponent = grid[row][col];
				return true;
			}
		}
		for (int col = mid + 1; col <= colR; col++){
			if(faction != grid[row][col].getFaction()){
				opponent = grid[row][col];
				return true;
			}
		}
		
		return false;
		
	}
	
	private boolean checkCol(int col, int rowU, int rowD, Creature[][] grid) {
		int mid = (rowD - rowU) / 2 + rowU;
		if (faction != grid[col][mid].getFaction()){
			opponent = grid[col][mid];
			return true;
		}
		for (int row = mid - 1; row >= rowU; row--){
			if(faction != grid[row][col].getFaction()){
				opponent = grid[row][col];
				return true;
			}
		}
		for (int row = mid + 1; row <= rowD; row++){
			if(faction != grid[row][row].getFaction()){
				opponent = grid[row][row];
				return true;
			}
		}
		
		return false;
		
	}

	public int getFaction() {
		return faction;
	}

	private boolean moveToOpponent(Creature[][] grid) {
		if(Math.abs(rowPos - opponent.getRowPos()) < 1 && Math.abs(colPos - opponent.getColPos()) < 1){
			return true;
		}
		return false;
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
