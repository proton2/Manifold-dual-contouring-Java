package core.utils;

import core.math.Matrix4f;
import core.math.Vec3i;
import core.model.Vertex;
import org.lwjgl.BufferUtils;
import simpleDC.MeshVertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class BufferUtil {

	public static FloatBuffer createFloatBuffer(int size)
	{
		return BufferUtils.createFloatBuffer(size);
	}
	
	public static IntBuffer createIntBuffer(int size)
	{
		return BufferUtils.createIntBuffer(size);
	}

	public static IntBuffer createFlippedBuffer(int... values)
	{
		IntBuffer buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(float... values) {
		FloatBuffer buffer = createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer createFlippedBuffer(byte... values) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}

	public static IntBuffer createFlippedBuffer(List<Integer> indices) {
		IntBuffer buffer = createIntBuffer(indices.size());
		buffer.put(indices.stream().mapToInt(x -> x).toArray());
		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBufferAOS(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * 11);
		for(int i = 0; i < vertices.length; i++) {
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
			buffer.put(vertices[i].getNormal().getX());
			buffer.put(vertices[i].getNormal().getY());
			buffer.put(vertices[i].getNormal().getZ());
			if(vertices[i].getTextureCoord()!=null) {
				buffer.put(vertices[i].getTextureCoord().getX());
				buffer.put(vertices[i].getTextureCoord().getY());
			}
		}
		buffer.flip();
		return buffer;
	}

	public static FloatBuffer createDcFlippedBufferAOS(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * 9);
		for (Vertex vertice : vertices) {
			buffer.put(vertice.getPos().getX());
			buffer.put(vertice.getPos().getY());
			buffer.put(vertice.getPos().getZ());
			buffer.put(vertice.getNormal().getX());
			buffer.put(vertice.getNormal().getY());
			buffer.put(vertice.getNormal().getZ());
			buffer.put(vertice.getColor().getX());
			buffer.put(vertice.getColor().getY());
			buffer.put(vertice.getColor().getZ());
		}
		buffer.flip();
		return buffer;
	}

	public static IntBuffer createDcFlippedBufferAOS(Vec3i[] vertices) {
		IntBuffer buffer = createIntBuffer(vertices.length * 3);
		for (Vec3i vertice : vertices) {
			buffer.put(vertice.x);
			buffer.put(vertice.y);
			buffer.put(vertice.z);
		}
		buffer.flip();
		return buffer;
	}

	public static FloatBuffer createDcFlippedBufferAOS(List<MeshVertex> vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.size() * 9);
		for (Vertex vertice : vertices) {
			buffer.put(vertice.getPos().getX());
			buffer.put(vertice.getPos().getY());
			buffer.put(vertice.getPos().getZ());
			buffer.put(vertice.getNormal().getX());
			buffer.put(vertice.getNormal().getY());
			buffer.put(vertice.getNormal().getZ());
			buffer.put(vertice.getColor().getX());
			buffer.put(vertice.getColor().getY());
			buffer.put(vertice.getColor().getZ());
		}
		buffer.flip();
		return buffer;
	}

	public static FloatBuffer createDcFlippedBufferAOS(MeshVertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * 9);
		for (Vertex vertice : vertices) {
			buffer.put(vertice.getPos().getX());
			buffer.put(vertice.getPos().getY());
			buffer.put(vertice.getPos().getZ());
			buffer.put(vertice.getNormal().getX());
			buffer.put(vertice.getNormal().getY());
			buffer.put(vertice.getNormal().getZ());
			buffer.put(vertice.getColor().getX());
			buffer.put(vertice.getColor().getY());
			buffer.put(vertice.getColor().getZ());
		}
		buffer.flip();
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Matrix4f matrix)
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4);
		
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				buffer.put(matrix.get(i, j));
		
		buffer.flip();
		
		return buffer;
	}
}
