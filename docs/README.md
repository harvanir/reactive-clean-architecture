# JDBC vs R2DBC Performance Comparison

Project comparison: [clean-architecture](https://github.com/harvanir/clean-architecture)

## Test context
- Warm up
- Concurrency: 200
- Duration: 2 mins
- Java options:
  - -server
  - -Xmx256
- Test operation: invoke API resource [POST]<code>/v1/items</code>
- Test method: perform test individually.

## Througput & Latency
![](jdbc-vs-r2dbc-aggregate.png)

## Java Heap Space
### JDBC
![](jdbc.png)

### R2DBC
![](r2dbc.png)