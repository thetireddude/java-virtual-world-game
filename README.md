This project was a course assignment for CSC 203 (Object Oriented Programming) done in **3 parts** and later independently extended.

- [PART 1](#PART-1)
- [PART 2](#PART-2)
- [PART 3](#PART-3)


# PART 1

In this project, you are given a large object-oriented codebase and are asked to refactor it to improve its cohesion.

Take some time to study this codebase. In doing so, you may have noticed that some classes support functionality (methods) that are not appropriate for all instances of the class. Moreover, these classes support data attributes that are not used by all instances of the class.

This is an issue of _cohesion_. Specifically, these classes exhibit low (poor) cohesion by representing multiple concepts, combining all attributes and methods used by each concept in a single class.

This project asks that you improve the code base by splitting each class exhibiting low cohesion into multiple, highly cohesive classes. Doing so in Java will require identifying common methods for each subset of these new classes and then introducing a new parent type for each logical grouping of these methods (more on this below).
Objectives

- Deepen your understanding of the specific functionality of the large project design
- To be able to read and understand java code and be able to evaluate the cohesion of the class structure in existing code
- To be able to implement abstract classes and/or interfaces and use them to improve cohesion in a project design
- Specifically, for this assignment, in the actual java code, introduce appropriate classes in order to remove the need for enumerated types and for other classes that contain methods that do not support the primary role of instances of that class
- To be able to make design changes to a large code base and have the code still work

## Task Overview

You must identify those classes with low cohesion and then split these classes into separate classes exhibiting high cohesion. Since each of these new classes will introduce a separate type, you may need to “root” them at a single type (as defined by an interface or an abstract class) to satisfy Java’s type checking rules. When you are done, many classes will end up with multiple parent types (e.g. implement multiple interfaces) or may have a parent type that has its own parent types in turn.

Based on the original source code, there are likely two categories of classes with low cohesion. The first category consists of those classes that depend on ActionKind or EntityKind. The second category depends on your final distribution of the methods in the original Functions class.

- "Kind": The original source code uses ActionKind and EntityKind to allow each Action instance and each Entity instance to play one of potentially many roles (polymorphism). You are to eliminate these Kind classes (enums) by splitting Action and Entity into multiple new classes.

- Other: Review all of the classes with a focus on cohesion. Does a class contain data that is not used by all instances of the class (i.e., each “kind” uses only subsets of the data)? Does a class contain methods that do not support the primary role of instances of the class (e.g., static methods that are used to create instances or parse files, but that are not actually part of the functionality provided by the instances)?

**You are strongly encouraged to:**
- Develop both a design document and the code refactoring at the same time.
- Implement the refactoring incrementally so that your refactored program executes properly at each step. That is, after each change, run the program using the main method in `VirtualWorld.java` and tests in `WorldTests.java` and make sure that it continues to behave as expected.
- Commit your code and push to GitHub often. On this project more than before you are likely to want to look at previous versions of the codebase.

## Introducing Parent Types

Consider this example. Above we’ve discussed the EntityKind enums, which are used to differentiate between different kinds of Entities. In this project, you’ll need to split those classes into multiple new classes, each of which represents a specific kind of Entity. However, to satisfy Java’s type-checking rules, we need to “root” those new classes at a single type.

You have a number of strategies in your arsenal that will help you address this. Namely, you can introduce an interface or an abstract class. Consider carefully the pros and cons of either approach.

### Strategy 1

An interface can define a number of abstract methods which are then implemented by each of the implementing subclasses. This solves our problem of rooting our new subclasses at a single parent type.

However, it will introduce a fair amount of code duplication. This is because each implementing subclass will need to implement all of the abstract methods that are listed in the interface, even if the implementations are identical for multiple subclasses. How to address this?

### Strategy 2

This can be addressed by using default methods in your interfaces. Default methods let you provide implementations for certain methods (which will be used by the implementing subclasses unless they have their own implementations). This solves the problem of duplicated method implementations, but does still cause difficulties because interfaces cannot have instance variables.

### Strategy 3

You can address this by instead using an abstract class. Abstract classes, as you recall, can have a mix of abstract and fully implemented methods (in a manner very similar to an interface having abstract and default methods). A key difference is that abstract classes can also have instance variables—this means you can avoid duplication of data, not just methods.

**So why not just use abstract classes if they solve so many problems?** Remember that a class can extend no more than one abstract class. As you design your solution, you will find that this introduces a number of constraints, not all of which are desirable.

Like many problems in software design, there is no “silver bullet” that solves all your problems. You will consider design trade-offs and make your own decisions about how to approach this project, likely using a mix of the above strategies.

No matter what you do, your main guiding principles throughout will be:

- Improve cohesion. Classes should only include functionality that relevant to all instances of the class. There should NOT be functionality in a class that only relevant to some instances of the class.
- Remove code duplication. There should be little-to-no code duplication in the project once you’re done. Where classes have similar or identical code, abstract out that functionality into a parent type.
- If you opt to use mostly interfaces and default methods, you will find that private instance variables and their public getters and setters must be duplicated across all implementing subclasses. This duplication is okay.

## Design document

There is no design document submission required. However, you are strongly encouraged to prepare a diagram describing your program design before you begin refactoring the source code. Show me this diagram during lab or office hours to receive some feedback about it before you dive too deeply into code editing.

## Source code refactoring

The following are some tips on approaching the introduction of interfaces or abstract classes to support splitting classes.

**Note**: A class should not implement an interface (or extend an abstract parent class) only to then define a method required by the interface (or abstract class) to do nothing at all. A class should not implement an interface (or extend an abstract parent class) and then define a method required by the interface (or abstract class) to raise an exception indicating that the method is not supported.

Your introduction of parent types for this project must be meaningful. It is insufficient to define a single interface / abstract class with all methods that are then only partially implemented by each of the classes.

- First, copy the original class to each of the new classes (each defining a single role).
- In each new class, eliminate each data attribute not used by this class and each method not supported by this class. (For this project, you can examine how instances playing this role are created as a hint about which data attributes are actually used.)
- Change the original class into an interface declaring only those methods shared by every new class.
- Group the new classes into sets with similar functionality. Introduce additional interfaces as appropriate (see below).
- Examine the original uses of the objects (before this change) to determine which methods are used by client code. Can the client code still access that method based on the reference type? Will it be able to do so if you change the type to one of the interfaces that you have already introduced? Do any interfaces have to extend a more general interface for it to compile?

At this point, if you only added interfaces, you will have lots of duplicate code. Next, consider if your interfaces could use default methods or if there is any common data / implementation you can pull up if your interface was instead an abstract parent (or if your interface was implemented by an abstract parent).

---

Your refactoring must not add or remove any program functionality. The resulting program must work as before. The `WorldTests` must continue to pass.

### Tips on Refactoring Methods

You can use the compiler (on the command-line or in the IDE) to help you with your refactoring. In particular, as you introduce interfaces and abstract classes, the compiler will report attempts to use methods not supported by the specified type. The existence of such errors may indicate missing methods for an interface or, more likely, attempts to treat a group of objects more generally than should be supported (i.e., not all of them implement the desired operation).

As part of your refactoring, you will be eliminating the *Kind* classes. This is desired to allow each new class to directly implement a single role, but has the unfortunate side-effect of eliminating a simple check of an object’s “kind”. This check is used, for instance, when searching for the nearest Tree to a Dude.

Consider the following tips.

- For a class that is being split into multiple class, change the original class into an interface declaring no methods. Compile the program to determine all uses of this interface (the method invocations will trigger compiler errors). Now determine which of these methods must be supported by all instances of an interface or abstract class and which should be supported via additional interfaces.

- You can copy the original class to, and change all references to, NameTmp and declare it to implement the new interface (or extend the new abstract class) so that most of the code will continue to compile.

- For those methods that are not logically part of the primary interface defined in the prior step, introduce new interfaces and change the necessary variable declarations to use the new types.

- A check for the "kind" of a referenced object can, for now (though we will address this later), be replaced by a use of instanceof. Use this sparingly; certainly instanceof is not needed to check the type of this.

- In the case that a *Kind value was passed as a parameter to another method (and then compared within), you can do the following.

- Change the parameter type from the specific *Kind to Class (this is a type where each instance represents properties of a specific Java class).

- Instead of passing a *Kind value, use .class to get the object associated with the desired Java class (e.g., String.class gives the Class object describing the String class).

- Change the comparison to use the isInstance() method on the Class object, passing to this method the object to be checked.

- For two methods that appear to be doing roughly the same thing, but that differ slightly in their implementation: examine the code to determine if the code can be rewritten to match. This does require careful consideration for what each method does (and does not) to avoid introducing bugs.

- Some methods may have the same general structure (and match identically in significant portions), but differ in some segments. For such methods, the general structure and identical portions can be refactored into a parent class. This parent class will declare new protected abstract method(s) that each subclass then implements to define the unique behavior (as done in the calculator lab).

# PART 2

For this assignment you must modify the pathing behavior of all entities that move within the world.

## Objectives
- To modify the code to use the specified PathingStrategy interface (that in turn uses streams to build a list of neighbors)
- Further to integrate the use of this pathing strategy and understand the associated code example which uses filter and collect
- Implement A star pathing algorithm in the existing code by implementing a new PathingStrategy subclass building off prior exercises.

### Overview
This assignment deviates from the pattern of previous assignments. Though this assignment does introduce/leverage some design strategies, the primary goal is to improve the functionality of some entities in the virtual world.

In particular, as you are likely very aware of by now, the Dudes’ and Fairy’s movements are very simplistic. You have likely seen an entity get stuck on an obstacle or on another entity. You will improve the pathing strategy as part of this assignment.

Pathing algorithms are quite interesting, in and of themselves, but our exploration of pathing in this assignment also motivates the use of some design patterns and techniques. Applying these patterns will also improve the flexibility of the implementation.

### Supporting Variety — Strategy Pattern
When an entity attempts to move, it needs to know the next step to take. How that next step is computed is, in many respects, irrelevant to the code within the corresponding entity. In fact, we may want to change that strategy for different builds of the program (to experiment), each time the program is executed (based on configuration), or dynamically during execution. The Strategy pattern allows you to encapsulate each pathing algorithm and switch between them as desired.

Your implementation must use the given PathingStrategy interface (discussed below).
```
interface PathingStrategy
{
   /*
    * Returns a prefix of a path from the start point to a point within reach
    * of the end point.  This path is only valid ("clear") when returned, but
    * may be invalidated by movement of other entities.
    *
    * The prefix includes neither the start point nor the end point.
    */
   List<Point> computePath(Point start, Point end,
      Predicate<Point> canPassThrough,
      BiPredicate<Point, Point> withinReach,
      Function<Point, Stream<Point>> potentialNeighbors);

   static final Function<Point, Stream<Point>> CARDINAL_NEIGHBORS =
      point ->
         Stream.<Point>builder()
            .add(new Point(point.x, point.y - 1))
            .add(new Point(point.x, point.y + 1))
            .add(new Point(point.x - 1, point.y))
            .add(new Point(point.x + 1, point.y))
            .build();
}
```
This strategy declares only a single method, computePath, to compute a path of points (returned as a list) from the start point to the end point (this is only expected to be a prefix, excluding the start and end points, of a real path; it need not represent a full path).

In order to compute this path, the pathing algorithm needs to know the directions in which travel might be able to proceed (determined by potentialNeighbors). In addition, in order to explore potential paths, the pathing algorithm must be able to determine if a given point can be traversed (i.e., is both a valid position in the world and a location to which the traveler can move; determined by canPassThrough). Finally, it is unlikely that the pathing algorithm should actually attempt to move to the end point (it is quite likely occupied, of course). Instead, the pathing algorithm will determine that a path is complete when a point is reached that is withinReach of the end point.

### Single-Step Pathing
As an example of defining a pathing strategy, consider the following implementation of the single-step pathing algorithm (SingleStepPathingStrategy) used to this point by the pathing entities (this specific implementation leverages the stream library).

Modify the appropriate entities to use a PathingStrategy (referencing the interface, of course). Use the given implementation to verify that your changes work.

```
class SingleStepPathingStrategy
   implements PathingStrategy
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
      return potentialNeighbors.apply(start)
         .filter(canPassThrough)
         .filter(pt ->
            !pt.equals(start)
            && !pt.equals(end)
            && Math.abs(end.x - pt.x) <= Math.abs(end.x - start.x)
            && Math.abs(end.y - pt.y) <= Math.abs(end.y - start.y))
         .limit(1)
         .collect(Collectors.toList());
   }
]
```
Of course, this implementation only matches the original pathing algorithm if potentialNeighbors returns the same neighbor points (in the same order) as before. Experiment with adding other points to the Stream returned by potentialNeighbors; perhaps allow the addition of diagonal movement, only allow diagonal movement, or remove the option to move straight up or down and replace them with the corresponding diagonals. Each of these approaches can be tried simply by changing the function passed to computePath.

### A* Pathing
Define a new PathingStrategy subclass called AStarPathingStrategy that implements the A* search algorithmLinks to an external site.. As before, an entity will take only one step along the computed path so the computePath method will be invoked multiple times to allow movement to the intended destination (see below for alternatives). As such, take care in how you maintain state relevant to the algorithm.

### Testing
You are strongly encouraged to write unit tests for this strategy. Since your implementation must conform to a specified interface, part of the grading will be based on instructor unit tests.

Once you’re finished with Lab 6, it provides a great testing program for your A* search. It’s a great way to visualize the algorithm running.

### Alternate Traversal Approaches
After completing the above, you might notice an indecisive Dude or Fairy ping-ponging between two points. This is an artifact of attempting to move to the nearest ore and only following one step of any computed path. That one step moves the Dude closer to a different Plant, which results in the computation of a new path … that brings the Dude right back to the previous point.

Consider some alternatives (implementation of these is entirely optional; any such changes will be in the entity code, not in the pathing strategy).

- Non-fickle: Once a path is computed, continue to follow that path as long as the target entity (e.g., ore) has not been collected by another. This approach skips the check for the “nearest target” as long as the previous target is available.

- Determined: Once a path is computed, follow it to the end. This approach skips the check for “nearest target” until a new path must be computed.

- Ol' College Try: Once a path is computed, follow it at least X steps (or until exhausted) before giving up. This approach skips the check for “nearest” target until it has consumed a fixed number of steps (e.g., five) in the current path (or it has consumed the entire path). After this initial effort, if the destination has not been reached, then check for the “nearest target” and compute a new path.

# PART 3

For this assignment you will modify the virtual world to support a “world-changing” event. This event is to be triggered by a mouse press and must have a localized effect. The event must be visualized by changing the affected background tiles and by modifying the affected entities (more below). In addition, the world event must create a new type of entity.

## Objectives
Add new functionality to existing code base demonstrating an understanding of the existing design and functionality
Be able to evaluate the current design based on the experience of adding to the code
 

### Your Goal: World Changing Event
Now that you have an excellent code base, you can edit it and grow the functionality by adding the following events.

### World Changing Event: Visualization
Decide on a world-changing event (e.g., a river spawns, a volcano erupts, or a rainbow is cast across the land). This event must be triggered by a mouse-click and must affect at least 7 tiles of the world in proximity to the mouse position when the click occurs. The event should affect no more than half of the world.

The world event must be visualized by modifying the background image of the affected tiles (so edit Background objects instead of Entity objects for this step. You are free to modify them however you would like, and are encouraged to be creative.

### World Changing Event: Effect
At least one type of existing mobile entity (e.g., fairies or dudes) must be affected by the world event, based on proximity to the event location. More specifically, this type of entity should change in appearance and behavior (similar to how dudes transform from DudeNotFull to DudeFull).

For example, a rainbow might change nearby dudes into dragons that seek to burn down houses.

The affected/transformed entities should change appearance and should change behavior (and the behavior must be active..it cannot be that it was moving before and now does not move). You must have both for full credit.

### World Changing Event: New Entity
The world event must cause a new type of mobile entity to spawn. This new entity should animate and move according to logic defined by you. Make sure you have multiple image files so it animates.

For instance, the new entity might seek out fairies to turn them into crystals, chase down dudes to infect them with the plague, or travel the world spreading apple seeds.

Note: This new entity is in addition to the entity transformation triggered by the event as just discussed. For example, a Dude transforming into a completely different kind of entity does not count for this requirement. A new entity must spawn (i.e. there are now more things in the world, not just an existing thing replaced.)

### Additional Requirements
The images for your changed background, affected entity, and new entity must be created by you (or found by you…you can find a gif and convert it to png or bmp files). You may not use any of the existing images that came with the project (including the wyvern). An exception is for your “affected entity”, you may alter that entity’s current image.

For entities, you can download a gif from here and then use this website to convert the gif into a series of images. Those images are what will be used to “animate” your entity. Be sure to re-size the images so they fit in the world! See the other images in the images folder to know what the size should be.

It goes without saying that all additions to the world should be professionally appropriate. Humor is okay. Crassness is not. If you’re not sure, ask me (or err on the side of caution).

### Design
Be sure to adhere to the design principles discussed this quarter. Refactor your code as needed, and resist the urge for quick hacks that would increase maintenance costs.

You are encouraged to reflect on the quality of your design and the effort required to add the functionality for this assignment. How do you think this effort compares to that needed to add the same functionality to the originally given code? (Especially if you have, e.g., entities that move quite differently from the original set.)
