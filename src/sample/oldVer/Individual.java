package sample.oldVer;

import java.util.List;

public class Individual implements Cloneable{
   /* public List<Module> chromosome;
    public double fitness;
    public double cost;


        #region ICloneable Members

    public object Clone()
    {
        //Individual newindividual = (Individual)this.MemberwiseClone();
        Individual newindividual = new Individual();
        newindividual.chromosome = this.chromosome.Select(item => (Module)item.Clone()).ToList();
        newindividual.fitness = this.fitness;
        return newindividual;
    }

        #endregion
}*/
}
