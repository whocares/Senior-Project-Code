package player;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import gameplay.Logic;
import units.Unit;
import terrain.Tile;
import java.lang.Math;

public class AI extends Player{
	private boolean availableMove;
	private boolean availableAttack;
	private boolean availablePurchase;
	private boolean availableCapture;

	private ArrayList<Unit> unitsWithMoves;
	private ArrayList<Unit> unitsWithAttacks;

	Logic log;
	int size;
	
	enum types {
		ANTIAIR, APC, ARTILLERY, BATTLESHIP, BOMBER, CHOPPERA, CHOPPERB,
		CRUISER, FIGHTERJET, HEAVYTANK, INFANTRY, LANDER, MECH, MEDTANK,
		MISSILE, RECON, ROCKETS, SUB, TANK
	};

	public AI(String pN, int pNum, char fact) {
		super("Herp Derp 4000", 2, fact);	
	}

	public void startTurn(){
		boolean endTurn = false;

		//determine possible actions
		availableMove = canIMove();
		availableAttack = canIAttack();
		availablePurchase = canIBuy();
		availableCapture = canICapture();

		//Loop through possible actions
		while(endTurn == false){

			if(availablePurchase){
				//buy a unit
			}
			if(availableMove){
				//move units
				if(availableCapture){
					//capture building
				}
				if(availableAttack){
					attack();	
				}
				//if no other captures or attacks but still moves
				moveCloserToEnemies();
			}

			//if no more possible moves
			endTurn = true;
		}
	}

	private void attack() {
		ArrayList<Unit> attackers = getPossibleAttacks();
		for (int i = 0; i < attackers.size(); i++) {
			Unit attacker = attackers.get(i);


		}
	}

	private void moveCloserToEnemies() {
		boolean notDone = true; 
		ArrayList<Unit> toMove; //units to be moved

		int count = 0; 

		Tile[][] tBoard = log.getTBoard(); //current tile board

		int hx = 0; //enemy HQ x
		int hy = 0; //enemy HQ y
		int gx = -1; //The X coordinate to go to
		int gy = -1; //The Y coordinate to go to

		int x = -1;
		int y = -1;
		int currDist = 0;

		do { 
			toMove = getPossibleMoves();

			//pmoves represents the + and - of where the unit can move 
			//at its current X and Y location!
			Unit moving = toMove.get(count);
			int mx = moving.getX();
			int my = moving.getY();
			char[][] pMoves = log.getMoves(toMove.get(count));

			//identifies enemies headquarters
			for (int r = 0; r < log.getSize(); r++) {
				for (int c = 0; c < log.getSize(); c++) {
					if (tBoard[r][c].getType() == 'h') {
						hx = r;
						hy = c;
					}
				}
			}

			//calculate the slope of the line to see 
			currDist = getDistance(hx, hy, mx, my);

			//move the current unit towards that point
			for (int r = 0; r < pMoves.length; r++) {
				gx = mx + r;
				for (int c = 0; c < pMoves.length; c++) {
					gy = my + c;

					if (currDist > getDistance(hx, hy, mx, gy)) {
						currDist = getDistance(hx, hy, mx, gy);
						x = mx; 
						y = gy;
					}
					if (currDist > getDistance(hx, hy, gx, my)) { 
						currDist = getDistance(hx, hy, gx, my);
						x = gx;
						y = my;
					}
					if (currDist > getDistance(hx, hy, gx, gy)) {
						currDist = getDistance(hx, hy, gx, gy);
						x = gx;
						y = gy;
					}
				}
			}
			log.moveUnit(toMove.get(count), x, y);

			count++;

			if (count >= toMove.size()) 
				notDone = false;
		} while (notDone);


	}

	/**
	 * Gets the distance from point a (x1, y1) to point b (x2, y2) 
	 * For the AI this means a is the hQ and b is the unit
	 */
	private int getDistance(int x1, int y1, int x2, int y2) {
		//while this returns a double we don't need to deal with decimals for our purposes
		int distance = (int) Math.sqrt( Math.pow((x2 - x1), 2) + Math.pow( (y2-y1), 2));

		return distance;
	}

	public void getLogic(Logic pLog) {
		log = pLog;
		size = log.getSize();
	}

	public ArrayList<Unit> getPossibleMoves() {
		Unit[][] uBoard = log.getUB();

		ArrayList<Unit> unitsWithMoves = new ArrayList<Unit>();

		//search unit board for units
		for(int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				//checks if current tile isn't empty and if I own the unit
				if (uBoard[row][col] != null && uBoard[row][col].getOwner() == getPNum()) {
					//checks to see if the unit has already moved this turn
					if(!uBoard[row][col].getHasMoved()){
						unitsWithMoves.add(uBoard[row][col]); //adds unit to our available moves
					}
				}
			}
		}
		return unitsWithMoves;
	}

	public ArrayList<Unit> getPossibleAttacks() {
		Unit[][] uBoard = log.getUB();

		ArrayList<Unit> unitsWithAttacks = new ArrayList<Unit>();

		//search unit board for units
		for(int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				//checks if current tile isn't empty and if I own the unit
				if (uBoard[row][col] != null && uBoard[row][col].getOwner() == getPNum()) { //must check tile type!!
					//checks to see if the unit has already attacked this turn
					if(!uBoard[row][col].getHasAttacked()){
						unitsWithAttacks.add(uBoard[row][col]); //adds unit to our available attacks
					}
				}
			}
		}
		return unitsWithAttacks;
	}

	

	
	
	/************************************************************************
	 * AI decides what units to create
	 ***********************************************************************/
	public void prodUnits() {
		Tile[][] map = log.getTBoard();

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (map[r][c].getType() == 'p' &&
						map[r][c].getOwner() == playNum) {
					getUnitWeights();
				}
			}
		}
	}
	
	
	/************************************************************************
	 * AI looks at building units to counter enemy units.
	 * Array holds ints for each unit in alphabetical order.
	 * For every unit that the enemy has, the counter to that unit is inc'd
	 * in the corresponding array index.
	 * Example:  Enemy has fighter jet.  Anti-Air, array index of 1, is inc'd
	 * @return int[] units
	 ***********************************************************************/
	private int[] counterEnemyUnits(){
		int[] counters = new int[19];
		Arrays.fill(counters, 0);
		Unit[][] unitBoard = log.getUB();
		String unitType;
		
		
		for(int i = 0; i<log.getSize(); i++)
			for(int j = 0; j < log.getSize(); j++)
				if(unitBoard[i][j] != null)
					if(unitBoard[i][j].getOwner() != this.getPNum()){
						unitType = unitBoard[i][j].getName();
						
						if(unitType == "Anti-Air"){
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
						//}else if(unitType == "APC"){
							//Do we really need to counter APCs?!?!
						}else if(unitType == "Artillery"){
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.MECH.ordinal()]++;
						}else if(unitType == "Bomber"){
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.MISSILE.ordinal()]++;
							counters[types.FIGHTERJET.ordinal()]++;
						}else if(unitType == "CHOPPERB"){
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.MISSILE.ordinal()]++;
							counters[types.CRUISER.ordinal()]++;
							counters[types.CHOPPERA.ordinal()]++;
							counters[types.FIGHTERJET.ordinal()]++;
						}else if(unitType == "CHOPPERA"){
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.MISSILE.ordinal()]++;
							counters[types.CRUISER.ordinal()]++;
							counters[types.FIGHTERJET.ordinal()]++;
						}else if(unitType == "Fighter Jet"){
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.MISSILE.ordinal()]++;
							counters[types.CRUISER.ordinal()]++;
							counters[types.FIGHTERJET.ordinal()]++;
						}else if(unitType == "Heavy Tank"){
							counters[types.BOMBER.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
						}else if(unitType == "Infantry"){
							counters[types.RECON.ordinal()]++;
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.CHOPPERA.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
						}else if(unitType == "Mech"){
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.CHOPPERA.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
						}else if(unitType == "MedTank"){
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
						}else if(unitType == "Missile"){
							counters[types.MECH.ordinal()]++;
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
						}else if(unitType == "Recon"){
							counters[types.MECH.ordinal()]++;
							counters[types.ANTIAIR.ordinal()]++;
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
						}else if(unitType == "Rockets"){
							counters[types.MECH.ordinal()]++;
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.CHOPPERA.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
						}else if(unitType == "Tank"){
							counters[types.TANK.ordinal()]++;
							counters[types.MEDTANK.ordinal()]++;
							counters[types.HEAVYTANK.ordinal()]++;
							counters[types.ARTILLERY.ordinal()]++;
							counters[types.ROCKETS.ordinal()]++;
							counters[types.BATTLESHIP.ordinal()]++;
							counters[types.CHOPPERA.ordinal()]++;
							counters[types.BOMBER.ordinal()]++;
							counters[types.MECH.ordinal()]++;
						}//else if(unitType == ""){
							
						//}
						
						
					}
					
		
		
		return counters;
	}
	
	/************************************************************************
	 * Counts the number of "free" buildings on the map
	 * This is used to tell the AI whether or not it should build more
	 * units to capture or not
	 * @return int num (of uncaptured buildings)
	 ***********************************************************************/
	private int getUncapturedBuildings(){
		Tile[][] mapBoard = log.getTBoard();
		int num = 0;
		
		for(int i = 0; i < log.getSize(); i++)
			for(int j = 0; j < log.getSize(); j++){
				if(mapBoard[i][j].getType() == 'b' &&mapBoard[i][j].getOwner()==-1){
					num++;
				}
		}
		
		
		return num;
	}
	

	/************************************************************************
	 * Calculates the difference in unit weights and counts
	 * retVal[0] is the difference in unit weights.  
	 * <0, Opponent army is stronger
	 * 
	 * retVal[1] is the difference in unit count
	 * <0, Opponent has more units
	 * 
	 * @return int[] retVal:
	 ***********************************************************************/
	private int[] getUnitWeights() {

		// Element 
		int[] retVal= new int[2];
		retVal[0] = 0;
		retVal[1] = 0;

		Unit[][] unitBoard = log.getUB();

		for(int i = 0; i<log.getSize(); i++)
			for(int j = 0; j < log.getSize(); j++){
				if(unitBoard[i][j] != null){
					if(unitBoard[i][j].getOwner() == this.getPNum()){
						retVal[0] += unitBoard[i][j].getArmor();
						retVal[1]++;
					}
					else{
						retVal[0] -= unitBoard[i][j].getArmor();
						retVal[1]--;
					}
				}


			}


		return retVal;
	}

	/************************************************************************
	 * Update unitsWithMoves arrayList and determine if moves are available
	 * @return boolean canIMove
	 ***********************************************************************/
	public boolean canIMove(){
		boolean moveAvailable = false;
		//find moves
		unitsWithMoves = getPossibleMoves();
		if(unitsWithMoves.size() > 0) {
			moveAvailable = true;
		}
		return moveAvailable;
	}

	/************************************************************************
	 * Update unitsWithAttacks arrayList and determine if attacks are available
	 * @return boolean canIAttack
	 ***********************************************************************/
	public boolean canIAttack(){
		boolean attackAvailable = false;
		//find attacks
		unitsWithAttacks = getPossibleAttacks();
		if(unitsWithAttacks.size() > 0){
			attackAvailable = true;
		}
		return attackAvailable;
	}

	public boolean canIBuy(){
		boolean buyAvailable = false;
		//find buys
		if(findUnitsICanAfford().size() > 0){
			buyAvailable = true;
		}

		return buyAvailable;
	}

	public boolean canICapture(){
		boolean captureAvailable = false;
		//find captures
		//if possibleCaptures.lenght > 0{
		//capturesAvailable = true;

		return captureAvailable;
	}

	/** Move unit
	 * If unit can't attack or capture:
	 * 1. find x,y coordinates of nearest enemy
	 * 2. move unit into same row or column as enemy
	 */

}
