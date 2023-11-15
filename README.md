# Rabobank Customer Statement Processor

Rabobank Customer Statement Processor application evaluates a customer statement records file for validity and reports invalid records. 
For evaluation of the records, correctness of end balance values and uniqueness of reference ids are checked.


Run the below command to start the application.

```sh
java -jar customer-statement-processor<version>.jar <customer_statement_records_file>
```
The 'customer_statement_records_file' parameter is the path to the customer statement records file. 
The file name has to have '.csv' or '.xml' extension.

## Unit and Integration Tests
The integration and unit tests can be run separately. Use below commands from the project root directory to run the tests.

To run all tests:
```sh
mvn test 
```
To run only the unit tests:
```sh
mvn test -Dtest='**/*Test'
```
To run only the integration tests:
```sh
mvn test -Dtest='**/*IT'
```

## Design Criteria

* _System resources shall be secured:_ A security mechanism is added to protect the system resources. So only authorized 
  user can use the APIs. Spring security 6.x is used to develop the JWT based security mechanism.   

* _New file formats shall be introduced easily:_ The input files can be in two different formats and new format 
  types can be introduced in the future. So, factory method design pattern is used to create and use the correct 
  file parser during the process run.

* _Very big customer statement files shall be supported:_ Monthly customer statement records of Rabobank could be too big.
  Reading all the records in the input file to the memory and run the validity check could limit the maximum 
  size of input files the application can process. So, it is preferred to read and evaluate the records in the input files 
  in a streamed way. Reading the records in '.csv' files line by line in a streamed way is achieved using FileReader 
  class of Java. To achieve this behaviour in '.xml' files, StAX XML parser is used to parse and read records. 
  Since, DOM parser reads all the file to the memory at once and SAX parser doesn't allow reading each record and 
  processing it, they are not used for XML parsing. This approach allows the application to be able to process 
  much bigger customer statement files.

* _Asynchronous processing of customer statement records:_ The APIs are design to process the customer statement 
  records asynchronously. This is needed to prevent clients from having timeout issues for big data files. 
  The client is expected to get customer statement records data processed via two 2 APIs.
  * _Starting a process (POST /api/customer-statement):_ When a client requests processing of customer statement records data,
    the corresponding API responds with a success message having a UUID reference of the newly started process. 
  * _Getting processing result (GET /api/customer-statement/{uuid}):_ The client queries the result of the started
    customer statement records data processing using this API. The uuid value in the URI parameter is obtained from the response body 
    of the  previous call. If the result of the process is not ready yet, the API responds with Http status code 202.
    If the process is finished and the result is successful, the API responds with status code 200, if an error occurs because of invalid data,  
    the API responds with the status code of 400 (Bad request).  


* _An asynchronous process to delete timed out results:_ The application does not provide the client with an API to delete results.
  Because the results might be needed for a while by the client. The application provides an API to delete
  the timed out processing results (DELETE /api/customer-statement). To delete timed out results, a cron job shall 
  be created in the container orchestration system, and it shall call this API.   

## Sample API Calls Using curl

Initially, a POST call to retrieve a token should be made. The token in the response body shall be used in other API calls.

```sh
curl -v -X POST  http://localhost:8080/api/token -u "user1:password1"
```

The below command starts a new customer statement records data processing. {JWT_TOKEN} must be of a user with SCOPE_PROCESS
authorization.
```sh
curl -v 'http://localhost:8080/api/customer-statement' -F file='@records.xml' -H 'Authorization: Bearer {JWT_TOKEN}'	
```

To get the result of a statment records processing:
```sh
curl -v 'http://localhost:8080/api/customer-statement/{uuid}' -H 'Authorization: Bearer {JWT_TOKEN}'
```
