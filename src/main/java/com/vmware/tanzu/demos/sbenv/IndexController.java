/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.sbenv;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class IndexController {
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> indexJson() {
        // Get environment variables sorted by key.
        return new TreeMap<>(System.getenv());
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    StringBuilder indexPlainText() {
        // Build a String containing environment variables sorted by key.
        final var sb = new StringBuilder(1024);
        final var env = new TreeMap<>(System.getenv());
        env.forEach((key, value) -> sb.append(key).append("=").append(value).append("\n"));
        return sb;
    }
}
