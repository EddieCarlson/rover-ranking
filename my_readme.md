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

* The filepath given as a program arg must point to a csv is the exact same format as `reviews.csv`
  (detailed parsing error messgaes are returned, if not)
* For the sake of making an interesting validation system:
  * sitter_email requires an '@'
  * sitter_name must be non-empty
  * rating must be an int between 0 and 5 (inclusive)

## Discussion Question (API Design):

In order to approach the problem, we need to first understand who the client is. My first thought is that the frontend would be calling the API to generate lists of sitters to display to an end-user browsing rover.com looking for a sitter for their pet. To satisfy this user, we will need to return a list of elements representing sitters, containing enough information to generate a list of sitter "thumbnails" with info about the sitters and links to the sitter profiles. The user can then scan the list of sitters and click on one to go to the sitter's profile page. If the response is served to the user's browser, it will need to contain public info only. The user will probably want to rank sitters by score (search_score, most likely), so returning score(s) with the sitters having been ranked by the scores is required. Also, the user will only want to see sitters willing to travel to their location.

(minimum) json response object representing a sitter to satisfy the above client:

{
  "sitter_name": "string",
  "sitter_img": "string - url",
  "sitter_id": "string - unique id",
  "search_score": "number",
}

The above fields can be generated from the info provided in `reviews.csv`

"profile_score" and "rankings_score" could also easily be included.

The user may also be interested to know "avg response time", "total sits", "sitter since", which could all be calculated in processing reviews.csv. Perhaps a sitter (brief) description from some other source can be included.

We do not want to return email or phone number, since that info is likely private, and we want the user to contact the sitter through the site.

A link to each sitter's profile could be generated from `sitter_id`.

If we are serving sensitive data, we will need to perform authentication, but that is not the case described above.

Endpoints:

GET /sitters
 * returns a json list where each element is a sitter willing to travel to the user's location (as shown above)
 * pagination query params: "offset" (default: 0) and "limit" (default 100)
 * perhaps the user wants to see only sitters above a certain search_score. query param "score_gt" (default: 0) can be provided
 * the location of the user must be known.
    * if the user is logged in, this could be looked up from their profile (from info in an auth/session/cookie header).
    * if not, and we still want to respond, we could take "lat" and "long" query params

GET /sitters/id
  * returns a single sitter looked up by the unique identifier for the sitter, containing enough information to generate the profile page
