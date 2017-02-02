# PR QuadTree implementation for LibGDX

<a href="url"><img src="https://upload.wikimedia.org/wikipedia/commons/8/8b/Point_quadtree.svg" height="300" width="300" ></a>


## Description
PR QuadTree implementation to detect collision in 2D Space. It is optimized and developed for LibGDX.
This implementation uses (depends) the Array class and Rectangle class, so it isn't a generic quadtree.

## How to create it

```java
//	Create dimension and QuadTree
Rectangle dimension = new Rectangle(0, 0, 100, 100);
//	First arg always is 0 (root node or level 0)
QuadTree quadTree = new QuadTree(0, dimension);

//	Sprite class has a Rectangle associated to detect collision
Sprite player = new Sprite(...);
Rectangle r1 = player.getBoundingRectangle();
//	Create more rectangles
Rectangle r2 = new Rectangle(50, 10, 40, 20);
Rectangle r3 = new Rectangle(50.5f, 10.47f, 40.5f, 20.7f);

//	Add rectangles
quadTree.insert(r1);
quadTree.insert(r2);
quadTree.insert(r3);
```

## How to use it

```java
Array<Rectangles> allRectangles, list;

// main loop
// other code

quadTree.clear()
//	Insert all rectangles
for (Rectangle r : allRectangles)
	quadTree.insert(r);

//	Get all rectangles in list that verify the area
list.clear()
Rectangle areaSelected = new Rectangle(20, 20, 10, 30);
quadTree.retrieve(list, areaSelected);

//	Check for collisions
for (Rectangle r : list)
{
	//	Algorithm to check collisions
}

//	A example: If some rectangle collides with the player then increment counter
for (Rectangle r : list)
{
	if (rPlayer.overlaps(r))
		numCollision++;
}

//	other code
//	end main loop

```

## There's more... the retrieveFast method
If you want to process the tree like any quadtree then use the retrieve() method. It's simple. If you want a plus of efficiency then use the retrieveFast method, but a configuration is required.

The retrieve() method returns a list of rectangles. This method is inefficient when there are many elements per node, because if there are elements between nodes, then it will return elements that are not close to the focus.

On the other hand, we have the retrieveFast() method. This method is more intelligent and efficient when there are many elements per node. But it has a problem: it doesn't work when a node is not full.

Please, watch <a href="https://www.youtube.com/watch?v=HtKyIH1ngGs" target="_blank">this video</a> to understand it. You'll understand quickly.

```java
public Array<Rectangle> retrieveFast(Array<Rectangle> list, Rectangle area)
{
	int index = getIndex(area);

	if (index != -1 & nodes[0] != null)
		nodes[index].retrieveFast(list, area);

	if (level == MAX_LEVEL || level == MAX_LEVEL-1) // (*)
		list.addAll(objects);

	return list;
}
```
(*) You can discard some levels, so only process those levels that are complete. Depending on your game, this method offers optimum configuration, but this requires a few trial-and-error method.In this case, it only processes the deepest levels of the tree.

In the worst case, If this is not well configured then there will be elements that collide and do not detect them.
