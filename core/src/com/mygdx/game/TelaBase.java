package com.mygdx.game;

import com.badlogic.gdx.Screen;

/**
 * Created by paulo on 03/08/2015.
 */
public abstract class TelaBase implements Screen {

    protected MyGdxGame game;

    public TelaBase (MyGdxGame game){
        this.game = game;

    }

    @Override
    public void hide() {
        dispose();
    }
}
