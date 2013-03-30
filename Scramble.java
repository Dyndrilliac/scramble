/*
	Title:  Scramble
	Author: Matthew Boyette
	Date:   3/8/2012
	
	This is a simple yet challenging word puzzle game where the user has three chances to guess a word that has had its letters rearranged in
	a random order. The user's total score is a number between zero and five, inclusively. It is determined by dividing the user's total number
	of points by the total number of puzzles that have been attempted. Guessing a puzzle on the first try is worth five points, two attempts is
	worth three points, and using all of your guesses to solve a puzzle will only earn you one point. The user also has the option to give up at
	any time. Regardless of whether the user gives up or fails all three guesses the answer to the puzzle will be revealed before the game sets
	itself up for another round. A user's current score and the number of puzzles they have attempted are shown at all times.
*/

import api.gui.*;
import api.util.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.*;

public final class Scramble
{
	private static Object[] wordsArray     = getWordBank();
	private static String   scrambledWord  = "";
	private static int      currentGuesses = 0;
	private static int      currentScore   = 0;
	private static int      currentWord    = 0;
	private static int      totalWords     = 0;
	
	private static final double calculateScore()
	{
		if (totalWords == 0) // Cannot divide by zero!
		{
			return 0.0;
		}
		else
		{
			return ((double)currentScore / (double)totalWords);
		}
	}
	
	private static final boolean checkGuess(final String guess)
	{
		// If 'guess' is the currently scrambled word, the result is true. Otherwise the result is false.
		if (guess.equalsIgnoreCase(wordsArray[currentWord].toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static final String getTitle()
	{
		DecimalFormat twoDecimalPlaces = new DecimalFormat("##.##");
		return "Score: " + Double.valueOf(twoDecimalPlaces.format(calculateScore())) + "        Puzzles: " + totalWords;
	}
	
	private static final Object[] getWordBank()
	{
		ArrayList<String> words = new ArrayList<String>();
		String filePath = null;
		
		do
		{
			filePath = Support.getFilePath(null, true, false);
		}
		while ((filePath == null) || filePath.isEmpty());
		
		Scanner inputStream = null; // Stream object for file input.
		
		try
		{
			// Initialize file stream. If the given path is invalid, an exception is thrown.
			inputStream = new Scanner(new File(filePath));
			
			while (inputStream.hasNextLine())
			{
				String line = inputStream.nextLine().toLowerCase().trim();
				
				if (!(line.isEmpty()))
				{
					words.add(line);
				}
			}
		}
		catch (Exception exception)
		{
			// Handle the exception by alerting the user of the error.
			Support.displayException(null, exception, true);
		}
		finally
		{
			if (inputStream != null)
			{
				// Close the input stream.
				inputStream.close();
				inputStream = null;
			}
		}
		
		return words.toArray();
	}
	
	private static final void handleInput(final Object... arguments)
	{
		ApplicationWindow window = (ApplicationWindow)arguments[1];
		JTextField        input  = null;
		
		for (int i = 0; i < window.getElements().size(); i++)
		{
			if (window.getElements().get(i) instanceof JTextField)
			{
				input = (JTextField)window.getElements().get(i);
			}
		}
		
		if (input == null)
		{
			Support.displayException(window, new Exception("Can't get the input element!"), true);
			return;
		}
		else if (input.getText() == null)
		{
			Support.displayException(window, new Exception("Can't get the input text!"), true);
			return;
		}
		
		// Check the user's guess.
		if (checkGuess(input.getText()))
		{
			JOptionPane.showMessageDialog(window, "Congratulations, your guess was correct!", "Correct!", JOptionPane.INFORMATION_MESSAGE);
			if (currentGuesses == 3)
			{
				currentScore += 5;
			}
			else if (currentGuesses == 2)
			{
				currentScore += 3;
			}
			else if (currentGuesses == 1)
			{
				currentScore += 1;
			}
			reinitialize(window);
		}
		else
		{
			JOptionPane.showMessageDialog(window, "Sorry, your guess was incorrect!", "Incorrect!", JOptionPane.INFORMATION_MESSAGE);
			currentGuesses--;
			if (currentGuesses <= 0)
			{
				revealAnswer(window);
			}
		}
	}
	
	public static final void main(final String[] args)
	{
		ApplicationWindow mainWindow = null;
		int choice = Support.promptDebugMode(mainWindow);
		
		// Define a self-contained ActionListener event handler.
		EventHandler myActionPerformed = new EventHandler()
		{
			public final void run(final Object... arguments) throws IllegalArgumentException
			{
				if ((arguments.length <= 1) || (arguments.length > 2))
				{
					throw new IllegalArgumentException("myActionPerformed Error : incorrect number of arguments.");
				}
				else if (!(arguments[0] instanceof ActionEvent))
				{
					throw new IllegalArgumentException("myActionPerformed Error : argument[0] is of incorrect type.");
				}
				else if (!(arguments[1] instanceof ApplicationWindow))
				{
					throw new IllegalArgumentException("myActionPerformed Error : argument[1] is of incorrect type.");
				}
				
				ActionEvent       event  = (ActionEvent)arguments[0];
				ApplicationWindow window = (ApplicationWindow)arguments[1];
				
				/*
					JDK 7 allows string objects as the expression in a switch statement.
					This generally produces more efficient byte code compared to a chain of if statements.
					http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
				*/
				switch (event.getActionCommand())
				{
					case "Guess":
						
						handleInput(arguments);
						break;
						
					case "Give Up":
						
						revealAnswer(window);
						break;
						
					default:
						
						if (event.getSource() instanceof JTextField)
						{
							handleInput(arguments);
						}
						break;
				}
			}
		};
		
		// Define a self-contained interface construction event handler.
		EventHandler myDrawGUI = new EventHandler()
		{
			public final void run(final Object... arguments) throws IllegalArgumentException
			{
				if (arguments.length <= 0)
				{
					throw new IllegalArgumentException("myDrawGUI Error : incorrect number of arguments.");
				}
				else if (!(arguments[0] instanceof ApplicationWindow))
				{
					throw new IllegalArgumentException("myDrawGUI Error : argument[0] is of incorrect type.");
				}
				
				ApplicationWindow window         = (ApplicationWindow)arguments[0];
				Container         contentPane    = window.getContentPane();
				JPanel            outputPanel    = new JPanel();
				JLabel            scrambledLabel = new JLabel("Scrambled Word:   " + scrambledWord);
				JPanel            textPanel      = new JPanel();
				JTextField        inputText      = new JTextField(25);
				JPanel            controlPanel   = new JPanel();
				JButton           guessButton    = new JButton("Guess");
				JButton           giveUpButton   = new JButton("Give Up");
				
				contentPane.setLayout(new BorderLayout());
				outputPanel.setLayout(new FlowLayout());
				outputPanel.add(scrambledLabel);
				contentPane.add(outputPanel, BorderLayout.NORTH);
				textPanel.setLayout(new FlowLayout());
				inputText.addActionListener(window);
				textPanel.add(inputText);
				contentPane.add(textPanel, BorderLayout.CENTER);
				controlPanel.setLayout(new FlowLayout());
				guessButton.addActionListener(window);
				controlPanel.add(guessButton);
				giveUpButton.addActionListener(window);
				controlPanel.add(giveUpButton);	
				contentPane.add(controlPanel, BorderLayout.SOUTH);
				
				window.getElements().add(scrambledLabel);
				window.getElements().add(inputText);
			}
		};
		
		if (choice == JOptionPane.YES_OPTION)
		{
			mainWindow = new ApplicationWindow(null, getTitle(), new Dimension(480, 125), true, false, 
				true, myActionPerformed, myDrawGUI);
		}
		else if (choice == JOptionPane.NO_OPTION)
		{
			mainWindow = new ApplicationWindow(null, getTitle(), new Dimension(480, 125), false, false, 
				true, myActionPerformed, myDrawGUI);
		}
		else
		{
			return;
		}
		
		pickWord();
		
		mainWindow.drawGUI();
		mainWindow.setAlwaysOnTop(true);
		mainWindow.setVisible(true);
	}
	
	private static final void pickWord()
	{
		// Randomly select a word from the master word list.
		currentWord = Games.getRandomInteger(0, (wordsArray.length-1), true);
		// Rearrange the word in a random order.
		scrambledWord = shuffleWord(wordsArray[currentWord].toString());
		// Set the number of available guesses.
		currentGuesses = 3;
	}
	
	private static final void reinitialize(final ApplicationWindow window)
	{
		// Increment the puzzle counter.
		totalWords++;
		// Reset the puzzle.
		pickWord();
		// Reset the GUI.
		JTextField input = null;
		JLabel     label = null;
		
		for (int i = 0; i < window.getElements().size(); i++)
		{
			if (window.getElements().get(i) instanceof JTextField)
			{
				input = (JTextField)window.getElements().get(i);
			}
		}
		
		for (int i = 0; i < window.getElements().size(); i++)
		{
			if (window.getElements().get(i) instanceof JLabel)
			{
				label = (JLabel)window.getElements().get(i);
			}
		}
		
		label.setText("Scrambled Word:   " + scrambledWord);
		input.setText("");
		input.grabFocus();
		window.setTitle(getTitle());
	}

	private static final void revealAnswer(final ApplicationWindow window)
	{
		JOptionPane.showMessageDialog(window, 
			"Puzzle:   " + scrambledWord + "\n" +
			"Solution: " + wordsArray[currentWord] + "\n\n" +
			"Better luck next time!\n", 
			"Solution to Puzzle", 
			JOptionPane.INFORMATION_MESSAGE);

		reinitialize(window);
	}
	
	private static final String shuffleWord(final String word)
	{
		// Create a list of characters.
		java.util.List<Character> characters = new ArrayList<Character>();
		
		// For each character in strWord, add that character to the list.
        for (char c : word.toCharArray())
        {
            characters.add(c);
        }
        
        // StringBuilder objects are the most efficient way to perform variable-length string concatenation.
        StringBuilder output = new StringBuilder(word.length());
        
        // Pick a letter randomly from the list, remove it from the list, and then append it to the output string.
        while(characters.size() != 0)
        {
        	int randPicker = Games.getRandomInteger(0, characters.size(), false);
            output.append(characters.remove(randPicker));
        }
        
        return output.toString();
	}
}