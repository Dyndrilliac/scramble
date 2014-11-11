/*
	Title: Scramble
	Author: Matthew Boyette
	Date: 3/8/2012
	
	This is a simple yet challenging word puzzle game where the user has three chances to guess a randomly chosen
	word that has had its letters rearranged in a random order. The user's total score is a number between zero and
	five, inclusively. It is determined by dividing the user's total number of points by the total number of puzzles
	that have been attempted. Guessing a puzzle on the first try is worth five points, two attempts is worth three
	points, and using all of your guesses to solve a puzzle will only earn you one point. The user also has the option
	to give up at any time. Regardless of whether the user gives up or fails all three guesses the answer to the puzzle
	will be revealed before the game sets itself up for another round. A user's current score and the number of puzzles
	they have attempted are shown at all times.
*/

import api.gui.ApplicationWindow;
import api.util.EventHandler;
import api.util.Mathematics;
import api.util.Support;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Scramble
{
	protected final static Object[] getWordBank(final Component parent, final boolean isDebugging)
	{
		ArrayList<String>	words		= new ArrayList<String>();
		String				filePath	= null;
		
		do
		{
			filePath = Support.getFilePath(parent, true, isDebugging);
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
		catch (final Exception exception)
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
	
	public final static void main(final String[] args)
	{
		new Scramble(args);
	}
	
	protected final static String shuffleWord(final String word)
	{
		// Create a list of characters.
		java.util.List<Character> characters = new ArrayList<Character>();
		
		// For each character in strWord, add that character to the list.
		for (char c: word.toCharArray())
		{
			characters.add(c);
		}
		
		// StringBuilder objects are the most efficient way to perform variable-length string concatenation.
		StringBuilder output = new StringBuilder(word.length());
		
		// Pick a letter randomly from the list, remove it from the list, and then append it to the output string.
		while (characters.size() != 0)
		{
			int randPicker = Mathematics.getRandomInteger(0, characters.size(), false);
			output.append(characters.remove(randPicker));
		}
		
		return output.toString();
	}
	
	private int					currentGuesses	= 0;
	private int					currentScore	= 0;
	private int					currentWord		= 0;
	private JTextField			input			= null;
	private boolean				isDebugging		= false;
	private JLabel				label			= null;
	private String				scrambledWord	= "";
	private int					totalWords		= 0;
	private ApplicationWindow	window			= null;
	private Object[]			wordsArray		= null;
	
	public Scramble(final String[] args)
	{
		this.setDebugging(Support.promptDebugMode(this.getWindow()));
		this.setWordsArray(Scramble.getWordBank(this.getWindow(), this.isDebugging()));
		
		// Define a self-contained ActionListener event handler.
		EventHandler<Scramble> myActionPerformed = new EventHandler<Scramble>(this)
		{
			private final static long serialVersionUID = 1L;

			@Override
			public final void run(final AWTEvent event)
			{	
				ActionEvent	actionEvent	= (ActionEvent)event;
				Scramble	parent		= this.getParent();
				
				/*
					JDK 7 allows string objects as the expression in a switch statement.
					This generally produces more efficient byte code compared to a chain of if statements.
					http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
				*/
				switch (actionEvent.getActionCommand())
				{
					case "Guess":
						
						parent.handleInput();
						break;
					
					case "Give Up":
						
						parent.revealAnswer(parent.getWindow());
						break;
					
					default:
						
						if (actionEvent.getSource() instanceof JTextField)
						{
							parent.handleInput();
						}
						break;
				}
			}
		};
		
		// Define a self-contained interface construction event handler.
		EventHandler<Scramble> myDrawGUI = new EventHandler<Scramble>(this)
		{
			private final static long serialVersionUID = 1L;

			@Override
			public final void run(final ApplicationWindow window)
			{
				Scramble	parent		= this.getParent();
				Container	contentPane	= window.getContentPane();
				
				parent.pickWord();
				
				JPanel		outputPanel		= new JPanel();
				JLabel		scrambledLabel	= new JLabel("Scrambled Word:   " + parent.getScrambledWord());
				JPanel		textPanel		= new JPanel();
				JTextField	inputText		= new JTextField(30);
				JPanel		controlPanel	= new JPanel();
				JButton		guessButton		= new JButton("Guess");
				JButton		giveUpButton	= new JButton("Give Up");
				
				contentPane.setLayout(new BorderLayout());
				outputPanel.setLayout(new FlowLayout());
				outputPanel.add(scrambledLabel);
				contentPane.add(outputPanel, BorderLayout.NORTH);
				textPanel.setLayout(new FlowLayout());
				inputText.addActionListener(window);
				inputText.setFont(Support.DEFAULT_TEXT_FONT);
				scrambledLabel.setFont(Support.DEFAULT_TEXT_FONT);
				textPanel.add(inputText);
				contentPane.add(textPanel, BorderLayout.CENTER);
				controlPanel.setLayout(new FlowLayout());
				guessButton.addActionListener(window);
				guessButton.setFont(Support.DEFAULT_TEXT_FONT);
				controlPanel.add(guessButton);
				giveUpButton.addActionListener(window);
				giveUpButton.setFont(Support.DEFAULT_TEXT_FONT);
				controlPanel.add(giveUpButton);
				contentPane.add(controlPanel, BorderLayout.SOUTH);
				parent.setInput(inputText);
				parent.setLabel(scrambledLabel);
			}
		};
		
		this.setWindow(new ApplicationWindow(null, this.getTitle(), new Dimension(380, 125), this.isDebugging(), false, myActionPerformed, myDrawGUI));
		this.getWindow().setIconImageByResourceName("icon.png");
	}
	
	protected final double calculateScore()
	{
		if (this.getTotalWords() == 0) // Cannot divide by zero!
		{
			return 0.0;
		}
		else
		{
			return ((double)this.getCurrentScore() / (double)this.getTotalWords());
		}
	}
	
	protected final boolean checkGuess(final String guess)
	{
		// If 'guess' is the currently scrambled word, the result is true. Otherwise, the result is false.
		if (guess.equalsIgnoreCase(this.getWordsArray()[this.getCurrentWord()].toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected final int getCurrentGuesses()
	{
		return this.currentGuesses;
	}
	
	protected final int getCurrentScore()
	{
		return this.currentScore;
	}
	
	protected final int getCurrentWord()
	{
		return this.currentWord;
	}
	
	public final JTextField getInput()
	{
		return this.input;
	}
	
	public final JLabel getLabel()
	{
		return this.label;
	}
	
	public final String getScrambledWord()
	{
		return this.scrambledWord;
	}
	
	protected final String getTitle()
	{
		return "Score: " + String.format("%.2f", this.calculateScore()) + "        Puzzles: " + this.getTotalWords();
	}
	
	protected final int getTotalWords()
	{
		return this.totalWords;
	}
	
	public final ApplicationWindow getWindow()
	{
		return this.window;
	}
	
	protected final Object[] getWordsArray()
	{
		return this.wordsArray;
	}
	
	public final void handleInput(final Object... arguments)
	{
		if (this.getInput() == null)
		{
			Support.displayException(window, new Exception("Can't get the input element!"), true);
			return;
		}
		else if (this.getInput().getText() == null)
		{
			Support.displayException(window, new Exception("Can't get the input element's text!"), true);
			return;
		}
		
		// Check the user's guess.
		if (this.checkGuess(this.getInput().getText()))
		{
			JOptionPane.showMessageDialog(window, "Congratulations, your guess was correct!", "Correct!", JOptionPane.INFORMATION_MESSAGE);
			
			if (this.getCurrentGuesses() == 3)
			{
				this.setCurrentScore(this.getCurrentScore() + 5);
			}
			else if (this.getCurrentGuesses() == 2)
			{
				this.setCurrentScore(this.getCurrentScore() + 3);
			}
			else if (this.getCurrentGuesses() == 1)
			{
				this.setCurrentScore(this.getCurrentScore() + 1);
			}
			
			this.reinitialize(window);
		}
		else
		{
			JOptionPane.showMessageDialog(window, "Sorry, your guess was incorrect!", "Incorrect!", JOptionPane.INFORMATION_MESSAGE);
			this.setCurrentGuesses(this.getCurrentGuesses() - 1);
			
			if (this.getCurrentGuesses() <= 0)
			{
				this.revealAnswer(window);
			}
		}
	}
	
	public final boolean isDebugging()
	{
		return this.isDebugging;
	}
	
	public final void pickWord()
	{
		// Randomly select a word from the master word list.
		this.setCurrentWord(Mathematics.getRandomInteger(0, (this.getWordsArray().length - 1), true));
		// Rearrange the word in a random order.
		this.setScrambledWord(Scramble.shuffleWord(this.getWordsArray()[this.getCurrentWord()].toString()));
		// Set the number of available guesses.
		this.setCurrentGuesses(3);
	}
	
	protected final void reinitialize(final ApplicationWindow window)
	{
		// Increment the puzzle counter.
		this.setTotalWords(this.getTotalWords() + 1);
		// Reset the puzzle.
		this.pickWord();
		// Reset the GUI.
		this.getLabel().setText("Scrambled Word:   " + this.getScrambledWord());
		this.getInput().setText("");
		this.getInput().grabFocus();
		window.setTitle(this.getTitle());
	}
	
	public final void revealAnswer(final ApplicationWindow window)
	{
		JOptionPane.showMessageDialog(window,
			"Puzzle:   " + this.getScrambledWord() + "\n" +
			"Solution: " + this.getWordsArray()[this.getCurrentWord()] + "\n\n" +
			"Better luck next time!\n",
			"Solution to Puzzle",
			JOptionPane.INFORMATION_MESSAGE);
		this.reinitialize(window);
	}
	
	protected final void setCurrentGuesses(final int currentGuesses)
	{
		this.currentGuesses = currentGuesses;
	}
	
	protected final void setCurrentScore(final int currentScore)
	{
		this.currentScore = currentScore;
	}
	
	protected final void setCurrentWord(final int currentWord)
	{
		this.currentWord = currentWord;
	}
	
	protected final void setDebugging(final boolean isDebugging)
	{
		this.isDebugging = isDebugging;
	}
	
	protected final void setInput(final JTextField input)
	{
		this.input = input;
	}
	
	protected final void setLabel(final JLabel label)
	{
		this.label = label;
	}
	
	protected final void setScrambledWord(final String scrambledWord)
	{
		this.scrambledWord = scrambledWord;
	}
	
	protected final void setTotalWords(final int totalWords)
	{
		this.totalWords = totalWords;
	}
	
	protected final void setWindow(final ApplicationWindow window)
	{
		this.window = window;
	}
	
	protected final void setWordsArray(final Object[] wordsArray)
	{
		this.wordsArray = wordsArray;
	}
}