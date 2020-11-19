// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiComponent;
import gg.codie.mineonline.gui.components.GuiTextField;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.utils.JREUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            GameSettings, GuiConnecting

public class GuiDirectConnect extends AbstractGuiScreen
{

    public GuiDirectConnect(AbstractGuiScreen guiscreen, String serverString)
    {
        parentScreen = guiscreen;
        initGui(serverString);
    }

    public GuiDirectConnect(AbstractGuiScreen guiscreen)
    {
        this(guiscreen, null);
    }

    public void updateScreen()
    {
        if(textField != null)
            textField.updateCursorCounter();
    }

    public void initGui(String serverString)
    {
        Keyboard.enableRepeatEvents(true);
        AbstractGuiScreen thisScreen = this;
        controlList.clear();

        GuiVersions.IVersionSelectListener selectListener = new GuiVersions.IVersionSelectListener() {
            @Override
            public void onSelect(String path) {
                joinServer(path);
            }
        };

        controlList.add(new GuiButton(0, getWidth() / 2 - 100, getHeight() / 4 + 96 + 12, "Connect", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {

                GuiSlotVersion.ISelectableVersionCompare compare = new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null && MinecraftVersionRepository.getSingleton().getLastSelectedJarPath().equals(selectableVersion.path);
                    }
                };

                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiVersions(thisScreen, null, selectListener, compare, false));
                else
                    MenuManager.setMenuScreen(new GuiVersions(thisScreen, null, selectListener, compare, false));

                //joinServer();
            }
        }));
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, "Cancel", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
//        MinecraftVersion selected = MinecraftVersionRepository.getSingleton().getVersion(MinecraftVersionRepository.getSingleton().getLastSelectedJarPath());
//        String versionName = "Select Version";
//        if (MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null)
//            versionName = Paths.get(MinecraftVersionRepository.getSingleton().getLastSelectedJarPath()).getFileName().toString();
//        if (selected != null)
//            versionName = selected.name;
//        controlList.add(new GuiButton(2, getWidth() / 2 - 100, (getHeight() / 4 - 10) - 50 + 18, versionName, new GuiButton.GuiButtonListener() {
//            @Override
//            public void OnButtonPress() {
//                if (LegacyGameManager.isInGame())
//                    LegacyGameManager.setGUIScreen(new GuiVersions(thisScreen));
//                else
//                    MenuManager.setMenuScreen(new GuiVersions(thisScreen));
//            }
//        }));
        String s = Settings.singleton.getLastServer().replaceAll("_", ":");
        if (serverString != null)
            s = serverString;
        ((GuiButton)controlList.get(0)).enabled = s.length() > 0;
        textField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, 200, 20, s);
        textField.isFocused = true;
        textField.setMaxStringLength(128);
    }

    public void resizeGui() {
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 4 + 96 + 12);
        controlList.get(1).resize(getWidth() / 2 - 100, getHeight() / 4 + 120 + 12);
        textField.resize(getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    private void joinServer(String jarPath) {
        String s = textField.getText().trim();
        Settings.singleton.setLastServer(s.replaceAll(":", "_"));
        Settings.singleton.saveSettings();
        String as[] = s.split(":");
        if(s.startsWith("["))
        {
            int i = s.indexOf("]");
            if(i > 0)
            {
                String s1 = s.substring(1, i);
                String s2 = s.substring(i + 1).trim();
                if(s2.startsWith(":") && s2.length() > 0)
                {
                    s2 = s2.substring(1);
                    as = new String[2];
                    as[0] = s1;
                    as[1] = s2;
                } else
                {
                    as = new String[1];
                    as[0] = s1;
                }
            }
        }
        if(as.length > 2)
        {
            as = new String[1];
            as[0] = s;
        }

        try {
            try {
                String mppass = MineOnlineAPI.getMpPass(Session.session.getAccessToken(), Session.session.getUsername(), Session.session.getUuid(), as[0], (as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565)) + "");
                MinecraftVersion.launchMinecraft(jarPath, as[0], (as.length <= 1 ? "25565" : as[1]), mppass);

                if (LegacyGameManager.isInGame())
                    LegacyGameManager.closeGame();
                else {
                    Display.destroy();
                    DisplayManager.getFrame().dispose();
                    System.exit(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // ignore for now
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore for now
        }
    }

    private int parseIntWithDefault(String s, int i)
    {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch(Exception exception)
        {
            return i;
        }
    }

    protected void keyTyped(char c, int i)
    {
        textField.textboxKeyTyped(c, i);
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }
        ((GuiButton)controlList.get(0)).enabled = textField.getText().length() > 0;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        textField.mouseClicked(i, j, k);
    }

    @Override
    public void drawScreen(int i, int j)
    {
        resizeGui();

        drawDefaultBackground();
        drawCenteredString("Play Multiplayer", getWidth() / 2, (getHeight() / 4 - 60) + 20, 0xffffff);
        drawString("Minecraft Multiplayer is currently not finished, but there", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60, 0xa0a0a0);
        drawString("is some buggy early testing going on.", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 9, 0xa0a0a0);
        drawString("Enter the IP of a server to connect to it:", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 36, 0xa0a0a0);
        textField.drawTextBox();
        super.drawScreen(i, j);
    }

    private AbstractGuiScreen parentScreen;
    private GuiTextField textField;
}
