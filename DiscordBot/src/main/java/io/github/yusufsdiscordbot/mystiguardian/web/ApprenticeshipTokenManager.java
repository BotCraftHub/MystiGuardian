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
package io.github.yusufsdiscordbot.mystiguardian.web;

import java.lang.reflect.Method;

/**
 * Utility class to generate access tokens for the apprenticeship web viewer. Uses reflection to
 * call the OAuth module's ApprenticeshipRequestsHandler.
 */
public class ApprenticeshipTokenManager {

    public static String generateAccessToken() {
        try {
            Class<?> handlerClass =
                    Class.forName(
                            "io.github.yusufsdiscordbot.mystiguardian.oauth.requests.ApprenticeshipRequestsHandler");
            Method generateMethod = handlerClass.getMethod("generateAccessToken");
            return (String) generateMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate access token. Make sure the OAuth module is running.", e);
        }
    }
}
