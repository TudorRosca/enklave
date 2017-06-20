package com.enklave.game.Raider;

import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by adrian on 28.04.2016.
 */
public class RaiderMap {
    private final ModelInstance instance;
    private ManagerAssets manager;
    private BoundingBox box;
    private Vector3 position;
    private Vector3 dimension;
    private Vector3 coordonate = new Vector3();

    public RaiderMap() {
        manager = ManagerAssets.getInstance();
        Model model = manager.getAssetsRaider().getModel(NameFiles.raiderMap);
        model.nodes.get(2).translation.set(-11,26.5f,-3f);
        instance = new ModelInstance(model);
        instance.transform.scl(1.5f);
        instance.transform.trn(50,50,5);
        instance.transform.rotate(1,0,0,55);
        box = new BoundingBox();
        position = new Vector3();
        dimension = new Vector3();
        instance.calculateBoundingBox(box).mul(instance.transform);
        box.getCenter(position);
        box.getDimensions(dimension);
    }
    public void DrawRaider(ModelBatch modelBatch){
        modelBatch.render(instance);
    }
    public  boolean TouchRaider(int x, int y, PerspectiveCamera camera){
        Ray ray = camera.getPickRay(x,y);
        coordonate = null;
        if(Intersector.intersectRayBoundsFast(ray,box)){
            coordonate = new Vector3();
            coordonate.x = Math.abs(Math.abs(position.x) - Math.abs(camera.position.x)) / (Gdx.graphics.getHeight() * 0.026f);
//            coordonate.y = Math.abs(Math.abs(position.y) - Math.abs(camera.position.y)) / (Gdx.graphics.getHeight() * 0.026f);
            coordonate.y =(Math.abs((Gdx.graphics.getHeight() * 0.033f) - Math.abs(camera.position.y)) / (Gdx.graphics.getHeight() * 0.0126f));
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
}
