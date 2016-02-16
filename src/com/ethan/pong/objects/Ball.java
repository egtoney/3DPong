package com.ethan.pong.objects;

import java.util.ArrayList;

import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.OpenGLRenderable;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.Elipse;
import com.charon.global.graphics.opengl.shapes.Rectangle;
import com.charon.global.world.CylindricalObject;
import com.charon.global.world.RectangularObject;
import com.charon.global.world.SolidObject;

//public class Ball extends CylindricalObject implements OpenGLRenderable{
public class Ball extends RectangularObject implements OpenGLRenderable{

	public Ball( float[] location ) {
		super(40, 40, 40, location, 1);
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		try {
			graphics.shader_manager.getShaderVariable("set_Color").setValue(new float[] { 1, 0, 0, 1 });
			
//			Elipse.drawElipse(graphics, "in_Position", getX(), getY(), 2*getRadius(), 2*getRadius());
			
			Rectangle.fillRectangle(graphics, "in_Position", getX(), getY(), getWidth(), getHeight());
			
		} catch (ShaderVariableException e) {
			e.printStackTrace();
		}
	}

	public void update( float seconds_passed, ArrayList<SolidObject> objects ) {
		float[] vel = getVelocity();
		float[] scaled_vel = new float[3];
		
		for( int i=0 ; i<3 ; i++ )
			scaled_vel[i] = vel[i] * seconds_passed;
		
		float[] new_location = getLocation();
		for( int i=0 ; i<3 ; i++ )
			new_location[i] += scaled_vel[i];
		
		boolean collides = false;
		boolean goal = false;
		
		for( SolidObject obj : objects ){
			if( colidesWith( new_location, obj ) ){
				if( obj instanceof Goal ){
					goal = true;
				}else{
					float[] new_vel = reflectVelocity( new_location, obj );
					setVelocity( new_vel );
					collides = true;
				}
			}
		}
		
		if( !collides ){
			setLocation( new_location );
			
			if( goal ){
				resetLocation();
			}
		}
		
	}

}
