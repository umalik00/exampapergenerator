public class Modules {
    //Modules object to be used to handle modules
    private String module_name = null;
    private String module_code = null;



    //Attributes and getter and setter methods defined

    public Modules() {
    }

    public Modules(String module_code, String module_name) {
        this.module_code = module_code;
        this.module_name = module_name;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getModule_code() {
        return module_code;
    }

    public void setModule_code(String module_code) {
        this.module_code = module_code;
    }

}
