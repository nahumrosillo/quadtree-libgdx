package com.quadtree.simulation;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


/**
 * PR QuadTree implementation for LibGDX
 *
 * @author Nahum Rosillo
 */
public class QuadTree
{
    //  --- Configuration
    private static int MAX_OBJECTS_BY_NODE = 10;
    private static int MAX_LEVEL = 6;
    //  ---

    private int level;
    private Array<Rectangle> objects;
    private Rectangle bounds;
    private QuadTree[] nodes;

    public QuadTree(int level, Rectangle bounds)
    {
        this.level = level;
        this.bounds = bounds;
        objects = new Array<Rectangle>();
        nodes = new QuadTree[4];
    }

    public void getZones(Array<Rectangle> allZones)
    {
        allZones.add(bounds);
        if (nodes[0] != null)
        {
            nodes[0].getZones(allZones);
            nodes[1].getZones(allZones);
            nodes[2].getZones(allZones);
            nodes[3].getZones(allZones);
        }
    }

    public void clear()
    {
        objects.clear();
        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i] != null)
            {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public void insert(Rectangle rect)
    {
        if (nodes[0] != null)
        {
            int index = getIndex(rect);
            if (index != -1)
            {
                nodes[index].insert(rect);
                return;
            }
        }

        objects.add(rect);

        if (objects.size > MAX_OBJECTS_BY_NODE && level < MAX_LEVEL)
        {
            if (nodes[0] == null)
                split();

            int i = 0;
            while(i < objects.size)
            {
                int index = getIndex(objects.get(i));

                if (index != -1)
                    nodes[index].insert(objects.removeIndex(i));
                else
                    i++;
            }
        }
    }

    public Array<Rectangle> retrieve(Array<Rectangle> list, Rectangle area)
    {
        int index = getIndex(area);

        if (index != -1 & nodes[0] != null)
            nodes[index].retrieve(list, area);

        list.addAll(objects);

        return list;
    }

    public Array<Rectangle> retrieveFast(Array<Rectangle> list, Rectangle area)
    {
        int index = getIndex(area);

        if (index != -1 & nodes[0] != null)
            nodes[index].retrieveFast(list, area);

        //  This if(..) is configurable: only process elements in MAX_LEVEL and MAX_LEVEL-1
        if (level == MAX_LEVEL || level == MAX_LEVEL-1)
            list.addAll(objects);

        return list;
    }

    private void split()
    {
        float subWidth =  (bounds.getWidth() * 0.5f);
        float subHeight = (bounds.getHeight() * 0.5f);
        float x = bounds.getX();
        float y = bounds.getY();

        nodes[0] = new QuadTree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(level+1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));

    }

    private int getIndex(Rectangle pRect)
    {
        int index = -1;
        float verticalMidpoint = bounds.getX() + (bounds.getWidth() * 0.5f);
        float horizontalMidpoint = bounds.getY() + (bounds.getHeight() * 0.5f);

        boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint)
        {
           if (topQuadrant)
               index = 1;
           else if (bottomQuadrant)
               index = 2;
        }
        else if (pRect.getX() > verticalMidpoint)
        {
            if (topQuadrant)
                index = 0;
            else if (bottomQuadrant)
                index = 3;
        }

        return index;
    }
}
