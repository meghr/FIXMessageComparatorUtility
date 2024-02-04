package org.example;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FIXMessageComparator extends JFrame {

    private JTextArea fixMessages1TextArea;
    private JTextArea fixMessages2TextArea;
    private JTextField separatorTextField;

    public FIXMessageComparator() {
        setTitle("FIX Message Comparator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JScrollPane resultScrollPane = new JScrollPane();
        add(resultScrollPane, BorderLayout.CENTER);

        JButton compareButton = new JButton("Compare Messages");
        compareButton.addActionListener(e -> compareMessages());

        JButton exportToJPEGButton = new JButton("Export to JPEG");
        exportToJPEGButton.addActionListener(e -> exportToJPEG(resultScrollPane.getViewport().getView()));

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetInputFields());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(compareButton);
        buttonPanel.add(exportToJPEGButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));

        JLabel separatorLabel = new JLabel("Separator (leave blank for default \" \"):");
        separatorTextField = new JTextField();
        inputPanel.add(separatorLabel);
        inputPanel.add(separatorTextField);

        JLabel fixMessages1Label = new JLabel("FIX Messages 1:");
        fixMessages1TextArea = new JTextArea(5, 50);
        JScrollPane fixMessages1ScrollPane = new JScrollPane(fixMessages1TextArea);
        inputPanel.add(fixMessages1Label);
        inputPanel.add(fixMessages1ScrollPane);

        JLabel fixMessages2Label = new JLabel("FIX Messages 2:");
        fixMessages2TextArea = new JTextArea(5, 50);
        JScrollPane fixMessages2ScrollPane = new JScrollPane(fixMessages2TextArea);
        inputPanel.add(fixMessages2Label);
        inputPanel.add(fixMessages2ScrollPane);

        return inputPanel;
    }

    private void compareMessages() {
        String separator = separatorTextField.getText().trim();
        if (separator.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No separator provided. Using default separator: ' ' (space).");
            separator = " ";
        }

        String[] fixMessages1 = fixMessages1TextArea.getText().trim().split("\n");
        String[] fixMessages2 = fixMessages2TextArea.getText().trim().split("\n");

        JPanel resultPanel = new JPanel(new GridLayout(Math.max(fixMessages1.length, fixMessages2.length), 1));

        for (int i = 0; i < Math.max(fixMessages1.length, fixMessages2.length); i++) {
            String message1 = (i < fixMessages1.length) ? fixMessages1[i] : "";
            String message2 = (i < fixMessages2.length) ? fixMessages2[i] : "";

            JPanel comparisonPanel = createComparisonPanel(message1, message2, separator);
            resultPanel.add(comparisonPanel);
        }

        JScrollPane resultScrollPane = (JScrollPane) getContentPane().getComponent(1);
        resultScrollPane.setViewportView(resultPanel);
        revalidate();
        repaint();
    }





    private JPanel createComparisonPanel(String message1, String message2, String separator) {
        JPanel comparisonPanel = new JPanel(new BorderLayout());
        comparisonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        Map<String, String> tags1 = extractTagsAndValues(message1, separator);
        Map<String, String> tags2 = extractTagsAndValues(message2, separator);

        String[][] data = new String[tags1.size()][4];
        int index = 0;
        for (String tag : tags1.keySet()) {
            String value1 = tags1.get(tag);
            String value2 = tags2.getOrDefault(tag, "");
            String result = (value1.equals(value2)) ? "Match" : "Mismatch";
            data[index][0] = tag;
            data[index][1] = value1;
            data[index][2] = value2;
            data[index][3] = result;
            index++;
        }

        String[] columnNames = {"Tag", "Value in Message 1", "Value in Message 2", "Result"};
        JTable comparisonTable = new JTable(data, columnNames);
        comparisonTable.getColumnModel().getColumn(3).setCellRenderer(new CustomTableCellRenderer());

        comparisonPanel.add(new JScrollPane(comparisonTable), BorderLayout.CENTER);
        return comparisonPanel;
    }

    private Map<String, String> extractTagsAndValues(String fixMessage, String separator) {
        Map<String, String> tags = new HashMap<>();
        String[] fields = fixMessage.split("\\|");
        for (String field : fields) {
            String[] parts = field.split("=");
            if (parts.length == 2) {
                tags.put(parts[0], parts[1]);
            }
        }
        return tags;
    }

    private void exportToJPEG(Component component) {
        try {
            // Create a temporary file
            File tempFile = File.createTempFile("fix_comparison", ".jpeg");

            // Export the component to a JPEG image
            Rectangle rect = component.getBounds();
            BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
            component.paint(image.getGraphics());
            ImageIO.write(image, "jpeg", tempFile);

            // Open the exported JPEG image
            Desktop.getDesktop().open(tempFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void resetInputFields() {
        fixMessages1TextArea.setText("");
        fixMessages2TextArea.setText("");
        separatorTextField.setText("");

        // Clear result section
        JScrollPane resultScrollPane = (JScrollPane) getContentPane().getComponent(1);
        resultScrollPane.setViewportView(new JPanel());

        revalidate();
        repaint();
    }

    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null && column == 3) {
                String result = value.toString();
                if (result.equals("Mismatch")) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.GREEN);
                }
            } else {
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FIXMessageComparator().setVisible(true));
    }
}
