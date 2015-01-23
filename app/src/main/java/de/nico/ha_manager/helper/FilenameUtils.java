/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.nico.ha_manager.helper;

public class FilenameUtils
{
		/**
		 * The extension separator character.
		 * @since Commons IO 1.4
		 */
		public static final char EXTENSION_SEPARATOR = '.';
				
		/**
		 * The Unix separator character.
		 */
		private static final char UNIX_SEPARATOR = '/';

		/**
		 * The Windows separator character.
		 */
		private static final char WINDOWS_SEPARATOR = '\\';
		
			
	    /**
		 * Returns the index of the last directory separator character.
		 * <p>
		 * This method will handle a file in either Unix or Windows format.
		 * The position of the last forward or backslash is returned.
		 * <p>
		 * The output will be the same irrespective of the machine that the code is running on.
		 * 
		 * @param filename  the filename to find the last path separator in, null returns -1
		 * @return the index of the last separator character, or -1 if there
		 * is no such character
		 */
		public static int indexOfLastSeparator(String filename) {
				if (filename == null) {
						return -1;
					}
				int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
				int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
				return Math.max(lastUnixPos, lastWindowsPos);
			}
			
		/**
		 * Returns the index of the last extension separator character, which is a dot.
		 * <p>
		 * This method also checks that there is no directory separator after the last dot.
		 * To do this it uses {@link #indexOfLastSeparator(String)} which will
		 * handle a file in either Unix or Windows format.
		 * <p>
		 * The output will be the same irrespective of the machine that the code is running on.
		 * 
		 * @param filename  the filename to find the last path separator in, null returns -1
		 * @return the index of the last separator character, or -1 if there
		 * is no such character
		 */
		public static int indexOfExtension(String filename) {
				if (filename == null) {
						return -1;
					}
				int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
				int lastSeparator = indexOfLastSeparator(filename);
				return (lastSeparator > extensionPos ? -1 : extensionPos);
			}
		//-----------------------------------------------------------------------
		/**
		 * Removes the extension from a filename.
		 * <p>
		 * This method returns the textual part of the filename before the last dot.
		 * There must be no directory separator after the dot.
		 * <pre>
		 * foo.txt    --> foo
		 * a\b\c.jpg  --> a\b\c
		 * a\b\c      --> a\b\c
		 * a.b\c      --> a.b\c
		 * </pre>
		 * <p>
		 * The output will be the same irrespective of the machine that the code is running on.
		 *
		 * @param filename  the filename to query, null returns null
		 * @return the filename minus the extension
		 */
		public static String removeExtension(String filename) {
				if (filename == null) {
						return null;
					}
				int index = indexOfExtension(filename);
				if (index == -1) {
						return filename;
					} else {
						return filename.substring(0, index);
					}
			}
}
