package org.elasticsearch.ceph.rados.service;

import com.ceph.rados.IoCTX;
import com.ceph.rados.Rados;
import com.ceph.rados.exceptions.RadosException;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by arno.broekhof on 30-11-15.
 */
public class RadosService extends AbstractLifecycleComponent<RadosService> {

    private static Rados rados;
    private static IoCTX ioCTX;
    private Settings settings;

    private String cephConfFile;
    private String cephId;
    private String cephPool;

    @Inject
    public RadosService(Settings settings) {

        super(settings);
        this.settings = settings;
        cephConfFile = settings.get("repositories.ceph.conf_file", null);
        cephId = settings.get("repositories.ceph.id", null);
        cephPool = settings.get("repositories.ceph.pool", null);
    }

    private void connect() throws Exception {
        rados = new Rados(cephId);
        rados.confReadFile(new File(cephConfFile));
        rados.connect();
        ioCTX = rados.ioCtxCreate(cephPool);
        logger.info("Running rados version {}", this.getRadosVersion());

    }

    private boolean checkIfPoolExists() throws Exception {
        boolean _return = false;
        for (String pool : rados.poolList()) {
            if (pool.equals(cephPool)) {
                _return = true;
            }
        }
        return _return;
    }

    public Rados getRados() {
        return this.rados;
    }

    public IoCTX getIoCTX() { return this.ioCTX; }

    /**
     * Get elasticsearch injected settings.
     * @return
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Get ceph configuration file.
     * @return String example: /etc/ceph/ceph.conf
     */
    public String getCephConfFile() {
        return cephConfFile;
    }

    /**
     * Get id to connect to ceph
     * @return String for example: admin
     */
    public String getCephId() {
        return cephId;
    }

    private int[] getRadosVersion() {
        return rados.getVersion();
    }

    /**
     * Get the pool to snapshot to
     * @return String of pool to use.
     */
    public String getCephPool() {
        return cephPool;
    }

    /**
     * Try to connect to ceph
     * by using the variable
     * ceph.repository.conf.file ( ceph.conf )
     * ceph.repository.id ( default to admin )
     * ceph.repository.pool ( no default )
     * @throws ElasticsearchException
     */
    @Override
    protected void doStart() throws ElasticsearchException {
        try {
            this.connect();
            final String cephPool = settings.get("ceph.repository.pool", null);
            logger.info("Connected to ceph cluster: {}", rados.clusterFsid());
            if ( ! checkIfPoolExists()) {
                logger.error("pool {} doesn't exist ", settings.get("ceph.repository.pool"));
            }
        } catch (Exception e) {
           throw new RuntimeException("Ceph is repository is enabled but unable to connect to!");
        }
    }



    @Override
    protected void doStop() throws ElasticsearchException {
        rados.shutDown();
    }

    @Override
    protected void doClose() throws ElasticsearchException {

    }

    public boolean objectExists(final String oid) {
        try {
            if (ioCTX.stat(oid).getSize() < 0) {
                return false;
            } else {
                return true;
            }

        } catch (RadosException e) {
            // do nothing
        }
        return false;
    }

    public boolean deleteObject(final String oid) throws Exception {
        ioCTX = rados.ioCtxCreate(cephPool);
        try {
            ioCTX.remove(oid);
            return true;
        } catch (RadosException e) {
            e.printStackTrace();
        }
        return true;
    }
}
