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
package io.github.yusufsdiscordbot.mystiguardian.urls;

import lombok.Getter;

@Getter
public enum APIUrls {
    BOOKING("https://supply-xml.booking.com"),
    TRIP_ADVISOR("https://api.content.tripadvisor.com/api/v1"),
    TRUST_PILOT("https://api.trustpilot.com/v1"),
    SERP_API("https://serpapi.com/search");

    private final String url;

    APIUrls(String url) {
        this.url = url;
    }
}
