package com.enklave.game.LoadResursed;


public class ManagerAssets {
    private final AssetsScreenChat assetsChatScreen;
    private AssetsButton assetsButton;
    private AssetsIntro assetsIntro;
    private AssetsLoadingN assetsLoading;
    private AssetsMaps assetsMaps;
    private AssetsSound assetsSound;
    private AssetsTutorial assetsTutorial;
    private AssetsProfile assetsProfile;
    private AssetsCrafting assetsCrafting;
    private AssetsEnklave3D assetsEnklave3D;
    private AssetsDescribeEnklave assetsDescribeEnklave;
    private AssetsExtension assetsExtension;
    private AssetsRooms assetsRooms;
    private AssetsCombat assetsCombat;
    private AssetsScreenBricks assetsBricks;
    private AssetsSettings assetsSettings;
    private AssetsChoiceFaction assetsChoiceFaction;
    private AssertEnklaveScreen assertEnklaveScreen;
    private AssetsRaider assetsRaider;
    private AssetsFadeInActions assetsFadeInActions;

    private static ManagerAssets ourInstance = new ManagerAssets();

    public static ManagerAssets getInstance() {
        return ourInstance;
    }

    private ManagerAssets() {
        assetsButton = new AssetsButton();
        assetsLoading = new AssetsLoadingN();
        assetsMaps = new AssetsMaps();
        assetsSound = new AssetsSound();
        assetsProfile = new AssetsProfile();
        assetsCrafting = new AssetsCrafting();
        assetsTutorial = new AssetsTutorial();
        assetsEnklave3D = new AssetsEnklave3D();
        assetsRaider = new AssetsRaider();
        assetsFadeInActions = new AssetsFadeInActions();
        assetsChatScreen = new AssetsScreenChat();
    }

    public void loadAssetsChoiceFaction()    {
        assetsChoiceFaction = new AssetsChoiceFaction();
        assetsChoiceFaction.loadResurse();
    }
    public AssetsChoiceFaction getAssetsChoiceFaction() {
        return assetsChoiceFaction;
    }

    public void loadAssetsTutorial(){
        assetsTutorial.load();
    }
    public AssetsTutorial getAssetsTutorial() {
    return assetsTutorial;
}

    public void loadAssetsSettings(){
        assetsSettings = new AssetsSettings();
    }
    public AssetsSettings getAssetsSettings() {
        return assetsSettings;
    }

    public void loadAssetsBricks(){
        assetsBricks = new AssetsScreenBricks();
    }
    public AssetsScreenBricks getAssetsBricks() {
        return assetsBricks;
    }

    public void loadAssetsCombat(){
        assetsCombat = new AssetsCombat();
    }
    public AssetsCombat getAssetsCombat() {
        return assetsCombat;
    }

    public void loadAssetsRooms(){
        assetsRooms = new AssetsRooms();
    }
    public AssetsRooms getAssetsRooms() {
        return assetsRooms;
    }

    public void loadAssetExtension(){
        assetsExtension = new AssetsExtension();
    }
    public AssetsExtension getAssetsExtension() {
        return assetsExtension;
    }

    public void loadDescribeEnkla(){
        assetsDescribeEnklave = new AssetsDescribeEnklave();
    }
    public AssetsDescribeEnklave getAssetsDescribeEnklave() {
        return assetsDescribeEnklave;
    }

    public void loadAssetsIntro(){
        assetsIntro = new AssetsIntro();
    }
    public AssetsIntro getAssetsIntro(){
        return assetsIntro;
    }

    public void loadAssetsEnklaveScreen(){
        assertEnklaveScreen = new AssertEnklaveScreen();
    }
    public AssertEnklaveScreen getAssertEnklaveScreen(){
        return assertEnklaveScreen;
    }

    public AssetsRaider getAssetsRaider(){
        return assetsRaider;
    }
    public AssetsButton getAssetsButton(){
        return assetsButton;
    }
    public AssetsLoadingN getAssetsLoading() {
        return assetsLoading;
    }
    public AssetsMaps getAssetsMaps() {
        return assetsMaps;
    }
    public AssetsSound getAssetsSound() {
        return assetsSound;
    }
    public AssetsProfile getAssetsProfile() {
        return assetsProfile;
    }
    public AssetsCrafting getAssetsCrafting() {
        return assetsCrafting;
    }
    public AssetsEnklave3D getAssetsEnklave3D() {
        return assetsEnklave3D;
    }
    public AssetsFadeInActions getAssetsFadeInActions() {
        return assetsFadeInActions;
    }
    public AssetsScreenChat getAssetsChatScreen() {
        return assetsChatScreen;
    }

    public boolean updateScreenLoading(){
        boolean flag = false;
        if(assetsMaps.update() && assetsButton.update() && assetsCrafting.update() && assetsProfile.update() && assetsEnklave3D.update() && assetsRaider.update() && assetsFadeInActions.update() && assetsChatScreen.update())
            flag = true;
        return flag;
    }
    public void disposeAll(){
        assetsMaps.dispose();
        assetsButton.dispose();
        assetsCrafting.dispose();
        assetsProfile.dispose();
        assetsEnklave3D.dispose();
        assetsRaider.dispose();
        assetsFadeInActions.dispose();
        assetsChatScreen.dispose();
    }
}
