cd ~/flink/build-target/bin

rm /tmp/out.tx*
./stop-local.sh

#copy last build jar into lib dir
cp ~/AgileGrenoble2015/BackEnd/target/flink-demo-twitter-1.0-SNAPSHOT.jar ~/flink/build-target/lib/

#when dll are ok, start it
./start-local-streaming.sh

#run the default twitter example
#./flink run -c org.agile.grenoble.twitter.TwitterStream /home/adminpsl/flink/build-target/lib/flink-demo-twitter-1.0-SNAPSHOT.jar /home/adminpsl/flinkDemo/twitter.properties  /tmp/out.txt


#run the filter twitter example
./flink run -c org.agile.grenoble.twitter.AgileGrenobleLive /home/adminpsl/flink/build-target/lib/flink-demo-twitter-1.0-SNAPSHOT.jar /home/adminpsl/flinkDemo/twitter.properties  /tmp/out.txt
