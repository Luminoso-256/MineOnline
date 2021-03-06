package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class FOVViewmodelAdvice {
    public static boolean leftHanded;

    @Advice.OnMethodEnter
    static void intercept() {
        try {
            float originalFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("originalFOV").get(null);
            float zFar = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("zFar").get(null);
            boolean leftHanded = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.FOVViewmodelAdvice").getField("leftHanded").get(null);

            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("ignore").set(null, true);
            if (leftHanded)
                GL11.glScalef(-1, 1, 1);
            GLU.gluPerspective(originalFOV, (float) Display.getWidth() / Display.getHeight(), 0.05f, zFar);
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("ignore").set(null, false);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
