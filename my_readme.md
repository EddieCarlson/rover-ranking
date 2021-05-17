# Rover Recovery

## Description:

This program will read sitter reviews from a csv in a known format and produce a variety of ratings (profile score, ratings score, and search score) for each sitter (identified uniquely by their email address in the reviews). The program will produce a csv file named `sitters.csv` that will be written to the current directory. This file will contain the columns: "email, name, profile_score, ratings_score, search_score", and each row subsequent to the header will represent a sitter and their scores. The sitters will be ordered first by their search_score (desc), then by name (asc). email and name are text, the other fields are rational numbers between 0 and 5 (inclusive), represented to 2 decimal points.

## Running:

### Prerequisites:

1. java 8
2. sbt

* If you already have java 8 installed, you only need to install sbt, which can be acquired via homebrew: `brew install sbt`. when sbt opens the project, it will automatically download the appropriate version of scala specified by the project.
* If you do not have java 8 installed, you can install it on your own (https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) and then install sbt, or (as recommended by scala documentation), use the Scala Installer to do both (plus some other stuff. see detail here: https://docs.scala-lang.org/getting-started/index.html)

### Execution:

`cd` to the root of this project and run `sbt 'run <filepath>'`, where <filepath> is the file path of the reviews csv file
Alternatively, if you wish to run the program multiple times with different files/filepaths, you can first run `sbt`, and inside the sbt session, run `run <filepath>`, so as to not incur the sbt startup cost for every run.

Note: the first sbt run may have a long startup time if has to download scala.

## Assumptions:

* The provided csv is in the same format as the `reviews.csv` file that came with the problem description. Meaning that the filename provided to the program must point to a csv having the same 14 columns in the same order as the original `reviews.csv`. This could be worked around by reading the header line and looking for the indices of desired columns (sitter, sitter_email, rating), but that is nearly as brittle and also opens the door for the program to run on csvs that happen to contain those column names but were produced for some other purpose and not meant to be processed by this program.
* Ratings are always integers (from the rating column in the input csv).
* A missing value (no character between commas) is meant to be parsed as an empty string.
* Validation on emails (containing an "@", e.g.) and names (not containing numbers, e.g.) is not required.

## Discussion Question (API Design):

GET /sitters - returns a json list where each element is a sitter, representing all sitters in the database. query params "offset" (default: 0) and "limit" (default 100) may be provided for pagination. limit cannot be larger than 1000. "sort" query param accepted, with allowed values ["profile_score", "ratings_score", "search_score"] (default: "search_score"), returning the list sorted descendingly. "ranking_gt" and "ranking_lt" query params accepted as lower and upper bounds for the score specified in "sort".

GET /sitters/<id> - returns a single sitter looked up by the identifier used by the database (<id> is an int or uuid). cannonical means of retrieval.

GET /sitters/email/<email> - returns a single sitter (email is unique) looked up by email address. This is provided beacuse sitter email seems like information the client may have if they don't have the id, and it also uniquely identifies the sitter. could also be done with query param `/sitters?email=<email>`, but I like path elements.

Just returning the same data emitted by the program, we would return the following as a json response body:

{
  "email": "<email>",
  "name": "<name>",
  "profile_score": <profile_score>,
  "ratings_score": <ratings_score>,
  "search_score": <search_score>
}

Presumably, these values have been pre-calculated by the program and stored in a `sitters` table in a database, making the lookup (by an indexed field) very fast.

However, phone number, response time, and response time are also present in the input csv, and could also be of value to the client. 
