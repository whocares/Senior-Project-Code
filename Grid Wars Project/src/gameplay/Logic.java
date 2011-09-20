package gameplay;

import java.util.ArrayList;

import player.Player;

import units.*;
import terrain.Tile;
import maps.MapReader;

//TODO
public class Logic {

	private final int BINCOME = 1000;
	private final int BASEINCOME = 3000;

	private final int PLAYER1 = 0;
	private final int PLAYER2 = 1;

	private ArrayList<Player> playerList;
	private Unit[][] unitBoard;
	private Tile[][] tBoard;
	private char[][] moves;
	private int mapSize;
	private MapReader mr;

	public Logic(String mapName, char p1Fact, char p2Fact, String p1Name, 
			String p2Name){
		mr = new MapReader(mapName);
		createBoards();    
		mapSize = mr.getSize();
		playerList = new ArrayList();
		Player p1 = new Player(p1Name, 1, p1Fact);
		Player p2 = new Player(p2Name, 2, p2Fact);
		playerList.add(p1);
		playerList.add(p2);
	}


	private void createBoards(){
		unitBoard =  new Unit[mapSize][mapSize];
		tBoard = mr.createMap();
		moves = new char[mapSize][mapSize];

		for(int i = 0; i < mapSize; i++)
			for(int j = 0; j < mapSize; j++)
				moves[i][j] = '-';
	}

	/*public boolean move(Unit pUnit, int destX, int destY){
		boolean returnVal = false;

		if (canMove(destX, destY)) {
			pUnit.setX(destX);
			pUnit.setY(destY);
		}


	}*/

	public boolean canMove(Unit pUnit, int pX, int pY) {

		int desiredX = pX;
		int desiredY = pY;

		int currX = pUnit.getX();
		int currY = pUnit.getY();



		return false;
	}

	public char[][] setUpMoves(Unit pUnit) {
		int movement = pUnit.getMove();

		int x = pUnit.getX();
		int y = pUnit.getY();

		Tile[][] tempMap = tBoard;
		char[][] moves = new char[mr.getSize()][mr.getSize()];

		char type = pUnit.getType();
		char tileType = tempMap[x][y].getType();

		//This loop searches the left side to see if pUnit can move
		for (int c = 0; c < movement || y - c > 0; c++) {
			tileType = tempMap[x][y-c].getType();
			if (unitBoard[x][y-c] == null) {
				if (tileType == 'm') {
					if ( (type == 'i' || type == 'a'))
						moves[x][y-c] = '-';
				} else if (tileType != 'w')
					moves[x][y-c] = '-';
				else 
					break;
			} else 
				break;
		}


		//This loop searches the right side to see if pUnit can move
		for (int c = 0; c < movement || y + c < mr.getSize(); c++) {
			tileType = tempMap[x][y+c].getType();
			if (unitBoard[x][y+c] == null) {
				if (tileType == 'm') {
					if ( (type == 'i' || type == 'a'))
						moves[x][y+c] = '-';
				} else if (tileType != 'w')
					moves[x][y+c] = '-';
				else 
					break;
			} else 
				break;
		}

		//This loop searches the top side to see if pUnits can move
		for (int r = 0; r < movement || x + r < mr.getSize(); r++) {
			tileType = tempMap[x+r][y].getType();
			if (unitBoard[x+r][y] == null) {
				if (tileType == 'm') {
					if ( (type == 'i' || type == 'a'))
						moves[x+r][y] = '-';
				} else if (tileType != 'w')
					moves[x+r][y] = '-';
				else 
					break;
			} else 
				break;
		}

		//This loop searches the bottom side to see if pUnits can move
		for (int r = 0; r < movement || x - r > 0; r++) {
			tileType = tempMap[x-r][y].getType();
			if (unitBoard[x-r][y] == null) {
				if (tileType == 'm') {
					if ( (type == 'i' || type == 'a'))
						moves[x-r][y] = '-';
				} else if (tileType != 'w')
					moves[x-r][y] = '-';
				else 
					break;
			} else 
				break;
		}

		/*//This loop searches the top right side to see if pUnit can move
		for (int r = 0; r < movement - 1 || x + r < mr.getSize(); r++) {
			for (int c = 0; c < movement -1 || y + c < mr.getSize(); c++) {
				tileType = tempMap[x+r][y+c].getType();
				if (r == movement - 2) {

				}
				if (unitBoard[x+r][y+c] == null) {
					if (tileType == 'm') {
						if ( (type == 'i' || type == 'a'))
							moves[x+r][y+c] = '-';
					} else if (tileType != 'w')
						moves[x+r][y+c] = '-';
					else 
						break;
				} else 
					break;
			}
		}*/

		return moves;
	}

	private char[][] calculateMoves(Unit pUnit){
		switch(pUnit.getType()){
		case Unit.AIRTYPE:
			for(int i = 0; i < pUnit.getMove();i++)
				break;
			break;
		case Unit.TANKTYPE:

			break;
		case Unit.INFANTRYTYPE:

			break;

		}

		return moves;

	}


	public char[][] getMoves(){
		return moves;
	}

	public void econDay(Player p) {
		int econ = p.getNumBuild() * BINCOME + BASEINCOME;
		p.setCash(p.getCash() + econ);
	}

	public Unit[] battle(Unit p1, Unit p2, int attackFirst) {
		Unit[] modifiedUnits = new Unit[2]; 
		int damage = 0; 
		int cDamage = 0;

		Unit mP1 = p1;
		Unit mP2 = p2;

		int mp2HB = tBoard[mP2.getX()][mP2.getY()].getHeight();
		int mp1HB = tBoard[mP1.getX()][mP1.getY()].getHeight();

		if (attackFirst == PLAYER1) {
			damage = (mP1.getAttack() + mP1.getBonus() + mp1HB) - 
					(mP2.getArmor() + mp2HB);
			mP2.setHP(mP2.getHP() - damage);
			if (mP2.getHP() <= 0) {
				//TODO: Take off of grid as it DIED :(
				playerList.get(PLAYER2).setNumUnits(playerList.get(PLAYER2).
						getNumUnits()- 1);
				unitBoard[mP2.getX()][mP2.getY()] = null;
			} else {
				cDamage = (mP2.getAttack() + mP2.getBonus() + mp2HB) - 
						(mP1.getArmor() + mp1HB);

				mP1.setHP(mP1.getHP() - cDamage);
				if (mP1.getHP() <= 0) {
					//TODO: take off grid
					playerList.get(PLAYER1).setNumUnits(playerList.get(PLAYER1).
							getNumUnits()- 1);;
							unitBoard[mP1.getX()][mP1.getY()] = null;
				}
			}
		} else {
			damage = (mP2.getAttack() + mP2.getBonus() + mp2HB) - 
					(mP1.getArmor() + mp1HB);
			mP2.setHP(mP1.getHP() - damage);
			if (mP1.getHP() <= 0) {
				//TODO: Take off of grid as it DIED :( 
				playerList.get(PLAYER1).setNumUnits(playerList.get(PLAYER1).
						getNumUnits()- 1);
				unitBoard[mP1.getX()][mP1.getY()] = null;
			} else {
				cDamage = (mP1.getAttack() + mP1.getBonus() + mp1HB) - 
						(mP2.getArmor() + mp2HB);

				mP2.setHP(mP2.getHP() - cDamage);
				if (mP2.getHP() <= 0) {
					//TODO: take off grid
					playerList.get(PLAYER2).setNumUnits(playerList.get(PLAYER2).
							getNumUnits()- 1);
					unitBoard[mP2.getX()][mP2.getY()] = null;
				}
			}
		}

		modifiedUnits[PLAYER1] = mP1;
		modifiedUnits[PLAYER2] = mP2;
		return modifiedUnits;
	}

	public void produceUnit(Player p, Unit pU, Tile pT) {
		int x = pT.getX();
		int y = pT.getY();

		if (unitBoard[x][y] == null) {
			unitBoard[x][y] = pU;
			p.setCash(p.getCash() - pU.getCost());
			p.setNumUnits(p.getNumUnits()+1);
		}	
	}

	public boolean didWin(Player p) {

		if (p.getPNum() == PLAYER1) {
			if (playerList.get(PLAYER2).getNumUnits() <= 0)  {
				return true;
			}
		}
		else {
			if (playerList.get(PLAYER2).getNumUnits() <= 0)
				return true;
		}

		return false;
	}

	/******************************************************** 
	 *	Move method
	 *	Works by first going north of the player, then checking
	 *	whether or not that spot is traversable.  At each spot,
	 *	it checks movement-1 number of spots left and right.
	 *	This amount decreases by one for every space away
	 *	it is from the unit.
	 *********************************************************/
	private void move(Unit pUnit){
		int movement = pUnit.getMove();

		if(tBoard[pUnit.getX()][pUnit.getY()].getType() == 'm'){
			movement-=2;
		}
		//Top
		for(int r = pUnit.getX()-1; r<movement && r>=0; r--){
			if(possibleMove(pUnit, r, pUnit.getY())==false){
				break;
			}
			else{
				moves[r][pUnit.getY()] = 'x';
				for(int j = 1; j < movement; j++){
					if(possibleMove(pUnit,r, pUnit.getY()+j)==true){
						moves[r][pUnit.getY()+j] = 'x';
					}
				}
				for(int j = 1; j > movement; j++){
					if(possibleMove(pUnit,r, pUnit.getY()-j)==true){
						moves[r][pUnit.getY()+j] = 'x';
					}
				}
			}
		}
		//Bottom
		for(int r = pUnit.getX()+1; r<movement && r<mapSize; r++){
			if(possibleMove(pUnit, r, pUnit.getY())==false){
				break;
			}
			else{
				moves[r][pUnit.getY()] = 'x';
				// Checking Left
				for(int j = 1; j < movement; j--){
					if(possibleMove(pUnit,r, pUnit.getY()+j)==true){
						moves[r][pUnit.getY()+j] = 'x';
					}
				}
				// Checking Right
				for(int j = 1; j > movement; j++){
					if(possibleMove(pUnit,r, pUnit.getY()-j)== true){
						moves[r][pUnit.getY()-j] = 'x';
					}
				}
			}
		}
		//Left
		for(int c = pUnit.getY()-1; c<movement && c>=0; c--){
			if(possibleMove(pUnit, pUnit.getX(), c)==false){
				break;
			}
			else{
				moves[pUnit.getX()][c] = 'x';
				// Checking below
				for(int j = 1; j < movement; j++){
					if(possibleMove(pUnit, pUnit.getX()+j, c)==true){
						moves[pUnit.getX()+j][c] = 'x';
					}
				}
				// Checking above
				for(int j = 1; j > movement; j++){
					if(possibleMove(pUnit,pUnit.getX()-j, c)==true){
						moves[pUnit.getX()-j][c] = 'x';
					}
				}

			}
		}
		//Right
		for(int c = pUnit.getY()+1; c<movement && c<mapSize; c++){
			if(possibleMove(pUnit, pUnit.getX(), c)==false){
				break;
			}
			else{
				moves[pUnit.getX()][c] = 'x';
				// Checking Below
				for(int j = 1; j < movement; j--){
					if(possibleMove(pUnit,pUnit.getX()+j, c)==true){
						moves[pUnit.getX()+j][c] = 'x';
					}
				}
				// Checking Above
				for(int j = 1; j > movement; j++){
					if(possibleMove(pUnit,pUnit.getX()-j, c)==true){
						moves[pUnit.getX()-j][c] = 'x';
					}
				}
			}
		}

		for(int i = 0; i < mapSize; i++)
			for(int j= 0; j < mapSize; j++){
				boolean adj = false;
				if(moves[i][j] == 'x'){
					if(i-1 >=0 && moves[i-1][j] == 'x')
						adj = true;
					if(i+1 < mapSize && moves[i+1][j] == 'x')
						adj = true;
					if(j-1 >= 0 && moves[i][j-1] == 'x')
						adj = true;
					if(j+1 < mapSize && moves[i][j+1] == 'x')
						adj = true;
					
					if(adj == false)
						moves[i][j] = '-';
				}
			}
	}
	

/******************************************************** 
 *	canMove is used by the move method.  It checks for 
 *	making sure that only infantry and mech infantry can
 *	move on mountains.  
 *	Later we can change this method to account for water
 *	and water units
 *********************************************************/
private boolean possibleMove(Unit pUnit, int x, int y){
	boolean retval = true;

	if(tBoard[x][y].getType()=='m'&&pUnit.getType()!='i' ||
			tBoard[x][y].getType()=='m'&&pUnit.getType()!='m'){
		retval = false;
	}

	return retval;
}






/**private void move(unit pUnit){
		moveUnit(pUnit, pUnit.getMovement(), pUnit.getX(), pUnit.getY());
	}*/

/******************************************************** 
 *	Move Method - WITH RECURSION
 *	This is if we want to test the limits of the phone.
 *********************************************************/
/**
	private void move_Unit(unit pUnit, int movesLeft, int lastX, int lastY){
		if(movesLeft == 0){
		}
		else if(tBoard[lastX][lastY] == 'x'){
		}
		else{
			int unitX = lastX;
			int unitY = lastY;
			if(canMove(pUnit, lastX+1, lastY){
				unitBoard[lastX+1][lastY];
				move(pUnit, movesLeft-1, lastX+1, lastY);
			}
			if(canMove(pUnit, lastX-1, lastY){
				unitBoard[lastX-1][lastY];
				move(pUnit, movesLeft-1, lastX-1, lastY);
			}
			if(canMove(pUnit, lastX, lastY+1){
				unitBoard[lastX][lastY+1];
				move(pUnit, movesLeft-1, lastX, lastY+1);
			}
			if(canMove(pUnit, lastX, lastY-1){
				unitBoard[lastX][lastY-1];
				move(pUnit, movesLeft-1, lastX, lastY-1);
			}
		}
	}
 */

}
