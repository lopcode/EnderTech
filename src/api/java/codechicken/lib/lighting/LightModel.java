package lib.lighting;

import net.minecraft.client.renderer.Tessellator;

public class LightModel implements lib.render.IVertexModifier
{
    public static class Light
    {
        public lib.vec.Vector3 ambient = new lib.vec.Vector3();
        public lib.vec.Vector3 diffuse = new lib.vec.Vector3();
        public lib.vec.Vector3 position;
        
        public Light(lib.vec.Vector3 pos)
        {
            position = pos.copy().normalize();
        }
        
        public Light setDiffuse(lib.vec.Vector3 vec)
        {
            diffuse.set(vec);
            return this;
        }
        
        public Light setAmbient(lib.vec.Vector3 vec)
        {
            ambient.set(vec);
            return this;
        }
    }
    
    public static LightModel standardLightModel;
    static
    {
        standardLightModel = new LightModel()
                .setAmbient(new lib.vec.Vector3(0.4, 0.4, 0.4))
                .addLight(new Light(new lib.vec.Vector3(0.2, 1, -0.7))
                    .setDiffuse(new lib.vec.Vector3(0.6, 0.6, 0.6)))
                .addLight(new Light(new lib.vec.Vector3(-0.2, 1, 0.7))
                    .setDiffuse(new lib.vec.Vector3(0.6, 0.6, 0.6)));
    }
    
    private lib.vec.Vector3 ambient = new lib.vec.Vector3();
    private Light[] lights = new Light[8];
    private int lightCount;
    
    public LightModel addLight(Light light)
    {
        lights[lightCount++] = light;
        return this;
    }
    
    public LightModel setAmbient(lib.vec.Vector3 vec)
    {
        ambient.set(vec);
        return this;
    }

    /**
     * @param colour The pre-lighting vertex colour. RGBA format
     * @param normal The normal at the vertex
     * @return The lighting applied colour
     */
    public int apply(int colour, lib.vec.Vector3 normal)
    {
        lib.vec.Vector3 n_colour = ambient.copy();
        for(int l = 0; l < lightCount; l++)
        {
            Light light = lights[l];
            double n_l = light.position.dotProduct(normal);
            double f = n_l > 0 ? 1 : 0;
            n_colour.x += light.ambient.x + f*light.diffuse.x*n_l;
            n_colour.y += light.ambient.y + f*light.diffuse.y*n_l;
            n_colour.z += light.ambient.z + f*light.diffuse.z*n_l;
        }

        if(n_colour.x > 1)
            n_colour.x = 1;
        if(n_colour.y > 1)
            n_colour.y = 1;
        if(n_colour.z > 1)
            n_colour.z = 1;
        
        n_colour.multiply((colour >>> 24)/255D, (colour >> 16 & 0xFF)/255D, (colour >> 8 & 0xFF)/255D);
        return (int)(n_colour.x*255)<<24 | (int)(n_colour.y*255)<<16 | (int)(n_colour.z*255)<<8 | colour&0xFF;
    }
    
    @Override
    public void applyModifiers(lib.render.CCModel m, Tessellator tess, lib.vec.Vector3 vec, lib.render.UV uv, lib.vec.Vector3 normal, int i)
    {
        lib.render.CCRenderState.setColour(apply((m == null || m.colours == null) ? -1 : m.colours[i], normal));
    }
    
    @Override
    public boolean needsNormals()
    {
        return true;
    }
    
    public PlanarLightModel reducePlanar()
    {
        int[] colours = new int[6];
        for(int i = 0; i < 6; i++)
            colours[i] = apply(-1, lib.vec.Rotation.axes[i]);
        return new PlanarLightModel(colours);
    }
}
