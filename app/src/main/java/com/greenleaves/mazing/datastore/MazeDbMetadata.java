/**
 * Copyright 2011 Massimo Gaddini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 */

package com.greenleaves.mazing.datastore;



/**
 * This class defines some metadata costants used by datastore. Contains table names and column names.
 * 
 * @author Vasudev Murthy
 * 
 */
public class MazeDbMetadata {

    public final static String DATABASE_NAME = "Maze.db";
    public final static int DATABASE_VERSION = 1;

    public final static class GamePlayerProfile {

        public final static String GAMEPLAYERPROFILE_TABLE_NAME = "GAMEPLAYERPROFILE";
        public final static String GAMEPLAYERPROFILE_DEFAULT_ORDER = null;
        public final static String GAMEPLAYERPROFILE_ID_COL = "_id";
        protected final static int GAMEPLAYERPROFILE_ID_IDX = 0;
        public final static String GAMEPLAYERPROFILE_PROFILEID_COL = "PROFILEID";
        protected final static int GAMEPLAYERPROFILE_PROFILEID_IDX = 1;
        public final static String GAMEPLAYERPROFILE_LEVEL_COL = "LEVEL";
        protected final static int GAMEPLAYERPROFILE_LEVEL_IDX = 2;
        public final static String GAMEPLAYERPROFILE_TOTALTIME_COL = "TOTALTIME";
        protected final static int GAMEPLAYERPROFILE_TOTALTIME_IDX = 3;
        public final static String GAMEPLAYERPROFILE_CURRENT_COL = "CURRENT";
        protected final static int GAMEPLAYERPROFILE_CURRENT_IDX = 4;

    }

}
