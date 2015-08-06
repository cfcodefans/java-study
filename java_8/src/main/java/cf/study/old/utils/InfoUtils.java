package cf.study.utils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * @author fan
 * 
 */
public class InfoUtils {

	private static ObjectMapper om = new ObjectMapper();

	public static class LogInfo {

	}
	
	public static String join(Object ...params) {
		return StringUtils.join(params);
	}

	public static <T> String tabulateLog(Collection<T> col, String... colNames) {
		String[][] table = tabulateBeans(col, colNames);
		return prettyTable(table);
	}

	public static String prettyTable(String[][] table) {
		return prettyTable(table, ' ');
	}

	public static String[][] toStringArray(Object[][] objs) {
		String[][] strs = new String[objs.length][objs[0].length];
		
		for (int i = 0; i < objs.length; i++) {
			for (int j = 0; j < objs[i].length; j++) {
				strs[i][j] = String.valueOf(objs[i][j]);
			}
		}
		
		return strs;
	}


	public static String prettyTable(String[][] table, char _char) {
		if (table == null) {
			return StringUtils.EMPTY;
		}
		
		int[] colWidths = new int[table[0].length];
		for (int c = 0; c < table[0].length; c++) {
			colWidths[c] = StringUtils.length(table[0][c]);
			for (int r = 0; r < table.length; r++) {
				int length = StringUtils.length(table[r][c]);
				if (colWidths[c] < length) {
					colWidths[c] = length;
				}
			}
		}
		
		for (int c = 0; c < table[0].length; c++) {
			for (int r = 0; r < table.length; r++) {
				int length = StringUtils.length(table[r][c]);
				if (colWidths[c] > length) {
					table[r][c] = StringUtils.rightPad(table[r][c], colWidths[c], _char);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (String[] row : table) {
			sb.append('\n');
			for (String field : row) {
				sb.append(field).append('|');
			}
		}
		return sb.toString();
	}

	public static <T> String[][] tabulateBeans(Collection<T> col, String... colNames) {
		if (CollectionUtils.isEmpty(col)) {
			return null;
		}

		List<T> list = new ArrayList<T>(col);
		T sample = null;
		for (T element : col) {
			sample = element;
			if (sample != null) {
				break;
			}
		}
		if (sample == null) {
			return null;
		}

		List<PropertyDescriptor> pds = new ArrayList<PropertyDescriptor>(Arrays.asList(PropertyUtils.getPropertyDescriptors(sample.getClass())));
		Set<String> readablePropertyNames = new LinkedHashSet<String>();
		
		String[][] table = new String[col.size() + 1][];
		if (ArrayUtils.isNotEmpty(colNames)) {
			for (String pName : colNames) {
				PropertyUtils.isReadable(sample, pName);
				readablePropertyNames.add(pName);
			}
		} else {
			for (PropertyDescriptor pd : pds) {
				PropertyUtils.isReadable(sample, pd.getName());
				readablePropertyNames.add(pd.getName());
			}
		}
		table[0] = readablePropertyNames.toArray(new String[0]);
		
		for (int i = 1; i < table.length; i++) {
			T element = list.get(i - 1);
			String[] row = new String[table[0].length];
			table[i] = row;
			if (element == null) {
				Arrays.fill(row, null);
				continue;
			}
			for (int i1 = 0; i1 < table[i].length; i1++) {
				String pName = table[0][i1];
				try {
					row[i1] = String.valueOf(PropertyUtils.getProperty(element, pName));
				} catch (Exception e) {
					_l.error("not such field: " + pName);
				}
			}
		}
		return table;
	}

	public static String joinMap(String entryDelimiter, String pairDelimiter, Map<String, Object> map) {
		if (StringUtils.isEmpty(pairDelimiter)) {
			pairDelimiter = ": ";
		}
		if (StringUtils.isEmpty(entryDelimiter)) {
			entryDelimiter = ", ";
		}

		if (MapUtils.isEmpty(map)) {
			return StringUtils.EMPTY;
		}

		List<String> strList = new ArrayList<String>(map.size());
		for (Map.Entry<String, Object> en : map.entrySet()) {
			strList.add(en.getKey() + pairDelimiter + en.getValue());
		}

		return StringUtils.join(strList, entryDelimiter);
	}

	public static String joinPairs(String entryDelimiter, String pairDelimiter, Object... keyOrVals) {
		if (StringUtils.isEmpty(pairDelimiter)) {
			pairDelimiter = ": ";
		}
		if (StringUtils.isEmpty(entryDelimiter)) {
			entryDelimiter = ", ";
		}

		if (ArrayUtils.isEmpty(keyOrVals)) {
			return StringUtils.EMPTY;
		}
		if (keyOrVals.length == 1) {
			return String.valueOf(keyOrVals[0]);
		}

		List<String> strList = new ArrayList<String>(keyOrVals.length / 2 + 1);
		for (int i = 0; i < keyOrVals.length - 1; i = i + 2) {
			strList.add(keyOrVals[i] + pairDelimiter + keyOrVals[i + 1]);
		}

		return StringUtils.join(strList, entryDelimiter);
	}

	public static String joinPairs(String entryDelimiter, String pairDelimiter, Map<Object, Object> map) {
		return joinPairs(entryDelimiter, pairDelimiter, toArray(map));
	}

	public static Object[] toArray(Map<Object, Object> map) {
		if (MapUtils.isEmpty(map)) {
			return ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		List<Object> list = new ArrayList<Object>(map.size() * 2);
		for (Map.Entry<Object, Object> en : map.entrySet()) {
			list.add(en.getKey());
			list.add(en.getValue());
		}
		return list.toArray();
	}

	public static String toJson(Object... keyOrVals) {
		if (ArrayUtils.isEmpty(keyOrVals)) {
			return "null";
		}

		try {
			return om.writeValueAsString(MapUtils.putAll(new LinkedHashMap<String, Object>(), keyOrVals));
		} catch (Exception e) {
			_l.error(e, e);
			return ArrayUtils.toString(keyOrVals);
		}
	}

	private static final Logger _l = Logger.getLogger(InfoUtils.class);

	public static String[][] toString(ResultSet rs) throws Exception {
		if (rs == null) {
			_l.info("RecordSet is null");
			return null;
		}
		
		
		final List<String[]> list = new ArrayList<String[]>();
		
		final ResultSetMetaData md = rs.getMetaData();
		final int colCnt = 1 + md.getColumnCount();
		final String[] h = new String[colCnt];
		h[0] = "*";
		for (int i = 1; i < h.length; i++) {
			h[i] = md.getColumnName(i);
		}
		list.add(h);
		
		rs.first();
		while (rs.next()) {
			String[] r = new String[colCnt];
			r[0] = String.valueOf(rs.getRow());
			for (int i = 1; i < r.length; i++) {
				r[i] = rs.getString(i);
			}
			list.add(r);
		}
		
		rs.beforeFirst();
		
		return list.toArray(new String[0][0]);
	}
	
	public static long count(ResultSet rs) throws Exception {
		int row = rs.getRow();
		if (row == 0) return 0;
		rs.last();
		final int count = rs.getRow();
		rs.absolute(row);
		return count;
	}
}
