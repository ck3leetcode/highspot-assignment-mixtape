# Highspot Assignment Mixtape

## What is highspot-assignment-mixtape
“Highspot-assignment-mixtape” is a command-line batch application that applies a batch of changes to an input mixtape file and output a new mixtape file with the changes.

## Prerequisite
*   Java >= 1.8
*   Gradle = 5.5.1
*   macOS

## Installation
In user’s terminal and download the project
```
git clone https://github.com/ck3leetcode/highspot-assignment-mixtape.git
```

## Build
Change to project directory and run the commands below:
```
./gradlew clean
./gradlew jar
```

## Usage
gradle
```
./gradlew run --args="--help"
```
Java
```
java -jar build/libs/assignment-mixtape-1.0-SNAPSHOT.jar --help
```
Console output
```
Usage: mixtape-exec [-hV] [--changes-file=<changesFile>]
                    [--input-file=<inputFile>] [--output-file=<outputFile>]
                    [COMMAND]
A command-line batch application that apply changes to a given mixtape json file
      --changes-file=<changesFile>
                  filepath of a json file contains changes for the input json
                    file.
  -h, --help      Show this help message and exit.
      --input-file=<inputFile>
                  filepath of a json file contains users, songs and playlist.
      --output-file=<outputFile>
                  filepath for the output file after changes applied.
  -V, --version   Print version information and exit.

Commands:

The most commonly used git commands are:
  help  Displays help information about the specified command
```

## Example
Run the command take the test files in the project:
gradle
```
./gradlew run --args="--changes-file=src/test/resources/changes_example.json --input-file=src/test/resources/mixtape-data.json --output-file=output.json" 
```
Note
Adjust the Logging level by appending --stacktrace or --debug

Java
```
java -jar build/libs/assignment-mixtape-1.0-SNAPSHOT.jar --changes-file=src/test/resources/changes_example.json --input-file=src/test/resources/mixtape-data.json --output-file=output.json
```

## For Testing
Run the command to execute unit tests:
```
./gradlew test
```

## File content
* input-file: it consists of a set of users, songs, and playlists that are part of a music service

* changes-file: it consists of a set of actions that applies to mixtape
There are 3 types of changes
* Add an existing song to an existing playlist. Example:
```
{
  "type" : "add_song",
  "song_id" : 1,
  playlist_id" : 2
}
```
* Add a new playlist for an existing user; the playlist should contain at least one existing song. Example:
```
{
  "type" : "create_playlist",
  "user_id" : 2,
  "song_ids" : [ 3, 4, 5 ]
}
```
* Remove an existing playlist. Example:
```
{
  "type" : "remove_playlist",
  "playlist_id" : 2
}
```

## FAQ
How does the change apply?
+ The application first parses the json files and applies the changes sequentially in the order of the content of file changes.json. Then output the final modified mixtape.

I am not getting output file. Why?
+ Please check the log. If any of the change/action is not applicable to the current state of mixtape, it will terminate and no output file will result.


## Discussion
In order to scale this application to handle very large input files and/or very large changes files, there are few technical challenges.
* The parser currently loads the whole content in memory at once for those JSON files. The application will not be able to handle large files if the memory resources are limited.
* It will take a long time to process and apply the change from those very large files.
* It is required to re-process the whole dataset if any step fails (parsing / applying changes / output the result).

### Version 2.0
I would upgrade application to version 2.0 by doing the following. First I would divide the work into 3 jobs. They are responsible for ingesting of the input files, executing the changes of the files and writing the file into target location.

For the ingestion job, I would implement a custom streaming parser (such as jackson stream API) that avoids loading the whole file in-memory in the first place. The ingestion job will also transform the data in the form of database entry and store in datastore (sql or no-sql).

Once the ingestion is done, the next job would pull the records from `changes` table in the datastore and apply the change. The job execute the changes from database and marked as execution status. Meanwhile, the record in the change table also contains the incremental id that represents the order of execution, so that the application would be able to pick it up where it fails and developer can look at failing entry as well.

Once the changes are applied, I would implement a custom write that pulls the data from the database and output the result to target location.

### Version 3.0 and beyond
If we want to make the application very scalable and support mulitple users, the application will not be a standalone app. I would re-design it as a more distributed system.

### Loading mixtape files
The user would upload the mixtape files to S3. In the meanwhile, there will be a service that listens the change on the S3 bucket and submit a map reduce (EMR) job that ingest the file into no-sql datastore like dynamo db. I would change the file format in csv or any other row based format that represents the mixtapes and the change files which are more friendly for running mapreduce job. The job would insert the mixtape data into dynamo db through the map reduce job.

### Processing change files
Similar to the mixtape files, user also upload the changes file to S3. The entry of the changes file will contain the timestamp for the use later in mapreduce job. After the ingestion is done, the service would kick off another map reduce job which maps the entry by playlist id first, and perform action in the reduce phase. The actions should be executed in the order of the timestamps which is defined in the changes file.

### Export
Once the process is done, there will be an external service that expose the content as a REST service. Each time the service return N records to the user with a pagination token. 
