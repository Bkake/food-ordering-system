# start zookeeper
docker-compose -f common.yml -f zookeeper.yml up 

# verify install
telnet localhost 2181
srvr

# start cluster kafka
docker-compose -f common.yml -f kafka_cluster.yml up 

# init
docker-compose -f common.yml -f init_kafka.yml up 
