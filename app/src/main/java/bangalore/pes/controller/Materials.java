package bangalore.pes.controller;

/**
 * Created by nisha on 04-07-2017.
 */
// class for creating Materials objects, so that they can be used in the database
public class Materials {
    int id;
    String material;
    String parameter_1;
    String parameter_2;

    public Materials(){}
    public Materials(String material,String parameter_1,String parameter_2)
    {
        this.material=material;
        this.parameter_1=parameter_1;
        this.parameter_2=parameter_2;

    }
    public void setMaterial(String material)
    {
        this.material=material;
    }
    public void setParameter_1(String parameter_1)
    {
        this.parameter_1=parameter_1;
    }
    public void setParameter_2(String parameter_2)
    {
        this.parameter_2=parameter_2;
    }
    public String getMaterial()
    {
        return this.material;
    }
    public String getParameter_1()
    {
        return this.parameter_1;
    }
    public String getParameter_2()
    {
        return this.parameter_2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
