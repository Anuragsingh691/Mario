package com.ard.mario.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import static com.badlogic.gdx.Input.Keys.R;

public class Mario extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;
	Texture[] man;
	int manState = 0;
	int pause=0;
	float gravity= 0.2f;
	float velocity =0;
	int manY=0;
	ArrayList<Integer> coinXs= new ArrayList<Integer>();
	ArrayList<Integer> coinys= new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();
	ArrayList<Rectangle> BombRectangle = new ArrayList<Rectangle>();
	Texture coins;
	int coinCount;
	ArrayList<Integer> BombXs= new ArrayList<Integer>();
	ArrayList<Integer> Bombys= new ArrayList<Integer>();
	Texture bombs;
	int bombCount;
	Random random;
	Rectangle manRectangle;
	int score=0;
	BitmapFont font;
	int gameState=0;
	Texture dizzy;
	private Music bg;
	private Music draw;

	@Override
	public void create () {
		batch = new SpriteBatch();
		bg= Gdx.audio.newMusic(Gdx.files.internal("mario.mp3"));
		draw=Gdx.audio.newMusic(Gdx.files.internal("draw2.mp3"));
		bg.setLooping(true);
		bg.setVolume(0.2f);
		background = new Texture("bg.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight() /2;
		coins = new Texture("coin.png");
		bombs= new Texture("bomb.png");
		random = new Random();
		manRectangle = new Rectangle();
		font =new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dizzy = new Texture("dizzy-1.png");
	}
	public void makeBomb()
	{
		float height = random.nextFloat()* Gdx.graphics.getHeight();
		Bombys.add((int)height);
		BombXs.add(Gdx.graphics.getWidth());
	}

	public void makeCoin()
	{
		float height = random.nextFloat()* Gdx.graphics.getHeight();
		coinys.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());

	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if (gameState == 1)
        {
            //Coins
            if (coinCount<100)
            {
                coinCount++;
            }
            else
            {
                coinCount=0;
                makeCoin();
            }
            coinRectangle.clear();
            for (int i=0;i<coinXs.size();i++)
            {
                batch.draw(coins, coinXs.get(i),coinys.get(i));
                coinXs.set(i,coinXs.get(i)-4);
                coinRectangle.add(new Rectangle(coinXs.get(i),coinys.get(i),coins.getWidth(),coins.getHeight()));
            }
            //Bombs
            if (bombCount<250)
            {
                bombCount++;
            }
            else
            {
                bombCount=0;
                makeBomb();
            }
            BombRectangle.clear();
            for (int i=0;i<BombXs.size();i++)
            {
                batch.draw(bombs, BombXs.get(i),Bombys.get(i));
                BombXs.set(i,BombXs.get(i)-8);
                BombRectangle.add(new Rectangle(BombXs.get(i),Bombys.get(i),bombs.getWidth(),bombs.getHeight()));
            }
            if (Gdx.input.justTouched())
            {
                velocity=-10;
                bg.play();
            }
            if (pause< 10)
            {
                pause++;
            }
            else
            {
                pause = 0;
                if (manState<3)
                {
                    manState++;
                }
                else
                {
                    manState=0;
                }
            }
            velocity= velocity+ gravity;
            manY-=velocity-0.5;
            if (manY<=0)
            {
                manY=0;
            }
        }
		else if (gameState == 0)
        {
            if (Gdx.input.justTouched())
            {
                gameState =1;
            }
        }
		else if (gameState == 2)
        {
        	bg.stop();
			draw.setVolume(0.2f);
			draw.play();
            if (Gdx.input.justTouched())
            {
                gameState =1;
                manY=Gdx.graphics.getHeight() /2;
                score=0;
                velocity=0;
                coinXs.clear();
                coinys.clear();
                coinRectangle.clear();
                coinCount = 0;
                BombXs.clear();
                Bombys.clear();
                BombRectangle.clear();
                bombCount = 0;
                draw.stop();
            }
        }
		if (gameState == 2)
		{
			batch.draw(dizzy,Gdx.graphics.getWidth() /2 - man[manState].getWidth() / 2,manY);
		}
		else
		{
			batch.draw(man[manState],Gdx.graphics.getWidth() /2 - man[manState].getWidth() / 2,manY);
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth() /2 - man[manState].getWidth() / 2,manY,man[manState].getWidth(),man[manState].getHeight());
		for (int i=0;i<coinRectangle.size();i++)
		{
			if (Intersector.overlaps(manRectangle,coinRectangle.get(i)))
			{
					score++;
					coinRectangle.remove(i);
					coinXs.remove(i);
					coinys.remove(i);
					break;
				}
		}
		for (int i=0;i<BombRectangle.size();i++)
		{
			if (Intersector.overlaps(manRectangle,BombRectangle.get(i)))
			{
                gameState = 2;
			}
		}
		font.draw(batch,String.valueOf(score),50,120);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
