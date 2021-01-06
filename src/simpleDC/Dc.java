package simpleDC;

import java.util.List;

import static simpleDC.SimpleDualContouring.MATERIAL_AIR;
import static simpleDC.SimpleDualContouring.edgevmap;
import static simpleDC.OctreeNodeType.*;

public class Dc {
    private static int[][] cellProcFaceMask = {{0,4,0},{1,5,0},{2,6,0},{3,7,0},{0,2,1},{4,6,1},{1,3,1},{5,7,1},{0,1,2},{2,3,2},{4,5,2},{6,7,2}} ;
    private static int[][] cellProcEdgeMask = {{0,1,2,3,0},{4,5,6,7,0},{0,4,1,5,1},{2,6,3,7,1},{0,2,4,6,2},{1,3,5,7,2}} ;

    private static int[][][] faceProcFaceMask = {
            {{4,0,0},{5,1,0},{6,2,0},{7,3,0}},
            {{2,0,1},{6,4,1},{3,1,1},{7,5,1}},
            {{1,0,2},{3,2,2},{5,4,2},{7,6,2}}
    } ;

    private static int[][][] faceProcEdgeMask = {
            {{1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2}},
            {{0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2}},
            {{1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}}
    };

    private static int[][][] edgeProcEdgeMask = {
            {{3,2,1,0,0},{7,6,5,4,0}},
            {{5,1,4,0,1},{7,3,6,2,1}},
            {{6,4,2,0,2},{7,5,3,1,2}},
    };

    private static int[][] processEdgeMask = {{3,2,1,0},{7,5,6,4},{11,10,9,8}} ;

    private static void ContourProcessEdge(OctreeNode[] node, int dir, List<Integer> indexBuffer)
    {
        int minSize = 1000000;		// arbitrary big number
        int minIndex = 0;
        int[] indices = { -1, -1, -1, -1 };
        boolean flip = false;
        boolean[] signChange = { false, false, false, false };

        for (int i = 0; i < 4; i++)
        {
            int edge = processEdgeMask[dir][i];
            int c1 = edgevmap[edge][0];
            int c2 = edgevmap[edge][1];

            int m1 = (node[i].drawInfo.corners >> c1) & 1;
            int m2 = (node[i].drawInfo.corners >> c2) & 1;

            if (node[i].size < minSize)
            {
                minSize = node[i].size;
                minIndex = i;
                flip = m1 != MATERIAL_AIR;
            }

            indices[i] = node[i].drawInfo.index;

            signChange[i] =
                    (m1 == MATERIAL_AIR && m2 != MATERIAL_AIR) ||
                            (m1 != MATERIAL_AIR && m2 == MATERIAL_AIR);
        }

        if (signChange[minIndex])
        {
            if (!flip)
            {
                indexBuffer.add(indices[0]);
                indexBuffer.add(indices[1]);
                indexBuffer.add(indices[3]);

                indexBuffer.add(indices[0]);
                indexBuffer.add(indices[3]);
                indexBuffer.add(indices[2]);
            }
            else
            {
                indexBuffer.add(indices[0]);
                indexBuffer.add(indices[3]);
                indexBuffer.add(indices[1]);

                indexBuffer.add(indices[0]);
                indexBuffer.add(indices[2]);
                indexBuffer.add(indices[3]);
            }
        }
    }

    private static void ContourEdgeProc(OctreeNode[] node, int dir, List<Integer> indexBuffer)
    {
        if (node[0] == null || node[1] == null || node[2] == null || node[3] == null) {
            return;
        }

        if (node[0].Type != Node_Internal &&
                node[1].Type != Node_Internal &&
                node[2].Type != Node_Internal &&
                node[3].Type != Node_Internal)
        {
            ContourProcessEdge(node, dir, indexBuffer);
        }
        else
        {
            for (int i = 0; i < 2; i++)
            {
                OctreeNode[] edgeNodes = new OctreeNode[4];
                int[] c = {
                        edgeProcEdgeMask[dir][i][0],
                        edgeProcEdgeMask[dir][i][1],
                        edgeProcEdgeMask[dir][i][2],
                        edgeProcEdgeMask[dir][i][3],
                };

                for (int j = 0; j < 4; j++)
                {
                    if (node[j].Type == Node_Leaf || node[j].Type == Node_Psuedo)
                    {
                        edgeNodes[j] = node[j];
                    }
                    else
                    {
                        edgeNodes[j] = node[j].children[c[j]];
                    }
                }

                ContourEdgeProc(edgeNodes, edgeProcEdgeMask[dir][i][4], indexBuffer);
            }
        }
    }

    private static void ContourFaceProc(OctreeNode[] node, int dir, List<Integer> indexBuffer) {
        if (node[0] == null || node[1] == null) {
            return;
        }

        if (node[0].Type == Node_Internal || node[1].Type == Node_Internal)
        {
            for (int i = 0; i < 4; i++)
            {
                OctreeNode[] faceNodes = new OctreeNode[2];
                int[] c = {
                        faceProcFaceMask[dir][i][0], faceProcFaceMask[dir][i][1],
                };

                for (int j = 0; j < 2; j++)
                {
                    if (node[j].Type != Node_Internal)
                    {
                        faceNodes[j] = node[j];
                    }
                    else
                    {
                        faceNodes[j] = node[j].children[c[j]];
                    }
                }

                ContourFaceProc(faceNodes, faceProcFaceMask[dir][i][2], indexBuffer);
            }

            int[][] orders = {
                    { 0, 0, 1, 1 },
                    { 0, 1, 0, 1 },
            };

            for (int i = 0; i < 4; i++)
            {
                OctreeNode[] edgeNodes = new OctreeNode[4];
                int[] c = {
                        faceProcEdgeMask[dir][i][1],
                        faceProcEdgeMask[dir][i][2],
                        faceProcEdgeMask[dir][i][3],
                        faceProcEdgeMask[dir][i][4],
                };

                int[] order = orders[faceProcEdgeMask[dir][i][0]];
                for (int j = 0; j < 4; j++)
                {
                    if (node[order[j]].Type == Node_Leaf || node[order[j]].Type == Node_Psuedo)
                    {
                        edgeNodes[j] = node[order[j]];
                    }
                    else
                    {
                        edgeNodes[j] = node[order[j]].children[c[j]];
                    }
                }

                ContourEdgeProc(edgeNodes, faceProcEdgeMask[dir][i][5], indexBuffer);
            }
        }
    }

    public static void ContourCellProc(OctreeNode node, List<Integer> indexBuffer) {
        if (node == null) {
            return;
        }

        if (node.Type == Node_Internal)
        {
            for (int i = 0; i < 8; i++)
            {
                ContourCellProc(node.children[i], indexBuffer);
            }

            for (int i = 0; i < 12; i++)
            {
                OctreeNode[] faceNodes = new OctreeNode[2];;
                int[] c = { cellProcFaceMask[i][0], cellProcFaceMask[i][1] };

                faceNodes[0] = node.children[c[0]];
                faceNodes[1] = node.children[c[1]];

                ContourFaceProc(faceNodes, cellProcFaceMask[i][2], indexBuffer);
            }

            for (int i = 0; i < 6; i++)
            {
                OctreeNode[] edgeNodes = new OctreeNode[4];
                int[] c = {
                        cellProcEdgeMask[i][0],
                        cellProcEdgeMask[i][1],
                        cellProcEdgeMask[i][2],
                        cellProcEdgeMask[i][3],
                };

                for (int j = 0; j < 4; j++)
                {
                    edgeNodes[j] = node.children[c[j]];
                }

                ContourEdgeProc(edgeNodes, cellProcEdgeMask[i][4], indexBuffer);
            }
        }
    }
}
