package simpleDC;

import core.buffers.MeshBuffer;
import core.buffers.MeshDcVBO;
import core.configs.CW;
import core.kernel.Camera;
import core.kernel.Input;
import core.math.Vec3f;
import core.math.Vec3i;
import core.model.Vertex;
import core.renderer.RenderInfo;
import core.renderer.Renderer;
import core.scene.GameObject;
import core.utils.BufferUtil;
import core.utils.Constants;
import shaders.DcSimpleShader;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

/**
 * Created by proton2 on 28.12.2019.
 */
public class DcWrapper extends GameObject {
    private OctreeNode root;
    private SimpleDualContouring simpleDc;
    private int thresholdIndex = 0;
    public float[] THRESHOLDS = { 0.0f, 0.001f, 0.01f, 0.05f, 0.1f, 0.2f, 0.4f, 0.5f, 0.8f, 1.0f, 1.5f, 2.0f, 5.0f, 10.0f, 25.0f, 50.0f, 100.0f, 250.0f, 500.0f, 1000.0f, 2500.0f, 5000.0f, 10000.0f, 25000.0f, 50000.0f, 100000.0f };
    private boolean refreshMesh = true;
    private boolean drawWireframe = false;
    private List<MeshVertex> vertices;
    private List<Integer> indcies;

    // octreeSize must be a power of two!
    private int octreeSize = 64;

    public DcWrapper() {
        vertices = new ArrayList<>();
        indcies = new ArrayList<>();
        simpleDc = new SimpleDualContouring();

        Camera.getInstance().setPosition(new Vec3f(0,15,0));
        Camera.getInstance().setForward(new Vec3f(1,0,0).normalize());
        Camera.getInstance().setUp(new Vec3f(0,1,0));
    }

    public void update() {
        if (refreshMesh) {
            refreshMesh = false;
            renderMesh();
        }

        if(Input.getInstance().isKeyHold(GLFW_KEY_F1)){
            sleep(200);
            drawWireframe = !drawWireframe;
        }
        if(Input.getInstance().isKeyHold(GLFW_KEY_F2)){
            sleep(200);
            refreshMesh = true;
            thresholdIndex = (thresholdIndex + 1) % THRESHOLDS.length;
        }
        if(Input.getInstance().isKeyHold(GLFW_KEY_F3)){
            sleep(200);
            refreshMesh = true;
        }

        glPolygonMode(GL_FRONT_AND_BACK, drawWireframe ? GL_LINE : GL_FILL);
    }

    private void renderMesh(){
        root = simpleDc.BuildOctree(new Vec3i(0), octreeSize, THRESHOLDS[thresholdIndex]);
        SimpleDualContouring.GenerateMeshFromOctree(root, vertices, indcies);

        MeshBuffer buffer = new MeshBuffer();
        buffer.setVertices(BufferUtil.createDcFlippedBufferAOS(vertices));
        buffer.setIndicates(BufferUtil.createFlippedBuffer(indcies));
        buffer.setNumVertices(vertices.size());
        buffer.setNumIndicates(indcies.size());

        MeshDcVBO meshBuffer = new MeshDcVBO(buffer);
        Renderer renderer = new Renderer(meshBuffer);
        renderer.setRenderInfo(new RenderInfo(new CW(), DcSimpleShader.getInstance()));
        addComponent(Constants.RENDERER_COMPONENT, renderer);
    }

    public void shutDown() {
        SimpleDualContouring.DestroyOctree(root);
    }
}