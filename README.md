# IMDb Movie Search Engine Using PageRank

![Demo that searches some movies](assets/demo.gif)

## Project Description

This project searches movies available on IMDb and links the user to the webpage for said movie.

## Motivation and Development

As part of our Data Structures and Algorithms course, we were required to work on a search engine for a dataset our choosing.  

We chose The Movies Dataset over other datasets because it required more preprocessing and feature selection than some of the other datasets used by our classmates. This, of course, helped us better grasp the concepts needed to work with data.  

As for how the search engine was made, we used Google's first research paper published at Stanford in 1998 to implement our own
version of the PageRank algorithm from scratch.

## Pre-requisites

- Java JDK 11 or above
- Apache Maven (this repo uses version 3.8.4)

## How to run/build

Open your favorite command prompt/terminal and simply enter `mvn package` to build and run the code.  
If you wish to only compile, you can use `mvn compile`.

## Future Work

- The Achilles' Heel of this project is the 'Add new record' feature. Once a record is added, the entire index needs to be reconstructed. A much more scalable solution was possible but not implemented due to time constraints.
- The current algorithm prioritizes word position and frequency of overall word occurrence over all other factors, sometimes making it difficult to search by exact movie title names. Future versions could benefit from giving more weightage to the Movie Title.

## References/Data used

Original Google research paper "The Anatomy of a Large-Scale Hypertextual Web Search Engine" by Sergey Brin and Lawrence Page:  
<https://research.google/pubs/pub334.pdf>

The Movies Dataset from Kaggle:  
<https://www.kaggle.com/rounakbanik/the-movies-dataset>

## Credits

@EkuDS1 (Implementation of search engine and build tools)  
@faraz455 (Graphical User Interface)  
@basimehsan (Adding new movie metadata to the index)
