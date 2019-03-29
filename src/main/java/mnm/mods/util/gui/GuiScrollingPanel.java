package mnm.mods.util.gui;

import static org.lwjgl.opengl.GL11.*;

import mnm.mods.util.Dim;
import mnm.mods.util.ILocation;
import mnm.mods.util.Location;
import mnm.mods.util.gui.BorderLayout.Position;
import net.minecraft.client.gui.Gui;

import javax.annotation.Nonnull;

/**
 * TODO: Horizontal scrolling
 *
 * @author Matthew
 */
public class GuiScrollingPanel extends GuiPanel {

    private GuiPanel panel;

    public GuiScrollingPanel() {
        super(new BorderLayout());
        this.panel = new GuiPanel();
        this.panel.setLocation(new Location(0, 0, 100000, 100000));
        GuiPanel panel = new GuiPanel();
        panel.addComponent(this.panel);
        this.addComponent(panel, Position.CENTER);
        this.addComponent(new Scrollbar(), Position.EAST);
    }

    @Override
    public void render(int mouseX, int mouseY, float parTicks) {
        ILocation rect = getLocation();

        glEnable(GL_SCISSOR_TEST);
        glScissor(rect.getXPos(), mc.mainWindow.getHeight() - rect.getYPos(), rect.getWidth(), rect.getHeight());

        super.render(mouseX, mouseY, parTicks);

        glDisable(GL_SCISSOR_TEST);
    }

    @Override
    public boolean mouseScrolled(double scroll) {
        Location rect = panel.getLocation().copy();
        int scr = (int) (rect.getYPos() + scroll / 12);
        rect.setYPos(scr);

        panel.getParent().map(GuiComponent::getLocation).ifPresent(prect -> {
            Dim dim = panel.getMinimumSize();
            if (rect.getYPos() + dim.height < prect.getHeight()) {
                rect.setYPos(prect.getHeight() - dim.height);
            }
        });
        getParent().ifPresent(parent -> {
            if (rect.getYPos() > parent.getLocation().getYPos()) {
                rect.setYPos(parent.getLocation().getYPos());
            }
        });
        panel.setLocation(rect);
        return true;
    }

    public GuiPanel getContentPanel() {
        return panel;
    }

    @Nonnull
    @Override
    public Dim getMinimumSize() {
        return getLocation().getSize();
    }

    // TODO Make draggable
    private class Scrollbar extends GuiComponent {

        @Override
        public void render(int mouseX, int mouseY, float parTicks) {
            ILocation loc = GuiScrollingPanel.this.getLocation();
            int scroll = panel.getLocation().getYPos();
            int min = loc.getYPos();
            int max = loc.getYHeight();
            int total = panel.getMinimumSize().height;
            if (total <= max) {
                return;
            }
            total -= max;
            Gui.drawRect(0, 20, 10, 10, -1);
            int size = Math.max(max / 2, 10);
            float perc = ((float) scroll / (float) total) * ((float) size / (float) max);
            int pos = (int) (-perc * max);

            Gui.drawRect(loc.getXPos()-1, loc.getYPos() + pos, loc.getXPos(), loc.getYPos() + pos + size - 1, -1);
            super.render(mouseX, mouseY, parTicks);
        }
    }
}
