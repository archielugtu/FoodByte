# SENG202-TeamSix
FoodByte is a popup point of food sale application suitable for food trucks or other similar platforms. It was created by a group of seven students from the University of Canterbury as part of the SENG202 course.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for personal use. The application will initially load with no data but an example data set can be found in the test_data folder in the example_data.xml file. This can be imported using the import feature of the management screen to get a feel for the aplication. To delete a data set the data.xml file must be deleted which will reset the aplication to its original blank state.

#### Prerequisites
- Java 11 https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html
- Maven 3.6 or beyond https://maven.apache.org/download.cgi

#### Directory navigation
Open command prompt (windows) or terminal (Mac/Linux) and navigate to the directory where the files were unzipped to.
For example, if the files were unzipped to Desktop/FoodByte, to navigate

#### How to Build FoodByte
To build FoodByte, ensure you are in the correct directory (see above) and run the following command:
<pre><code>mvn package</code>
<code>cp target/FoodByte-1.0-SNAPSHOT.jar FoodByte.jar</code></pre>

#### Run FoodByte
To run the project, ensure you are in the correct directory (see above) and run the following command:
<pre><code>java -jar FoodByte.jar</code></pre>

#### Run Tests
Make sure you are in the correct directory (see above) and run the following command:
<pre><code>mvn test</code></pre>

#### Built With
- IntelliJ IDEA Ultimate (Java IDE)
- Maven (Dependency Management)

#### Versioning
We used GitLab (University of Canterbury) for version control.

## Authors
- Andrew Clifford
- Connor MacDonald
- Taran Jennison
- Anzac Morel
- Rchi Lugtu
- Hamesh Ravji
- George Stephenson