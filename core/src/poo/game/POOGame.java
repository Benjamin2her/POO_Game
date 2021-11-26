package poo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class POOGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;

	//Variables de Assets
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> rainDrops;
	private long lastDropTime;

	@Override
	public void create () {

		//
		//batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		// CARGADO DE IMAGENES
		dropImage = new Texture(Gdx.files.internal("drop_water.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// CARGA EFECTOS DE SONIDOS Y MÃšSICA DE FONDO
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// INICIA SONIDO DE FONDO
		rainMusic.setVolume(0.29f);
		rainMusic.setLooping(true);
		rainMusic.play();

		// INICIALIZACIÃ“N DE CAMERA Y SPRITEBATCH
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		// INSTANCIAMOS LA IMAGEN DE LA CUBETA EN EL JUEGO USANDO UN RECTÃNGULO
		bucket 		  = new Rectangle();
		bucket.x 	  = 800/2 - 64/2;
		bucket.y 	  = 20;
		bucket.width  = 64;
		bucket.height = 64;

		// ARREGLO DINÃMICO PARA ALMACENAR OBJETOS GOTAS
		rainDrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.45f, 0.1f, 0.69f,1);
		camera.update();

		// RENDERIZADO DE ELEMENTOS EN EL JUEGO: PLAYER Y GOTAS
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// RENDERIZADO DE PLAYER
		batch.draw(bucketImage, bucket.x, bucket.y);

		//RENDERIZADO DE GOTAS
		for(Rectangle raindrop: rainDrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

		// DETECTA CLIC Y MOVIMIENTO DEL MOUSE, Y ACTUALIZA COORDENADA x DEL PLAYER
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}

		// DETECTA TECLAS left Y right Y ACTUALIZA COORDENADA x DEL PAYER
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// EVITA QUE EL PLAYER SALGA DE LOS LÃMITES DE LA PANTALLA
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		// CADA SEGUNDO SE GENERA UNA NUEVA GOTA A TRAVÃ‰S DEL MÃTODO spawnRaindrop()
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		// MOVIMIENTO DE LAS GOTAS Y DETECCIÃ“N DE COLISIÃ“N CON PLAYER
		for (Array.ArrayIterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
				dropSound.play(0.5f, 0.2f, 0.1f);
				iter.remove();
			}
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	// MÃtodo para generar gotas en el escenario
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		rainDrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}
