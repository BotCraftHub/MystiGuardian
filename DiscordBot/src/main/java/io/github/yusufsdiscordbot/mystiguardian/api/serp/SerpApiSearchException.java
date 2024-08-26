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
package io.github.yusufsdiscordbot.mystiguardian.api.serp;

/**
 * This class is adapted from [<a href="https://github.com/serpapi/google-search-results-java">serp
 * api</a>] The original code is licensed under the MIT License. See the LICENSE file in the project
 * root for more information.
 */
public class SerpApiSearchException extends Exception {
    /** Constructor */
    public SerpApiSearchException() {
        super();
    }

    /**
     * Constructor
     *
     * @param exception original exception
     */
    public SerpApiSearchException(Exception exception) {
        super(exception);
    }

    /**
     * Constructor
     *
     * @param message short description of the root cause
     */
    public SerpApiSearchException(String message) {
        super(message);
    }
}
