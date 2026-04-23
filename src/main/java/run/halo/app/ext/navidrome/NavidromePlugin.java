package run.halo.app.ext.navidrome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

@Component
public class NavidromePlugin extends BasePlugin {

    private static final Logger log = LoggerFactory.getLogger(NavidromePlugin.class);

    public NavidromePlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        log.info("Navidrome plugin started.");
    }

    @Override
    public void stop() {
        log.info("Navidrome plugin stopped.");
    }
}
