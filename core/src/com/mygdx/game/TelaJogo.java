package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;



/**
 * Created by paulo on 03/08/2015.
 */
public class TelaJogo extends TelaBase{

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Image jogador;
    private Texture TextureJogadorParado;
    private Texture TextureJogadorDireita;
    private Texture TextureJogadorEsquerda;
    private boolean indoDireita;
    private boolean indoEsquerda;

    /**
     * Construtor padrao da tela de jogo
     * @param game referencia para a classe principal
     */

    public TelaJogo(MyGdxGame game) {
        super(game);
    }

    /**
     * chamado quando a tela e exibida
     */

    @Override
    public void show() {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initFonte();
        initInformacoes();
        initJogador();

    }

    private void initJogador() {
        TextureJogadorParado = new Texture("sprites/player.png");
        TextureJogadorDireita = new Texture ("sprites/player-right.png");
        TextureJogadorEsquerda = new Texture("sprites/player-left.png");
        jogador = new Image(TextureJogadorParado);
        float x = camera.viewportWidth / 2 - jogador.getWidth() /2;
        float y = 0;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
    }

    private void initFonte() {
        fonte = new BitmapFont();
    }

    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;


        lbPontuacao = new Label("0 pontos", lbEstilo);
        palco.addActor(lbPontuacao);
    }


    /**
     * chamado a todo quadro de atualização do jogo
     * @param delta tempo entre um quadro e outro (em segundos)
     */

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - 20);

        capturaTeclas();
        atualizarJogador(delta);

        palco.act(delta);
        palco.draw();

    }

    /**
     * Atualiza a posicao do jogador
     * @param delta movimentacao será em pixels
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; // velocidade de movimento do jogador

        if (indoDireita) {
            if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
                float X = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(X, y);
            }
        }

        if (indoEsquerda){
            if (jogador.getX() > 0) {
                float X = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(X, y);
            }

        }

        if (indoDireita){
            // trocar imagem direita
            jogador.setDrawable(new SpriteDrawable(new Sprite(TextureJogadorDireita)));
        }else if (indoEsquerda){
            // trocar imagem esquerda
            jogador.setDrawable(new SpriteDrawable(new Sprite(TextureJogadorEsquerda)));
        }
    }

    /**
     * Verifica se as teclas estao selecionadas
     */
    private void capturaTeclas() {
        indoDireita = false;
        indoEsquerda = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;

        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;

        }
    }

    /**
     * Chamado sempre que há uma alteração no tamanho da tela
     * @param width novo valor de largura da tela
     * @param height novo valor de altura da tela
     */

    @Override
    public void resize(int width, int height) {

        camera.setToOrtho(false, width, height );
        camera.update();

    }

    /**
     * Chamado sempre que o jogo for minimizado
     */

    @Override
    public void pause() {

    }

    /**
     * Chamado sempre que o jogo voltar para o primeiro plano
     */

    @Override
    public void resume() {

    }

    /**
     * Chamado quando a tela for destruída
     */

    @Override
    public void dispose() {

        batch.dispose();
        palco.dispose();
        fonte.dispose();


    }
}
