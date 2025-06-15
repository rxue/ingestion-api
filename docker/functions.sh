downloadDataSet() {
  echo "Download dataset"
  local downloadAddress="https://www.cs.cmu.edu/~enron/enron_mail_20150507.tar.gz"
  originalFileName=$(basename ${downloadAddress})
  if [ ! -f ${originalFileName} ]; then curl -O ${downloadAddress}; fi
  echo "Extract to the Docker volume for the Spark to ingest"
  tar -xzf $originalFileName -C ~/dockervolume/spark/input/
}
