/*
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

public class UnixClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        java.io.File path = new java.io.File("/tmp/aaaaa");
        int retries = 0;
        while (!path.exists()) {
            TimeUnit.MILLISECONDS.sleep(500L);
            retries++;
            if (retries > 10) {
                throw new IOException(
                    String.format(
                        "File %s does not exist after retry",
                        path.getAbsolutePath()
                    )
                );
            }
        }
        AFUNIXSocket s = null;
        String data = "blah blah";
        s = AFUNIXSocket.newInstance();
		s.connect(new AFUNIXSocketAddress(path) );
        System.out.println("connected to " + s.getRemoteSocketAddress());
        PrintWriter w = new PrintWriter(s.getOutputStream());
        w.print(data);
        w.flush();

        InputStreamReader r = new InputStreamReader(s.getInputStream());
        CharBuffer result = CharBuffer.allocate(1024);
        r.read(result);
        result.flip();
        System.out.println("read from server: " + result.toString());
        final int status;
        if (!result.toString().equals(data)) {
            System.out.println("ERROR: data mismatch");
            status = -1;
        } else {
            System.out.println("SUCCESS");
            status = 0;
        }
        System.exit(status);
    }
}
