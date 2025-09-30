package com.ats;

import org.apache.tika.Tika;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ResumeATSApp extends JFrame {

    private JTextArea resumeTextArea;
    private JTextArea jobDescriptionTextArea;
    private JButton uploadResumeButton;
    private JButton submitButton;
    private JLabel atsScoreLabel;

    // Common stop words to ignore in keyword extraction
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "for", "are", "but", "not", "you", "with", "this", "that",
            "from", "they", "have", "has", "had", "will", "would", "can", "could",
            "our", "their", "what", "which", "when", "where", "who", "whom", "your",
            "all", "any", "there", "were", "been", "was", "his", "her", "him", "she",
            "himself", "herself", "them", "then", "than", "also", "its",
            "about", "into", "some", "more", "other", "such", "only", "own", "same",
            "so", "too", "very"
    );

    public ResumeATSApp() {
        setTitle("Resume Parser & ATS Scorer");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        setContentPane(mainPanel);

        // Resume display area (non-editable)
        resumeTextArea = new JTextArea(10, 40);
        resumeTextArea.setEditable(false);
        JScrollPane resumeScroll = new JScrollPane(resumeTextArea);
        resumeScroll.setBorder(BorderFactory.createTitledBorder("Resume Text"));
        mainPanel.add(resumeScroll, BorderLayout.NORTH);

        // Job description input area
        jobDescriptionTextArea = new JTextArea(5, 40);
        jobDescriptionTextArea.setText("Enter job description here...");
        jobDescriptionTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jobDescriptionTextArea.getText().equals("Enter job description here...")) {
                    jobDescriptionTextArea.setText("");
                }
            }
        });
        JScrollPane jobDescScroll = new JScrollPane(jobDescriptionTextArea);
        jobDescScroll.setBorder(BorderFactory.createTitledBorder("Job Description"));
        mainPanel.add(jobDescScroll, BorderLayout.CENTER);

        // Buttons and score label panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        uploadResumeButton = new JButton("Upload Resume");
        uploadResumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadResumeButton.addActionListener(this::handleUploadResume);
        sidePanel.add(uploadResumeButton);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        submitButton = new JButton("Calculate ATS Score");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(this::handleCalculateScore);
        sidePanel.add(submitButton);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        atsScoreLabel = new JLabel("ATS Score: 0%", JLabel.CENTER);
        atsScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        atsScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(atsScoreLabel);

        mainPanel.add(sidePanel, BorderLayout.EAST);
    }

    private void handleUploadResume(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String parsedText = parseResume(file.getAbsolutePath());
            if (parsedText.startsWith("Error")) {
                JOptionPane.showMessageDialog(this, parsedText, "Parsing Error", JOptionPane.ERROR_MESSAGE);
            } else {
                resumeTextArea.setText(parsedText);
            }
        }
    }

    private void handleCalculateScore(ActionEvent e) {
        String resumeText = resumeTextArea.getText();
        String jobDescription = jobDescriptionTextArea.getText();
        if (resumeText.isBlank() || jobDescription.isBlank() || jobDescription.equals("Enter job description here...")) {
            JOptionPane.showMessageDialog(this, "Please upload a resume and enter a job description.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int atsScore = calculateScore(resumeText, jobDescription);
        atsScoreLabel.setText("ATS Score: " + atsScore + "%");
    }

    // Parse resume file text using Apache Tika
    public String parseResume(String filePath) {
        Tika tika = new Tika();
        try {
            File file = new File(filePath);
            return tika.parseToString(file);
        } catch (IOException | org.apache.tika.exception.TikaException e) {
            return "Error parsing resume: " + e.getMessage();
        }
    }

    // Extract keywords from text excluding stop words and very short words
    public Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        String[] words = text.toLowerCase().split("\\W+");
        for (String word : words) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                keywords.add(word);
            }
        }
        return keywords;
    }

    // Calculate ATS score as percentage of matching keywords over job description keywords
    public int calculateScore(String resumeText, String jobDescription) {
        Set<String> resumeKeywords = extractKeywords(resumeText);
        Set<String> jobKeywords = extractKeywords(jobDescription);

        if (jobKeywords.isEmpty()) return 0;

        resumeKeywords.retainAll(jobKeywords); // keep only common keywords

        double score = ((double) resumeKeywords.size() / jobKeywords.size()) * 100;
        return (int) Math.round(score);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ResumeATSApp app = new ResumeATSApp();
            app.setVisible(true);
        });
    }
}
