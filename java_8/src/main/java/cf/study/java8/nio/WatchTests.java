package cf.study.java8.nio;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class WatchTests {
	
	static WatchService ws = null;
	static Map<WatchKey, Path> keys;
	static final Path start = Paths.get("./test");
	
	@BeforeClass
	public static void setUp() throws Exception {
		ws = FileSystems.getDefault().newWatchService();
		keys = new HashMap<WatchKey, Path>();
		
		Path _start = start;
		regAllForWatch(_start);
	}

	private static void regAllForWatch(Path _start) throws IOException {
		Files.walkFileTree(_start, new SimpleFileVisitor<Path>() {
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				regForWatch(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if (ws != null) {
			ws.close();
		}
	}
	
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	public static void processEvent() {
		while (true) {
			// wait for key to be signalled
			WatchKey wk;
			try {
				wk = ws.take();
				System.out.println(wk);
			} catch (InterruptedException x) {
				return;
			}
			
			Path dir = keys.get(wk);
			if (dir == null) {
				System.err.println("WatchKey not recognized! " + wk.watchable());
				continue;
			}
			
			for (WatchEvent<?> event : wk.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				//TBD - provide example of how  OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}
				
				// Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);
				
				//print out event
				System.out.format("%s: %s\n", event.kind().name(), child);
				
				if (kind == ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							regAllForWatch(child);
						} else {
							regForWatch(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
			}
			
			if (wk.reset()) {
				keys.remove(wk);
				//all directories are inaccessible
				if (keys.isEmpty()) {
					return;
				}
			}
		}
	}
	
	private static void regForWatch(Path dir) throws IOException {
		WatchKey regKey = dir.register(ws, StandardWatchEventKinds.ENTRY_CREATE, 
						 StandardWatchEventKinds.ENTRY_DELETE,	
						 StandardWatchEventKinds.ENTRY_MODIFY);
		
		keys.put(regKey, dir);
	}

	@Test
	public void testWatchDir() throws Exception {
		ExecutorService thread = Executors.newSingleThreadExecutor();
		thread.submit(WatchTests::processEvent);
		
		ScheduledExecutorService scheduledThread = Executors.newScheduledThreadPool(1);
		scheduledThread.schedule(() -> {
			try {
				Path p = Paths.get("./test/test_file");
				p = Files.createFile(p);
				Path _p = Paths.get("./test/test_file_renamed");
				Files.move(p, _p);
				Files.delete(_p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 5, TimeUnit.SECONDS);
		
		thread.shutdown();
		thread.awaitTermination(30, TimeUnit.SECONDS);
	}
}
