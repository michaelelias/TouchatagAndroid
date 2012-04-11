package com.touchatag.beta.tag.command;

public class SimpleCommand extends Command {
    
    public SimpleCommand(int... baseCommand) {
        super(baseCommand);
    }
    
    public SimpleCommand(Command parent, int... baseCommand) {
        super(parent, baseCommand);
    }
    
    @Override
    public SimpleCommand add(byte[] parameters) {
        super.add(parameters);
        return this;
    }
    
    @Override
    public SimpleCommand add(int... parameters) {
        super.add(parameters);
        return this;
    }
    
}
