package com.pl;

import com.pl.api.ApiHandler;
import com.pl.api.StaticHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try { port = Integer.parseInt(envPort); }
            catch (NumberFormatException ignored) {}
        }

        // Determine static files location (relative to jar or working dir)
        String staticDir = resolveStaticDir();
        System.out.println("Serving static files from: " + staticDir);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", new ApiHandler());
        server.createContext("/",    new StaticHandler(staticDir));
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("==============================================");
        System.out.println("  Premier League App started!");
        System.out.println("  Open http://localhost:" + port + " in your browser");
        System.out.println("==============================================");
    }

    private static String resolveStaticDir() {
        // 1. Next to the JAR: dist/static  (built by build script into out/static → packed into JAR root)
        //    When running via `java -jar dist/premier-league.jar`, cwd is the project folder.
        //    The JAR itself contains static/ at its root; we extract it at startup if needed.
        //    But simpler: the build script copies static → out/static → jar root.
        //    So we can find it via the classloader as a directory on the classpath.

        // 2. Classpath resource (works when running `java -jar` since static/ is inside the jar)
        try {
            var url = Main.class.getClassLoader().getResource("static/index.html");
            if (url != null) {
                // It's inside the JAR – extract to a temp dir
                return extractStaticToTemp();
            }
        } catch (Exception ignored) {}

        // 3. Running directly from compiled `out/` directory
        Path p1 = Paths.get("out/static");
        if (Files.isDirectory(p1)) return p1.toAbsolutePath().toString();

        // 4. Running from project root (dev mode)
        Path p2 = Paths.get("src/main/resources/static");
        if (Files.isDirectory(p2)) return p2.toAbsolutePath().toString();

        throw new RuntimeException(
            "Cannot find static resources. Make sure you built with build-and-run.bat first.");
    }

    private static String extractStaticToTemp() throws Exception {
        Path tmp = Files.createTempDirectory("pl-static");
        tmp.toFile().deleteOnExit();

        // List of known static files to extract from JAR
        String[] files = {"index.html"};
        for (String f : files) {
            var in = Main.class.getClassLoader().getResourceAsStream("static/" + f);
            if (in != null) {
                Path dest = tmp.resolve(f);
                Files.copy(in, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                in.close();
            }
        }
        return tmp.toAbsolutePath().toString();
    }
}
