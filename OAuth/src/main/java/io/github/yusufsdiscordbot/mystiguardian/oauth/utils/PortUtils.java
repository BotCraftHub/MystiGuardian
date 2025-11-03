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
package io.github.yusufsdiscordbot.mystiguardian.oauth.utils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Utility class for finding available network ports.
 *
 * <p>This class provides methods to dynamically discover open ports for server binding.
 */
public class PortUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private PortUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Finds an available port in the specified range.
     *
     * @param startPort the starting port number
     * @param endPort the ending port number
     * @return the first available port in the range
     * @throws IOException if no available port is found in the range
     */
    public static int findOpenPort(int startPort, int endPort) throws IOException {
        for (int port = startPort; port <= endPort; port++) {
            try (ServerSocket socket = new ServerSocket(port)) {
                return port; // Port is available
            } catch (IOException e) {
                // Port is in use, try the next one
            }
        }
        throw new IOException("No available port found in the range.");
    }
}
