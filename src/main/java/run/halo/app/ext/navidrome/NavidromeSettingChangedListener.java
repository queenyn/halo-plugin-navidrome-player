package run.halo.app.ext.navidrome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.Plugin;
import run.halo.app.plugin.PluginConfigUpdatedEvent;
import run.halo.app.plugin.PluginContext;

@Component
public class NavidromeSettingChangedListener {

    private static final Logger log = LoggerFactory.getLogger(NavidromeSettingChangedListener.class);

    private final PluginContext pluginContext;

    public NavidromeSettingChangedListener(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    @EventListener
    public void onPluginConfigUpdated(PluginConfigUpdatedEvent event) {
        if (!(event.getSource() instanceof Plugin plugin)) {
            return;
        }

        if (plugin.getMetadata() == null || !pluginContext.getName().equals(plugin.getMetadata().getName())) {
            return;
        }

        log.info("Navidrome settings updated. Subsequent ReactiveSettingFetcher reads will use the latest values automatically.");
    }
}
