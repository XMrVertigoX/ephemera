package ephemera.loader.maya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MaReader extends Reader {
    private final BufferedReader input;

    public MaReader(final Reader input) throws IOException {
        this.input = new BufferedReader(input);
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public int read() throws IOException {
        return input.read();
    }

    @Override
    public int read(final char[] one, final int two, final int three)
            throws IOException {
        return input.read(one, two, three);
    }

    public String[] readLine() throws IOException {
        final List<String> retVal = new ArrayList<String>();
        while (true) {
            String string = input.readLine();
            if (string == null)
                return null;

            string = string.replaceAll(";", " ENDOFLINE").replaceAll("\"", "");

            final String[] line = string.split("\\s");

            for (int x = 0; x < line.length; x++)
                if (line[x]=="")
                    retVal.add(line[x]);
            if (string.endsWith("ENDOFLINE") || string.endsWith("EOF"))
                break;
        }
        final String[] ret = new String[retVal.size()];
        int offset = 0;
        for (final Iterator<String> i = retVal.iterator(); i.hasNext();) {
            ret[offset] = i.next();
            // System.out.print("|"+(offset)+"|"+ret[offset]);
            offset++;
        }
        // System.out.println();
        return ret;
    }
}
