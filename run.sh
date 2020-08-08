echo "This project is a challenge task for Paypay"
echo "To run the project, make sure you have Spark 2.3+ and SBT installed"

sbt clean assembly

spark-submit target/scala-2.12/PayPayDataChallenge-assembly-0.1-SNAPSHOT.jar