/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.settings.support;

import java.util.Arrays;

/**
 * Utility class for converting a String array to an int array
 * 
 * @author Peter De Bruycker
 */
public class ArrayUtil {

    private ArrayUtil() {

    }

    public static int[] toIntArray(String[] stringArray) throws IllegalArgumentException {
        int result[] = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            result[i] = Integer.parseInt(stringArray[i]);
        }
        return result;
    }

    public static String asIntervalString(int[] array) {
        Arrays.sort(array);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < array.length) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                if (array[i] == array[i + 1] - 1) {
                    while (i < array.length - 1 && array[i] == array[i + 1] - 1) {
                        i++;
                    }
                    sb.append("-");
                    sb.append(array[i]);
                }
                if (i < array.length - 1) {
                    sb.append(",");
                }
            }
            i++;
        }
        return sb.toString();
    }

}