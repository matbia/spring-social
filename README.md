# Spring Social
Spring Social is a demo application that makes use of various features of Spring Framework (MVC, Data, Security) to implement more complex functionalities.

## Features
 - Full-fledged authentication system with email user confirmation, account recovery and bruteforce attack protection
 - Feed with support for images and videos (MP4 and embedded YouTube) and infinite scrolling
 - Customizable user profiles
 - Live chat

## Usage
Open the *application.properties* file and edit it accordingly by providing required properties, like database and mail configuration.
Using a database other than PostgreSQL requires changing the JDBC driver and further properties tweaks. 
Once the proper configuration is in place, just compile and run. By default Tomcat will start on port 8080.

Can also be packaged into a JAR using Maven:
```
mvn clean package
```
And run like:
```
java -jar spring-social-1.0.jar
```
Default admin account credentials are: **admin@example:12345678**

## License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)

