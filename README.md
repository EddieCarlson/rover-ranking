# Rover Recovery

Copyright 2021 Candidate

## Description:

This program will read sitter reviews from a csv in a known format and produce a variety of ratings (profile score,
ratings score, and search score) for each sitter (identified uniquely by their email address in the reviews). The
program will produce a csv file named `sitters.csv` that will be written to the current directory. This file will
contain the columns: [email, name, profile_score, ratings_score, search_score], and each row following the header will
represent a sitter and their scores. The sitters will be ordered first by their search_score (desc), then by name (asc).
email and name are text, the other fields are rational numbers between 0 and 5 (inclusive), rounded to 2 decimal
points.

## Running:

### Prerequisites:

1. java 8 jdk
2. sbt

* The scala documentation recommends installing sbt and a compatible jdk with:
  `brew install coursier/formulas/coursier && cs setup` (see: https://docs.scala-lang.org/getting-started/index.html)
* If you already have a java 8 jdk installed, you can just install sbt via: `brew install sbt`
  * if this doesn't work, or sbt gives you some sass about your jdk, try the first option

When sbt opens the project, it will automatically download the appropriate version of scala specified by the project.

### Execution:

`cd` to the root of this project and run `sbt 'run <filepath>'`, where `<filepath>` is the file path of the csv file 
containing the reviews. Alternatively, if you wish to run the program multiple times with different files/filepaths,
you can first run `sbt`, and inside the sbt session, run `run <filepath>`, so as to not incur the sbt startup cost for
every run.

Note: the first sbt run may have a long startup time if has to download scala.

## Assumptions:

* The filepath given as a program arg must point to a csv in the exact same format as the original `reviews.csv`
  (detailed error messgaes are returned, if not)
* Sitters are sorted by rounded search scores (i.e. calculated search scores of 2.003 and 2.004 would be considered
  identical), then by name
* For the sake of making an interesting validation system:
  * sitter_email requires an '@'
  * sitter_name must be non-empty
  * rating must be an int between 0 and 5 (inclusive)
    
## Parsing and Validation Error Reporting:
* I now realize it was unnecessary, but I spent a good amount time making a robust yet unobtrusive parsing/validating
  error handling and reporting system. I would be pleased if the reviewer could see it in action by running 
  `sbt 'run invalid_reviews.csv'` :)

## Discussion Question (API Design):

### Approach:

Firstly, we need to understand who the client is. I imagine the frontend would call the
API to generate a list of sitters to display to an end-user browsing rover.com. So, we will return a list of elements
representing sitters, containing enough information to generate a list of sitter "thumbnails" with info about the
sitters and links to the sitter profiles. The user can then scan the list of sitters and click on one to go to the
sitter's profile page. If the response goes directly to the end user, it will need to contain public info only. The user
may want sitters ranked by score (likely `search_score`), so returning score(s) with the sitters in a pre-sorted list
is required. The user will only want to see sitters willing to travel to their location.

(minimum) json response object representing a sitter to satisfy the above client:

```
{
  "sitter_name": <string>,
  "sitter_img": <string - url>,
  "sitter_id": <string - unique id>,
  "search_score": <number>,
}
```

The above fields can be generated from the info provided in `reviews.csv`

A link to each sitter's profile could be generated from `sitter_id`.

### Possible Additions:

`profile_score` and `rankings_score` could also easily be included.

The user may also be interested to know "avg response time", "total sits", "sitter since", which could all be calculated
in processing reviews.csv. Perhaps a sitter (brief) description from some other source could be included.

We do not want to return `email` or `sitter_phone`, since that info is likely private, and we want the user to contact
the sitter through the site.

### Endpoints:

`GET /sitters`
 * returns a json list where each element is a sitter (as shown above) willing to travel to the user's location
 * pagination query params: `offset` (default: 0) and `limit` (default 100)
 * perhaps the user wants to see only sitters above a certain search_score. query param `score_gt` (default: 0) can be
   provided
 * the location of the user must be known so the service can filter sitters near enough to be willing to sit for them.
    * if the user is logged in, this could be looked up from their profile (from info in an auth/session/cookie header).
    * if not, and we still want to respond, we could take `lat` and `long` query params

`GET /sitters/<id>`
  * returns a single sitter looked up by the unique identifier for the sitter, containing enough information to generate
    the profile page
 
Internal and authed clients will want an endpoint that provides the private information as well.
