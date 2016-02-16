package com.ethan.pong.objects;

import java.awt.Dimension;
import java.util.ArrayList;

import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.OpenGLRenderable;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.Elipse;
import com.charon.global.graphics.opengl.shapes.Rectangle;
import com.charon.global.world.SolidObject;

public class Stage implements OpenGLRenderable{

	private static final int WALL_WIDTH = 50;
	private ArrayList<Wall> walls = new ArrayList<>();
	private ArrayList<Goal> goals = new ArrayList<>();
	
	public Stage( int width, int height ){
		walls.add( new Wall( width, WALL_WIDTH, WALL_WIDTH, new float[] { 0, 0, 0 }, 1 ) );
		walls.add( new Wall( width, WALL_WIDTH, WALL_WIDTH, new float[] { 0, height-WALL_WIDTH, 0 }, 1 ) );
		goals.add( new Goal( WALL_WIDTH, height - 2*WALL_WIDTH, WALL_WIDTH, new float[] { 0, WALL_WIDTH, 0 }, 1 ) );
		goals.add( new Goal( WALL_WIDTH, height - 2*WALL_WIDTH, WALL_WIDTH, new float[] { width - WALL_WIDTH, WALL_WIDTH, 0 }, 1 ) );
	}
	
	@Override
	public void render(OpenGLGraphics graphics) {
		try {
			Dimension window = graphics.getWindowDimension();
			
			/*
			 * The borders
			 */
			for( Wall wall : walls ){
				wall.render(graphics);
			}
			
			
			/*
			 * The goals
			 */
			for( Goal goal : goals ){
				goal.render(graphics);
			}
			
			
			/*
			 * Field decorations
			 */
			graphics.shader_manager.getShaderVariable("set_Color").setValue(new float[] { 0.9f, 0.9f, 0.9f, 1 });
			// Middle line
			Rectangle.fillRectangle(graphics, "in_Position", (window.width-2)/2.0f, 50, 2, window.height-100);
			
			// Middle circle
			Elipse.drawElipse(graphics, "in_Position", window.width/2, window.height/2, 200, 200);
			
		} catch (ShaderVariableException e) {
			e.printStackTrace();
		}
	}

	public void addObject(ArrayList<SolidObject> objects) {
		objects.addAll(walls);
		objects.addAll(goals);
	}

}
