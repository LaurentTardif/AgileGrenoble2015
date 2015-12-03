cd ~/flink/build-target/bin

rm /tmp/out.tx*
./stop-local.sh
#copy needed jar to flink
cp ~/flinkDemo/3rdParty/twitter/joauth-6.0.2.jar ~/flink/build-target/lib
cp ~/flinkDemo/3rdParty/twitter/hbc-core-2.2.0.jar ~/flink/build-target/lib
cp ~/flinkDemo/3rdParty/twitter/guava-14.0.1.jar ~/flink/build-target/lib

#copy last build jar into lib dir
cp ~/AgileGrenoble2015/BackEnd/target/flink-demo-twitter-1.0-SNAPSHOT.jar ~/flink/build-target/lib/

#when dll are ok, start it
./start-local-streaming.sh


#run the filter twitter example
./flink run -c org.agile.grenoble.twitter.AgileSimpleStreamHistory /home/adminpsl/flink/build-target/lib/flink-demo-twitter-1.0-SNAPSHOT.jar /home/adminpsl/flinkDemo/twitter.properties  Grenoble
