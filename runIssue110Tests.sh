#!/bin/bash
mvn package -Dgpg.skip=true -DskipTests=true -Dmaven.javadoc.skip=true

rm -Rf issue110test
mkdir issue110test

pushd issue110test >/dev/null

mkdir startdir
ln -s startdir startdirl

mkdir testdir
ln -s testdir testdirl

touch testfile
ln -s testfile testfilel

pushd startdir >/dev/null

echo "### startdir ###"

echo "../testdir"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testdir

echo "../testdirl"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testdirl

echo "../testfile"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testfile

echo "../testfilel"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testfilel

echo "../testnotthere"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testnotthere

popd >/dev/null

pushd startdirl >/dev/null

echo "### startdirl ###"

echo "../testdir"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testdir

echo "../testdirl"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testdirl

echo "../testfile"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testfile

echo "../testfilel"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testfilel

echo "../testnotthere"
java -cp ../../main/target/argparse4j-0.9.0-SNAPSHOT.jar:../../issue110/target/argparse4j-issue110-0.9.0-SNAPSHOT.jar net.sourceforge.argparse4j.issue110.Issue110App ../testnotthere

popd >/dev/null

popd >/dev/null
