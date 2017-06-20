package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;


public class ScreenBricks implements Screen {
    private final Decal decalmap,decalsbricks[];
    private final Label tabContinue;
    private final Environment environment;
    private final ModelInstance instance;
    private final Model model;
    private  Stage stage;
    private ManagerAssets manager;
    private int Width = Gdx.graphics.getWidth(),Height = Gdx.graphics.getHeight();
    private CameraInputController controller;
    private DecalBatch batch;
    private float semnul = 3, x = 0, y = 0;
    private ModelBatch modelBatch;

    public ScreenBricks(final GameManager gameManager) {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(0f, 15f, 15f, 0f, 16.5f, 0.0f));
        ModelBuilder modelBuilder = new ModelBuilder();
        model =  modelBuilder.createBox(0.1f,0.1f,0.1f,new Material(ColorAttribute.createDiffuse(Color.WHITE)),Position | Normal);
        instance = new ModelInstance(model);
        manager = ManagerAssets.getInstance();
        manager.loadAssetsBricks();
        manager.getAssetsBricks().finish();
        Texture tex = manager.getAssetsBricks().getTexture(NameFiles.bricksMaps);
        decalmap = Decal.newDecal(new TextureRegion(tex));
        decalmap.setPosition(0, 0, 0);
        Vector2 crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), Width, Height);
        decalmap.setDimensions(crop.x * 0.3f, crop.y * 0.3f);
        decalsbricks = new Decal[12];
        tex = manager.getAssetsBricks().getTexture(NameFiles.baseEnklave3D);
        crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),Width,Height);
        Decal exec = Decal.newDecal(new TextureRegion(tex));
        exec.setPosition(0, 0, 160);
        exec.setDimensions(crop.x * 0.07f, crop.y * 0.07f);
        exec.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decalsbricks[0] = exec;
        tex = manager.getAssetsBricks().getTexture(NameFiles.insideEnklave);
        crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), Width, Height);

        for (int i = 1; i < 9; i++) {
            exec = Decal.newDecal(new TextureRegion(tex));
            exec.setPosition(0, 0, Width*0.15f);
            exec.setDimensions(crop.x * 0.07f, crop.y * 0.07f);
            exec.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            decalsbricks[i] = exec;
        }
        tex = manager.getAssetsBricks().getTexture(NameFiles.topEnklave3D);
        crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), Width, Height);
        exec = Decal.newDecal(new TextureRegion(tex));
        exec.setPosition(0, 0, Width * 0.15f);
        exec.setDimensions(crop.x * 0.07f, crop.y * 0.07f);
        exec.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decalsbricks[9] = exec;
        tex = manager.getAssetsBricks().getTexture(NameFiles.circleEnklave);
        crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), Width, Height);
        decalsbricks[10] = Decal.newDecal(new TextureRegion(tex));
        decalsbricks[10].setPosition(0, 0, Width*0.15f);
        decalsbricks[10].setDimensions(crop.x * 0.07f, crop.y * 0.07f);
        decalsbricks[10].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decalsbricks[11] = Decal.newDecal(new TextureRegion(tex));
        decalsbricks[11].setPosition(0, 0, Width*0.15f);
        decalsbricks[11].setDimensions(crop.x * 0.07f, crop.y * 0.07f);
        decalsbricks[11].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decalsbricks[11].rotateZ(50);
        tabContinue = new Label("Tap Here to Continue!",new Label.LabelStyle(Font.getFont((int)(Height*0.03f)),Color.WHITE));
        tabContinue.setPosition(Gdx.graphics.getWidth() / 2 - tabContinue.getWidth() / 2, Gdx.graphics.getHeight() * 0.01f);
        tabContinue.setVisible(false);
        tabContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(InformationProfile.getInstance().getDateUserGame().getFaction()== 0){
                    gameManager.setScreen(new ScreenChoiceFaction(gameManager));
                }else {
                    gameManager.setScreen(gameManager.screenLoading);
                }
            }
        });
        manager.loadAssetsChoiceFaction();
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Width,Height));
        stage.addActor(tabContinue);
        PerspectiveCamera camera2 = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera2.position.set(0, -Width * 0.075f, Width * 0.135f);
        camera2.lookAt(0, 0, 0);
        camera2.far = Width*0.3f;
        camera2.near = 1;
        camera2.update();
        controller = new CameraInputController(camera2);
        CameraGroupStrategy cameraGroupStrategy = new CameraGroupStrategy(camera2);
        batch = new DecalBatch(cameraGroupStrategy);
        modelBatch = new ModelBatch();
        InputMultiplexer in = new InputMultiplexer();
        in.addProcessor(stage);
        in.addProcessor(controller);
        Gdx.input.setInputProcessor(in);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glViewport(0, 0, Width, Height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (decalsbricks[0].getPosition().z >= Width*0.00115) {
            decalsbricks[0].translate(0, 0, -45 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[1].getPosition().z >= Width * 0.0033 && decalsbricks[0].getPosition().z <= Width*0.00115) {
            decalsbricks[1].translate(0, 0, -50 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[2].getPosition().z >= Width * 0.0056 && decalsbricks[1].getPosition().z <= Width * 0.0033) {
            decalsbricks[2].translate(0, 0, -55 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[3].getPosition().z >= Width * 0.0079 && decalsbricks[2].getPosition().z <= Width * 0.0056) {
            decalsbricks[3].translate(0, 0, -60 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[4].getPosition().z >= Width * 0.0102 && decalsbricks[3].getPosition().z <= Width * 0.0079) {
            decalsbricks[4].translate(0, 0, -65 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[5].getPosition().z >= Width * 0.0125 && decalsbricks[4].getPosition().z <= Width * 0.0102) {
            decalsbricks[5].translate(0, 0, -70 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[6].getPosition().z >= Width * 0.01482 && decalsbricks[5].getPosition().z <= Width * 0.0125) {
            decalsbricks[6].translate(0, 0, -75 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[7].getPosition().z >=  Width * 0.0172f && decalsbricks[6].getPosition().z <=  Width * 0.01482f) {
            decalsbricks[7].translate(0, 0, -80 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[8].getPosition().z >=  Width * 0.0195f && decalsbricks[7].getPosition().z <=  Width * 0.0172f) {
            decalsbricks[8].translate(0, 0, -85 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[9].getPosition().z > Width * 0.0215f && decalsbricks[8].getPosition().z <= Width * 0.0195f) {
            decalsbricks[9].translate(0, 0, -90 * Gdx.graphics.getDeltaTime());
        }
        if (decalsbricks[9].getPosition().z <= (Width * 0.0218f)) {
            float possition = Math.round((Width * 0.0218f)*100)/100;
            if (decalsbricks[9].getZ() != possition) {
                decalsbricks[0].translate(0, 0, Width*0.00117f - Math.abs(decalsbricks[0].getZ()));
                decalsbricks[1].translate(0, 0, Width * 0.0033f - decalsbricks[1].getZ());
                decalsbricks[2].translate(0, 0, Width * 0.0056f - decalsbricks[2].getZ());
                decalsbricks[3].translate(0, 0, Width * 0.0079f - decalsbricks[3].getZ());
                decalsbricks[4].translate(0, 0, Width * 0.0102f - decalsbricks[4].getZ());
                decalsbricks[5].translate(0, 0, Width * 0.0125f - decalsbricks[5].getZ());
                decalsbricks[6].translate(0, 0, Width * 0.01482f - decalsbricks[6].getZ());
                decalsbricks[7].translate(0, 0, Width * 0.0172f - decalsbricks[7].getZ());
                decalsbricks[8].translate(0, 0, Width * 0.0195f - decalsbricks[8].getZ());
                decalsbricks[9].translate(0, 0, possition - decalsbricks[9].getZ());
                decalsbricks[10].translate(0, 0,Width * 0.0046f -decalsbricks[10].getZ());
                decalsbricks[11].translate(0, 0,Width * 0.0217f -decalsbricks[11].getZ());
            }
            for (int i = 0; i < 10; i++) {
                decalsbricks[i].rotateZ(-1);
            }
            decalsbricks[10].rotateZ(1);
            decalsbricks[11].rotateZ(-1);
            if (decalsbricks[10].getZ() < Width * 0.00185) {
                semnul = 3;
            }
            if (decalsbricks[10].getZ() > Width * 0.0204 ) {
                semnul = -3;
            }

            decalsbricks[10].translate(0, 0, semnul * Gdx.graphics.getDeltaTime());
        }
        modelBatch.begin(controller.camera);
        modelBatch.render(instance,environment);
        modelBatch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        for (int i = 0; i < 12; i++) {
            batch.add(decalsbricks[i]);
        }
        batch.add(decalmap);
        batch.flush();
        stage.act(delta);
        stage.draw();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        if (controller.camera.position.z < Width * 0.028f) {
            controller.camera.position.z = 30;
            controller.camera.position.x = x;
            controller.camera.position.y = y;
            controller.camera.lookAt(0, 0, 0);
        } else {
            x = controller.camera.position.x;
            y = controller.camera.position.y;
        }
        controller.camera.lookAt(0, 0, 0);
        controller.camera.update();
        if(manager.getAssetsChoiceFaction().update()){
            if(!InformationProfile.getInstance().getDateUser().getUserName().equals("")) {
                tabContinue.setVisible(true);
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        manager.getAssetsBricks().dispose();
        modelBatch.dispose();
        environment.clear();
        model.dispose();
    }
}
