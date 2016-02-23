package com.ethan.pong.objects;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.OpenGLRenderable;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.RectangularPrism;
import com.charon.global.util.Vector;
import com.charon.global.world.RectangularObject;
import com.charon.global.world.SolidObject;

//public class Ball extends CylindricalObject implements OpenGLRenderable{
public class Ball extends RectangularObject implements OpenGLRenderable{

	private boolean finished = false;

	public Ball( Vector location ) {
		super(1, 1, 1, location, 1);
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		try {
			RectangularPrism.fillPrism(graphics, getX(), getY(), getZ(), getWidth(), getHeight(), getDepth());
			
		} catch (ShaderVariableException e) {
			e.printStackTrace();
		}
	}

	public void update( float seconds_passed, Stage stage, ArrayList<SolidObject> objects ) {
		float[] vel = getVelocity();
		float[] scaled_vel = new float[3];
		
		for( int i=0 ; i<3 ; i++ )
			scaled_vel[i] = vel[i] * seconds_passed;
		
		Vector new_location = getLocation();
		
		Vector old_location = new Vector( new_location.x, new_location.y, new_location.z );
		
		new_location.x += scaled_vel[0];
		new_location.y += scaled_vel[1];
		new_location.z += scaled_vel[2];
		
		boolean collides = false;
		boolean goal = false;
		
		for( SolidObject obj : objects ){
			if( collidesWith( new_location, obj ) ){
				if( obj instanceof Goal ){
					goal = true;
					
				}else{
					float[] n_vel = reflectVelocity( new_location, obj );
					float vel_mag = n_vel[0]*n_vel[0] + n_vel[1]*n_vel[1];
					float limit = 30*30;
					float scale = (vel_mag > limit) ? limit/vel_mag : 1;
					
					System.out.println(vel_mag+" "+limit);
					
					for( int i=0 ; i<3 ; i++ )
						n_vel[i] *= scale;
					
					setVelocity( n_vel );
					collides = true;
				}
			}
		}
		
		if( !collides ){
			if( goal ){
				finishGame();
			}
			
			if( new_location.x < 0 || new_location.x > stage.getWidth() || new_location.y < 0 || new_location.y > stage.getHeight() ){
				setLocation(old_location);
			}
		}
	}
	
	private synchronized void finishGame(){
		if( !finished ){
			finished = true;
			Timer t = new Timer();
			t.schedule(new TimerTask(){
	
				@Override
				public void run() {
					System.out.println("finished");
					resetLocation();
					finished = false;
				}
				
			}, 60);
		}
	}

}
