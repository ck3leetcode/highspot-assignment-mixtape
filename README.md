# Highspot Assignment Mixtape

## What is highspot-assignment-mixtape
“Highspot-assignment-mixtape” is a java based command-line batch application that applies a batch of changes to an input mixtape file and output a new mixtape file with the changes.

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
using Gradle
```
./gradlew run --args="--help"
```
using Java / Jar
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

The most commonly used commands are:
  help  Displays help information about the specified command
```

## Example
Run the command in the project directory that runs the test files:

using Gradle
```
./gradlew run --args="--changes-file=src/test/resources/changes_example.json --input-file=src/test/resources/mixtape-data.json --output-file=output.json" 
```
Note
Please see `log4j.properties` for logging configuration

using Java / Jar
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
### Add an existing song to an existing playlist. Example:
```
{
  "type" : "add_song",
  "song_id" : 1,
  playlist_id" : 2
}
```
### Add a new playlist for an existing user; the playlist should contain at least one existing song. Example:
```
{
  "type" : "create_playlist",
  "user_id" : 2,
  "song_ids" : [ 3, 4, 5 ]
}
```
### Remove an existing playlist. Example:
```
{
  "type" : "remove_playlist",
  "playlist_id" : 2
}
```

## FAQ
How does the change apply?
+ The application first parses the json files and applies the changes in the order of the changes defined in file `changes.json`. Then output the final modified mixtape.

I am not getting output file. Why?
+ Please check the log. If any of the change is not applicable to the current state of mixtape, it will terminate and no output file will result.


## Discussion
In order to scale this application to handle very large input files and/or very large changes files, there are few technical challenges.
* The parser currently loads the whole content in memory at once for those JSON files. The application will not be able to handle large files and result `OutOfMemoryException`
* It is required to re-process the whole dataset if any step fails (ingestion / applying changes / output the result).

### Version 2.0
I would upgrade application to version 2.0 by doing the following. I would divide the work into three phases. They are responsible for ingesting the input files, executing the changes of the files and writing the file into target location.

For the ingestion phase, I would implement a custom streaming parser (such as jackson stream API) that avoids loading the whole mixtape file in-memory in the first place. During parsing, the information will be populated into a datastore in the order of user, song and playlist.

Once the ingestion is done, the next phase is to apply the `changes`. Again, I would implement a custom streaming parser (such as jackson stream API) that avoids loading the whole change file in-memory in the first place. When a change in the file is paresd, the applicatoin will attempt to apply the change to the datastore.

Another approach is to also store the `changes` into datastore instead of applying the change directly. Once all the changes first are stored in the database, the application starts to pull the records from datastore to apply the change in the order of the insertion. After a single change is executed against the datastore, the change will be marked as `Done` or `Failed` in the datastore entry. The benefit is that the application would be able to pick up where it fails and developer can look at failing entry as well.

Once the changes are applied, I would implement a writer that pulls the data from the datastore and output the result to target location. Finally, the application will clean up the entries in the datastore.
