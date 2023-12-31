import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.List;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener {
    @Serial
    private static final long serialVersionUID = 1L;
    private final JButton [] [] grid;

    private String state;

    private Minimax aiAgent;

    private boolean isPruning = false;

    private int depth = 2;

    public GUI () {
        super ("Connect Four");
        aiAgent = new Minimax(depth);
        state = "##########################################";
        final JPanel settings_panel = new JPanel();
        settings_panel.setLayout (new FlowLayout());
        final JPanel control_panel = new JPanel();
        control_panel.setLayout (new GridLayout(1, 7));
        final JPanel grid_panel = new JPanel();
        grid_panel.setBackground(Color.decode("#4472C4"));
        grid_panel.setLayout (new GridLayout (6, 7));
        this.grid = new JButton[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                final JButton button = new JButton (new ImageIcon("src/noPlayer.png"));
                button.setToolTipText("#");
                button.setOpaque(false);
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setBorder(BorderFactory.createEmptyBorder(5,1,5,1));
                grid_panel.add(button);
                this.grid[row][col] = button;
            }
        }
        final JLabel label1 = new JLabel("Depth");
        settings_panel.add(label1);
        final JComboBox<Integer> depthBox = new JComboBox<>(new Integer[]{2,3,4,5,6,7});
        depthBox.addActionListener(e -> {
            depth = (Integer) (depthBox.getSelectedItem());
            aiAgent = new Minimax(depth);
        });
        settings_panel.add(depthBox);
        final JCheckBox pruning = new JCheckBox("Use pruning?", isPruning);
        pruning.addActionListener(e -> isPruning = pruning.isSelected());
        settings_panel.add(pruning);
        final Container content = this.getContentPane();
        content.setLayout (new BorderLayout ());
        content.add(settings_panel, BorderLayout.SOUTH);
        content.add(control_panel, BorderLayout.NORTH);
        content.add(grid_panel, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        this.setVisible (true);
        for (int col = 0; col < 7; col++) {
            final JButton button = new JButton ("Drop");
            button.setActionCommand("" + col);
            button.addActionListener(this);
            control_panel.add(button);
        }
    }

    public void actionPerformed (ActionEvent e) {
        try {
            boolean played = false;
            final int col = Integer.parseInt (e.getActionCommand());
            for (int row = 0; row <= 5; row++) {
                if (this.grid[5 - row][col].getToolTipText().equals("#")) {
                    played = true;
                    this.grid[5 - row][col].setToolTipText("o");
                    this.grid[5 - row][col].setIcon
                            (new ImageIcon("src/player1.png")
                            );
                    int index = indexInString(row, col);
                    state = state.substring(0, index) + 'o' + state.substring(index + 1);
                    break;
                }
            }
            if (!played) return;
            Minimax.State currState = new Minimax.State(0, true, state);
            if (!isPruning) {
                aiAgent.value(currState);
            }
            else {
                aiAgent.abValue(currState, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
            state = currState.next.boardState;
            for (int i = 0; i <= 5; i++) {
                for (int j = 0; j <= 6; j++) {
                    this.grid[5 - i][j].setToolTipText(String.valueOf(state.charAt(indexInString(i, j))));
                    this.grid[5 - i][j].setIcon
                            (state.charAt(indexInString(i, j)) == 'o' ?
                                    new ImageIcon("src/player1.png") :
                                    state.charAt(indexInString(i, j)) == 'a' ?
                                            new ImageIcon("src/player2.png") :
                                            new ImageIcon("src/noPlayer.png")
                            );
                }
            }

            if (!state.contains("#")) {
                printScore(this);
            }
        }
        catch (final NumberFormatException ex) {
            throw new RuntimeException("Incorrect Input.");
        }
    }

    private int indexInString (int x, int y) {
        return x + 6 * y;
    }

    private void printScore(GUI gui){
        int agScore = 0, oppScore = 0;
        String state = gui.state;
        List<String> neighborCells = aiAgent.getNeighborCells(state);
        for(String neighborCell: neighborCells){
            if(neighborCell.equals("aaaa")) agScore += 1;
            if(neighborCell.equals("oooo")) oppScore += 1;
        }
        if((agScore - oppScore) > 0) {
            JOptionPane.showMessageDialog(gui, "Agent wins :)" + " agent score: " + agScore +
                                                        " opponent score: " + oppScore);
        }
        else if((agScore - oppScore) < 0) {
            JOptionPane.showMessageDialog(gui, "Agent loses :( Score: " + " agent score: " + agScore +
                                                        " opponent score: " + oppScore);
        }
        else {
            JOptionPane.showMessageDialog(gui, "Draw :| Score: " + " agent score: " + agScore +
                                                        " opponent score: " + oppScore);
        }
    }

    public static void main (String[] args) {
        new GUI();
    }

}
