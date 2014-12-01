/**
 * Copyright © 2014 Jan Seeger
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
package net.alphadev.usbstorage.util;

import net.alphadev.usbstorage.api.Path;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class MimeUtil {
    public static String guessMimeType(Path path) {
        return new Tika(TikaConfig.getDefaultConfig()).detect(path.getName());
    }
}
