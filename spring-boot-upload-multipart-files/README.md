## File Upload Service

## Overview
Service to Upload files, save the metadata of every file in metadata.csv file, list all uploaded files and avoid uploading duplicate files.
   
## File Location: Metadata and uploaded files are stored inside the 'uploads' directory <br>(inside the spring-boot project directory.)
1. Metadata.csv contains the metadata
2. All uploaded files are contained in the same directory

 
## List of APIs and their endpoints
Three API this service, as implemented in FilesController Class
1. Upload File UI Screen (AngularJS) <http://localhost:8081>
2. Upload File - Service API URL <http://localhost:8081/upload>
3. Get List of Files Uploaded - Service API URL <http://localhost:8081/files>
4. Download a specific filep - Service API URL <http://localhost:8081/files/FILENAME>


## Testing (Junit+Mockito)
1. Declares a dummyfile.txt and performs an upload testing to ascertain a success response.
2. To run the test case navigate to the src/test/java and run as 'junit test'.


## Instructions to run the application
1. Initiate the UI service inside the 'angular-8-upload-file' directory by using the 'ng serve --port 8081' command in terminal or command prompt
2. Start the Spring boot application inisde the 'spring-boot-upload-multipart-files' by using the 'mvn spring-boot:run' command in terminal or command prompt

## Logging (log4j)(inside the spring-boot project directory.)
## (All URLS and properties can be changed in application.properties file)
