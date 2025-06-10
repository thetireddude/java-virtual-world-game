import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;


// Partner name - Alejandro Corona Garibay

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public static final String DISASTER_IMAGE_NAME = "magma";

    public String loadFile = "world.sav";
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    private  ImageStore imageStore;
    private  WorldModel world;
    private  WorldView view;
    private  EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        spawnDisaster(pressed);

        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);

        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            System.out.println(entity.getId() + ": " + entity.getClass());
        }

    }

    public void spawnDisaster(Point point){
        List<Point> disasterPoints = Arrays.asList(
                new Point(point.x, point.y),
                new Point(point.x, point.y+1),
                new Point(point.x+1, point.y),
                new Point(point.x, point.y-1),
                new Point(point.x-1, point.y),
                new Point(point.x-1, point.y-1),
                new Point(point.x-1, point.y+1),
                new Point(point.x-2, point.y+1),
                new Point(point.x-1, point.y+2),
                new Point(point.x+1, point.y+1),
                new Point(point.x+2, point.y+1),
                new Point(point.x+1, point.y+2));

        Background disasterBackground = new Background(DISASTER_IMAGE_NAME, imageStore.getImageList(DISASTER_IMAGE_NAME));
        List<Point> changedTiles = new ArrayList<>();

        for(Point pos: disasterPoints){
            if(!world.withinBounds(pos)){
                continue;
            }
            changedTiles.add(pos);
            world.setBackgroundCell(pos, disasterBackground);
        }

        Optional<Entity> target = world.findNearest(point, new ArrayList<>(Arrays.asList(DudeFull.class, DudeNotFull.class)));
        if(!target.isEmpty()){
            if(disasterPoints.contains(target.get().getPosition())){
                Dude dude = (Dude) target.get();
                dude.infect(world, scheduler, imageStore);
            }
        }

        spawnMonster(changedTiles, imageStore, world, scheduler);
    }

    public static void spawnMonster(List<Point> changed, ImageStore imageStore, WorldModel world, EventScheduler scheduler){

        Random rand = new Random();
        int rand_idx1 = rand.nextInt(changed.size());

        if(!world.isOccupied(changed.get(rand_idx1))){
            Monster monster = new Monster("monster", changed.get(rand_idx1), imageStore.getImageList("monster"), 0.2, 0.1);

            world.addEntity(monster);
            monster.scheduleActions(scheduler, world, imageStore);
        }

    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Animatable) {
                ((Animatable) entity).scheduleActions(scheduler, world, imageStore);
            } else if (entity instanceof Active) {
                ((Active) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            WorldModel.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
