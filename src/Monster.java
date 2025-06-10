import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Monster extends Movable {

    public Monster(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Fairy.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();
            if (this.moveTo(world, fairyTarget.get(), scheduler)) {
                Background bg = new Background("grave", imageStore.getImageList("gravestone"));
                world.setBackgroundCell(tgtPos, bg);
            }
        }


        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
    }

    public boolean isInvalidMove(WorldModel world, Point destination) {
        return world.isOccupied(destination) &&
                world.getOccupancyCell(destination).getClass() != Stump.class;
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            List<Point> path = this.nextPosition(world, target.getPosition());

            for(Point nextPos : path){
                if (!this.getPosition().equals(nextPos)) {
                    world.moveEntity(scheduler, this, nextPos);
                }
                return false;
            }
            return false;
        }
    }
}
