package Questions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DeepSeekMCQGenerator {

    // Using OpenRouter.ai API
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    // Make sure your actual API Key is here. It looks correct in your provided code.
    private static final String API_KEY = "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";


    // MCQ class - now using private fields with public getters and 'char' for correct answer
    public static class MCQ {
        private String question;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private char correctAnswer;
        private int id;
        private String course;

        public MCQ(String question, String optionA, String optionB, String optionC, String optionD, char correctAnswer) {
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        // Getters
        public String getQuestion() { return question; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public char getCorrectAnswer() { return correctAnswer; }
        public int getId() { return id; }
        public String getCourse() { return course; } // Ensure this getter also exists
        public void setId(int id) { this.id = id; }

        @Override
        public String toString() {
            String displayA = optionA;
            String displayB = optionB;
            String displayC = optionC;
            String displayD = optionD;

            switch (correctAnswer) {
                case 'A': displayA = "'" + optionA + "'"; break;
                case 'B': displayB = "'" + optionB + "'"; break;
                case 'C': displayC = "'" + optionC + "'"; break;
                case 'D': displayD = "'" + optionD + "'"; break;
            }

            return "Q: " + question +
                    "\nA. " + displayA +
                    "\nB. " + displayB +
                    "\nC. " + displayC +
                    "\nD. " + displayD +
                    "\nCorrect Answer: " + correctAnswer;
        }
    }

    public static List<MCQ> generateMCQs(String subject, int count) {
        List<MCQ> mcqs = new ArrayList<>();

        // CORRECTED LINE: Check against the placeholder string, not your actual key.
        if (API_KEY.equals("YOUR_OPENROUTER_API_KEY")) {
            System.err.println("❌ OpenRouter API Key is not set. Please replace 'YOUR_OPENROUTER_API_KEY' with your actual key in DeepSeekMCQGenerator.java.");
            return mcqs;
        }

        try {
            String prompt = "Generate " + count + " multiple choice questions for subject: " + subject + ".\n" +
                    "Each question must have exactly four options labeled A, B, C, D, and a single correct answer letter.\n" +
                    "Strictly follow this format for each question, including the 'Question:', 'A.', 'B.', 'C.', 'D.', and 'Correct Answer:' prefixes.\n" +
                    "Separate each question block with two newlines.\n\n" +
                    "Example:\n" +
                    "Question: What is the capital of France?\n" +
                    "A. Berlin\n" +
                    "B. Madrid\n" +
                    "C. Paris\n" +
                    "D. Rome\n" +
                    "Correct Answer: C\n\n" +
                    "Generate the " + count + " questions now:";

            JSONObject payload = new JSONObject();
            payload.put("model", "mistralai/mistral-7b-instruct");
            payload.put("messages", new JSONArray()
                    .put(new JSONObject().put("role", "user").put("content", prompt))
            );

            HttpURLConnection con = (HttpURLConnection) new URL(API_URL).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + API_KEY);
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(payload.toString().getBytes());
                os.flush();
            }

            int responseCode = con.getResponseCode();
            System.out.println("DEBUG: OpenRouter API Response Code: " + responseCode);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode == HttpURLConnection.HTTP_OK ? con.getInputStream() : con.getErrorStream()
            ))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                String fullResponse = response.toString();
                System.out.println("DEBUG: Full OpenRouter API Response (JSON): " + fullResponse);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String content = new JSONObject(fullResponse)
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    System.out.println("DEBUG: AI Raw Content: \n" + content);

                    String[] qBlocks = content.trim().split("\n\n");
                    for (String qBlock : qBlocks) {
                        String[] lines = qBlock.split("\n");
                        if (lines.length >= 6) {
                            String questionText = "";
                            String optionA = "";
                            String optionB = "";
                            String optionC = "";
                            String optionD = "";
                            char correctChar = ' ';

                            for (String currentLine : lines) {
                                if (currentLine.startsWith("Question:")) {
                                    questionText = currentLine.replaceFirst("Question:", "").trim();
                                } else if (currentLine.startsWith("A.")) {
                                    optionA = currentLine.replaceFirst("A\\.", "").trim();
                                } else if (currentLine.startsWith("B.")) {
                                    optionB = currentLine.replaceFirst("B\\.", "").trim();
                                } else if (currentLine.startsWith("C.")) {
                                    optionC = currentLine.replaceFirst("C\\.", "").trim();
                                } else if (currentLine.startsWith("D.")) {
                                    optionD = currentLine.replaceFirst("D\\.", "").trim();
                                } else if (currentLine.startsWith("Correct Answer:")) {
                                    String correctStr = currentLine.replaceFirst("Correct Answer:", "").trim().toUpperCase();
                                    if (correctStr.length() == 1 && correctStr.matches("[ABCD]")) {
                                        correctChar = correctStr.charAt(0);
                                    } else {
                                        System.err.println("⚠️ Invalid correct answer format in AI response: '" + correctStr + "'");
                                    }
                                }
                            }

                            if (!questionText.isEmpty() && !optionA.isEmpty() && !optionB.isEmpty() &&
                                    !optionC.isEmpty() && !optionD.isEmpty() && correctChar != ' ') {
                                mcqs.add(new MCQ(questionText, optionA, optionB, optionC, optionD, correctChar));
                            } else {
                                System.err.println("⚠️ Skipped malformed question block (missing essential fields):\n" + qBlock);
                            }
                        } else {
                            System.err.println("⚠️ Skipped incomplete question block (less than 6 lines):\n" + qBlock);
                        }
                    }

                } else {
                    System.err.println("❌ API request failed with status code: " + responseCode);
                    System.err.println("Error details: " + fullResponse);

                    if (responseCode == 402) {
                        System.err.println("Hint: Error 402 usually means 'Payment Required' or 'Quota Exceeded'. Check your OpenRouter.ai account usage.");
                    } else if (responseCode == 401) {
                        System.err.println("Hint: Error 401 means 'Unauthorized'. Check your API Key.");
                    } else if (responseCode == 429) {
                        System.err.println("Hint: Error 429 means 'Too Many Requests'. You might be hitting rate limits.");
                    }
                }

            }
        } catch (Exception e) {
            System.err.println("❌ Failed to generate questions (Exception): " + e.getMessage());
            e.printStackTrace();
        }

        return mcqs;
    }
}