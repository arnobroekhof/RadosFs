package org.elasticsearch.repositories.ceph;

import org.elasticsearch.ceph.rados.service.RadosService;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.repositories.RepositoryName;
import org.elasticsearch.repositories.RepositorySettings;
import org.elasticsearch.repositories.blobstore.BlobStoreRepository;
import org.elasticsearch.repositories.ceph.blobstore.RadosBlobStore;
import org.elasticsearch.threadpool.ThreadPool;

import java.io.IOException;

/**
 * Created by arno.broekhof on 30-11-15.
 */
public class RadosRepository extends BlobStoreRepository {

    // The internal "type" for Elasticsearch
    public final static String TYPE = "ceph";

    private final RadosBlobStore blobStore;
    private final ByteSizeValue chunkSize;
    private final BlobPath basePath;
    private final boolean compress;
    private final RepositorySettings repositorySettings;

    @Inject
    public RadosRepository(RepositoryName name, RepositorySettings repositorySettings, IndexShardRepository indexShardRepository, ThreadPool threadPool, RadosService radosService) throws IOException {
        super(name.getName(), repositorySettings, indexShardRepository);

        this.repositorySettings = repositorySettings;
        String pool = repositorySettings.settings().get("pool", settings.get("repositories.ceph.pool"));
        if (pool == null) {
            throw new IllegalArgumentException("no 'pool' defined for ceph snapshot/restore");
        }

        logger.info("Using pool: [{}] for snapshot/restore", pool);

        blobStore = new RadosBlobStore(settings, threadPool, radosService);
        this.chunkSize = repositorySettings.settings().getAsBytesSize("chunk_size", settings.getAsBytesSize("chunk_size", null));
        this.compress = repositorySettings.settings().getAsBoolean("compress", settings.getAsBoolean("compress", false));
        this.basePath = BlobPath.cleanPath();


    }


    @Override
    protected BlobStore blobStore() {
        return blobStore;
    }

    @Override
    protected BlobPath basePath() {
        return basePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCompress() {
        return compress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ByteSizeValue chunkSize() {
        return chunkSize;
    }
}
