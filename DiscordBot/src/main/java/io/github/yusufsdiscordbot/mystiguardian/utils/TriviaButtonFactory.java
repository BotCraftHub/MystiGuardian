/*
 * Copyright 2024 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.components.buttons.Button;

public class TriviaButtonFactory {

    private static final int MAX_BUTTON_LABEL_LENGTH = 80;

    /**
     * Creates a list of buttons from a list of answers. If any answer exceeds the maximum button
     * label length, it will be split into multiple buttons.
     *
     * @param allAnswers List of answers to convert into buttons.
     * @return List of created buttons.
     */
    public List<Button> createAnswerButtons(List<String> allAnswers) {
        List<Button> buttons = new ArrayList<>();

        for (String answer : allAnswers) {
            if (answer.length() <= MAX_BUTTON_LABEL_LENGTH) {
                // Add button with single label if it fits within the maximum length
                buttons.add(createButton(answer));
            } else {
                // Split long labels into multiple buttons
                buttons.addAll(splitLongLabel(answer));
            }
        }

        return buttons;
    }

    /**
     * Creates a button with the given label.
     *
     * @param label The label of the button.
     * @return The created button.
     */
    private Button createButton(String label) {
        return Button.primary("trivia:" + label, label);
    }

    /**
     * Splits a long label into multiple buttons with shorter labels.
     *
     * @param longLabel The long label to split.
     * @return A list of buttons with split labels.
     */
    private List<Button> splitLongLabel(String longLabel) {
        List<Button> buttons = new ArrayList<>();
        int length = longLabel.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + MAX_BUTTON_LABEL_LENGTH, length);
            String part = longLabel.substring(start, end);
            buttons.add(createButton(part));
            start = end;
        }

        return buttons;
    }
}
