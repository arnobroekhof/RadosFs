package org.elasticsearch.plugin.ceph.rados;

import com.sun.istack.internal.logging.Logger;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.repositories.RepositoriesModule;
import org.elasticsearch.repositories.ceph.RadosRepository;
import org.elasticsearch.ceph.rados.service.RadosService;

import java.util.Collection;

/**
 * Created by arno.broekhof on 30-11-15.
 */
public class RadosPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(RadosPlugin.class);

    // Elasticsearch settings
    private final Settings settings;

    /**
     * Constructor. Sets settings to settings.
     * @param settings Our settings
     */
    public RadosPlugin(Settings settings) {
        this.settings = settings;
    }

    public String name() {
        return "elasticsearch-ceph";
    }

    public String description() {
        return "Elasticsearch Ceph Rados Repository plugin";
    }

    /**
     * Register our services, if needed.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Collection<Class<? extends LifecycleComponent>> services() {
        Collection<Class<? extends LifecycleComponent>> services = Lists.newArrayList();
        if (settings.getAsBoolean("repositories.ceph.enabled",false)) {
            logger.info("Ceph repository set to enabled");
            services.add(RadosService.class);
        } else {
            logger.info("Ceph plugin present but not enabled, try adding ceph.repository.enabled = true");
        }
        return services;
    }

    /**
     * Load our repository module into the list, if enabled
     * @param repositoriesModule The repositories module to register ourselves with
     */
    public void onModule(RepositoriesModule repositoriesModule) {
        if (settings.getAsBoolean("repositories.ceph.enabled", false)) {
            repositoriesModule.registerRepository(RadosRepository.TYPE, RadosRepositoryModule.class);
        }
    }
}
