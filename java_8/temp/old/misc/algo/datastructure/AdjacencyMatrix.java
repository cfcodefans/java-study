package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class AdjacencyMatrix {

	@Test
	public void test() {
		main(new String[] {"7"});
	}
	
	public static void main(String[] args) {
		int V = Integer.parseInt(args[0]);
		boolean adj[][] = new boolean[V][V];
		
		for (int i = 0; i < V; i++) {
			for (int j = 0; j < V; j++) {
				adj[i][j] = false;
			}
		}
		
		for (int i = 0; i < V; i++) {
			adj[i][i] = true;
		}
		
		for (;;) {
			int a = 0;
			int b = 0;
			adj[a][b] = true;
			adj[b][a] = true;
		}
	}
}
