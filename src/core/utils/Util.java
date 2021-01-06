package core.utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import core.math.Quaternion;
import core.math.Vec2f;
import core.math.Vec3f;
import core.model.Mesh;
import core.model.Vertex;
import core.utils.objloader.Face;
import core.utils.objloader.SmoothingGroup;

public class Util {

	public static String [] removeEmptyStrings(String[] data)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for (int i = 0; i < data.length; i++)
			if(!data[i].equals(""))
				result.add(data[i]);
		
		String[] res = new String[result.size()];
		result.toArray(res);
		
		return res;
	}
	
	public static int[] toIntArray(Integer[] data)
	{
		int[] result = new int[data.length];
		
		for(int i=0; i < data.length; i++)
			result[i] = data[i].intValue();
		
		return result;
	}
	
	public static Vertex[] toVertexArray(FloatBuffer data)
	{
		Vertex[] vertices = new Vertex[data.limit() / Vertex.FLOATS];
		
		for(int i=0; i<vertices.length; i++)
		{
			vertices[i] = new Vertex();
			vertices[i].setPos(new Vec3f(data.get(),data.get(),data.get()));
			vertices[i].setTextureCoord(new Vec2f(data.get(),data.get()));
			vertices[i].setNormal(new Vec3f(data.get(),data.get(),data.get()));
		}
		
		return vertices;
	}
	
	public static Vertex[] toVertexArray(ArrayList<Vertex> data)
	{
		Vertex[] vertices = new Vertex[data.size()];
		
		for(int i=0; i<vertices.length; i++)
		{
			vertices[i] = new Vertex();
			vertices[i].setPos(data.get(i).getPos());
			vertices[i].setTextureCoord(data.get(i).getTextureCoord());
			vertices[i].setNormal(data.get(i).getNormal());
		}
		
		return vertices;
	}
	
	public static void generateNormalsCW(Vertex[] vertices, int[] indices)
	{
	    for ( int i = 0; i < indices.length; i += 3 )
	    {
	    	Vec3f v0 = vertices[indices[i    ]].getPos();
	    	Vec3f v1 = vertices[indices[i + 1]].getPos();
	    	Vec3f v2 = vertices[indices[i + 2]].getPos();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	        vertices[indices[i	  ]].setNormal(vertices[indices[i    ]].getNormal().add(normal));
	        vertices[indices[i + 1]].setNormal(vertices[indices[i + 1]].getNormal().add(normal));
	        vertices[indices[i + 2]].setNormal(vertices[indices[i + 2]].getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.length; ++i )
	    {	
	    	vertices[i].setNormal(vertices[i].getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(Vertex[] vertices, int[] indices)
	{
	    for ( int i = 0; i < indices.length; i += 3 )
	    {
	    	Vec3f v0 = vertices[indices[i    ]].getPos();
	    	Vec3f v1 = vertices[indices[i + 1]].getPos();
	    	Vec3f v2 = vertices[indices[i + 2]].getPos();
	        
	    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
	        
	        vertices[indices[i	  ]].setNormal(vertices[indices[i    ]].getNormal().add(normal));
	        vertices[indices[i + 1]].setNormal(vertices[indices[i + 1]].getNormal().add(normal));
	        vertices[indices[i + 2]].setNormal(vertices[indices[i + 2]].getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.length; ++i )
	    {	
	    	vertices[i].setNormal(vertices[i].getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCW(ArrayList<Vertex> vertices, ArrayList<Integer> indices)
	{
	    for ( int i = 0; i < indices.size(); i += 3 )
	    {
	    	Vec3f v0 = vertices.get(indices.get(i)).getPos();
	    	Vec3f v1 = vertices.get(indices.get(i+1)).getPos();
	    	Vec3f v2 = vertices.get(indices.get(i+2)).getPos();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	        vertices.get(indices.get(i)).setNormal(vertices.get(indices.get(i)).getNormal().add(normal));
	        vertices.get(indices.get(i+1)).setNormal(vertices.get(indices.get(i+1)).getNormal().add(normal));
	        vertices.get(indices.get(i+2)).setNormal(vertices.get(indices.get(i+2)).getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.size(); ++i )
	    {	
	    	vertices.get(i).setNormal(vertices.get(i).getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(ArrayList<Vertex> vertices, ArrayList<Integer> indices)
	{
	    for ( int i = 0; i < indices.size(); i += 3 )
	    {
	    	Vec3f v0 = vertices.get(indices.get(i)).getPos();
	    	Vec3f v1 = vertices.get(indices.get(i+1)).getPos();
	    	Vec3f v2 = vertices.get(indices.get(i+2)).getPos();
	        
	    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
	        
	        vertices.get(indices.get(i)).setNormal(vertices.get(indices.get(i)).getNormal().add(normal));
	        vertices.get(indices.get(i+1)).setNormal(vertices.get(indices.get(i+1)).getNormal().add(normal));
	        vertices.get(indices.get(i+2)).setNormal(vertices.get(indices.get(i+2)).getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.size(); ++i )
	    {	
	    	vertices.get(i).setNormal(vertices.get(i).getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCW(SmoothingGroup smoothingGroup)
	{
	    for (Face face : smoothingGroup.getFaces())
	    {
	    	Vec3f v0 = smoothingGroup.getVertices().get(face.getIndices()[0]).getPos();
	    	Vec3f v1 = smoothingGroup.getVertices().get(face.getIndices()[1]).getPos();
	    	Vec3f v2 = smoothingGroup.getVertices().get(face.getIndices()[2]).getPos();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	    	smoothingGroup.getVertices().get(face.getIndices()[0]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[0]).getNormal().add(normal));
	    	smoothingGroup.getVertices().get(face.getIndices()[1]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[1]).getNormal().add(normal));
	    	smoothingGroup.getVertices().get(face.getIndices()[2]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[2]).getNormal().add(normal));
	    }

	    for (Vertex vertex : smoothingGroup.getVertices())
	    {	
	    	vertex.setNormal(vertex.getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(SmoothingGroup smoothingGroup)
	{
		  for (Face face : smoothingGroup.getFaces())
		    {
		    	Vec3f v0 = smoothingGroup.getVertices().get(face.getIndices()[0]).getPos();
		    	Vec3f v1 = smoothingGroup.getVertices().get(face.getIndices()[1]).getPos();
		    	Vec3f v2 = smoothingGroup.getVertices().get(face.getIndices()[2]).getPos();
		        
		    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
		        
		    	smoothingGroup.getVertices().get(face.getIndices()[0]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[0]).getNormal().add(normal));
		    	smoothingGroup.getVertices().get(face.getIndices()[1]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[1]).getNormal().add(normal));
		    	smoothingGroup.getVertices().get(face.getIndices()[2]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[2]).getNormal().add(normal));
		    }

		    for (Vertex vertex : smoothingGroup.getVertices())
		    {	
		    	vertex.setNormal(vertex.getNormal().normalize());
		    }     
	}
	
	
	public static void generateTangentsBitangents(Mesh mesh)
	{
		for ( int i = 0; i < mesh.getIndices().length; i += 3 )
		{
		    	Vec3f v0 = mesh.getVertices()[mesh.getIndices()[i]].getPos();
		    	Vec3f v1 = mesh.getVertices()[mesh.getIndices()[i+1]].getPos();
		    	Vec3f v2 = mesh.getVertices()[mesh.getIndices()[i+2]].getPos();
		        
		    	Vec2f uv0 = mesh.getVertices()[mesh.getIndices()[i]].getTextureCoord();
		    	Vec2f uv1 = mesh.getVertices()[mesh.getIndices()[i+1]].getTextureCoord();
		    	Vec2f uv2 = mesh.getVertices()[mesh.getIndices()[i+2]].getTextureCoord();
		    	
		    	Vec3f e1 = v1.sub(v0);
		    	Vec3f e2 = v2.sub(v0);
		    	
		    	Vec2f deltaUV1 = uv1.sub(uv0);
		    	Vec2f deltaUV2 = uv2.sub(uv0);
		    	
		    	float r = (1.0f / (deltaUV1.getX() * deltaUV2.getY() - deltaUV1.getY() * deltaUV2.getX()));
		    	
		    	Vec3f tangent = new Vec3f();
		    	tangent.setX(r * deltaUV2.getY() * e1.getX() - deltaUV1.getY() * e2.getX());
		    	tangent.setY(r * deltaUV2.getY() * e1.getY() - deltaUV1.getY() * e2.getY());
		    	tangent.setZ(r * deltaUV2.getY() * e1.getZ() - deltaUV1.getY() * e2.getZ());
		    	Vec3f bitangent = new Vec3f();
		    	Vec3f normal = mesh.getVertices()[mesh.getIndices()[i]].getNormal().add(
		    				   mesh.getVertices()[mesh.getIndices()[i+1]].getNormal()).add(
		    				   mesh.getVertices()[mesh.getIndices()[i+2]].getNormal());
		    	normal = normal.normalize();
		    	
		    	bitangent = tangent.cross(normal);
		    	
		    	tangent = tangent.normalize();
		    	bitangent = bitangent.normalize();
		 }
	}
	
	public static Quaternion normalizePlane(Quaternion plane)
	{
		float mag;
		mag = (float) Math.sqrt(plane.getX() * plane.getX() + plane.getY() * plane.getY() + plane.getZ() * plane.getZ());
		plane.setX(plane.getX()/mag);
		plane.setY(plane.getY()/mag);
		plane.setZ(plane.getZ()/mag);
		plane.setW(plane.getW()/mag);
	
		return plane;
	}
	
	public static Vec2f[] texCoordsFromFontMap(char x)
	{
		float x_ = (x%16)/16.0f;
		float y_ = (x/16)/16.0f;
		Vec2f[] texCoords = new Vec2f[4];
		texCoords[0] = new Vec2f(x_, y_ + 1.0f/16.0f);
		texCoords[1] = new Vec2f(x_, y_);
		texCoords[2] = new Vec2f(x_ + 1.0f/16.0f, y_ + 1.0f/16.0f);
		texCoords[3] = new Vec2f(x_ + 1.0f/16.0f, y_);
		
		return texCoords;
	}
}
