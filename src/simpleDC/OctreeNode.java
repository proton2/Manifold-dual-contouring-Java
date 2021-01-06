package simpleDC;

import core.math.Vec3i;

public class OctreeNode {
    public OctreeNodeType Type;
    public Vec3i min;
    public int size;
    public OctreeNode[] children;
    public OctreeDrawInfo drawInfo;

    public OctreeNode(Vec3i position, int size, OctreeNodeType type) {
        this.Type = type;
        this.min = position;
        this.size = size;
        this.drawInfo = new OctreeDrawInfo();
        this.children = new OctreeNode[8];
    }
}
