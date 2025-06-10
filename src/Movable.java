import processing.core.PImage;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Movable extends ActiveAnimatedEntity {

    public Movable(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    //    public Point nextPosition(WorldModel world, Point destPos) {
//        int horiz = Integer.signum(destPos.x - this.getPosition().x);
//        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);
//
//        if (horiz == 0 || this.isInvalidMove(world, newPos)) {
//            int vert = Integer.signum(destPos.y - this.getPosition().y);
//            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
//
//            if (vert == 0 || this.isInvalidMove(world, newPos)) {
//                newPos = this.getPosition();
//            }
//        }
//
//        return newPos;
//    }

    // nexPosition( ) that uses the SingleStepPathingStrategy

//    public List<Point> nextPosition(WorldModel world, Point destPos){
//        PathingStrategy SingleStep = new SingleStepPathingStrategy();
//
//        Predicate<Point> canPassThrough = point -> !this.isInvalidMove(world, point);
//        BiPredicate<Point, Point> withinReach = (desPos,pos) -> (Math.abs(desPos.x - pos.x) == 1) && (Math.abs(desPos.y - pos.y) == 1);
//
//        List<Point> path = SingleStep.computePath(this.getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
//        return path;
//    }

    // nextPosition( ) that uses the AStarPathingStrategy

    public List<Point> nextPosition(WorldModel world, Point destPos){
        PathingStrategy AStar = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = point -> !this.isInvalidMove(world, point);
        BiPredicate<Point, Point> withinReach = (desPos,pos) ->
                (desPos.x - pos.x == 1 && desPos.y - pos.y == 0) || (desPos.x - pos.x == -1 && desPos.y - pos.y == 0) ||
                        (desPos.x - pos.x == 0 && desPos.y - pos.y == 1) || (desPos.x - pos.x == 0 && desPos.y - pos.y == -1);


        List<Point>  path = AStar.computePath(this.getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        return path;
    }

    public abstract boolean moveTo(WorldModel model, Entity target, EventScheduler scheduler);

    /**
     * The entity can move to destination if it's not occupied.
     */
    public boolean isInvalidMove(WorldModel world, Point destination) {
        return world.isOccupied(destination);
    }
}
