package mnm.mods.tabbychat.gui.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.TabbyChatClient;
import mnm.mods.util.Color;
import mnm.mods.util.ILocation;
import mnm.mods.util.Location;
import mnm.mods.util.config.SettingsFile;
import mnm.mods.util.gui.BorderLayout;
import mnm.mods.util.gui.ComponentScreen;
import mnm.mods.util.gui.GuiButton;
import mnm.mods.util.gui.GuiComponent;
import mnm.mods.util.gui.GuiPanel;
import mnm.mods.util.gui.VerticalLayout;
import mnm.mods.util.gui.config.SettingPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GuiSettingsScreen extends ComponentScreen {

    private static Map<Class<? extends SettingPanel<?>>, Supplier<? extends SettingPanel<?>>> settings = Maps.newLinkedHashMap();

    static {
        registerSetting(GuiSettingsGeneral.class, GuiSettingsGeneral::new);
        registerSetting(GuiSettingsServer.class, GuiSettingsServer::new);
        registerSetting(GuiSettingsChannel.class, GuiSettingsChannel::new);
        registerSetting(GuiAdvancedSettings.class, GuiAdvancedSettings::new);
    }

    private List<SettingPanel<?>> panels = Lists.newArrayList();

    private GuiPanel panel;

    private GuiPanel settingsList;
    private SettingPanel<?> selectedSetting;

    public GuiSettingsScreen(SettingPanel<?> setting) {
        this.selectedSetting = setting;

        for (Map.Entry<Class<? extends SettingPanel<?>>, Supplier<? extends SettingPanel<?>>> sett : settings.entrySet()) {
            try {
                if (setting != null && setting.getClass() == sett.getKey()) {
                    panels.add(setting);
                } else {
                    panels.add(sett.getValue().get());
                }
            } catch (Exception e) {
                TabbyChat.logger.error("Unable to add {} as a setting.", sett.getKey(), e);
            }
        }
    }

    @Override
    public void initGui() {


        getPanel().addComponent(panel = new GuiPanel(new BorderLayout()));

        int x = this.width / 2 - 300 / 2;
        int y = this.height / 2 - 200 / 2;
        panel.setLocation(new Location(x, y, 300, 200));

        GuiPanel panel = new GuiPanel(new BorderLayout());
        this.panel.addComponent(panel, BorderLayout.Position.WEST);
        panel.addComponent(settingsList = new GuiPanel(new VerticalLayout()), BorderLayout.Position.WEST);

        GuiButton close = new GuiButton("Close") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                mc.displayGuiScreen(null);
            }
        };
        close.setLocation(new Location(0, 0, 40, 10));
        close.setSecondaryColor(Color.of(0, 255, 0, 127));
        panel.addComponent(close, BorderLayout.Position.SOUTH);

        {
            // Populate the settings
            for (SettingPanel<?> sett : panels) {
                SettingsButton button = new SettingsButton(sett) {
                    @Override
                    public void onClick(double mouseX, double mouseY) {
                        selectSetting(getSettings());
                    }
                };
                settingsList.addComponent(button);
                sett.initGUI();
            }
        }
        SettingPanel<?> panelClass;
        if (selectedSetting == null) {
            panelClass = panels.get(0);
        } else {
            panelClass = selectedSetting;
        }
        selectSetting(panelClass);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        for (SettingPanel<?> settingPanel : panels) {
            SettingsFile config = settingPanel.getSettings();
            try {
                config.save();
            } catch (IOException e) {
                TabbyChat.logger.warn("Unable to save server config", e);
            }
        }
        TabbyChatClient.getInstance().getChat().getChatBox().getChatArea().markDirty();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.panels.forEach(GuiPanel::clearComponents);
        super.setWorldAndResolution(mc, width, height);
    }

    private void deactivateAll() {
        for (GuiComponent comp : settingsList.getChildren()) {
            if (comp instanceof SettingsButton) {
                ((SettingsButton) comp).setActive(false);
            }
        }
    }

    private <T extends SettingPanel<?>> void activate(Class<T> settingClass) {
        for (GuiComponent comp : settingsList.getChildren()) {
            if (comp instanceof SettingsButton
                    && ((SettingsButton) comp).getSettings().getClass().equals(settingClass)) {
                ((SettingsButton) comp).setActive(true);
                break;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        // drawDefaultBackground();
        ILocation rect = panel.getLocation();
        Gui.drawRect(rect.getXPos(), rect.getYPos(), rect.getXWidth(), rect.getYHeight(), Integer.MIN_VALUE);
        super.render(mouseX, mouseY, tick);
    }

    private void selectSetting(SettingPanel<?> setting) {
//        setting.clearComponents();
        deactivateAll();
        panel.removeComponent(selectedSetting);
        selectedSetting = setting;
        activate(setting.getClass());
        this.panel.addComponent(this.selectedSetting, BorderLayout.Position.CENTER);
    }

    private static <T extends SettingPanel<?>> void registerSetting(Class<T> settings, Supplier<T> constructor) {
        if (!GuiSettingsScreen.settings.containsKey(settings)) {
            GuiSettingsScreen.settings.put(settings, constructor);
        }
    }

}
