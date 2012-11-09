package psywerx.platformGl.game;

import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class Game {
    LinkedList<Obstacle> objects = new LinkedList<Obstacle>();
    protected float smoothDirection;
    protected Player player;
    protected Background bg;
    protected boolean dead = false;

    private double score = 0;

    private double lastCreated = 0;

    protected Game() {
        player = new Player();
        bg = new Background();
    }

    protected void tick(double theta) {
        score += theta;
        lastCreated += theta;
        if (lastCreated > 1 && !Main.game.dead) {
            lastCreated = 0;
            Obstacle o = new Obstacle();
            o.obstacle.position.x = (float) Math.random() * 2 - 1;
            o.obstacle.velocity.y = (float) Math.random();
            o.obstacle.position.y = -1;
            objects.add(o);
        }

        if (dead) return;
        player.update(theta);
        for (Obstacle o : objects) {
            o.update(theta);
        }
    }

    protected void draw(GL2ES2 gl) {
        // Clear screen
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0, 0f, 0, 1f);
        gl.glClear(GL2ES2.GL_STENCIL_BUFFER_BIT | GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(Main.shaderProgram);

        float[] model_view_projection = new float[16]; // Gets sent to the
        float ratio = Main.WIDTH / (float) Main.HEIGHT;
        float[] model_projection = new float[16];
        Matrix.setLookAtM(model_projection, 0, 0f, 0f, -4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.frustumM(model_view_projection, 0, -ratio, ratio, -1, 1, 3, 7);

        smoothDirection = (player.direction.x * 25f) * 0.2f + smoothDirection * 0.8f;
        Matrix.rotateM(model_projection, 0, -smoothDirection / 1.5f, 0, 1f, 0f);

        float[] projection = new float[16];
        Matrix.multiplyMM(projection, 0, model_view_projection, 0, model_projection, 0);

        gl.glUniformMatrix4fv(Main.projectionMatrix_location, 1, false, projection, 0);

        // Draw actual stuff:
        bg.draw(gl);
        player.draw(gl);
        for (Obstacle g : objects) {
            g.draw(gl);
        }
    }

    public void reset() {
        bg.bgSquare.color = new float[] { 0.2f, 0.2f, 0.2f };
        dead = false;
        Main.game.objects = new LinkedList<Obstacle>();
        score = 0;
    }

    public void die() {
        dead = true;
        bg.bgSquare.color = new float[] { 0.6f, 0.2f, 0.2f };
        System.out.println("You are score " + score);

    }
}
