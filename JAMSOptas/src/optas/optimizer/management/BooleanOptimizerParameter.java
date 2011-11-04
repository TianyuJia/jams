package optas.optimizer.management;

public class BooleanOptimizerParameter extends OptimizerParameter {

    private boolean value;    

    public BooleanOptimizerParameter(){
        
    }
    public BooleanOptimizerParameter(String name, String desc, boolean value) {
        setName(name);
        setDescription(desc);
        this.value = value;
    }

    /**
     * @return the value
     */
    public boolean isValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(boolean value) {
        this.value = value;
    }
}