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
package com.greenleaves.mazing.ui.shape;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.greenleaves.mazing.maze.generator.MazeCell;
import com.greenleaves.mazing.maze.generator.MazeGenerator;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.font.FontManager;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.util.Callback;
import org.anddev.andengine.util.Debug;




/**
 * This class hide the generating and drawing of the maze. 
 * A maze is a grid of cells where each cell has one or more side carved.
 * 
 * @author Vasudev Murthy
 *
 */
public class MazeShape extends Rectangle implements ComplexShape, IAccelerometerListener {

    
    /**
     * Maze wall width in pixel
     */
    private static final int WALL_WIDTH = 2;
    
    
    /**
     * Maze generator
     */
    private MazeGenerator mMazeGenerator;
    

    /**
     * The maze grid
     */
    private MazeCell[][] mMazeGrid;
    
    /**
     * Maze width
     */
    private int mMazeWidth = 0;
    
    /**
     * Maze height
     */
    private int mMazeHeight = 0;
    
    /**
     * Horizontal cell size
     */
    private float mStepX;
    
    /**
     * Vertical cell size
     */
    private float mStepY;
    
    /**
     * The marker's texture
     */
    private TextureRegion mMarkerTexture;
    
    /**
     * The target's texture
     */
    private TextureRegion mTargetTexture;
    
    /**
     * The target's sprite
     */
    private Sprite mTargetSprite;
    
    /**
     * The marker's sprite
     */
    private Sprite mMarkerSprite;
    
    /**
     * The context
     */
    private Context mCtx;
    
    /**
     * The physics world used for move marker
     */
    private PhysicsWorld mPhysicsWorld;
    
    /**
     * The AndEngine engine
     */
    private Engine mEngine;

    /**
     * The update handler for the shape
     */
    IUpdateHandler mUpdateHandler;
    
    /**
     * Constructs MazeShape object.
     * 
     * @param pX  X left top corner
     * @param pY  Y left top corner
     * @param width width of the shape
     * @param height height of the shape
     * @param mazeHeight maze height expressed in number of cell
     * @param mazeWidth maze width expressed in number of cell
     * @param generator maze generator instance to be used for generate maze
     * @param ctx the context
     */
    public MazeShape(float pX, float pY, float width, float height, 
                    int mazeHeight, int mazeWidth, MazeGenerator generator, Context ctx) {
        super(pX, pY, width, height);
        
        Debug.setDebugTag("MazeShape");
        
        mMazeHeight = mazeHeight;
        mMazeWidth = mazeWidth;
        mMazeGenerator = generator;
        mCtx = ctx;
    }
   
    /* (non-Javadoc)
     * @see com.greenleaves.mazing.ui.shape.ComplexShape#loadResources(org.anddev.andengine.opengl.texture.TextureManager, org.anddev.andengine.opengl.font.FontManager, android.content.Context)
     */
    public void loadResources(TextureManager textureManager, FontManager fontManager, Context ctx) {
        /* Texture */
        BitmapTextureAtlas textureMarker = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlas textureTarget = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        /* TextureRegion */
        mMarkerTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureMarker, ctx, "gfx/ball.png", 0, 0); // 32x32
        mTargetTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureTarget, ctx, "gfx/target_hole.png", 0, 0); // 32x32
        
        textureManager.loadTextures(textureMarker, textureTarget);
    }
    
    /* (non-Javadoc)
     * @see com.greenleaves.mazing.ui.shape.ComplexShape#init(boolean, org.anddev.andengine.util.Callback, org.anddev.andengine.util.Callback, android.content.Context)
     */
    public void init(boolean visible, final Callback<Boolean> callback, final Callback<Exception> exceptionCallback, Context ctx) {
        setVisible(visible);
        
        mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        
        new AsyncTask<Void, Void, Boolean>() {
            private Exception mException = null;

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            public Boolean doInBackground(final Void... params) {
                Boolean result = true;
                try {
                    mMazeGrid = mMazeGenerator.generate(mMazeHeight, mMazeWidth);
                    Debug.i("Maze generation completed");
                } catch (final Exception e) {
                    mException = e;
                    result = false;
                }
                return result;
            }

            @Override
            public void onPostExecute(final Boolean result) {
                if (mException == null) {
                    try {
                        Debug.i("Maze drawing start");
                        drawMaze();
                        Debug.i("Maze drawing finish. Add marker");
                        addMarker();
                        Debug.i("Add marker finish. Add target");
                        addTarget();
                        Debug.i("Add target finish");
                        
                        callback.onCallback(result);
                    } catch(Exception e){
                        mException = e;
                    }    
                } 
                
                if (mException != null) {
                    if(exceptionCallback == null) {
                        Debug.e("Error", mException);
                    } else {
                        exceptionCallback.onCallback(mException);
                    }
                }
                
                super.onPostExecute(result);
            }
        }.execute((Void[]) null);
    }

    /* (non-Javadoc)
     * @see com.greenleaves.mazing.ui.shape.ComplexShape#enable(org.anddev.andengine.engine.Engine)
     */
    public void enable(Engine engine) {
        setVisible(true);
        
        mEngine = engine;
        
        engine.enableAccelerometerSensor(mCtx, this);
        engine.registerUpdateHandler(mPhysicsWorld);
    }
    
    /* (non-Javadoc)
     * @see com.greenleaves.mazing.ui.shape.ComplexShape#disable(org.anddev.andengine.engine.Engine)
     */
    public void disable(Engine engine) {
        engine.disableAccelerometerSensor(mCtx);
        engine.unregisterUpdateHandler(mPhysicsWorld);
        if (mUpdateHandler != null) {
            unregisterUpdateHandler(mUpdateHandler);
        }
    }
    
    public void setSolvedCallback(final Callback<Void> callback){
        
        mUpdateHandler = new IUpdateHandler() {
            
            private boolean mSolved = false;
            
            @Override
            public void reset() { }

            @Override
            public void onUpdate(final float pSecondsElapsed) {
                if(mMarkerSprite.collidesWith(mTargetSprite) && (!mSolved)) {
                    mSolved = true;
                    Debug.i("Collision !!! Maze solved!");  
                    callback.onCallback(null);
                }
            }
        };
        
        registerUpdateHandler(mUpdateHandler);
    }

    /* (non-Javadoc)
     * @see org.anddev.andengine.sensor.accelerometer.IAccelerometerListener#onAccelerometerChanged(org.anddev.andengine.sensor.accelerometer.AccelerometerData)
     */
    public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
        //mPhysicsWorld.setGravity(new Vector2(-pAccelerometerData.getX(), pAccelerometerData.getY()));
        
        Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getX(), pAccelerometerData.getY());
        mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    protected void addMarker() {
        float scaleXY = getMaxMarkerDim() * 0.8f / mMarkerTexture.getWidth();

        if (scaleXY > 1) {
            scaleXY = 1;
        }

        mMarkerSprite = new Sprite(0, 0, 
                mMarkerTexture.getWidth() * scaleXY, 
                mMarkerTexture.getHeight() * scaleXY, 
                mMarkerTexture);

        mMarkerSprite.setPosition(getMarkerStartPositionX(), getMarkerStartPositionY());

        FixtureDef markerFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0);
        Body body = PhysicsFactory.createCircleBody(mPhysicsWorld, mMarkerSprite,
                BodyType.DynamicBody, markerFixtureDef);

        attachChild(mMarkerSprite);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mMarkerSprite, body, true, true));
    }
    
    protected void addTarget() {
        float scaleXY = getMaxMarkerDim() * 0.8f / mTargetTexture.getWidth();

        if (scaleXY > 1) {
            scaleXY = 1;
        }

        mTargetSprite = new Sprite(0, 0, 
                mTargetTexture.getWidth() * scaleXY, 
                mTargetTexture.getHeight() * scaleXY, 
                mTargetTexture);

        mTargetSprite.setPosition(getTargetPositionX(), getTargetPositionY());

        FixtureDef targetFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0);
        Body body = PhysicsFactory.createCircleBody(mPhysicsWorld, mTargetSprite,
                BodyType.StaticBody, targetFixtureDef);

        attachChild(mTargetSprite);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mTargetSprite, body, true, true));
    }

    protected void drawMaze() {
        mStepX = ( getWidthScaled() - ( mMazeWidth * WALL_WIDTH ) - WALL_WIDTH ) / (float) mMazeWidth;
        mStepY = ( getHeightScaled() - ( mMazeHeight * WALL_WIDTH ) - WALL_WIDTH ) / (float) mMazeHeight;

        FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(1, 0.2f, 0);

        int start = -1;
        int end = -1;
        for (int i = 0; i < ( mMazeHeight - 1 ); i++) {

            start = end = -1;
            for (int j = 0; j < mMazeWidth; j++) {
                if (!mMazeGrid[i][j].isSouthWallClosed()) {
                    if (start >= 0) {
                        end = j;
                        
                        Shape wallH = new Rectangle(( ( start == 0 ) ? WALL_WIDTH : 0 ) + start
                                * ( mStepX + WALL_WIDTH ), ( i + 1 ) * ( mStepY + WALL_WIDTH ),
                                ( end - start ) * ( mStepX + WALL_WIDTH )
                                        + ( ( start == 0 ) ? 0 : WALL_WIDTH ), WALL_WIDTH);
                        PhysicsFactory.createBoxBody(mPhysicsWorld, wallH, BodyType.StaticBody,
                                wallFixtureDef);
                        attachChild(wallH);

                        start = end = -1;
                    }
                } else {
                    if (start < 0) {
                        start = j;
                    }
                }
            }

            if (start >= 0) {
                end = mMazeWidth;
                Shape wallH = new Rectangle(( ( start == 0 ) ? WALL_WIDTH : 0 ) + start
                        * ( mStepX + WALL_WIDTH ), ( i + 1 ) * ( mStepY + WALL_WIDTH ),
                        ( end - start ) * ( mStepX + WALL_WIDTH )
                                + ( ( start == 0 ) ? 0 : WALL_WIDTH ), WALL_WIDTH);
                PhysicsFactory.createBoxBody(mPhysicsWorld, wallH, BodyType.StaticBody,
                        wallFixtureDef);
                attachChild(wallH);
            }
        }

        for (int j = 0; j < ( mMazeWidth - 1 ); j++) {

            start = end = -1;

            for (int i = 0; i < mMazeHeight; i++) {
                if (!mMazeGrid[i][j].isEastWallClosed()) {
                    if (start >= 0) {
                        end = i;
                        
                        Shape wallV = new Rectangle(( j + 1 ) * ( mStepX + WALL_WIDTH ),
                                WALL_WIDTH + start * ( mStepY + WALL_WIDTH ), WALL_WIDTH,
                                ( end - start ) * ( mStepY + WALL_WIDTH ));
                        PhysicsFactory.createBoxBody(mPhysicsWorld, wallV, BodyType.StaticBody,
                                wallFixtureDef);
                        attachChild(wallV);

                        start = end = -1;
                    }
                } else {
                    if (start < 0) {
                        start = i;
                    }
                }
            }

            if (start >= 0) {
                end = mMazeHeight;
                Shape wallV = new Rectangle(( j + 1 ) * ( mStepX + WALL_WIDTH ), WALL_WIDTH
                        + start * ( mStepY + WALL_WIDTH ), WALL_WIDTH, ( end - start )
                        * ( mStepY + WALL_WIDTH ));
                PhysicsFactory.createBoxBody(mPhysicsWorld, wallV, BodyType.StaticBody,
                        wallFixtureDef);
                attachChild(wallV);
            }
        }
        
        /* box */
        Shape ground = new Rectangle(0, ( mStepY + WALL_WIDTH ) * mMazeHeight, 
                WALL_WIDTH + ( mStepX + WALL_WIDTH ) * mMazeWidth, WALL_WIDTH);
        Shape roof = new Rectangle(0, 0, WALL_WIDTH + ( mStepX + WALL_WIDTH ) * mMazeWidth, WALL_WIDTH);
        Shape left = new Rectangle(0, WALL_WIDTH, WALL_WIDTH, ( mStepY + WALL_WIDTH ) * mMazeHeight - WALL_WIDTH);
        Shape right = new Rectangle( ( mStepX + WALL_WIDTH ) * mMazeWidth, WALL_WIDTH, WALL_WIDTH, 
                ( mStepY + WALL_WIDTH ) * mMazeHeight - WALL_WIDTH);

        PhysicsFactory.createBoxBody(mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
        
        attachChild(ground);
        attachChild(roof);
        attachChild(left);
        attachChild(right);
    }

    /**
     * Computes the max marker dimension in pixel 
     * @return the max dimension
     */
    protected float getMaxMarkerDim() {
        return ( mStepX > mStepY ) ? mStepY : mStepX;
    }

    /**
     * Returns the start position's coordinate X of the marker in the maze
     * @return the start position's coordinate X
     */
    protected float getMarkerStartPositionX() {
        return WALL_WIDTH;
    }

    /**
     * Returns the start position's coordinate Y of the marker in the maze
     * @return the start position's coordinate Y
     */
    protected float getMarkerStartPositionY() {
        return WALL_WIDTH;
    }
    
    /**
     * Returns the target position's coordinate X
     * @return the target position's coordinate X
     */
    protected float getTargetPositionX() {
        return (mStepX + WALL_WIDTH) * (mMazeWidth - 1) + WALL_WIDTH + (mStepX - mTargetSprite.getWidthScaled())/2f;
    }

    /**
     * Returns the target position's coordinate Y
     * @return the target position's coordinate Y
     */
    protected float getTargetPositionY() {
        return (mStepY + WALL_WIDTH) * (mMazeHeight - 1) + WALL_WIDTH + (mStepY - mTargetSprite.getHeightScaled())/2f;
    }
}