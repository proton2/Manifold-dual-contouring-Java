/*
    Java version of Manifold Dual contouring author John Lin20 https://github.com/Lin20/isosurface
 */

package manifoldDC;

import core.math.Vec3i;
import simpleDC.OctreeNodeType;

public class MdcOctreeNode{
    public OctreeNodeType Type;
    public Vec3i min;
    public int size;
    public int corners;
    public int index = 0;
    public MdcVertex[] vertices;
    public int child_index;
    public MdcOctreeNode[] children;

    public MdcOctreeNode(Vec3i position, int size, OctreeNodeType type, int childIndex) {
        this.min = position;
        this.size = size;
        this.Type = type;
        this.children = new MdcOctreeNode[8];
        this.vertices = new MdcVertex[0];
        this.child_index = childIndex;
    }
}
