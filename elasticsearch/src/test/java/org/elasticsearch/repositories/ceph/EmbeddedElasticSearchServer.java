package org.elasticsearch.repositories.ceph;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.plugin.ceph.rados.RadosPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by arno.broekhof on 30-11-15.
 */
public class EmbeddedElasticSearchServer {

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticSearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticSearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "true")
                .put("path.plugins", "target")
                .put("repositories.ceph.enabled", "true")
                .put("repositories.ceph.conf_file", "/etc/ceph/ceph.conf")
                .put("repositories.ceph.id", "admin")
                .put("repositories.ceph.pool", "elasticsearch_snapshot")
                .put("plugin.types", RadosPlugin.class)
                .put("path.data", dataDirectory);

        node = new NodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
    }


    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
        node.close();
        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}

