package br.com.lrferr.balaogramatica;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
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
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

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
	
	private BitmapTextureAtlas ballonTextureAtlas;
	private TextureRegion ballonTextureRegion;
	
	private BitmapTextureAtlas backgroundTextureAtlas;
	private TextureRegion backgroundTextureRegion;

	private BitmapTextureAtlas pinTextureAtlas;
	private TextureRegion pinTextureRegion;
	
	private Font fontBalloon;
	
	private Text scoreText;
	private Text levelText;
	
	private PhysicsWorld physicsWorld;
	private Balloon balloon;
	
	private Sprite background;
	
	Set<Body> pendentsDestroyBodies = new HashSet<Body>();
	
	private Contact contact;
	private Sprite pin;
	
	
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

		this.ballonTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 48, 64, TextureOptions.BILINEAR);
		this.ballonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.ballonTextureAtlas, this, "balloon.png", 0, 0);
		this.ballonTextureAtlas.load();
		
		this.backgroundTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.backgroundTextureAtlas, this, "background_sky_"+CAMERA_WIDTH+"_"+CAMERA_HEIGHT+".png", 0, 0);
		this.backgroundTextureAtlas.load();
		
		this.pinTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, 30, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.pinTextureAtlas, this, "pin.png", 0, 0);
		this.pinTextureAtlas.load();
		
		FontFactory.setAssetBasePath("font/");
		this.fontBalloon = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 48, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getAssets(), "Bubblegum.ttf", 40, true, android.graphics.Color.CYAN);
		this.fontBalloon.load();
		
		
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

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
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
		if (pAccelerationData.getY() < 0) 
			b = -1.0f;
		else if (pAccelerationData.getY() > 0)
			b = 1.0f;
		else 
			b = 0.0f;
		
		final Vector2 gravity = Vector2Pool.obtain(a, b);
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
		background = new Sprite(0, 0, this.backgroundTextureRegion, this.getVertexBufferObjectManager());
		this.mainScene.setBackground(new SpriteBackground(background));

		this.mainScene.setOnAreaTouchListener(this);
		this.mainScene.setTouchAreaBindingOnActionMoveEnabled(true);
		this.mainScene.setTouchAreaBindingOnActionDownEnabled(true);
		this.mainScene.setOnSceneTouchListener(this);

		this.physicsWorld = new PhysicsWorld(new Vector2(0, 0.03f), false);
		
		createWalls();

		createPin();
		
		this.mainScene.registerUpdateHandler(this.physicsWorld);
		
		this.mainScene.registerUpdateHandler(new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed){
				// destroy pendent body
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


		createBalloons(15, "A", 120.0f, -90.0f, 5);
		
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


	public void createWalls(){
		ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
		roof = new Rectangle(0, -100, CAMERA_HEIGHT, 2, this.getVertexBufferObjectManager());
		left = new Rectangle(0, -100, 2, CAMERA_HEIGHT + 100, this.getVertexBufferObjectManager());
		right = new Rectangle(CAMERA_WIDTH - 2, -100, 2, CAMERA_HEIGHT + 100, this.getVertexBufferObjectManager());


		PhysicsFactory.createBoxBody(this.physicsWorld, ground, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallGround");
		PhysicsFactory.createBoxBody(this.physicsWorld, roof, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallRoof");
		PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallLeft");
		PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("wallRight");

		this.mainScene.attachChild(ground);
		this.mainScene.attachChild(roof);
		this.mainScene.attachChild(left);
		this.mainScene.attachChild(right);

	}

	public void createPin() {
		pin = new Sprite(0, CAMERA_HEIGHT - this.pinTextureRegion.getHeight(), this.pinTextureRegion, this.getVertexBufferObjectManager());
		//Body pinBody = PhysicsFactory.createBoxBody(this.physicsWorld, pin, BodyType.StaticBody, PIN_FIXTURE_DEF);
		//pinBody.setUserData("pin");
		//this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(pin, pinBody, true, true));
		PhysicsFactory.createBoxBody(this.physicsWorld, pin, BodyType.StaticBody, WALL_FIXTURE_DEF).setUserData("pin");
		this.mainScene.attachChild(pin);
		
	}
	
	public void createBalloons(int quantidade, String letter, float xInicio, float yInicio, int forma) {
		for (int i = 0; i < quantidade; i++){			
			balloon = new Balloon(120.0f, -50.0f, this.ballonTextureRegion, this.getVertexBufferObjectManager()){
				
			};
			final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
			final Text ballText = new Text(10, 10, this.fontBalloon, letter, new TextOptions(HorizontalAlign.CENTER), vertexBufferObjectManager);
			balloon.attachChild(ballText);
			
			Body balloonBody = PhysicsFactory.createBoxBody(this.physicsWorld, balloon, BodyType.DynamicBody, BALLOON_FIXTURE_DEF_3);
			balloonBody.setUserData(balloon);
			//final PhysicsHandler physicsHandlerBalloon = new PhysicsHandler(balloon);
			//balloon.registerUpdateHandler(physicsHandlerBalloon);

			
			this.mainScene.registerTouchArea(balloon);
			this.mainScene.attachChild(balloon);
			
			this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(balloon, balloonBody, true, true));
		}

	}
	
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
		}
		
		System.gc();
	}

	public void removeBalloon(Balloon balloon) {
		synchronized(pendentsDestroyBodies) {
				
			final PhysicsConnector balloonPhysicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(balloon);	
			physicsWorld.unregisterPhysicsConnector(balloonPhysicsConnector);
			pendentsDestroyBodies.add(balloonPhysicsConnector.getBody());
			//physicsWorld.destroyBody(balloonPhysicsConnector.getBody());
			if (balloon != null) {							
				mainScene.unregisterTouchArea(balloon);
				mainScene.detachChild(balloon);
			}
			
			System.gc();
		}
	}
	
	
}
