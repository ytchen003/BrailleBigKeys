package com.example.braillebigkeys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView outputText;
    private StringBuilder userInput = new StringBuilder();
    private StringBuilder currentBraillePattern = new StringBuilder();
    private boolean isNumberMode = false;
    private boolean isUppercaseMode = false;

    // Braille character mappings
    private static final Map<String, String> BRAILLE_MAP = new HashMap<>();
    private static final Map<String, String> NUMBER_MAP = new HashMap<>();
    private static final Map<String, String> SYMBOL_MAP = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeBrailleMappings();

        outputText = findViewById(R.id.outputText);

        // Initialize buttons
        setupButtons();
        updateButtonLabels();
    }

    private void setupButtons() {
        // Dot buttons
        int[] dotButtons = {R.id.dot_1, R.id.dot_2, R.id.dot_3, R.id.dot_4, R.id.dot_5, R.id.dot_6};
        for (int i = 0; i < dotButtons.length; i++) {
            final int dotNumber = i + 1;
            findViewById(dotButtons[i]).setOnClickListener(v -> addDot(String.valueOf(dotNumber)));
        }

        // Function buttons
        findViewById(R.id.submit_button).setOnClickListener(v -> submitBrailleCharacter());
        findViewById(R.id.space_button).setOnClickListener(v -> addSpace());
        findViewById(R.id.delete_button).setOnClickListener(v -> deleteLastCharacter());
        findViewById(R.id.mode_switch).setOnClickListener(v -> toggleNumberMode());
        findViewById(R.id.caps_button).setOnClickListener(v -> toggleUppercaseMode());
    }

    private void initializeBrailleMappings() {
        // Letters (a-z)
        BRAILLE_MAP.put("1", "a");
        BRAILLE_MAP.put("12", "b");
        BRAILLE_MAP.put("14", "c");
        BRAILLE_MAP.put("145", "d");
        BRAILLE_MAP.put("15", "e");
        BRAILLE_MAP.put("124", "f");
        BRAILLE_MAP.put("1245", "g");
        BRAILLE_MAP.put("125", "h");
        BRAILLE_MAP.put("24", "i");
        BRAILLE_MAP.put("245", "j");
        BRAILLE_MAP.put("13", "k");
        BRAILLE_MAP.put("123", "l");
        BRAILLE_MAP.put("134", "m");
        BRAILLE_MAP.put("1345", "n");
        BRAILLE_MAP.put("135", "o");
        BRAILLE_MAP.put("1234", "p");
        BRAILLE_MAP.put("12345", "q");
        BRAILLE_MAP.put("1235", "r");
        BRAILLE_MAP.put("234", "s");
        BRAILLE_MAP.put("2345", "t");
        BRAILLE_MAP.put("136", "u");
        BRAILLE_MAP.put("1236", "v");
        BRAILLE_MAP.put("2456", "w");
        BRAILLE_MAP.put("1346", "x");
        BRAILLE_MAP.put("13456", "y");
        BRAILLE_MAP.put("1356", "z");

        // Numbers (prefixed with number sign ⠼)
        NUMBER_MAP.put("1", "1");
        NUMBER_MAP.put("12", "2");
        NUMBER_MAP.put("14", "3");
        NUMBER_MAP.put("145", "4");
        NUMBER_MAP.put("15", "5");
        NUMBER_MAP.put("124", "6");
        NUMBER_MAP.put("1245", "7");
        NUMBER_MAP.put("125", "8");
        NUMBER_MAP.put("24", "9");
        NUMBER_MAP.put("245", "0");

        // Common symbols
        SYMBOL_MAP.put("3", ",");
        SYMBOL_MAP.put("2", ";");
        SYMBOL_MAP.put("23", ":");
        SYMBOL_MAP.put("25", ".");
        SYMBOL_MAP.put("26", "?");
        SYMBOL_MAP.put("235", "!");
        SYMBOL_MAP.put("6", "-");
        SYMBOL_MAP.put("36", "\"");
        SYMBOL_MAP.put("356", "'");
        SYMBOL_MAP.put("236", "(");
    }

    private void addDot(String dot) {
        if (!currentBraillePattern.toString().contains(dot)) {
            currentBraillePattern.append(dot);
            updateButtonLabels();
            updateOutputText();  // Update the output text immediately when a dot is added
        }
    }

    private void submitBrailleCharacter() {
        if (currentBraillePattern.length() > 0) {
            String pattern = currentBraillePattern.toString();
            String brailleChar = convertToBraille(pattern);

            if (brailleChar != null && !brailleChar.isEmpty()) {
                if (isUppercaseMode && !isNumberMode) {
                    brailleChar = brailleChar.toUpperCase();
                    isUppercaseMode = false; // Auto-disable caps after one character
                    updateButtonLabels();
                }
                userInput.append(brailleChar);
                outputText.setText(userInput.toString());
            }

            currentBraillePattern.setLength(0);
            updateButtonLabels();
        }
    }

    private String convertToBraille(String pattern) {
        if (isNumberMode) {
            // Number sign (dots 3-4-5-6) must come before numbers
            if (!userInput.toString().endsWith("⠼") && NUMBER_MAP.containsKey(pattern)) {
                return "⠼" + NUMBER_MAP.get(pattern);
            }
            // Use containsKey to safely get the value or return an empty string
            return NUMBER_MAP.containsKey(pattern) ? NUMBER_MAP.get(pattern) : "";
        }

        // Use containsKey instead of getOrDefault to be compatible with lower API levels
        if (BRAILLE_MAP.containsKey(pattern)) {
            return BRAILLE_MAP.get(pattern);
        } else if (SYMBOL_MAP.containsKey(pattern)) {
            return SYMBOL_MAP.get(pattern);
        }
        return "";
    }

    private void addSpace() {
        userInput.append(" ");
        outputText.setText(userInput.toString());
    }

    private void deleteLastCharacter() {
        if (userInput.length() > 0) {
            userInput.deleteCharAt(userInput.length() - 1);
            outputText.setText(userInput.toString());
        }
    }

    private void toggleNumberMode() {
        isNumberMode = !isNumberMode;
        currentBraillePattern.setLength(0);
        updateButtonLabels();
    }

    private void toggleUppercaseMode() {
        isUppercaseMode = !isUppercaseMode;
        updateButtonLabels();
    }

    private void updateButtonLabels() {
        Button modeSwitchBtn = findViewById(R.id.mode_switch);
        Button capsBtn = findViewById(R.id.caps_button);

        // Update mode switch button label
        modeSwitchBtn.setText(isNumberMode ? "ABC" : "123");

        // Update caps button appearance
        if (capsBtn != null) {
            capsBtn.setBackgroundResource(isUppercaseMode ?
                    R.drawable.caps_lock_on : R.drawable.caps_lock_off);
        }

        // Update dot buttons to show selected dots
        int[] dotButtons = {R.id.dot_1, R.id.dot_2, R.id.dot_3,
                R.id.dot_4, R.id.dot_5, R.id.dot_6};

        for (int i = 0; i < dotButtons.length; i++) {
            Button dotBtn = findViewById(dotButtons[i]);
            if (dotBtn != null) {
                String dotNumber = String.valueOf(i+1);
                boolean isActive = currentBraillePattern.toString().contains(dotNumber);
                dotBtn.setBackgroundResource(
                        isActive ? R.drawable.dot_active : R.drawable.dot_inactive);
            }
        }
    }

    private void updateOutputText() {
        // Get the Braille character based on the current pattern and update the TextView
        String brailleChar = convertToBraille(currentBraillePattern.toString());
        if (!brailleChar.isEmpty()) {
            outputText.setText(userInput.toString() + brailleChar); // Display the full input with the current character
        } else {
            outputText.setText(userInput.toString() + "(Incomplete)"); // Show incomplete if pattern is not valid
        }
    }
}
