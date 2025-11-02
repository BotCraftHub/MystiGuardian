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
package io.github.yusufsdiscordbot.mystiguardian.config;

import com.google.api.services.sheets.v4.Sheets;
import java.util.List;

/**
 * Configuration record for Digital Apprenticeship (DA) scraping and posting.
 *
 * <p>This configuration contains all necessary information for:
 * <ul>
 *   <li>Discord guild and channel targets for posting apprenticeships</li>
 *   <li>Google Sheets service for storing apprenticeship data</li>
 *   <li>Spreadsheet ID for tracking apprenticeships</li>
 * </ul>
 *
 * @param guildChannels list of guild-channel pairs where apprenticeships should be posted
 * @param sheetsService the Google Sheets API service instance
 * @param spreadsheetId the ID of the Google Spreadsheet for apprenticeship tracking
 *
 * @see ApprenticeshipSpreadsheetManager
 */
public record DAConfig(
        List<GuildChannelConfig> guildChannels, Sheets sheetsService, String spreadsheetId) {

    /**
     * Configuration for a specific Discord guild and channel pair.
     *
     * <p>Represents a target location where apprenticeship announcements
     * should be posted.
     *
     * @param guildId the Discord guild (server) ID
     * @param channelId the Discord text channel ID within that guild
     */
    public record GuildChannelConfig(long guildId, long channelId) {}
}
