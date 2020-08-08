package paypay.challenge.main

import java.text.SimpleDateFormat

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

import scala.util.Try

object Utility {

    val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    val toEpochTimeUdf: UserDefinedFunction = udf((t: String) => {
        val format = new SimpleDateFormat(TIME_FORMAT)
        Try(format.parse(t)).map(x => x.getTime).getOrElse(-1L)
    })

    val getUrlFromHttpRequestUdf: UserDefinedFunction = udf((httpRequest: String) => {
        httpRequest.split(" ")(1)
    })
}
