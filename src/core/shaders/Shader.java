package core.shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import java.util.HashMap;

import core.math.Matrix4f;
import core.math.Quaternion;
import core.math.Vec2f;
import core.math.Vec3f;
import core.scene.GameObject;
import core.utils.BufferUtil;

/**
 * 
 * @author oreon3D
 * Shader Program Template
 *
 */
public abstract class Shader {

	private int program;
	private HashMap<String, Integer> uniforms;
	
	public Shader()
	{
		program = glCreateProgram();
		uniforms = new HashMap<String, Integer>();
		
		if (program == 0)
		{
			System.err.println("Shader creation failed");
			System.exit(1);
		}	
	}
	
	public void bind()
	{
		glUseProgram(program);
	}
	
	public void addUniform(String uniform)
	{
		int uniformLocation = glGetUniformLocation(program, uniform);
		
		if (uniformLocation == 0xFFFFFFFF)
		{
			System.err.println(this.getClass().getName() + " Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		uniforms.put(uniform, uniformLocation);
	}
	
	public void addUniformBlock(String uniform)
	{
		int uniformLocation =  glGetUniformBlockIndex(program, uniform);		
		if (uniformLocation == 0xFFFFFFFF)
		{
			System.err.println(this.getClass().getName() + " Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		uniforms.put(uniform, uniformLocation);
	}
	
	public void addVertexShader(String text)
	{
		addProgram(text, GL_VERTEX_SHADER);
	}
	
	public void addGeometryShader(String text)
	{
		addProgram(text, GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShader(String text)
	{
		addProgram(text, GL_FRAGMENT_SHADER);
	}
	
	public void addTessellationControlShader(String text)
	{
		addProgram(text, GL_TESS_CONTROL_SHADER);
	}
	
	public void addTessellationEvaluationShader(String text)
	{
		addProgram(text, GL_TESS_EVALUATION_SHADER);
	}
	
	public void addComputeShader(String text)
	{
		addProgram(text, GL_COMPUTE_SHADER);
	}
	
	public void compileShader()
	{
		glLinkProgram(program);

		if(glGetProgrami(program, GL_LINK_STATUS) == 0)
		{
			System.out.println(this.getClass().getName() + " " + glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
		
		glValidateProgram(program);
		
		if(glGetProgrami(program, GL_VALIDATE_STATUS) == 0)
		{
			System.err.println(this.getClass().getName() +  " " + glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
	}
	
	private void addProgram(String text, int type)
	{
		int shader = glCreateShader(type);
		
		if (shader == 0)
		{
			System.err.println(this.getClass().getName() + " Shader creation failed");
			System.exit(1);
		}	
		
		glShaderSource(shader, text);
		glCompileShader(shader);
		
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
		{
			System.err.println(this.getClass().getName() + " " + glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		
		glAttachShader(program, shader);
	}
	
	public void setUniformi(String uniformName, int value)
	{
		glUniform1i(uniforms.get(uniformName), value);
	}
	public void setUniformf(String uniformName, float value)
	{
		glUniform1f(uniforms.get(uniformName), value);
	}
	public void setUniform(String uniformName, Vec2f value)
	{
		glUniform2f(uniforms.get(uniformName), value.getX(), value.getY());
	}
	public void setUniform(String uniformName, Vec3f value)
	{
		glUniform3f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ());
	}
	public void setUniform(String uniformName, Quaternion value)
	{
		glUniform4f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ(), value.getW());
	}
	public void setUniform(String uniformName, Matrix4f value)
	{
		glUniformMatrix4fv(uniforms.get(uniformName), true, BufferUtil.createFlippedBuffer(value));
	}
	
	public void bindUniformBlock(String uniformBlockName, int uniformBlockBinding )
	{
		glUniformBlockBinding(program, uniforms.get(uniformBlockName), uniformBlockBinding);
	}
	
	public void bindFragDataLocation(String name, int index){
		glBindFragDataLocation(program, index, name);
	}
	
	public int getProgram()
	{
		return this.program;
	}
	
	public void updateUniforms(GameObject object){};
}
