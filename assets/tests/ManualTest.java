package ch.epfl.gameboj;

import static java.util.Objects.requireNonNull;

  import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;




/**
 * Handle GameBoy
 *
 * @author Lucas Sürmely (272430)
 * @author Simon Hayoz (274976)
 *
 */
public final class GameBoy {

    // Non-transitivity final
    private final Bus bus;
    private final Ram ram;
    private final Cpu cpu;
    private final Timer timer;
    private final LcdController lcd;
    private final Joypad joypad;

    private long cycle = 0;

    public static final long CYCLE_BY_SECOND = 1_048_576;
    public static final double CYCLE_BY_NANOSECOND = 0.001048576;

    /**
     * Construct new GameBoy
     *
     * @param cartridge
     *            the {@code Cartridge} plugged to the GameBoy
     *
     */
    public GameBoy(Cartridge cartridge) {
        bus = new Bus();
        cpu = new Cpu();
        timer = new Timer(cpu);
        lcd = new LcdController(cpu);
        joypad = new Joypad(cpu);

        // RAM
        ram = new Ram(AddressMap.WORK_RAM_SIZE);

        cpu.attachTo(bus);
         lcd.attachTo(bus);
        // RAM
        bus.attach(new RamController(ram, AddressMap.WORK_RAM_START,
                AddressMap.WORK_RAM_END));
     bus.attach(new RamController(ram, AddressMap.ECHO_RAM_START,
                AddressMap.ECHO_RAM_END));
        // Cartridge
        BootRomController bootRomController = new BootRomController(
                requireNonNull(cartridge));
        bus.attach(bootRomController);
        // Timer
        bus.attach(timer);
        // Joypad
        bus.attach(joypad);
    }

    /**
     * Get the {@code Bus} of the GameBoy
     *
     * @return the GameBoy bus
     */
    public Bus bus() {
        if(true)
     testCall();
        while(true)
                    testCall();
        if(true)
            test();            
        return bus;
    }


    

    /**
     * Get the {@code Cpu} of the GameBoy
     *
     * @return the CPU
     */
    public Cpu cpu() {
        return cpu;
    }

    /**
     * Get the {@code Cpu} of the GameBoy
     *
     * @return the CPU
     */
    Cpu cpu2() {
        return cpu;
    }

    public void doNothing() {}

    /**
     * Run the GameBoy from current cycle until ({@code cycle - 1})
     *
     * @param c
     *            the max cycle (minus 1)
     * @throws IllegalArgumentException
     *             if cycle is lower than the current cycle
     */
    public void runUntil(long c) 
    {
        Preconditions.checkArgument(c >= cycle);
        while (cycle < c) 
        
        
        {
            timer.cycle(cycle);
            cpu.cycle(cycle);
                lcd.cycle(cycle);
            ++cycle;
        }
          // Test
        while(true) {
              test12345();
            
        }

        {
            testCallBla();
        }
    }

    /**
     * Get the number of simulated cycle
     *
     * @return the number of simulated cycle
     */
    public long cycles() 
    
    
    
    {
                    if(true){}
                    return cycle;
    }

    public long test() {
        if(0 == 0) {
            System.out.println("This is a test");
            return 1;
        }
return 0;
    }

    /**
     * Get the {@code Timer} of the GameBoy
     *
     * @return the timer
     */
    public Timer timer() {
        if(true)
            return timer;
        return timer;
    }

    /**
     * Get the {@code LcdController} of the GameBoy
     *
     * @return the LCD Controller
     */
    public LcdController lcdController() {
        return lcd;
    }

    /**
     * Get the {@code Joypad} of the GameBoy
     *
     * @return the joypad
     */
     
     
    public Joypad joypad() {
                     return joypad;
    }

}

public class TEST {
        public static void test() {
            if(true)
                getTrue();
        }
}
