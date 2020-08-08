# DataEngineerChallenge

This is an interview challenge for PayPay. Please feel free to fork. Pull Requests will be ignored.

The challenge is to make make analytical observations about the data using the distributed tools below.

## Processing & Analytical goals:

### 1.  Sessionize 
Sessionize the web log by IP. Sessionize = aggregrate all page hits by visitor/IP during a session.
    https://en.wikipedia.org/wiki/Session_(web_analytics)

Based on histogram of the session durations, 20 minutes is a better selection for expiration than 15 minutes.
We can see majority durations are under 20 minutes.
Page Hits are counted by the distinct request per session.

It's possible one page hit can have multiple requests. 
Here I consider each http request represents an interaction between user/client and server.

The session_id is unique within each visitor/IP scope.

From the table we can see the session on top has 411 requests sent to server.

|client|session_id|PageHits|
|---   |---|--------|
|  106.51.132.54:5048|         1|     411|
|  106.51.132.54:5049|         1|     322|
|  106.51.132.54:4508|         1|     292|
|  106.51.132.54:5037|         1|     272|
|  106.51.132.54:4974|         1|     250|
|   78.46.60.71:58504|         1|     237|
|213.239.204.204:3...|         1|     234|
|  106.51.132.54:4841|         1|     227|
|  106.51.132.54:5035|         1|     186|
| 88.198.69.103:47828|         1|     156|
|188.40.135.194:59090|         1|     153|
|  106.51.132.54:4489|         1|     145|
| 66.249.71.110:41229|         1|     126|
| 141.8.143.205:42162|         1|     108|
| 66.249.71.118:51970|         1|     100|
| 66.249.71.126:39460|         1|      97|
|  176.9.11.165:33818|         1|      94|
|213.239.204.204:3...|         1|      94|
|     46.4.19.5:59758|         1|      92|
|    46.4.27.23:50889|         1|      90|


### (2) Determine the average session time

Here I use overall average, ignoring all data points with 0 session time.
Result: 565.0 seconds, or 9.4 minutes

Also attach the distribution of session duration:

(20 minutes is a better cut-off than 15 minutes as session expiration)

|DurationTimeByMinute|Count|
|---   |---   |
|0.0                 |2896    |
|1.0                 |6212    |
|2.0                 |7290    |
|3.0                 |8602    |
|4.0                 |8613    |
|5.0                 |8148    |
|6.0                 |8424    |
|7.0                 |8998    |
|8.0                 |9276    |
|9.0                 |10278   |
|10.0                |10179   |
|11.0                |10935   |
|12.0                |11815   |
|13.0                |13284   |
|14.0                |14767   |
|15.0                |14835   |
|16.0                |9230    |
|17.0                |2075    |
|18.0                |628     |
|19.0                |303     |
|20.0                |105     |
|21.0                |20      |
|22.0                |6       |
|23.0                |10      |
|24.0                |5       |
|25.0                |10      |
|26.0                |5       |
|27.0                |3       |
|28.0                |8       |
|29.0                |4       |
|30.0                |2       |


### 3. Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session.

The session is identified by the combinination of visotor/ip and session_id within this scope.
Url count is using distinct calculation, and the top counts per session are:


|per_visitor_ip                                                                              |session_id|url_count|
|---   |---   |--- |
|106.51.132.54:5048-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |408      |
|106.51.132.54:5049-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |322      |
|106.51.132.54:4508-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |292      |
|106.51.132.54:5037-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |272      |
|106.51.132.54:4974-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |250      |
|78.46.60.71:58504-Chrome/16.0.912.41 (2.6.0.1, ruby 2.1.2 (2014-05-08))                     |1         |237      |
|213.239.204.204:35094-Mozilla/5.0 (X11; Linux i686) (2.5.3.3, ruby 2.1.2 (2014-05-08))      |1         |234      |
|106.51.132.54:4841-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |227      |
|106.51.132.54:5035-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |185      |
|88.198.69.103:47828-Mozilla/5.0 (X11; Linux i686) (2.5.3.3, ruby 2.1.2 (2014-05-08))        |1         |156      |
|188.40.135.194:59090-Chrome/16.0.912.41 (2.5.3.3, ruby 2.1.2 (2014-05-08))                  |1         |153      |
|106.51.132.54:4489-Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)   |1         |145      |
|141.8.143.205:42162-Mozilla/5.0 (compatible; YandexBot/3.0; +http://yandex.com/bots)        |1         |108      |
|66.249.71.110:41229-Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)|1         |102      |
|176.9.11.165:33818-Chrome/16.0.912.41 (2.6.0.1, ruby 2.1.2 (2014-05-08))                    |1         |94       |
|213.239.204.204:37978-Chrome/16.0.912.41 (2.5.3.3, ruby 2.1.2 (2014-05-08))                 |1         |94       |
|46.4.19.5:59758-Mozilla/5.0 (X11; Linux i686) (2.6.0.1, ruby 2.1.2 (2014-05-08))            |1         |92       |
|46.4.27.23:50889-Mozilla/5.0 (X11; Linux i686) (2.5.3.3, ruby 2.1.2 (2014-05-08))           |1         |90       |
|46.4.28.81:38316-Mozilla/5.0 (X11; Linux i686) (2.6.0.1, ruby 2.1.2 (2014-05-08))           |1         |90       |
|188.40.94.195:46918-Safari/535.7 (2.5.3.3, ruby 2.1.2 (2014-05-08))                         |1         |89       |

### 4. Find the most engaged users, ie the IPs with the longest session times

These are most enaged users, and the top 1 had around 123 minutes.
Here the data doesn't have how long the last session lasted, 
so I use the start time of last session as the end of all the session for the user.
So it actually under-estimates the true enagement.

|per_visitor_ip                                                                                                                    |total_session_time_by_minute|
|---|---|
|54.169.191.85:15462--                                                                                                             |122.93                      |
|203.191.34.178:10400-Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36|106.6                       |
|54.169.191.85:3328--                                                                                                              |95.87                       |
|88.198.69.103:47828-Mozilla/5.0 (X11; Linux i686) (2.5.3.3, ruby 2.1.2 (2014-05-08))                                              |58.1                        |
|14.96.205.187:1025-Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36  |55.09                       |
|103.29.159.138:57045-Mozilla/5.0 (Windows NT 6.1; rv:21.0) Gecko/20100101 Firefox/21.0                                            |50.12                       |
|122.169.141.4:11486-Mozilla/5.0 (Windows NT 6.3; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0                                      |50.02                       |
|122.169.141.4:50427-Mozilla/5.0 (Windows NT 6.3; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0                                      |49.75                       |
|103.29.159.186:27174-Mozilla/5.0 (Windows NT 6.1; rv:25.0) Gecko/20100101 Firefox/25.0                                            |49.75                       |
|122.169.141.4:59653-Mozilla/5.0 (Windows NT 6.3; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0                                      |49.63                       |

## Summary

It's interesting project with log exploratory data analysis on the customer behaviors.

Some challenges I had:
- Define the right interval for session expiration
- Error logs, with incomplete or mis-formated Urls, mainly came with 5xx/4xx errors
- Use the right fields to determine a session, while filter out the unused ones

To better understand how customer interacted with the service. We need:
- If there's a better way to identify a customer, such as cookie / membership ID
- Determine if we need to count 4xx or 5xx as a kind of interactions
- Also pay attention on some robot agents, like web scraper I noticed in the logs.

