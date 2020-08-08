name := "PayPayDataChallenge"

version := "0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-core_2.12" % "2.4.6",
    "org.apache.spark" % "spark-sql_2.12" % "2.4.6",
    "org.apache.spark" % "spark-mllib_2.12" % "2.4.6" % "runtime"
)
