package com.enklave.game.Combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.enklave.game.Combat.Request.HitEnklave;
import com.enklave.game.Combat.Request.HitUser;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Screens.ScreenCombat;
import com.enklave.game.Utils.NameFiles;


public class CombatFitght {
    private final InformationProfile informationProfile;
    private InformationEnklave informationEnklave;
    private ManagerAssets managerAssets;
    private final int WIDTH = Gdx.graphics.getWidth(), HEIGHT = Gdx.graphics.getHeight();
    private Group groupShield,groupButtonAction;//groupturret1,groupturret2,groupturret3,
    private ImmediateModeRenderer20 renderer;
    private Texture lookup;
    private float r = WIDTH*0.071F,cx = WIDTH/2,cy = HEIGHT/1.88f,thickness = HEIGHT*0.006f;
    private float c1x ,c1y ;
    private ScreenCombat screenCombat;
    private Image cover, mask, brickLoader;
    private Label labelNrBricks;

    public CombatFitght(ScreenCombat screen,float pos) {
        cy = pos;
        managerAssets = ManagerAssets.getInstance();
        informationProfile = InformationProfile.getInstance();
        informationEnklave = InformationEnklave.getInstance();
        screenCombat = screen;
        lookup = managerAssets.getAssetsCombat().getTexture(NameFiles.progressbarcircular);
        lookup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//        groupturret1 = maketurret(180);
//        groupturret1.setPosition(0,pos * -0.33f);
//        groupturret2 = maketurret(-60);
//        groupturret2.setPosition(pos*0.33f,pos *0.26f);
//        groupturret3 = maketurret(60);
//        groupturret3.setPosition(-pos*0.33f,pos*0.26f);
        groupShield = makeShieldEnklave();
        groupButtonAction = makeButtonAction();
        renderer = new ImmediateModeRenderer20(false,true,1);
        informationEnklave.selectTargetEnklave = -1;
    }

    private Group maketurret(int grade){
        Group group = new Group();
        Texture tex = managerAssets.getAssetsCombat().getTexture(NameFiles.turretsimpleCombat);
        Vector2 crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), WIDTH, HEIGHT);
        Image turret = new Image(new TextureRegion(tex));
        turret.setSize(crop.x*0.075f,crop.y*0.075f);
        turret.setPosition(WIDTH / 2 - turret.getWidth() / 2, HEIGHT /2 - turret.getHeight()/2 );
        turret.setOrigin(turret.getWidth() / 2, turret.getHeight() / 2);
        turret.setRotation(grade);
        group.addActor(turret);
        tex = managerAssets.getAssetsCombat().getTexture(NameFiles.targetRecharge);
        crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), WIDTH, HEIGHT);
        final Image frameselect = new Image(new TextureRegion(tex));
        frameselect.setName("frameselect");
        frameselect.toFront();
        frameselect.setSize(crop.x * 0.15f, crop.y * 0.15f);
        frameselect.setPosition(turret.getRight() - turret.getWidth() / 2 - frameselect.getWidth() / 2, turret.getTop() - turret.getHeight() / 2 - frameselect.getHeight() / 2);
        frameselect.setVisible(false);
        group.addActor(frameselect);
        turret.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (frameselect.isVisible()) {
                    frameselect.setVisible(false);
                } else {
                    deselect();
                    screenCombat.deselectplayers();
                    SelectRecharge();
                    frameselect.setVisible(true);
                }
            }
        });
        tex = new Texture(0,0, Pixmap.Format.RGBA8888);
        tex.dispose();
        return group;
    }
    private Group makeShieldEnklave(){
        Group group = new Group();
        Texture tex = managerAssets.getAssetsCombat().getTexture(NameFiles.maskLoader);
        Vector2 crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),WIDTH,HEIGHT);
        brickLoader = new Image(new TextureRegion(tex));
        brickLoader.setSize(crop.x * 0.16f, crop.y * 0.16f);
        brickLoader.setPosition(WIDTH / 2 - brickLoader.getWidth() / 2, HEIGHT / 2 - brickLoader.getHeight() * 0.18f);
        mask = new Image(new TextureRegion(managerAssets.getAssetsCombat().getTexture(NameFiles.brickLoader)));
        mask.setSize(brickLoader.getWidth(), brickLoader.getHeight());
        mask.setPosition(brickLoader.getX(), brickLoader.getY());
        mask.setScale(1,  (float)informationEnklave.getEnergyBrick() / informationEnklave.getEnergyBrickfull());
        group.addActor(mask);
        tex = managerAssets.getAssetsCombat().getTexture(NameFiles.target);
        final Image frameselect = new Image(new TextureRegion(tex));
        frameselect.setName("frameselect");
        frameselect.toFront();
        frameselect.setSize(brickLoader.getWidth(), brickLoader.getHeight());
        frameselect.setPosition(brickLoader.getX(), brickLoader.getY());
        frameselect.setVisible(false);
        Color col = null;
        switch (informationEnklave.getFaction()){
            case 1:{
                col = new Color(1,0,0,1);
                break;
            }
            case 2:{
                col = new Color(0f, 0.831f, 0.969f,1f);
                break;
            }
            case 3:{
                col = new Color(0.129f, 0.996f, 0.29f,1);
                break;
            }
        }
        labelNrBricks = new Label(informationEnklave.getBricks()+"",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.03)),col));
        labelNrBricks.setSize(frameselect.getWidth(),frameselect.getHeight());
        labelNrBricks.setPosition(frameselect.getX(),frameselect.getY());
        labelNrBricks.setAlignment(Align.center);
        group.addActor(labelNrBricks);
        group.addActor(brickLoader);
        group.addActor(frameselect);
        brickLoader.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(InformationEnklave.getInstance().getFaction() != informationProfile.getDateUserGame().getFaction() && informationProfile.getDateUserGame().getEnklaveCombatId() != -1) {
                    if (frameselect.isVisible()) {
                        frameselect.setVisible(false);
                    } else {
                        deselect();
                        screenCombat.deselectplayers();
                        SelectFire();
                        frameselect.setVisible(true);
                        InformationEnklave.getInstance().selectTargetEnklave = 0;
                    }
                }
            }
        });
        cy = brickLoader.getY() + brickLoader.getHeight()/2;
        tex = new Texture(0,0, Pixmap.Format.RGBA8888);
        tex.dispose();
        return group;
    }
    private Group makeButtonAction(){
        Group group = new Group();
        Texture tex = managerAssets.getAssetsCombat().getTexture(NameFiles.buttonFire);
        Vector2 crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),WIDTH,HEIGHT);
        Image hill = new Image(new TextureRegion(tex));
        hill.setName("fire");
        hill.setSize(crop.x * 0.3f, crop.y * 0.3f);
        hill.setPosition(WIDTH / 2 - hill.getWidth() / 2, HEIGHT * 0.025f);
        hill.setVisible(false);
        hill.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(InformationEnklave.getInstance().selectTargetEnklave == 0){
                    new HitEnklave().makeRequest(InformationEnklave.getInstance().getCombatId(),screenCombat);
                    cover.setVisible(true);
//                    informationProfile.getDateUserGame().setEnergy(informationProfile.getDateUserGame().getEnergy() - ListOfAttachers.getInstance().get()));
//                    screenCombat.getProgressBarEnergy().updateCombat();
                }else{
                    new HitUser().makeRequest(InformationEnklave.getInstance().selectTargetEnklave,screenCombat);
                    cover.setVisible(true);
                }
            }
        });
        group.addActor(hill);
        tex = managerAssets.getAssetsCombat().getTexture(NameFiles.buttonRecharge);
         crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),WIDTH,HEIGHT);
        Image cell = new Image(new TextureRegion(tex));
        cell.setName("recharge");
        cell.setSize(crop.x * 0.3f, crop.y * 0.3f);
        cell.setPosition(WIDTH / 2 - hill.getWidth() / 2, HEIGHT * 0.025f);
        cell.setVisible(false);
        cell.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                new HitUser().makeRequest();
            }
        });
        group.addActor(cell);
        Pixmap pixmap = new Pixmap((int)cell.getWidth(),(int)cell.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(105/255, 105/255, 105/255 , 0.7f);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.fillCircle((int)(cell.getWidth()/2),(int)(cell.getHeight()/2),(int)(cell.getHeight()/2));//(int)cell.getWidth()/2,(int)cell.getHeight()/2,(int)cell.getHeight());
        cover = new Image(new Texture(pixmap));
        cover.setPosition(WIDTH / 2 - hill.getWidth() / 2, HEIGHT * 0.025f);
        cover.setVisible(false);
        group.addActor(cover);
        c1x = cover.getX() + (cover.getWidth() / 2);
        c1y = cover.getY() + (cover.getHeight() / 2);
        return group;
    }

    public void StartTimer() {
        cover.setVisible(false);
    }

    public void deselect() {
//        groupturret1.findActor("frameselect").setVisible(false);
//        groupturret2.findActor("frameselect").setVisible(false);
//        groupturret3.findActor("frameselect").setVisible(false);
        groupShield.findActor("frameselect").setVisible(false);
        groupButtonAction.findActor("recharge").setVisible(false);
        groupButtonAction.findActor("fire").setVisible(false);
    }

    public void SelectFire(){
        groupButtonAction.findActor("fire").setVisible(true);
    }

    public void SelectRecharge(){
        groupButtonAction.findActor("recharge").setVisible(true);
    }

    public void renderCercle(OrthographicCamera camera,float amt, Color c){
        float start = 0f;
        float end = amt * 360f;

        lookup.bind();
        renderer.begin(camera.combined, GL20.GL_TRIANGLE_STRIP);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        int segs = (int)(24 * Math.cbrt(r));
        end += 90f;
        start += 90f;
        float halfThick = thickness/2f;
        float step = 360f / segs;
        for (float angle=start; angle<(end+step); angle+=step) {
            float tc = 0.5f;
            if (angle==start)
                tc = 0f;
            else if (angle>=end)
                tc = 1f;

            float fx = MathUtils.cosDeg(angle);
            float fy = MathUtils.sinDeg(angle);

            float z = 0f;
            //renderer.
            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 1f);
            renderer.vertex(cx + fx * (r + halfThick), cy + fy * (r + halfThick), z);

            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 0f);
            renderer.vertex(cx + fx * (r + -halfThick), cy + fy * (r + -halfThick), z);
        }
        renderer.end();
    }

    public void renderCercleRecharge(OrthographicCamera camera,float amt, Color c){
        float start = 0f;
        float end = amt * 360f;

        lookup.bind();
        renderer.begin(camera.combined, GL20.GL_TRIANGLE_STRIP);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        int segs = (int)(24 * Math.cbrt(r));
        end += 90f;
        start += 90f;
        float halfThick = thickness/2f;
        float step = 360f / segs;
        for (float angle=start; angle<(end+step); angle+=step) {
            float tc = 0.5f;
            if (angle==start)
                tc = 0f;
            else if (angle>=end)
                tc = 1f;

            float fx = MathUtils.cosDeg(angle);
            float fy = MathUtils.sinDeg(angle);

            float z = 0f;
            //renderer.
            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 1f);
            renderer.vertex(c1x + fx * (r + halfThick), c1y + fy * (r + halfThick), z);

            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 0f);
            renderer.vertex(c1x + fx * (r + -halfThick), c1y + fy * (r + -halfThick), z);
        }
        renderer.end();
    }

    public void AddStage(Stage stage){
//        stage.addActor(groupturret1);
//        stage.addActor(groupturret2);
//        stage.addActor(groupturret3);
        stage.addActor(groupShield);
        stage.addActor(groupButtonAction);
    }

    public void updateMaskBrick(){
//        mask.remove();
//        mask.setHeight((brickLoader.getHeight() * informationEnklave.getEnergyBrick())/informationEnklave.getEnergyBrickfull());
        float scale = (float)(informationEnklave.getEnergyBrick())/(float)(informationEnklave.getEnergyBrickfull());
        ScaleToAction sc = new ScaleToAction();
        sc.setScale(1,scale);
        sc.setDuration(0.5f);
        mask.addAction(sc);
        labelNrBricks.setText(""+informationEnklave.getBricks());
    }
}
