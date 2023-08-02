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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
class BindingsController {
    private final Logger logger = LoggerFactory.getLogger(BindingsController.class);

    @GetMapping("/bindings")
    ResponseEntity<Map<String, Map<String, String>>> dumpBindings() {
        final String bindingRoot = System.getenv("SERVICE_BINDING_ROOT");
        if (!StringUtils.hasLength(bindingRoot)) {
            logger.info("No bindings found");
            return ResponseEntity.noContent().build();
        }
        final var bindingRootDir = new File(bindingRoot);
        if (!bindingRootDir.exists() || !bindingRootDir.isDirectory()) {
            logger.info("No bindings found: {}", bindingRootDir);
            return ResponseEntity.ok().build();
        }

        final var bindings = new HashMap<String, Map<String, String>>(4);
        final var bindingDirs = bindingRootDir.listFiles(File::isDirectory);
        if (bindingDirs != null) {
            for (final var bindingDir : bindingDirs) {
                final var bindingName = bindingDir.getName();
                Arrays.stream(Objects.requireNonNull(bindingDir.listFiles(File::isFile))).forEach(f -> {
                    final var key = f.getName();
                    try {
                        final var value = Files.readString(f.toPath());
                        var bindingProps = bindings.computeIfAbsent(bindingName, k -> new HashMap<>(4));
                        bindingProps.put(key, value);
                    } catch (IOException e) {
                        logger.warn("Failed to binding key {} from {}", key, bindingName, e);
                    }
                });
            }
        }
        logger.info("Found {} binding(s)", bindings.size());
        return ResponseEntity.ok(bindings);
    }
}
