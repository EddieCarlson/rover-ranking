# Rover Coding Project

Rover.com was destroyed in a terrible Amazon Web Services and GitHub accident!
Thankfully, no dogs were harmed, but we will have to rebuild our site using
data we retrieved from the Google search index. 

Your task is to recreate a search ranking algorithm and compute search
scores for our sitters.

# Requirements 

To recreate our search rankings, you'll create *a simple command-line program* that can be run
locally and that will input and output csv data.

Additionally, there is a short **discussion question**
to answer (see below) about how you would build this system in the real
world. 

Please include a README with instructions on how to setup and run your project
locally. Note that we are primarily Mac users. 

**Please use the language that you feel will best show your skills.** Keep in
mind that if you are brought for an in-person interview, you may be asked to continue
building upon this solution. Feel free to use whatever libraries or packages you wish. 

Finally, if you have any questions, don't hesitate to ask.

## Recreating the Search Ranking Algorithm
We were able to write a script and scrape the Google index for all of the
reviews customers have left for their stays with sitters. 

We have saved that information in the attached CSV.

Your command-line program should import the data to hold in memory, and use it to recreate our search algorithm. You don't need to store the data in a database - save that for the discussion question! 

Here's how the search ranking algorithm will work:

- For each sitter, we first calculate a Profile Score and a Ratings Score.
  These are then used to calculate the overall Search Score, which is used for search rankings.

- The Profile Score is 5 times the fraction of the English alphabet comprised by the
  distinct letters in what we've recovered of the sitter's name. For example, the sitter name `Leilani R.` has 6 distinct letters.

- The Ratings Score is the average of their stay ratings.

- The Search Score is a weighted average of the Profile Score and Ratings
  Score. When a sitter has no stays, their Search Score is equal to the Profile Score. When a sitter has 10 or more
  stays, their Search Score is equal to the Ratings Score. The idea is that as a sitter gets more reviews, we will weigh the
  Ratings Score more heavily.

- Scores should be limited to two decimal places.

## Output a list of Sitters

Your program should output a csv called `sitters.csv`, containing the following
columns:

* Sitter email (`email`)
* Sitter name (`name`)
* Profile Score (`profile_score`)
* Ratings Score (`ratings_score`)
* Search Score (`search_score`)

The csv should be sorted by Search Score (descending), sorting alphabetically on the
sitter name as a tie-breaker.

## Hint for Testing the Search Ranking Algorithm

Suppose there is a sitter whose Profile Score is 2.5 and who gets a rating of
5.0 with every stay. Then their Search Score should behave like this:

| Stay          | Search Score |
| ------------- | ------------- |
| 0 | 2.50
| 1 | 2.75
| 2 | 3.00
| 3 | 3.25
| 4 | 3.50
| 5 | 3.75
| 6 | 4.00
| 7 | 4.25
| 8 | 4.50
| 9 |  4.75
| 10 | 5.00
| 11 | 5.00
| 12 | 5.00

## Discussion Question

Imagine you are designing a production web application to compute the search scores for sitters and to return a list of sitters for search results. How would you do it? Please answer **ONE** of the following discussion questions about the approach you'd take:

- Data Scientists want to perform offline computations that are then used to compute the search scores served in the production application. How would you design a pipeline to do this?
- What infrastructure choices might you make to build and host this project at scale? Suppose your web application must return fast search results with a peak of 10 searches per second.
- We calculate search scores infrequently but we then access them frequently throughout the day. How should we structure the data to optimize resource utilization? What would be a good data storage solution and why?
- Describe how you would approach API design for a backend service to provide sitter and rank data to a client.

Write your answer in the README inside your project github repo. Your answer should not exceed 400 words.

## When you're done with the project...

When you're done with the project, compress your project directory into a Zip file or similar, making sure to include the output file, `sitters.csv`. Then, reply to the email you received from us with your attachment.

# Evaluation 

### Checklist:
- [ ] Are Profile, Rating, and Search Scores computed correctly?
- [ ] Does the output file include all necessary columns, and is it in descending order based on Search Score? 
- [ ] Does the README include setup/running instructions (ideally for Mac)?
- [ ] Does the README include your answer to the Discussion Question?
- [ ] Have you included your CLI code, README, and output file in the Zip folder? 

The work you create here should be representative of code that we'd expect to
receive from you if you were hired tomorrow (proper abstractions, tests
for the scoring algorithm calculation, best practices, etc). 
