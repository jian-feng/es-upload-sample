# Upload documents to ElasticSearch

This sample demostrate how to use camel 3.x and camel-elasticsearch on Spring Boot.

- [1. How to run](#1-how-to-run)
- [2. Use REST API to upload](#2-use-rest-api-to-upload)
- [3. Copy CSV File to upload](#3-copy-csv-file-to-upload)


Each line in CSV file is uploaded to ES as a single document.

For example:
Input CSV File (sample.csv)
```csv
@timestamp,app,somedata
2023-04-01T12:12:07.427+09:00,dbinsert,1111111
2023-04-01T13:12:07.427+09:00,dbinsert,1111112
```

Documents uploaded in ElasticSearch:
```json
{
  "_index": "sample",
  "_id": "-u1aaYcBcHx6rumj4mBy",
  "_version": 1,
  "_score": null,
  "fields": {
    "app": [
      "dbinsert"
    ],
    "@timestamp": [
      "2023-04-01T04:12:07.427Z"
    ],
    "somedata": [
      "1111112"
    ],
    "app.keyword": [
      "dbinsert"
    ],
    "somedata.keyword": [
      "1111112"
    ]
  },
  "sort": [
    1680322327427
  ]
}
```

```json
{
  "_index": "sample",
  "_id": "-e1aaYcBcHx6rumj4mBy",
  "_version": 1,
  "_score": null,
  "fields": {
    "app": [
      "dbinsert"
    ],
    "@timestamp": [
      "2023-04-01T03:12:07.427Z"
    ],
    "somedata": [
      "1111111"
    ],
    "app.keyword": [
      "dbinsert"
    ],
    "somedata.keyword": [
      "1111111"
    ]
  },
  "sort": [
    1680318727427
  ]
}
```

## 1. How to run

Setup your ElasticSearch connection in [application.properties](src/main/java/com/sample/Application.java) as follwoing.
```properties
# Spring Boot will auto configure an Elasticsearch RestClient that will be used by camel, 
# it is possible to customize the client with the following basic properties:
camel.component.elasticsearch.host-addresses=127.0.0.1:9200

# If ElasticSearch is configued with xpack.security.enabled=true
# the following properties must be setup correctly.
# camel.component.elasticsearch.user=elastic
# camel.component.elasticsearch.password=xxx
# camel.component.elasticsearch.enable-ssl=true
# camel.component.elasticsearch.certificate-path=ca.crt
```

Then, start Camel.
```sh
mvn clean spring-boot:run
```

## 2. Use REST API to upload

```sh
curl http://localhost:8080/api/upload --data-binary @sample.csv -H "indexName: sample" -H "content-type: text/html"
```

## 3. Copy CSV File to upload

Copy you csv file to `upload` directory.
Camel will use file name as indexName of ElasticSearch.
While Camel upload csv successfully, the original csv will be move to `upload/done`.

