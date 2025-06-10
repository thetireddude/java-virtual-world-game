import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        /* Does not check withinReach.  Since only a single step is taken
         * on each call, the caller will need to check if the destination
         * has been reached.
         */

        HashMap<Point, Point> prev_points = new HashMap<>();

//        Comparator<Point> hueristicCmp = Comparator.comparing((Point p) -> (estimatedCostToGoal(p, end) + costFromStart(p, start)))
//                .thenComparing((Point p) -> estimatedCostToGoal(p, end));

        Comparator<Point> hueristicCmp = Comparator.comparing((Point p) -> (estimatedCostToGoal(p, end) + costFromStart(p, start)))
                .thenComparing((Point p) -> estimatedCostToGoal(p, end))
                .thenComparing((Point p) -> euclideanCostToGoal(p, end));

        PriorityQueue<Point> notVisited = new PriorityQueue<>(hueristicCmp);

        List<Point> visited = new ArrayList<>();
        Predicate<Point> ifVisited = point -> !(visited.contains(point));

        notVisited.add(start);
        List<Point> path = new ArrayList<>();

        while(!notVisited.isEmpty()) {
            Point current = notVisited.poll();

//            System.out.println("\ncurrent: " + current);
//            System.out.println(notVisited);
            visited.add(current);
//            System.out.println(visited);

            if(withinReach.test(current, end)){
                path = reconstructPath(prev_points, current, start);
                break;
            }

            potentialNeighbors.apply(current)
                    .filter(canPassThrough)
                    .filter(ifVisited)
                    .filter(pt ->
                            !pt.equals(start)
                                    && !pt.equals(end))
                    .forEach(point -> {
                        prev_points.put(point, current);
                        if(!notVisited.contains(point)) {
                            notVisited.add(point);
                        }
                    });
//            System.out.println("notVisited: " + notVisited);
//            System.out.println("visited: " + visited);
//            System.out.println("prev_points: " + prev_points);
        }

//        System.out.println("Compute Path was called");  //Debug log
        return path;
    }

    public int estimatedCostToGoal(Point current, Point dest){
        return Math.abs(dest.x  - current.x) + Math.abs(dest.y - current.y);
    }

    public int costFromStart(Point current, Point start){
        return Math.abs(current.x  - start.x) + Math.abs(current.y - start.y);
    }

    public double euclideanCostToGoal(Point current, Point dest){
        return Math.round(Math.sqrt(Math.pow(Math.abs(dest.x - current.x), 2) + Math.pow(Math.abs(dest.y - current.y), 2)));
    }

    public List<Point> reconstructPath(HashMap<Point, Point> map, Point p, Point start){
        List<Point> path = new ArrayList<>();
        path.add(p);
        while(map.containsKey(p)){
            if(!(map.get(p) == start)) {
                path.add(0, map.get(p));
            }
            p = map.get(p);
        }
        return path;
    }
}