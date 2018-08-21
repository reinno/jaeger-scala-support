## start example
### prerequest
install docker

### start jaeger locally
``` bash
docker run -d -e \
  COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 9411:9411 \
  jaegertracing/all-in-one:latest
```

### publish client to local
```bash
sbt "project client" publishLocal
sbt "project akka-http-client" publishLocal
sbt "project spray-client" publishLocal
```

### start example A(akka-http)
```bash
sbt "project akka-http-example" run
```

### start example B(spray)
```bash
sbt "project spray-example" run
```


### test example
```bash
curl localhost:9001/users
curl localhost:9002/users
```