/*
 * Copyright (C) 2013 Bogdan Pistol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gg.pistol.scissors.gui;

import gg.pistol.scissors.game.Game;
import gg.pistol.scissors.game.GameAlreadyStartedException;
import gg.pistol.scissors.game.Gesture;
import gg.pistol.scissors.game.Referee;
import gg.pistol.scissors.game.RefereeImpl;
import gg.pistol.scissors.player.GameObserver;
import gg.pistol.scissors.player.Player;
import gg.pistol.scissors.player.RandomComputerPlayer;
import gg.pistol.scissors.player.SmartComputerPlayer;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Random;

/**
 * Graphical User Interface game observer.
 *
 * @author Bogdan Pistol
 */
public class GameDialog extends JDialog implements GameObserver {

    private final Random random = new Random();

    private JLabel player1Name;
    private JLabel player1Gesture;

    private JLabel player2Name;
    private JLabel player2Gesture;

    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;

    private JButton newGameButton;
    @Nullable private Referee referee;
    @Nullable private HumanPlayer humanPlayer;
    @Nullable private Player player1;
    @Nullable private Player player2;

    public GameDialog() {
        super((Window) null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().add(createContent(), BorderLayout.CENTER);
        getContentPane().add(Box.createVerticalStrut(20), BorderLayout.NORTH);
        getContentPane().add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(500, 300));
        setTitle("Scissors");
    }

    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        content.add(northPanel, BorderLayout.NORTH);
        player1Name = new JLabel("Player 1");
        northPanel.add(player1Name);

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        content.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BorderLayout());

        player1Gesture = new JLabel();
        player1Gesture.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(player1Gesture, BorderLayout.NORTH);

        JPanel newGamePanel = new JPanel(new BorderLayout());
        newGamePanel.setLayout(new BoxLayout(newGamePanel,  BoxLayout.X_AXIS));
        newGameButton = new JButton("New game");
        newGamePanel.add(Box.createHorizontalGlue());
        newGamePanel.add(newGameButton);
        newGamePanel.add(Box.createHorizontalGlue());
        centerPanel.add(newGamePanel, BorderLayout.EAST);
        newGameButton.setVisible(false);

        player2Gesture = new JLabel();
        player2Gesture.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(player2Gesture, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel();
        content.add(southPanel, BorderLayout.SOUTH);
        player2Name = new JLabel("Player 2");
        southPanel.add(player2Name);
        rockButton = new JButton("Rock");
        rockButton.setEnabled(false);
        southPanel.add(rockButton);
        paperButton = new JButton("Paper");
        paperButton.setEnabled(false);
        southPanel.add(paperButton);
        scissorsButton = new JButton("Scissors");
        scissorsButton.setEnabled(false);
        southPanel.add(scissorsButton);
        
        addActions();
        return content;
    }

    private void addActions() {
        rockButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (humanPlayer != null) {
                    humanPlayer.setHumanGesture(Gesture.ROCK);
                }
            }
        });
        paperButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (humanPlayer != null) {
                    humanPlayer.setHumanGesture(Gesture.PAPER);
                }
            }
        });
        scissorsButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (humanPlayer != null) {
                    humanPlayer.setHumanGesture(Gesture.SCISSORS);
                }
            }
        });
        newGameButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGameButton.setVisible(false);
                showMenu();
            }
        });
    }

    @Override
    public void gestureThrown(final Player player, final Gesture gesture) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (player == player1) {
                    player1Gesture.setText(gesture.toString());
                } else {
                    player2Gesture.setText(gesture.toString());
                }
            }
        });
    }

    @Override
    public void countdown(final int count) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                player1Gesture.setText(Integer.toString(count));
                player2Gesture.setText(Integer.toString(count));

                if (count == 0) {
                    player1Gesture.setText("Please choose your gesture!");
                    player2Gesture.setText("Please choose your gesture!");

                    if (humanPlayer != null) {
                        rockButton.setEnabled(true);
                        paperButton.setEnabled(true);
                        scissorsButton.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void gameFinished(final Game game) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Player player : game.getPlayers()) {
                    String gestureText = "time expired";
                    if (game.getPlayerGesture(player) != null) {
                        gestureText = game.getPlayerGesture(player).toString();
                    }
                    String text = gestureText + " - " + game.getPlayerResult(player);
                    if (player == player1) {
                        player1Gesture.setText(text);
                    } else {
                        player2Gesture.setText(text);
                    }
                }
                newGameButton.setVisible(true);
                rockButton.setEnabled(false);
                paperButton.setEnabled(false);
                scissorsButton.setEnabled(false);
                if (humanPlayer != null) {
                    humanPlayer.cancelHumanGesture();
                    humanPlayer = null;
                }
            }
        });
    }

    public void showMenu() {
        String[] options = new String[]{"Human vs Computer", "Computer vs Computer", "Quit"};
        int ret = JOptionPane.showOptionDialog(this, "Do you want to play against the computer or do you want the computers to play against each other?",
                "Game type", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, UIManager.getIcon("OptionPane.questionIcon"), options, options[0]);
        switch (ret) {
            case 0:
                player2 = humanPlayer = new HumanPlayer("Player 2: Human Player");
                break;
            case 1:
                player2 = random.nextBoolean() ? new RandomComputerPlayer("Player 2: Random Computer") : new SmartComputerPlayer("Player 2: Smart Computer");
                break;
            case 2:
            case -1:
                dispose();
                return;
        }
        player1 = random.nextBoolean() ? new RandomComputerPlayer("Player 1: Random Computer") : new SmartComputerPlayer("Player 1: Smart Computer");

        player1Name.setText(player1.getName());
        player2Name.setText(player2.getName());
        referee = new RefereeImpl(2, 1000L, 2000L, Arrays.<Player>asList(player1, player2), Arrays.<GameObserver>asList(this));

        try {
            referee.startGame();
        } catch (GameAlreadyStartedException e) {
            // should not happen
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (referee != null) {
            referee.stopGame();
        }
    }

}
