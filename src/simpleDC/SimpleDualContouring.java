package simpleDC;

import core.math.Vec3f;
import core.math.Vec3i;
import core.math.Vec4f;
import core.utils.Constants;
import solver.LevenQefSolver;
import solver.QEFData;
import utils.SimplexNoise;

import java.util.List;

import static simpleDC.OctreeNodeType.*;

public class SimpleDualContouring {
    public static final int MATERIAL_AIR = 0;
    public static final int MATERIAL_SOLID = 1;

    private static final Vec3i[] CHILD_MIN_OFFSETS = {
            // needs to match the vertMap from Dual Contouring impl
            new Vec3i( 0, 0, 0 ),
            new Vec3i( 0, 0, 1 ),
            new Vec3i( 0, 1, 0 ),
            new Vec3i( 0, 1, 1 ),
            new Vec3i( 1, 0, 0 ),
            new Vec3i( 1, 0, 1 ),
            new Vec3i( 1, 1, 0 ),
            new Vec3i( 1, 1, 1 ),
    };

    public static final int[][] edgevmap = {
        {0,4},{1,5},{2,6},{3,7},	// x-axis
        {0,2},{1,3},{4,6},{5,7},	// y-axis
        {0,1},{2,3},{4,5},{6,7}		// z-axis
    };

    public SimpleDualContouring() {
    }

    // -------------------------------------------------------------------------------
    private OctreeNode ConstructLeaf(OctreeNode leaf) {
        if (leaf == null || leaf.size != 1) {
            return null;
        }

        int corners = 0;
        for (int i = 0; i < 8; i++) {
            Vec3f cornerPos = leaf.min.add(CHILD_MIN_OFFSETS[i]).toVec3f();
            float density = SimplexNoise.Sample(cornerPos);
		    int material = density < 0.f ? MATERIAL_SOLID : MATERIAL_AIR;
            corners |= (material << i);
        }

        if (corners == 0 || corners == 255) {
            return null;    // voxel is full inside or outside the volume
        }

        // otherwise the voxel contains the surface, so find the edge intersections
	    int MAX_CROSSINGS = 6;
        int edgeCount = 0;
        Vec3f averageNormal = new Vec3f(0.f);
        QEFData qef = new QEFData(new LevenQefSolver());

        for (int i = 0; i < 12 && edgeCount < MAX_CROSSINGS; i++) {
		    int c1 = edgevmap[i][0];
		    int c2 = edgevmap[i][1];

		    int m1 = (corners >> c1) & 1;
		    int m2 = (corners >> c2) & 1;

            if ((m1 == MATERIAL_AIR && m2 == MATERIAL_AIR) || (m1 == MATERIAL_SOLID && m2 == MATERIAL_SOLID)) {
                continue; // no zero crossing on this edge
            }

            Vec3f p1 = leaf.min.add(CHILD_MIN_OFFSETS[c1]).toVec3f();
            Vec3f p2 = leaf.min.add(CHILD_MIN_OFFSETS[c2]).toVec3f();
            Vec3f p = ApproximateZeroCrossingPosition(p1, p2);
            Vec3f n = CalculateSurfaceNormal(p);
            qef.qef_add_point(p, n);
            averageNormal = averageNormal.add(n);
            edgeCount++;
        }

        OctreeDrawInfo drawInfo = new OctreeDrawInfo();
        drawInfo.position = qef.solve();
        drawInfo.qef = qef;
        drawInfo.averageNormal = averageNormal.div((float)edgeCount);//.normalize();
        drawInfo.averageNormal.normalize();
        drawInfo.corners = corners;

        leaf.Type = Node_Leaf;
        leaf.drawInfo = drawInfo;
        return leaf;
    }

    private static OctreeNode SimplifyOctree(OctreeNode node, float threshold) {
        if (node == null) {
            return null;
        }
        if (node.Type != Node_Internal) {    // can't simplify!
            return node;
        }

        QEFData qef = new QEFData(new LevenQefSolver());
        int[] signs = { -1, -1, -1, -1, -1, -1, -1, -1 };
        int midsign = -1;
        boolean isCollapsible = true;

        for (int i = 0; i < 8; i++) {
            node.children[i] = SimplifyOctree(node.children[i], threshold);
            if (node.children[i] != null) {
                OctreeNode child = node.children[i];
                if (child.Type == Node_Internal) {
                    isCollapsible = false;
                }
                else {
                    qef.add(child.drawInfo.qef);
                    midsign = (child.drawInfo.corners >> (7 - i)) & 1;
                    signs[i] = (child.drawInfo.corners >> i) & 1;
                }
            }
        }

        if (!isCollapsible) {
            return node;    // at least one child is an internal node, can't collapse
        }

        Vec4f position = qef.solve();
        float error = qef.getError();

        // at this point the masspoint will actually be a sum, so divide to make it the average
        if (error > threshold) {
            return node;    // this collapse breaches the threshold
        }

        // change the node from an internal node to a 'psuedo leaf' node
        OctreeDrawInfo drawInfo = new OctreeDrawInfo();

        for (int i = 0; i < 8; i++) {
            if (signs[i] == -1) {
                drawInfo.corners |= (midsign << i); // Undetermined, use centre sign instead
            }
            else {
                drawInfo.corners |= (signs[i] << i);
            }
        }

        drawInfo.averageNormal = new Vec3f(0);
        for (int i = 0; i < 8; i++) {
            if (node.children[i] != null) {
                OctreeNode child = node.children[i];
                if (child.Type == Node_Psuedo || child.Type == Node_Leaf) {
                    drawInfo.averageNormal = drawInfo.averageNormal.add(child.drawInfo.averageNormal);
                }
            }
        }

        drawInfo.averageNormal.normalize();
        drawInfo.position = position;
        drawInfo.qef = qef;

        for (int i = 0; i < 8; i++) {
            DestroyOctree(node.children[i]);
            node.children[i] = null;
        }

        node.Type = Node_Psuedo;
        node.drawInfo = drawInfo;
        return node;
    }

    private static Vec3f ApproximateZeroCrossingPosition(Vec3f p0, Vec3f p1) {
        // approximate the zero crossing by finding the min value along the edge
        float minValue = 100000.f;
        float t = 0.f;
        float currentT = 0.f;
        int steps = 8;
        float increment = 1.f / (float)steps;
        while (currentT <= 1.f)
        {
            Vec3f p = p0.add(p1.sub(p0).mul(currentT)); // p = p0 + ((p1 - p0) * currentT);
            float density = Math.abs(SimplexNoise.Sample(p));
            if (density < minValue) {
                minValue = density;
                t = currentT;
            }
            currentT += increment;
        }
        return p0.add((p1.sub(p0)).mul(t)); // p0 + ((p1 - p0) * t);
    }

    public Vec3f CalculateSurfaceNormal(Vec3f p) {
        float H = 1f;
        Vec3f xOffcet = new Vec3f(H, 0.f, 0.f);
        Vec3f yOffcet = new Vec3f(0.f, H, 0.f);
        Vec3f zOffcet = new Vec3f(0.f, 0.f, H);
        float dx = SimplexNoise.Sample(p.add(xOffcet)) - SimplexNoise.Sample(p.sub(xOffcet));
        float dy = SimplexNoise.Sample(p.add(yOffcet)) - SimplexNoise.Sample(p.sub(yOffcet));
        float dz = SimplexNoise.Sample(p.add(zOffcet)) - SimplexNoise.Sample(p.sub(zOffcet));

        Vec3f v = new Vec3f(dx, dy, dz);
        v.normalize();
        return v;
    }

    private static void GenerateVertexIndices(OctreeNode node, List<MeshVertex> vertexBuffer) {
        if (node == null) {
            return;
        }

        if (node.Type != Node_Leaf) {
            for (int i = 0; i < 8; i++) {
                GenerateVertexIndices(node.children[i], vertexBuffer);
            }
        }

        if (node.Type != Node_Internal) {
            node.drawInfo.index = vertexBuffer.size();
            vertexBuffer.add(new MeshVertex(node.drawInfo.position.getVec3f(), node.drawInfo.averageNormal, Constants.Red));
        }
    }

    private OctreeNode ConstructOctreeNodes(OctreeNode node) {
        if (node == null) {
            return null;
        }
        if (node.size == 1) {
            return ConstructLeaf(node);
        }

	    int childSize = node.size / 2;
        boolean hasChildren = false;

        for (int i = 0; i < 8; i++) {
            Vec3i childMin = node.min.add(CHILD_MIN_OFFSETS[i].mul(childSize));
            OctreeNode child = new OctreeNode(childMin, childSize, OctreeNodeType.Node_Internal);
            node.children[i] = ConstructOctreeNodes(child);
            hasChildren |= (node.children[i] != null);
        }

        if (!hasChildren) {
            return null;
        }

        return node;
    }

    public OctreeNode BuildOctree(Vec3i min, int size, float threshold) {
        OctreeNode root = new OctreeNode(min, size, OctreeNodeType.Node_Internal);
        root = ConstructOctreeNodes(root);
        root = SimplifyOctree(root, threshold);
        return root;
    }

    public static void GenerateMeshFromOctree(OctreeNode node, List<MeshVertex> vertexBuffer, List<Integer> indexBuffer) {
        if (node == null) {
            return;
        }

        vertexBuffer.clear();
        indexBuffer.clear();

        GenerateVertexIndices(node, vertexBuffer);
        Dc.ContourCellProc(node, indexBuffer);
    }

    public static void DestroyOctree(OctreeNode node) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            DestroyOctree(node.children[i]);
        }

        if (node.drawInfo != null) {
            node.drawInfo = null;
        }
    }
}