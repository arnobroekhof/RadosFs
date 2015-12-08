package org.elasticsearch.repositories.ceph.blobstore;

import org.elasticsearch.ceph.rados.service.RadosService;
import org.elasticsearch.common.blobstore.BlobContainer;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.threadpool.ThreadPool;

import java.io.IOException;
import java.util.concurrent.Executor;


/**
 * Created by arno.broekhof on 30-11-15.
 */
public class RadosBlobStore extends AbstractComponent implements BlobStore {

    private final ThreadPool threadPool;
    private final int bufferSizeInBytes;
    private final RadosService radosService;

    public RadosBlobStore(Settings settings, ThreadPool threadPool, RadosService radosService) {
        super(settings);

        this.radosService = radosService;
        this.threadPool = threadPool;
        this.bufferSizeInBytes = (int) settings.getAsBytesSize("buffer_size", new ByteSizeValue(100, ByteSizeUnit.KB)).bytes();

    }

    public Executor executor() {
        return threadPool.executor(ThreadPool.Names.SNAPSHOT);
    }

    /**
     * Get our buffer size
     */
    public int bufferSizeInBytes() {
        return bufferSizeInBytes;
    }


    private String translateToRadosObject(BlobPath blobPath) {
        return blobPath.buildAsString("_");
    }

    @Override
    public BlobContainer blobContainer(BlobPath blobPath) {
        return null;
    }

    @Override
    public void delete(BlobPath blobPath) throws IOException {

    }

    @Override
    public void close() {
        logger.info("Closing RadosBlobStore");
    }
}
