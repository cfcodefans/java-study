package cf.study.misc.algo.datastructure;

import org.junit.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class Strings {
	static int countMatches(String p, String s) {
		int cnt = 0;
		int M = p.length();
		int N = s.length();
		
		if (M > N) return 0;
		for (int i = 0; i < N; i++) {
			int j;
			for (j = 0; j < M; j++) {
				if (s.charAt(i + j) != p.charAt(j)) {
					break;
				}
			}
			if (j == M) cnt++;
		}
		
		return cnt;
	}
	
	@Test
	public void testCountMatches() {
		int c1 = StringUtils.countMatches("abaabbaaabbbaaaabbbb", "ab");
		int c2 = countMatches("ab", "abaabbaaabbbaaaabbbb");
		Assert.assertEquals(c1, c2);
	}
	
	static String squeeze(String s) {
		char[] a = s.toCharArray();
		int N = 1;
		for (int i = 1; i < a.length; i++) {
			a[N] = a[i];
			if (a[N] != ' ') N++;
			else if (a[N - 1] != ' ') N++;
		}
		return new String(a, 0, N);
	}
	
	@Test
	public void testSqueeze() {
		Assert.assertEquals(" this is a string", squeeze("  this  is   a    string"));
	}
}
