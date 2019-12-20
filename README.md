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
git clone git@github.com:ck3leetcode/highspot-assignment-mixtape.git
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
In order to scale this application to handle very large input files and/or very large changes files. There are few things to consider:
* storage
* performance
* robustness

 As mentioned above, both input files are ingested in memory first before then running through actions one at a time.


