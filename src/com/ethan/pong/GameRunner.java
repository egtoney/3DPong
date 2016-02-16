package com.ethan.pong;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import com.charon.global.graphics.opengl.GraphicsFunction;
import com.charon.global.graphics.opengl.OpenGLFrame;
import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.RenderFunction;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.Rectangle;
import com.charon.global.world.SolidObject;
import com.ethan.pong.objects.Ball;
import com.ethan.pong.objects.Paddle;
import com.ethan.pong.objects.Stage;

public class GameRunner {

	private static Paddle left_paddle;
	private static Paddle right_paddle;
	private static Stage main_stage;
	private static Ball main_ball;
	
	private static ArrayList< SolidObject > objects = new ArrayList<>();

	public static void main(String[] args){
		OpenGLFrame frame = new OpenGLFrame("EDay Pong", true, true);
		
		frame.setVisible(true);
		frame.useShader("opengl_frame");
		frame.setDesiredFPS(60);
		frame.setFPSVisible(true);
		
		frame.addKeyListener(new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if( key == GLFW.GLFW_KEY_ESCAPE ){
					frame.close();
					System.exit(0);
				}
				if( action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_SPACE ){
					Random rand = new Random();
					float sx = ( rand.nextInt() % 2 == 0 ) ? -1 : 1 ;
					float sy = ( rand.nextInt() % 2 == 0 ) ? -1 : 1 ;
					
					float dx = sx * rand.nextFloat() * 400.0f;
					float dy = sy * rand.nextFloat() * 400.0f;
					main_ball.setVelocity( new float[] { dx, dy, 0 } );
				}
			}
			
		});

		left_paddle = new Paddle( frame, Paddle.PlayerSlot.PLAYER_ONE, new float[] {0, (900-300)/2, 0} );
		right_paddle = new Paddle( frame, Paddle.PlayerSlot.PLAYER_TWO, new float[] {1600-50, (900-300)/2, 0} );
		main_stage = new Stage( 1600, 900 );
		main_ball = new Ball( new float[] { 800, 450, 0 } );

		objects.add( left_paddle );
		objects.add( right_paddle );
		main_stage.addObject( objects );
		
		frame.setRenderFunction(new RenderFunction(){

			@Override
			public void render(OpenGLGraphics graphics) {
				main_stage.render(graphics);
				left_paddle.render(graphics);
				right_paddle.render(graphics);
				main_ball.render(graphics);
			}
			
		});
		
		frame.queueGraphicsFunction(new GraphicsFunction(){

			@Override
			public void call() {
				
			}
			
		});
		
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				left_paddle.update( 0.01f );
				right_paddle.update( 0.01f );
				main_ball.update( 0.01f, objects );
			}
			
		}, 0, 10);
	}
	
}
