package br.com.lrferr.balaogramatica;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.modifier.IModifier;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BalaoGramaticaActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnMenuItemClickListener,  IOnAreaTouchListener, 
																				IAccelerationListener, IClickDetectorListener, IScrollDetectorListener {

	/*** Constants ***/
	private static final int CAMERA_HEIGHT = 480;
	private static final int CAMERA_WIDTH = 320;
	
	private  final int LAYER_BACKGROUND = 0;


	private	 final int LAYER_COUNT = 4;

	private final int BALLOON_COLORS = 7;
	private final int BACKGROUND_TYPES = 5;
	private final float X_GRAVITY = 0;
	private final float Y_GRAVITY = 0.2f;
	
	private final int HIT_BALLOON  = 10;
	private final int EXTRA_BALLOONS = 3;
	private final int HIT_WORD = 50;
	private final int LENGTH_WORD = 4;
	
	/* The categories. Multiples of 2*/
	public static final short CATEGORYBIT_WALL = 1;
	public static final short CATEGORYBIT_BALLOON = 2;
	public static final short CATEGORYBIT_PIN = 4;
	
	/* And what should collide with what. */
	public static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_BALLOON;
	public static final short MASKBITS_BALLOON = CATEGORYBIT_WALL + CATEGORYBIT_BALLOON;

	public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL, MASKBITS_WALL, (short)0);

	public static final FixtureDef BALLOON_FIXTURE_DEF_1 = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_2 = PhysicsFactory.createFixtureDef(1, 0.1f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_3 = PhysicsFactory.createFixtureDef(1, 1.0f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_4 = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_5 = PhysicsFactory.createFixtureDef(0.5f, 0.1f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_6 = PhysicsFactory.createFixtureDef(0.1f, 1.0f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_7 = PhysicsFactory.createFixtureDef(1, 0.5f, 0.1f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_8 = PhysicsFactory.createFixtureDef(0.5f, 0.1f, 0.5f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);
	public static final FixtureDef BALLOON_FIXTURE_DEF_9 = PhysicsFactory.createFixtureDef(0.1f, 1.0f, 1.0f, false, CATEGORYBIT_BALLOON, MASKBITS_BALLOON, (short)0);

	
	Rectangle ground;
	Rectangle roof;
	Rectangle left;
	Rectangle right;


	/*** Fields***/
	private Camera camera;
	
	private Scene mainScene;
	private Scene levelSelectScene;
	private MenuScene menuScene;
	
	private BitmapTextureAtlas [] balloonTextureAtlas = new BitmapTextureAtlas[BALLOON_COLORS];
	private TextureRegion [] balloonTextureRegion  = new TextureRegion [BALLOON_COLORS];
	
	private BitmapTextureAtlas boomTextureAtlas;
	private TextureRegion boomTextureRegion;
	
	private BitmapTextureAtlas [] backgroundTextureAtlas = new BitmapTextureAtlas[BACKGROUND_TYPES];
	private TextureRegion [] backgroundTextureRegion = new TextureRegion [BACKGROUND_TYPES];

	private BitmapTextureAtlas pinTextureAtlas;
	private TextureRegion pinTextureRegion;
	
	private Font fontBalloon;
	private Font fontScore;
	private Font fontLevel;
	
	private Text scoreText;
	private Text levelText;
	
	private PhysicsWorld physicsWorld;
	private Balloon balloon;
	private Sprite boom;
	
	private Sprite background;
	private Sprite pin;
	
	Set<Body> pendentsDestroyBodies = new HashSet<Body>();
	
	private List<String> words = new ArrayList<String>();
	
	private TimerHandler timeHandler; 
		
	private int score = 0;
	
	private String word;
	private Text wordText;
	
	private List<String> lettersClicked = new ArrayList<String>();
	
	HUD hud = new HUD();
	private TimerHandler loopTimerHandler;
	private boolean isRunning;
	private boolean isLevelRunning;
	
	/*** SimpleBaseGameActivity ***/
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return 
				new EngineOptions(true, 
						ScreenOrientation.PORTRAIT_FIXED, 
						new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), 
						this.camera);		

	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		
    	for (int i = 0; i < BALLOON_COLORS; i++) {
    		int j = i + 1;
    		this.balloonTextureAtlas[i] = new BitmapTextureAtlas(this.getTextureManager(), 48, 64, TextureOptions.BILINEAR);
    		this.balloonTextureRegion[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.balloonTextureAtlas[i], this, "balloon" + j +".png", 0, 0);
    		this.balloonTextureAtlas[i].load();
    	}

		this.boomTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 48, 64, TextureOptions.BILINEAR);
		this.boomTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.boomTextureAtlas, this, "boom.png", 0, 0);
		this.boomTextureAtlas.load();
		
		for(int k = 0 ; k < BACKGROUND_TYPES; k++) {
			this.backgroundTextureAtlas[k] = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			int l = k + 1;
			this.backgroundTextureRegion[k] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.backgroundTextureAtlas[k], this, "background_sky_"+ l +"_"+ CAMERA_WIDTH + "_" + CAMERA_HEIGHT+".png", 0, 0);
			this.backgroundTextureAtlas[k].load();
		}
		
		this.pinTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, 30, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.pinTextureAtlas, this, "pin.png", 0, 0);
		this.pinTextureAtlas.load();
		
		FontFactory.setAssetBasePath("font/");
		this.fontBalloon = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getAssets(), "Bubblegum.ttf", 40, true, android.graphics.Color.CYAN);		
		//this.fontScore = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getAssets(), "Bubblegum.ttf", 5, true, android.graphics.Color.MAGENTA);
		final ITexture strokeScoreFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture strokeLevelFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.fontScore = new StrokeFont(this.getFontManager(), strokeScoreFontTexture, Typeface.createFromAsset(getAssets(), "font/Bubblegum.ttf"), 20, true, Color.GRAY, 2, Color.BLACK);
		this.fontLevel = new StrokeFont(this.getFontManager(), strokeLevelFontTexture, Typeface.createFromAsset(getAssets(), "font/Bubblegum.ttf"), 20, true, Color.GRAY, 2, Color.BLACK);
		//this.fontLevel = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getAssets(), "Bubblegum.ttf", 5, true, android.graphics.Color.MAGENTA);
		this.fontBalloon.load();
		this.fontScore.load();
		this.fontLevel.load();
		
		
		readWordsFromFile(LENGTH_WORD);
		
	}

	@Override
	protected Scene onCreateScene() {
		// TODO Auto-generated method stub
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mainScene = new Scene();
		this.levelSelectScene = new Scene();
		this.menuScene = new MenuScene();
		
		return createMainScene();
			
		
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		//this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		//this.disableAccelerationSensor();
	}




	/*** IClickDetectorListener ***/
	@Override
	public void onClick(ClickDetector pClickDetector, int pPointerID,
			float pSceneX, float pSceneY) {
		// TODO Auto-generated method stub
		
	}

	/*** IOnAreaTouchListener ***/
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		if(pSceneTouchEvent.isActionUp()) {
			this.removeBalloon((Balloon)pTouchArea);
			return true;
		}

		return false;
	}

	/*** IOnMenuItemClickListener ***/
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub
		return false;
	}

	/*** IOnSceneTouchListener ***/
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	/*** IAccelerationListener ***/
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		float a,b;
		
		if (pAccelerationData.getX() < 0) 
			a = -1.0f;
		else if (pAccelerationData.getX() > 0)
			a = 1.0f;
		else 
			a = 0.0f;
		/*
		if (pAccelerationData.getY() < 0) 
			b = -1.0f;
		else if (pAccelerationData.getY() > 0)
			b = 1.0f;
		else 
			b = 0.0f;
		*/
		final Vector2 gravity = Vector2Pool.obtain(a, Y_GRAVITY);
		this.physicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	/*** IScrollDetectorListener ***/
	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}


	/*** outros métodos ***/
	
	public Scene createMainScene() {
		for(int i = 0; i < LAYER_COUNT; i++) {
			this.mainScene.attachChild(new Entity());
		}

		Random r1 = new Random();
    	int i = r1.nextInt(BACKGROUND_TYPES);
		background = new Sprite(0, 0, this.backgroundTextureRegion[i], this.getVertexBufferObjectManager());
		this.mainScene.setBackground(new SpriteBackground(background));

		this.mainScene.setOnAreaTouchListener(this);
		this.mainScene.setTouchAreaBindingOnActionMoveEnabled(true);
		this.mainScene.setTouchAreaBindingOnActionDownEnabled(true);
		this.mainScene.setOnSceneTouchListener(this);

		this.physicsWorld = new PhysicsWorld(new Vector2(X_GRAVITY, Y_GRAVITY), false);
		
		createTextsLevelAndScore(1);
		
		createWalls();

		createPin();
		
		this.mainScene.registerUpdateHandler(this.physicsWorld);
		
		this.mainScene.registerUpdateHandler(new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed){
				// destroy pendents bodies
				Set<Body> safeList = Collections.synchronizedSet(pendentsDestroyBodies);
				synchronized(safeList) {
				
					if (safeList != null && safeList.size() > 0){
						for (Body b: safeList){
							physicsWorld.destroyBody(b);
							//safeList.remove(b);						
						}
						pendentsDestroyBodies.removeAll(safeList);
						pendentsDestroyBodies = new HashSet<Body>();
						System.gc();
					}
				}
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});
		

		this.isLevelRunning = true;

		word = getRandomWord(words);
		
		showWord();
		
		createRandomBalloons(word, EXTRA_BALLOONS);
		
		
		this.mainScene.registerUpdateHandler(loopTimerHandler = new TimerHandler(12.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				
				
				if (isLevelRunning) {
					
					if (isEqual(word, lettersClicked)) {
						score = score + HIT_WORD * word.length();
					}
					
					lettersClicked = new ArrayList<String>();
					
					word = getRandomWord(words);
					
					showWord();
							
					createRandomBalloons(word, EXTRA_BALLOONS);
					
				}
			}
			
			
			
		}));

		
		
		/* check collision */
		this.physicsWorld.setContactListener(new ContactListener() {

			@Override
			public void beginContact(final Contact pContact){
				synchronized(pendentsDestroyBodies){
				
					if (pContact.getFixtureB().getBody() != null &&
							pContact.getFixtureA().getBody().getUserData().equals("pin") && 
	                             pContact.getFixtureB().getBody().getUserData() instanceof Balloon) {
						removeBalloon(pContact.getFixtureB());
					}
					else if (pContact.getFixtureA().getBody() != null &&
							pContact.getFixtureB().getBody().getUserData().equals("pin") && 
	            		 pContact.getFixtureA().getBody().getUserData() instanceof Balloon){
						removeBalloon(pContact.getFixtureA());
					}
                       
				}
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
					
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
					
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
					
			}
				
         });
         
		//this.mEngine.setScene(mainScene);

		return this.mainScene;
	}		


	public void createTextsLevelAndScore(int level) {
		levelText = new Text(5, 5, this.fontLevel,
				"Level: " + level, "Level: XX".length(), this.getVertexBufferObjectManager());
		levelText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		levelText.setAlpha(1.0f);		
		
		hud.attachChild(levelText);
		//this.mainScene.getChildByIndex(LAYER_SCORE).attachChild(levelText);

		/* The ScoreText showing how many points the pEntity scored. */
		this.scoreText = new Text(CAMERA_WIDTH - 150, 5, this.fontScore, "Score: 0", "Score: XXXXXX".length(), this.getVertexBufferObjectManager());
		this.scoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.scoreText.setAlpha(1.0f);
		hud.attachChild(scoreText);
		camera.setHUD(hud);
		//this.mainScene.getChildByIndex(LAYER_SCORE).attachChild(this.scoreText);
		
		this.timeHandler = new TimerHandler(0.5f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				scoreText.setText("Score: " + score);
			}
		});
		this.mainScene.registerUpdateHandler(timeHandler);


	}

	public void createWalls(){
		ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
		roof = new Rectangle(0, -700, CAMERA_HEIGHT, 2, this.getVertexBufferObjectManager());
		left = new Rectangle(0, -700, 2, CAMERA_HEIGHT + 100, this.getVertexBufferObjectManager());
		right = new Rectangle(CAMERA_WIDTH - 2, -700, 2, CAMERA_HEIGHT + 100, this.getVertexBufferObjectManager());


		PhysicsFactory.createBoxBody(this.physicsWorld, ground, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallGround");
		PhysicsFactory.createBoxBody(this.physicsWorld, roof, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallRoof");
		PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallLeft");
		PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallRight");

		this.mainScene.getChildByIndex(LAYER_BACKGROUND).attachChild(ground);
		this.mainScene.getChildByIndex(LAYER_BACKGROUND).attachChild(roof);
		this.mainScene.getChildByIndex(LAYER_BACKGROUND).attachChild(left);
		this.mainScene.getChildByIndex(LAYER_BACKGROUND).attachChild(right);

	}

	public void createPin() {
		pin = new Sprite(0, CAMERA_HEIGHT - this.pinTextureRegion.getHeight(), this.pinTextureRegion, this.getVertexBufferObjectManager());
		//Body pinBody = PhysicsFactory.createBoxBody(this.physicsWorld, pin, BodyType.StaticBody, PIN_FIXTURE_DEF);
		//pinBody.setUserData("pin");
		//this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(pin, pinBody, true, true));
		PhysicsFactory.createBoxBody(this.physicsWorld, pin, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("pin");
		this.mainScene.getChildByIndex(LAYER_BACKGROUND).attachChild(pin);
		
	}
	
	public void createRandomBalloons(String word, int extraBalloonsCount) {
		for (int i = 0; i < word.length(); i++){	
			Random r = new Random();
			int number = r.nextInt(extraBalloonsCount);
			
			for (int j = 0; j < extraBalloonsCount; j++) {
				
				if (number == j)
					createBalloon(String.valueOf(word.charAt(i)), CAMERA_WIDTH / 2, - balloonTextureRegion[0].getHeight() * i);		
				else
					createBalloon(getRandomLetter(), CAMERA_WIDTH / 2, - balloonTextureRegion[0].getHeight() * i);
				//this.mainScene.unregisterEntityModifier(df);
			}		

		}

	}
	
	
	public String getRandomLetter() {
		   Random r = new Random();

		    String alphabet = "abcdefghijklmnopqrstuvwxyz";
		    return String.valueOf(alphabet.charAt(r.nextInt(alphabet.length())));
		   
	}

	public void createBalloon(String letter, float xInicio, float yInicio) {
			
			Random r1 = new Random();
	    	int i = r1.nextInt(BALLOON_COLORS);
			
	    	balloon = new Balloon(xInicio, yInicio, 1.0f, letter, this.balloonTextureRegion[i], this.getVertexBufferObjectManager()){			
			};
			final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
			final Text ballText = new Text(12, 10, this.fontBalloon, balloon.getConteudo(), new TextOptions(HorizontalAlign.CENTER), vertexBufferObjectManager);
			balloon.attachChild(ballText);
			
			Body balloonBody = PhysicsFactory.createBoxBody(this.physicsWorld, balloon, BodyType.DynamicBody, BALLOON_FIXTURE_DEF_3);
			balloonBody.setUserData(balloon);

			
			this.mainScene.registerTouchArea(balloon);
			this.mainScene.attachChild(balloon);
			
			this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(balloon, balloonBody, true, true));

	}
	
	public void createBoom(float xInicio, float yInicio) {
		boom = new Sprite(xInicio, yInicio, this.boomTextureRegion, this.getVertexBufferObjectManager());
		
		this.mainScene.attachChild(boom);

		boom.registerEntityModifier(
				 new SequenceEntityModifier(
						new ScaleModifier(1, 1.0f, 0.0f)						
					));
		/*
		boom.registerUpdateHandler(new TimerHandler(1.0f, false, new ITimerCallback(){

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// TODO Auto-generated method stub
				mainScene.detachChild(boom);
			}
			
		}));
		 */
		//this.mainScene.detachChild(boom);
}
	/* remove balloon when it collides with pin*/
	public void removeBalloon(Fixture fixture) {
		final PhysicsConnector balloonPhysicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape((Balloon) fixture.getBody().getUserData());	
		Balloon b = null;
		if (balloonPhysicsConnector != null)
			b = (Balloon) balloonPhysicsConnector.getShape();
		physicsWorld.unregisterPhysicsConnector(balloonPhysicsConnector);
		pendentsDestroyBodies.add(fixture.getBody());
		if (b != null) {							
			mainScene.unregisterTouchArea(b);
			mainScene.detachChild(b);
			b.dispose();
		}
		
		createBoom(b.getX(), b.getY());
		
		score+= HIT_BALLOON;
		System.gc();
	}

	/* remove balloon when the player touch on it*/
	public void removeBalloon(Balloon balloon) {
		synchronized(pendentsDestroyBodies) {
			lettersClicked.add(balloon.getConteudo());	
			final PhysicsConnector balloonPhysicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(balloon);	
			physicsWorld.unregisterPhysicsConnector(balloonPhysicsConnector);
			pendentsDestroyBodies.add(balloonPhysicsConnector.getBody());
			//physicsWorld.destroyBody(balloonPhysicsConnector.getBody());
			if (balloon != null) {							
				mainScene.unregisterTouchArea(balloon);
				mainScene.detachChild(balloon);
				balloon.dispose();
			}
			createBoom(balloon.getX(), balloon.getY());
			//score+= 100;
			System.gc();
		}
	}
	
    private void readWordsFromFile(int i)
    {

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getAssets().open("dic/words_"+i+"_letters.txt")));
            
            String line = br.readLine();
            while (line != null){
            	words.add(line);
            	line = br.readLine();
            }
            
        } catch (FileNotFoundException e) {  
            System.out.println(e);  
        }  
        catch (IOException e) {  
            System.out.println(e);  
        }  
                 
    }
 
    private String getRandomWord(List<String> words) {
    	Random r = new Random();
    	int i = r.nextInt(words.size());
    	return words.get(i);
    }
    
    private void showWord() {
    	this.wordText = new Text(0, 0, this.fontScore, this.word, new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		this.wordText.setPosition(CAMERA_WIDTH/2 - 30, CAMERA_HEIGHT/2);
		this.wordText.registerEntityModifier(
				new SequenceEntityModifier(
					new ScaleModifier(2, 0.5f, 4.0f),
					new ParallelEntityModifier(
					new ScaleModifier(8, 4.0f, 0.5f),
					new AlphaModifier(8, 1.0f, 0.0f)
				)));
				
		//this.wordText.registerEntityModifier();

		//this.mainScene.getChildByIndex(LAYER_SCORE).attachChild(this.wordText);
		
		hud.attachChild(this.wordText);
		
    }
    
    private boolean isEqual(String word, List<String> other) {
    	String s = new String();
    	for (int i = 0; i < other.size(); i++) {
    		s = s + other.get(i);
    	}
    	if (word.equalsIgnoreCase(s)) 
    		return true;
    	else
    		return false;
    }
    
    /* work with levels */
    private String isLevelUnLocked(int levelNum){
        MyDatabase myDB = new MyDatabase(this);
        String myReturn = myDB.isLevelUnLocked(levelNum);
        myDB.close();
        return myReturn;
    }
       
    private int unLockLevel(int levelNum, String isUnLocked){
        MyDatabase myDB = new MyDatabase(this);
        int myReturn = myDB.unLockLevel(levelNum, isUnLocked);
        myDB.close();
        return myReturn;
    }
}
