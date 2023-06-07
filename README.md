
# ARS (automatic speech recognition) POC with Spring boot and AWS Transcribe



## Environment Variables

To run this project, you will need to add the following environment variables to your application.properties file

`aws.awsAccessKey=YOUR AWS ACCESS KEY`

`aws.awsSecretKey=YOUR AWS SECRET KEY`

`aws.bucketName=BUCKET NAME FOR DEPOSIT FILES`

`aws.awsRegion=REGION OF THE BUCKET`

## Run Locally

Clone the project

```bash
  git clone https://github.com/panchoarc/aws-transcribe-recognition-poc.git
```

Go to the project directory

```bash
  cd aws-transcribe-recognition-poc
```
Start the server

```bash
  ./mvnw clean spring-boot:run
```

