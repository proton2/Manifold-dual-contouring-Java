package core.kernel;

import core.configs.Default;
import manifoldDC.MdcWrapper;
import simpleDC.DcWrapper;

/**
 * 
 * @author oreon3D
 * The RenderingEngine manages the render calls of all 3D entities
 * with shadow rendering and post processing effects
 *
 */
public class RenderingEngine {
	
	private Window window;

	private MdcWrapper dcWrapper;
	
	public RenderingEngine()
	{
		window = Window.getInstance();
		dcWrapper = new MdcWrapper();
	}
	
	public void init()
	{
		window.init();
	}

	public void render()
	{	
		Camera.getInstance().update();
		
		Default.clearScreen();

		dcWrapper.update();
		dcWrapper.render();
		
		// draw into OpenGL window
		window.render();
	}
	
	public void update(){}
	
	public void shutdown(){
		dcWrapper.shutDown();
	}
}
