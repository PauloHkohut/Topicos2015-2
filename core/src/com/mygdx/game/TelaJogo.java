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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
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
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture textureTiro;
    private Texture textureMeteoro1;
    private Texture textureMeteoro2;
    private Array<Image> meteoros1 = new Array<Image>();
    private Array<Image> meteoros2 = new Array<Image>();



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
        batch  = new SpriteBatch();
        palco  = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initFonte();
        initInformacoes();
        initJogador();
        initTexturas();

    }

    private void initTexturas() {
        textureTiro = new Texture("sprites/shot.png");
        textureMeteoro1 = new Texture("sprites/enemie-1.png");
        textureMeteoro2 = new Texture("sprites/enemie-2.png");
    }


    /**
     * Instacia os objetos do jogador o adiciona ao palco;
     */
    private void initJogador() {
        TextureJogadorParado   = new Texture("sprites/player.png");
        TextureJogadorDireita  = new Texture ("sprites/player-right.png");
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
        atualizarTiros(delta);
        atualizarMeteoros(delta);

        //Atualiza a situacao do palco
        palco.act(delta);

        //Desenha o palco na tela
        palco.draw();

    }

    private void atualizarMeteoros(float delta) {
        int tipo = MathUtils.random(1, 3);

        if (tipo == 1){
            // cria meteoro1
            Image meteoro = new Image(textureMeteoro1);

            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            meteoro.setPosition(x, y);

            meteoros1.add(meteoro);
            palco.addActor(meteoro);
        }else {
            // cria meteoro2
        }
        float velocidade = 200;
            for (Image meteoro : meteoros1){
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade * delta;

                meteoro.setPosition(x, y);
            }
    }

    private final float minimoIntervaloTiros = 0.4f; // Minimo de tempo entre os tiros
    private float intervaloTiros = 0; // Tempo acumulado entre os tiros

    private void atualizarTiros(float delta) {
        intervaloTiros = intervaloTiros + delta; // Acumula o tempo percorrido

        if (atirando){
            // Verifica se o tempo minimo foi atingido

            if (intervaloTiros >= minimoIntervaloTiros ){
                Image tiro = new Image(textureTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();

                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;

            }

        }
        float velocidade = 200;// Velocidade do movimento do tiro
        // Percorre a todo as lista de tiro na tela
        for (Image tiro : tiros) {
            //movimento do tiro em direção ao topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            // remove os tiros que sairam da tela
            if (tiro.getY() > camera.viewportHeight) {
                tiros.removeValue(tiro, true);//remove da lista
                tiro.remove();//remove do palco

            }
        }
    }

    /**
     * Atualiza a posicao do jogador
     * @param delta movimentacao será em pixels
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; // velocidade de movimento do jogador

        //verifica se o jogador esta fora da tela
        if (indoDireita) {
            if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
                float X = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(X, y);
            }
        }


        //verifica se o jogador esta fora da tela
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
        indoDireita  = false;
        indoEsquerda = false;
        atirando     = false;


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;

        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            atirando = true;
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
        TextureJogadorParado.dispose();
        TextureJogadorDireita.dispose();
        TextureJogadorEsquerda.dispose();
        textureTiro.dispose();
        textureMeteoro1.dispose();
        textureMeteoro2.dispose();


    }
}
