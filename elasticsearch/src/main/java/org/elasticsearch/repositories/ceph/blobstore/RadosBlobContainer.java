package org.elasticsearch.repositories.ceph.blobstore;

import com.ceph.rados.IoCTX;
import com.ceph.rados.exceptions.RadosException;
import com.ceph.rados.fs.*;
import com.ceph.rados.fs.RadosOutputStream;
import org.elasticsearch.ceph.rados.service.RadosService;
import org.elasticsearch.common.blobstore.BlobMetaData;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.support.AbstractBlobContainer;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.collect.ImmutableMap;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by arno.broekhof on 3-12-15.
 */
public class RadosBlobContainer extends AbstractBlobContainer {

    private RadosService radosService;

    protected RadosBlobContainer(BlobPath path, RadosService radosService) {

        super(path);
        this.radosService = radosService;
    }


    @Override
    public boolean blobExists(String s) {
        return radosService.objectExists(s);
    }

    @Override
    public InputStream openInput(String s) throws IOException {
        RadosInputStream radosInputStream = null;
        try {
            radosInputStream = new RadosInputStream(radosService.getRados().ioCtxCreate(radosService.getCephPool()), s);
        } catch (RadosException e) {
            e.printStackTrace();
        }
        return radosInputStream;
    }

    @Override
    public OutputStream createOutput(String s) throws IOException {
        RadosOutputStream radosOutputStream = null;
        try {
            radosOutputStream = new RadosOutputStream(radosService.getRados().ioCtxCreate(radosService.getCephPool()), s);
        } catch (RadosException e) {
            e.printStackTrace();
        }
        return radosOutputStream;
    }

    @Override
    public boolean deleteBlob(String s) throws IOException {
        try {
            return radosService.deleteObject(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ImmutableMap<String, BlobMetaData> listBlobs() throws IOException {
        return null;
    }

    @Override
    public ImmutableMap<String, BlobMetaData> listBlobsByPrefix(String s) throws IOException {
        return null;
    }
}
