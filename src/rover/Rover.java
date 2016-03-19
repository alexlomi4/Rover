package rover;

import commands.LoggingCommand;
import commands.RoverCommand;
import commands.RoverCommandParser;
import commands.RoverCommandParserException;
import enums.Direction;
import ground.GroundVisor;
import ground.GroundVisorException;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by AlexL on 27.02.2016.
 */
public class Rover implements Moveable, Turnable, ProgramFileAware {
    private Direction direction = Direction.EAST;
    private int x;
    private int y;
    private RoverCommandParser commandParser;
    private GroundVisor visor;
    private LinkedList<RoverCommand> commands = new LinkedList<>();

    public Rover(){
        this.visor = new GroundVisor();
        this.commandParser = new RoverCommandParser(this);
    }

    public Rover(GroundVisor visor){
        this.visor = (visor != null ? visor : new GroundVisor());
        this.commandParser = new RoverCommandParser(this);
    }

    public GroundVisor getVisor(){
        return this.visor;
    }

    public void setVisor(GroundVisor visor){
        if (visor != null)
            this.visor = visor;
    }
    public RoverCommandParser getParser(){
        return this.commandParser;
    }

    public LinkedList<RoverCommand> getCommandList(){
        return this.commands;
    }

    @Override
    public void move(int x, int y) {
        try {
            if (!this.visor.hasObstacles(x, y)) {
                this.x = x;
                this.y = y;
                System.out.println("Rover moved to cell: " + x + " " + y);
            }
            else
                System.out.println("Rover can't move to occupied cell: " + x + " " + y);
        } catch (GroundVisorException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void turnTo(Direction direction){
        if (direction == null)
            System.err.println("Rover can't turn : direction is null");
        else {
            this.direction = direction;
            System.out.println("Rover turned to " + direction);
        }
    }

    @Override
    public void executeProgramFile(String filename){
        try(FileInputStream fis = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis))
        ){

            this.commandParser.setReader(reader);

            RoverCommand command = null;
            do {
                try{
                    command = this.commandParser.readNextCommand();
                    this.commands.addLast(command);
                }catch(RoverCommandParserException parsEx){parsEx.printStackTrace();}
            }while ( command != null);

            while ( (command = this.commands.pollFirst()) != null ){
                new LoggingCommand(command).execute();
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
