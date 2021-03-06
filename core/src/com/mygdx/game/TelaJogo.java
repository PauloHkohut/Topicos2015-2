package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
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
    private Stage palcoInformacoes; // Palco somente para as infomacoes da tela
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Label lbGameOver;
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

    private Array<Texture> texturaExplosao = new Array<Texture>();
    private Array<Explosao> explosoes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;


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
        palcoInformacoes  = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initSons();
        initFonte();
        initInformacoes();
        initJogador();
        initTexturas();

    }

    private void initSons() {
        somTiro     = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);
    }

    private void initTexturas() {
        textureTiro = new Texture("sprites/shot.png");
        textureMeteoro1 = new Texture("sprites/enemie-1.png");
        textureMeteoro2 = new Texture("sprites/enemie-2.png");

        for (int i=1; i<=17; i++){
            Texture text = new Texture("sprites/explosion-" + i + ".png");
            texturaExplosao.add(text);
        }
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


    /**
     * Instancia os objetos defonte
     */
    private void initFonte() {
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.size = 24;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = Color.BLUE;

        fonte = generator.generateFont(parameter);

        //fonte = new BitmapFont();

        generator.dispose();;
    }

    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;


        lbPontuacao = new Label("0 pontos", lbEstilo);
        palcoInformacoes.addActor(lbPontuacao);

        lbGameOver = new Label("GAME OVER!!!", lbEstilo);
        palcoInformacoes.addActor(lbGameOver);
    }


    /**
     * chamado a todo quadro de atualização do jogo
     * @param delta tempo entre um quadro e outro (em segundos)
     */

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getPrefHeight() - 20);
        lbPontuacao.setText(pontuacao + " pontos");

        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getPrefWidth() / 2,
                camera.viewportHeight / 2);

        lbGameOver.setVisible(gameOver == true);

        atualizarExplosoes(delta);
        if (gameOver == false) {

            if (!musicaFundo.isPlaying()) //Se nao esta tocando
                musicaFundo.play(); // Iniciar musica

                capturaTeclas();
                atualizarJogador(delta);
                atualizarTiros(delta);
                atualizarMeteoros(delta);
                detectarColisoes(meteoros1, 5);
                detectarColisoes(meteoros2, 15);
            }else {
                if (musicaFundo.isPlaying()) { //Se esta tocando
                    musicaFundo.stop(); // Parar musica
                }
            reiniciarJogo();
        }
        //Atualiza a situacao do palco
        palco.act(delta);

        //Desenha o palco na tela
        palco.draw();

        //Desenha o palco de informacoes
        palcoInformacoes.act(delta);
        palcoInformacoes.draw();

    }


    /**
     * Verifica se o usuario pressionou ENTER para reiniciar o jogo
     */
    private void reiniciarJogo() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            Preferences preferencias = Gdx.app.getPreferences("SpaceInvaders");
            int pontuacaoMaxima = preferencias.getInteger("pontuacao_maxima", 0);

            // Verifica se minha pontuacao e maior que a pontuacao maxima
            if (pontuacao > pontuacaoMaxima){
                preferencias.putInteger("pontuacao_maxima", pontuacao);
                preferencias.flush();
            }

            game.setScreen(new TelaMenu(game));
        }
    }


    private void atualizarExplosoes(float delta) {
        for (Explosao explosao : explosoes){

            // Verifica se a explosao chegou ao fim
            if (explosao.getEstagio() >= 16){

                // Chegou ao fim
                explosoes.removeValue(explosao, true); //  Remove a explosao do array
                explosao.getAtor().remove(); // Remove o ator do palco
            }else {

                // Ainda nao chegou ao fim
                explosao.atualizar(delta);
            }
        }
    }

    private Rectangle recJogador = new Rectangle();
    private Rectangle recTiro = new Rectangle();
    private Rectangle recMeteoro = new Rectangle();

    private  int pontuacao = 0;
    private boolean gameOver;

    private void detectarColisoes(Array<Image> meteoros, int valePonto) {
        recJogador.set(jogador.getX(), jogador.getY(), jogador.getImageWidth(), jogador.getImageHeight());

        for (Image meteoro : meteoros){
            recMeteoro.set(meteoro.getX(), meteoro.getY(), meteoro.getImageWidth(), meteoro.getImageHeight());

            for (Image tiro : tiros){
                recTiro.set(tiro.getX(), tiro.getY(), tiro.getImageWidth(), tiro.getImageHeight());

                // Detecta colisao com os tiros
                if (recMeteoro.overlaps(recTiro)){
                    // Ocorre uma colisao do tiro com o meteoro
                    pontuacao += 5; // Incrementa a pontuacao
                    tiro.remove(); // Remove do palco
                    tiros.removeValue(tiro, true); // Remove da lista
                    meteoro.remove(); // Remove do palco
                    meteoros.removeValue(meteoro, true); // Remove da lista

                    criarExplosao(meteoro.getX() + meteoro.getWidth() / 2,
                            meteoro.getY() + meteoro.getHeight() / 2);
                }
            }

            // Detecta colisao com o player
            if (recJogador.overlaps(recMeteoro)){
                // Ocorre uma colisaocom o jogador meteoro1
                gameOver = true;
                somGameOver.play();

            }
        }
    }

    /**
     * Cria aexplosao na posicao X e Y
     * @param x
     * @param y
     */

    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturaExplosao.get(0));
        ator.setPosition(x - ator.getWidth() / 2, y - ator.getHeight() / 2);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturaExplosao);
        explosoes.add(explosao);
        somExplosao.play();
    }

    private void atualizarMeteoros(float delta) {
        int qtdeMeteoro = meteoros1.size + meteoros2.size; // Retorna quantidade de meteoros criados

        if (qtdeMeteoro < 15) {
            int tipo = MathUtils.random(1, 4); // Retorna 1 ou 2 aleatoriamente

            if (tipo == 1) {
                // cria meteoro1
                Image meteoro = new Image(textureMeteoro1);

                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);

                meteoros1.add(meteoro);
                palco.addActor(meteoro);
            } else if (tipo == 2){
                // cria meteoro2
                Image meteoro = new Image(textureMeteoro2);

                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);

                meteoros2.add(meteoro);
                palco.addActor(meteoro);
            }
        }
            float velocidade = 100; // 100 pixels por segundo
            for (Image meteoro : meteoros1) {
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade * delta;

                meteoro.setPosition(x, y); // Atualiza a posicao do meteoro

                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove(); // remove do palco
                    meteoros1.removeValue(meteoro, true); //remove da lista

                    pontuacao = pontuacao - 30;
                }
            }

            float velocidade2 = 150; // 150 pixels por segundo
            for (Image meteoro : meteoros2) {
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade2 * delta;

                meteoro.setPosition(x, y); // Atualiza a posicao do meteoro

                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove(); // remove do palco
                    meteoros2.removeValue(meteoro, true); //remove da lista

                    pontuacao = pontuacao - 60;
                }
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
                somTiro.play();

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


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || clicouEsquerda()){
            indoEsquerda = true;

        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || clicouDireita()){
            indoDireita = true;

        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.app.getType() ==
                Application.ApplicationType.Android){

            atirando = true;
        }
    }

    private boolean clicouDireita() {

        if (Gdx.input.isTouched()) {
            Vector3 posicao = new Vector3();
            // Captura clique/toque na tela do windowns
            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getY();
            posicao.z = 0;

            //converter para uma coordenada do jogo
            posicao = camera.unproject(posicao);
            float meio = camera.viewportWidth / 2;

            if (posicao.x > meio) {
                return true;
            }
        }

        return false;
    }

    private boolean clicouEsquerda() {

        if (Gdx.input.isTouched()) {
            Vector3 posicao = new Vector3();
            // Captura clique/toque na tela do windowns
            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getY();
            posicao.z = 0;

            //converter para uma coordenada do jogo
            posicao = camera.unproject(posicao);
            float meio = camera.viewportWidth / 2;

            if (posicao.x < meio) {
                return true;
            }
        }
        return false;
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
        palcoInformacoes.dispose();
        fonte.dispose();
        TextureJogadorParado.dispose();
        TextureJogadorDireita.dispose();
        TextureJogadorEsquerda.dispose();
        textureTiro.dispose();
        textureMeteoro1.dispose();
        textureMeteoro2.dispose();

        for (Texture text : texturaExplosao){
            text.dispose();
        }
        somTiro.dispose();
        somExplosao.dispose();
        somGameOver.dispose();
        musicaFundo.dispose();

    }
}
