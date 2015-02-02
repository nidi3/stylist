package guru.nidi.stylist.rating;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 */
@Service
public class BatchCreator {
    public void createBatch(double rating, OutputStream target) throws IOException {
        final int r = (int) (255 * (1 - rating));
        final int g = (int) (200 * (rating));
        final String rgb = toHex(r) + toHex(g) + "00";
        final String value = Math.round(100 * rating) + "%";
        write(rgb, value, target);
    }

    public void createInProgress(OutputStream target) throws IOException {
        write("aaa", "wait", target);
    }

    private void write(String rgb, String value, OutputStream target) throws IOException {
        try (final OutputStreamWriter out = new OutputStreamWriter(target)) {
            out.write("<svg xmlns='http://www.w3.org/2000/svg' width='90' height='20'>\n" +
                    "    <rect rx='3' width='90' height='20' fill='#555' />\n" +
                    "    <rect rx='3' x='47' width='43' height='20' fill='#" + rgb + "' />\n" +
                    "    <path fill='#" + rgb + "' d='M47 0h4v20h-4z' />\n" +
                    "    <g fill='#fff' text-anchor='middle' font-family='sans-serif' font-size='11'>\n" +
                    "        <text x='22' y='15' fill='#010101' fill-opacity='.3'>quality</text>\n" +
                    "        <text x='22' y='14'>quality</text>\n" +
                    "        <text x='70' y='15' fill='#010101' fill-opacity='.3'>" + value + "</text>\n" +
                    "        <text x='70' y='14'>" + value + "</text>\n" +
                    "    </g>\n" +
                    "</svg>");
        }
    }

    private String toHex(int v) {
        final String h = Integer.toHexString(v);
        return (h.length() < 2 ? "0" : "") + h;
    }

}
