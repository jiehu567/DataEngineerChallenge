package challenge.main
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.col

object SchemaConfig {

    // Field Names
    val TIMESTAMP = "timestamp"
    val LAG_TIMESTAMP = "lag_timestamp"
    val CLIENT = "client"
    val CLIENT_IP = "client_ip"
    val CLIENT_PORT = "client_port"
    val TARGET = "target"
    val TARGET_IP = "target_ip"
    val TARGET_PORT = "target_port"
    val WEB_AGENT = "web_agent"
    val STATUS_CODE = "status_code"
    val REQUEST_URL = "request_url"
    val SESSION_ID = "session_id"
    val VISITOR_IP = "per_visitor_ip"
    val SESSION_DURATION = "session_duration"
    val URL = "url"
    val IS_NEW_SESSION = "is_new_session"
    val TIME_DIFF = "time_diff"
    val PAGE_HITS = "PageHits"
    val URL_COUNT = "url_count"
    val TOTAL_SESSION_TIME_MINUTES = "total_session_time_by_minute"
    val DURATION_BY_MINUTE = "DurationTimeByMinute"

    // Mapping Columns in DataFrame
    val CLIENT_COL: Column = col(CLIENT)
    val TARGET_COL: Column = col(TARGET)
    val TIMESTAMP_COL: Column = col(TIMESTAMP)
    val LAG_TIMESTAMP_COL: Column = col(LAG_TIMESTAMP)
    val CLIENT_IP_COL: Column = col(CLIENT_IP)
    val CLIENT_PORT_COL: Column = col(CLIENT_PORT)
    val TARGET_IP_COL: Column = col(TARGET_IP)
    val TARGET_PORT_COL: Column = col(TARGET_PORT)
    val WEB_AGENT_COL: Column = col(WEB_AGENT)
    val STATUS_CODE_COL: Column = col(STATUS_CODE)
    val REQUEST_URL_COL: Column = col(REQUEST_URL)
    val SESSION_ID_COL: Column = col(SESSION_ID)
    val SESSION_DURATION_COL: Column = col(SESSION_DURATION)
    val VISITOR_IP_COL: Column = col(VISITOR_IP)
    val URL_COL: Column = col(URL)
    val IS_NEW_SESSION_COL: Column = col(IS_NEW_SESSION)
    val TIME_DIFF_COL: Column = col(TIME_DIFF)
    val PAGE_HITS_COL: Column = col(PAGE_HITS)
    val URL_COUNT_COL: Column = col(URL_COUNT)

    val _C0_TIMESTAMP = "_c0"     // timestamp
    val _C2_CLIENT = "_c2"     // client
    val _C3_TARGET = "_c3"     // target
    val _C7_STATUS_CODE = "_c7"     // status
    val _C11_HTTP_REQUEST = "_c11"    // http request
    val _C12_WEB_AGENT = "_c12"    // web agent

    val _C0_TIMESTAMP_COL: Column = col(_C0_TIMESTAMP)
    val _C7_STATUS_CODE_COL: Column = col(_C7_STATUS_CODE)
    val _C11_HTTP_REQUEST_COL: Column = col(_C11_HTTP_REQUEST)
}
