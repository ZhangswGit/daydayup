package com.swapServer.signOn;

public abstract class SingleSignOn implements SignOn {

    @Override
    public int order(){
        return 1;
    }

    public abstract String producer();
}
