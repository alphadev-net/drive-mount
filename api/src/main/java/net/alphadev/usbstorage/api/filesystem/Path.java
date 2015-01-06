/**
 * Copyright © 2014-2015 Jan Seeger
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
package net.alphadev.usbstorage.api.filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Immutable class that refers to an abstract Path resource.
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public final class Path {
    private final List<String> paths;

    public Path(String path) {
        this(Arrays.asList(path.split("/")));
    }

    private Path(List<String> list) {
        this.paths = list;
    }

    public static Path createWithAppended(Path path, String... appendToPath) {
        final List<String> paths = new ArrayList<>(path.paths);
        for (String appendix : appendToPath) {
            if (appendix.indexOf('/') != -1) {
                paths.addAll(Arrays.asList(appendix.split("/")));
            } else {
                paths.add(appendix);
            }
        }
        return new Path(paths);
    }

    /**
     * Returns parent.
     *
     * @return parent path or null
     */
    public Path getParent() {
        if (paths.size() < 3) {
            return null;
        }

        List<String> parentPaths = paths.subList(0, paths.size() - 1);
        return new Path(parentPaths);
    }

    public String getName() {
        if (paths.size() < 2) {
            return null;
        }

        return paths.get(paths.size() - 1);
    }

    public String getDeviceId() {
        if (paths.get(0).isEmpty()) {
            return null;
        }

        return paths.get(0);
    }

    public final Iterable<String> getIterator() {
        return paths.subList(1, paths.size());
    }

    public String toAbsolute() {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;

        for (String token : paths) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append('/');
            }
            sb.append(token);
        }

        return sb.toString();
    }

    public boolean isRoot() {
        return paths.size() == 1;
    }
}
