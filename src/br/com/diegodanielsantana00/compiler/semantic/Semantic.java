package br.com.diegodanielsantana00.compiler.semantic;

public class Semantic {
    private String type;
    private String name;
    private int escopo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEscopo() {
        return escopo;
    }
    
    public void setEscopo(int escopo) {
        this.escopo = escopo;
    }

}
