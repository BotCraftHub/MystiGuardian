/*
 * Copyright 2025 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ReviewEvaluator {

    @NotNull
    public static String evaluate(@NotNull List<JsonNode> allReviews) {
        int totalReviews = 0;
        int oneTwoStars = 0, threeFourStars = 0, fiveStars = 0;

        StringBuilder highlights = new StringBuilder("Review Highlights:\n");

        for (JsonNode reviews : allReviews) {
            int sourceReviewCount = reviews.size();
            totalReviews += sourceReviewCount;

            for (JsonNode review : reviews) {
                int rating = review.get("rating").asInt();
                String reviewText = review.get("text").asText();

                if (rating <= 2) {
                    oneTwoStars++;
                    if (oneTwoStars <= 3) { // Add up to 3 low-rating highlights
                        highlights.append("- Low Rating: ").append(reviewText).append("\n");
                    }
                } else if (rating <= 4) {
                    threeFourStars++;
                    if (threeFourStars <= 3) { // Add up to 3 mid-rating highlights
                        highlights.append("- Medium Rating: ").append(reviewText).append("\n");
                    }
                } else {
                    fiveStars++;
                    if (fiveStars <= 3) { // Add up to 3 high-rating highlights
                        highlights.append("- High Rating: ").append(reviewText).append("\n");
                    }
                }
            }
        }

        double lowRatingRatio = (double) oneTwoStars / totalReviews;
        double midRatingRatio = (double) threeFourStars / totalReviews;
        double highRatingRatio = (double) fiveStars / totalReviews;

        String recommendation = generateRecommendation(lowRatingRatio, midRatingRatio, highRatingRatio);

        return recommendation + "\n" + highlights.toString();
    }

    @NotNull
    private static String generateRecommendation(
            double lowRatingRatio, double midRatingRatio, double highRatingRatio) {
        if (lowRatingRatio > 0.5) {
            return "Not Recommended";
        } else if (highRatingRatio > 0.5) {
            return "Highly Recommended";
        } else if (midRatingRatio > 0.5) {
            return "Recommended with Caution";
        } else {
            return "Mixed Reviews - Proceed with Caution";
        }
    }
}
