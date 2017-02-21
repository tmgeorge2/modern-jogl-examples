/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.tut01;

import buffer.BufferUtils;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import glsl.ShaderCodeKt;
import main.framework.Framework;
import main.framework.Semantic;
import vec._4.Vec4;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

/**
 * @author gbarbieri
 */
public class HelloTriangle extends Framework {

    private final String VERTEX_SHADER = "tut01/shader.vert";
    private final String FRAGMENT_SHADER = "tut01/shader.frag";

    public static void main(String[] args) {
        new HelloTriangle("Tutorial 01 - Hello Triangle");
    }

    private HelloTriangle(String title) {
        super(title);
    }

    private int theProgram;
    private IntBuffer positionBufferObject = GLBuffers.newDirectIntBuffer(1), vao = GLBuffers.newDirectIntBuffer(1);
    private float[] vertexPositions = {
            +0.75f, +0.75f, 0.0f, 1.0f,
            +0.75f, -0.75f, 0.0f, 1.0f,
            -0.75f, -0.75f, 0.0f, 1.0f};

    /**
     * Called after the window and OpenGL are initialized. Called exactly once,
     * before the main loop.
     *
     * @param gl
     */
    @Override
    public void init(GL3 gl) {

        initializeProgram(gl);

        initializeVertexBuffer(gl);

        gl.glGenVertexArrays(1, vao);
        gl.glBindVertexArray(vao.get(0));
    }

    private void initializeProgram(GL3 gl) {

        ShaderProgram shaderProgram = new ShaderProgram();

        ShaderCode vertex = ShaderCodeKt.shaderCodeOf(VERTEX_SHADER, gl, getClass());
        ShaderCode fragment = ShaderCodeKt.shaderCodeOf(FRAGMENT_SHADER, gl, getClass());

        shaderProgram.add(vertex);
        shaderProgram.add(fragment);

        shaderProgram.link(gl, System.err);

        vertex.destroy(gl);
        fragment.destroy(gl);

        theProgram = shaderProgram.program();
    }

    private void initializeVertexBuffer(GL3 gl) {

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertexPositions);

        gl.glGenBuffers(1, positionBufferObject);

        gl.glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject.get(0));
        gl.glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        BufferUtils.destroyDirectBuffer(vertexBuffer);
    }

    /**
     * Called to update the display. You don't need to swap the buffers after
     * all of your rendering to display what you rendered, it is done
     * automatically.
     *
     * @param gl
     */
    @Override
    public void display(GL3 gl) {

        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, 0f).put(1, 0f).put(2, 0f).put(3, 1f));

        gl.glUseProgram(theProgram);

        gl.glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject.get(0));
        gl.glEnableVertexAttribArray(Semantic.Attr.POSITION);
        gl.glVertexAttribPointer(Semantic.Attr.POSITION, 4, GL_FLOAT, false, Vec4.SIZE, 0);

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);

        gl.glDisableVertexAttribArray(Semantic.Attr.POSITION);
        gl.glUseProgram(0);
    }

    /**
     * Called whenever the window is resized. The new window size is given, in
     * pixels. This is an opportunity to call glViewport or glScissor to keep up
     * with the change in size.
     *
     * @param gl
     * @param w
     * @param h
     */
    @Override
    public void reshape(GL3 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
    }

    /**
     * Called at the end, here you want to clean all the resources.
     *
     * @param gl
     */
    @Override
    protected void end(GL3 gl) {

        gl.glDeleteProgram(theProgram);
        gl.glDeleteBuffers(1, positionBufferObject);
        gl.glDeleteVertexArrays(1, vao);

        BufferUtils.destroyDirectBuffer(positionBufferObject);
        BufferUtils.destroyDirectBuffer(vao);
    }

    /**
     * Called whenever a key on the keyboard was pressed. The key is given by
     * the KeyCode(). It's often a good idea to have the escape key to exit the
     * program.
     *
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {

        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                animator.remove(window);
                window.destroy();
                break;
        }
    }
}