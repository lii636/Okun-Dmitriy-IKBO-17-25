package uvm.vm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import uvm.exception.UvmException;

class MachineStateTest {
    @Test
    void supportsCompleteInstructionAddressSpace() {
        MachineState state = new MachineState(new byte[0]);
        int lastAddress = MachineState.DATA_MEMORY_SIZE - 1;
        state.setData(lastAddress, 42);
        assertEquals(42, state.data(lastAddress));
        assertThrows(
            UvmException.class,
            () -> state.setData(MachineState.DATA_MEMORY_SIZE, 1)
        );
    }

    @Test
    void keepsCommandAndDataMemorySeparate() {
        byte[] command = {1, 2, 3, 4, 5};
        MachineState state = new MachineState(command);
        state.setData(0, 99);
        assertArrayEquals(command, state.commandMemory());
        assertEquals(99, state.data(0));
    }

    @Test
    void returnsDefensiveCommandMemoryCopy() {
        MachineState state = new MachineState(new byte[] {1, 2, 3});
        byte[] exposed = state.commandMemory();
        exposed[0] = 99;
        assertArrayEquals(new byte[] {1, 2, 3}, state.commandMemory());
    }
}
