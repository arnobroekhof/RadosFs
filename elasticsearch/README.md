

# Register repository
curl -XPUT 'http://localhost:9200/_snapshot/ceph-test1' -d '{ "type" : "ceph", "pool" : "elasticsearch_snapshot" }'

 # start snapshot
curl -XPUT 'http://localhost:9200/_snapshot/ceph-test1/testing_snapshot'
