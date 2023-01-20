##General purpose
This project is energy-storing system that also validates data that has been sent to it. 
Main entities of that project:
- Meter: is a device that is measuring the amount of gas or electricity being used within a house.
It's just a counter, so it will be a number that is increasing along time.
- Meter reading: It's the number that the Meter is showing at a specific date andtime.
- Consumption: It's the difference, between two given date/times, of meter readings. Example: If
the meter reading on 2022/01/15 is 120 and the meter reading on 2022/02/15 is 150, then the
consumption between 2022/01/15 and 2022/02/15 is 30. Could be KWh, m3, but the unit of
measure is not relevant for this exercise.
- Profile: It's a collection of ratios [0..1], called Fractions, for every calendar month. It represents
how the consumption of a given year is distributed among months, so all the fractions for the 12
months must sum up 1. For example, for a house in the Netherlands it would be normal to have
higher ratios during winter than during summer, given that the energy consumed will be higher
because of heating.
##Technical description
This project is written with java 17.
This project built using maven. You can download and install it here https://maven.apache.org/
Project based on spring boot and spring ecosystem.
Modules that were used:
* Spring core
* Spring data
* Spring boot
* Spring web
<!-- -->
In root project directory you can find docker-compose file if you using docker.
You can create db by installing docker desktop and executing ***docker-compose up*** in the root directory.
<!-- -->
For parsing csv files used ***OpenCSV*** library.
To reduce boilerplate code used ***Lombok***.
Database migration organized with ***Liquibase***.
This project also use ***Swagger*** that based on ***OpenApi***. It provides self documented api and swagger-ui to 
make it easier to send request in a specific format. 
<!-- -->
+ Swagger-ui url: {host:port}/swagger-ui/index.html#
+ By default: http://localhost:8080/swagger-ui/index.html#
* Liquibase migrations: {root}/src/main/resources/db/migration
##Details
###Fractions upload
This is the format of csv file that is consumed by 
LegacyUploadController.uploadFractions
or host:port/v1/fractions
- Month,Profile,Fraction
- JAN,A,0.2
- JAN,B,0.18
- FEB,A,0.1
- FEB,B,0.21
- ...
###Meter measurements upload
This is the format of csv file that is consumed by
LegacyUploadController.uploadMeasurements method
or host:port/v1/measurements
* MeterID,Profile,Month,Meter reading
* 0001,A,JAN,10
* 0004,B,JAN,8
* 0004,B,FEB,10
* 0001,A,FEB,12
* 0001,A,MAR,18
* ...

##Further improvements

To achieve better format of loading in my opinion there is two ways:
* First if the data is received like batch fraction and mesurements at the same 
  file/request/message we can merge it into one file and verify 
  and parse fractions within measurements. It will simplify database structure and logic.
* Second if data is recieved incrementally we can provide endpoint to upload it via rest or via message queue
and use json format for it. It much easier to work with json rather then excel files. And also we can store
  messages and implement event sourcing by it. Also if messages or rest requests contain only one profile data
  per message/request it is easier to work with. 
