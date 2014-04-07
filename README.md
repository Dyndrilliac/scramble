*******************************************************************

* Title:  Scramble
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   3/8/2012

*******************************************************************

This code makes use of my [Custom Java API](https://github.com/Dyndrilliac/java-custom-api). In order to build this source, you should clone the repository for the API using your Git client, then import the project into your IDE of choice (I prefer Eclipse), and finally modify the build path to include the API project. For more detailed instructions, see the README for the API project.
	
This is a simple yet challenging word puzzle game where the user has three chances to guess a randomly chosen word that has had its letters rearranged in a random order. The user's total score is a number between zero and five, inclusively. It is determined by dividing the user's total number of points by the total number of puzzles that have been attempted. Guessing a puzzle on the first try is worth five points, two attempts is worth three points, and using all of your guesses to solve a puzzle will only earn you one point. The user also has the option to give up at any time. Regardless of whether the user gives up or fails all three guesses the answer to the puzzle will be revealed before the game sets itself up for another round. A user's current overall score and the number of puzzles they have attempted are shown at all times.

A pre-compiled JAR binary can be downloaded from [this link](https://www.dropbox.com/s/sej92s55210iapi/Scramble.jar). A sample wordbank using some common computer science terms can be downloaded from [this link](https://github.com/Dyndrilliac/scramble/blob/master/wordbanks/computer%20science.wordbank).