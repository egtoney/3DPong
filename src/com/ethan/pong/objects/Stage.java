package com.ethan.pong.objects;

import java.util.ArrayList;

import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.OpenGLRenderable;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.Rectangle;
import com.charon.global.util.Vector;
import com.charon.global.world.SolidObject;

public class Stage implements OpenGLRenderable{

	private static final int WALL_WIDTH = 1;
	private ArrayList<Wall> walls = new ArrayList<>();
	private ArrayList<Goal> goals = new ArrayList<>();
	private float width = 0;
	private float height = 0;
	
	public Stage( float stage_width, float stage_height ){
		this.width = stage_width;
		this.height = stage_height;

		walls.add( new Wall( stage_width, WALL_WIDTH, WALL_WIDTH, new Vector( 0, -WALL_WIDTH, 0 ), 1 ) );
		walls.add( new Wall( stage_width, WALL_WIDTH, WALL_WIDTH, new Vector( 0, stage_height, 0 ), 1 ) );
		walls.add( new Wall( 1000, 1000, WALL_WIDTH, new Vector( -1000, -400, 0 ), 1 ) );
		walls.add( new Wall( 1000, 1000, WALL_WIDTH, new Vector( stage_width, -400, 0 ), 1 ) );
		goals.add( new Goal( WALL_WIDTH, stage_height, WALL_WIDTH, new Vector ( 0, 0, 0 ), 1 ) );
		goals.add( new Goal( WALL_WIDTH, stage_height, WALL_WIDTH, new Vector ( stage_width - WALL_WIDTH, 0, 0 ), 1 ) );
	}
	
	@Override
	public void render(OpenGLGraphics graphics) {
		try {
			
			/*
			 * The goals
			 */
			for( Goal goal : goals ){
				goal.render(graphics);
			}
			
			
			/*
			 * Field decorations
			 */

			String prev = graphics.shader_manager.getActiveShader();
			graphics.shader_manager.useShader("opengl_frame");

			graphics.shader_manager.getShaderVariable("set_Color").setValue(new float[] {0, 1, 1, 1} );
			Rectangle.fillRectangle(graphics, "in_Position", WALL_WIDTH, 0, width-2*WALL_WIDTH, height);

			graphics.shader_manager.useShader(prev);
			
		} catch (ShaderVariableException e) {
			e.printStackTrace();
		}
	}

	public void addObject(ArrayList<SolidObject> objects) {
		objects.addAll(walls);
		objects.addAll(goals);
	}

	public float getWidth() {
		return width;
	}
	
	public float getHeight(){
		return height;
	}

}
