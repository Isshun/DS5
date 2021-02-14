package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.notification.NotificationLevel;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;

import static org.smallbox.faraway.client.notification.NotificationLevel.*;

@GameObject
public class NotificationController extends LuaController {
    private final static Color BG_COLOR_CRITICAL = new Color(0xBE000033);
    private final static Color COLOR_CRITICAL = new Color(0xBE0000FF);
    private final static Color COLOR_SEVERE = new Color(0xFF0000FF);
    private final static Color COLOR_WARNING = new Color(0xFFB600FF);
    private final static Color COLOR_INFO = new Color(0xFFFFFFFF);

    @BindLua private UIList listNotification;

    @OnGameLayerBegin
    private void test() {
        addNotification(CRITICAL, "Starvation");
        addNotification(CRITICAL, "Suffocating");
        addNotification(CRITICAL, "Severely injured");
        addNotification(SEVERE, "Threatening wild animal");
        addNotification(SEVERE, "Hostile visitor");
        addNotification(SEVERE, "Dying plant");
        addNotification(SEVERE, "Unrefrigerated food");
        addNotification(WARNING, "Slightly injured");
        addNotification(WARNING, "High stress");
        addNotification(WARNING, "Building broken");
        addNotification(WARNING, "Building out of order");
        addNotification(WARNING, "Heat stroke");
        addNotification(WARNING, "Hypothermia");
        addNotification(WARNING, "Building lacks resources");
        addNotification(WARNING, "Invalid construction location");
        addNotification(WARNING, "No research selected");
        addNotification(WARNING, "Power cut");
        addNotification(INFO, "Friendly visitor");
        addNotification(INFO, "Spaceship nearby");
        listNotification.switchViews();
    }

    public void addNotification(NotificationLevel level, String text) {
        CompositeView viewNotification = listNotification.createFromTemplate(CompositeView.class);

        UILabel label = viewNotification.findLabel("lb_notification");
        label.setText(text);

        switch (level) {
            case INFO: label.setTextColor(COLOR_INFO); break;
            case WARNING: label.setTextColor(COLOR_WARNING); break;
            case SEVERE: label.setTextColor(COLOR_SEVERE); break;
            case CRITICAL: label.setTextColor(COLOR_CRITICAL); break;
        }

        listNotification.addNextView(viewNotification);
    }

}
