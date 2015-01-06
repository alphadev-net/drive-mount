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
package net.alphadev.usbstorage.api.device;

import net.alphadev.usbstorage.api.Identifiable;
import net.alphadev.usbstorage.api.scsi.Transmittable;

import java.io.Closeable;

/**
 * A bulk device is an abstract device which communicates using SCSI.
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface BulkDevice extends Closeable, Identifiable {
    /**
     * Transmits a given payload to the device BulkDevice being represented.
     *
     * @param payload to transfer
     * @return the amount actually sent
     */
    int write(Transmittable payload);

    /**
     * Receives a payload of a given length from the BulkDevice being represented.
     *
     * @param length of the payload
     * @return the payload data
     */
    byte[] read(int length);

    /**
     * @return true if the connection to the BulkDevice being represented has already been disengaged.
     */
    boolean isClosed();
}
