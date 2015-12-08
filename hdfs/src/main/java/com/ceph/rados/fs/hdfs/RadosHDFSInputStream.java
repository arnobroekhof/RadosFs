package com.ceph.rados.fs.hdfs;

import com.ceph.rados.IoCTX;
import com.ceph.rados.fs.RadosFileSystemStore;
import com.ceph.rados.fs.RadosInputStream;

/**
 * Created by arno.broekhof on 7-12-15.
 */
public class RadosHDFSInputStream extends RadosInputStream {

    public RadosHDFSInputStream(RadosFileSystemStore store, String id) {
        super(store.getIoCTX(), id);
    }
}
