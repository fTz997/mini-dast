## To be developed   

- [x] Consult scans in progress

- [ ] Persist into MySQL, the results of each scan.
      
- [x] Retrieve data from all scans finished 

- [ ]  Access the "details" of a particular scan using their id as a parameter

- [ ] (?)

## Application Usage Documentation

### Deployment

1. Ensure that the application code is properly configured and ready for deployment.
2. Deploy the application to your preferred server or hosting environment. This could be a cloud platform, a dedicated server, or any other suitable environment.
3. Make sure the necessary dependencies and configurations are set up correctly for the application to run smoothly.
4. Start the application server to begin accepting requests.

### Installation

1. Clone the repository containing the application code to your local machine or server.
2. Ensure that you have Java and Maven installed on your system.
3. Navigate to the root directory of the application in your terminal or command prompt.
4. Run the following command to build the application:

   ```
   mvn clean install
   ```

5. Once the build is successful, you can run the application using the following command:

   ```
   java -jar target/<application-name>.jar
   ```

   Replace `<application-name>` with the name of your application JAR file.

6. The application should now be up and running, ready to accept requests.

---

### Usage

#### Starting a Scan

1. To start a scan, send a POST request to the `/scan` endpoint of the application.
2. The request body should contain a JSON object with a key `urls` and a list of URLs as its value. For example:

   ```json
   {
     "urls": ["https://example.com", "https://example2.com"]
   }
   ```

3. The application will initiate a scan for each URL provided using the ZAP Proxy.
4. Once the scan is complete, the application will return a response containing the scan results for each URL.

#### Viewing Scan Results

1. Upon receiving the response from the `/scan/{scan-id}` endpoint, you can view the scan results for each URL.
2. The scan results will include information such as vulnerabilities detected, scan duration, and any other relevant details.
3. You can use these scan results to assess the security posture of the scanned URLs and take appropriate actions to mitigate any identified risks.

---


### Zaproxy and testing envronment deployment 

1. **Building and Running**: To deploy your application using Docker Compose, navigate to the directory containing your `docker-compose.yml` file in your terminal or command prompt and run the following command:

   ```
   docker-compose up --build
   ```

   This command will build the Docker images for your application and the ZAP Proxy, create the necessary containers, and start them.

2. **Accessing the Application**: Once the containers are up and running, you can access your application at `http://localhost:8080`.

3. **Stopping the Application**: To stop the application and shut down the containers, press `Ctrl + C` in the terminal where Docker Compose is running. Alternatively, you can run the following command:

   ```
   docker-compose down
   ```

   This will stop and remove the containers created by Docker Compose.
