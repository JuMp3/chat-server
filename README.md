# Socket Chat

The test project is developed in java 8. It exposes a server chat listening on 10000 port, using Socket TCP. So, you can use whatever client for connect and send messages.
Any message will be broadcast to all clients connected to the server.

You can use the bash, shell, cmd of OS, or the Client included in the project, as you can view in Test class (ChatServerTest)


### Installation

The app:
  - requires Java v8 and Maven  v3+ to run.
  - is exposed to port 10000

### Local deploy

If you want test the app in localhost, you can:
  1. use the Main class with command, after produced jar (mvn clean install):
    ```
    java -jar chat-server.jar
    ```  
  2. use the docker file on DockerHub: 
    ```
    docker pull jump3/chat-socket-server
    ```
  3. use the local Dockerfile for build your docker image e you can run a new container (copy the compile jar and Dockerfile in the same folder before run this command):
    ```
    docker run --name chat-server -d -p 10000:10000 chat-server
    ```