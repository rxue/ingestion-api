from pyspark.sql import SparkSession

spark = SparkSession.builder \
    .master("local[*]") \
    .appName("DebugAuth") \
    .getOrCreate()
# Absolute path to your directory containing text files
text_dir = "file:///opt/bitnami/spark/jobs/test.csv"

#df = spark.read.text(text_dir)


# Example: Simple word count
#words = text_rdd.flatMap(lambda line: line.split())
#word_counts = words.map(lambda word: (word, 1)).reduceByKey(lambda a, b: a + b)

# Show top 20 words
#for word, count in word_counts.take(20):
#    print(f"{word}: {count}")


# Read all text files in the directory as one RDD

rdd = spark.sparkContext.textFile(text_dir)
print("Line count:", rdd.count())

# Stop the session
spark.stop()
