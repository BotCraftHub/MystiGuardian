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
package io.github.yusufsdiscordbot.mystiguardian.api.job;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Core interface representing an apprenticeship opportunity.
 *
 * <p>This interface provides a common contract for different apprenticeship sources
 * such as Higher In (Rate My Apprenticeship) and GOV.UK Find an Apprenticeship.
 *
 * <p>Implementations must provide apprenticeship details including company information,
 * location, salary, and application deadlines. The interface also requires implementations
 * to generate Discord message embeds for displaying apprenticeship information.
 *
 * @see HigherinApprenticeship
 * @see FindAnApprenticeship
 * @see ApprenticeshipSource
 */
public interface Apprenticeship {
    /**
     * Gets the unique identifier for this apprenticeship.
     *
     * @return the apprenticeship ID, never null
     */
    String getId();

    /**
     * Gets the title/name of the apprenticeship position.
     *
     * @return the apprenticeship title
     */
    String getTitle();

    /**
     * Gets the name of the company offering this apprenticeship.
     *
     * @return the company name
     */
    String getCompanyName();

    /**
     * Gets the location where the apprenticeship will be based.
     *
     * @return the apprenticeship location (e.g., "London", "Remote")
     */
    String getLocation();

    /**
     * Gets the salary information for this apprenticeship.
     *
     * @return the salary details (e.g., "£25,000 - £30,000")
     */
    String getSalary();

    /**
     * Gets the application closing date for this apprenticeship.
     *
     * @return the closing date, or null if not available
     */
    LocalDate getClosingDate();

    /**
     * Gets the URL to the full apprenticeship listing.
     *
     * @return the apprenticeship URL
     */
    String getUrl();

    /**
     * Generates a Discord embed representation of this apprenticeship.
     *
     * <p>The embed should contain formatted information about the apprenticeship
     * including title, company, location, salary, and relevant dates.
     *
     * @return a MessageEmbed containing formatted apprenticeship details
     */
    MessageEmbed getEmbed();

    /**
     * Gets the categories/tags associated with this apprenticeship.
     *
     * <p>Categories may include fields like "Software Development", "Data Science",
     * "Engineering", etc. Default implementation returns an empty list.
     *
     * @return a list of category names, empty list if not applicable
     */
    default List<String> getCategories() {
        return Collections.emptyList();
    }
}
