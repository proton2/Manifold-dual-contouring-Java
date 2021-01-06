package core.kernel;

import core.utils.ImageLoader;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * 
 * @author oreon3D
 * GLFW Window implementation
 *
 */
public class Window {

	private static Window instance = null;

	private long window;
	private int width;
	private int height;
	
	public static Window getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Window();
	    }
	      return instance;
	}
	
	public void init(){}
	
	public void create(int width, int height)
	{
		setWidth(width);
		setHeight(height);
		
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);	
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);	
		
		window = glfwCreateWindow(width, height, "OREON ENGINE", 0, 0);
		
		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}
		
		ByteBuffer bufferedImage = ImageLoader.loadImageToByteBuffer("./res/logo/oreon_lwjgl_icon32.png");
		
		GLFWImage image = GLFWImage.malloc();
		
		image.set(32, 32, bufferedImage);
		
		GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
		
		glfwSetWindowIcon(window, images);
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwShowWindow(window);
	}
	
	public void render()
	{
		glfwSwapBuffers(window);
	}
	
	public void dispose()
	{
		glfwDestroyWindow(window);
	}
	
	public boolean isCloseRequested()
	{
		return glfwWindowShouldClose(window);
	}
	
	public void setWindowSize(int x, int y){
		glfwSetWindowSize(window, x, y);
		setHeight(y);
		setWidth(x);
		Camera.getInstance().setProjection(70, x, y);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public long getWindow() {
		return window;
	}

	public void setWindow(long window) {
		this.window = window;
	}
}
