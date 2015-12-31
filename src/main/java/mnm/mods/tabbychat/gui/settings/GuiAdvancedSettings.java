package mnm.mods.tabbychat.gui.settings;

import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.settings.TabbySettings;
import mnm.mods.tabbychat.util.ChatVisibility;
import mnm.mods.tabbychat.util.Translation;
import mnm.mods.util.Color;
import mnm.mods.util.gui.GuiGridLayout;
import mnm.mods.util.gui.GuiLabel;
import mnm.mods.util.gui.config.GuiSettingBoolean;
import mnm.mods.util.gui.config.GuiSettingEnum;
import mnm.mods.util.gui.config.GuiSettingNumber.GuiSettingInt;
import mnm.mods.util.gui.config.SettingPanel;

public class GuiAdvancedSettings extends SettingPanel<TabbySettings> {

    public GuiAdvancedSettings() {
        setLayout(new GuiGridLayout(10, 15));
        setDisplayString(Translation.SETTINGS_ADVANCED.toString());
        setBackColor(Color.getColor(255, 0, 0, 64));
    }

    @Override
    public void initGUI() {
        addComponent(new GuiLabel(Translation.ADVANCED_FADE_TIME.toString()), new int[] { 1, 1 });
        GuiSettingInt gsi = new GuiSettingInt(getSettings().advanced.fadeTime);
        gsi.getInput().setInterval(50);
        addComponent(gsi, new int[] { 5, 1, 2, 1 });

        addComponent(new GuiLabel(Translation.ADVANCED_CHAT_DELAY.toString()), new int[] { 1, 3 });
        gsi = new GuiSettingInt(getSettings().advanced.msgDelay);
        gsi.getInput().setInterval(50);
        addComponent(gsi, new int[] { 5, 3, 2, 1 });

        addComponent(new GuiLabel(Translation.ADVANCED_CHAT_VISIBILITY.toString()), new int[] { 1, 5 });
        addComponent(new GuiSettingEnum<ChatVisibility>(getSettings().advanced.visibility, ChatVisibility.values()), new int[] { 5, 5, 3, 1 });

        addComponent(new GuiLabel(Translation.ADVANCED_HIDE_DELIMS.toString()), new int[] { 2, 7 });
        addComponent(new GuiSettingBoolean(getSettings().advanced.hideTag), new int[] { 1, 7 });

        addComponent(new GuiLabel(Translation.EXPERIMENTAL.toString()), new int[] { 0, 13 });
    }

    @Override
    public TabbySettings getSettings() {
        return TabbyChat.getInstance().settings;
    }
}
