# Automatic Speech Recognition (ASR) POC with Spring Boot and AWS Transcribe

This project demonstrates a Proof of Concept (POC) for Automatic Speech Recognition (ASR) using Spring Boot and AWS Transcribe. Follow the steps below to set up and run the project locally.

## Prerequisites
Before running the project, make sure you have the following:

- Java Development Kit (JDK) 17 or higher installed
- AWS Access Key and Secret Key with appropriate permissions
- AWS S3 bucket for depositing files
- AWS region for the bucket

## Environment Variables
To run this project, you need to set the following environment variables in your `application.properties` file:

```properties
aws.awsAccessKey=YOUR_AWS_ACCESS_KEY
aws.awsSecretKey=YOUR_AWS_SECRET_KEY
aws.bucketName=BUCKET_NAME_FOR_DEPOSIT_FILES
aws.awsRegion=REGION_OF_THE_BUCKET

spring.servlet.multipart.max-file-size=FILE SIZE WITH THE CORRECT INFORMATION UNIT (KB,MB,GB)
spring.servlet.multipart.max-request-size=SAME AS spring.servlet.multipart.max-file-size property
```


# Step 1: Clone the Project Repository
1. Open a terminal or command prompt.
2. Change to the directory where you want to clone the project.
3. Run the following command to clone the project repository:

```bash
git clone https://github.com/panchoarc/aws-transcribe-recognition-poc.git
```

# Step 2: Build the Project
1. Navigate to the project directory:
```bash
cd aws-transcribe-recognition-poc
```

2. Build the project using Maven:
```bash
mvn clean install
```

This command will download all the required dependencies, compile the source code, and package the project into a JAR file.

# Step 3: Run the Project

1. After the build is successful, you can run the Spring Boot project using the following command:

```bash
java -jar <path_to_jar_file>
```

Replace <path_to_jar_file> with the path to the generated JAR file, usually located in the target directory.

## Building and Running the Docker Image
Follow these steps to build and run the Docker image:

1. Ensure Docker is installed and running on your system.
2. Open a terminal or command prompt.
3. Navigate to the project directory where the Dockerfile is located.
4. Build the Docker image using the following command:

```bash
docker build -t <image_name> .
```

Replace <image_name> with a suitable name for your Docker image.

5. Once the image is built, you can run the Docker container with the following command:

```bash
docker run -d -p <host_port>:<container_port> <image_name> --name <container_name>
```

Replace <host_port> with the port number you want to expose on your host machine, <container_port> with the port number your Spring Boot application listens on and <container_name> for the name of the container for better identification.

Now you should have a running Spring Boot project inside a Docker container.

# Swagger Documentation

This project includes Swagger documentation to help you explore and test the API endpoints. Follow the steps below to access the Swagger UI:

1. Start the project by following the instructions mentioned earlier in this README.

2. Once the project is running, open a web browser and navigate to the following URL:

```bash
http://localhost:<port_number>/swagger-ui.html
```

Replace `<port_number>` with the port number on which your Spring Boot application is running.

3. The Swagger UI page will open, showing a list of available API endpoints and their details. You can use this interface to interact with the APIs, view request/response examples, and test different endpoints.

4. Explore the available endpoints, their request/response structures, and any additional information provided in the Swagger documentation.



