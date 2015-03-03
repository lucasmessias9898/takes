/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, to print it all.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "origin")
public final class RqPrint implements Request {

    /**
     * Original request.
     */
    private final transient Request origin;

    /**
     * Ctor.
     * @param req Original request
     */
    public RqPrint(final Request req) {
        this.origin = req;
    }

    @Override
    public List<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }

    /**
     * Print it all.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String print() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.print(baos);
        return new String(baos.toByteArray());
    }

    /**
     * Print it all.
     * @param output Output stream
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        final String eol = "\r\n";
        final Writer writer = new OutputStreamWriter(output);
        for (final String line : this.head()) {
            writer.append(line);
            writer.append(eol);
        }
        writer.append(eol);
        writer.flush();
        this.printBody(output);
    }

    /**
     * Print body.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String printBody() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printBody(baos);
        return new String(baos.toByteArray());
    }

    /**
     * Print body.
     * @param output Output stream to print to
     * @throws IOException If fails
     */
    public void printBody(final OutputStream output) throws IOException {
        final InputStream input = this.body();
        try {
            while (true) {
                final int data = input.read();
                if (data < 0) {
                    break;
                }
                output.write(data);
            }
        } finally {
            input.close();
        }
    }

}