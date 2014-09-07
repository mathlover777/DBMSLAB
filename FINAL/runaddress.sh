export CLASSPATH=$CLASSPATH:commons-io-2.4.jar:jackson-core-asl-1.8.5.jar:jackson-mapper-asl-1.8.5.jar

javac -cp $CLASSPATH AddressConverter.java

java -cp . AddressConverter 