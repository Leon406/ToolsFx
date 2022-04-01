/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hash.password

import java.security.MessageDigest

/**
 * Utility for constant time comparison to prevent against timing attacks.
 *
 * @author Rob Winch
 */
internal object PasswordEncoderUtils {
    /**
     * Constant time comparison to prevent against timing attacks.
     *
     * @param expected
     * @param actual
     * @return
     */
    fun equals(expected: String?, actual: String?): Boolean {
        return MessageDigest.isEqual(expected?.toByteArray(), actual?.toByteArray())
    }
}
