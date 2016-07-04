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
package com.greenleaves.mazing.game.model;

import com.greenleaves.androidsqlhelper.annotation.PersistentEntity;
import com.greenleaves.androidsqlhelper.annotation.PersistentField;


/**
 * This class contains information about current game profile.
 *
 * @author Vasudev Murthy
 */
@PersistentEntity(fieldPrefix="m", unique={"ProfileId"})
public class GamePlayerProfile {

    @PersistentField
    private String mProfileId;

    @PersistentField
    private int mLevel;

    @PersistentField
    private long mTotalTime;

    @PersistentField
    private boolean mCurrent;

    /**
     *
     */
    public GamePlayerProfile() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the profileId
     */
    public String getProfileId() {
        return mProfileId;
    }

    /**
     * @param profileId the profileId to set
     */
    public void setProfileId(String profileId) {
        mProfileId = profileId;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return mLevel;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        mLevel = level;
    }

    /**
     * @return the totalTime
     */
    public long getTotalTime() {
        return mTotalTime;
    }

    /**
     * @param totalTime the totalTime to set
     */
    public void setTotalTime(long totalTime) {
        mTotalTime = totalTime;
    }

    /**
     * @return the current
     */
    public boolean isCurrent() {
        return mCurrent;
    }

    /**
     * @param current the current to set
     */
    public void setCurrent(boolean current) {
        mCurrent = current;
    }
}
