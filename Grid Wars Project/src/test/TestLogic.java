package test;

import static org.junit.Assert.*;
import gameplay.Logic;

import maps.MapReader;

import org.junit.Test;

import units.*;

public class TestLogic {
	
	@Test
	public void createLogic() {
		Logic log = new Logic("map1", 'r','b', "Austin", "Matt");
		assertNotNull(log);
	}
	
	@Test
	public void testBoards() {
		Logic log = new Logic("map1", 'r','b', "Austin", "Matt");
		assertNotNull(log.getUB());
		assertNotNull(log.getMoves());
		
		assertNull(log.getUB()[0][0]);
	}
	
	@Test
	public void testInfo() {
		Logic log = new Logic("map1", 'r', 'b', "Austin", "Matt");
		MapReader mr = new MapReader("map1");
		
		assertNotNull(log.getP1());
		assertNotNull(log.getP2());
		
		assertEquals(log.getP1().getPName(), "Austin");
		assertEquals(log.getP2().getPName(), "Matt");
		
		assertEquals(log.getP1().getFact(), 'r');
		assertEquals(log.getP2().getFact(), 'b');
		
		assertEquals(log.getSize(), mr.getSize());
		
		assertEquals(log.getP1().getCash(), 6000);
		
		log.econDay(log.getP1());
		
		assertEquals(log.getP1().getCash(), 9000);
		
		log.getP1().addBuilding();
		log.econDay(log.getP1());
		
		assertEquals(log.getP1().getCash(), 13000);
		
		Infantry inf = new Infantry(1);
		Infantry inf2 = new Infantry(1);
		
		log.produceUnit(log.getP1(), inf, log.getTile(1, 1));
		
		assertEquals(log.getP1().getNumUnits(), 1);
		assertTrue(log.didWin(log.getP1()));
		
		log.produceUnit(log.getP2(), inf2, log.getTile(1,2));
		
		assertEquals(log.getP2().getNumUnits(), 1);
		
		System.out.println("Unit 1 HP: " + log.getUnit(1,1).getHP());
		System.out.println("Unit 2 HP: " + log.getUnit(1,2).getHP());
		
		System.out.println("After battle!");
		
		log.battle(log.getUnit(1, 1), log.getUnit(1, 2), 0);

		System.out.println("Unit 1 HP: " + log.getUnit(1,1).getHP());
		System.out.println("Unit 2 HP: " + log.getUnit(1,2).getHP());
		
		assertTrue(log.getUnit(1, 1).getHP() != 10);
	}
	
}