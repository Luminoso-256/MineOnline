package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.IMenuScreen;
import gg.codie.mineonline.gui.MainMenuScreen;
import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PlayerRendererTest {

    static boolean formopen = false;

    public static void setMenuScreen(IMenuScreen menuScreen) {
        PlayerRendererTest.menuScreen = menuScreen;
    }

    private static IMenuScreen menuScreen;

    static WindowAdapter closeListener = new WindowAdapter(){
        public void windowClosing(WindowEvent e){
            DisplayManager.getFrame().dispose();
        }
    };

    public static void main(String[] args) throws Exception {
        formopen = true;

//        DisplayManager.getFrame().setResizable(false);

        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        DisplayManager.init();

        DisplayManager.createDisplay();

        DisplayManager.getFrame().addWindowListener(closeListener);

        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);
        Loader loader = new Loader();
        GameObject playerPivot = new GameObject("player_origin", new Vector3f(), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));
        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -21, 0), new Vector3f(), new Vector3f(1, 1, 1));
        new Session("codie");
        playerPivot.addChild(playerGameObject);

        GameObject playerScale = new GameObject("player scale", new Vector3f(-20, 0, -65), new Vector3f(), new Vector3f(1, 1, 1));
        playerScale.addChild(playerPivot);

        playerGameObject.setPlayerAnimation(new IdlePlayerAnimation());
        Camera camera = new Camera();

        RawModel model = loader.loadBoxToVAO(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1), TextureHelper.getCubeTextureCoords(new Vector2f(2048, 1024),
                new Vector2f(0, 0), new Vector2f(2048, 1024),
                new Vector2f(0, 0), new Vector2f(2048, 1024),
                new Vector2f(0, 0), new Vector2f(2048, 1024),
                new Vector2f(0, 0), new Vector2f(2048, 1024),
                new Vector2f(0, 0), new Vector2f(2048, 1024),
                new Vector2f(0, 0), new Vector2f(2048, 1024)
        ));
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(PlayerRendererTest.class.getResource("/img/background.png")));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);
        GameObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(75f, 75f, 75f));

        menuScreen = new MainMenuScreen();

        //playerScale.scale(new Vector3f(1, 0.5f, 1));

        int lastWidth = Display.getWidth();
        int lastHeight = Display.getHeight();
        // Game Loop
        while(!Display.isCloseRequested() && formopen) {
            renderer.prepare();
            // Camera roll lock.
            // Broken and not necessary.

//            if(playerPivot.getLocalRotation().z > 0) {
//                playerPivot.increaseRotation(new Vector3f(0, 0, -playerPivot.getLocalRotation().z));
//            }

            if(Display.getWidth() != lastWidth || Display.getHeight() != lastHeight) {
                menuScreen.resize();
            }

            lastWidth = Display.getWidth();
            lastHeight = Display.getHeight();

            backgroundImage.increaseRotation(new Vector3f(0, 0.025f, 0));

            Vector3f currentScale = MathUtils.getScale(playerPivot.localMatrix);
            ///playerPivot.scale(new Vector3f(2 - currentScale.x, 2 - currentScale.y, 2 - currentScale.z));
            playerPivot.setScale(new Vector3f(1, 2, 1));

            if(Mouse.isButtonDown(0)) {
                Vector3f currentRotation = playerPivot.getLocalRotation();
                Vector3f rotation = new Vector3f();

                // Camera pitch rotation with lock.
                // Currently broken.

//                float dy = Mouse.getDY();

//                if(currentRotation.x + (dy * -0.3f) > 30) {
//                    rotation.x = 30 - currentRotation.x;
//                } else if(currentRotation.x + (dy * -0.3f) < -30) {
//                    rotation.x = -30 - currentRotation.x;
//                } else {
//                    rotation.x = dy * -0.3f;
//                }

                rotation.y = (Mouse.getDX() * 0.5f);

                if (menuScreen.showPlayer())
                    playerPivot.increaseRotation(rotation);
            }

            playerGameObject.update();
            menuScreen.update();

            camera.move();

            shader.start();
            shader.loadViewMatrix(camera);

            renderer.render(backgroundImage, shader);
            if (menuScreen.showPlayer())
                renderer.render(playerGameObject, shader);

            shader.stop();

            menuScreen.render(renderer);


            if (menuScreen.showPlayer())
                renderer.renderCenteredString(new Vector2f(250, 100), Session.session.getUsername(), Color.white);

            renderer.renderString(new Vector2f(2, 10), "MineOnline Debug", org.newdawn.slick.Color.yellow); //x, y, string to draw, color


            DisplayManager.updateDisplay();

        }

        shader.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();

        DisplayManager.getFrame().removeWindowListener(closeListener);
    }

}
