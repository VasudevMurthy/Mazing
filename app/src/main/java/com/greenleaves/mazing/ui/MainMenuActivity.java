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
package com.greenleaves.mazing.ui;


import android.util.DisplayMetrics;

import com.greenleaves.mazing.ui.animator.MultiSlideMenuAnimator;
import com.greenleaves.mazing.util.ActivityUtils;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.modifier.ease.EaseBackOut;

import javax.microedition.khronos.opengles.GL10;


/**
 * @author Vasudev Murthy
 *
 */
public class MainMenuActivity extends BaseGameActivity implements MenuScene.IOnMenuItemClickListener {
    
    protected static final int MENU_START = 0;
    protected static final int MENU_QUIT = MENU_START + 1;

    protected Camera camera;
    protected BitmapTextureAtlas mMenuTexture;
    protected TextureRegion mMenuStartTextureRegion;
    protected TextureRegion mMenuQuitTextureRegion;
    protected TextureRegion mMenuCreditsTextureRegion;
//    protected TextureRegion mBackgroundTextureRegion;
    
    /* 
     * @see org.anddev.andengine.ui.IGameInterface#onLoadEngine()
     */
    @Override
    public Engine onLoadEngine() {
        Debug.setDebugTag("MainMenuActivity");
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int cameraWidth = dm.widthPixels;
        int cameraHeight = dm.heightPixels;
        
        camera = new Camera(0, 0, cameraWidth, cameraHeight);
        return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
                new RatioResolutionPolicy(cameraWidth, cameraHeight), camera));
    }

    /* (non-Javadoc)
     * @see org.anddev.andengine.ui.IGameInterface#onLoadComplete()
     */
    @Override
    public void onLoadComplete() {
    }

    /* (non-Javadoc)
     * @see org.anddev.andengine.ui.IGameInterface#onLoadResources()
     */
    @Override
    public void onLoadResources() {
        mMenuTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mMenuStartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture, this, "gfx/menu_start.png", 0, 0);
        mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture, this, "gfx/menu_exit.png", 0, 50);
        mEngine.getTextureManager().loadTexture(mMenuTexture);

    }

    /* (non-Javadoc)
     * @see org.anddev.andengine.ui.IGameInterface#onLoadScene()
     */
    @Override
    public Scene onLoadScene() {
        MenuScene menuScene = new MenuScene(camera);
        
        IMenuItem startMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_START, mMenuStartTextureRegion), (float) 1.2, 1);
        startMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(startMenuItem);

        IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, mMenuQuitTextureRegion), (float) 1.2, 1);
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);
        

        
        MultiSlideMenuAnimator menuAnimator = new MultiSlideMenuAnimator(20, EaseBackOut.getInstance());
        menuAnimator.setDuration(2);
        menuScene.setMenuAnimator(menuAnimator);
        menuScene.buildAnimations();

        menuScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
        menuScene.setOnMenuItemClickListener(this);

        return menuScene;
    }

    /* (non-Javadoc)
     * @see org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener#onMenuItemClicked(org.anddev.andengine.entity.scene.menu.MenuScene, org.anddev.andengine.entity.scene.menu.item.IMenuItem, float, float)
     */
    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
            float pMenuItemLocalX, float pMenuItemLocalY) {
        
        switch(pMenuItem.getID()) {
        case MENU_START:
            Debug.i("Pressed Start menu");
            ActivityUtils.launchActivity(this, MazeActivity.class);
            return true;
        case MENU_QUIT:
            Debug.i("Pressed Start menu");
            ActivityUtils.launchActivity(this, CreditsActivity.class);
            return true;
        default:
            return false;
        }
    }
}
