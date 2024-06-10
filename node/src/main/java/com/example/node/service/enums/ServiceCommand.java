package com.example.node.service.enums;

public enum ServiceCommand {
    START("/start"),
    REGISTRATION("/registration"),
    HELP("/help"),
    CANCEL("/cancel");

    private final String value;

    ServiceCommand(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String v){

        for(ServiceCommand c: ServiceCommand.values()){
            if(c.value.equals(v)){
                return c;
            }
        }

        return null;
    }
}
