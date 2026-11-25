package uvm.dump;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import uvm.exception.UvmException;
import uvm.vm.MachineState;

public class MemoryXmlDumper {
    public void dump(MachineState state, int from, int to, Path output) {
        if (to >= MachineState.DATA_MEMORY_SIZE) {
            throw new UvmException("dump range exceeds data memory: " + to);
        }
        String xml = render(state, from, to);
        try {
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            Files.writeString(output, xml, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UvmException("cannot write dump: " + output, exception);
        }
    }

    public String render(MachineState state, int from, int to) {
        try {
            StringWriter buffer = new StringWriter();
            XMLStreamWriter writer = XMLOutputFactory.newFactory()
                .createXMLStreamWriter(buffer);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            writer.writeStartElement("memory");
            writer.writeAttribute("from", Integer.toString(from));
            writer.writeAttribute("to", Integer.toString(to));
            writer.writeCharacters("\n");
            for (int address = from; address <= to; address++) {
                writer.writeCharacters("  ");
                writer.writeEmptyElement("cell");
                writer.writeAttribute("address", Integer.toString(address));
                writer.writeAttribute("value", Integer.toString(state.data(address)));
                writer.writeCharacters("\n");
            }
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
            writer.close();
            return buffer.toString();
        } catch (XMLStreamException exception) {
            throw new UvmException("cannot render memory dump", exception);
        }
    }
}
