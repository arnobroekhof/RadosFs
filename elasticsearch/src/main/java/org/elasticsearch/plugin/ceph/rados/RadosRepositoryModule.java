package org.elasticsearch.plugin.ceph.rados;

import org.elasticsearch.ceph.rados.service.RadosService;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.index.snapshots.IndexShardRepository;
import org.elasticsearch.index.snapshots.blobstore.BlobStoreIndexShardRepository;
import org.elasticsearch.repositories.Repository;
import org.elasticsearch.repositories.ceph.RadosRepository;

/*
 * Rados Repository Module that binds classes.
 */
public class RadosRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Repository.class).to(RadosRepository.class).asEagerSingleton();
        bind(IndexShardRepository.class).to(BlobStoreIndexShardRepository.class).asEagerSingleton();
        bind(RadosService.class).asEagerSingleton();

    }
}
