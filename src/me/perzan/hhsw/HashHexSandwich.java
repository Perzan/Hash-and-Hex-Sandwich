package me.perzan.hhsw;

import java.util.Objects;

public class HashHexSandwich {
	
	private static final int 
		THREAD_COUNT = Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS")),
		FROM = Integer.MIN_VALUE, 
		TO = Integer.MAX_VALUE
		;
	
	public static void main(String[] args) {
		String prefix = args.length > 0? args[0] : "";
		String suffix = args.length > 1? args[1] : "";
		
		long range = (long) TO - FROM;
		
		long sectionSize = range/THREAD_COUNT;
		
		System.out.println("sectionsize: " + sectionSize);
		
		Thread[] threads = new Thread[THREAD_COUNT];
		
		for (int i = 0; i < THREAD_COUNT; i++) {
			final int threadIndex = i;
			
			int start = (int) (sectionSize*i + FROM);
			long finish = (sectionSize + start);
			
			Thread thread = new Thread(() -> {
				for (int hash = start; hash < finish; hash++) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					
					//System.out.println(hash);
					
					if (test(hash, prefix, suffix)) {
						System.out.println("Found it: " + hash);
						for (Thread t : threads)
							t.interrupt();
					}
				}
			});
			
			System.out.println("Thread " + thread.getId());
			System.out.println("starts: " + start);
			System.out.println("finishes: " + finish);
			System.out.println();
			
			threads[threadIndex] = thread;
		}
		
		for (Thread thread : threads)
			thread.start();
	}
	
	static int hash(Object o) {
		return Objects.hash(o);
	}
	
	static String hex(int i) {
		return Integer.toHexString(i);
	}
	
	static String hexhash(Object o) {
	    return hex(hash(o));
	}
	
	public static int hashsandwich(int hash, String prefix, String suffix) {
		return hash(String.join("", prefix, hex(hash), suffix));
	}

	public static boolean test(int hash, String prefix, String suffix) {
		return hash == hashsandwich(hash, prefix, suffix);
	}
	
}
