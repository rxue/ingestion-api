from pyspark.sql import SparkSession, Row

spark = SparkSession.builder \
    .config("spark.hadoop.mapreduce.input.fileinputformat.input.dir.recursive", "true") \
    .appName("DebugAuth") \
    .getOrCreate()
# Absolute path to your directory containing text files
text_dir = "file:///opt/bitnami/spark/jobs/input/"
originalRdd = spark.sparkContext.wholeTextFiles(text_dir)
def parse_email(file_pair):
    filename, content = file_pair
    fields = {
        "from": None,
        "to": None,
        "subject": None,
        "date": None
    }

    for line in content.splitlines():
        if line.startswith("From: "):
            fields["from"] = line[len("From: "):].strip()
        elif line.startswith("To: "):
            fields["to"] = line[len("To: "):].strip()
        elif line.startswith("Subject: "):
            fields["subject"] = line[len("Subject: "):].strip()
        elif line.startswith("Date: "):
            fields["date"] = line[len("Date: "):].strip()
    
    return Row(**fields)
print("Parse the original RDD to structured RDD so that it can be used to create a Data Frame")
parsed_rdd = originalRdd.map(parse_email)
print("Convert to data frame")
emails_df = spark.createDataFrame(parsed_rdd)

print("Query with Spark SQL")
emails_df.createOrReplaceTempView("emails")

result_df = spark.sql("SELECT count(*), from FROM emails GROUP BY from")
print("Got result data frame")
result_df.show(truncate=False)

# Stop the session
spark.stop()
