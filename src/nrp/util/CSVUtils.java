package nrp.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtils
{
    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine( Writer w, List< String > values ) throws IOException
    {
        writeLine( w, values, DEFAULT_SEPARATOR, ' ' );
    }

    public static void writeLine( Writer w, List< String > values, char separators )
            throws IOException
    {
        writeLine( w, values, separators, ' ' );
    }

    public static void writeLine( Writer w, List< String > values, char separators,
            char customQuote ) throws IOException
    {
        boolean first = true;

        // default customQuote is empty

        if ( separators == ' ' ) {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for ( String value : values ) {
            if ( !first ) {
                sb.append( separators );
            }

            if ( customQuote == ' ' ) {
                sb.append( followCSVformat( value ) );
            } else {
                sb.append( customQuote ).append( followCSVformat( value ) ).append( customQuote );
            }

            first = false;
        }

        sb.append( "\n" );
        w.append( sb.toString() );
        // Files.write( path, sb.toString().getBytes(), StandardOpenOption.APPEND );
    }

    // https://tools.ietf.org/html/rfc4180
    private static String followCSVformat( String value )
    {
        String result = value;
        if ( result.contains( "\"" ) ) {
            result = result.replace( "\"", "\"\"" );
        }
        return result;
    }
}