package it.ned.bookmanager.view.swing;

import javax.swing.*;
import java.awt.*;

public class BookManagerSwingView extends JFrame {

    private final JPanel mainPanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                BookManagerSwingView frame = new BookManagerSwingView();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public BookManagerSwingView() {
        setPreferredSize(new Dimension(700, 500));
        setTitle("Book Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);
    }
}
