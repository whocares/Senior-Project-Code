package maps;

import java.io.*;

import terrain.Tile;

public class MapReader {
	private Tile[][] map;
	private int size;
	private String[]line;
	
	public MapReader(String pFileName) {
		int c = 0;
		
		try {
			FileInputStream fstream = new FileInputStream("pFileName");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				size = strLine.length();
				line[c] = strLine;
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public Tile[][] createMap() {
		for (int r = 0; r < size; r++) {
			for (int i = 0; i < size; i++) {
				Tile t = new Tile(line[r].charAt(i));
				map[r][i] = t;
			}
		}
		return map;
	}
}
