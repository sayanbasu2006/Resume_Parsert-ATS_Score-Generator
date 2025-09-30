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
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Resume display area (non-editable)
        resumeTextArea = new JTextArea(10, 40);
        resumeTextArea.setEditable(false);
        JScrollPane resumeScroll = new JScrollPane(resumeTextArea);
        add(resumeScroll, BorderLayout.NORTH);

        // Job description input area
        jobDescriptionTextArea = new JTextArea(5, 40);
        jobDescriptionTextArea.setText("Enter job description here...");
        JScrollPane jobDescScroll = new JScrollPane(jobDescriptionTextArea);
        add(jobDescScroll, BorderLayout.CENTER);

        // Upload resume button
        uploadResumeButton = new JButton("Upload Resume");
        uploadResumeButton.addActionListener(this::handleUploadResume);
        add(uploadResumeButton, BorderLayout.WEST);

        // Submit button for calculating ATS score
        submitButton = new JButton("Calculate ATS Score");
        submitButton.addActionListener(this::handleCalculateScore);
        add(submitButton, BorderLayout.SOUTH);

        // Label to display ATS score
        atsScoreLabel = new JLabel("ATS Score: 0%", JLabel.CENTER);
        add(atsScoreLabel, BorderLayout.EAST);
    }

    private void handleUploadResume(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String parsedText = parseResume(file.getAbsolutePath());
            resumeTextArea.setText(parsedText);
        }
    }

    private void handleCalculateScore(ActionEvent e) {
        String resumeText = resumeTextArea.getText();
        String jobDescription = jobDescriptionTextArea.getText();
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
            e.printStackTrace();
        }
        return "Error parsing resume!";
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
