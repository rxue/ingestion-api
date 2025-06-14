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
