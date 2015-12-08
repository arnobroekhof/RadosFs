package com.ceph.rados.fs.hdfs;

import com.ceph.rados.IoCTX;
import com.ceph.rados.fs.RadosFileSystemStore;
import com.ceph.rados.fs.RadosOutputStream;

/**
 * Created by arno.broekhof on 7-12-15.
 */
public class RadosHDFSOutputStream extends RadosOutputStream {

    public RadosHDFSOutputStream(RadosFileSystemStore store, String id) {
        super(store.getIoCTX(),id);

    }
}
