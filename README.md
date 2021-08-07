# Automated valet car parking system

This is a shell application that manage parking space for vehicles.

## Prerequisite
 - Install Java 11 
    - Ubuntu (16.04)
        ```shell
        curl -O https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz
        tar zxvf openjdk-11.0.2_linux-x64_bin.tar.gz
        sudo mv jdk-11* /usr/local/
        ```
        - Set the environment variable
        ```shell
        sudo vim /etc/profile.d/jdk.sh
        ```
        - Add
            ```
            export JAVA_HOME=/usr/local/jdk-11.0.2
            export PATH=$PATH:$JAVA_HOME/bin
            ```
            ```shell
            source /etc/profile
            ```
 - Clone this repository
   ```shell
   git clone https://github.com/ranierdeleon/car-park-application.git
   ```
 - Build the application
   ```shell
   cd car-park-application/
   ./gradlew build
   ```
 - Run **test**
   ```shell
   ./gradlew test
   ```

## Starting **the** application
 - Start the application by running the command below:
   ```shell
   java -jar build/libs/parking-0.0.1-SNAPSHOT.jar
   ```
 - To run a test scenario
   ```shell
   script --file scripts/test-data.sh
   ```
 - Please use the command `help` to get the list of available [commands](#commands)

## Commands

Command | Description
--- | --- 
enter | Create a ticket for the supplied vehicle number
exit-parking | Exit vehicle to parking
vehicle-history | List all ticket of vehicle
revenue | Calculates entire revenue of the car park
add-parking-lot | Add parking lot space
view-parking-lot | View parking lot availability summary
help | Display list of available command
clear | Clear the shell screen
exit, quit | Exit the shell
history | Display or save the history of previously run commands
script | Read and execute commands from a file.
stacktrace | Display the full stacktrace of the last error.

## Assumptions:
 - There are two types of vehicles (Car and Motorcycle)
 - All the lots in the parking space are distinctly numbered: CarLot1, CarLot2...., MotorcycleLot1, MotorcycleLot2...,
 - Each vehicle upon entering is alloted to the lot with the lowest number
 - When vehicle exit the car park the system will return the parking lot that the vehicle will be removed from and charge them an appropriate parking fee
 - Parking fee: $1/hour for motorcycle and $2/hour for car
 - Parking fee is rounded up to the nearest hour (e.g. 1hr 1min is charged as 2hr)
 - Returning vehicle don't need to input type
