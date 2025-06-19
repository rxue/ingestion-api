# How to run locally (in MacOS with Docker installed)
1. after downloading project, `cd` to the `docker` directory
2. `chmod +x run.sh` so that it is executed as a process
3. execute `run.sh` with command `./run.sh`

* start request: `curl -X POST http://localhost:8080/start -H "Content-Type: text/plain" -d "https://www.cs.cmu.edu/~enron/enron_mail_20150507.tar.gz"`
* request to check status: `curl -s -i -w "\nStatus Code: %{http_code}\n" http://localhost:8080/status`
* request to check top senders: `curl -s -i -w "\nStatus Code: %{http_code}\n" http://localhost:8080/top-senders`

`ingestion-manager` is the service consuming the jms message sent by the REST API - `async-api-endpoint`. The log of `ingestion-manager` can be monitored with command `docker logs -f ingestion-manager`

# Daybook
## Practical lessons learnt
### 20250614
#### Docker image `apache/spark` can not keep alive without extra configuration after startup => use `bitnami/spark` instead
#### Docker image of `bitnami/spark:3.5` has authentication problem
Authentication problem of `bitnami/spark:3.5` causes Spark job not be able to read data from input file, so use `3.4` instead

#### `docker exec -it spark-master spark-submit --master spark://spark-master:7077 /opt/bitnami/spark/jobs/local_test.py`
#### When a *Quarkus* project is generated, a docker directory, `src/main/docker` is automatically generated
there are several `Dockerfile`, which can be used out of the box without any modification

#### Quarkus project can be built into *native executable*
Resource: https://quarkus.io/guides/building-native-image

### 20250615
#### A flaw of `mvn package` without `clean`
When running `mvn package` without `clean`, changes in the `src/main/resources/application.properties` cannot come to the newly generated jar or war

### 20250617
#### `java.lang.reflect.Proxy.newProxyInstance` can only work with class with `interface`
Example code where `HttpFileDownloader implements IHttpFileDownloader`

```
IHttpFileDownloader httpFileDownloader = (IHttpFileDownloader) Proxy.newProxyInstance(
  IHttpFileDownloader.class.getClassLoader(),
  new Class[]{IHttpFileDownloader.class},
  new StateLogger(new HttpFileDownloader()));
```

### 20250618
#### It might not be a good idea to mix use of CDI beans or Spring beans in mix with normal business objects initialized with constructor
#### It is a bad idea to pass a dependency from one class to its instance variable merely because that instance need the dependency but not the current object
#### When using JakartaEE interceptor, if the self-defined annotation is used in one class on the class level, i.e. the `TYPE` of the `@Target`, interception of each method on the annotated class will be in a separate thread! and this will affect the use of explicit transaction boundary in `quarkus-narayana-jta`

### 20250619
#### Postgresql: double-quotation is not allowed in inserted value in the `insert` statement
#### JakartaEE : Don't mix use `Connection` retrieved from an `EntityManager` with that EntityManager
#### `quarkus` : Any exception thrown by the interceptor class' intercepting method annotated with `@AroundInvoke` is swallowed silently and this would cause the whole application crashed
