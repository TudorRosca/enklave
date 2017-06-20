package com.enklave.game.Enklave;

import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.PointCoordonate;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;


public class Enklave3D {
    private final Environment environment;
    private AnimationController controller;
    private  BoundingBox box = new BoundingBox();
    private Vector2 latLong;
    private Vector2 coordraw;
    private SpaceCoordonate coordonate;
    private Bounds bounds = new Bounds();
    private Vector3 position = new Vector3(),dimensions = new Vector3();


    private ModelInstance instanceModel;

    public Enklave3D(Vector2 coordonate,int faction) {
        ManagerAssets manager = ManagerAssets.getInstance();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Specular, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Diffuse, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(Color.WHITE,  0f,30, -300f));//-3, -HEIGHT*0.13f, 320
//        SpotLight spotLight = new SpotLight();
//        spotLight.set(Color.WHITE,0,-230,320,0,50,-1,500,0,2);
//        environment.add(new PointLight().set(Color.WHITE,0,-230,320,1000));
//        environment.add(spotLight);
        latLong = coordonate;
        coordraw = new Vector2();
        Model model = null;
        switch (faction){
            case 0:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveGrey3D);
                break;
            }
            case 1:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveRed3D);
                break;
            }
            case 2:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveBlue3D);
                break;
            }
            case 3:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveGreen3D);
                break;
            }
        }
        for(int i=0;i<model.materials.size;i++){
            model.materials.get(i).set(new ColorAttribute(ColorAttribute.Specular));
            model.materials.get(i).set(new ColorAttribute(ColorAttribute.Reflection));
        }
        model.nodes.get(0).translation.set(0,-180f,-50);
        model.nodes.get(1).translation.set(0,-180f,-50);
        model.nodes.get(2).translation.set(0,-180f,-50);
        model.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.4f));
//        model.materials.get(4).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.45f));
        final ModelInstance intance = new ModelInstance(model);
        instanceModel = new ModelInstance(model);
        controller = new AnimationController(instanceModel);
        controller.setAnimation(instanceModel.animations.get(0).id, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                controller.queue(instanceModel.animations.get(0).id,-1,1f,null,0);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {

            }
        });
//        box = instanceModel.calculateBoundingBox(new BoundingBox());
    }


//    public Enklave3D(double coordDrawLat, double coordDrawLng) {
//        ManagerAssets manager = ManagerAssets.getInstance();
//        decalsbricks =new Decal[12];
//        decalsbricks[0] = Decal.newDecal(new TextureRegion(manager.getAssetsEnklave3D().getTexture(NameFiles.baseEnklave3D)));
//        decalsbricks[0].setPosition((float) coordDrawLng, (float) coordDrawLat, 0 + 1);
//        decalsbricks[0].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        decalsbricks[0].setDimensions(30, 30);
//        for (int i = 1; i < 9; i++) {
//            decalsbricks[i] = Decal.newDecal(new TextureRegion(manager.getAssetsEnklave3D().getTexture(NameFiles.insideEnklave)));
//            decalsbricks[i].setPosition((float) coordDrawLng, (float) coordDrawLat, i + 1);
//            decalsbricks[i].setDimensions(30, 30);
//            decalsbricks[i].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        }
//        decalsbricks[9] = Decal.newDecal(new TextureRegion(manager.getAssetsEnklave3D().getTexture(NameFiles.topEnklave3D)));
//        decalsbricks[10] = Decal.newDecal(new TextureRegion(manager.getAssetsEnklave3D().getTexture(NameFiles.circleEnklave)));
//        decalsbricks[11] = Decal.newDecal(new TextureRegion(manager.getAssetsEnklave3D().getTexture(NameFiles.circleEnklave)));
//        for(int i = 9 ;i<12;i++){
//            decalsbricks[i].setPosition((float) coordDrawLng, (float) coordDrawLat, i + 1);
//            decalsbricks[i].setDimensions(30, 30);
//            decalsbricks[i].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        }
//    }
//Texture texture = new Texture(Gdx.files.internal("Object3D/enklavediffusegreen.tga"));

    public Enklave3D(double coordDrawLat, double coordDrawLng,int faction){
        ManagerAssets manager = ManagerAssets.getInstance();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Specular, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Diffuse, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(Color.WHITE, 1f, 1f, 200f));
//        environment.add(new PointLight().set(Color.WHITE,0,0,0,1100));
        latLong = new Vector2();
        latLong.y = (float) coordDrawLat;
        latLong.x = (float) coordDrawLng;
        coordraw = new Vector2();
        Model model = null;
        switch (faction){
            case 0:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveGrey3D);
                break;
            }
            case 1:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveRed3D);
                break;
            }
            case 2:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveBlue3D);
                break;
            }
            case 3:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveGreen3D);
                break;
            }
        }
        for(int i=0;i<model.materials.size;i++){
            model.materials.get(i).set(new ColorAttribute(ColorAttribute.Specular));
            model.materials.get(i).set(new ColorAttribute(ColorAttribute.Reflection));
        }
        model.nodes.get(0).translation.set(0,-180f,0);
        model.nodes.get(1).translation.set(0,-180f,0);
        model.nodes.get(2).translation.set(0,-180f,0);
        model.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.4f));
        instanceModel = new ModelInstance(model);
        instanceModel.transform.trn(0,0,-50);
        controller = new AnimationController(instanceModel);
        controller.setAnimation(instanceModel.animations.get(0).id, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                controller.queue(instanceModel.animations.get(0).id, -1, 1f, null, 0);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {

            }
        });
        instanceModel.transform.trn((float) coordDrawLng, (float) coordDrawLat, 0);
        instanceModel.transform.scl(0.05f);
        instanceModel.transform.rotate(1, 0, 0, -90);
        instanceModel.calculateBoundingBox(box).mul(instanceModel.transform);
        box.getCenter(position);
        box.getDimensions(dimensions);
    }


    public void FrontEnklave(int faction){
        ManagerAssets manager = ManagerAssets.getInstance();
        Model model = null;
        switch (faction){
            case 0:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveGrey3D);
                break;
            }
            case 1:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveRed3D);
                break;
            }
            case 2:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveBlue3D);
                break;
            }
            case 3:{
                model = manager.getAssetsEnklave3D().getModel(NameFiles.enklaveGreen3D);
                break;
            }
        }
        for(int i=0;i<model.materials.size;i++){
            model.materials.get(i).set(new ColorAttribute(ColorAttribute.Specular));
            model.materials.get(i).set(new ColorAttribute(ColorAttribute.Reflection));
        }
        model.nodes.get(0).translation.set(0,-180f,0);
        model.nodes.get(1).translation.set(0,-180f,0);
        model.nodes.get(2).translation.set(0,-180f,0);

        model.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.4f));
        instanceModel = new ModelInstance(model);
        instanceModel.transform.trn(0,0,310);
        controller = new AnimationController(instanceModel);
        controller.setAnimation(instanceModel.animations.get(0).id, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                controller.queue(instanceModel.animations.get(0).id, -1, 1f, null, 0);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {

            }
        });
        instanceModel.transform.scl(0.2f);
        instanceModel.transform.rotate(1, 0, 0, 90);
    }

//    public void calcCoordonateDraw(PointCoordonate reference,double unitlat,double unitlong){
//        coordraw.y = (float) (bounds.calcDisance(reference.getLatitude(), reference.getLongitude(), latLong.y, reference.getLongitude()) * unitlat);
//        coordraw.x = (float)(bounds.calcDisance(reference.getLatitude(), reference.getLongitude(), reference.getLatitude(), latLong.x) * unitlong);
//        if(reference.getLatitude() > latLong.y)
//            coordraw.y = (-coordraw.y);
//        if(reference.getLongitude() > latLong.x)
//            coordraw.x = (-coordraw.x);
//        for (int i = 0; i < 12; i++) {
//            decalsbricks[i].setPosition(coordraw.x, coordraw.y, i + 1);
//            decalsbricks[i].setDimensions(30, 30);
//            decalsbricks[i].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        }
//
//    }
    public void calcDrawEnklave(PointCoordonate reference,double unitlat,double unitlong){
        coordraw.y = (float) (bounds.calcDisance(reference.getLatitude(), reference.getLongitude(), latLong.y, reference.getLongitude()) * unitlat);
        coordraw.x = (float)(bounds.calcDisance(reference.getLatitude(), reference.getLongitude(), reference.getLatitude(), latLong.x) * unitlong);
        if(reference.getLatitude() > latLong.y)
            coordraw.y = (-coordraw.y);
        if(reference.getLongitude() > latLong.x)
            coordraw.x = (-coordraw.x);
        instanceModel.transform.translate(coordraw.x, coordraw.y, 0);
    }
    public boolean touchEnklave(PerspectiveCamera camera,int screenX,int screenY){
        coordonate = null;
        instanceModel.calculateBoundingBox(box).mul(instanceModel.transform);
        box.getCenter(position);
        box.getDimensions(dimensions);
        Ray ray = camera.getPickRay(screenX,screenY);
//        instanceModel.transform.getTranslation(position);
//        position.add(box.getCenter(new Vector3()));
//        Gdx.app.log("res"+ray.toString(),"position"+position);
        if(Intersector.intersectRayBoundsFast(ray,box)){ // position, box.getDimensions(new Vector3()).len(), null)) {
            coordonate = new SpaceCoordonate();
            coordonate.x = Math.abs(Math.abs(position.x) - Math.abs(camera.position.x)) / (Gdx.graphics.getHeight() * 0.026f);
            coordonate.y = Math.abs(Math.abs(position.y) - Math.abs(camera.position.y)) / (Gdx.graphics.getHeight() * 0.026f);
            coordonate.z = -(Math.abs((Gdx.graphics.getHeight() * 0.033f) - Math.abs(camera.position.z)) / (Gdx.graphics.getHeight() * 0.026f));
            if (box.getCenterX() < camera.position.x) {
                coordonate.x = -coordonate.x;
            }
            if (box.getCenterY() < camera.position.y) {
                coordonate.y = -coordonate.y;
            }
            return true;
        }
        return false;
    }
    public void translateCamera(CameraInputController controller){
        controller.camera.position.x += (float) coordonate.x;
        controller.camera.position.y += (float) coordonate.y;
        controller.camera.position.z += (float) coordonate.z;
        controller.camera.lookAt(position.x, position.y, 0);
        controller.camera.update();
    }
    public void translateEnklave(Enklave3D enk,float x,float y){
        enk.instanceModel.transform.rotate(1,0,0,90);
        enk.instanceModel.transform.trn(x, y, 0);
        enk.instanceModel.transform.rotate(1, 0, 0, -90);
    }
//    public void translateenklave(Enklave3D enk,float x,float y){
//        for(int i=0;i<12;i++)
//            enk.getDecalbrick(i).translate(x, y,0);
//    }
//    public void addtoDecalBatch(DecalBatch batch){
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        for(int i=0;i<12;i++){
//            batch.add(decalsbricks[i]);
//        }
//    }
    public void setLatLong(Vector2 latLong) {
        this.latLong = latLong;
    }

    public Vector2 getCoordraw() {
        return coordraw;
    }

    public void setCoordraw(Vector2 coordraw) {
        this.coordraw = coordraw;
    }

    public BoundingBox getDecalbrick(int i) {
        return this.box;
    }

    public Vector2 getLatLong() {
        return latLong;
    }

    public ModelInstance getInstanceModel() {
        return instanceModel;
    }

    public void EnklaveDraw(ModelBatch batch) {
        batch.render(instanceModel);

        controller.update(Gdx.graphics.getDeltaTime());
    }
    public void drawEnklave(ModelBatch batch){
        batch.render(instanceModel);
        controller.update(Gdx.graphics.getDeltaTime());
    }

    public class SpaceCoordonate {
        public double x;
        public double y;
        public double z;

        public SpaceCoordonate() {
        }
    }
}

