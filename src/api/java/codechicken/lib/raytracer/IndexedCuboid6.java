package lib.raytracer;

public class IndexedCuboid6 extends lib.vec.Cuboid6
{
    public Object data;
    
    public IndexedCuboid6(Object data, lib.vec.Cuboid6 cuboid)
    {
        super(cuboid);
        this.data = data;
    }
}