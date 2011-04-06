package com.touchatag.android.tag.command;

import com.touchatag.android.util.ByteUtils;


public class Command {
    
    private final Command parent;
    private final int[] baseCommand;
    private int[] parameters = c();

    protected Command(int... baseCommand) {
        this(null, baseCommand);
    }
    
    protected Command(Command parent, int... baseCommand) {
        this.parent = parent;      
        this.baseCommand = baseCommand;
    }
    
    public int[] build(int... subcommand) {
        int[] thisCommand = c(getCommand(subcommand), subcommand);
        if (parent != null) {
            return parent.build(thisCommand);
        }
        return thisCommand;
    }
    
    protected Command add(int... parameters) {
        this.parameters = c(this.parameters, parameters);
        return this;
    }
    
    protected Command add(byte[] parameters) {
        return add(asInts(parameters));
    }
    
    public byte[] toBytes(){
    	return ByteUtils.toBytes(build());
    }
    
    /**
     * Trigger method when the command is being constructed. Allows to initialize parameters on command build. The subcommand which
     * is already processed is passed in as context
     * @param subcommand
     */
    protected void onBuild(int[] subcommand) {
        // Trigger that can be overridden
    }

    final private int[] getCommand(int[] subcommand) {
        onBuild(subcommand);
        return c(baseCommand, parameters);
    }
    
    final private static int[] c(int... bytes) {
        return bytes;
    }
    
    final private static int[] c(int[] base, int... extension) {
        int[] compound = new int[base.length+extension.length];
        System.arraycopy(base, 0, compound, 0, base.length);
        System.arraycopy(extension, 0, compound, base.length, extension.length);
        return compound;
    }
    
    final private int[] asInts(byte[] bytes) {
        return ByteUtils.toInts(bytes);
    }
    
}
