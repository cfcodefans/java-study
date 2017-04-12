package cf.study.misc.algo.search;

import java.util.Arrays;

import org.junit.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class StringSearch {
	public int kmpSearch(final String target, final String searchStr) {
		if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchStr) || target.length() > searchStr.length()) {
			return -1;
		}
		
		final char[] t = target.toCharArray();
		final char[] s = searchStr.toCharArray();
		
		final int[] ft = kmpPartialMatchTable_(t);
		
		for (int i = 0, j = 0, len = s.length - t.length; i < len; i += j - (ft[j] < -1 ? 0 : ft[j])) {
			for (j = 0; j < t.length && t[j] == s[i + j]; j++);
			if (j == t.length) return i;
//			System.out.println(String.format(":%s%c\n:%s%s\n", StringUtils.repeat(" ", i + j - 1), s[i+j],  StringUtils.repeat(" ", i), target));
		}
		
		return -1;
	}

	private int[] kmpPartialMatchTable(final char[] t) {
		print(t);
		
		final int[] ft = new int[t.length];
		ft[0] = -1;
		ft[1] = 0;
		for (int pos = 2, cnd = 0; pos < t.length;) {
			if (t[pos - 1] == t[cnd]) {
				cnd++;
				ft[pos] = cnd;
				pos++;
			} else if (cnd > 0) {
				cnd = ft[cnd];
			} else {
				ft[pos] = 0;
				pos++;
			}
			print(ft);
		}
		print(ft);
		return ft;
	}
	
	private int[] kmpPartialMatchTable_(final char[] t) {
		print(t);
		
		final int[] ft = new int[t.length];
		Arrays.fill(ft, 0);
		ft[0] = -1;
		ft[1] = 0;
		
		for (int i = 2; i < t.length; i++) {
			for (int j = i - 1; j > 0; j--) {
				if (t[i - 1] == t[j]) {
//					System.out.println(t[i] + "=="+ t[j]);
					ft[i] = ft[j - 1] <= 0 ? j - 1 : ft[j - 1];
				}
			}
			print(ft);
		}
		print(ft);
		return ft;
	}

	private void print(final char[] t) {
		final StringBuilder sb = new StringBuilder();
		for (char c : t) {
			sb.append(c).append('\t');
		}
		System.out.println(sb.toString());
	}

	private void print(final int[] ft) {
		System.out.println(StringUtils.join(ArrayUtils.toObject(ft), '\t'));
	}
	
	@Test
	public void testPartialMatchTable() {
		final String target = "EBCEDE";
		kmpPartialMatchTable(target.toCharArray());
		Assert.assertEquals("AACD".indexOf(target), kmpSearch(target, "AACD"));
	}
	
	final String search = "ABC1ABCDAB2ABCDABCDABDE";
	@Test
	public void testSearch() {
		final String target = "A_CDABDA__";
		kmpPartialMatchTable(target.toCharArray());
		Assert.assertEquals(search.indexOf(target), kmpSearch(target, search));
	}

	@Test
	public void testPartialMatchTable_() {
		final String target = "EBCEDE";
		kmpPartialMatchTable_(target.toCharArray());
		System.out.println();
		kmpPartialMatchTable(target.toCharArray());
//		Assert.assertEquals("AACD".indexOf(target), kmpSearch(target, "AACD"));
	}
}
