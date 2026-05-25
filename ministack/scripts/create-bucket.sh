#!/bin/bash

echo "===> [S3] Creando bucket en Ministack..."

BUCKET_NAME="transcription-bucket"
ENDPOINT="http://localhost:4566"

# Crear bucket
awslocal --endpoint-url=$ENDPOINT s3api create-bucket \
  --bucket $BUCKET_NAME

# Configurar CORS
echo "===> [S3] Configurando CORS..."
awslocal --endpoint-url=$ENDPOINT s3api put-bucket-cors \
  --bucket $BUCKET_NAME \
  --cors-configuration file:///etc/ministack/init/ready.d/cors.json

# Listar buckets
echo "===> [S3] Buckets:"
awslocal --endpoint-url=$ENDPOINT s3api list-buckets

echo "===> [S3] OK"