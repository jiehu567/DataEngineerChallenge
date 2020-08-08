package challenge.main

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SparkSession}
import paypay.challenge.main.Utility.{getUrlFromHttpRequestUdf, toEpochTimeUdf}
import challenge.main.SchemaConfig._


object ELBLogAnalyzer {

    val dataFilePath = "data/2015_07_22_mktplace_shop_web_log_sample.log.gz"
    val sessionGap: Int = 20 * 60 * 1000 // 15 minutes by milli-second
    val options = Map(
        ("delimiter" -> " "),
        ("header" -> "false"),
        ("inferSchema" -> "true"),
        ("mode" -> "DROPMALFORMED"),
        ("nullValue" -> "N/A"),
        ("quote" -> "\""),
        ("treatEmptyValuesAsNulls" -> "true")
    )

    def main(args: Array[String]): Unit = {

        // Session Configurations
        val spark = SparkSession.builder()
          .appName("ELBLogAnalyzer")
          .master("local[*]").getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")

        val rawDF = spark.read.format("csv")
          .options(options)
          .load(dataFilePath)

        rawDF.show(false)
        println("=====================")
        // rawDF.select(max(_C0_TIMESTAMP), min(_C0_TIMESTAMP)).show(false)

        val partitionByVisitorIPOrderByTimestamp = Window.partitionBy(VISITOR_IP_COL).orderBy(TIMESTAMP_COL)

        val logsWithIfNewSessionFlagDF = rawDF
          .where(_C0_TIMESTAMP_COL.isNotNull
                && _C11_HTTP_REQUEST_COL.isNotNull
                && _C7_STATUS_CODE_COL.isNotNull
            // 5xx means server error so here consider an invalid session
                && _C7_STATUS_CODE_COL < 500)
          .withColumn(TIMESTAMP, toEpochTimeUdf(col(_C0_TIMESTAMP)))
          .withColumn(URL, getUrlFromHttpRequestUdf(col(_C11_HTTP_REQUEST)))
          .withColumn(VISITOR_IP, concat(col(_C2_CLIENT),
              lit("-"),
              col(_C12_WEB_AGENT)))
          .withColumn(LAG_TIMESTAMP,
              lag(TIMESTAMP,1, 0).over(partitionByVisitorIPOrderByTimestamp))
          .withColumn(TIME_DIFF, TIMESTAMP_COL.minus(LAG_TIMESTAMP_COL).as(TIME_DIFF))
          .withColumn(IS_NEW_SESSION, when(TIME_DIFF_COL < lit(sessionGap),0).otherwise(1))
          .select(
              VISITOR_IP_COL,
              TIMESTAMP_COL,
              LAG_TIMESTAMP_COL,
              TIMESTAMP_COL,
              TIME_DIFF_COL,
              IS_NEW_SESSION_COL,
              sum(IS_NEW_SESSION_COL)
                .over(partitionByVisitorIPOrderByTimestamp)
                .as(SESSION_ID),
              col(_C2_CLIENT).as(CLIENT),
              col(_C3_TARGET).as(TARGET),
              col(_C7_STATUS_CODE).as(STATUS_CODE),
              URL_COL,
          ).cache()

        logsWithIfNewSessionFlagDF
          .where(CLIENT_COL === "1.186.37.25:59227").show(false)

        /*
        * 1. Sessionize the web log, Sessionize = aggregrate all page hits by visitor/IP during a session.
        *    https://en.wikipedia.org/wiki/Session_(web_analytics)
        *    - Assume session expires: 20 mins, can be determined by below histogram in question 2
        *    - Page hits: request count
        *    - Output: page hits per session, adding visitor / IP information
        */

        println(
            """
              |        * 1. Sessionize the web log, Sessionize = aggregrate all page hits by visitor/IP during a session.
              |        *    https://en.wikipedia.org/wiki/Session_(web_analytics)
              |        *    - Assume session expires: 20 mins, can be determined by below histogram in question 2
              |        *    - Page hits: request count
              |        *    - Output: page hits per session, adding visitor / IP information
              |""".stripMargin)

        val sessionizedLogsDF = logsWithIfNewSessionFlagDF.groupBy(
            CLIENT_COL,
            SESSION_ID_COL
        ).agg(countDistinct(URL_COL).as(PAGE_HITS))
        sessionizedLogsDF.orderBy(desc(PAGE_HITS), asc(CLIENT), asc(SESSION_ID)).show(20)

        /*
       * 2. Determine the average session time
       *  - Here I use overall overage, ignoring all 0 session time
       */

        println(
            """
              |       * 2. Determine the average session time
              |       *  - Here I use overall overage, ignoring all 0 session time
              |""".stripMargin)

        val sessionTimeDF = logsWithIfNewSessionFlagDF
          .groupBy(VISITOR_IP_COL, SESSION_ID_COL)
          .agg(
              ((max(TIMESTAMP_COL) - min(TIMESTAMP_COL)) / 1000 ).as(SESSION_DURATION)
          ).filter(SESSION_DURATION_COL > 0)

        sessionTimeDF.agg(concat(round(avg(SESSION_DURATION), 0), lit(" seconds"))).show(false)

        // Histogram, 1 minute as interval
        sessionTimeDF
            .withColumn(DURATION_BY_MINUTE, round(col(SESSION_DURATION)/60, 0))
          .groupBy(DURATION_BY_MINUTE).agg(count("*"))
            .orderBy(asc(DURATION_BY_MINUTE)).show(100, false)

        /*
        * 3. Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session.
        */

        println(
            """
              | 3. Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session.
              |""".stripMargin)

        val uniqueUrlPerSessionDF = logsWithIfNewSessionFlagDF
          .groupBy(VISITOR_IP_COL, SESSION_ID_COL)
          .agg(countDistinct(URL_COL).as(URL_COUNT))

        uniqueUrlPerSessionDF.orderBy(desc(URL_COUNT)).show(false)

        /*
        * 4. Find the most engaged users, ie the IPs with the longest session times
        */

        println(
            """
              | 4. Find the most engaged users, ie the IPs with the longest session times (minutes)
              |""".stripMargin)

        val mostEnagedUsersDF = logsWithIfNewSessionFlagDF
          .groupBy(VISITOR_IP_COL, SESSION_ID_COL)
          .agg(((max(TIMESTAMP_COL) - min(TIMESTAMP_COL)) / 1000 ).as(SESSION_DURATION))
          .groupBy(VISITOR_IP_COL)
          .agg((round(sum(SESSION_DURATION) / 60.0, 2)).as(TOTAL_SESSION_TIME_MINUTES))

        mostEnagedUsersDF.orderBy(desc(TOTAL_SESSION_TIME_MINUTES)).show(10, false)

        spark.close()
    }
}
