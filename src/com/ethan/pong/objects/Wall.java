package com.ethan.pong.objects;

import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.OpenGLRenderable;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.Rectangle;
import com.charon.global.world.RectangularObject;

public class Wall extends RectangularObject implements OpenGLRenderable{

	public Wall(float width, float height, float depth, float[] location, float mass) {
		super(height, width, depth, location, mass);
		
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		try {
			graphics.shader_manager.getShaderVariable("set_Color").setValue(new float[] { 0.7f, 0.7f, 0.7f, 1 });
			Rectangle.fillRectangle(graphics, "in_Position", getX(), getY(), getWidth(), getHeight());
			
		} catch (ShaderVariableException e) {
			e.printStackTrace();
		}
	}

}
