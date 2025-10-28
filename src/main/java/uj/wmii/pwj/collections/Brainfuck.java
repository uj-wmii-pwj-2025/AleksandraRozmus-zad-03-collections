package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public interface Brainfuck {
    /**
     * Executes uploaded program.
     */
    void execute();

    /**
     * Creates a new instance of Brainfuck interpreter with given program, using standard IO and stack of 1024 size.
     * @param program brainfuck program to interpret
     * @return new instance of the interpreter
     * @throws IllegalArgumentException if program is null or empty
     */
    static Brainfuck createInstance(String program) {
        if (program == null || program.isEmpty())
            throw new IllegalArgumentException("Program cannot be null or empty");

        return createInstance(program, System.out, System.in, 1024);
    }

    /**
     * Creates a new instance of Brainfuck interpreter with given parameters.
     * @param program brainfuck program to interpret
     * @param out output stream to be used by interpreter implementation
     * @param in input stream to be used by interpreter implementation
     * @param stackSize maximum stack size, that is allowed for this interpreter
     * @return new instance of the interpreter
     * @throws IllegalArgumentException if: program is null or empty, OR out is null, OR in is null, OR stackSize is below 1.
     */
    static Brainfuck createInstance(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || program.isEmpty())
            throw new IllegalArgumentException("Program cannot be null or empty");
        if (out == null) 
            throw new IllegalArgumentException("Output cannot be null");
        if (in == null) 
            throw new IllegalArgumentException("Input cannot be null");
        if (stackSize < 1) 
            throw new IllegalArgumentException("Stack size cannot be less than 1");
        
        return new BrainfckImpl(program, out, in, stackSize);
    }
}

class BrainfckImpl implements Brainfuck {
    String commands;
    PrintStream output;
    InputStream input;
    byte[] stack;
    int stackPtr;

    BrainfckImpl(String program, PrintStream out, InputStream in, int stackSize) {
        commands = program;
        output = out;
        input = in;
        stack = new byte[stackSize];
        stackPtr = 0;
    }

    int findMatchingBracket(String option, int startIdx) {
        int idx = 0, direction, ratio = 1;
        char incBracket, decBracket;

        if (option.equals("left")) {
            idx = startIdx + 1;
            direction = 1;
            incBracket = '[';
            decBracket = ']';
        }
        else {
            idx = startIdx - 1;
            direction = -1;
            incBracket = ']';
            decBracket = '[';
        }

        while (idx >= 0 && idx < commands.length()) {
            if (commands.charAt(idx) == incBracket)
                    ratio++;
                else if (commands.charAt(idx) == decBracket)
                    ratio--;

                if (ratio == 0)
                    break;
                idx += direction;
        }
        return idx;
    }

    @Override
    public void execute() {
        int commandPtr = 0;

        while (commandPtr < commands.length()) {
            switch (commands.charAt(commandPtr)) {
                case '>':
                    stackPtr++;
                    break;
                case '<':
                    stackPtr--;
                    break;
                case '+':
                    stack[stackPtr]++;
                    break;
                case '-':
                    stack[stackPtr]--;
                    break;
                case '.':
                    output.print((char) stack[stackPtr]);
                    break;
                case ',':
                    try {
                        int inByte = input.read();
                        stack[stackPtr] = (byte) inByte;
                    } catch (IOException exception) {}
                    break;
                case '[':
                    if (stack[stackPtr] == 0) 
                        commandPtr = findMatchingBracket("left", commandPtr);
                    break;
                case ']':
                    if (stack[stackPtr] != 0) 
                        commandPtr = findMatchingBracket("right", commandPtr);
                    break;
                default:
                    break;
            }
            commandPtr++;
        }
    }
}
