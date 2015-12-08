package org.elasticsearch.repositories.ceph;

import com.ceph.rados.IoCTX;
import com.ceph.rados.Rados;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.junit.*;

import java.io.File;
import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by arno.broekhof on 30-11-15.
 */
public class CephBaseTest {

    private static String ENV_CONFIG_FILE = System.getenv("RADOS_JAVA_CONFIG_FILE");
    private static String ENV_ID = System.getenv("RADOS_JAVA_ID");
    private static String ENV_POOL = System.getenv("RADOS_JAVA_POOL");

    private static final String CONFIG_FILE = ENV_CONFIG_FILE == null ? "/etc/ceph/ceph.conf" : ENV_CONFIG_FILE;
    private static final String ID = ENV_ID == null ? "admin" : ENV_ID;
    private static final String POOL = ENV_POOL == null ? "data" : ENV_POOL;

    private static Rados rados;
    private static IoCTX ioctx;

    private EmbeddedElasticSearchServer embeddedElasticSearchServer;

    @BeforeClass
    public static void setUpCeph() throws Exception {
        rados = new Rados(ID);
        rados.confReadFile(new File(CONFIG_FILE));
        rados.connect();

    }

    @Before
    public void setupElasticsearchServer() throws Exception {
        embeddedElasticSearchServer = new EmbeddedElasticSearchServer();
    }

    @After
    public void destroyElasticsearchServer() throws Exception {
        embeddedElasticSearchServer.shutdown();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        rados.shutDown();
    }

    @Test
    public void testGetPools() throws Exception {
        String[] pools  = rados.poolList();
        for (String pool: pools) {
            System.out.println(pool);
            assertNotNull(pool);
        }

    }

    @Test
    public void indexAndGet() throws IOException {
        getClient().prepareIndex("myindex", "document", "1")
                .setSource(jsonBuilder().startObject().field("test", "123").endObject())
                .execute()
                .actionGet();

        GetResponse response = getClient().prepareGet("myindex", "document", "1").execute().actionGet();
        assertEquals(response.getSource().get("test"), "123");
    }

    public Client getClient() {
        return embeddedElasticSearchServer.getClient();
    }


}
