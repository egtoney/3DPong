package com.ethan.pong.objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import com.charon.global.graphics.opengl.OpenGLFrame;
import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.OpenGLRenderable;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.RectangularPrism;
import com.charon.global.util.Vector;
import com.charon.global.world.RectangularObject;

public class Paddle extends RectangularObject implements OpenGLRenderable{

	public enum PlayerSlot{
		PLAYER_ONE,
		PLAYER_TWO
	};
	
	private PaddleControlls controller;
	private float pps = 6; // units/second
	
	public Paddle(OpenGLFrame frame, PlayerSlot player, Vector location) {
		super(3, 1, 1, location, 1);
		
		switch( player ){
		case PLAYER_ONE:
			controller = new PaddleControlls( GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S );
			break;
		case PLAYER_TWO:
			controller = new PaddleControlls( GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN );
			break;
		}
		
		frame.addKeyListener(controller);
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		try {
			RectangularPrism.fillPrism(graphics, getX(), getY(), getZ(), getWidth(), getHeight(), getDepth());
			
		} catch (ShaderVariableException e) {
			e.printStackTrace();
		}
	}
	
	public void update( float seconds_passed ){
		float dy = 0;
		
		if( controller.key_state[PaddleControlls.KEY_UP] ){
			dy++;
		}
		if( controller.key_state[PaddleControlls.KEY_DOWN] ){
			dy--;
		}
		
		dy *= pps*seconds_passed;
		
		Vector location = getLocation();
		
		if( dy > 0 ){
			if( location.y+getHeight()+dy >= 9 ){
				dy = 9.0f - location.y - getHeight();
			}
		}else if( dy < 0 ){
			if( location.y+dy <= 0 ){
				dy = 0.0f - location.y;
			}
		}
		
		this.move(new Vector (0, dy, 0));
	}
	
	private class PaddleControlls extends GLFWKeyCallback {

		public static final int KEY_UP = 0;
		public static final int KEY_DOWN = 1;
		
		public final boolean[] key_state = { false, false };
		public final int up_key;
		public final int down_key;
		
		public PaddleControlls( int up_key, int down_key ){
			this.up_key = up_key;
			this.down_key = down_key;
		}
		
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			boolean state = ( action == GLFW.GLFW_RELEASE ) ? false : true ;
			
			if( key == up_key ){
				key_state[ KEY_UP ] = state;
			}else if( key == down_key ){
				key_state[ KEY_DOWN ] = state;
			}
		}
		
	}
	
}
