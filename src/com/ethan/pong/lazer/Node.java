package com.ethan.pong.lazer;

import java.util.ArrayList;
import java.util.HashMap;

public class Node{
	
	public static int connection_count = 0;
	public static HashMap< Integer, Character > visited = new HashMap<>();
	public static int max_connections = 0;
	public static int recursion_depth = 0;
	public static String result = "";
	
	public int x, y;
	private ArrayList<Node> neighbors = new ArrayList<>();
	
	public Node( int x, int y ){
		this.x = x;
		this.y = y;
	}
	
	public float sqrdDistance(Node node) {
		return (float) x*node.x + y*node.y;
	}
	
	public void addNeighbors(Node node) {
		for( Node n : node.neighbors ){
			if( !neighbors.contains(n) ){
				tryToConnect(n);
				n.forceAddNeighbors(this);
			}
		}
	}
	
	public void forceAddNeighbors(Node node){
		if( !neighbors.contains(node) ){
			neighbors.add(node);
		}
	}
	
	public void completeGraph(){
		for( Node n : neighbors ){
			n.forceAddNeighbors(this);
		}
	}

	public void tryToConnect( Node n ){
		int dx = this.x - n.x;
		int dy = this.y - n.y;
		if( Math.abs(dx) < 5 && Math.abs(dy) < 5 ){
			connection_count++;
			neighbors.add(n);
		}
		if( max_connections < neighbors.size() )
			max_connections = neighbors.size();
	}
	
	public void getPath(){
		recursion_depth++;
		if(recursion_depth < 100000){
			result += ((char) x) + "" + ((char) y) + "";
				
			for( Node n : neighbors ){
				int hash = n.x*1000+n.y;
				if( !visited.containsKey(hash) ){
					visited.put(hash, ' ');
					n.getPath();
				}
			}
			if( x==0 ) x=1; // yeah, special case...
			result += ((char) x) + "" + ((char) y)+ "";
		}
	}
	
}
