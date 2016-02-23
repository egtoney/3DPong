package com.ethan.pong;

import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.charon.global.graphics.opengl.GraphicsFunction;
import com.charon.global.graphics.opengl.OpenGLFrame;
import com.charon.global.graphics.opengl.OpenGLGraphics;
import com.charon.global.graphics.opengl.RenderFunction;
import com.charon.global.graphics.opengl.shaders.ShaderVariableException;
import com.charon.global.graphics.opengl.shapes.Points;
import com.charon.global.graphics.opengl.shapes.Rectangle;
import com.charon.global.util.Vector;
import com.charon.global.world.SolidObject;
import com.ethan.pong.lazer.LazerCommunication;
import com.ethan.pong.lazer.Node;
import com.ethan.pong.objects.Ball;
import com.ethan.pong.objects.Paddle;
import com.ethan.pong.objects.Stage;

public class GameRunner {

	private static Paddle left_paddle;
	private static Paddle right_paddle;
	private static Stage main_stage;
	private static Ball main_ball;
	
	private static Dimension depth_map_size = new Dimension(250, 250);
	
	private static ArrayList< SolidObject > objects = new ArrayList<>();

	//Set pixels ((width*2)* (height*2))
	//It has to have twice the size of shadowmap size
	private static IntBuffer depth_image = BufferUtils.createIntBuffer(depth_map_size.width*depth_map_size.height*4);
	protected static IntBuffer fbuffer1;
	protected static IntBuffer fbuffer2;
	protected static IntBuffer dbuffer;
	
	protected static Node previous = new Node(0,0);
	
	protected static boolean has_init = false;
	protected static boolean using_pi = true;

	private static float stage_width = 18;
	private static float stage_height = 9;
	private static float ball_width = 1;
	private static float ball_height = 1;
	
	public static void main(String[] args){
		OpenGLFrame frame = new OpenGLFrame("EDay Pong", 255, 255, false, false);
		
		frame.setVisible(true);
		frame.useShader("opengl_frame");
		frame.setDesiredFPS(60);
		frame.setFPSVisible(true);
		
		LazerCommunication com = new LazerCommunication();
		Timer t = new Timer();
		t.scheduleAtFixedRate(com, 1000, 1000);
		
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
					
					float dx = ( sx * ( rand.nextFloat() + 2 ) ) * 6.0f;
					float dy = ( sy * ( rand.nextFloat() + 0.25f ) ) * 3.0f;
					main_ball.setVelocity( new float[] { dx, dy, 0 } );
				}
			}
			
		});
		
		left_paddle = new Paddle( frame, Paddle.PlayerSlot.PLAYER_ONE, new Vector( 0, (stage_height-3.0f)/2.0f, 0 ) );
		right_paddle = new Paddle( frame, Paddle.PlayerSlot.PLAYER_TWO, new Vector( stage_width-1.0f, (stage_height-3.0f)/2.0f, 0 ) );
		main_stage = new Stage( stage_width, stage_height );
		main_ball = new Ball( new Vector( (stage_width - ball_width)/2.0f, (stage_height - ball_height)/2.0f, 0 ) );

		objects.add( left_paddle );
		objects.add( right_paddle );
		main_stage.addObject( objects );
		
		frame.setRenderFunction(new RenderFunction(){

			@Override
			public void render(OpenGLGraphics graphics) {
				if( has_init ){
					if( using_pi )
						GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbuffer2.get(0));
					
					GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
					
					// Render scene into Frame buffer first
					graphics.setDisplay3D();
					
					renderScene( graphics );
					
					if( using_pi ){
						//Read pixels from buffer
						GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbuffer2.get(0));
						//Read pixels 
						GL11.glReadPixels(0,
								0,
								depth_map_size.width,
								depth_map_size.height,
								GL11.GL_DEPTH_COMPONENT,
								GL11.GL_UNSIGNED_INT,
								depth_image);
						
						//Switch back to default FBO
						GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
						GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT ); 
						
						renderScene( graphics );
		
						GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT ); 
						
						//Draw pixels, format has to have only one 
//					  	GL11.glDrawPixels(depth_map_size.width, depth_map_size.height, GL11.GL_LUMINANCE , GL11.GL_UNSIGNED_INT, depth_image);
						
						IntBuffer depth_points = depth_image.duplicate();
						
						int[][] depth_map = new int[depth_map_size.width][depth_map_size.height];
						
						HashMap<Integer, Node> vert_points = new HashMap<>();
						ArrayList<Node> last_col = new ArrayList<>();
						ArrayList<Node> this_col = new ArrayList<>();
						
						final int RANGE = 1000;
						
						// Negative Y Scan
						float prev_depth = 0;
						float prev_derivative = 0;
						Node.connection_count = 0;
						for( int x=250 ; x>0 ; x-=1 ){
							this_col.clear();
							for( int y=0 ; y<250 ; y+=1 ){
								int depth_buff = depth_points.get();
								depth_map[x-1][y] = depth_buff;
								float derivative = (depth_buff-prev_depth);
								if( Math.abs(derivative-prev_derivative) > RANGE ){
									Node point = new Node(x, y);
									this_col.add(point);
								}
								prev_depth = depth_buff;
								prev_derivative = derivative;
							}
							
							Node last_pt = null;
							for( Node n : this_col ){
								for( Node l : last_col ){
									n.tryToConnect(l);
								}
								if( last_pt != null )
									n.tryToConnect(last_pt);
								last_pt = n;
								
								vert_points.put(n.x*1000+n.y,n);
							}
							last_col.clear();
							last_col.addAll(this_col);
							
						}
		
						HashMap<Integer, Node> hor_points = new HashMap<>();
						ArrayList<Node> last_row = new ArrayList<>();
						ArrayList<Node> this_row = new ArrayList<>();
						
						depth_points.rewind();
						prev_depth = 0;
						prev_derivative = 0;
						Node.connection_count = 0;
						for( int y=0 ; y<250 ; y+=1 ){
							this_row.clear();
							for( int x=0 ; x<250 ; x+=1 ){
								int depth_buff = depth_map[x][y];
								float derivative = (depth_buff-prev_depth);
								if( Math.abs(derivative-prev_derivative) > RANGE ){
									Node point = new Node(x, y);
									this_row.add(point);
								}
								prev_depth = depth_buff;
								prev_derivative = derivative;
							}
		
							Node last_pt = null;
							for( Node n : this_row ){
								for( Node l : last_row ){
									n.tryToConnect(l);
								}
								if( last_pt != null )
									n.tryToConnect(last_pt);
								last_pt = n;
								
								hor_points.put(n.x*1000+n.y,n);
							}
							last_row.clear();
							last_row.addAll(this_row);
							
						}
		
						HashMap<Integer, Node> points = new HashMap<>();
						Set<Integer> hor_keys = hor_points.keySet();
						Set<Integer> vert_keys = vert_points.keySet();
						
						for( int h_k : hor_keys ){
							Node temp = hor_points.get(h_k);
							
							if( vert_points.containsKey(h_k) ){
								temp.addNeighbors( vert_points.get(h_k) );
							}
							
							points.put(temp.x*1000+temp.y, temp);
						}
						
						for( int v_k : vert_keys ){
							points.put( vert_points.get(v_k).x*1000+vert_points.get(v_k).y, vert_points.get(v_k) );
						}
						
						Set<Integer> p_keys = points.keySet();
						Node curr_node = null;
						float min_distance = Float.MAX_VALUE;
						for( int i : p_keys ){
							points.get(i).completeGraph();
							float distance = previous.sqrdDistance(points.get(i));
							if( distance < min_distance ){
								curr_node = points.get(i);
								min_distance = distance;
							}
						}
						previous = curr_node;
						
						Node.result = "";
						Node.visited.clear();
						Node.recursion_depth = 0;
						curr_node.getPath();
						String result = Node.result;
						LazerCommunication.data = Node.result;
						
						Collection<Node> keys = points.values();
						int[] pts = new int[ keys.size() * 2 ];
						int i = 0;
						for( Node n : keys ){
							pts[i++] = n.x;
							pts[i++] = n.y;
						}
						
						graphics.setDisplay2D();
						GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
						
						// Draw points
						try {
							String prev_shader = graphics.shader_manager.getActiveShader();
							
							graphics.shader_manager.useShader("opengl_frame");
							
							graphics.shader_manager.getShaderVariable("set_Color").setValue(new float[] { 0, 1, 0, 1 } );
							Points.drawPoints(graphics, "in_Position", pts);
							
							graphics.shader_manager.useShader(prev_shader);
						} catch (ShaderVariableException e) {
							e.printStackTrace();
						}
					}
					
					graphics.setDisplay2D();
				}
			}
			
		});
		
		frame.queueGraphicsFunction(new GraphicsFunction(){

			@Override
			public void call() {
				// ------------- Depth buffer texture -------------
				fbuffer2 = BufferUtils.createIntBuffer(1);
				dbuffer = BufferUtils.createIntBuffer(1);
				
				GL30.glGenFramebuffers(fbuffer2);
				GL11.glGenTextures(dbuffer);
				
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbuffer2.get(0));
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, dbuffer.get(0));
				
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
						0,
						GL11.GL_DEPTH_COMPONENT,
						depth_map_size.width,
						depth_map_size.height,
						0,
						GL11.GL_DEPTH_COMPONENT,
						GL11.GL_UNSIGNED_INT,
						depth_image);

				//Some parameters
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);   

				//Attach 2D texture to this FBO
				GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
						GL30.GL_DEPTH_ATTACHMENT,
						GL11.GL_TEXTURE_2D,
						dbuffer.get(0),
						0);

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				//Disable color buffer
				//http://stackoverflow.com/questions/12546368/render-the-depth-buffer-into-a-texture-using-a-frame-buffer
				GL11.glDrawBuffer(GL11.GL_NONE);
				GL11.glReadBuffer(GL11.GL_NONE);

				//Set default frame buffer before doing the check
				//http://www.opengl.org/wiki/FBO#Completeness_Rules
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

				int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

				// Always check that our framebuffer is ok
				if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
				{
					System.err.println("Can not use FBO! Status error:" + status);
				}
				
				has_init = true;
			}
			
		});
		
		frame.setDesiredFPS(30);
		
		Timer t1 = new Timer();
		t1.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				left_paddle.update( 0.01f );
				right_paddle.update( 0.01f );
				main_ball.update( 0.01f, main_stage, objects );
			}
			
		}, 0, 10);
	}

	public static void renderScene( OpenGLGraphics graphics ){
		graphics.shader_manager.useShader("do_nothing");

		GL11.glTranslatef(0, 0, -26);
		
		GL11.glRotatef(-50, 1, 0, 0);
		
		float percent = main_ball.getX() / stage_width - 0.5f;
		
		GL11.glRotatef(5*percent, 0, 1, 0);

		GL11.glTranslatef(-stage_width/2.0f, -stage_height/2.0f, 0);
		
		main_stage.render(graphics);
		left_paddle.render(graphics);
		right_paddle.render(graphics);
		main_ball.render(graphics);
	}
	
}
